package com.ecommerce.project.controller;

import com.ecommerce.project.config.AppConstants;
import com.ecommerce.project.payload.ProductDTO;
import com.ecommerce.project.payload.ProductsResponse;
import com.ecommerce.project.service.ProductServiceImpl;
import com.ecommerce.project.validation.ProductValidationGroup;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ProductController {
	private final ProductServiceImpl productServiceImpl;

	@Autowired
	public ProductController(ProductServiceImpl productServiceImpl) {
		this.productServiceImpl = productServiceImpl;
	}

	@PostMapping("/admin/categories/{categoryId}/product")
	public ResponseEntity<ProductDTO> addProduct(@RequestBody @Validated(ProductValidationGroup.class) ProductDTO productDTO, @PathVariable Long categoryId) {
		return new ResponseEntity<>(productServiceImpl.addProduct(productDTO, categoryId), HttpStatus.CREATED);
	}

	@GetMapping("/public/products")
	public ResponseEntity<ProductsResponse> getAllProducts(
		@RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER) Integer pageNumber,
		@RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE) Integer pageSize,
		@RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_PRODUCTS_BY) String sortBy,
		@RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_PRODUCTS_ORDER) String sortOrder
	) {
		return new ResponseEntity<>(
			productServiceImpl.getAllProducts(pageNumber, pageSize, sortBy, sortOrder),
			HttpStatus.OK
		);
	}

	@GetMapping("/public/categories/{categoryId}/products")
	public ResponseEntity<ProductsResponse> getProductsByCategory(
		@PathVariable Long categoryId,
		@RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER) Integer pageNumber,
		@RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE) Integer pageSize,
		@RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_PRODUCTS_BY) String sortBy,
		@RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_PRODUCTS_ORDER) String sortOrder
	) {
		return new ResponseEntity<>(
			productServiceImpl.getProductsByCategoryId(categoryId, pageNumber, pageSize, sortBy, sortOrder),
			HttpStatus.OK
		);
	}

	@GetMapping("/public/products/keyword/{keyword}")
	public ResponseEntity<ProductsResponse> getProductsByKeyword(
		@PathVariable String keyword,
		@RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER) Integer pageNumber,
		@RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE) Integer pageSize,
		@RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_PRODUCTS_BY) String sortBy,
		@RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_PRODUCTS_ORDER) String sortOrder
	) {
		return new ResponseEntity<>(
			productServiceImpl.getProductsByKeyword(keyword, pageNumber, pageSize, sortBy, sortOrder),
			HttpStatus.FOUND
		);
	}

	@PutMapping("/admin/products/{productId}")
	public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long productId, @RequestBody @Valid ProductDTO productDTO) {
		return new ResponseEntity<>(productServiceImpl.updateProduct(productId, productDTO), HttpStatus.ACCEPTED);
	}

	@DeleteMapping("/admin/products/{productId}")
	public ResponseEntity<ProductDTO> deleteProduct(@PathVariable Long productId) {
		return new ResponseEntity<>(productServiceImpl.deleteProduct(productId), HttpStatus.OK);
	}

}
