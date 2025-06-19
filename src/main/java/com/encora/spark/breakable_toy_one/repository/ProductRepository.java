package com.encora.spark.breakable_toy_one.repository;

import com.encora.spark.breakable_toy_one.model.Product;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class ProductRepository {
    private final Map<UUID, Product> products = new ConcurrentHashMap<>();

    //Save products
    //public void save(Product product){
        //products.put(product.getId(),product)};

    public Product save(Product product){
        if(product.getId() == null){
            product.setId(UUID.randomUUID());
            product.setCreationDate(LocalDateTime.now());
        }
        product.setUpdateDate(LocalDateTime.now());
        products.put(product.getId(), product);
        return product;
    }

    //Search products
    //optional filters
    //public List<Product> findAll(){
        //return new ArrayList<>(products.values());}

    public List<Product> findAll(String name, List<String> categories, Boolean inStock) {
        return products.values().stream()
                .filter(p -> name == null || p.getName().toLowerCase().contains(name.toLowerCase()))
                .filter(p -> categories == null || categories.contains(p.getCategory()))
                .filter(p -> inStock == null ||
                        (inStock ? p.getQuantityInStock() > 0 : p.getQuantityInStock() == 0))
                .collect(Collectors.toList());
    }

    public List<Product> findPaginated(int page, int size, String sortBy, boolean ascending) {
        Comparator<Product> comparator = getComparator(sortBy);
        if (!ascending) comparator = comparator.reversed();

        return products.values().stream()
                .sorted(comparator)
                .skip((long) page * size)
                .limit(size)
                .collect(Collectors.toList());
    }

    public Optional<Product> findById(UUID id){
        return Optional.ofNullable(products.get(id));
    }

    public boolean deleteById(UUID id){
        return products.remove(id) != null;
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

    public void markInStock(UUID id, int quantity) {
        Product p = products.get(id);
        if (p != null) {
            p.setQuantityInStock(quantity);
            p.setUpdateDate(LocalDateTime.now());
        }
    }

    //Inventory Metrics

    public Map<String, Object> getInventoryMetrics() {
        List<Product> allProducts = new ArrayList<>(products.values());

        // Metrics by category
        Map<String, Map<String, Object>> metricsByCategory = allProducts.stream()
                .filter(p -> p.getQuantityInStock() > 0)
                .collect(Collectors.groupingBy(
                        Product::getCategory,
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                list -> {
                                    double totalValue = list.stream()
                                            .mapToDouble(p -> p.getUnitPrice() * p.getQuantityInStock())
                                            .sum();
                                    return Map.of(
                                            "totalProducts", list.size(),
                                            "totalValue", totalValue,
                                            "avgPrice", totalValue / list.size()
                                        );
                                    }
                            )
                    ));

        // Global Metrics
        double totalValue = metricsByCategory.values().stream()
                .mapToDouble(m -> (double) m.get("totalValue"))
                .sum();
        long totalProducts = allProducts.stream()
                .filter(p -> p.getQuantityInStock() > 0)
                .count();
        return Map.of(
                "totalProducts", totalProducts,
                "totalValue", totalValue,
                "avgPrice", totalValue / totalProducts,
                "metricsByCategory", metricsByCategory
            );
        }
    //Eliminate all products
    public void deleteAll() {
            products.clear();
        }

        //Help private methods
        private Comparator<Product> getComparator(String sortBy) {
            return switch (sortBy != null ? sortBy.toLowerCase() : "name") {
                case "price" -> Comparator.comparing(Product::getUnitPrice);
                case "stock" -> Comparator.comparing(Product::getQuantityInStock);
                case "date" -> Comparator.comparing(Product::getCreationDate);
                default -> Comparator.comparing(Product::getName);
            };
        }
    }
