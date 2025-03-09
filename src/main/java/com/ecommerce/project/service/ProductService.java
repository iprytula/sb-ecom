package com.ecommerce.project.service;

import com.ecommerce.project.payload.PageableResponse;
import com.ecommerce.project.payload.ProductDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ProductService {
	ProductDTO addProduct(ProductDTO product, Long categoryId);

	PageableResponse<ProductDTO> getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

	PageableResponse<ProductDTO> getProductsByCategoryId(Long categoryId, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

	PageableResponse<ProductDTO> getProductsByKeyword(String keyword, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

	ProductDTO updateProduct(Long productId, ProductDTO productDTO);

	ProductDTO deleteProduct(Long productId);

	ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException;
}
