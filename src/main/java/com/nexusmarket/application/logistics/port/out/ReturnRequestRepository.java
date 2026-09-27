package com.nexusmarket.application.logistics.port.out;

import com.nexusmarket.domain.logistics.ReturnRequest;

import java.util.Optional;

public interface ReturnRequestRepository {

    void save(ReturnRequest returnRequest);

    Optional<ReturnRequest> findById(String id);
}
