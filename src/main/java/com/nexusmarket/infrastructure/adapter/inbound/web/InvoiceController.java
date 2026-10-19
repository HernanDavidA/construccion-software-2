package com.nexusmarket.infrastructure.adapter.inbound.web;

import com.nexusmarket.domain.model.Invoice;
import com.nexusmarket.domain.port.inbound.QueryInvoicesUseCase;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    private final QueryInvoicesUseCase invoices;
    private final CurrentUser currentUser;

    public InvoiceController(QueryInvoicesUseCase invoices, CurrentUser currentUser) {
        this.invoices = invoices;
        this.currentUser = currentUser;
    }

    @GetMapping
    List<Invoice> list(HttpServletRequest request) {
        return invoices.list(currentUser.require(request));
    }

    @GetMapping("/order/{orderId}")
    Invoice byOrder(HttpServletRequest request, @PathVariable String orderId) {
        return invoices.getByOrder(currentUser.require(request), orderId);
    }
}
