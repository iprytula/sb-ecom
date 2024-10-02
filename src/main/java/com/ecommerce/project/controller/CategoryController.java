package com.ecommerce.project.controller;

import com.ecommerce.project.service.CategoryServiceImpl;
import com.ecommerce.project.model.Category;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public")
public class CategoryController {
	private final CategoryServiceImpl categoryServiceImpl;

	@Autowired
	CategoryController(CategoryServiceImpl categoryServiceImpl) {
		this.categoryServiceImpl = categoryServiceImpl;
	}

	@GetMapping("/categories")
	public ResponseEntity<List<Category>> getCategories() {
		List<Category> categories = categoryServiceImpl.getAllCategories();
		return new ResponseEntity<>(categories, HttpStatus.OK);
	}

	@PostMapping("/categories")
	public ResponseEntity<Category> createCategory(@Valid @RequestBody Category category) {
		Category categoryCreated = categoryServiceImpl.createCategory(category);
		return new ResponseEntity<>(categoryCreated, HttpStatus.CREATED);
	}

	@PutMapping("/categories/{id}")
	public ResponseEntity<Category> updateCategory(
		@RequestBody Category category, @PathVariable int id
	) {
		return new ResponseEntity<>(
			categoryServiceImpl.updateCategory(category, id),
			HttpStatus.OK
		);
	}

	@DeleteMapping("/categories/{categoryId}")
	public ResponseEntity<String> deleteCategory(@PathVariable long categoryId) {
		return new ResponseEntity<>(
			categoryServiceImpl.deleteCategory(categoryId),
			HttpStatus.OK
		);
	}
}
