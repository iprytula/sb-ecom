package com.ecommerce.project.service;

import model.Category;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryService implements CategoryServiceInterface {

	private final List<Category> categories = new ArrayList<>();

	@Override
	public List<Category> getAllCategories() {
		return categories;
	}

	@Override
	public Category createCategory(Category category) {
		long nextCategoryId = !categories.isEmpty() ? categories.get(categories.size() - 1).getCategoryId() + 1 : 1;

		category.setCategoryId(nextCategoryId);
		categories.add(category);

		return category;
	}

	@Override
	public Category updateCategory(Category newCategoryData, long id) {
		Category category = categories.stream()
			.filter(cat -> cat.getCategoryId().equals(id))
			.findFirst()
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource not found"));

		int index = categories.indexOf(category);
		category.setCategoryName(newCategoryData.getCategoryName());

		categories.set(index, category);

		return category;
	}

	@Override
	public String deleteCategory(long categoryId) {
		Category categoryToDelete = categories.stream()
			.filter(category -> category.getCategoryId().equals(categoryId))
			.findFirst()
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource not found"));

		categories.remove(categoryToDelete);
		return "Category with id " + categoryId + " was deleted successfully";
	}
}
