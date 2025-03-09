package com.ecommerce.project.service;

import com.ecommerce.project.payload.CartAdminDTO;
import com.ecommerce.project.payload.CartDTO;
import com.ecommerce.project.payload.PageableResponse;

public interface CartService {
	CartDTO addProductToCart(Long productId, Integer quantity);

	PageableResponse<CartAdminDTO> getAllCarts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

	CartDTO getLoggedInUserCart();

	CartDTO updateCartItemQuantity(Long productId, Integer quantity);

	CartDTO deleteProductFromCart(Long productId);
}
