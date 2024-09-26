package com.ecommerce.project.service;

import com.ecommerce.project.model.Category;
import com.ecommerce.project.repository.CategoryRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

	private final CategoryRepository categoryRepository;

	public CategoryServiceImpl(CategoryRepository categoryRepository) {
		this.categoryRepository = categoryRepository;
	}

	@Override
	public List<Category> getAllCategories() {
		return categoryRepository.findAll();
	}

	@Override
	public Category createCategory(Category category) {
		return categoryRepository.save(category);
	}

	@Override
	public Category updateCategory(Category newCategoryData, long categoryId) {
		Category category =
			categoryRepository.findById(categoryId)
				.orElseThrow(
					() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource not found")
				);

		category.setCategoryName(newCategoryData.getCategoryName());
		categoryRepository.save(category);

		return category;
	}

	@Override
	public String deleteCategory(long categoryId) {
		Category categoryToDelete =
			categoryRepository.findById(categoryId)
				.orElseThrow(
					() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource not found")
				);

		categoryRepository.delete(categoryToDelete);
		return "Category with id " + categoryId + " was deleted successfully";
	}
}
