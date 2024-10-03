package com.ecommerce.project.controller;

import com.ecommerce.project.payload.CategoriesResponse;
import com.ecommerce.project.payload.CategoryDTO;
import com.ecommerce.project.service.CategoryServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public")
public class CategoryController {
	private final CategoryServiceImpl categoryServiceImpl;

	@Autowired
	CategoryController(CategoryServiceImpl categoryServiceImpl) {
		this.categoryServiceImpl = categoryServiceImpl;
	}

	@GetMapping("/categories")
	public ResponseEntity<CategoriesResponse> getCategories() {
		CategoriesResponse categories = categoryServiceImpl.getAllCategories();
		return new ResponseEntity<>(categories, HttpStatus.OK);
	}

	@PostMapping("/categories")
	public ResponseEntity<CategoryDTO> createCategory(@Valid @RequestBody CategoryDTO category) {
		return new ResponseEntity<>(categoryServiceImpl.createCategory(category), HttpStatus.CREATED);
	}

	@PutMapping("/categories/{id}")
	public ResponseEntity<CategoryDTO> updateCategory(
		@RequestBody CategoryDTO category, @PathVariable int id
	) {
		return new ResponseEntity<>(
			categoryServiceImpl.updateCategory(category, id),
			HttpStatus.OK
		);
	}

	@DeleteMapping("/categories/{categoryId}")
	public ResponseEntity<CategoryDTO> deleteCategory(@PathVariable long categoryId) {
		return new ResponseEntity<>(
			categoryServiceImpl.deleteCategory(categoryId),
			HttpStatus.OK
		);
	}
}
