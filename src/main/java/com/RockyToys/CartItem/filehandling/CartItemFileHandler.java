package com.RockyToys.CartItem.filehandling;

import com.RockyToys.CartItem.model.CartItem;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Component
public class CartItemFileHandler {
    private final Path filePath = Path.of("data", "cart_items.txt");

    public synchronized List<CartItem> readAll() {
        List<CartItem> items = new ArrayList<>();
        if (!Files.exists(filePath)) return items;
        try {
            int lineNo = 0;
            for (String line : Files.readAllLines(filePath, StandardCharsets.UTF_8)) {
                lineNo++;
                if (line.isBlank() || line.stripLeading().startsWith("#")) continue;
                try { items.add(CartItem.fromFileString(line)); }
                catch (RuntimeException e) { System.err.println("Skipping invalid cart item line " + lineNo + ": " + e.getMessage()); }
            }
            return items;
        } catch (IOException e) {
            throw new IllegalStateException("Could not read data/cart_items.txt.", e);
        }
    }

    public synchronized void writeAll(List<CartItem> items) {
        try {
            Files.createDirectories(filePath.getParent());
            List<String> lines = items.stream().map(CartItem::toFileString).toList();
            Files.write(filePath, lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Could not save data/cart_items.txt.", e);
        }
    }
}
