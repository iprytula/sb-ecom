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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
		Sort sortByAndOrder =
			sortOrder.equalsIgnoreCase("asc")
				? Sort.by(sortBy).ascending()
				: Sort.by(sortBy).descending();

		Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
		Page<Address> addressesPage = addressRepository.findAll(pageDetails);

		return getAddressesResponse(pageNumber, pageSize, addressesPage);
	}

	@Override
	public List<AddressDTO> getLoggedInUserAddresses() {
		User user = authUtil.getLoggedInUser();

		return addressRepository.findAllByUserId(user.getId()).stream()
			.map(address -> modelMapper.map(address, AddressDTO.class))
			.toList();
	}

	@Override
	public AddressDTO getAddressById(Long addressId) {
		return addressRepository.findById(addressId)
			.map(address -> modelMapper.map(address, AddressDTO.class))
			.orElseThrow(() -> new ResourceNotFoundException("Address", "addressId", addressId));
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
}
