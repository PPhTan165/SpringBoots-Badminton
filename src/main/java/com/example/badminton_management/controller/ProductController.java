package com.example.badminton_management.controller;

import com.example.badminton_management.dto.CreateProductRequest;
import com.example.badminton_management.dto.ProductResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct (
            @Valid @RequestBody CreateProductRequest request
            ){
        ProductResponse response = service.createProduct(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/secure-test")
    public ResponseEntity<String> secureTest(){
        return ResponseEntity.ok("You have accessed a protected API ");
    }

}
