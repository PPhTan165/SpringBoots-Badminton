package com.example.badminton_management.service;

import com.example.badminton_management.dto.CreateProductRequest;
import com.example.badminton_management.dto.ProductResponse;
import com.example.badminton_management.enums.ProductStatus;
import com.example.badminton_management.exception.ResourceNotFoundException;
import com.example.badminton_management.model.Brand;
import com.example.badminton_management.model.Category;
import com.example.badminton_management.model.Product;
import com.example.badminton_management.repository.BrandRepository;
import com.example.badminton_management.repository.CategoryRepository;
import com.example.badminton_management.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository, BrandRepository brandRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.brandRepository = brandRepository;
    }

    public ProductResponse mapToResponse(Product product){
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setSku(product.getSku());
        response.setDescription(product.getDescription());
        response.setPrice(product.getPrice());
        response.setStockQuantity(product.getStockQuantity());
        response.setImageUrl(product.getImageUrl());
        response.setStatus(product.getStatus().name());

        response.setCategoryId(product.getCategory().getId());
        response.setCategoryName(product.getCategory().getName());

        if(product.getBrand() != null){
            response.setBrandId(product.getBrand().getId());
            response.setBrandName(product.getBrand().getName());
        }

        return response;
    }

    public ProductResponse createProduct(CreateProductRequest request){
        if(productRepository.existsBySku(request.getSku())){
            throw new IllegalArgumentException("SKU already exists");
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(()-> new ResourceNotFoundException("Category not found"));

        Brand brand = brandRepository.findById(request.getBrandId())
                .orElseThrow(()-> new ResourceNotFoundException("Brand not found"));


        Product product = new Product();
        product.setName(request.getName());
        product.setSku(request.getSku());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setImageUrl(request.getImageUrl());
        product.setCategory(category);
        product.setBrand(brand);
        product.setStatus(ProductStatus.ACTIVE);

        return mapToResponse(productRepository.save(product));
    }

    public List<ProductResponse> getAllProducts(){
        return productRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    public ProductResponse getProductById(long id){
       if(id < 0) {
           throw new IllegalArgumentException("Id must be greater than 0");
       }
       Product product = productRepository.findById(id)
               .orElseThrow(()-> new ResourceNotFoundException("Product not found with id: "+ id));

       if(product == null){
           throw new ResourceNotFoundException("Product not found");
       }

       return mapToResponse(product);
    }
}
