package com.ecommerce.project.service;

import com.ecommerce.project.payload.ProductDTO;
import com.ecommerce.project.payload.ProductsResponse;
import org.springframework.stereotype.Service;

@Service
public interface ProductService {
	ProductDTO addProduct(ProductDTO product, Long categoryId);
	ProductsResponse getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);
	ProductsResponse getProductsByCategoryId(Long categoryId, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);
}
