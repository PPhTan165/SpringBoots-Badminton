package com.example.badminton_management.service;

import com.example.badminton_management.dto.category.CategoryResponse;
import com.example.badminton_management.dto.category.CreateCategoryRequest;
import com.example.badminton_management.dto.category.UpdateCategoryRequest;
import com.example.badminton_management.dto.product.ProductResponse;
import com.example.badminton_management.exception.BadRequestException;
import com.example.badminton_management.exception.ResourceNotFoundException;
import com.example.badminton_management.model.Category;
import com.example.badminton_management.repository.CategoryRepository;
import com.example.badminton_management.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    private CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository repository) {
        this.categoryRepository = repository;
    }

    public CategoryResponse mapToResponse(Category category){
        CategoryResponse response = new CategoryResponse();
        response.setId(category.getId());
        response.setName(category.getName());
        response.setDescription(category.getDescription());
        response.setParentId(category.getParent().getId());

        return response;
    }

    public List<CategoryResponse> getAllCategories(){
        return categoryRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    public CategoryResponse getCategoryById(Long id){
        if(id < 0){
            throw new BadRequestException("Id must be greater than 0");
        }

        Category category = categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

        if(category == null){
            throw new ResourceNotFoundException("Category not found");
        }

        return mapToResponse(category);
    }

    public CategoryResponse createCategory(CreateCategoryRequest request){
        if(categoryRepository.existsByName(request.getName())){
            throw new IllegalArgumentException("Category already exists");
        }
        Category childrent = categoryRepository.findById(request.getParentId())
                .orElseThrow(()->new ResourceNotFoundException("Childrent not found"));

        Category category = new Category();
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setParent(childrent);

        return mapToResponse(categoryRepository.save(category));
    }

    @Transactional
    public CategoryResponse updateCategory(Long id, UpdateCategoryRequest request){
        Category category = categoryRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Category not found with id: "+id));

        if(categoryRepository.existsByNameAndIdNot(request.getName(), id)){
            throw new BadRequestException("Category already exists");
        }

        Category parent = categoryRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("ParentId not found"));

        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setParent(parent);

        return mapToResponse(categoryRepository.save(category));
    }
}
