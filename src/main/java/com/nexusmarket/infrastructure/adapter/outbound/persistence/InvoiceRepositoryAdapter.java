package com.nexusmarket.infrastructure.adapter.outbound.persistence;

import com.nexusmarket.domain.model.Invoice;
import com.nexusmarket.domain.port.outbound.InvoiceRepository;
import com.nexusmarket.infrastructure.adapter.outbound.persistence.document.InvoiceDocument;
import com.nexusmarket.infrastructure.adapter.outbound.persistence.repository.InvoiceMongoRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class InvoiceRepositoryAdapter implements InvoiceRepository {

    private final InvoiceMongoRepository mongo;

    public InvoiceRepositoryAdapter(InvoiceMongoRepository mongo) {
        this.mongo = mongo;
    }

    @Override
    public Invoice save(Invoice invoice) {
        return mongo.save(InvoiceDocument.from(invoice)).toDomain();
    }

    @Override
    public Optional<Invoice> findById(String id) {
        return mongo.findById(id).map(InvoiceDocument::toDomain);
    }

    @Override
    public Optional<Invoice> findByOrderId(String orderId) {
        return mongo.findByOrderId(orderId).map(InvoiceDocument::toDomain);
    }

    @Override
    public List<Invoice> findAll() {
        return mongo.findAll().stream().map(InvoiceDocument::toDomain).toList();
    }
}
