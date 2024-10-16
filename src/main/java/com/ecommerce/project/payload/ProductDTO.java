package com.ecommerce.project.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
	private Long id;
	private String name;
	private String description;
	private Double price;
	private Integer quantity;
	private Double specialPrice;
	private Double discountPrice;
	private String image;
	private Long categoryId;
}
