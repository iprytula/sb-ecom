package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.APIException;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.payload.CategoriesResponse;
import com.ecommerce.project.payload.CategoryDTO;
import com.ecommerce.project.repository.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;

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
	public CategoriesResponse getAllCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
		Sort sortByAndOrder =
			sortOrder.equalsIgnoreCase("asc")
				? Sort.by(sortBy).ascending()
				: Sort.by(sortBy).descending();

		Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
		Page<Category> categoryPage = categoryRepository.findAll(pageDetails);
		List<Category> categories = categoryPage.getContent();

		if (categories.isEmpty()) {
			throw new ResourceNotFoundException("No categories found");
		}

		List<CategoryDTO> categoryDTOS =
			categories.stream()
			.map(category -> modelMapper.map(category, CategoryDTO.class))
			.toList();

		CategoriesResponse categoriesResponse = new CategoriesResponse();
		categoriesResponse.setContent(categoryDTOS);
		categoriesResponse.setPageNumber(pageNumber);
		categoriesResponse.setPageSize(pageSize);
		categoriesResponse.setTotalPages(categoryPage.getTotalPages());
		categoriesResponse.setTotalElements(categoryPage.getTotalElements());
		categoriesResponse.setLastPage(categoryPage.isLast());

		return categoriesResponse;
	}

	@Override
	public CategoryDTO createCategory(CategoryDTO categoryDTO) {
		Category savedCategory = categoryRepository.findByCategoryName(categoryDTO.getCategoryName());
		Category categoryToSave = modelMapper.map(categoryDTO, Category.class);

		if (savedCategory != null) {
			throw new APIException("Category with name: " + savedCategory.getCategoryName() + ", already exists");
		}

		categoryRepository.save(categoryToSave);

		return modelMapper.map(categoryToSave, CategoryDTO.class);
	}

	@Override
	public CategoryDTO updateCategory(CategoryDTO categoryDTO, long categoryId) {
		Category category =
			categoryRepository.findById(categoryId)
				.orElseThrow(
					() -> new ResourceNotFoundException("Category", "categoryId", categoryId)
				);

		category.setCategoryName(categoryDTO.getCategoryName());
		categoryRepository.save(category);

		return this.modelMapper.map(category, CategoryDTO.class);
	}

	@Override
	public CategoryDTO deleteCategory(Long categoryId) {
		Category categoryToDelete =
			categoryRepository.findById(categoryId)
				.orElseThrow(
					() -> new ResourceNotFoundException("Category", "categoryId", categoryId)
				);

		categoryRepository.delete(categoryToDelete);

		return this.modelMapper.map(categoryToDelete, CategoryDTO.class);
	}
}
