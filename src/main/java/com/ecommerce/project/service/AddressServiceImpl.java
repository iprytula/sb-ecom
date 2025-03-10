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
		List<Address> addresses = addressesPage.getContent();

		if (addresses.isEmpty()) {
			throw new ResourceNotFoundException("No addresses found");
		}

		List<AddressDTO> addressesDTOs = addresses.stream()
			.map(address -> {
				AddressDTO addressDTO = modelMapper.map(address, AddressDTO.class);
				addressDTO.setUserId(address.getUser().getId());

				return addressDTO;
			}).toList();

		return PageableResponse.<AddressDTO>builder()
			.content(addressesDTOs)
			.totalElements(addressesPage.getTotalElements())
			.totalPages(addressesPage.getTotalPages())
			.pageNumber(pageNumber)
			.pageSize(pageSize)
			.totalPages(addressesPage.getTotalPages())
			.totalElements(addressesPage.getTotalElements())
			.lastPage(addressesPage.isLast())
			.build();
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

	public List<AddressDTO> getAddressesByUserId(Long userId) {
		return addressRepository.findAllByUserId(userId).stream()
			.map(address -> modelMapper.map(address, AddressDTO.class))
			.toList();
	}

	@Override
	public AddressDTO updateAddress(AddressDTO addressDTO, Long addressId) {
		Address addressToUpdate = addressRepository.findById(addressId)
			.orElseThrow(() -> new ResourceNotFoundException("Address", "addressId", addressId));

		addressToUpdate.setCity(addressDTO.getCity());
		addressToUpdate.setStreet(addressDTO.getStreet());
		addressToUpdate.setState(addressDTO.getState());
		addressToUpdate.setCountry(addressDTO.getCountry());
		addressToUpdate.setBuildingName(addressDTO.getBuildingName());
		addressToUpdate.setZipCode(addressDTO.getZipCode());

		return modelMapper.map(addressRepository.save(addressToUpdate), AddressDTO.class);
	}

	@Override
	public AddressDTO deleteAddress(Long addressId) {
		Address addressToDelete = addressRepository.findById(addressId)
			.orElseThrow(() -> new ResourceNotFoundException("Address", "addressId", addressId));

		addressRepository.delete(addressToDelete);

		return modelMapper.map(addressToDelete, AddressDTO.class);
	}
}
