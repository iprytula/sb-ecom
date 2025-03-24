package com.ecommerce.project.repository;

import com.ecommerce.project.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

	Optional<CartItem> findCartItemByCartIdAndProductId(Long cartId, Long productId);

	@Modifying
	@Query("DELETE FROM CartItem ci WHERE ci.cart.id = ?1 AND ci.product.id = ?2")
	void deleteCartItemByCartIdAndProductId(Long cartId, Long productId);

}
