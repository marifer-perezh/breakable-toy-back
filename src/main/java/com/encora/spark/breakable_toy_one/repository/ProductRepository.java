package com.encora.spark.breakable_toy_one.repository;


import com.encora.spark.breakable_toy_one.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository <Product, Integer> {

}
