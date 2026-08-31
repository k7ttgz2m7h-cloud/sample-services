package com.stepflow.sample.inventory;

import com.stepflow.sample.inventory.entity.Inventory;
import com.stepflow.sample.inventory.repository.InventoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class InventoryServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(InventoryServiceApplication.class, args);
    }

    @Bean
    CommandLineRunner seedInventory(InventoryRepository repository) {
        return args -> {
            repository.save(new Inventory(null, "PROD-1", 100));
            repository.save(new Inventory(null, "PROD-2", 50));
        };
    }
}
