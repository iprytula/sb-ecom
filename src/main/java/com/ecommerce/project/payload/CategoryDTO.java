package com.ecommerce.project.payload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {
	private Long categoryId;

	@NotBlank(message = "Category name is mandatory")
	@Size(min = 2, max = 25, message = "Category name must be between 2 and 25 characters")
	private String name;

	private List<ProductDTO> products;
}
