package com.RockyToys.Category.filehandling;

import com.RockyToys.Category.model.Category;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Component
public class CategoryFileHandler {
    private final Path filePath = Path.of("data", "categories.txt");

    public synchronized List<Category> readAll() {
        List<Category> categories = new ArrayList<>();
        if (!Files.exists(filePath)) return categories;
        try {
            int lineNumber = 0;
            for (String line : Files.readAllLines(filePath, StandardCharsets.UTF_8)) {
                lineNumber++;
                if (line.isBlank() || line.stripLeading().startsWith("#")) continue;
                String[] fields = line.split("\\|", -1);
                if (fields.length != 3) {
                    System.err.println("Skipping malformed category record at line " + lineNumber);
                    continue;
                }
                try {
                    categories.add(new Category(fields[0], fields[1], fields[2]));
                } catch (RuntimeException ex) {
                    System.err.println("Skipping invalid category record at line " + lineNumber);
                }
            }
            return categories;
        } catch (IOException e) {
            throw new IllegalStateException("Could not read categories.txt.", e);
        }
    }

    public synchronized void writeAll(List<Category> categories) {
        try {
            Files.createDirectories(filePath.getParent());
            List<String> lines = new ArrayList<>();
            for (Category c : categories) {
                lines.add(String.join("|", safe(c.getCategoryId()), safe(c.getCategoryName()), safe(c.getDescription())));
            }
            Files.write(filePath, lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Could not save categories.txt.", e);
        }
    }

    public boolean categoryExists(String categoryId) {
        if (categoryId == null || categoryId.isBlank()) return false;
        return readAll().stream().anyMatch(c -> c.getCategoryId().equalsIgnoreCase(categoryId.trim()));
    }

    public String nextId(List<Category> categories) {
        int max = categories.stream().map(Category::getCategoryId)
                .filter(id -> id.startsWith("CG"))
                .mapToInt(id -> {
                    try { return Integer.parseInt(id.substring(2)); }
                    catch (NumberFormatException e) { return 0; }
                }).max().orElse(0);
        return String.format("CG%03d", max + 1);
    }

    private String safe(String value) {
        if (value != null && value.contains("|")) {
            throw new IllegalArgumentException("Category fields cannot contain '|'.");
        }
        return value == null ? "" : value;
    }
}
