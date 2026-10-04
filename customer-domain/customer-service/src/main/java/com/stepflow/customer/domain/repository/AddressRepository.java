package com.stepflow.customer.domain.repository;

import com.stepflow.customer.domain.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {
}
