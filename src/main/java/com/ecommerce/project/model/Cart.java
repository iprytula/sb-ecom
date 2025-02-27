package com.ecommerce.project.model;

import com.ecommerce.project.exceptions.ResourceNotFoundException;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

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

	private Boolean isActive = true;

	public void addCartItem(CartItem cartItem) {
		cartItems.add(cartItem);
		totalPrice += cartItem.getPrice() * cartItem.getQuantity();
	}

	public void setCartItem(CartItem cartItem) {
		CartItem cartItemToUpdate = cartItems.stream().filter(ci -> ci.getId().equals(cartItem.getId())).findFirst()
			.orElseThrow(() -> new ResourceNotFoundException("CartItem", "id", cartItem.getId()));
		int cartItemIndex = cartItems.indexOf(cartItemToUpdate);

		cartItemToUpdate.setQuantity(cartItem.getQuantity());
		cartItemToUpdate.setPrice(cartItem.getPrice());

		cartItems.set(cartItemIndex, cartItemToUpdate);

		totalPrice = cartItems.stream().mapToDouble(ci -> ci.getPrice() * ci.getQuantity()).sum();
	}
}
