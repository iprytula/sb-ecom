package com.ecommerce.project.util;

import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.User;
import com.ecommerce.project.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthUtil {
	private final UserRepository userRepository;

	@Autowired
	public AuthUtil(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public User getLoggedInUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		return userRepository.findByUserName(authentication.getName())
			.orElseThrow(() -> new ResourceNotFoundException("User not found"));
	}
}
