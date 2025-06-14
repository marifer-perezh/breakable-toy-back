package com.encora.spark.breakable_toy_one.model;

import org.springframework.cglib.core.Local;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class Product {
    //Declare variables
    private UUID id;
    private String name;
    private String category;
    private double unitPrice;
    private LocalDate expirationDate;
    private int quantityInStock;
    private LocalDateTime creationDate;
    private LocalDateTime updateDate;

    //Empty constructor (Spring asks for this)
    public Product(){}

    //Constructor completo
    public Product(UUID id, String name, String category, double unitPrice,
                   LocalDate expirationDate,int quantityInStock,
                   LocalDateTime creationDate, LocalDateTime updateDate){
        this.id = id;
        this.name = name;
        this.category = category;
        this.unitPrice = unitPrice;
        this.expirationDate = expirationDate;
        this.quantityInStock = quantityInStock;
        this.creationDate = creationDate;
        this.updateDate = updateDate;
    }

    //Getters y Setters
    public UUID getId(){
        return id;
    }
    public void setId(UUID id){
        this.id = id;
    }

    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }

    public String getCategory(){
        return category;
    }
    public void setCategory(String category){
        this.category = category;
    }

    public double getUnitPrice(){
        return unitPrice;
    }
    public void setUnitPrice(double unitPrice){
        this.unitPrice = unitPrice;
    }

    public LocalDate getExpirationDate(){
        return expirationDate;
    }
    public void setExpirationDate(LocalDate expirationDate){
        this.expirationDate = expirationDate;
    }

    public int getQuantityInStock(){
        return quantityInStock;
    }
    public void setQuantityInStock(int quantityInStock){
        this.quantityInStock = quantityInStock;
    }

    public LocalDateTime getCreationDate(){
        return creationDate;
    }
    public void setCreationDate(LocalDateTime creationDate){
        this.creationDate = creationDate;
    }

    public LocalDateTime getUpdateDate(){
        return updateDate;
    }
    public void setUpdateDate(LocalDateTime updateDate){
        this.updateDate = updateDate;
    }
}
