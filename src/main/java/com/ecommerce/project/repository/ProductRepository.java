package com.ecommerce.project.repository;

import com.ecommerce.project.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
	Product findByName(String name);

	@Query("SELECT p FROM Product p WHERE p.category.categoryId = ?1")
	Page<Product> findByCategoryId(Pageable pageable, Long categoryId);

	Page<Product> findByNameLikeIgnoreCase(String name, Pageable pageable);
}
