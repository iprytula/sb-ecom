package com.ecommerce.project.repository;

import com.ecommerce.project.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

	Optional<Cart> findCartByUserId(Long id);

	@Query("""
		SELECT c FROM Cart c
		WHERE c.id
		IN (SELECT ci.cart.id FROM CartItem ci WHERE ci.product.id = ?1)
		""")
	Optional<List<Cart>> findCartsByProductId(Long productId);
}
