package com.encora.spark.breakable_toy_one.repository;

import com.encora.spark.breakable_toy_one.model.Product;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;

@Repository
public class ProductRepository {
    private final Map<UUID, Product> products = new HashMap<>();

    public List<Product> findAll(){
        return new ArrayList<>(products.values());
    }

    public Optional<Product> findById(UUID id){
        return Optional.ofNullable(products.get(id));
    }

    public void save(Product product){
        products.put(product.getId(),product);
    }

    public void update(Product product){
        products.put(product.getId(), product);
    }

    public void markOutOfStock(UUID id){
        Product p = products.get(id);
        if (p != null){
           p.setQuantityInStock(0);
           p.setUpdateDate(LocalDateTime.now());
        }
    }

    public void markInStock(UUID id, int quantity){
        Product p = products.get(id);
        if (p != null) {
            p.setQuantityInStock(quantity);
            p.setUpdateDate(LocalDateTime.now());
        }
    }

    public void deleteById(UUID id){
        products.remove(id);
    }
}
