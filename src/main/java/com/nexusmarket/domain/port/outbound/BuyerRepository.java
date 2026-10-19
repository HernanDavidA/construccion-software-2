package com.nexusmarket.domain.port.outbound;

import com.nexusmarket.domain.model.Buyer;

import java.util.List;
import java.util.Optional;

public interface BuyerRepository {

    Buyer save(Buyer buyer);

    Optional<Buyer> findById(String id);

    Optional<Buyer> findByUserId(String userId);

    List<Buyer> findAll();
}
