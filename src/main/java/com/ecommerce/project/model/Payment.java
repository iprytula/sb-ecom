package com.ecommerce.project.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long paymentId;

	@OneToOne
	@JoinColumn(name = "order_id")
	private Order order;

	@NotBlank(message = "Payment method is required")
	@Size(min = 4, max = 50, message = "Payment method must be between 4 and 50 characters")
	private String paymentMethod;

	private String pgPaymentId;
	private String pgStatus;
	private String pgResponseMessage;
	private String pgName;

	public Payment(Long paymentId, String pgPaymentId, String pgStatus, String pgResponseMessage, String pgName) {
		this.paymentId = paymentId;
		this.pgPaymentId = pgPaymentId;
		this.pgStatus = pgStatus;
		this.pgResponseMessage = pgResponseMessage;
		this.pgName = pgName;
	}
}
