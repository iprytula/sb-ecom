package com.ecommerce.project.controller;

import com.ecommerce.project.config.AppConstants;
import com.ecommerce.project.config.Authority;
import com.ecommerce.project.payload.AddressDTO;
import com.ecommerce.project.payload.PageableResponse;
import com.ecommerce.project.service.AddressService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AddressController {

	private final AddressService addressService;

	@Autowired
	AddressController(AddressService addressService) {
		this.addressService = addressService;
	}

	@PostMapping("/addresses")
	public ResponseEntity<AddressDTO> createAddress(@Valid @RequestBody AddressDTO addressDTO) {
		return new ResponseEntity<>(
			addressService.createAddress(addressDTO),
			HttpStatus.CREATED
		);
	}

	@GetMapping("/addresses")
	@PreAuthorize(Authority.ADMIN)
	public ResponseEntity<PageableResponse<AddressDTO>> getAllAddresses(
		@RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER) Integer pageNumber,
		@RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE) Integer pageSize,
		@RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_ADDRESSES_BY) String sortBy,
		@RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_ADDRESSES_ORDER) String sortOrder
	) {
		return new ResponseEntity<>(
			addressService.getAllAddresses(pageNumber, pageSize, sortBy, sortOrder),
			HttpStatus.OK
		);
	}

	@GetMapping("/addresses/me")
	public ResponseEntity<List<AddressDTO>> getLoggedInUserAddresses() {
		return new ResponseEntity<>(
			addressService.getLoggedInUserAddresses(),
			HttpStatus.OK
		);
	}

	@GetMapping("/addresses/user/{userId}")
	@PreAuthorize(Authority.ADMIN)
	public ResponseEntity<List<AddressDTO>> getAddressesByUserId(@PathVariable Long userId) {
		return new ResponseEntity<>(
			addressService.getAddressesByUserId(userId),
			HttpStatus.OK
		);
	}

	@GetMapping("/admin/addresses/{addressId}")
	@PreAuthorize(Authority.ADMIN)
	public ResponseEntity<AddressDTO> getAddressById(@PathVariable Long addressId) {
		return new ResponseEntity<>(
			addressService.getAddressById(addressId),
			HttpStatus.OK
		);
	}

	@PutMapping("/addresses/{addressId}")
	public ResponseEntity<AddressDTO> updateAddress(@Valid @RequestBody AddressDTO addressDTO, @PathVariable Long addressId) {
		return new ResponseEntity<>(
			addressService.updateAddress(addressDTO, addressId),
			HttpStatus.OK
		);
	}

	@DeleteMapping("/addresses/{addressId}")
	public ResponseEntity<AddressDTO> deleteAddress(@PathVariable Long addressId) {
		return new ResponseEntity<>(
			addressService.deleteAddress(addressId),
			HttpStatus.OK
		);
	}

}
