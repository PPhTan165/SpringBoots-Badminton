package com.example.badminton_management.repository;

public interface OrderRepository {
    boolean existsName(String name);
    boolean existsNameNotId(String name, Long id);
}
