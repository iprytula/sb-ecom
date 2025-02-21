package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.APIException;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Cart;
import com.ecommerce.project.model.CartItem;
import com.ecommerce.project.model.Product;
import com.ecommerce.project.model.User;
import com.ecommerce.project.payload.CartDTO;
import com.ecommerce.project.payload.ProductDTO;
import com.ecommerce.project.repository.CartItemRepository;
import com.ecommerce.project.repository.CartRepository;
import com.ecommerce.project.repository.ProductRepository;
import com.ecommerce.project.util.AuthUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

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

		Cart cart = this.createCart();

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
		newCartItem.setDiscount(product.getDiscountPrice());
		newCartItem.setProductPrice(product.getPrice());

		cart.addCartItem(newCartItem);

		cart.setTotalPrice(cart.getTotalPrice() + (product.getPrice() * quantity));

		CartDTO cartDTO = modelMapper.map(cartRepository.save(cart), CartDTO.class);

		List<ProductDTO> products = cart.getCartItems().stream()
			.map(item -> {
				ProductDTO map = modelMapper.map(item.getProduct(), ProductDTO.class);
				map.setQuantity(item.getQuantity());
				return map;
			}).toList();

		cartDTO.setProducts(products);

		return cartDTO;
	}

	private Cart createCart() {
		User loggedInUser = authUtil.loggedInUser();
		Optional<Cart> userCart = cartRepository.findCartByUserId(loggedInUser.getId());
		if(userCart.isPresent()) {
			return userCart.get();
		}

		Cart cart = new Cart();
		cart.setTotalPrice(0.00);
		cart.setUser(loggedInUser);

		return cartRepository.save(cart);
	}
}
