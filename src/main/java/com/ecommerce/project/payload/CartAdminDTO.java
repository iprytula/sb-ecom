package com.ecommerce.project.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartAdminDTO {
	private Long id;
	private UserDTO user;
	private Double totalPrice;
	private List<ProductDTO> products = new ArrayList<>();
	private Boolean isActive;
	private Date orderDate;
}
