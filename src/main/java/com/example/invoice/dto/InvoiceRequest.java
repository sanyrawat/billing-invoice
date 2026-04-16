package com.example.invoice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceRequest {

    @NotBlank(message = "Invoice number is mandatory")
    private String invoiceNumber;

    @NotNull(message = "Invoice date is mandatory")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate invoiceDate;

    @NotBlank(message = "Customer name is mandatory")
    private String customerName;

    @NotBlank(message = "Customer address is mandatory")
    private String customerAddress;

    @Valid
    @NotEmpty(message = "At least one item is mandatory")
    private List<InvoiceItem> items;

    @NotNull(message = "Tax percentage is mandatory")
    @DecimalMin(value = "0.00", inclusive = true, message = "Tax percentage cannot be negative")
    private BigDecimal taxPercentage;


    @NotBlank(message = "Company name is mandatory")
    private String companyName;

    @NotBlank(message = "Company address is mandatory")
    private String companyAddress;

    private String companyPhone;

    private String companyEmail;

    private String companyGstin;

    private String companyState;

    private String customerContactNo;
    
    private String customerGstin;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate poDate;

    private String poNumber;

    private String ewayBillNumber;

    private String bankName;

    private String accountNumber;

    private String ifscCode;

    private String accountHolderName;
}
