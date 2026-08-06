package com.retailflow.billingservice.billing.printer;

import com.retailflow.billingservice.billing.dto.response.BillItemResponse;
import com.retailflow.billingservice.billing.dto.response.BillResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Formats a bill as a printable receipt for TVS 58mm thermal printers.
 * A real integration would send the returned text to the printer device.
 */
@Slf4j
@Service
public class TvsPrinterService implements PrinterService {

    private static final String LINE = "--------------------------------";
    private static final String HEAVY_LINE = "================================";

    @Override
    public String print(BillResponse bill) {
        StringBuilder sb = new StringBuilder();
        sb.append(HEAVY_LINE).append('\n')
                .append("        RetailFlow Store").append('\n')
                .append("       sales@retailflow.com").append('\n')
                .append(HEAVY_LINE).append('\n')
                .append("Invoice   : ").append(bill.getInvoiceNumber()).append('\n')
                .append("Date      : ").append(bill.getBillDate()).append('\n')
                .append("Sale ID   : ").append(bill.getSaleId()).append('\n')
                .append(LINE).append('\n')
                .append("Item          Qty     Amount").append('\n')
                .append(LINE).append('\n');

        if (bill.getItems() != null) {
            for (BillItemResponse item : bill.getItems()) {
                String name = item.getProductName() != null ? item.getProductName() : "Item " + item.getProductId();
                sb.append(String.format("%-14s %3d %10.2f%n",
                        trim(name, 14), item.getQuantity(), amount(item.getTotal())));
            }
        }

        sb.append(LINE).append('\n')
                .append(String.format("%-20s %10.2f%n", "Subtotal", amount(bill.getSubtotal())))
                .append(String.format("%-20s %10.2f%n", "Discount", amount(bill.getDiscount())))
                .append(String.format("%-20s %10.2f%n", "Tax", amount(bill.getTax())))
                .append(String.format("%-20s %10.2f%n", "TOTAL", amount(bill.getTotal())))
                .append(HEAVY_LINE).append('\n')
                .append("Payment     : ").append(bill.getPaymentStatus()).append('\n')
                .append("Thank you for shopping with us!").append('\n')
                .append(HEAVY_LINE);

        String printable = sb.toString();
        log.info("Printing bill [{}] via TVS printer", bill.getInvoiceNumber());

        return printable;
    }

    private String trim(String value, int max) {
        if (value == null || value.length() <= max) {
            return value;
        }
        return value.substring(0, max);
    }

    private BigDecimal amount(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}
