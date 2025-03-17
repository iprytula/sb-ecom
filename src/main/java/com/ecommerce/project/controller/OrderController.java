package com.ecommerce.project.controller;

import com.ecommerce.project.payload.OrderDTO;
import com.ecommerce.project.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

	private final OrderService orderService;

	@Autowired
	OrderController(OrderService orderService) {
		this.orderService = orderService;
	}

	@PostMapping("/order/{paymentMethod}")
	public ResponseEntity<OrderDTO> placeOrder(
		@RequestBody OrderDTO orderRequest,
		@PathVariable String paymentMethod
	) {
		return new ResponseEntity<>(
			orderService.placeOrder(orderRequest, paymentMethod),
			HttpStatus.CREATED
		);
	}
}
