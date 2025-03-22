package com.ecommerce.project.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequestDTO {
	private Long id;
	private Long addressId;
	private String email;
	private String paymentMethod;
	private String pgName;
	private String pgPaymentId;
	private String pgStatus;
	private String pgResponseMessage;
}
