package com.nexusmarket.domain.port.outbound;

import com.nexusmarket.domain.model.ReturnRequest;

import java.util.List;
import java.util.Optional;

public interface ReturnRepository {

    ReturnRequest save(ReturnRequest returnRequest);

    Optional<ReturnRequest> findById(String id);

    List<ReturnRequest> findByOrderId(String orderId);
}
