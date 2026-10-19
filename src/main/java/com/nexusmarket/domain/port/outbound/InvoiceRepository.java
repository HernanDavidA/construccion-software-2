package com.nexusmarket.domain.port.outbound;

import com.nexusmarket.domain.model.Invoice;

import java.util.List;
import java.util.Optional;

public interface InvoiceRepository {

    Invoice save(Invoice invoice);

    Optional<Invoice> findById(String id);

    Optional<Invoice> findByOrderId(String orderId);

    List<Invoice> findAll();
}
