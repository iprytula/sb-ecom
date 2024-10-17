package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.APIException;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.model.Product;
import com.ecommerce.project.payload.ProductDTO;
import com.ecommerce.project.payload.ProductsResponse;
import com.ecommerce.project.repository.CategoryRepository;
import com.ecommerce.project.repository.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

	private final ProductRepository productRepository;
	private final CategoryRepository categoryRepository;
	private final ModelMapper modelMapper;

	@Autowired
	public ProductServiceImpl(
		ProductRepository productRepository,
		CategoryRepository categoryRepository,
		ModelMapper modelMapper
	) {
		this.productRepository = productRepository;
		this.categoryRepository = categoryRepository;
		this.modelMapper = modelMapper;
	}

	@Override
	public ProductDTO addProduct(ProductDTO product, Long categoryId) {
		Product productToSave = modelMapper.map(product, Product.class);
		Product savedProduct = productRepository.findByName(productToSave.getName());
		Category productCategory = categoryRepository.findById(categoryId)
			.orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));

		if (savedProduct != null) {
			throw new APIException("product with name " + productToSave.getName() + " already exists");
		}

		productToSave.setImage(productToSave.getImage() != null ? productToSave.getImage() : "default.png");
		productToSave.setCategory(productCategory);

		return modelMapper.map(productRepository.save(productToSave), ProductDTO.class);
	}

	@Override
	public ProductsResponse getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
		Sort sortByAndOrder =
			sortOrder.equalsIgnoreCase("asc")
				? Sort.by(sortBy).ascending()
				: Sort.by(sortBy).descending();

		Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
		Page<Product> productsPage = productRepository.findAll(pageDetails);
		return getProductsResponse(pageNumber, pageSize, productsPage);
	}

	@Override
	public ProductsResponse getProductsByCategoryId(Long categoryId, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
		Pageable pageDetails = PageRequest.of(pageNumber, pageSize, getSortByAndOrder(sortBy, sortOrder));
		Page<Product> productsPage = productRepository.findByCategoryId(pageDetails, categoryId);
		return getProductsResponse(pageNumber, pageSize, productsPage);
	}

	@Override
	public ProductsResponse getProductsByKeyword(String keyword, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
		Pageable pageDetails = PageRequest.of(pageNumber, pageSize, getSortByAndOrder(sortBy, sortOrder));
		Page<Product> productsPage = productRepository.findByNameLikeIgnoreCase("%" + keyword + "%", pageDetails);
		return getProductsResponse(pageNumber, pageSize, productsPage);
	}

	@Override
	public ProductDTO updateProduct(Long productId, ProductDTO productDTO) {
		Product existingProduct = productRepository.findById(productId)
			.orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

		modelMapper.typeMap(ProductDTO.class, Product.class).addMappings(mapper -> mapper.skip(Product::setId));
		modelMapper.map(productDTO, existingProduct);

		Product updatedProduct = productRepository.save(existingProduct);

		return modelMapper.map(updatedProduct, ProductDTO.class);
	}

	@Override
	public ProductDTO deleteProduct(Long productId) {
		Product productToDelete = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));
		productRepository.delete(productToDelete);

		return modelMapper.map(productToDelete, ProductDTO.class);
	}

	private ProductsResponse getProductsResponse(Integer pageNumber, Integer pageSize, Page<Product> productsPage) {
		List<Product> products = productsPage.getContent();

		if (products.isEmpty()) {
			throw new ResourceNotFoundException("No products found");
		}

		List<ProductDTO> productDTOList = products.stream()
			.map(product -> modelMapper.map(product, ProductDTO.class)).toList();

		ProductsResponse productsResponse = new ProductsResponse();
		productsResponse.setContent(productDTOList);
		productsResponse.setTotalElements(productsPage.getTotalElements());
		productsResponse.setTotalPages(productsPage.getTotalPages());
		productsResponse.setPageNumber(pageNumber);
		productsResponse.setPageSize(pageSize);
		productsResponse.setTotalPages(productsPage.getTotalPages());
		productsResponse.setTotalElements(productsPage.getTotalElements());
		productsResponse.setLastPage(productsPage.isLast());

		return productsResponse;
	}

	private Sort getSortByAndOrder(String sortBy, String sortOrder) {
		return sortOrder.equalsIgnoreCase("asc")
			? Sort.by(sortBy).ascending()
			: Sort.by(sortBy).descending();
	}

}
