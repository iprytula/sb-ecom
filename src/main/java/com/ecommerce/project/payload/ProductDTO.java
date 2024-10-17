package com.ecommerce.project.payload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.groups.Default;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
	private Long id;
	@NotBlank(message = "Product name couldn't be blank")
	@Size(min = 2, max = 50, message = "Product name must be between 2 and 50 characters")
	private String name;
	private String description;
	private Double price;
	private Integer quantity;
	private Double specialPrice;
	private Double discountPrice;
	private String image;
	@NotNull(message = "categoryId is required", groups = Default.class)
	private Long categoryId;
}
