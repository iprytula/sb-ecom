package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.APIException;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.payload.CategoriesResponse;
import com.ecommerce.project.payload.CategoryDTO;
import com.ecommerce.project.repository.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

	private final CategoryRepository categoryRepository;
	private final ModelMapper modelMapper;

	@Autowired
	public CategoryServiceImpl(
		CategoryRepository categoryRepository,
		ModelMapper modelMapper
	) {
		this.categoryRepository = categoryRepository;
		this.modelMapper = modelMapper;
	}

	@Override
	public CategoriesResponse getAllCategories() {
		List<Category> categories = categoryRepository.findAll();

		if (categories.isEmpty()) {
			throw new APIException("No Categories created yet");
		}

		List<CategoryDTO> categoryDTOS =
			categories.stream()
			.map(category -> modelMapper.map(category, CategoryDTO.class))
			.toList();

		return new CategoriesResponse(categoryDTOS);
	}

	@Override
	public CategoryDTO createCategory(CategoryDTO category) {
		Category savedCategory = categoryRepository.findByCategoryName(category.getCategoryName());
		Category categoryToSave = modelMapper.map(category, Category.class);
		if (savedCategory != null) {
			throw new APIException("Category with name: " + savedCategory.getCategoryName() + ", already exists");
		}
		categoryRepository.save(categoryToSave);
		return modelMapper.map(categoryToSave, CategoryDTO.class);
	}

	@Override
	public CategoryDTO updateCategory(CategoryDTO newCategoryData, long categoryId) {
		Category category =
			categoryRepository.findById(categoryId)
				.orElseThrow(
					() -> new ResourceNotFoundException("Category", "categoryId", categoryId)
				);

		category.setCategoryName(newCategoryData.getCategoryName());
		categoryRepository.save(category);

		return this.modelMapper.map(category, CategoryDTO.class);
	}

	@Override
	public CategoryDTO deleteCategory(long categoryId) {
		Category categoryToDelete =
			categoryRepository.findById(categoryId)
				.orElseThrow(
					() -> new ResourceNotFoundException("Category", "categoryId", categoryId)
				);

		categoryRepository.delete(categoryToDelete);

		return this.modelMapper.map(categoryToDelete, CategoryDTO.class);
	}
}
