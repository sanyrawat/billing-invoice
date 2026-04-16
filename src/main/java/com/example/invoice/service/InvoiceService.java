package com.example.invoice.service;

import com.example.invoice.dto.InvoiceRequest;

public interface InvoiceService {
    byte[] generateInvoicePdf(InvoiceRequest invoiceRequest);
}
