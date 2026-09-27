package com.nexusmarket.application.logistics.port.in;

import com.nexusmarket.domain.logistics.ReturnRequest;

public interface RequestReturnUseCase {

    ReturnRequest execute(String actorId, String returnId, String orderId, String reason);
}
