package com.ecommerce.project.service;

import com.ecommerce.project.model.Category;

import java.util.List;

public interface CategoryService {
	List<Category> getAllCategories();

	Category createCategory(Category category);

	Category updateCategory(Category newCategoryData, long id);

	String deleteCategory(long categoryId);
}
