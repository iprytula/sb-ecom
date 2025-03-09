package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Address;
import com.ecommerce.project.model.User;
import com.ecommerce.project.payload.AddressDTO;
import com.ecommerce.project.payload.PageableResponse;
import com.ecommerce.project.repository.AddressRepository;
import com.ecommerce.project.util.AuthUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {

	private final AddressRepository addressRepository;
	private final AuthUtil authUtil;
	private final ModelMapper modelMapper;

	@Autowired
	AddressServiceImpl(
		AddressRepository addressRepository,
		AuthUtil authUtil,
		ModelMapper modelMapper
	) {
		this.addressRepository = addressRepository;
		this.authUtil = authUtil;
		this.modelMapper = modelMapper;
	}

	public AddressDTO createAddress(AddressDTO addressDTO) {
		Address addressToSave = modelMapper.map(addressDTO, Address.class);
		User user = authUtil.getLoggedInUser();

		addressToSave.setUser(user);

		return modelMapper.map(
			addressRepository.save(addressToSave),
			AddressDTO.class
		);
	}

	@Override
	public PageableResponse<AddressDTO> getAllAddresses(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
		PageableResponse<AddressDTO> response = new PageableResponse<>();
		List<Address> addresses = addressRepository.findAll();
		response.setContent(addresses.stream().map(address -> modelMapper.map(address, AddressDTO.class)).toList());
		return null;
	}

	private PageableResponse<AddressDTO> getAddressesResponse(Integer pageNumber, Integer pageSize, Page<Address> addressesPage) {
		List<Address> addresses = addressesPage.getContent();

		if (addresses.isEmpty()) {
			throw new ResourceNotFoundException("No addresses found");
		}

		List<AddressDTO> addressesDTOs = addresses.stream()
			.map(address -> modelMapper.map(address, AddressDTO.class)).toList();

		PageableResponse<AddressDTO> pageableResponse = new PageableResponse<>();
		pageableResponse.setContent(addressesDTOs);
		pageableResponse.setTotalElements(addressesPage.getTotalElements());
		pageableResponse.setTotalPages(addressesPage.getTotalPages());
		pageableResponse.setPageNumber(pageNumber);
		pageableResponse.setPageSize(pageSize);
		pageableResponse.setTotalPages(addressesPage.getTotalPages());
		pageableResponse.setTotalElements(addressesPage.getTotalElements());
		pageableResponse.setLastPage(addressesPage.isLast());

		return pageableResponse;
	}

	private Sort getSortByAndOrder(String sortBy, String sortOrder) {
		return sortOrder.equalsIgnoreCase("asc")
			? Sort.by(sortBy).ascending()
			: Sort.by(sortBy).descending();
	}
}
