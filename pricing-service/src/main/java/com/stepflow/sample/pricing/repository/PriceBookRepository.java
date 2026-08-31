package com.stepflow.sample.pricing.repository;

import com.stepflow.sample.pricing.entity.PriceBook;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PriceBookRepository extends JpaRepository<PriceBook, Long> {
    Optional<PriceBook> findByPriceBookId(String priceBookId);
}
