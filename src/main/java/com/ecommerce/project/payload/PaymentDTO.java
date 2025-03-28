package com.ecommerce.project.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDTO {
	private Long id;
	private String paymentMethod;
	private String pgPaymentId;
	private String pgStatus;
	private String pgResponseMessage;
	private String pgName;
}
