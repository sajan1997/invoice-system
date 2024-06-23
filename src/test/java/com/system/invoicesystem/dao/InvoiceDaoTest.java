package com.system.invoicesystem.dao;

import com.system.invoicesystem.constant.enums.InvoiceStatusEnum;
import com.system.invoicesystem.entity.InvoiceEntity;
import com.system.invoicesystem.repository.InvoiceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class InvoiceDaoTest {

    @InjectMocks
    private InvoiceDao invoiceDao;

    @Mock
    private InvoiceRepository invoiceRepository;

    @Test
    void saveInvoice() {
        InvoiceEntity invoiceEntity = InvoiceEntity.builder().amount(500.00).paidAmount(0.0)
                .dueDate(LocalDate.now().plusDays(5)).status(InvoiceStatusEnum.PENDING).build();

        when(invoiceRepository.save(any())).thenReturn(invoiceEntity);

        InvoiceEntity response = invoiceDao.saveInvoice(invoiceEntity);
        assertNotNull(response);
        assertEquals(invoiceEntity, response);
    }

    @Test
    void getInvoices() {
        InvoiceEntity invoiceEntity = InvoiceEntity.builder().id(1L).amount(500.00).paidAmount(0.0)
                .dueDate(LocalDate.now().plusDays(5)).status(InvoiceStatusEnum.PENDING).build();

        List<InvoiceEntity> invoiceList = List.of(invoiceEntity);
        when(invoiceRepository.findAll()).thenReturn(invoiceList);

        List<InvoiceEntity> response = invoiceDao.getInvoices();
        assertNotNull(response);
        assertEquals(invoiceList, response);
    }

    @Test
    void getInvoiceById() {
        InvoiceEntity invoiceEntity = InvoiceEntity.builder().id(1L).amount(500.00).paidAmount(0.0)
                .dueDate(LocalDate.now().plusDays(5)).status(InvoiceStatusEnum.PENDING).build();

        when(invoiceRepository.findById(any())).thenReturn(Optional.ofNullable(invoiceEntity));

        Optional<InvoiceEntity> response = invoiceDao.getInvoiceById(1L);
        assertNotNull(response);
        assertTrue(response.isPresent());
        assertEquals(invoiceEntity, response.get());
    }

}