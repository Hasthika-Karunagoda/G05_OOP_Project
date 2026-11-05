package com.RockyToys.Cart.filehandling;

import com.RockyToys.Cart.model.Cart;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Component
public class CartFileHandler {
    private final Path filePath = Path.of("data", "carts.txt");

    public synchronized List<Cart> readAll() {
        List<Cart> carts = new ArrayList<>();
        if (!Files.exists(filePath)) return carts;
        try {
            int lineNo = 0;
            for (String line : Files.readAllLines(filePath, StandardCharsets.UTF_8)) {
                lineNo++;
                if (line.isBlank() || line.stripLeading().startsWith("#")) continue;
                try { carts.add(Cart.fromFileString(line)); }
                catch (RuntimeException ex) { System.err.println("Skipping invalid cart record at line " + lineNo + ": " + ex.getMessage()); }
            }
            return carts;
        } catch (IOException ex) {
            throw new IllegalStateException("Could not read data/carts.txt.", ex);
        }
    }

    public synchronized void writeAll(List<Cart> carts) {
        try {
            Files.createDirectories(filePath.getParent());
            List<String> lines = carts.stream().map(Cart::toFileString).toList();
            Files.write(filePath, lines, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            throw new IllegalStateException("Could not save data/carts.txt.", ex);
        }
    }
}
