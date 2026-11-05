package com.RockyToys.Category.model;

public class Category {
    private String categoryId;
    private String categoryName;
    private String description;

    public Category() {}

    public Category(String categoryId, String categoryName, String description) {
        setCategoryId(categoryId);
        setCategoryName(categoryName);
        setDescription(description);
    }

    public String getCategoryId() { return categoryId; }
    public void setCategoryId(String categoryId) {
        if (categoryId == null || categoryId.isBlank()) throw new IllegalArgumentException("Category ID is required.");
        this.categoryId = categoryId.trim();
    }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) {
        if (categoryName == null || categoryName.isBlank()) throw new IllegalArgumentException("Category name is required.");
        if (categoryName.trim().length() > 50) throw new IllegalArgumentException("Category name cannot exceed 50 characters.");
        this.categoryName = categoryName.trim();
    }

    public String getDescription() { return description; }
    public void setDescription(String description) {
        String value = description == null ? "" : description.trim();
        if (value.length() > 200) throw new IllegalArgumentException("Description cannot exceed 200 characters.");
        this.description = value;
    }
}
