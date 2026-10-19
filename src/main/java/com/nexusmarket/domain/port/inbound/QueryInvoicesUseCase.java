package com.nexusmarket.domain.port.inbound;

import com.nexusmarket.domain.model.Invoice;
import com.nexusmarket.domain.model.User;

import java.util.List;

public interface QueryInvoicesUseCase {
    Invoice getByOrder(User actor, String orderId);

    List<Invoice> list(User actor);
}
