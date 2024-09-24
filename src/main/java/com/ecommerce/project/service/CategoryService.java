package com.ecommerce.project.service;

import model.Category;
import org.springframework.stereotype.Service;

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
		categories.add(category);
		return category;
	}
}
