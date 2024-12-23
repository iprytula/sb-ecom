package com.ecommerce.project.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "products")
@ToString
public class Product {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false)
	private Long id;
	@Column(nullable = false)
	private String name;
	private String description;
	private Double price;
	private Integer quantity;
	private Double specialPrice;
	private Double discountPrice;
	private String image;

	@ManyToOne
//	@JoinColumn(name = "category_id")
	@JoinColumn(nullable = false)
	private Category category;

	@ManyToOne
	@JoinColumn(name = "seller_id")
	private User user;
}
