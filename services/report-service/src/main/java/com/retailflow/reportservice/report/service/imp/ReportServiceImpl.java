package com.retailflow.reportservice.report.service.imp;

import com.retailflow.reportservice.client.*;
import com.retailflow.reportservice.report.dto.response.*;
import com.retailflow.reportservice.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ReportDataClient reportDataClient;

    @Override
    public SalesReportResponse getSalesReport(LocalDate from, LocalDate to) {
        List<SaleRecord> sales = reportDataClient.fetchSales().stream()
                .filter(sale -> isCompleted(sale.status()))
                .filter(sale -> inRange(sale.saleDate(), from, to))
                .toList();

        BigDecimal revenue = sales.stream()
                .map(sale -> defaultZero(sale.total()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long itemsSold = sales.stream()
                .flatMap(sale -> sale.items() == null
                        ? java.util.stream.Stream.empty() : sale.items().stream())
                .mapToLong(item -> item.quantity() == null ? 0L : item.quantity())
                .sum();

        BigDecimal average = sales.isEmpty()
                ? BigDecimal.ZERO
                : revenue.divide(BigDecimal.valueOf(sales.size()), 2, RoundingMode.HALF_UP);

        return SalesReportResponse.builder()
                .from(from)
                .to(to)
                .totalSales(sales.size())
                .totalItemsSold(itemsSold)
                .totalRevenue(round(revenue))
                .averageOrderValue(average)
                .topProducts(topProducts(sales))
                .byWarehouse(byWarehouse(sales))
                .build();
    }

    @Override
    public PurchaseReportResponse getPurchaseReport(LocalDate from, LocalDate to) {
        List<PurchaseRecord> purchases = reportDataClient.fetchPurchases().stream()
                .filter(purchase -> "RECEIVED".equals(purchase.status()))
                .filter(purchase -> inRange(purchase.purchaseDate(), from, to))
                .toList();

        BigDecimal spent = purchases.stream()
                .map(purchase -> defaultZero(purchase.total()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long itemsPurchased = purchases.stream()
                .flatMap(purchase -> purchase.items() == null
                        ? java.util.stream.Stream.empty() : purchase.items().stream())
                .mapToLong(item -> item.quantity() == null ? 0L : item.quantity())
                .sum();

        return PurchaseReportResponse.builder()
                .from(from)
                .to(to)
                .totalPurchases(purchases.size())
                .totalItemsPurchased(itemsPurchased)
                .totalSpent(round(spent))
                .topProducts(topPurchasedProducts(purchases))
                .build();
    }

    @Override
    public InventoryReportResponse getInventoryReport() {
        Map<Long, ProductRecord> products = productMap();

        List<InventoryRecord> inventory = reportDataClient.fetchInventory();
        BigDecimal stockValue = inventory.stream()
                .map(row -> row.quantity() == null
                        ? BigDecimal.ZERO
                        : defaultZero(purchasePrice(row.productId(), products))
                                .multiply(BigDecimal.valueOf(row.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long totalQuantity = inventory.stream()
                .mapToLong(row -> row.quantity() == null ? 0L : row.quantity())
                .sum();

        List<InventoryItemReport> items = inventory.stream()
                .map(row -> InventoryItemReport.builder()
                        .productId(row.productId())
                        .productName(productName(row.productId(), products))
                        .warehouseId(row.warehouseId())
                        .quantity(row.quantity())
                        .availableQuantity(row.availableQuantity())
                        .reorderLevel(row.reorderLevel())
                        .stockValue(round(defaultZero(purchasePrice(row.productId(), products))
                                .multiply(BigDecimal.valueOf(row.quantity() == null ? 0 : row.quantity()))))
                        .status(stockStatus(row.quantity(), row.reorderLevel()))
                        .build())
                .toList();

        return InventoryReportResponse.builder()
                .asOf(LocalDate.now())
                .totalRows(items.size())
                .totalQuantity(totalQuantity)
                .totalStockValue(round(stockValue))
                .items(items)
                .build();
    }

    @Override
    public ProfitReportResponse getProfitReport(LocalDate from, LocalDate to) {
        Map<Long, ProductRecord> products = productMap();

        List<SaleRecord> sales = reportDataClient.fetchSales().stream()
                .filter(sale -> isCompleted(sale.status()))
                .filter(sale -> inRange(sale.saleDate(), from, to))
                .toList();

        BigDecimal revenue = sales.stream()
                .map(sale -> defaultZero(sale.total()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<Long, ProfitAccumulator> byProduct = new LinkedHashMap<>();

        for (SaleRecord sale : sales) {
            if (sale.items() == null) {
                continue;
            }
            for (SaleItemRecord item : sale.items()) {
                int qty = item.quantity() == null ? 0 : item.quantity();
                BigDecimal lineRevenue = defaultZero(item.total());
                BigDecimal lineCost = defaultZero(purchasePrice(item.productId(), products))
                        .multiply(BigDecimal.valueOf(qty));

                ProfitAccumulator acc = byProduct.computeIfAbsent(
                        item.productId(), id -> new ProfitAccumulator());
                acc.quantity += qty;
                acc.revenue = acc.revenue.add(lineRevenue);
                acc.cost = acc.cost.add(lineCost);
            }
        }

        BigDecimal cogs = byProduct.values().stream()
                .map(acc -> acc.cost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal profit = revenue.subtract(cogs);
        BigDecimal margin = revenue.signum() > 0
                ? profit.divide(revenue, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))
                : BigDecimal.ZERO;

        List<ProfitItemReport> items = byProduct.entrySet().stream()
                .map(entry -> ProfitItemReport.builder()
                        .productId(entry.getKey())
                        .productName(productName(entry.getKey(), products))
                        .quantitySold(entry.getValue().quantity)
                        .revenue(round(entry.getValue().revenue))
                        .cost(round(entry.getValue().cost))
                        .profit(round(entry.getValue().revenue.subtract(entry.getValue().cost)))
                        .build())
                .toList();

        return ProfitReportResponse.builder()
                .from(from)
                .to(to)
                .revenue(round(revenue))
                .costOfGoods(round(cogs))
                .grossProfit(round(profit))
                .marginPercentage(margin.setScale(2, RoundingMode.HALF_UP))
                .byProduct(items)
                .build();
    }

    @Override
    public LowStockReportResponse getLowStockReport(int threshold) {
        Map<Long, ProductRecord> products = productMap();

        List<InventoryItemReport> items = reportDataClient.fetchLowStock(threshold).stream()
                .map(row -> InventoryItemReport.builder()
                        .productId(row.productId())
                        .productName(productName(row.productId(), products))
                        .warehouseId(row.warehouseId())
                        .quantity(row.quantity())
                        .availableQuantity(row.availableQuantity())
                        .reorderLevel(row.reorderLevel())
                        .stockValue(round(defaultZero(purchasePrice(row.productId(), products))
                                .multiply(BigDecimal.valueOf(row.quantity() == null ? 0 : row.quantity()))))
                        .status(stockStatus(row.quantity(), row.reorderLevel()))
                        .build())
                .toList();

        return LowStockReportResponse.builder()
                .threshold(threshold)
                .totalItems(items.size())
                .items(items)
                .build();
    }

    private List<TopProductReport> topProducts(List<SaleRecord> sales) {
        Map<Long, TopAccumulator> map = new LinkedHashMap<>();
        for (SaleRecord sale : sales) {
            if (sale.items() == null) {
                continue;
            }
            for (SaleItemRecord item : sale.items()) {
                TopAccumulator acc = map.computeIfAbsent(item.productId(), id -> new TopAccumulator());
                acc.quantity += item.quantity() == null ? 0 : item.quantity();
                acc.revenue = acc.revenue.add(defaultZero(item.total()));
            }
        }
        return map.entrySet().stream()
                .sorted(Comparator.comparing((Map.Entry<Long, TopAccumulator> e) -> e.getValue().revenue)
                        .reversed())
                .limit(10)
                .map(entry -> TopProductReport.builder()
                        .productId(entry.getKey())
                        .productName(productName(entry.getKey(), productMap()))
                        .quantity(entry.getValue().quantity)
                        .revenue(round(entry.getValue().revenue))
                        .build())
                .toList();
    }

    private List<TopProductReport> topPurchasedProducts(List<PurchaseRecord> purchases) {
        Map<Long, TopAccumulator> map = new LinkedHashMap<>();
        for (PurchaseRecord purchase : purchases) {
            if (purchase.items() == null) {
                continue;
            }
            for (PurchaseItemRecord item : purchase.items()) {
                TopAccumulator acc = map.computeIfAbsent(item.productId(), id -> new TopAccumulator());
                acc.quantity += item.quantity() == null ? 0 : item.quantity();
                acc.revenue = acc.revenue.add(defaultZero(item.total()));
            }
        }
        return map.entrySet().stream()
                .sorted(Comparator.comparing((Map.Entry<Long, TopAccumulator> e) -> e.getValue().revenue)
                        .reversed())
                .limit(10)
                .map(entry -> TopProductReport.builder()
                        .productId(entry.getKey())
                        .productName(productName(entry.getKey(), productMap()))
                        .quantity(entry.getValue().quantity)
                        .revenue(round(entry.getValue().revenue))
                        .build())
                .toList();
    }

    private List<WarehouseSalesReport> byWarehouse(List<SaleRecord> sales) {
        Map<Long, WarehouseAccumulator> map = new LinkedHashMap<>();
        for (SaleRecord sale : sales) {
            WarehouseAccumulator acc = map.computeIfAbsent(sale.warehouseId(), id -> new WarehouseAccumulator());
            acc.count++;
            acc.revenue = acc.revenue.add(defaultZero(sale.total()));
        }
        return map.entrySet().stream()
                .map(entry -> WarehouseSalesReport.builder()
                        .warehouseId(entry.getKey())
                        .numberOfSales(entry.getValue().count)
                        .revenue(round(entry.getValue().revenue))
                        .build())
                .toList();
    }

    private Map<Long, ProductRecord> productMap() {
        return reportDataClient.fetchProducts().stream()
                .collect(Collectors.toMap(ProductRecord::id, Function.identity(), (a, b) -> a));
    }

    private String productName(Long productId, Map<Long, ProductRecord> products) {
        ProductRecord product = products.get(productId);
        return product != null && product.name() != null ? product.name() : "Product " + productId;
    }

    private BigDecimal purchasePrice(Long productId, Map<Long, ProductRecord> products) {
        ProductRecord product = products.get(productId);
        return product != null ? defaultZero(product.purchasePrice()) : BigDecimal.ZERO;
    }

    private boolean isCompleted(String status) {
        return "COMPLETED".equalsIgnoreCase(status);
    }

    private boolean inRange(LocalDate date, LocalDate from, LocalDate to) {
        if (date == null) {
            return false;
        }
        return !date.isBefore(from) && !date.isAfter(to);
    }

    private String stockStatus(Integer quantity, Integer reorderLevel) {
        int qty = quantity == null ? 0 : quantity;
        int reorder = reorderLevel == null ? 0 : reorderLevel;
        if (qty <= 0) {
            return "OUT_OF_STOCK";
        }
        return qty <= reorder ? "LOW" : "OK";
    }

    private BigDecimal defaultZero(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private BigDecimal round(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP);
    }

    private static final class TopAccumulator {
        long quantity;
        BigDecimal revenue = BigDecimal.ZERO;
    }

    private static final class WarehouseAccumulator {
        long count;
        BigDecimal revenue = BigDecimal.ZERO;
    }

    private static final class ProfitAccumulator {
        long quantity;
        BigDecimal revenue = BigDecimal.ZERO;
        BigDecimal cost = BigDecimal.ZERO;
    }
}
