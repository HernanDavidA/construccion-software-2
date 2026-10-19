package com.nexusmarket.infrastructure.adapter.outbound.persistence;

import com.nexusmarket.domain.model.Refund;
import com.nexusmarket.domain.port.outbound.RefundRepository;
import com.nexusmarket.infrastructure.adapter.outbound.persistence.document.RefundDocument;
import com.nexusmarket.infrastructure.adapter.outbound.persistence.repository.RefundMongoRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class RefundRepositoryAdapter implements RefundRepository {

    private final RefundMongoRepository mongo;

    public RefundRepositoryAdapter(RefundMongoRepository mongo) {
        this.mongo = mongo;
    }

    @Override
    public Refund save(Refund refund) {
        return mongo.save(RefundDocument.from(refund)).toDomain();
    }

    @Override
    public Optional<Refund> findByReturnId(String returnId) {
        return mongo.findByReturnId(returnId).map(RefundDocument::toDomain);
    }
}
