package com.nexusmarket.domain.port.inbound;

import com.nexusmarket.domain.model.Refund;
import com.nexusmarket.domain.model.User;

public interface ProcessRefundUseCase {
    Refund process(User actor, String returnId);
}
