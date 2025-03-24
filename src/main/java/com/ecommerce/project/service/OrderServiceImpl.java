package com.ecommerce.project.service;

import com.ecommerce.project.config.OrderStatus;
import com.ecommerce.project.exceptions.APIException;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.*;
import com.ecommerce.project.payload.*;
import com.ecommerce.project.repository.*;
import com.ecommerce.project.util.AuthUtil;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

	private final AuthUtil authUtil;
	private final CartRepository cartRepository;
	private final AddressRepository addressRepository;
	private final OrderRepository orderRepository;
	private final OrderItemRepository orderItemRepository;
	private final ProductRepository productRepository;
	private final ModelMapper modelMapper;
	private final PaymentRepository paymentRepository;
	private final CartItemRepository cartItemRepository;

	public OrderServiceImpl(
		AuthUtil authUtil,
		CartRepository cartRepository,
		AddressRepository addressRepository,
		OrderRepository orderRepository,
		OrderItemRepository orderItemRepository,
		ProductRepository productRepository,
		ModelMapper modelMapper,
		PaymentRepository paymentRepository,
		CartItemRepository cartItemRepository
	) {
		this.authUtil = authUtil;
		this.cartRepository = cartRepository;
		this.addressRepository = addressRepository;
		this.orderRepository = orderRepository;
		this.orderItemRepository = orderItemRepository;
		this.productRepository = productRepository;
		this.modelMapper = modelMapper;
		this.paymentRepository = paymentRepository;
		this.cartItemRepository = cartItemRepository;
	}

	@Override
	@Transactional
	public OrderDTO placeOrder(OrderRequestDTO orderRequest) {
		User user = authUtil.getLoggedInUser();
		Cart cart = cartRepository.findCartByUserId(user.getId())
			.orElseThrow(() -> new ResourceNotFoundException("Cart", "userId", user.getId()));
		if (cart == null) {
			throw new ResourceNotFoundException("Cart", "userId", user.getId());
		}

		Address address = addressRepository.findById(orderRequest.getAddressId())
			.orElseThrow(() -> new ResourceNotFoundException("Address", "addressId", orderRequest.getAddressId()));

		Order order = new Order();
		order.setEmail(orderRequest.getEmail());
		order.setTotalAmount(cart.getTotalPrice());
		order.setOrderStatus(OrderStatus.PLACED);
		order.setAddress(address);

		Payment payment = new Payment(
			orderRequest.getPaymentMethod(),
			orderRequest.getPgPaymentId(),
			orderRequest.getPgStatus(),
			orderRequest.getPgResponseMessage(),
			orderRequest.getPgName()
		);
		payment.setOrder(order);
		payment = paymentRepository.save(payment);
		order.setPayment(payment);

		Order savedOrder = orderRepository.save(order);

		List<CartItem> cartItems = cart.getCartItems();
		if (cartItems.isEmpty()) {
			throw new APIException("Cart is empty");
		}

		List<OrderItem> orderItems = new ArrayList<>();
		for (CartItem cartItem : cartItems) {
			OrderItem orderItem = new OrderItem();
			orderItem.setProduct(cartItem.getProduct());
			orderItem.setQuantity(cartItem.getQuantity());
			orderItem.setDiscount(cartItem.getDiscount());
			orderItem.setOrderedProductPrice(cartItem.getPrice());
			orderItem.setOrder(savedOrder);
			orderItems.add(orderItem);
		}

		orderItems = orderItemRepository.saveAll(orderItems);

		List<Product> productsToUpdate = cart.getCartItems().stream()
			.map(cartItem -> {
				Product product = cartItem.getProduct();
				product.setQuantity(product.getQuantity() - cartItem.getQuantity());

				cartItemRepository.deleteCartItemByCartIdAndProductId(cart.getId(), product.getId());

				return product;
			})
			.toList();

		productRepository.saveAll(productsToUpdate);
		cart.clearCart();
		cartRepository.save(cart);

		OrderDTO orderDTO = modelMapper.map(savedOrder, OrderDTO.class);
		List<OrderItemDTO> orderItemsDTOs = orderItems.stream()
			.map(item -> modelMapper.map(item, OrderItemDTO.class))
			.toList();

		orderDTO.setOrderItems(orderItemsDTOs);

		orderDTO.setAddress(modelMapper.map(address, AddressDTO.class));

		return orderDTO;
	}
}
