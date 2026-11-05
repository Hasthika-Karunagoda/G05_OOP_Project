package com.RockyToys.Category.service;

import com.RockyToys.Category.dto.CategoryDTO;
import com.RockyToys.Category.filehandling.CategoryFileHandler;
import com.RockyToys.Category.filehandling.ToyFileHandler;
import com.RockyToys.Category.model.Category;
import com.RockyToys.Category.model.Toy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {
    private final CategoryFileHandler categoryFileHandler;
    private final ToyFileHandler toyFileHandler;

    public CategoryService(CategoryFileHandler categoryFileHandler, ToyFileHandler toyFileHandler) {
        this.categoryFileHandler = categoryFileHandler;
        this.toyFileHandler = toyFileHandler;
    }

    public synchronized List<Category> getAllCategories() {
        return categoryFileHandler.readAll();
    }

    public synchronized Optional<Category> getCategoryById(String id) {
        return getAllCategories().stream()
                .filter(c -> c.getCategoryId().equalsIgnoreCase(id))
                .findFirst();
    }

    public synchronized Category addCategory(CategoryDTO dto) {
        validate(dto);
        List<Category> categories = getAllCategories();
        boolean duplicate = categories.stream()
                .anyMatch(c -> c.getCategoryName().equalsIgnoreCase(dto.getCategoryName().trim()));
        if (duplicate) throw new IllegalArgumentException("Category name already exists.");

        Category category = new Category(categoryFileHandler.nextId(categories),
                dto.getCategoryName(), dto.getDescription());
        categories.add(category);
        categoryFileHandler.writeAll(categories);
        return category;
    }

    public synchronized Category updateCategory(String id, CategoryDTO dto) {
        validate(dto);
        List<Category> categories = getAllCategories();
        Category existing = categories.stream()
                .filter(c -> c.getCategoryId().equalsIgnoreCase(id))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("Category not found: " + id));

        boolean duplicate = categories.stream().anyMatch(c ->
                !c.getCategoryId().equalsIgnoreCase(id)
                && c.getCategoryName().equalsIgnoreCase(dto.getCategoryName().trim()));
        if (duplicate) throw new IllegalArgumentException("Category name already exists.");

        existing.setCategoryName(dto.getCategoryName());
        existing.setDescription(dto.getDescription());
        categoryFileHandler.writeAll(categories);
        return existing;
    }

    public synchronized void deleteCategory(String id) {
        List<Toy> toys = toyFileHandler.readAll();
        boolean used = toys.stream().anyMatch(t -> t.getCategoryId().equalsIgnoreCase(id));
        if (used) throw new IllegalArgumentException("This category is assigned to one or more toys. Update or remove those toys first.");

        List<Category> categories = getAllCategories();
        boolean removed = categories.removeIf(c -> c.getCategoryId().equalsIgnoreCase(id));
        if (!removed) throw new IllegalArgumentException("Category not found: " + id);
        categoryFileHandler.writeAll(categories);
    }

    private void validate(CategoryDTO dto) {
        if (dto == null || dto.getCategoryName() == null || dto.getCategoryName().isBlank())
            throw new IllegalArgumentException("Category name is required.");
        if (dto.getCategoryName().trim().length() > 50)
            throw new IllegalArgumentException("Category name cannot exceed 50 characters.");
        if (dto.getDescription() != null && dto.getDescription().length() > 200)
            throw new IllegalArgumentException("Description cannot exceed 200 characters.");
        if (dto.getCategoryName().contains("|") || (dto.getDescription() != null && dto.getDescription().contains("|")))
            throw new IllegalArgumentException("Category fields cannot contain '|'.");
    }
}
