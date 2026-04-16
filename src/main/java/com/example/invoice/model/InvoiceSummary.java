package com.example.invoice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceSummary {
    private BigDecimal subtotal;
    private BigDecimal taxAmount;
    private BigDecimal grandTotal;
}
