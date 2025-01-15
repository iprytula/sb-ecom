package com.ecommerce.project.security.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
public class UserInfoResponse {
	private Long id;
	private String username;
	private List<String> roles;
//	private String jwtToken;
}
