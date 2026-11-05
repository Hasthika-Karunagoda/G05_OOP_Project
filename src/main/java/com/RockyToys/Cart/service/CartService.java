package com.RockyToys.Cart.service;

import com.RockyToys.Cart.dsa.CartLinkedList;
import com.RockyToys.Cart.filehandling.CartFileHandler;
import com.RockyToys.Cart.model.Cart;
//import com.RockyToys.CartItem.model.CartItem;
//import com.RockyToys.CartItem.service.CartItemService;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class CartService {
    private final CartFileHandler fileHandler;
   // private final CartItemService cartItemService;
    private final List<Cart> carts = new ArrayList<>();

    public CartService(CartFileHandler fileHandler, /**CartItemService cartItemService**/) {
       this.fileHandler = fileHandler;
   //     this.cartItemService = cartItemService;
    }

    @PostConstruct
    public synchronized void load() {
        carts.clear();
        carts.addAll(fileHandler.readAll());
    }

    public synchronized List<Cart> getAllCarts() {
        return carts.stream().map(c -> enrich(c, BigDecimal.ZERO)).toList();
    }

    public synchronized Cart getCartById(String cartId) {
        Cart cart = findById(cartId);

        if (cart == null) {
            throw new IllegalArgumentException("Cart not found.");
        }

        return enrich(cart, cart.getDiscountPercentage());
    }

    public synchronized Cart getOrCreateCartForUser(String userId) {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException(
                    "A logged-in user is required.");
        }

        Cart existing = carts.stream()
                .filter(c -> userId.equals(c.getUserId()))
                .findFirst()
                .orElse(null);

        if (existing != null) {
            return enrich(existing, existing.getDiscountPercentage());
        }

        Cart cart = new Cart(
                nextCartId(),
                userId,
                LocalDateTime.now()
        );

        carts.add(cart);
        persist();

        return enrich(cart, BigDecimal.ZERO);
    }

    public synchronized Cart saveCart(Cart cart) {
        if (cart == null || cart.getUserId() == null || cart.getUserId().isBlank())
            throw new IllegalArgumentException("Cart and user ID are required.");
        if (cart.getCartId() == null || cart.getCartId().isBlank()) cart.setCartId(nextCartId());
        if (cart.getDate() == null) cart.setDate(LocalDateTime.now());
        int index = findIndex(cart.getCartId());
        if (index >= 0) carts.set(index, cart); else carts.add(cart);
        persist();
        return enrich(cart, cart.getDiscountPercentage());
    }

    public synchronized Cart updateCart(String cartId, Cart updatedCart) {
        Cart current = findById(cartId);
        if (updatedCart == null) throw new IllegalArgumentException("Cart details are required.");
        current.setDate(LocalDateTime.now());
        if (updatedCart.getDiscountPercentage() != null) current.setDiscountPercentage(updatedCart.getDiscountPercentage());
        persist();
        return enrich(current, current.getDiscountPercentage());
    }

    public synchronized void deleteCart(String cartId) {

        if (cartId == null || cartId.isBlank()) {
            throw new IllegalArgumentException("Cart ID is required.");
        }

        Cart cart = findById(cartId);

        // Delete all cart items first
        cartItemService.deleteAllCartItems(cart.getCartId());

        // Remove the cart
        carts.removeIf(c ->
                c.getCartId().equalsIgnoreCase(cart.getCartId())
        );

        // Save the updated cart list
        persist();
    }

    public BigDecimal calculateSubTotal(Cart cart) {
        if (cart == null) throw new IllegalArgumentException("Cart is required.");
        return cart.getCartItemList().stream().map(CartItem::getTotalAmount).reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal calculateDiscount(BigDecimal subTotal, BigDecimal percentage) {
        if (subTotal == null || percentage == null) throw new IllegalArgumentException("Subtotal and discount are required.");
        if (percentage.compareTo(BigDecimal.ZERO) < 0 || percentage.compareTo(new BigDecimal("100")) > 0)
            throw new IllegalArgumentException("Discount must be between 0 and 100 percent.");
        return subTotal.multiply(percentage).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
    }

    private Cart enrich(Cart stored, BigDecimal percentage) {
        Cart cart = new Cart(stored.getCartId(), stored.getUserId(), stored.getDate());
        cart.setDiscountPercentage(percentage);
        CartLinkedList linkedList = new CartLinkedList();
        for (CartItem item : cartItemService.getCartItems(stored.getCartId())) linkedList.add(item);
        cart.setCartItemList(linkedList.toList());
        cart.setSubTotal(calculateSubTotal(cart));
        cart.recalculateTotals();
        cart.setDiscountAmount(calculateDiscount(cart.getSubTotal(), percentage));
        cart.setPayableAmount(cart.getSubTotal().subtract(cart.getDiscountAmount()).setScale(2, RoundingMode.HALF_UP));
        return cart;
    }

    private Cart findById(String cartId) {
        int index = findIndex(cartId);
        if (index < 0) throw new IllegalArgumentException("Cart not found: " + cartId);
        return carts.get(index);
    }
    private int findIndex(String cartId) {
        if (cartId == null) return -1;
        for (int i = 0; i < carts.size(); i++) if (carts.get(i).getCartId().equalsIgnoreCase(cartId)) return i;
        return -1;
    }
    private String nextCartId() {
        int max = carts.stream().map(Cart::getCartId).filter(id -> id != null && id.matches("CR\\d+"))
                .map(id -> Integer.parseInt(id.substring(2))).max(Comparator.naturalOrder()).orElse(0);
        return String.format("CR%03d", max + 1);
    }
    private void persist() { fileHandler.writeAll(carts); }
}
