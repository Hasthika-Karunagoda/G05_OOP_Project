package com.RockyToys.ToyManagement.filehandling;

import com.RockyToys.ToyManagement.model.Toy;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Component
public class ToyFileHandler {
    private final Path filePath = Path.of("data", "toys.txt");

    public List<Toy> readAll() {
        List<Toy> toys = new ArrayList<>();
        if (!Files.exists(filePath)) return toys;
        try {
            int lineNumber = 0;
            for (String line : Files.readAllLines(filePath, StandardCharsets.UTF_8)) {
                lineNumber++;
                if (line.isBlank() || line.stripLeading().startsWith("#")) continue;
                String[] f = line.split("\\|", -1);
                // Current format: toyId|name|description|categoryId|price|ageGroup
                if (f.length != 6) {
                    // Ignore old data belonging to a different model instead of crashing startup.
                    System.err.println("Skipping malformed toy record at line " + lineNumber);
                    continue;
                }
                try {
                    toys.add(new Toy(f[0], f[1], f[2], f[3], new BigDecimal(f[4]), f[5]));
                } catch (RuntimeException ex) {
                    System.err.println("Skipping invalid toy record at line " + lineNumber + ": " + ex.getMessage());
                }
            }
            return toys;
        } catch (IOException e) {
            throw new IllegalStateException("Could not read toys.txt.", e);
        }
    }

    public void writeAll(List<Toy> toys) {
        try {
            Files.createDirectories(filePath.getParent());
            List<String> lines = new ArrayList<>();
            for (Toy t : toys) {
                lines.add(String.join("|", safe(t.getToyId()), safe(t.getName()), safe(t.getDescription()),
                    safe(t.getCategoryId()), t.getPrice().toPlainString(), safe(t.getAgeGroup())));
            }
            Files.write(filePath, lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Could not save toys.txt.", e);
        }
    }

    private String safe(String value) {
        if (value != null && value.contains("|")) throw new IllegalArgumentException("Toy fields cannot contain '|'.");
        return value == null ? "" : value;
    }
}
