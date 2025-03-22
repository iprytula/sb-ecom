package com.ecommerce.project.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "order_items")
@AllArgsConstructor
@NoArgsConstructor
public class OrderItem {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "product_id")
	private Product product;

	@ManyToOne
	@JoinColumn(name = "order_id")
	private Order order;

	private Integer quantity;
	private double discount;
	private double orderedProductPrice;

	public OrderItem(
		Product product,
		Order order,
		Integer quantity,
		Double discount,
		Double price
	) {
		this.product = product;
		this.order = order;
		this.quantity = quantity;
		this.discount = discount;
		this.orderedProductPrice = price;
	}

	@Override
	public String toString() {
		return "OrderItem{id=" + id + ", product=" + product.getName() + ", quantity=" + quantity + "}";
	}
}