package com.ecommerce.project.service;

import com.ecommerce.project.payload.CategoryDTO;
import com.ecommerce.project.payload.PageableResponse;

public interface CategoryService {
	PageableResponse<CategoryDTO> getAllCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

	CategoryDTO createCategory(CategoryDTO category);

	CategoryDTO updateCategory(CategoryDTO newCategoryData, long id);

	CategoryDTO deleteCategory(Long categoryId);
}
