package com.system.invoicesystem.dto;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
public class InvoiceRequestDto {

    @NotNull
    private Double amount;

    @NotNull
    private LocalDate dueDate;

}
