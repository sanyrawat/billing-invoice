package com.example.invoice.service.impl;

import com.example.invoice.dto.InvoiceItem;
import com.example.invoice.dto.InvoiceRequest;
import com.example.invoice.model.InvoiceSummary;
import com.example.invoice.service.InvoiceService;
import com.example.invoice.util.PdfGeneratorUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Slf4j
@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private static final BigDecimal HUNDRED = new BigDecimal("100");

    private final PdfGeneratorUtil pdfGeneratorUtil;

    @Override
    public byte[] generateInvoicePdf(InvoiceRequest invoiceRequest) {
        InvoiceSummary summary = calculateInvoiceSummary(invoiceRequest);
        log.info("Generating invoice PDF for invoiceNumber={}, customerName={}",
                invoiceRequest.getInvoiceNumber(), invoiceRequest.getCustomerName());
        return pdfGeneratorUtil.generateInvoicePdf(invoiceRequest, summary);
    }

    private InvoiceSummary calculateInvoiceSummary(InvoiceRequest invoiceRequest) {
        BigDecimal subtotal = invoiceRequest.getItems().stream()
                .map(this::calculateLineItemTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal taxAmount = subtotal
                .multiply(invoiceRequest.getTaxPercentage())
                .divide(HUNDRED, 2, RoundingMode.HALF_UP);

        BigDecimal grandTotal = subtotal.add(taxAmount).setScale(2, RoundingMode.HALF_UP);

        return InvoiceSummary.builder()
                .subtotal(subtotal)
                .taxAmount(taxAmount)
                .grandTotal(grandTotal)
                .build();
    }

    private BigDecimal calculateLineItemTotal(InvoiceItem item) {
        return item.getQuantity()
                .multiply(item.getUnitPrice())
                .setScale(2, RoundingMode.HALF_UP);
    }
}
