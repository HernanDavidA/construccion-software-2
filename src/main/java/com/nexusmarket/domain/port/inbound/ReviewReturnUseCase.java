package com.nexusmarket.domain.port.inbound;

import com.nexusmarket.domain.model.ReturnRequest;
import com.nexusmarket.domain.model.User;

public interface ReviewReturnUseCase {
    ReturnRequest approve(User actor, String returnId);

    ReturnRequest reject(User actor, String returnId);
}
