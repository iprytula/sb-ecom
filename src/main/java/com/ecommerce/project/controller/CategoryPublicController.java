package com.ecommerce.project.controller;

import com.ecommerce.project.service.CategoryService;
import model.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/public")
public class CategoryPublicController {
	private final CategoryService categoryService;

	@Autowired
	CategoryPublicController(CategoryService categoryService) {
		this.categoryService = categoryService;
	}

	@GetMapping("/categories")
	public ResponseEntity<List<Category>> getCategories() {
		List<Category> categories = categoryService.getAllCategories();
		return new ResponseEntity<>(categories, HttpStatus.OK);
	}

	@PostMapping("/categories")
	public ResponseEntity<Category> createCategory(@RequestBody Category category) {
		Category categoryCreated = categoryService.createCategory(category);
		return new ResponseEntity<>(categoryCreated, HttpStatus.CREATED);
	}

	@PutMapping("/categories/{id}")
	public ResponseEntity<Category> updateCategory(
		@RequestBody Category category, @PathVariable int id
	) {
		try {
			Category updatedCategory = categoryService.updateCategory(category, id);
			return new ResponseEntity<>(updatedCategory, HttpStatus.OK);
		} catch (ResponseStatusException e) {
			return new ResponseEntity<>(category, HttpStatus.NOT_FOUND);
		}
	}

	@DeleteMapping("/categories/{categoryId}")
	public ResponseEntity<String> deleteCategory(@PathVariable long categoryId) {
		try {
			String status = categoryService.deleteCategory(categoryId);
			return new ResponseEntity<>(status, HttpStatus.OK);
		} catch (ResponseStatusException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
		}
	}
}
