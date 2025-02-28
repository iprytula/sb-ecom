package com.ecommerce.project.controller;

import com.ecommerce.project.config.AppConstants;
import com.ecommerce.project.payload.CategoriesResponse;
import com.ecommerce.project.payload.CategoryDTO;
import com.ecommerce.project.service.CategoryServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class CategoryController {
	private final CategoryServiceImpl categoryServiceImpl;

	@Autowired
	CategoryController(CategoryServiceImpl categoryServiceImpl) {
		this.categoryServiceImpl = categoryServiceImpl;
	}

	@GetMapping("/public/categories")
	public ResponseEntity<CategoriesResponse> getCategories(
		@RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER) Integer pageNumber,
		@RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE) Integer pageSize,
		@RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_CATEGORIES_BY) String sortBy,
		@RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_CATEGORIES_ORDER) String sortOrder
	) {
		return new ResponseEntity<>(
			categoryServiceImpl.getAllCategories(pageNumber, pageSize, sortBy, sortOrder),
			HttpStatus.OK
		);
	}

	@PostMapping("/admin/categories")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<CategoryDTO> createCategory(@Valid @RequestBody CategoryDTO category) {
		return new ResponseEntity<>(
			categoryServiceImpl.createCategory(category),
			HttpStatus.CREATED
		);
	}

	@PutMapping("/admin/categories/{id}")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<CategoryDTO> updateCategory(
		@RequestBody CategoryDTO category, @PathVariable int id
	) {
		return new ResponseEntity<>(
			categoryServiceImpl.updateCategory(category, id),
			HttpStatus.OK
		);
	}

	@DeleteMapping("/admin/categories/{categoryId}")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<CategoryDTO> deleteCategory(@PathVariable long categoryId) {
		return new ResponseEntity<>(
			categoryServiceImpl.deleteCategory(categoryId),
			HttpStatus.OK
		);
	}
}
