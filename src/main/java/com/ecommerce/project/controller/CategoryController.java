package com.ecommerce.project.controller;

import com.ecommerce.project.service.CategoryService;
import model.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CategoryController {
	private final CategoryService categoryService;

	@Autowired
	CategoryController(CategoryService categoryService) {
		this.categoryService = categoryService;
	}

	@GetMapping("/api/public/categories")
	public List<Category> getCategories() {
		return categoryService.getAllCategories();
	}

	@PostMapping("/api/public/categories")
	public Category createCategory(@RequestBody Category category) {
		return categoryService.createCategory(category);
	}
}