package com.nexusmarket.domain.port.inbound;

import com.nexusmarket.domain.model.ReturnRequest;
import com.nexusmarket.domain.model.User;

import java.util.List;

public interface QueryReturnsUseCase {
    List<ReturnRequest> listByOrder(User actor, String orderId);
}
