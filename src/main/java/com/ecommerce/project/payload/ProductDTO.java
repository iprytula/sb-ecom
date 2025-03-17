package com.ecommerce.project.payload;

import com.ecommerce.project.validation.AddProductValidationGroup;
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

	@NotBlank(message = "Product name couldn't be blank", groups = { AddProductValidationGroup.class, Default.class })
	@Size(min = 2, max = 50, message = "Product name must be between 2 and 50 characters")
	private String name;

	@Size(min = 20, message = "Product description should contain at least 20 characters")
	private String description;
	private Double standardPrice;
	private Double specialPrice;
	private Integer quantity;
	private String image;

	@NotNull(message = "categoryId is required", groups = Default.class)
	private Long categoryId;
}
