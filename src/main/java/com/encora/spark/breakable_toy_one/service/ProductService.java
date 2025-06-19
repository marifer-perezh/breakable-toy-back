package com.encora.spark.breakable_toy_one.service;

import com.encora.spark.breakable_toy_one.model.Product;
import com.encora.spark.breakable_toy_one.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.*;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProductService {
    private final ProductRepository repo;

    public ProductService(ProductRepository repo) {
        this.repo = repo;
    }

    //Basic CRUD
    //Create
    public Product createProduct(Product product){
        if(product.getId() == null){
            product.setId(UUID.randomUUID());
            product.setCreationDate(LocalDateTime.now());
        }
        product.setUpdateDate(LocalDateTime.now());
        return repo.save(product);
    }
    //Read
    public Optional<Product> getProductById(UUID id){
        return repo.findById(id);
    }

    public List<Product> getAllProducts(){
        return repo.findAll(null, null, null);
    }
    //Update
    public Product updateProduct(UUID id, Product productDetails) {
        return repo.findById(id)
                .map(existingProduct -> {
                    existingProduct.setName(productDetails.getName());
                    existingProduct.setCategory(productDetails.getCategory());
                    existingProduct.setUnitPrice(productDetails.getUnitPrice());
                    existingProduct.setQuantityInStock(productDetails.getQuantityInStock());
                    existingProduct.setExpirationDate(productDetails.getExpirationDate());
                    return repo.save(existingProduct);
                })
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
    }
    //Delete
    public void deleteProduct(UUID id) {
        repo.deleteById(id);
    }

    /*
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
    public Optional<Product> findById(UUID id) {
        return repo.findById(id);
    }
    public void delete(UUID id) {
        repo.deleteById(id);
    }

    */

    //Advanced functions
    public List<Product> getFilteredProducts(
            String name,
            List<String> categories,
            Boolean inStock,
            String sortBy,
            String sortDirection) {

        List<Product> filteredProducts = repo.findAll(name, categories, inStock);

        Comparator<Product> comparator = getComparator(sortBy);
        if ("desc".equalsIgnoreCase(sortDirection)) {
            comparator = comparator.reversed();
        }
        filteredProducts.sort(comparator);

        return filteredProducts;
    }

    public Map<String, Object> getFilteredAndPaginatedProducts(
            String name,
            List<String> categories,
            Boolean inStock,
            int page,
            int size,
            String sortBy,
            String sortDirection) {

        // 1. Filtrate
        List<Product> filteredProducts = repo.findAll(name, categories, inStock);

        // 2. Order
        Comparator<Product> comparator = getComparator(sortBy);
        if ("desc".equalsIgnoreCase(sortDirection)) {
            comparator = comparator.reversed();
        }
        filteredProducts.sort(comparator);

        // 3. Paginate
        int totalItems = filteredProducts.size();
        int totalPages = (int) Math.ceil((double) totalItems / size);
        int start = page * size;
        int end = Math.min(start + size, totalItems);
        List<Product> paginatedProducts = filteredProducts.subList(start, end);

        // 4. Return data
        return Map.of(
                "data", paginatedProducts,
                "pagination", Map.of(
                        "currentPage", page,
                        "pageSize", size,
                        "totalItems", totalItems,
                        "totalPages", totalPages
                )
        );
       }

    public void markOutOfStock(UUID id) {
        if (repo.findById(id).isEmpty()) {
            throw new RuntimeException("Product not found");
        }
        repo.markOutOfStock(id);
    }

    public void markInStock(UUID id, int quantity) {
        if (repo.findById(id).isEmpty()) {
            throw new RuntimeException("Product not found");
        }
        repo.markInStock(id, quantity);
    }

    public Map<String, Object> getInventoryMetrics() {
        return repo.getInventoryMetrics();
    }

    //Private Methods

    private Comparator<Product> getComparator(String sortBy) {
        return switch (sortBy != null ? sortBy.toLowerCase() : "name") {
            case "price" -> Comparator.comparing(Product::getUnitPrice);
            case "stock" -> Comparator.comparing(Product::getQuantityInStock);
            case "expiration" -> Comparator.comparing(
                    Product::getExpirationDate,
                    Comparator.nullsLast(Comparator.naturalOrder())
            );
            case "date" -> Comparator.comparing(Product::getCreationDate);
            default -> Comparator.comparing(Product::getName);
        };
    }
}