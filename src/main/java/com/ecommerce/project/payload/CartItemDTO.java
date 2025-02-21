package com.ecommerce.project.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItemDTO {
	private Long id;
	private CartDTO cart;
	private ProductDTO product;
	private Integer quantity;
	private Double discount;
	private Double productPrice;
}
