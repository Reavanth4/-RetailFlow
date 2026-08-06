package com.retailflow.salesservice.sale.service.imp;

import com.retailflow.salesservice.client.InventoryGateway;
import com.retailflow.salesservice.common.exception.InsufficientStockException;
import com.retailflow.salesservice.common.exception.InvalidRequestException;
import com.retailflow.salesservice.common.exception.ResourceNotFoundException;
import com.retailflow.salesservice.sale.dto.request.ReturnCreateRequest;
import com.retailflow.salesservice.sale.dto.request.ReturnItemRequest;
import com.retailflow.salesservice.sale.dto.request.SaleCreateRequest;
import com.retailflow.salesservice.sale.dto.request.SaleItemRequest;
import com.retailflow.salesservice.sale.dto.request.SaleUpdateRequest;
import com.retailflow.salesservice.sale.dto.response.ReturnResponse;
import com.retailflow.salesservice.sale.dto.response.SaleResponse;
import com.retailflow.salesservice.sale.entity.ReturnItem;
import com.retailflow.salesservice.sale.entity.Sale;
import com.retailflow.salesservice.sale.entity.SaleItem;
import com.retailflow.salesservice.sale.entity.SaleReturn;
import com.retailflow.salesservice.sale.entity.SaleStatus;
import com.retailflow.salesservice.sale.mapper.ReturnMapper;
import com.retailflow.salesservice.sale.mapper.SaleMapper;
import com.retailflow.salesservice.sale.repository.SaleRepository;
import com.retailflow.salesservice.sale.repository.SaleReturnRepository;
import com.retailflow.salesservice.sale.service.SaleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SaleServiceImpl implements SaleService {

    private final SaleRepository saleRepository;

    private final SaleReturnRepository saleReturnRepository;

    private final SaleMapper saleMapper;

    private final ReturnMapper returnMapper;

    private final InventoryGateway inventoryGateway;

    @Override
    public SaleResponse createSale(SaleCreateRequest request) {
        Sale sale = new Sale();
        sale.setCustomerId(request.getCustomerId());
        sale.setWarehouseId(request.getWarehouseId());
        sale.setSaleDate(request.getSaleDate() != null ? request.getSaleDate() : LocalDate.now());
        sale.setTax(defaultZero(request.getTax()));
        sale.setDiscount(defaultZero(request.getDiscount()));
        sale.setStatus(SaleStatus.DRAFT);
        sale.setSaleNumber(generateSaleNumber());

        applyItems(sale, request.getItems());

        return saleMapper.toResponse(saleRepository.save(sale));
    }

    @Override
    public SaleResponse updateSale(Long saleId, SaleUpdateRequest request) {
        Sale sale = findSale(saleId);

        if (sale.getStatus() != SaleStatus.DRAFT) {
            throw new InvalidRequestException(
                    "Only DRAFT sales can be updated, current status: " + sale.getStatus());
        }

        if (request.getCustomerId() != null) {
            sale.setCustomerId(request.getCustomerId());
        }
        if (request.getWarehouseId() != null) {
            sale.setWarehouseId(request.getWarehouseId());
        }
        if (request.getSaleDate() != null) {
            sale.setSaleDate(request.getSaleDate());
        }
        if (request.getTax() != null) {
            sale.setTax(request.getTax());
        }
        if (request.getDiscount() != null) {
            sale.setDiscount(request.getDiscount());
        }

        if (request.getItems() != null && !request.getItems().isEmpty()) {
            sale.clearItems();
            applyItems(sale, request.getItems());
        } else {
            recalculate(sale);
        }

        return saleMapper.toResponse(saleRepository.save(sale));
    }

    @Override
    public SaleResponse getSaleById(Long saleId) {
        return saleMapper.toResponse(findSale(saleId));
    }

    @Override
    public List<SaleResponse> getAllSales() {
        return saleRepository.findAll().stream()
                .map(saleMapper::toResponse)
                .toList();
    }

    @Override
    public SaleResponse completeSale(Long saleId) {
        Sale sale = findSale(saleId);

        if (sale.getStatus() != SaleStatus.DRAFT) {
            throw new InvalidRequestException(
                    "Only DRAFT sales can be completed, current status: " + sale.getStatus());
        }

        for (SaleItem item : sale.getItems()) {
            int available = inventoryGateway.getAvailableStock(item.getProductId(), sale.getWarehouseId());

            if (available < item.getQuantity()) {
                throw new InsufficientStockException(
                        "Insufficient stock for product " + item.getProductId()
                                + ". Available: " + available
                                + ", requested: " + item.getQuantity());
            }

            inventoryGateway.stockOut(
                    item.getProductId(),
                    sale.getWarehouseId(),
                    item.getQuantity(),
                    "SALE",
                    "SALE",
                    sale.getId(),
                    "Stock deducted for sale " + sale.getSaleNumber());
        }

        sale.setStatus(SaleStatus.COMPLETED);

        return saleMapper.toResponse(saleRepository.save(sale));
    }

    @Override
    public SaleResponse cancelSale(Long saleId) {
        Sale sale = findSale(saleId);

        if (sale.getStatus() != SaleStatus.DRAFT) {
            throw new InvalidRequestException(
                    "Only DRAFT sales can be cancelled, current status: " + sale.getStatus());
        }

        sale.setStatus(SaleStatus.CANCELLED);

        return saleMapper.toResponse(saleRepository.save(sale));
    }

    @Override
    public void deleteSale(Long saleId) {
        Sale sale = findSale(saleId);

        if (sale.getStatus() != SaleStatus.DRAFT) {
            throw new InvalidRequestException(
                    "Only DRAFT sales can be deleted, current status: " + sale.getStatus());
        }

        saleRepository.delete(sale);
    }

    @Override
    public ReturnResponse createReturn(Long saleId, ReturnCreateRequest request) {
        Sale sale = findSale(saleId);

        if (sale.getStatus() != SaleStatus.COMPLETED && sale.getStatus() != SaleStatus.RETURNED) {
            throw new InvalidRequestException(
                    "Only completed sales can be returned, current status: " + sale.getStatus());
        }

        SaleReturn saleReturn = new SaleReturn();
        saleReturn.setSaleId(saleId);
        saleReturn.setReturnNumber(generateReturnNumber());
        saleReturn.setReturnDate(LocalDate.now());
        saleReturn.setReason(request.getReason());

        for (ReturnItemRequest itemRequest : request.getItems()) {
            SaleItem saleItem = findSaleItem(sale, itemRequest.getProductId());
            int alreadyReturned = alreadyReturnedQuantity(saleId, itemRequest.getProductId());
            int eligible = saleItem.getQuantity() - alreadyReturned;

            if (itemRequest.getQuantity() > eligible) {
                throw new InvalidRequestException(
                        "Return quantity for product " + itemRequest.getProductId()
                                + " exceeds eligible quantity. Eligible: " + eligible);
            }

            BigDecimal total = saleItem.getUnitPrice()
                    .multiply(BigDecimal.valueOf(itemRequest.getQuantity()));

            ReturnItem returnItem = ReturnItem.builder()
                    .productId(itemRequest.getProductId())
                    .quantity(itemRequest.getQuantity())
                    .unitPrice(saleItem.getUnitPrice())
                    .total(total)
                    .build();

            saleReturn.addItem(returnItem);
        }

        SaleReturn saved = saleReturnRepository.save(saleReturn);

        for (ReturnItem item : saved.getItems()) {
            inventoryGateway.stockIn(
                    item.getProductId(),
                    sale.getWarehouseId(),
                    item.getQuantity(),
                    "RETURN_IN",
                    "RETURN",
                    saved.getId(),
                    "Stock restored for return " + saved.getReturnNumber());
        }

        if (isFullyReturned(sale)) {
            sale.setStatus(SaleStatus.RETURNED);
            saleRepository.save(sale);
        }

        return returnMapper.toResponse(saved);
    }

    @Override
    public List<ReturnResponse> getReturnsBySale(Long saleId) {
        return saleReturnRepository.findBySaleId(saleId).stream()
                .map(returnMapper::toResponse)
                .toList();
    }

    private void applyItems(Sale sale, List<SaleItemRequest> itemRequests) {
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal total = BigDecimal.ZERO;

        for (SaleItemRequest itemRequest : itemRequests) {
            BigDecimal tax = defaultZero(itemRequest.getTax());
            BigDecimal discount = defaultZero(itemRequest.getDiscount());
            BigDecimal lineAmount = itemRequest.getUnitPrice()
                    .multiply(BigDecimal.valueOf(itemRequest.getQuantity()));
            BigDecimal lineTotal = lineAmount.subtract(discount).add(tax);

            SaleItem item = SaleItem.builder()
                    .productId(itemRequest.getProductId())
                    .quantity(itemRequest.getQuantity())
                    .unitPrice(itemRequest.getUnitPrice())
                    .tax(tax)
                    .discount(discount)
                    .total(lineTotal)
                    .build();

            sale.addItem(item);
            subtotal = subtotal.add(lineAmount);
            total = total.add(lineTotal);
        }

        sale.setSubtotal(subtotal);
        sale.setTotal(total);
    }

    private void recalculate(Sale sale) {
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal total = BigDecimal.ZERO;

        for (SaleItem item : sale.getItems()) {
            subtotal = subtotal.add(item.getUnitPrice()
                    .multiply(BigDecimal.valueOf(item.getQuantity())));
            total = total.add(item.getTotal());
        }

        sale.setSubtotal(subtotal);
        sale.setTotal(total);
    }

    private boolean isFullyReturned(Sale sale) {
        int totalSold = sale.getItems().stream()
                .mapToInt(SaleItem::getQuantity)
                .sum();

        int totalReturned = saleReturnRepository.findBySaleId(sale.getId()).stream()
                .flatMap(r -> r.getItems().stream())
                .mapToInt(ReturnItem::getQuantity)
                .sum();

        return totalReturned >= totalSold;
    }

    private int alreadyReturnedQuantity(Long saleId, Long productId) {
        return saleReturnRepository.findBySaleId(saleId).stream()
                .flatMap(r -> r.getItems().stream())
                .filter(item -> item.getProductId().equals(productId))
                .mapToInt(ReturnItem::getQuantity)
                .sum();
    }

    private SaleItem findSaleItem(Sale sale, Long productId) {
        return sale.getItems().stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new InvalidRequestException(
                        "Product " + productId + " was not part of this sale."));
    }

    private Sale findSale(Long saleId) {
        return saleRepository.findById(saleId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Sale not found with id: " + saleId));
    }

    private String generateSaleNumber() {
        return "SL-" + String.format("%06d", saleRepository.count() + 1);
    }

    private String generateReturnNumber() {
        return "RT-" + String.format("%06d", saleReturnRepository.count() + 1);
    }

    private BigDecimal defaultZero(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }
}
