package com.retailflow.purchaseservice.purchase.service.imp;

import com.retailflow.purchaseservice.client.InventoryGateway;
import com.retailflow.purchaseservice.common.exception.DuplicateResourceException;
import com.retailflow.purchaseservice.common.exception.InvalidRequestException;
import com.retailflow.purchaseservice.common.exception.ResourceNotFoundException;
import com.retailflow.purchaseservice.purchase.dto.request.PurchaseCreateRequest;
import com.retailflow.purchaseservice.purchase.dto.request.PurchaseItemRequest;
import com.retailflow.purchaseservice.purchase.dto.request.PurchaseUpdateRequest;
import com.retailflow.purchaseservice.purchase.dto.response.PurchaseResponse;
import com.retailflow.purchaseservice.purchase.entity.Purchase;
import com.retailflow.purchaseservice.purchase.entity.PurchaseItem;
import com.retailflow.purchaseservice.purchase.entity.PurchaseStatus;
import com.retailflow.purchaseservice.purchase.mapper.PurchaseMapper;
import com.retailflow.purchaseservice.purchase.repository.PurchaseRepository;
import com.retailflow.purchaseservice.purchase.service.PurchaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PurchaseServiceImpl implements PurchaseService {

    private final PurchaseRepository purchaseRepository;

    private final PurchaseMapper purchaseMapper;

    private final InventoryGateway inventoryGateway;

    @Override
    public PurchaseResponse createPurchase(PurchaseCreateRequest request) {
        Purchase purchase = new Purchase();
        purchase.setSupplierId(request.getSupplierId());
        purchase.setWarehouseId(request.getWarehouseId());
        purchase.setPurchaseDate(request.getPurchaseDate() != null ? request.getPurchaseDate() : LocalDate.now());
        purchase.setTax(defaultZero(request.getTax()));
        purchase.setDiscount(defaultZero(request.getDiscount()));
        purchase.setStatus(PurchaseStatus.DRAFT);
        purchase.setPurchaseNumber(generatePurchaseNumber());

        applyItems(purchase, request.getItems());

        return purchaseMapper.toResponse(purchaseRepository.save(purchase));
    }

    @Override
    public PurchaseResponse updatePurchase(Long purchaseId, PurchaseUpdateRequest request) {
        Purchase purchase = findPurchase(purchaseId);

        if (purchase.getStatus() == PurchaseStatus.RECEIVED
                || purchase.getStatus() == PurchaseStatus.CANCELLED) {
            throw new InvalidRequestException(
                    "Purchase cannot be updated after it is " + purchase.getStatus());
        }

        if (request.getSupplierId() != null) {
            purchase.setSupplierId(request.getSupplierId());
        }
        if (request.getWarehouseId() != null) {
            purchase.setWarehouseId(request.getWarehouseId());
        }
        if (request.getPurchaseDate() != null) {
            purchase.setPurchaseDate(request.getPurchaseDate());
        }
        if (request.getTax() != null) {
            purchase.setTax(request.getTax());
        }
        if (request.getDiscount() != null) {
            purchase.setDiscount(request.getDiscount());
        }

        if (request.getItems() != null && !request.getItems().isEmpty()) {
            purchase.clearItems();
            applyItems(purchase, request.getItems());
        } else {
            recalculate(purchase);
        }

        return purchaseMapper.toResponse(purchaseRepository.save(purchase));
    }

    @Override
    public PurchaseResponse getPurchaseById(Long purchaseId) {
        return purchaseMapper.toResponse(findPurchase(purchaseId));
    }

    @Override
    public List<PurchaseResponse> getAllPurchases() {
        return purchaseRepository.findAll().stream()
                .map(purchaseMapper::toResponse)
                .toList();
    }

    @Override
    public PurchaseResponse orderPurchase(Long purchaseId) {
        Purchase purchase = findPurchase(purchaseId);

        if (purchase.getStatus() != PurchaseStatus.DRAFT) {
            throw new InvalidRequestException(
                    "Only DRAFT purchases can be ordered, current status: " + purchase.getStatus());
        }

        purchase.setStatus(PurchaseStatus.ORDERED);

        return purchaseMapper.toResponse(purchaseRepository.save(purchase));
    }

    @Override
    public PurchaseResponse receivePurchase(Long purchaseId) {
        Purchase purchase = findPurchase(purchaseId);

        if (purchase.getStatus() == PurchaseStatus.RECEIVED) {
            throw new DuplicateResourceException(
                    "Purchase has already been received.");
        }
        if (purchase.getStatus() == PurchaseStatus.CANCELLED) {
            throw new InvalidRequestException(
                    "A cancelled purchase cannot be received.");
        }

        for (PurchaseItem item : purchase.getItems()) {
            inventoryGateway.stockIn(
                    item.getProductId(),
                    purchase.getWarehouseId(),
                    item.getQuantity(),
                    "PURCHASE",
                    "PURCHASE",
                    purchase.getId(),
                    "Goods received for purchase " + purchase.getPurchaseNumber());
        }

        purchase.setStatus(PurchaseStatus.RECEIVED);

        return purchaseMapper.toResponse(purchaseRepository.save(purchase));
    }

    @Override
    public PurchaseResponse cancelPurchase(Long purchaseId) {
        Purchase purchase = findPurchase(purchaseId);

        if (purchase.getStatus() == PurchaseStatus.RECEIVED) {
            throw new InvalidRequestException(
                    "A received purchase cannot be cancelled.");
        }
        if (purchase.getStatus() == PurchaseStatus.CANCELLED) {
            throw new InvalidRequestException(
                    "Purchase is already cancelled.");
        }

        purchase.setStatus(PurchaseStatus.CANCELLED);

        return purchaseMapper.toResponse(purchaseRepository.save(purchase));
    }

    @Override
    public void deletePurchase(Long purchaseId) {
        Purchase purchase = findPurchase(purchaseId);

        if (purchase.getStatus() == PurchaseStatus.RECEIVED) {
            throw new InvalidRequestException(
                    "A received purchase cannot be deleted.");
        }

        purchaseRepository.delete(purchase);
    }

    private void applyItems(Purchase purchase, List<PurchaseItemRequest> itemRequests) {
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal total = BigDecimal.ZERO;

        for (PurchaseItemRequest itemRequest : itemRequests) {
            BigDecimal tax = defaultZero(itemRequest.getTax());
            BigDecimal discount = defaultZero(itemRequest.getDiscount());
            BigDecimal lineAmount = itemRequest.getUnitPrice()
                    .multiply(BigDecimal.valueOf(itemRequest.getQuantity()));
            BigDecimal lineTotal = lineAmount.subtract(discount).add(tax);

            PurchaseItem item = PurchaseItem.builder()
                    .productId(itemRequest.getProductId())
                    .quantity(itemRequest.getQuantity())
                    .unitPrice(itemRequest.getUnitPrice())
                    .tax(tax)
                    .discount(discount)
                    .total(lineTotal)
                    .build();

            purchase.addItem(item);
            subtotal = subtotal.add(lineAmount);
            total = total.add(lineTotal);
        }

        purchase.setSubtotal(subtotal);
        purchase.setTotal(total);
    }

    private void recalculate(Purchase purchase) {
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal total = BigDecimal.ZERO;

        for (PurchaseItem item : purchase.getItems()) {
            BigDecimal lineAmount = item.getUnitPrice()
                    .multiply(BigDecimal.valueOf(item.getQuantity()));
            subtotal = subtotal.add(lineAmount);
            total = total.add(item.getTotal());
        }

        purchase.setSubtotal(subtotal);
        purchase.setTotal(total);
    }

    private Purchase findPurchase(Long purchaseId) {
        return purchaseRepository.findById(purchaseId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Purchase not found with id: " + purchaseId));
    }

    private String generatePurchaseNumber() {
        return "PO-" + String.format("%06d", purchaseRepository.count() + 1);
    }

    private BigDecimal defaultZero(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }
}
