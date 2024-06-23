package com.system.invoicesystem.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class InvoiceResponseDto {

    private Long id;

    private Double amount;

    private Double paidAmount;

    private LocalDate dueDate;

    private String status;

}
