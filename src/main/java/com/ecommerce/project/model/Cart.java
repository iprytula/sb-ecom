package com.ecommerce.project.model;

import com.ecommerce.project.exceptions.ResourceNotFoundException;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity
@Data
@Table(name = "carts")
@NoArgsConstructor
@AllArgsConstructor
public class Cart {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne
	@JoinColumn(name = "user_id")
	private User user;

	@OneToMany(mappedBy = "cart", cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE }, orphanRemoval = true)
	private List<CartItem> cartItems = new ArrayList<>();

	private Double totalPrice = 0.0;

	public void addCartItem(CartItem cartItem) {
		Optional<CartItem> existingCartItem = cartItems.stream()
			.filter(ci -> ci.getProduct().getId().equals(cartItem.getProduct().getId()))
			.findFirst();

		if (existingCartItem.isPresent()) {
			existingCartItem.get().setQuantity(existingCartItem.get().getQuantity() + cartItem.getQuantity());
		} else {
			cartItems.add(cartItem);
		}

		recalculateTotalPrice();
	}

	public void updateCartItem(CartItem cartItem) {
		CartItem cartItemToUpdate = cartItems.stream()
			.filter(ci -> ci.getId().equals(cartItem.getId()))
			.findFirst()
			.orElseThrow(() -> new ResourceNotFoundException("CartItem", "id", cartItem.getId()));

		cartItemToUpdate.setQuantity(cartItem.getQuantity());

		recalculateTotalPrice();
	}

	public void deleteCartItem(CartItem cartItem) {
		CartItem cartItemToDelete = cartItems.stream()
			.filter(ci -> ci.getProduct().getId().equals(cartItem.getProduct().getId()))
			.findFirst()
			.orElseThrow(() -> new ResourceNotFoundException("CartItem", "id", cartItem.getId()));

		cartItems.remove(cartItemToDelete);

		recalculateTotalPrice();
	}

	private void recalculateTotalPrice() {
		totalPrice = cartItems.stream()
			.mapToDouble(ci -> ci.getProduct().getPrice() * ci.getQuantity())
			.sum();
	}

	@Override
	public String toString() {
		return "Cart{" +
			"id=" + id +
			", user=" + user +
			", cartItems=" + cartItems +
			", totalPrice=" + totalPrice +
			'}';
	}

}
