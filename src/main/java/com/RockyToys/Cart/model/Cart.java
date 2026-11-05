package com.RockyToys.Cart.model;

import com.RockyToys.CartItem.model.CartItem;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Cart {
    private String cartId;
    private String userId;
    private LocalDateTime date;
    private int totalCartItems;
    private int totalQuantity;
    private BigDecimal subTotal = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    private List<CartItem> cartItemList = new ArrayList<>();
    private BigDecimal discountPercentage = BigDecimal.ZERO;
    private BigDecimal discountAmount = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    private BigDecimal payableAmount = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

    public Cart() {}

    public Cart(String cartId, String userId, LocalDateTime date) {
        this.cartId = cartId;
        this.userId = userId;
        this.date = date == null ? LocalDateTime.now() : date;
    }

    public String getCartId() { return cartId; }
    public void setCartId(String cartId) { this.cartId = cartId; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }
    public int getTotalCartItems() { return totalCartItems; }
    public void setTotalCartItems(int totalCartItems) { this.totalCartItems = totalCartItems; }
    public int getTotalQuantity() { return totalQuantity; }
    public void setTotalQuantity(int totalQuantity) { this.totalQuantity = totalQuantity; }
    public BigDecimal getSubTotal() { return subTotal; }
    public void setSubTotal(BigDecimal subTotal) { this.subTotal = money(subTotal); }
    public List<CartItem> getCartItemList() { return cartItemList; }
    public void setCartItemList(List<CartItem> cartItemList) { this.cartItemList = cartItemList == null ? new ArrayList<>() : new ArrayList<>(cartItemList); }
    public BigDecimal getDiscountPercentage() { return discountPercentage; }
    public void setDiscountPercentage(BigDecimal discountPercentage) { this.discountPercentage = discountPercentage == null ? BigDecimal.ZERO : discountPercentage; }
    public BigDecimal getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = money(discountAmount); }
    public BigDecimal getPayableAmount() { return payableAmount; }
    public void setPayableAmount(BigDecimal payableAmount) { this.payableAmount = money(payableAmount); }

    public void recalculateTotals() {
        totalCartItems = cartItemList.size();
        totalQuantity = cartItemList.stream().mapToInt(CartItem::getQuantity).sum();
        subTotal = cartItemList.stream().map(CartItem::getTotalAmount).reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
        discountAmount = subTotal.multiply(discountPercentage).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        payableAmount = subTotal.subtract(discountAmount).setScale(2, RoundingMode.HALF_UP);
    }

    public String toFileString() {
        if (cartId == null || userId == null || date == null) throw new IllegalStateException("Cart ID, user ID and date are required.");
        return esc(cartId) + "|" + esc(userId) + "|" + date;
    }

    public static Cart fromFileString(String line) {
        String[] fields = line.split("\\|", -1);
        if (fields.length != 3) throw new IllegalArgumentException("Invalid cart record.");
        return new Cart(unesc(fields[0]), unesc(fields[1]), LocalDateTime.parse(fields[2]));
    }

    private static BigDecimal money(BigDecimal value) {
        return (value == null ? BigDecimal.ZERO : value).setScale(2, RoundingMode.HALF_UP);
    }
    private static String esc(String value) { return value.replace("\\", "\\\\").replace("|", "\\p").replace("\n", "\\n").replace("\r", "\\r"); }
    private static String unesc(String value) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (c == '\\' && i + 1 < value.length()) {
                char n = value.charAt(++i);
                result.append(n == 'p' ? '|' : n == 'n' ? '\n' : n == 'r' ? '\r' : n);
            } else result.append(c);
        }
        return result.toString();
    }
}
