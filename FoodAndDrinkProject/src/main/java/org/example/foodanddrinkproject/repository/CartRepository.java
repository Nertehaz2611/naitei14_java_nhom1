package org.example.foodanddrinkproject.repository;

import org.example.foodanddrinkproject.entity.Cart;
import org.example.foodanddrinkproject.enums.CartStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    // Important: find the cart that is specifically ACTIVE
    Optional<Cart> findByUserIdAndStatus(Long userId, CartStatus status);
}