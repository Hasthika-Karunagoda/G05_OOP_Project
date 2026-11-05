package com.RockyToys.ToyManagement.filehandling;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;


@Component
public class CategoryFileHandler {
    private final Path filePath = Path.of("data", "categories.txt");

    public boolean categoryExists(String categoryId) {
        if (categoryId == null || categoryId.isBlank() || !Files.exists(filePath)) return false;
        try {
            return Files.readAllLines(filePath).stream()
                .filter(line -> !line.isBlank() && !line.stripLeading().startsWith("#"))
                .map(line -> line.split("\\|", -1)[0].trim())
                .anyMatch(id -> id.equalsIgnoreCase(categoryId.trim()));
        } catch (IOException e) {
            throw new IllegalStateException("Could not read categories.txt.", e);
        }
    }
}
