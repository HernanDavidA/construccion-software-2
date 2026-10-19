package com.nexusmarket.infrastructure.adapter.outbound.persistence.document;

import com.nexusmarket.domain.model.Cart;
import com.nexusmarket.domain.model.CartItem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "carts")
public class CartDocument {

    @Id
    private String id;
    private String buyerId;
    private List<CartItemDocument> items;

    public static CartDocument from(Cart cart) {
        List<CartItemDocument> stored = cart.getItems().stream()
                .map(item -> new CartItemDocument(item.productId(), item.variantId(), item.quantity()))
                .toList();
        return new CartDocument(cart.getId(), cart.getBuyerId(), stored);
    }

    public Cart toDomain() {
        List<CartItem> domainItems = items == null
                ? List.of()
                : items.stream().map(item -> new CartItem(item.getProductId(), item.getVariantId(), item.getQuantity())).toList();
        return new Cart(id, buyerId, domainItems);
    }
}
