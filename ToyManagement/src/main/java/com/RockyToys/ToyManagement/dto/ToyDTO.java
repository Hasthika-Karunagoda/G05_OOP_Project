package com.RockyToys.ToyManagement.dto;

import java.math.BigDecimal;

public class ToyDTO {
    private String toyId;
    private String name;
    private String description;
    private String categoryId;
    private BigDecimal price;
    private String ageGroup;

    public ToyDTO() {}
    public ToyDTO(String toyId, String name, String description, String categoryId, BigDecimal price, String ageGroup) {
        this.toyId = toyId; this.name = name; this.description = description;
        this.categoryId = categoryId; this.price = price; this.ageGroup = ageGroup;
    }
    public String getToyId() { return toyId; }
    public void setToyId(String toyId) { this.toyId = toyId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCategoryId() { return categoryId; }
    public void setCategoryId(String categoryId) { this.categoryId = categoryId; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public String getAgeGroup() { return ageGroup; }
    public void setAgeGroup(String ageGroup) { this.ageGroup = ageGroup; }
}
