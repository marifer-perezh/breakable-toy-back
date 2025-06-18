package com.encora.spark.breakable_toy_one.service;

import com.encora.spark.breakable_toy_one.model.Product;
import com.encora.spark.breakable_toy_one.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProductService {

    private final ProductRepository repo;

    public ProductService(ProductRepository repo) {
        this.repo = repo;
    }

    public List<Product> getAll() {
        return repo.findAll();
    }

    public Product create(Product product) {
        product.setId(UUID.randomUUID());
        product.setCreationDate(LocalDateTime.now());
        product.setUpdateDate(LocalDateTime.now());
        repo.save(product);
        return product;
    }

    public void markOutOfStock(UUID id) {
        if (repo.findById(id).isEmpty()) {
            throw new RuntimeException("Product not found");
        }
        repo.markOutOfStock(id);
    }

    public Product update(UUID id, Product updated) {
        return repo.findById(id)
                .map(p -> {
                    p.setName(updated.getName());
                    p.setCategory(updated.getCategory());
                    p.setUnitPrice(updated.getUnitPrice());
                    p.setExpirationDate(updated.getExpirationDate());
                    p.setQuantityInStock(updated.getQuantityInStock());
                    p.setUpdateDate(LocalDateTime.now());
                    repo.update(p);
                    return p;
                })
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    public void markInStock(UUID id, int quantity) {
        repo.markInStock(id, quantity);
    }

    public void delete(UUID id) {
        repo.deleteById(id);
    }

    public Optional<Product> findById(UUID id) {
        return repo.findById(id);
    }
}