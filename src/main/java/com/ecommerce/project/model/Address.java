package com.ecommerce.project.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "addresses")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Address {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank
	@Size(min = 5, message = "Street name should be at least 5 characters")
	private String street;

	@NotBlank
	@Size(min = 5, message = "Building name should be at least 5 characters")
	private String buildingName;

	@NotBlank
	@Size(min = 4, message = "City name should be at least 4 characters")
	private String city;

	@NotBlank
	@Size(min = 2, message = "City name should be at least 5 characters")
	private String state;

	@NotBlank
	@Size(min = 4, message = "Zip code name should be at least 4 characters")
	private String zipCode;

	@NotBlank
	@Size(min = 2, message = "Country name should be at least 2 characters")
	private String country;

	@ManyToMany(mappedBy = "addresses")
	private List<User> users = new ArrayList<>();
}
