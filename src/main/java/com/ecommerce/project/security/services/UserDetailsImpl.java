package com.ecommerce.project.security.services;

import java.io.Serial;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.ecommerce.project.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;

@NoArgsConstructor
@Data
public class UserDetailsImpl implements UserDetails {
	@Serial
	private static final long serialVersionUID = 1L;

	private Long id;

	private String username;

	private String email;

	@JsonIgnore
	private String password;

	private Collection<? extends GrantedAuthority> authorities;

	public UserDetailsImpl(
		Long id,
		String username,
		String email,
		String password,
		Collection<? extends GrantedAuthority> authorities
	) {
		this.id = id;
		this.username = username;
		this.email = email;
		this.password = password;
		this.authorities = authorities;
	}

	/**
	 * Builds a {@link UserDetailsImpl} from a {@link User}.
	 *
	 * @param user the user to build the {@link UserDetailsImpl} from
	 * @return the built {@link UserDetailsImpl}
	 */
	public static UserDetailsImpl build(User user) {
		List<GrantedAuthority> authorities = user.getRoles().stream()
			.map(role -> new SimpleGrantedAuthority(role.getRoleName()))
			.collect(Collectors.toList());

		return new UserDetailsImpl(
			user.getId(),
			user.getUserName(),
			user.getEmail(),
			user.getPassword(),
			authorities);
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		UserDetailsImpl user = (UserDetailsImpl) o;
		return Objects.equals(id, user.id);
	}

}