package com.nexusmarket.infrastructure.adapter.outbound.persistence;

import com.nexusmarket.domain.model.ReturnRequest;
import com.nexusmarket.domain.port.outbound.ReturnRepository;
import com.nexusmarket.infrastructure.adapter.outbound.persistence.document.ReturnRequestDocument;
import com.nexusmarket.infrastructure.adapter.outbound.persistence.repository.ReturnMongoRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ReturnRepositoryAdapter implements ReturnRepository {

    private final ReturnMongoRepository mongo;

    public ReturnRepositoryAdapter(ReturnMongoRepository mongo) {
        this.mongo = mongo;
    }

    @Override
    public ReturnRequest save(ReturnRequest returnRequest) {
        return mongo.save(ReturnRequestDocument.from(returnRequest)).toDomain();
    }

    @Override
    public Optional<ReturnRequest> findById(String id) {
        return mongo.findById(id).map(ReturnRequestDocument::toDomain);
    }

    @Override
    public List<ReturnRequest> findByOrderId(String orderId) {
        return mongo.findByOrderId(orderId).stream().map(ReturnRequestDocument::toDomain).toList();
    }
}
