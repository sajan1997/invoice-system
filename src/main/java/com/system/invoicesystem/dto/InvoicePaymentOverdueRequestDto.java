package com.system.invoicesystem.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvoicePaymentOverdueRequestDto {

    @NotNull
    private Double lateFee;

    @NotNull
    @Min(1)
    private Integer overdueDays;

}
