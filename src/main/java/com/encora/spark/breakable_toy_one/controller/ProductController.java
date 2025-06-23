package com.encora.spark.breakable_toy_one.controller;

import com.encora.spark.breakable_toy_one.model.Product;
import com.encora.spark.breakable_toy_one.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.Integer;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/products")
public class ProductController {
    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    //CRUD Endpoints
    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        Product createdProduct = service.createProduct(product);
        return ResponseEntity.ok(createdProduct);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Integer id) {
        return service.getProductById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(
            @PathVariable Integer id,
            @RequestBody Product productDetails
    ) {
        Product updatedProduct = service.updateProduct(id, productDetails);
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Integer id) {
        service.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    // Advance Search
    @GetMapping
    public ResponseEntity<List<Product>> getProducts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) List<String> categories,
            @RequestParam(required = false) Boolean inStock,
            //@RequestParam(defaultValue = "0") int page,
            //@RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection
    )
    {
        /*Map<String, Object> response = service.getFilteredAndPaginatedProducts(
                name, categories, inStock, page, size, sortBy, sortDirection
        );*/

        List<Product> products = service.getFilteredProducts(name, categories, inStock,sortBy, sortDirection);
        //return ResponseEntity.ok(response);
        return ResponseEntity.ok(products);
    }

    //Stock
    @PostMapping("/{id}/out-of-stock")
    public ResponseEntity<Void> markOutOfStock(@PathVariable Integer id) {
        service.markOutOfStock(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/in-stock")
    public ResponseEntity<Void> markInStock(
            @PathVariable Integer id,
            @RequestParam(defaultValue = "10") int quantity
    ) {
        service.markInStock(id, quantity);
        return ResponseEntity.ok().build();
    }

    //Metrics
    @GetMapping("/metrics")
    public ResponseEntity<Map<String, Object>> getInventoryMetrics() {
        Map<String, Object> metrics = service.getInventoryMetrics();
        return ResponseEntity.ok(metrics);
    }

    /*
    @GetMapping("/all")
    public List<Product> getAll() {
        return service.getAll();
    }

    @GetMapping
    public ResponseEntity<List<Product>> getProducts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) List<String> categories,
            @RequestParam(defaultValue = "0") int page
    ) {

        return ResponseEntity.ok(service.getFilteredProducts(name, categories, page));
    }

    @PostMapping
    public Product createProduct(@RequestBody Product product) {
        return service.create(product);
    }

    @PostMapping("/{id}/outofstock")
    public void markOutOfStock(@PathVariable Integer id) {
        service.markOutOfStock(id);
    }

    @PutMapping("/{id}")
    public Product updateProduct(@PathVariable Integer id, @RequestBody Product updated) {
        return service.update(id, updated);
    }

    @PutMapping("/{id}/instock")
    public void markInStock(@PathVariable Integer id,
                            @RequestParam(defaultValue = "10") int quantity) {
        service.markInStock(id, quantity);
    }

    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable Integer id) {
        service.delete(id);
    }
    */
}