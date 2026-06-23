package com.ensas.billing_service.repositories;

import com.ensas.billing_service.entities.Cart;
import com.ensas.billing_service.entities.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByCartAndProductId(Cart cart, Long productId);
}
