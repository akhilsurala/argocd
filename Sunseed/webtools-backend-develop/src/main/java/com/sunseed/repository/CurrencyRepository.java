package com.sunseed.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sunseed.entity.Currency;

public interface CurrencyRepository extends JpaRepository<Currency,Long> {
}
