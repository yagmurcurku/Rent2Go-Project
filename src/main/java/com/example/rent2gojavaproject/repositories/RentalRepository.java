package com.example.rent2gojavaproject.repositories;

import com.example.rent2gojavaproject.models.Rental;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RentalRepository extends JpaRepository<Rental, Integer> {

}
