package com.encora.spark.breakable_toy_one.controller;

import com.encora.spark.breakable_toy_one.model.Product;
import com.encora.spark.breakable_toy_one.repository.ProductRepository;
import com.encora.spark.breakable_toy_one.service.ProductService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/products")
public class ProductController {
    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @GetMapping
    public List<Product> getAll() {
        return service.getAll();
    }

    @PostMapping
    public Product createProduct(@RequestBody Product product) {
        return service.create(product);
    }

    @PostMapping("/{id}/outofstock")
    public void markOutOfStock(@PathVariable UUID id) {
        service.markOutOfStock(id);
    }

    @PutMapping("/{id}")
    public Product updateProduct(@PathVariable UUID id, @RequestBody Product updated) {
        return service.update(id, updated);
    }

    @PutMapping("/{id}/instock")
    public void markInStock(@PathVariable UUID id,
                            @RequestParam(defaultValue = "10") int quantity) {
        service.markInStock(id, quantity);
    }

    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable UUID id) {
        service.delete(id);
    }
}