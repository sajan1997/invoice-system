package com.system.invoicesystem.controller;

import com.system.invoicesystem.constant.MessageConstant;
import com.system.invoicesystem.constant.enums.InvoiceStatusEnum;
import com.system.invoicesystem.dto.InvoiceCreateResponseDto;
import com.system.invoicesystem.dto.InvoicePaymentOverdueRequestDto;
import com.system.invoicesystem.dto.InvoicePaymentRequestDto;
import com.system.invoicesystem.dto.InvoiceRequestDto;
import com.system.invoicesystem.dto.InvoiceResponseDto;
import com.system.invoicesystem.dto.ResponseDto;
import com.system.invoicesystem.entity.InvoiceEntity;
import com.system.invoicesystem.service.InterfaceInvoiceService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class InvoiceControllerTest {

    @InjectMocks
    private InvoiceController invoiceController;

    @Mock
    private InterfaceInvoiceService interfaceInvoiceService;

    @Test
    void createInvoice() {

        InvoiceRequestDto invoiceRequestDto = new InvoiceRequestDto();
        invoiceRequestDto.setAmount(500.00);
        invoiceRequestDto.setDueDate(LocalDate.now().plusDays(5));

        InvoiceCreateResponseDto invoiceCreateResponseDto = InvoiceCreateResponseDto.builder().id(1L).build();
        when(interfaceInvoiceService.createInvoice(any())).thenReturn(invoiceCreateResponseDto);

        ResponseEntity<ResponseDto<InvoiceCreateResponseDto>> response = invoiceController.createInvoice(invoiceRequestDto);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(invoiceCreateResponseDto, response.getBody().getData());
    }

    @Test
    void invoices() {
        InvoiceResponseDto invoiceResponseDto = InvoiceResponseDto.builder().id(1L).amount(500.00).paidAmount(0.0)
                .dueDate(LocalDate.now().plusDays(5)).status(InvoiceStatusEnum.PENDING.getValue()).build();
        InvoiceResponseDto invoiceResponseDto1 = InvoiceResponseDto.builder().id(2L).amount(1000.00).paidAmount(300.0)
                .dueDate(LocalDate.now().minusDays(6)).status(InvoiceStatusEnum.PENDING.getValue()).build();
        when(interfaceInvoiceService.invoices()).thenReturn(List.of(invoiceResponseDto, invoiceResponseDto1));

        ResponseEntity<ResponseDto<List<InvoiceResponseDto>>> response = invoiceController.invoices();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().getData().size());
    }

    @Test
    void invoicePayment() {

        InvoicePaymentRequestDto paymentRequestDto = new InvoicePaymentRequestDto();
        paymentRequestDto.setAmount(300.00);

        InvoiceResponseDto invoiceResponseDto = InvoiceResponseDto.builder().id(2L).amount(1000.00).paidAmount(300.0)
                .dueDate(LocalDate.now().minusDays(6)).status(InvoiceStatusEnum.PENDING.getValue()).build();

        when(interfaceInvoiceService.invoicePayment(any(), any())).thenReturn(invoiceResponseDto);

        ResponseEntity<ResponseDto<InvoiceResponseDto>> response = invoiceController.invoicePayment(2L, paymentRequestDto);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(invoiceResponseDto, response.getBody().getData());
    }

    @Test
    void processPaymentOverdue() {
        InvoicePaymentOverdueRequestDto paymentOverdueRequestDto = new InvoicePaymentOverdueRequestDto();
        paymentOverdueRequestDto.setLateFee(30.00);
        paymentOverdueRequestDto.setOverdueDays(5);

        when(interfaceInvoiceService.processPaymentOverdue(any())).thenReturn(MessageConstant.OVERDUE_INVOICES_PROCESSED);

        ResponseEntity<ResponseDto<String>> response = invoiceController.processPaymentOverdue(paymentOverdueRequestDto);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(MessageConstant.OVERDUE_INVOICES_PROCESSED, response.getBody().getData());
    }

}