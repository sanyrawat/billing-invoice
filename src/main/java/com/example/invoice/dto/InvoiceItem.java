package com.example.invoice.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceItem {

    @NotBlank(message = "Item description is mandatory")
    private String description;

    private String hsnSac;

    @NotNull(message = "Quantity is mandatory")
    @DecimalMin(value = "1", inclusive = true, message = "Quantity must be greater than 0")
    private BigDecimal quantity;

    private String unit;

    @NotNull(message = "Unit price is mandatory")
    @DecimalMin(value = "0.01", inclusive = true, message = "Unit price must be greater than 0")
    private BigDecimal unitPrice;
}
