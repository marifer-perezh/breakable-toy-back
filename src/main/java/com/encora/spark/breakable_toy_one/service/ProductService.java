package com.encora.spark.breakable_toy_one.service;

import com.encora.spark.breakable_toy_one.model.Product;
import com.encora.spark.breakable_toy_one.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.lang.Integer;
import java.time.LocalDateTime;
import java.util.*;
import java.util.Optional;


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
            
            product.setCreationDate(LocalDateTime.now());
        }
        product.setUpdateDate(LocalDateTime.now());
        return repo.save(product);
    }
    //Read
    public Optional<Product> getProductById(Integer id){
        return repo.findById(id);
    }

    public List<Product> getAllProducts(){
        return repo.findAll();
    }
    //Update
    public Product updateProduct(Integer id, Product productDetails) {
        return repo.findById(id)
                .map(existingProduct -> {
                    existingProduct.setName(productDetails.getName());
                    existingProduct.setCategory(productDetails.getCategory());
                    existingProduct.setUnitPrice(productDetails.getUnitPrice());
                    existingProduct.setQuantityInStock(productDetails.getQuantityInStock());
                    existingProduct.setExpirationDate(productDetails.getExpirationDate());
                    existingProduct.setUpdateDate(LocalDateTime.now());
                    return repo.save(existingProduct);
                })
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
    }
    //Delete
    public void deleteProduct(Integer id) {
        repo.deleteById(id);
    }

    //Advanced functions
    public List<Product> getFilteredProducts(
            String name,
            List<String> categories,
            Boolean inStock,
            String sortBy,
            String sortDirection) {

        List<Product> filteredProducts = repo.findAll();

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
        List<Product> filteredProducts = repo.findAll();

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

    public void markOutOfStock(Integer id) {
        Optional<Product> product = repo.findById(id);
        if (product.isEmpty()) {
            throw new RuntimeException("Product not found");
        }
        Product productUpdate = product.get();
        productUpdate.setQuantityInStock(0);
        repo.save(productUpdate);
    }

    public void markInStock(Integer id, int quantity) {
        Optional<Product> product = repo.findById(id);
        if (product.isEmpty()) {
            throw new RuntimeException("Product not found");
        }
        Product productUpdate = product.get();
        productUpdate.setQuantityInStock(quantity);
        repo.save(productUpdate);
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> getInventoryMetrics() {
        // TODO: Require implementation next integration
        return Collections.EMPTY_MAP;
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