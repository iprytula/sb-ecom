package com.ecommerce.project.repository;

import com.ecommerce.project.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
	@Query("SELECT c FROM Cart c WHERE c.user.id = ?1 AND c.isActive = true")
	Optional<Cart> findActiveCartByUserId(Long id);

	@Query("""
		SELECT c FROM Cart c
		WHERE c.isActive = true AND c.id IN (SELECT ci.cart.id FROM CartItem ci WHERE ci.product.id = ?1)
		""")
	Optional<List<Cart>> findActiveCartsByProductId(Long productId);
}
