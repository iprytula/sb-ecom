package com.ecommerce.project.security.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class UserInfoResponse {
	private Long id;
	private String username;
	private String email;
//	private String jwtToken;
	private List<String> roles;
}
