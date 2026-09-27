package com.nexusmarket.adapter.out.persistence.memory;

import com.nexusmarket.application.logistics.port.out.ReturnRequestRepository;
import com.nexusmarket.domain.logistics.ReturnRequest;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryReturnRequestRepository implements ReturnRequestRepository {

    private final Map<String, ReturnRequest> returns = new LinkedHashMap<>();

    @Override
    public void save(ReturnRequest returnRequest) {
        returns.put(returnRequest.getId(), returnRequest);
    }

    @Override
    public Optional<ReturnRequest> findById(String id) {
        return Optional.ofNullable(returns.get(id));
    }
}
