package com.ecommerce.project.config;

public class Authority {
	public static final String ADMIN = "hasAuthority('" + AppRoles.ROLE_ADMIN + "')";
	public static final String SELLER = "hasAuthority('" + AppRoles.ROLE_SELLER + "')";
}
