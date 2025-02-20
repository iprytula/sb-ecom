package com.ecommerce.project.service;

import com.ecommerce.project.payload.CartDTO;
import com.ecommerce.project.payload.CartsResponse;

import java.util.List;

public interface CartService {
	CartDTO addProductToCart(Long productId, Integer quantity);

	CartsResponse getAllCarts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);
}
