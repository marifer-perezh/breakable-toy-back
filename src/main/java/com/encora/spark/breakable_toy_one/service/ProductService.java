package com.encora.spark.breakable_toy_one.service;

import com.encora.spark.breakable_toy_one.model.Product;
import com.encora.spark.breakable_toy_one.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;
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
    public static Specification<Product> hasNameLike(String name){
        return(root, query, cb) ->
                name == null || name.isBlank() ? null: cb.like(cb.lower(root.get("name")),"%"+ name.toLowerCase()+"%");
    }
    public static Specification<Product> isInCategories(List<String> categories){
        return(root, query, cb) ->
                (categories == null || categories.isEmpty()) ? null: root.get("category").in(categories);
    }
    public static Specification<Product> isInStock(Boolean inStock){
        return(root, query, cb) -> {
            if (inStock == null) return null;
            return inStock ? cb.greaterThan(root.get("quantityInStock"), 0):cb.lessThanOrEqualTo(root.get("quantityInStock"),0);
        };
    }
    public Page<Product> getFilteredProducts(
            String name,
            List<String> categories,
            Boolean inStock,
            Pageable pageable) {

        Specification<Product> spec = hasNameLike(name).and(isInCategories(categories)).and(isInStock(inStock));
        return repo.findAll(spec, pageable);
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

    public List<String> getAllCategories() {
        return repo.findAll()
                .stream()
                .map(Product::getCategory)
                .distinct()
                .toList();
    }
    public Map<String, Object> getInventoryMetrics() {
        // TODO: Require implementation next integration
        List<Product> allProducts = repo.findAll();
        //Metricas Generales
        int totalStock = 0;
        double totalValue = 0;
        int inStockCount = 0;
        double inStockPriceSum = 0;

         Map<String, List<Product>> groupedByCategory = new HashMap<>();

         for(Product product : allProducts){
             int stock = product.getQuantityInStock();
             double value = product.getUnitPrice() * stock;

             totalStock += stock;
             totalValue += value;

             if(stock>0){
                 inStockCount++;
                 inStockPriceSum += product.getUnitPrice();
             }
             //Agrupamos por category
             groupedByCategory
                     .computeIfAbsent(product.getCategory(),k-> new ArrayList<>()).add(product);
         }
         double averagePrice = inStockCount > 0 ? inStockPriceSum / inStockCount: 0;

         //Metricas por categoria
        Map<String, Map<String, Object>> categoryMetrics = new HashMap<>();
        for(Map.Entry<String, List<Product>> entry : groupedByCategory.entrySet()){
            String category = entry.getKey();
            List<Product> products = entry.getValue();

            int catStock = 0;
            double catValue = 0;
            int catInStockCount = 0;
            double catInStockPriceSum = 0;

            for (Product p : products){
                int stock = p.getQuantityInStock();
                double value = p.getUnitPrice()*stock;

                catStock += stock;
                catValue += value;

                if(stock>0){
                    catInStockCount++;
                    catInStockPriceSum += p.getUnitPrice();
                }
            }
            double avg = catInStockCount > 0 ? catInStockPriceSum / catInStockCount:0;

            Map<String, Object> metrics = new HashMap<>();
            metrics.put("totalStock", catStock);
            metrics.put("totalValue", catValue);
            metrics.put("averagePrice", avg);

            categoryMetrics.put(category, metrics);
        }
        //Respuesta final
        Map<String, Object> response = new HashMap<>();
        response.put("overall", Map.of(
                "totalStock", totalStock,
                "totalValue", totalValue,
                "averagePrice", averagePrice
        ));
        response.put("byCategory", categoryMetrics);

        return response;
    }

}