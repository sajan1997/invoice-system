package com.system.invoicesystem.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class InvoicePaymentRequestDto {

    @NotNull
    private Double amount;

}
