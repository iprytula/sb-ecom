package com.ecommerce.project.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

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
	private Double standardPrice;
	private Double specialPrice;
	private Integer quantity;
	private String image;

	@ManyToOne
	@JoinColumn(nullable = false)
	private Category category;

	@ManyToOne
	@JoinColumn(name = "seller_id")
	private User user;

	@OneToMany(
		mappedBy = "product",
		cascade = {CascadeType.PERSIST, CascadeType.MERGE},
		orphanRemoval = true,
		fetch = FetchType.EAGER
	)
	private List<CartItem> products = new ArrayList<>();

	public Double getPrice() {
		if (this.specialPrice != null) {
			return this.specialPrice;
		} else {
			return this.standardPrice;
		}
	}

	public Double getDiscount() {
		if (this.specialPrice != null) {
			return this.standardPrice - this.specialPrice;
		} else {
			return 0.0;
		}
	}
}
