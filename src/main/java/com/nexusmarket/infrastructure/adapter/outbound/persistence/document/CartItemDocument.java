package com.nexusmarket.infrastructure.adapter.outbound.persistence.document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDocument {

    private String productId;
    private String variantId;
    private int quantity;
}
