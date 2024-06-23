package com.system.invoicesystem.dao;

import com.system.invoicesystem.entity.InvoiceEntity;
import com.system.invoicesystem.repository.InvoiceRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class InvoiceDao {

    private final InvoiceRepository invoiceRepository;

    public InvoiceEntity saveInvoice(InvoiceEntity invoiceEntity){
        return invoiceRepository.save(invoiceEntity);
    }

    public List<InvoiceEntity> getInvoices(){
        return invoiceRepository.findAll();
    }

    public Optional<InvoiceEntity> getInvoiceById(Long id){
        return invoiceRepository.findById(id);
    }
}
