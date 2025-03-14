package com.ecommerce.project.repository;

import com.ecommerce.project.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, Long> {

	@Query("SELECT a FROM Address a WHERE a.user.userId = ?1")
	List<Address> findAllByUserId(Long userId);

	@Query("SELECT a FROM Address a WHERE a.user.userId = ?2 AND a.addressId = ?1")
	Optional<Address> findByIdAndUserId(Long addressId, Long userId);

}
