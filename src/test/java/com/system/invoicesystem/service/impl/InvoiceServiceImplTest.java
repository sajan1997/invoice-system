package com.system.invoicesystem.service.impl;

import com.system.invoicesystem.constant.MessageConstant;
import com.system.invoicesystem.constant.enums.InvoiceStatusEnum;
import com.system.invoicesystem.dao.InvoiceDao;
import com.system.invoicesystem.dto.InvoiceCreateResponseDto;
import com.system.invoicesystem.dto.InvoicePaymentOverdueRequestDto;
import com.system.invoicesystem.dto.InvoicePaymentRequestDto;
import com.system.invoicesystem.dto.InvoiceRequestDto;
import com.system.invoicesystem.dto.InvoiceResponseDto;
import com.system.invoicesystem.entity.InvoiceEntity;
import com.system.invoicesystem.exception.InvalidPaymentException;
import com.system.invoicesystem.exception.InvoiceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@ExtendWith(SpringExtension.class)
@SpringBootTest
class InvoiceServiceImplTest {

    @InjectMocks
    private InvoiceServiceImpl invoiceServiceImpl;

    @Mock
    private InvoiceDao invoiceDao;

    @Test
    void createInvoice() {
        InvoiceRequestDto invoiceRequestDto = new InvoiceRequestDto();
        invoiceRequestDto.setAmount(500.00);
        invoiceRequestDto.setDueDate(LocalDate.now().plusDays(5));

        InvoiceEntity invoiceEntity = InvoiceEntity.builder().id(1L).amount(500.00).paidAmount(0.0)
                .dueDate(LocalDate.now().plusDays(5)).build();
        when(invoiceDao.saveInvoice(any())).thenReturn(invoiceEntity);

        InvoiceCreateResponseDto response = invoiceServiceImpl.createInvoice(invoiceRequestDto);
        assertNotNull(response);
        assertEquals(invoiceEntity.getId(), response.getId());
    }

    @Test
    void invoices() {

        InvoiceEntity invoiceEntity = InvoiceEntity.builder().id(1L).amount(500.00).paidAmount(0.0)
                .dueDate(LocalDate.now().plusDays(5)).status(InvoiceStatusEnum.PENDING).build();
        InvoiceEntity invoiceEntity1 = InvoiceEntity.builder().id(2L).amount(1000.00).paidAmount(300.0)
                .dueDate(LocalDate.now().minusDays(6)).status(InvoiceStatusEnum.PENDING).build();
        List<InvoiceEntity> invoiceList = List.of(invoiceEntity, invoiceEntity1);

        when(invoiceDao.getInvoices()).thenReturn(invoiceList);

        List<InvoiceResponseDto> response = invoiceServiceImpl.invoices();
        assertNotNull(response);
        assertEquals(invoiceList.size(), response.size());
        assertEquals(invoiceEntity.getId(), response.get(0).getId());
    }

    @Test
    void invoicePayment() {
        InvoicePaymentRequestDto paymentRequestDto = new InvoicePaymentRequestDto();
        paymentRequestDto.setAmount(200.00);

        InvoiceEntity invoiceEntity = InvoiceEntity.builder().id(1L).amount(500.00).paidAmount(0.0)
                .dueDate(LocalDate.now().plusDays(5)).status(InvoiceStatusEnum.PENDING).build();
        when(invoiceDao.getInvoiceById(any())).thenReturn(Optional.ofNullable(invoiceEntity));

        when(invoiceDao.saveInvoice(any())).thenReturn(invoiceEntity);
        //partial pay
        InvoiceResponseDto response = invoiceServiceImpl.invoicePayment(1L, paymentRequestDto);
        assertNotNull(response);
        assertEquals(invoiceEntity.getId(), response.getId());

        //full pay
        paymentRequestDto.setAmount(300.00);
        InvoiceResponseDto response1 = invoiceServiceImpl.invoicePayment(1L, paymentRequestDto);
        assertNotNull(response1);
        assertEquals(invoiceEntity.getId(), response1.getId());
    }


    @Test
    void invoicePaymentFailure() {
        InvoicePaymentRequestDto paymentRequestDto = new InvoicePaymentRequestDto();
        paymentRequestDto.setAmount(200.00);

        //invoice not found
        when(invoiceDao.getInvoiceById(any())).thenReturn(Optional.empty());
        assertThrows(InvoiceNotFoundException.class, () -> invoiceServiceImpl.invoicePayment(1L, paymentRequestDto));

        //excess payment
        paymentRequestDto.setAmount(700.00);
        InvoiceEntity invoiceEntity = InvoiceEntity.builder().id(1L).amount(500.00).paidAmount(0.0)
                .dueDate(LocalDate.now().plusDays(5)).status(InvoiceStatusEnum.PENDING).build();
        when(invoiceDao.getInvoiceById(any())).thenReturn(Optional.ofNullable(invoiceEntity));

        assertThrows(InvalidPaymentException.class, () -> invoiceServiceImpl.invoicePayment(1L, paymentRequestDto));
    }

    @Test
    void processPaymentOverdue() {
        InvoicePaymentOverdueRequestDto paymentOverdueRequestDto = new InvoicePaymentOverdueRequestDto();
        paymentOverdueRequestDto.setLateFee(30.00);
        paymentOverdueRequestDto.setOverdueDays(5);

        InvoiceEntity invoiceEntity = InvoiceEntity.builder().id(1L).amount(500.00).paidAmount(0.0)
                .dueDate(LocalDate.now().minusDays(6)).status(InvoiceStatusEnum.PENDING).build();
        InvoiceEntity invoiceEntity1 = InvoiceEntity.builder().id(2L).amount(1000.00).paidAmount(400.0)
                .dueDate(LocalDate.now().minusDays(6)).status(InvoiceStatusEnum.PENDING).build();
        List<InvoiceEntity> invoiceList = List.of(invoiceEntity, invoiceEntity1);

        // paidAmount is zero
        when(invoiceDao.getInvoices()).thenReturn(invoiceList);
        String response = invoiceServiceImpl.processPaymentOverdue(paymentOverdueRequestDto);
        assertNotNull(response);
        assertEquals(MessageConstant.OVERDUE_INVOICES_PROCESSED, response);
    }

    @Test
    void processPaymentOverdueFailure() {
        InvoicePaymentOverdueRequestDto paymentOverdueRequestDto = new InvoicePaymentOverdueRequestDto();
        paymentOverdueRequestDto.setLateFee(30.00);
        paymentOverdueRequestDto.setOverdueDays(5);

        InvoiceEntity invoiceEntity = InvoiceEntity.builder().id(1L).amount(500.00).paidAmount(500.00)
                .dueDate(LocalDate.now().minusDays(6)).status(InvoiceStatusEnum.PAID).build();
        when(invoiceDao.getInvoices()).thenReturn(List.of(invoiceEntity));

        // no overdue invoices
        assertThrows(InvoiceNotFoundException.class, () -> invoiceServiceImpl.processPaymentOverdue(paymentOverdueRequestDto));
    }
}