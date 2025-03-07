package com.ecommerce.project.service;

import com.ecommerce.project.model.Address;
import com.ecommerce.project.model.User;
import com.ecommerce.project.payload.AddressDTO;
import com.ecommerce.project.repository.AddressRepository;
import com.ecommerce.project.util.AuthUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

}
