package com.nexusmarket.domain.port.outbound;

import com.nexusmarket.domain.model.Seller;

import java.util.List;
import java.util.Optional;

public interface SellerRepository {

    Seller save(Seller seller);

    Optional<Seller> findById(String id);

    List<Seller> findAll();
}
