package com.retailflow.reportservice.report.service.imp;

import com.retailflow.reportservice.client.*;
import com.retailflow.reportservice.report.dto.response.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportServiceImplTest {

    @Mock
    private ReportDataClient reportDataClient;

    private ReportServiceImpl reportService;

    @BeforeEach
    void setUp() {
        reportService = new ReportServiceImpl(reportDataClient);
    }

    private SaleItemRecord item(Long productId, int qty, BigDecimal total) {
        return new SaleItemRecord(1L, productId, qty, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, total);
    }

    private SaleRecord sale(Long id, Long warehouseId, LocalDate date, String status,
                            BigDecimal total, List<SaleItemRecord> items) {
        return new SaleRecord(id, "SL-00000" + id, 1L, warehouseId, date,
                total, BigDecimal.ZERO, BigDecimal.ZERO, total, status, items);
    }

    @Test
    void salesReport_shouldSumOnlyCompletedSalesInRange() {
        LocalDate from = LocalDate.of(2026, 8, 1);
        LocalDate to = LocalDate.of(2026, 8, 31);

        when(reportDataClient.fetchSales()).thenReturn(List.of(
                sale(1L, 1L, LocalDate.of(2026, 8, 5), "COMPLETED",
                        new BigDecimal("1000.00"), List.of(item(1L, 2, new BigDecimal("1000.00")))),
                sale(2L, 1L, LocalDate.of(2026, 7, 20), "COMPLETED",
                        new BigDecimal("500.00"), List.of(item(1L, 1, new BigDecimal("500.00")))),
                sale(3L, 2L, LocalDate.of(2026, 8, 10), "CANCELLED",
                        new BigDecimal("999.00"), List.of(item(1L, 1, new BigDecimal("999.00"))))));

        SalesReportResponse report = reportService.getSalesReport(from, to);

        assertThat(report.getTotalSales()).isEqualTo(1);
        assertThat(report.getTotalRevenue()).isEqualByComparingTo("1000.00");
        assertThat(report.getTotalItemsSold()).isEqualTo(2);
        assertThat(report.getAverageOrderValue()).isEqualByComparingTo("1000.00");
        assertThat(report.getByWarehouse()).hasSize(1);
        assertThat(report.getByWarehouse().get(0).getRevenue()).isEqualByComparingTo("1000.00");
    }

    @Test
    void profitReport_shouldComputeCogsFromPurchasePrice() {
        when(reportDataClient.fetchProducts()).thenReturn(List.of(
                new ProductRecord(1L, "GB-001", "890000001", "Gold Bangle",
                        new BigDecimal("4000.00"), new BigDecimal("5000.00"), true)));
        when(reportDataClient.fetchSales()).thenReturn(List.of(
                sale(1L, 1L, LocalDate.of(2026, 8, 5), "COMPLETED",
                        new BigDecimal("10000.00"), List.of(item(1L, 2, new BigDecimal("10000.00"))))));

        ProfitReportResponse report = reportService.getProfitReport(
                LocalDate.of(2026, 8, 1), LocalDate.of(2026, 8, 31));

        assertThat(report.getRevenue()).isEqualByComparingTo("10000.00");
        assertThat(report.getCostOfGoods()).isEqualByComparingTo("8000.00");
        assertThat(report.getGrossProfit()).isEqualByComparingTo("2000.00");
        assertThat(report.getMarginPercentage()).isEqualByComparingTo("20.00");
        assertThat(report.getByProduct()).hasSize(1);
        assertThat(report.getByProduct().get(0).getProfit()).isEqualByComparingTo("2000.00");
    }

    @Test
    void inventoryReport_shouldMarkStatusAndStockValue() {
        when(reportDataClient.fetchProducts()).thenReturn(List.of(
                new ProductRecord(1L, "GB-001", "890000001", "Gold Bangle",
                        new BigDecimal("4000.00"), new BigDecimal("5000.00"), true)));
        when(reportDataClient.fetchInventory()).thenReturn(List.of(
                new InventoryRecord(1L, 1L, 1L, 100, 0, 100, 20),
                new InventoryRecord(2L, 1L, 2L, 5, 0, 5, 20),
                new InventoryRecord(3L, 1L, 3L, 0, 0, 0, 20)));

        InventoryReportResponse report = reportService.getInventoryReport();

        assertThat(report.getTotalRows()).isEqualTo(3);
        assertThat(report.getTotalQuantity()).isEqualTo(105);
        assertThat(report.getTotalStockValue()).isEqualByComparingTo("420000.00");
        assertThat(report.getItems().get(0).getStatus()).isEqualTo("OK");
        assertThat(report.getItems().get(1).getStatus()).isEqualTo("LOW");
        assertThat(report.getItems().get(2).getStatus()).isEqualTo("OUT_OF_STOCK");
    }

    @Test
    void purchaseReport_shouldOnlyCountReceivedPurchases() {
        when(reportDataClient.fetchPurchases()).thenReturn(List.of(
                new PurchaseRecord(1L, "PO-000001", 10L, 1L, LocalDate.of(2026, 8, 3),
                        new BigDecimal("8000.00"), BigDecimal.ZERO, BigDecimal.ZERO,
                        new BigDecimal("8000.00"), "RECEIVED",
                        List.of(new PurchaseItemRecord(1L, 1L, 2, new BigDecimal("4000.00"),
                                BigDecimal.ZERO, BigDecimal.ZERO, new BigDecimal("8000.00")))),
                new PurchaseRecord(2L, "PO-000002", 10L, 1L, LocalDate.of(2026, 8, 4),
                        new BigDecimal("4000.00"), BigDecimal.ZERO, BigDecimal.ZERO,
                        new BigDecimal("4000.00"), "ORDERED",
                        List.of(new PurchaseItemRecord(2L, 1L, 1, new BigDecimal("4000.00"),
                                BigDecimal.ZERO, BigDecimal.ZERO, new BigDecimal("4000.00"))))));

        PurchaseReportResponse report = reportService.getPurchaseReport(
                LocalDate.of(2026, 8, 1), LocalDate.of(2026, 8, 31));

        assertThat(report.getTotalPurchases()).isEqualTo(1);
        assertThat(report.getTotalSpent()).isEqualByComparingTo("8000.00");
        assertThat(report.getTotalItemsPurchased()).isEqualTo(2);
    }
}
