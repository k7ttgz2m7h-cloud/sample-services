package com.stepflow.customer.domain.repository;

import com.stepflow.customer.domain.entity.CustomerDeliveryAddress;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.*;

public interface CustomerDeliveryAddressRepository extends JpaRepository<CustomerDeliveryAddress, Long> {
    @Query("select d from CustomerDeliveryAddress d join fetch d.address where d.customer.customerId=:customerId order by d.id")
    List<CustomerDeliveryAddress> findAggregateAddresses(@Param("customerId") UUID customerId);

    Optional<CustomerDeliveryAddress> findByIdAndCustomerCustomerId(Long id, UUID customerId);

    List<CustomerDeliveryAddress> findByCustomerCustomerIdAndPrimaryAddressTrue(UUID customerId);

    @Modifying
    @Query("update CustomerDeliveryAddress d set d.primaryAddress=false where d.customer.customerId=:customerId")
    int clearPrimary(@Param("customerId") UUID customerId);

    @Modifying
    @Query("update CustomerDeliveryAddress d set d.primaryAddress=false where d.customer.customerId=:customerId and d.id<>:exceptId")
    int clearPrimaryExcept(@Param("customerId") UUID customerId, @Param("exceptId") Long exceptId);
}
