package com.example.invoice.controller;

import com.example.invoice.dto.InvoiceRequest;
import com.example.invoice.service.InvoiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/invoices")
@Tag(name = "Invoice API", description = "Generate professional invoice PDFs")
public class InvoiceController {

    private final InvoiceService invoiceService;

    @PostMapping("/generate")
    @Operation(summary = "Generate invoice PDF", description = "Accepts invoice JSON payload and returns PDF document")
    public ResponseEntity<byte[]> generateInvoice(@Valid @RequestBody InvoiceRequest invoiceRequest) {
        log.info("Invoice generation request received for invoiceNumber={}", invoiceRequest.getInvoiceNumber());
        byte[] pdfBytes = invoiceService.generateInvoicePdf(invoiceRequest);

        String fileName = invoiceRequest.getInvoiceNumber() + ".pdf";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.attachment().filename(fileName).build());

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }
}
