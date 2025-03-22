package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.APIException;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.*;
import com.ecommerce.project.payload.*;
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
import org.springframework.transaction.annotation.Transactional;

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

		User loggedInUser = authUtil.getLoggedInUser();
		Cart cart = cartRepository.findCartByUserId(loggedInUser.getId())
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
		newCartItem.setDiscount(product.getDiscount());
		newCartItem.setPrice(product.getPrice());

		cart.addCartItem(newCartItem);

		CartDTO cartDTO = (CartDTO) mapCartToCartDTO(cartRepository.save(cart), false);

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
	public PageableResponse<CartAdminDTO> getAllCarts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
		Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
			? Sort.by(sortBy).ascending()
			: Sort.by(sortBy).descending();

		Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
		Page<Cart> cartsPage = cartRepository.findAll(pageDetails);
		List<Cart> carts = cartsPage.getContent();

		if (carts.isEmpty())
			throw new ResourceNotFoundException("No carts found");

		List<CartAdminDTO> cartDTOs = cartsPage.getContent().stream()
			.map(cart -> {
				CartAdminDTO cartDTO = (CartAdminDTO) mapCartToCartDTO(cart, true);
				cartDTO.setUser(mapUserToUserDTO(cart.getUser()));
				cartDTO.setProducts(mapCartItemsToProductsDTOs(cart.getCartItems()));
				return cartDTO;
			})
			.toList();

		return PageableResponse.<CartAdminDTO>builder()
			.content(cartDTOs)
			.totalElements(cartsPage.getTotalElements())
			.totalPages(cartsPage.getTotalPages())
			.pageNumber(pageNumber)
			.pageSize(pageSize)
			.lastPage(cartsPage.isLast())
			.build();
}

	@Override
	public CartDTO getLoggedInUserCart() {
		Long loggedInUserId = authUtil.getLoggedInUser().getId();
		Cart cart = cartRepository.findCartByUserId(loggedInUserId)
			.orElseThrow(() -> new ResourceNotFoundException("Cart", "userId", loggedInUserId));

		CartDTO cartDTO = (CartDTO) mapCartToCartDTO(cart, false);
		cartDTO.setProducts(mapCartItemsToProductsDTOs(cart.getCartItems()));

		return cartDTO;
	}

	@Override
	@Transactional
	public CartDTO updateCartItemQuantity(Long productId, Integer quantity) {
		Cart cart = cartRepository.findCartByUserId(authUtil.getLoggedInUser().getId())
			.orElseThrow(() -> new ResourceNotFoundException("Cart", "userId", authUtil.getLoggedInUser().getId()));
		Product product = productRepository.findById(productId)
			.orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));
		CartItem cartItemToUpdate = cartItemRepository.findCartItemByCartIdAndProductId(cart.getId(), productId)
			.orElseThrow(() -> new ResourceNotFoundException("CartItem", "productId", productId));

		if (product.getQuantity() < quantity) {
			throw new APIException("Please, make an order of the " + product.getName()
				+ " less than or equal to the quantity " + product.getQuantity() + ".");
		}

		if (quantity <= 0) throw new APIException("Quantity must be greater than 0.");

		cartItemToUpdate.setQuantity(quantity);
		cartItemToUpdate.setPrice(product.getPrice());

		cart.updateCartItem(cartItemToUpdate);

		CartDTO cartDTO = (CartDTO) mapCartToCartDTO(cartRepository.save(cart), false);
		cartDTO.setProducts(mapCartItemsToProductsDTOs(cart.getCartItems()));

		return cartDTO;
	}

	@Override
	@Transactional
	public CartDTO deleteProductFromCart(Long productId) {
		Cart cart = cartRepository.findCartByUserId(authUtil.getLoggedInUser().getId())
			.orElseThrow(() -> new ResourceNotFoundException("Cart", "userId", authUtil.getLoggedInUser().getId()));
		CartItem cartItemToDelete = cartItemRepository.findCartItemByCartIdAndProductId(cart.getId(), productId)
			.orElseThrow(() -> new ResourceNotFoundException("CartItem", "productId", productId));

		cart.deleteCartItem(cartItemToDelete);
		cartRepository.save(cart);

		CartDTO cartDTO = (CartDTO) mapCartToCartDTO(cart, false);
		cartDTO.setProducts(mapCartItemsToProductsDTOs(cart.getCartItems()));

		return cartDTO;
	}

	private Object mapCartToCartDTO(Cart cart, boolean isAdmin) {
		if (isAdmin) {
			CartAdminDTO cartAdminDTO = modelMapper.map(cart, CartAdminDTO.class);
			UserDTO userDTO = mapUserToUserDTO(cart.getUser());
			cartAdminDTO.setUser(userDTO);

			return cartAdminDTO;
		}

		return modelMapper.map(cart, CartDTO.class);
	}

	private List<ProductDTO> mapCartItemsToProductsDTOs(List<CartItem> cartItems) {
		return cartItems.stream()
			.map(cartItem -> {
				ProductDTO productDTO = modelMapper.map(cartItem.getProduct(), ProductDTO.class);
				productDTO.setQuantity(cartItem.getQuantity());
				return productDTO;
			})
			.toList();
	}

	private UserDTO mapUserToUserDTO(User user) {
		UserDTO userDTO = modelMapper.map(user, UserDTO.class);

		List<String> rolesStrings = user.getRoles().stream()
			.map(Role::getRoleName)
			.toList();

		userDTO.setRoles(rolesStrings);

		return userDTO;
	}
}
