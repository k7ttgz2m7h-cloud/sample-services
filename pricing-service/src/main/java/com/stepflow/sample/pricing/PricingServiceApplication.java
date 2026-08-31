package com.stepflow.sample.pricing;

import com.stepflow.sample.pricing.entity.PriceBook;
import com.stepflow.sample.pricing.repository.PriceBookRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class PricingServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(PricingServiceApplication.class, args);
    }

    @Bean
    CommandLineRunner seedPriceBook(PriceBookRepository repository) {
        return args -> repository.save(new PriceBook(null, "PB-1", "STANDARD", "USD"));
    }
}
