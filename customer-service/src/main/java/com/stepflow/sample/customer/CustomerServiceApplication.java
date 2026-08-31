package com.stepflow.sample.customer;

import com.stepflow.sample.customer.entity.Customer;
import com.stepflow.sample.customer.repository.CustomerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class CustomerServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(CustomerServiceApplication.class, args);
    }

    @Bean
    CommandLineRunner seedCustomers(CustomerRepository repository) {
        return args -> {
            repository.save(new Customer(null, "CUST-1", "John Doe", "john@test.com", "ACTIVE"));
            repository.save(new Customer(null, "CUST-2", "Jane Doe", "jane@test.com", "ACTIVE"));
            repository.save(new Customer(null, "CUST-3", "Blocked Customer", "blocked@test.com", "BLOCKED"));
        };
    }
}
