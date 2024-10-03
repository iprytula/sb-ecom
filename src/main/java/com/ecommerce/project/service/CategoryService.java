package com.ecommerce.project.service;

import com.ecommerce.project.payload.CategoriesResponse;
import com.ecommerce.project.payload.CategoryDTO;

public interface CategoryService {
	CategoriesResponse getAllCategories();

	CategoryDTO createCategory(CategoryDTO category);

	CategoryDTO updateCategory(CategoryDTO newCategoryData, long id);

	CategoryDTO deleteCategory(long categoryId);
}
