package com.ecommerce.project.service;

import com.ecommerce.project.payload.AddressDTO;
import com.ecommerce.project.payload.PageableResponse;

public interface AddressService {
	AddressDTO createAddress(AddressDTO addressDTO);

	PageableResponse<AddressDTO> getAllAddresses(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);
}
