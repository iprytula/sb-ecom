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
import com.ecommerce.project.payload.UserDTO;
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

		User loggedInUser = authUtil.loggedInUser();
		Cart cart = cartRepository.findActiveCartByUserId(loggedInUser.getId())
			.orElseGet(() -> {
				Cart newCart = new Cart();
				newCart.setUser(loggedInUser);
				return cartRepository.save(newCart);
			});

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

		cartDTO.setUser(convertUserToUserDTO(loggedInUser));

		List<ProductDTO> products = cart.getCartItems().stream()
			.map(item -> {
				ProductDTO productDTO = modelMapper.map(item.getProduct(), ProductDTO.class);
				productDTO.setQuantity(item.getQuantity());
				return productDTO;
			}).toList();

		cartDTO.setProducts(products);

		return cartDTO;
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
		Long loggedInUserId = authUtil.loggedInUser().getId();
		Cart cart = cartRepository.findActiveCartByUserId(loggedInUserId)
			.orElseThrow(() -> new ResourceNotFoundException("Cart", "userId", loggedInUserId));

		CartDTO cartResponse = modelMapper.map(cart, CartDTO.class);
		cartResponse.setUser(convertUserToUserDTO(authUtil.loggedInUser()));
		cartResponse.setProducts(getProductsFromCartItems(cart.getCartItems()));

		return cartResponse;
	}

	@Override
	public CartDTO updateProductQuantity(Long productId, Integer quantity) {
		Cart cart = cartRepository.findActiveCartByUserId(authUtil.loggedInUser().getId())
			.orElseThrow(() -> new ResourceNotFoundException("Cart", "userId", authUtil.loggedInUser().getId()));
		Product product = productRepository.findById(productId)
			.orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));
		CartItem cartItemToUpdate = cartItemRepository.findCartItemByCartIdAndProductId(cart.getId(), productId)
			.orElseThrow(() -> new ResourceNotFoundException("CartItem", "productId", productId));

		if (product.getQuantity() < quantity) {
			throw new APIException("Please, make an order of the " + product.getName()
				+ " less than or equal to the quantity " + product.getQuantity() + ".");
		}

		cartItemToUpdate.setQuantity(quantity);
		cartItemToUpdate.setPrice(product.getPrice());

		cart.setCartItem(cartItemToUpdate);

		CartDTO cartDTO = modelMapper.map(cartRepository.save(cart), CartDTO.class);
		cartDTO.setProducts(getProductsFromCartItems(cart.getCartItems()));

		return cartDTO;
	}

	public CartDTO deleteProductFromCart(Long productId) {
		Cart cart = cartRepository.findActiveCartByUserId(authUtil.loggedInUser().getId())
			.orElseThrow(() -> new ResourceNotFoundException("Cart", "userId", authUtil.loggedInUser().getId()));
		CartItem cartItemToDelete = cartItemRepository.findCartItemByCartIdAndProductId(cart.getId(), productId)
			.orElseThrow(() -> new ResourceNotFoundException("CartItem", "productId", productId));

		cart.getCartItems().remove(cartItemToDelete);
		cartRepository.save(cart);

		CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
		cartDTO.setProducts(getProductsFromCartItems(cart.getCartItems()));

		return cartDTO;
	}

	private CartsResponse getCartsResponse(Integer pageNumber, Integer pageSize, Page<Cart> cartsPage) {
		if (cartsPage.isEmpty())
			throw new ResourceNotFoundException("No carts found");

		List<CartDTO> cartDTOs = cartsPage.getContent().stream()
			.map(cart -> {
				CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
				cartDTO.setUser(convertUserToUserDTO(cart.getUser()));
				cartDTO.setProducts(getProductsFromCartItems(cart.getCartItems()));
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

	private List<ProductDTO> getProductsFromCartItems(List<CartItem> cartItems) {
		return cartItems.stream()
			.map(cartItem -> {
				ProductDTO productDTO = modelMapper.map(cartItem.getProduct(), ProductDTO.class);
				productDTO.setQuantity(cartItem.getQuantity());
				return productDTO;
			})
			.toList();
	}

	private UserDTO convertUserToUserDTO(User user) {
		UserDTO userDTO = modelMapper.map(user, UserDTO.class);

		List<String> rolesStrings = user.getRoles().stream()
			.map(role -> role.getRoleName().name())
			.toList();

		userDTO.setRoles(rolesStrings);

		return userDTO;
	}
}
