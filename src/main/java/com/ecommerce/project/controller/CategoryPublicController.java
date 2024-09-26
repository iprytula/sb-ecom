package com.ecommerce.project.controller;

import com.ecommerce.project.service.CategoryServiceImpl;
import com.ecommerce.project.model.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/public")
public class CategoryPublicController {
	private final CategoryServiceImpl categoryServiceImpl;

	@Autowired
	CategoryPublicController(CategoryServiceImpl categoryServiceImpl) {
		this.categoryServiceImpl = categoryServiceImpl;
	}

	@GetMapping("/categories")
	public ResponseEntity<List<Category>> getCategories() {
		List<Category> categories = categoryServiceImpl.getAllCategories();
		return new ResponseEntity<>(categories, HttpStatus.OK);
	}

	@PostMapping("/categories")
	public ResponseEntity<Category> createCategory(@RequestBody Category category) {
		Category categoryCreated = categoryServiceImpl.createCategory(category);
		return new ResponseEntity<>(categoryCreated, HttpStatus.CREATED);
	}

	@PutMapping("/categories/{id}")
	public ResponseEntity<Category> updateCategory(
		@RequestBody Category category, @PathVariable int id
	) {
		try {
			Category updatedCategory = categoryServiceImpl.updateCategory(category, id);
			return new ResponseEntity<>(updatedCategory, HttpStatus.OK);
		} catch (ResponseStatusException e) {
			return new ResponseEntity<>(category, HttpStatus.NOT_FOUND);
		}
	}

	@DeleteMapping("/categories/{categoryId}")
	public ResponseEntity<String> deleteCategory(@PathVariable long categoryId) {
		try {
			String status = categoryServiceImpl.deleteCategory(categoryId);
			return new ResponseEntity<>(status, HttpStatus.OK);
		} catch (ResponseStatusException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
		}
	}
}
