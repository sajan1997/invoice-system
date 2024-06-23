package com.system.invoicesystem.controller;

import com.system.invoicesystem.constant.MessageConstant;
import com.system.invoicesystem.constant.RoutingConstant;
import com.system.invoicesystem.dto.InvoiceCreateResponseDto;
import com.system.invoicesystem.dto.InvoicePaymentOverdueRequestDto;
import com.system.invoicesystem.dto.InvoicePaymentRequestDto;
import com.system.invoicesystem.dto.InvoiceRequestDto;
import com.system.invoicesystem.dto.InvoiceResponseDto;
import com.system.invoicesystem.dto.ResponseDto;
import com.system.invoicesystem.service.InterfaceInvoiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.system.invoicesystem.constant.RoutingConstant.INVOICE;

@RequiredArgsConstructor
@RequestMapping(value = INVOICE)
@RestController
public class InvoiceController {

    private final InterfaceInvoiceService interfaceInvoiceService;

    @PostMapping
    public ResponseEntity<ResponseDto<InvoiceCreateResponseDto>> createInvoice(@Valid @RequestBody InvoiceRequestDto invoiceRequestDto) {
        return new ResponseEntity<>(new ResponseDto<>(MessageConstant.CREATE_INVOICE, interfaceInvoiceService.createInvoice(invoiceRequestDto)),
                HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<ResponseDto<List<InvoiceResponseDto>>> invoices() {
        return new ResponseEntity<>(new ResponseDto<>(MessageConstant.INVOICES, interfaceInvoiceService.invoices()),
                HttpStatus.OK);
    }

    @PostMapping(value = RoutingConstant.INVOICE_PAYMENT)
    public ResponseEntity<ResponseDto<InvoiceResponseDto>> invoicePayment(@PathVariable(value = "invoiceId") Long id, @Valid @RequestBody InvoicePaymentRequestDto invoicePaymentRequestDto) {
        return new ResponseEntity<>(new ResponseDto<>(MessageConstant.INVOICE_PAYMENT, interfaceInvoiceService.invoicePayment(id, invoicePaymentRequestDto)),
                HttpStatus.OK);
    }

    @PostMapping(value = RoutingConstant.INVOICE_OVERDUE)
    public ResponseEntity<ResponseDto<String>> processPaymentOverdue(@Valid @RequestBody InvoicePaymentOverdueRequestDto paymentOverdueRequestDto) {
        return new ResponseEntity<>(new ResponseDto<>(MessageConstant.INVOICES_PAYMENT_OVERDUE, interfaceInvoiceService.processPaymentOverdue(paymentOverdueRequestDto)),
                HttpStatus.OK);
    }

}
