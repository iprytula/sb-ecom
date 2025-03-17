package com.ecommerce.project.service;

import com.ecommerce.project.payload.OrderDTO;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl implements OrderService {

	@Override
	public OrderDTO placeOrder(OrderDTO orderRequest, String paymentMethod) {
		return null;
	}
}
