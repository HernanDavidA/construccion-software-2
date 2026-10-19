package com.nexusmarket.application.service;

import com.nexusmarket.domain.exception.BusinessRuleException;
import com.nexusmarket.domain.exception.ResourceNotFoundException;
import com.nexusmarket.domain.model.Refund;
import com.nexusmarket.domain.model.ReturnRequest;
import com.nexusmarket.domain.model.ReturnStatus;
import com.nexusmarket.domain.model.Role;
import com.nexusmarket.domain.model.User;
import com.nexusmarket.domain.port.inbound.ProcessRefundUseCase;
import com.nexusmarket.domain.port.outbound.InvoiceRepository;
import com.nexusmarket.domain.port.outbound.RefundRepository;
import com.nexusmarket.domain.port.outbound.ReturnRepository;
import com.nexusmarket.domain.service.AccessPolicy;
import org.springframework.stereotype.Service;

@Service
public class ProcessRefundService implements ProcessRefundUseCase {

    private final AccessPolicy accessPolicy;
    private final ReturnRepository returns;
    private final RefundRepository refunds;
    private final InvoiceRepository invoices;

    public ProcessRefundService(
            AccessPolicy accessPolicy,
            ReturnRepository returns,
            RefundRepository refunds,
            InvoiceRepository invoices) {
        this.accessPolicy = accessPolicy;
        this.returns = returns;
        this.refunds = refunds;
        this.invoices = invoices;
    }

    @Override
    public Refund process(User actor, String returnId) {
        accessPolicy.requireRole(actor, Role.ADMINISTRATOR);
        ReturnRequest returnRequest = returns.findById(returnId).orElseThrow(() -> new ResourceNotFoundException("Devolución no encontrada"));
        if (returnRequest.getStatus() != ReturnStatus.APPROVED) {
            throw new BusinessRuleException("Solo se reembolsa una devolución aprobada");
        }
        Refund refund = refunds.findByReturnId(returnRequest.getId()).orElseThrow(() -> new ResourceNotFoundException("Reembolso no encontrado"));
        refund.process();
        var invoice = invoices.findById(refund.getInvoiceId()).orElseThrow(() -> new ResourceNotFoundException("Factura no encontrada"));
        invoice.voidInvoice();
        invoices.save(invoice);
        return refunds.save(refund);
    }
}
