package com.system.invoicesystem.service;

import com.system.invoicesystem.dto.InvoiceCreateResponseDto;
import com.system.invoicesystem.dto.InvoicePaymentOverdueRequestDto;
import com.system.invoicesystem.dto.InvoicePaymentRequestDto;
import com.system.invoicesystem.dto.InvoiceRequestDto;
import com.system.invoicesystem.dto.InvoiceResponseDto;

import java.util.List;

public interface InterfaceInvoiceService {

    InvoiceCreateResponseDto createInvoice(InvoiceRequestDto invoiceRequestDto);

    List<InvoiceResponseDto> invoices();

    InvoiceResponseDto invoicePayment(Long id, InvoicePaymentRequestDto paymentRequestDto);

    String processPaymentOverdue(InvoicePaymentOverdueRequestDto paymentOverdueRequestDto);

}
