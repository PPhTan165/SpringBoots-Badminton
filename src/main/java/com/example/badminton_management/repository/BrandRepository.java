package com.example.badminton_management.repository;

import com.example.badminton_management.model.Brand;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BrandRepository extends JpaRepository<Brand,Long> {
    boolean existsByName(String name);

}
