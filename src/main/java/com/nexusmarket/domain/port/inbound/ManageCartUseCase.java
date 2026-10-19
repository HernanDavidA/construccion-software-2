package com.nexusmarket.domain.port.inbound;

import com.nexusmarket.domain.command.AddCartItem;
import com.nexusmarket.domain.model.Cart;
import com.nexusmarket.domain.model.User;

public interface ManageCartUseCase {
    Cart view(User actor);

    Cart addItem(User actor, AddCartItem command);

    Cart removeItem(User actor, String productId, String variantId);

    Cart clear(User actor);
}
