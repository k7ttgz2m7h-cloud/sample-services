package com.stepflow.sample.inventory.repository;

import com.stepflow.sample.inventory.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
}
