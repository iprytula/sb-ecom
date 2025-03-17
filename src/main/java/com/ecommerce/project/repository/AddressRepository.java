package com.ecommerce.project.repository;

import com.ecommerce.project.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, Long> {

	List<Address> findAllByUserId(Long userId);

	Optional<Address> findByIdAndUserId(Long addressId, Long userId);

}
