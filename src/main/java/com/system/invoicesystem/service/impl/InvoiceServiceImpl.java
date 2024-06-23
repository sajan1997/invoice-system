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
import com.system.invoicesystem.service.InterfaceInvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class InvoiceServiceImpl implements InterfaceInvoiceService {

    private final InvoiceDao invoiceDao;

    @Override
    public InvoiceCreateResponseDto createInvoice(InvoiceRequestDto invoiceRequestDto) {
        InvoiceEntity savedInvoice = saveInvoiceDetail(invoiceRequestDto.getAmount(), invoiceRequestDto.getDueDate());

        return InvoiceCreateResponseDto.builder().id(savedInvoice.getId()).build();
    }

    private InvoiceEntity saveInvoiceDetail(Double amount, LocalDate dueDate) {
        InvoiceEntity invoiceEntity = InvoiceEntity.builder().amount(amount)
                .dueDate(dueDate).paidAmount(0.0).status(InvoiceStatusEnum.PENDING).build();
        return invoiceDao.saveInvoice(invoiceEntity);
    }

    @Override
    public List<InvoiceResponseDto> invoices() {
        return invoiceDao.getInvoices()
                .stream()
                .map(this::mapInvoiceToResponseDto).toList();
    }

    private InvoiceResponseDto mapInvoiceToResponseDto(InvoiceEntity invoiceEntity) {
        return InvoiceResponseDto.builder().id(invoiceEntity.getId()).amount(invoiceEntity.getAmount())
                .paidAmount(invoiceEntity.getPaidAmount()).dueDate(invoiceEntity.getDueDate()).status(invoiceEntity.getStatus().getValue()).build();
    }

    @Override
    public InvoiceResponseDto invoicePayment(Long id, InvoicePaymentRequestDto paymentRequestDto) {

        InvoiceEntity invoiceEntity = invoiceDao.getInvoiceById(id)
                .orElseThrow(() -> new InvoiceNotFoundException(MessageConstant.INVALID_INVOICE));
        Double totalPaidAmount = invoiceEntity.getPaidAmount() + paymentRequestDto.getAmount();

        // valid payAmount matches remaining amount to be paid
        validatePaymentAmount(invoiceEntity, totalPaidAmount);

        return mapInvoiceToResponseDto(updateInvoicePayment(invoiceEntity, totalPaidAmount));
    }

    private void validatePaymentAmount(InvoiceEntity invoiceEntity, Double totalPaidAmount) {
        if (totalPaidAmount > invoiceEntity.getAmount()) {
            Double dueAmount = invoiceEntity.getAmount() - invoiceEntity.getPaidAmount();
            throw new InvalidPaymentException(MessageConstant.INVALID_AMOUNT_YOUR_DUE_AMOUNT + dueAmount);
        }
    }

    private InvoiceEntity updateInvoicePayment(InvoiceEntity invoiceEntity, Double totalPaidAmount) {
        invoiceEntity.setPaidAmount(totalPaidAmount);
        if (totalPaidAmount.equals(invoiceEntity.getAmount())) {
            invoiceEntity.setStatus(InvoiceStatusEnum.PAID);
        }
        return invoiceDao.saveInvoice(invoiceEntity);
    }

    @Override
    public String processPaymentOverdue(InvoicePaymentOverdueRequestDto paymentOverdueRequestDto) {

        // filter invoice status pending and dueDate which is overdue
        List<InvoiceEntity> invoiceList = Optional.ofNullable(getPaymentDueInvoiceList(paymentOverdueRequestDto))
                .filter(invoices -> !CollectionUtils.isEmpty(invoices))
                .orElseThrow(() -> new InvoiceNotFoundException("There are no overdue invoices"));

        // process invoice, if amount paid is partial set invoice status paid else void and
        // create new invoice adding lateFee and extend dueDate
        processOverDueInvoice(paymentOverdueRequestDto.getLateFee(), paymentOverdueRequestDto.getOverdueDays(), invoiceList);

        return MessageConstant.OVERDUE_INVOICES_PROCESSED;
    }

    private List<InvoiceEntity> getPaymentDueInvoiceList(InvoicePaymentOverdueRequestDto paymentOverdueRequestDto) {
        return invoiceDao.getInvoices().stream()
                .filter(invoice -> InvoiceStatusEnum.PENDING.equals(invoice.getStatus()) &&
                        invoice.getDueDate().isBefore(LocalDate.now().minusDays(paymentOverdueRequestDto.getOverdueDays())))
                .toList();
    }

    private void processOverDueInvoice(Double lateFee, Integer overdueDays, List<InvoiceEntity> invoiceList) {
        invoiceList.forEach(invoice -> {
            if (invoice.getPaidAmount() > 0 && invoice.getPaidAmount() < invoice.getAmount()) {
                Double remainingAmount = invoice.getAmount() - invoice.getPaidAmount();
                invoice.setStatus(InvoiceStatusEnum.PAID);
                saveInvoiceDetail(remainingAmount + lateFee,
                        LocalDate.now().plusDays(overdueDays));
            } else {
                invoice.setStatus(InvoiceStatusEnum.VOID);
                saveInvoiceDetail(invoice.getAmount() + lateFee,
                        LocalDate.now().plusDays(overdueDays));
            }
            invoiceDao.saveInvoice(invoice);
        });
    }

}
