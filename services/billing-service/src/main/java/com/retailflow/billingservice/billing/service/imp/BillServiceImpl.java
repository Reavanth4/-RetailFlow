package com.retailflow.billingservice.billing.service.imp;

import com.retailflow.billingservice.billing.dto.request.BillCreateRequest;
import com.retailflow.billingservice.billing.dto.request.BillItemRequest;
import com.retailflow.billingservice.billing.dto.response.BillResponse;
import com.retailflow.billingservice.billing.entity.Bill;
import com.retailflow.billingservice.billing.entity.BillItem;
import com.retailflow.billingservice.billing.entity.PaymentStatus;
import com.retailflow.billingservice.billing.mapper.BillMapper;
import com.retailflow.billingservice.billing.printer.PrinterService;
import com.retailflow.billingservice.billing.repository.BillRepository;
import com.retailflow.billingservice.billing.service.BillService;
import com.retailflow.billingservice.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BillServiceImpl implements BillService {

    private final BillRepository billRepository;
    private final BillMapper billMapper;
    private final PrinterService printerService;

    @Override
    @Transactional
    public BillResponse createBill(BillCreateRequest request) {
        Bill bill = billMapper.toEntity(request);
        bill.setInvoiceNumber(generateInvoiceNumber());
        bill.setPaymentStatus(PaymentStatus.PENDING);

        for (BillItemRequest itemRequest : request.getItems()) {
            BillItem item = billMapper.toItemEntity(itemRequest);
            item.setTotal(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            bill.addItem(item);
        }

        BigDecimal subtotal = bill.getItems().stream()
                .map(BillItem::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        bill.setSubtotal(subtotal);
        bill.setDiscount(bill.getDiscount() == null ? BigDecimal.ZERO : bill.getDiscount());
        bill.setTax(bill.getTax() == null ? BigDecimal.ZERO : bill.getTax());
        bill.setTotal(subtotal.subtract(bill.getDiscount()).add(bill.getTax()));
        bill.setBillDate(request.getBillDate() == null ? LocalDate.now() : request.getBillDate());

        bill = billRepository.save(bill);
        log.info("Bill [{}] created for sale [{}]", bill.getInvoiceNumber(), bill.getSaleId());

        return billMapper.toResponse(bill);
    }

    @Override
    @Transactional(readOnly = true)
    public BillResponse getBillById(Long id) {
        return billMapper.toResponse(findBill(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BillResponse> getAllBills() {
        return billMapper.toResponseList(billRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BillResponse> getBillsBySale(Long saleId) {
        return billMapper.toResponseList(billRepository.findBySaleId(saleId));
    }

    @Override
    @Transactional(readOnly = true)
    public String printBill(Long id) {
        Bill bill = findBill(id);
        return printerService.print(billMapper.toResponse(bill));
    }

    private Bill findBill(Long id) {
        return billRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bill not found with id: " + id));
    }

    private String generateInvoiceNumber() {
        long next = billRepository.count() + 1;
        return String.format("INV-%06d", next);
    }
}
