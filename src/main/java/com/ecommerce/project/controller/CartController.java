package com.ecommerce.project.controller;

import com.ecommerce.project.payload.CartDTO;
import com.ecommerce.project.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class CartController {
	private final CartService cartService;

	@Autowired
	CartController(CartService cartService) {
		this.cartService = cartService;
	}

	@PostMapping("/carts/products/{productId}/quantity/{quantity}")
	private ResponseEntity<CartDTO> addProductToCart(@PathVariable Long productId, @PathVariable Integer quantity) {
		return new ResponseEntity<>(
			cartService.addProductToCart(productId, quantity),
			HttpStatus.CREATED
		);
	}
}
