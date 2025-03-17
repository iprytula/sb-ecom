package com.ecommerce.project.service;

import com.ecommerce.project.payload.OrderDTO;

public interface OrderService {
	OrderDTO placeOrder(OrderDTO orderRequest, String paymentMethod);
}
