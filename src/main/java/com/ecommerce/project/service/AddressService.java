package com.ecommerce.project.service;

import com.ecommerce.project.payload.AddressDTO;
import com.ecommerce.project.payload.PageableResponse;

import java.util.List;

public interface AddressService {
	AddressDTO createAddress(AddressDTO addressDTO);

	PageableResponse<AddressDTO> getAllAddresses(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

	List<AddressDTO> getLoggedInUserAddresses();

	AddressDTO getAddressById(Long addressId);

	List<AddressDTO> getAddressesByUserId(Long userId);

	AddressDTO updateAddress(AddressDTO addressDTO, Long addressId);

	AddressDTO deleteAddress(Long addressId);
}
