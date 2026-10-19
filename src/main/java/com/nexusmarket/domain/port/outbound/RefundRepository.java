package com.nexusmarket.domain.port.outbound;

import com.nexusmarket.domain.model.Refund;

import java.util.Optional;

public interface RefundRepository {

    Refund save(Refund refund);

    Optional<Refund> findByReturnId(String returnId);
}
