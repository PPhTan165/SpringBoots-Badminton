package com.example.badminton_management.service;

import com.example.badminton_management.repository.CategoryRepository;

public class CategoryService {
    private CategoryRepository repository;

    public CategoryService(CategoryRepository repository) {
        this.repository = repository;
    }


}
