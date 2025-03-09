package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.APIException;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Cart;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.model.Product;
import com.ecommerce.project.payload.PageableResponse;
import com.ecommerce.project.payload.ProductDTO;
import com.ecommerce.project.repository.CartItemRepository;
import com.ecommerce.project.repository.CartRepository;
import com.ecommerce.project.repository.CategoryRepository;
import com.ecommerce.project.repository.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

	private final ProductRepository productRepository;
	private final CategoryRepository categoryRepository;
	private final CartRepository cartRepository;
	private final ModelMapper modelMapper;
	private final FileServiceImpl fileServiceImpl;

	@Value("${project.image_path}")
	private String path;

	@Autowired
	public ProductServiceImpl(
		ProductRepository productRepository,
		CategoryRepository categoryRepository,
		CartItemRepository cartItemRepository,
		CartRepository cartRepository,
		ModelMapper modelMapper,
		FileServiceImpl fileServiceImpl
	) {
		this.productRepository = productRepository;
		this.categoryRepository = categoryRepository;
		this.cartRepository = cartRepository;
		this.modelMapper = modelMapper;
		this.fileServiceImpl = fileServiceImpl;
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
	public PageableResponse<ProductDTO> getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
		Sort sortByAndOrder =
			sortOrder.equalsIgnoreCase("asc")
				? Sort.by(sortBy).ascending()
				: Sort.by(sortBy).descending();

		Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
		Page<Product> productsPage = productRepository.findAll(pageDetails);
		return getProductsResponse(pageNumber, pageSize, productsPage);
	}

	@Override
	public PageableResponse<ProductDTO> getProductsByCategoryId(
		Long categoryId, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
		Pageable pageDetails = PageRequest.of(pageNumber, pageSize, getSortByAndOrder(sortBy, sortOrder));
		Page<Product> productsPage = productRepository.findByCategoryId(pageDetails, categoryId);
		return getProductsResponse(pageNumber, pageSize, productsPage);
	}

	@Override
	public PageableResponse<ProductDTO> getProductsByKeyword(
		String keyword, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder
	) {
		Pageable pageDetails = PageRequest.of(pageNumber, pageSize, getSortByAndOrder(sortBy, sortOrder));
		Page<Product> productsPage = productRepository.findByNameLikeIgnoreCase("%" + keyword + "%", pageDetails);
		return getProductsResponse(pageNumber, pageSize, productsPage);
	}

	@Override
	@Transactional
	public ProductDTO updateProduct(Long productId, ProductDTO productDTO) {
		Product existingProduct = productRepository.findById(productId)
			.orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

		modelMapper.typeMap(ProductDTO.class, Product.class)
			.addMappings(mapper -> {
				if (productDTO.getImage() == null) {
					mapper.skip(Product::setImage);
				}
				mapper.skip(Product::setId);
			});
		modelMapper.map(productDTO, existingProduct);

		ProductDTO updatedProductDTO = modelMapper.map(productRepository.save(existingProduct), ProductDTO.class);

		List<Cart> cartsToModify = cartRepository.findActiveCartsByProductId(productId).orElseGet(ArrayList::new);
		if (!cartsToModify.isEmpty()) {
			cartsToModify.forEach(cart -> cart.getCartItems()
				.stream()
				.filter(ci -> ci.getProduct().getId().equals(productId))
				.findFirst()
				.ifPresent(cart::updateCartItem)
			);

			cartRepository.saveAll(cartsToModify);
		}

		return updatedProductDTO;
	}

	@Transactional
	@Override
	public ProductDTO deleteProduct(Long productId) {
		Product productToDelete = productRepository.findById(productId)
			.orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

		productRepository.delete(productToDelete);

		List<Cart> cartsToModify = cartRepository.findActiveCartsByProductId(productId).orElseGet(ArrayList::new);
		if (!cartsToModify.isEmpty()) {
			cartsToModify.forEach(cart -> {
				cart.getCartItems().stream()
					.filter(ci -> ci.getProduct().getId().equals(productId))
					.findFirst()
					.ifPresent(cart::deleteCartItem);
			});

			cartRepository.saveAll(cartsToModify);
		}

		return modelMapper.map(productToDelete, ProductDTO.class);
	}

	@Override
	public ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException {
		Product product = productRepository.findById(productId)
			.orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

		String fileName = fileServiceImpl.uploadFile(path, image);
		product.setImage(fileName);
		Product updatedProduct = productRepository.save(product);

		return modelMapper.map(updatedProduct, ProductDTO.class);
	}


	private PageableResponse<ProductDTO> getProductsResponse(Integer pageNumber, Integer pageSize, Page<Product> productsPage) {
		List<Product> products = productsPage.getContent();

		if (products.isEmpty()) {
			throw new ResourceNotFoundException("No products found");
		}

		List<ProductDTO> productDTOList = products.stream()
			.map(product -> modelMapper.map(product, ProductDTO.class)).toList();

		PageableResponse<ProductDTO> productsResponse = new PageableResponse<>();
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
