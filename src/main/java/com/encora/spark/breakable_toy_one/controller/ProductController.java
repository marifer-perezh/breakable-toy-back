package com.encora.spark.breakable_toy_one.controller;

import com.encora.spark.breakable_toy_one.model.Product;
import com.encora.spark.breakable_toy_one.repository.ProductRepository;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/products")
public class ProductController {
    private final ProductRepository repo;

    public ProductController(ProductRepository repo){
        this.repo = repo;
    }
    //GET
    @GetMapping
    public List<Product> getAll(){
        return repo.findAll();
    }

    //POST
    @PostMapping
    public Product createProduct(@RequestBody Product product){
        product.setId(UUID.randomUUID());
        product.setCreationDate(LocalDateTime.now());
        product.setUpdateDate(LocalDateTime.now());
        repo.save(product);
        return product;
    }
    @PostMapping("/{id}/outofstock")
    public void markOutOfStock(@PathVariable UUID id){
        if (repo.findById(id).isEmpty()) {
            throw new RuntimeException("Product not found");
        }
        repo.markOutOfStock(id);
    }

    //PUT
    @PutMapping("/{id}")
    public Product updateProduct(@PathVariable UUID id, @RequestBody Product updated) {
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
    @PutMapping("/{id}/instock")
    public void markInStock(@PathVariable UUID id,
                            @RequestParam(defaultValue = "10") int quantity){
        repo.markInStock(id, quantity);

    }

    //Delete
    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable UUID id){
        
    }

}
