package com.ecommerce.project.service;

import com.ecommerce.project.payload.CategoriesResponse;
import com.ecommerce.project.payload.CategoryDTO;

public interface CategoryService {
	CategoriesResponse getAllCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

	CategoryDTO createCategory(CategoryDTO category);

	CategoryDTO updateCategory(CategoryDTO newCategoryData, long id);

	CategoryDTO deleteCategory(Long categoryId);
}
