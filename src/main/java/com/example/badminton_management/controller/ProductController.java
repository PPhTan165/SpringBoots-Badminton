package com.example.badminton_management.controller;

import com.example.badminton_management.dto.product.CreateProductRequest;
import com.example.badminton_management.dto.product.ProductResponse;
import com.example.badminton_management.dto.product.UpdateProductRequest;
import com.example.badminton_management.dto.product.UpdateProductStatusRequest;
import com.example.badminton_management.model.Product;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.example.badminton_management.service.ProductService;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProduct(){
       return ResponseEntity.ok(service.getAllProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable long id) {return ResponseEntity.ok(service.getProductById(id));}

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ProductResponse> createProduct (
            @Valid @RequestBody CreateProductRequest request
            ){
        ProductResponse response = service.createProduct(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProductRequest request
            ){
        return ResponseEntity.ok(service.updateProduct(id,request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}")
    public ResponseEntity<ProductResponse> inActiveProduct(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProductStatusRequest request
            ){
        return  ResponseEntity.ok(service.updateProductStatus(id,request));
    }
}
