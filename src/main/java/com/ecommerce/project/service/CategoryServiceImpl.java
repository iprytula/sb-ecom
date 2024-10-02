package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.APIException;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

	private final CategoryRepository categoryRepository;

	@Autowired
	public CategoryServiceImpl(CategoryRepository categoryRepository) {
		this.categoryRepository = categoryRepository;
	}

	@Override
	public List<Category> getAllCategories() {
		List<Category> categories = categoryRepository.findAll();
		if (categories.isEmpty()) {
			throw new APIException("No Categories created yet");
		}
		return categories;
	}

	@Override
	public Category createCategory(Category category) {
		Category savedCategory = categoryRepository.findByCategoryName(category.getCategoryName());
		if (savedCategory != null) {
			throw new APIException("Category with name: " + savedCategory.getCategoryName() + ", already exists");
		}
		return categoryRepository.save(category);
	}

	@Override
	public Category updateCategory(Category newCategoryData, long categoryId) {
		Category category =
			categoryRepository.findById(categoryId)
				.orElseThrow(
					() -> new ResourceNotFoundException("Category", "categoryId", categoryId)
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
					() -> new ResourceNotFoundException("Category", "categoryId", categoryId)
				);

		categoryRepository.delete(categoryToDelete);
		return "Category with id " + categoryId + " was deleted successfully";
	}
}
