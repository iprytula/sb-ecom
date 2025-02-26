package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.APIException;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Cart;
import com.ecommerce.project.model.CartItem;
import com.ecommerce.project.model.Product;
import com.ecommerce.project.model.User;
import com.ecommerce.project.payload.CartDTO;
import com.ecommerce.project.payload.CartsResponse;
import com.ecommerce.project.payload.ProductDTO;
import com.ecommerce.project.repository.CartItemRepository;
import com.ecommerce.project.repository.CartRepository;
import com.ecommerce.project.repository.ProductRepository;
import com.ecommerce.project.util.AuthUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CartServiceImpl implements CartService {
	private final CartRepository cartRepository;
	private final ProductRepository productRepository;
	private final CartItemRepository cartItemRepository;
	private final ModelMapper modelMapper;
	private final AuthUtil authUtil;

	@Autowired
	CartServiceImpl(
		CartRepository cartRepository,
		ProductRepository productRepository,
		CartItemRepository cartItemRepository,
		ModelMapper modelMapper,
		AuthUtil authUtil
	) {
		this.cartRepository = cartRepository;
		this.modelMapper = modelMapper;
		this.authUtil = authUtil;
		this.productRepository = productRepository;
		this.cartItemRepository = cartItemRepository;
	}

	@Override
	public CartDTO addProductToCart(Long productId, Integer quantity) {
		if (productId == null || quantity == null || quantity <= 0) {
			throw new APIException("productId and quantity must be present and greater than 0.");
		}

		Cart cart = this.getActiveCartOrCreateOne();

		Product product = productRepository.findById(productId)
			.orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

		if (cartItemRepository.findCartItemByCartIdAndProductId(cart.getId(), productId).isPresent()) {
			throw new APIException("Product " + product.getName() + " already exists in the cart");
		}

		if (product.getQuantity() < quantity) {
			throw new APIException("Please, make an order of the " + product.getName()
				+ " less than or equal to the quantity " + product.getQuantity() + ".");
		}

		CartItem newCartItem = new CartItem();
		newCartItem.setProduct(product);
		newCartItem.setCart(cart);
		newCartItem.setQuantity(quantity);
		newCartItem.setPrice(product.getPrice());

		cart.addCartItem(newCartItem);

		CartDTO cartDTO = modelMapper.map(
			cartRepository.save(cart),
			CartDTO.class
		);

		List<ProductDTO> products = cart.getCartItems().stream()
			.map(item -> {
				ProductDTO productDTO = modelMapper.map(item.getProduct(), ProductDTO.class);
				productDTO.setQuantity(item.getQuantity());
				return productDTO;
			}).toList();

		cartDTO.setProducts(products);

		return cartDTO;
	}

	private Cart getActiveCartOrCreateOne() {
		User loggedInUser = authUtil.loggedInUser();
		Optional<Cart> userCart = cartRepository.findActiveCartByUserId(loggedInUser.getId());
		if (userCart.isPresent()) {
			return userCart.get();
		}

		Cart cart = new Cart();
		cart.setTotalPrice(0.00);
		cart.setUser(loggedInUser);

		return cartRepository.save(cart);
	}

	private Cart getLoggedInUserActiveCart() {
		Long loggedInUserId = authUtil.loggedInUser().getId();
		Optional<Cart> cart = cartRepository.findActiveCartByUserId(loggedInUserId);

		if (cart.isEmpty()) {
			throw new ResourceNotFoundException("Cart", "userId", loggedInUserId);
		}

		return cart.get();
	}

	@Override
	public CartsResponse getAllCarts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
		Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
			? Sort.by(sortBy).ascending()
			: Sort.by(sortBy).descending();

		Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
		Page<Cart> cartsPage = cartRepository.findAll(pageDetails);

		return getCartsResponse(pageNumber, pageSize, cartsPage);
}

	@Override
	public CartDTO getLoggedInUserCart() {
		Cart cart = getLoggedInUserActiveCart();

		List<ProductDTO> products = cart.getCartItems().stream()
			.map(cartItem -> modelMapper.map(cartItem.getProduct(), ProductDTO.class))
			.toList();

		CartDTO cartResponse = modelMapper.map(cart, CartDTO.class);
		cartResponse.setProducts(products);

		return cartResponse;
	}

	private CartsResponse getCartsResponse(Integer pageNumber, Integer pageSize, Page<Cart> cartsPage) {
		if (cartsPage.isEmpty())
			throw new ResourceNotFoundException("No carts found");

		List<CartDTO> cartDTOs = cartsPage.getContent().stream()
			.map(cart -> {
				CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
				List<ProductDTO> products = cart.getCartItems().stream()
					.map(cartItem -> modelMapper.map(cartItem.getProduct(), ProductDTO.class))
					.toList();
				cartDTO.setProducts(products);
				return cartDTO;
			})
			.toList();

		CartsResponse cartsResponse = new CartsResponse();
		cartsResponse.setContent(cartDTOs);
		cartsResponse.setTotalElements(cartsPage.getTotalElements());
		cartsResponse.setTotalPages(cartsPage.getTotalPages());
		cartsResponse.setPageNumber(pageNumber);
		cartsResponse.setPageSize(pageSize);
		cartsResponse.setLastPage(cartsPage.isLast());

		return cartsResponse;
	}
}
