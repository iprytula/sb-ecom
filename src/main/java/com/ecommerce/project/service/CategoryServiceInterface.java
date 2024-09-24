package com.ecommerce.project.service;

import model.Category;

import java.util.List;

public interface CategoryServiceInterface {
	List<Category> getAllCategories();

	Category createCategory(Category category);

	Category updateCategory(Category newCategoryData, long id);

	String deleteCategory(long categoryId);
}
