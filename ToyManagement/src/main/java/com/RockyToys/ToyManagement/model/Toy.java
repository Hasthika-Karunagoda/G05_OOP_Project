package com.RockyToys.ToyManagement.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Toy {
    private String toyId;
    private String name;
    private String description;
    private String categoryId;
    private BigDecimal price;
    private String ageGroup;

    public Toy() {}

    public Toy(String toyId, String name, String description, String categoryId,
               BigDecimal price, String ageGroup) {
        setToyId(toyId);
        setName(name);
        setDescription(description);
        setCategoryId(categoryId);
        setPrice(price);
        setAgeGroup(ageGroup);
    }

    public String getToyId() { return toyId; }
    public void setToyId(String toyId) {
        if (toyId == null || toyId.isBlank()) throw new IllegalArgumentException("Toy ID is required.");
        this.toyId = toyId.trim();
    }
    public String getName() { return name; }
    public void setName(String name) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Toy name is required.");
        this.name = name.trim();
    }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description == null ? "" : description.trim(); }
    public String getCategoryId() { return categoryId; }
    public void setCategoryId(String categoryId) {
        if (categoryId == null || categoryId.isBlank()) throw new IllegalArgumentException("Category ID is required.");
        this.categoryId = categoryId.trim();
    }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) {
        if (price == null || price.signum() < 0) throw new IllegalArgumentException("Price must be non-negative.");
        this.price = price.setScale(2, RoundingMode.HALF_UP);
    }
    public String getAgeGroup() { return ageGroup; }
    public void setAgeGroup(String ageGroup) {
        if (ageGroup == null || ageGroup.isBlank()) throw new IllegalArgumentException("Age group is required.");
        this.ageGroup = ageGroup.trim();
    }
}
