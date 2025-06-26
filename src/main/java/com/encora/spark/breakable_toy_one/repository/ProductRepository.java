package com.encora.spark.breakable_toy_one.repository;

import com.encora.spark.breakable_toy_one.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ProductRepository extends JpaRepository <Product, Integer>, JpaSpecificationExecutor<Product> {
    List<Product> findByNameLike(String name);
    List<Product> findByNameContaining(String name, Pageable pageable);
    List<Product> findByNameLikeAndQuantityInStockGreaterThan(String name, int quantityInStock, Pageable pageable);

}
