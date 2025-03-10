package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.APIException;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.payload.CategoryDTO;
import com.ecommerce.project.payload.PageableResponse;
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
	public PageableResponse<CategoryDTO> getAllCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
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

		return PageableResponse.<CategoryDTO>builder()
			.content(categoryDTOS)
			.pageNumber(pageNumber)
			.pageSize(pageSize)
			.totalPages(categoryPage.getTotalPages())
			.totalElements(categoryPage.getTotalElements())
			.lastPage(categoryPage.isLast())
			.build();
	}

	@Override
	public CategoryDTO createCategory(CategoryDTO categoryDTO) {
		Category savedCategory = categoryRepository.findByName(categoryDTO.getName());
		Category categoryToSave = modelMapper.map(categoryDTO, Category.class);

		if (savedCategory != null) {
			throw new APIException("Category with name: " + savedCategory.getName() + ", already exists");
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

		category.setName(categoryDTO.getName());
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
