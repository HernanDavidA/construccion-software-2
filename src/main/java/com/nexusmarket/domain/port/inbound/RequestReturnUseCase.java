package com.nexusmarket.domain.port.inbound;

import com.nexusmarket.domain.command.RequestReturn;
import com.nexusmarket.domain.model.ReturnRequest;
import com.nexusmarket.domain.model.User;

public interface RequestReturnUseCase {
    ReturnRequest request(User actor, RequestReturn command);
}
