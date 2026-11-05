package com.RockyToys.CartItem.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class CartItem {
    private String cartId;
    private String toyId;
    private String toyName;
    private BigDecimal unitPrice;
    private int quantity;
    private BigDecimal totalAmount;

    public CartItem() {}

    public CartItem(String cartId, String toyId, String toyName,
                    BigDecimal unitPrice, int quantity) {
        setCartId(cartId);
        setToyId(toyId);
        setToyName(toyName);
        setUnitPrice(unitPrice);
        setQuantity(quantity);
        calculateTotalAmount();
    }

    public String getCartId() { return cartId; }
    public void setCartId(String cartId) {
        if (cartId == null || cartId.isBlank()) throw new IllegalArgumentException("Cart ID is required.");
        this.cartId = cartId.trim();
    }
    public String getToyId() { return toyId; }
    public void setToyId(String toyId) {
        if (toyId == null || toyId.isBlank()) throw new IllegalArgumentException("Toy ID is required.");
        this.toyId = toyId.trim();
    }
    public String getToyName() { return toyName; }
    public void setToyName(String toyName) { this.toyName = toyName == null ? "" : toyName.trim(); }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) {
        if (unitPrice == null || unitPrice.signum() < 0) throw new IllegalArgumentException("Unit price must be non-negative.");
        this.unitPrice = unitPrice.setScale(2, RoundingMode.HALF_UP);
    }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) {
        if (quantity < 1) throw new IllegalArgumentException("Quantity must be at least 1.");
        this.quantity = quantity;
        if (unitPrice != null) calculateTotalAmount();
    }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public void calculateTotalAmount() {
        if (unitPrice != null && quantity >= 1) totalAmount = unitPrice.multiply(BigDecimal.valueOf(quantity)).setScale(2, RoundingMode.HALF_UP);
    }

    public String toFileString() {
        return esc(cartId) + "|" + esc(toyId) + "|" + esc(toyName) + "|" + unitPrice.toPlainString()
                + "|" + quantity + "|" + totalAmount.toPlainString();
    }

    public static CartItem fromFileString(String line) {
        java.util.List<String> fields = splitEscaped(line);
        if (fields.size() != 6) throw new IllegalArgumentException("Invalid cart item record.");
        String[] p = fields.toArray(new String[0]);
        CartItem item = new CartItem(unesc(p[0]), unesc(p[1]), unesc(p[2]), new BigDecimal(p[3]), Integer.parseInt(p[4]));
        item.calculateTotalAmount(); // total is always recalculated, not trusted from the file
        return item;
    }

    private static java.util.List<String> splitEscaped(String line) {
        java.util.List<String> fields = new java.util.ArrayList<>();
        StringBuilder field = new StringBuilder();
        boolean escaped = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (escaped) {
                field.append('\\').append(c);
                escaped = false;
            } else if (c == '\\') {
                escaped = true;
            } else if (c == '|') {
                fields.add(field.toString());
                field.setLength(0);
            } else {
                field.append(c);
            }
        }
        if (escaped) field.append('\\');
        fields.add(field.toString());
        return fields;
    }

    private static String esc(String value) {
        return value.replace("\\", "\\\\").replace("|", "\\p").replace("\n", "\\n").replace("\r", "\\r");
    }
    private static String unesc(String value) {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (c == '\\' && i + 1 < value.length()) {
                char n = value.charAt(++i);
                out.append(n == 'p' ? '|' : n == 'n' ? '\n' : n == 'r' ? '\r' : n);
            } else out.append(c);
        }
        return out.toString();
    }
}
