package com.ecommerce.project.controller;

import com.ecommerce.project.config.AppConstants;
import com.ecommerce.project.payload.CartDTO;
import com.ecommerce.project.payload.CartsResponse;
import com.ecommerce.project.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

	@GetMapping("/admin/carts")
	private ResponseEntity<CartsResponse> getAllCarts(
		@RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER) Integer pageNumber,
		@RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE) Integer pageSize,
		@RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_CARTS_BY) String sortBy,
		@RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_CARTS_ORDER) String sortOrder
	) {
		return new ResponseEntity<>(
			cartService.getAllCarts(pageNumber, pageSize, sortBy, sortOrder),
			HttpStatus.OK
		);
	}
}
