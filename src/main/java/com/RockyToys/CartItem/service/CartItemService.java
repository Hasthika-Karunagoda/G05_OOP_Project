package com.RockyToys.CartItem.service;

//import com.RockyToys.Cart.service.CartService;
import com.RockyToys.CartItem.dto.CartItemDTO;
import com.RockyToys.CartItem.filehandling.CartItemFileHandler;
import com.RockyToys.CartItem.model.CartItem;
//import com.RockyToys.ToyManagement.model.Toy;
//import com.RockyToys.ToyManagement.service.ToyService;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartItemService {
    private final CartItemFileHandler fileHandler;
   // private final ToyService toyService;
    private final List<CartItem> items = new ArrayList<>();
   // private CartService cartService;

    public CartItemService(CartItemFileHandler fileHandler, ToyService toyService) {
        this.fileHandler = fileHandler;
        //this.toyService = toyService;
    }
    public void setCartService(CartService cartService) {
        this.cartService = cartService;
    }

    @PostConstruct
    public synchronized void load() {
        items.clear();
        items.addAll(fileHandler.readAll());
    }

    public synchronized List<CartItem> getCartItems(String cartId) {
        requireCartId(cartId);
        return items.stream().filter(i -> i.getCartId().equals(cartId)).toList();
    }

    public synchronized CartItem getCartItem(String cartId, String toyId) {
        requireCartId(cartId);
        return items.stream().filter(i -> i.getCartId().equals(cartId) && i.getToyId().equalsIgnoreCase(toyId)).findFirst().orElse(null);
    }

    public synchronized CartItem saveCartItem(String cartId, CartItemDTO dto) {
        requireCartId(cartId);
        if (dto == null || dto.getToyId() == null || dto.getToyId().isBlank()) throw new IllegalArgumentException("Toy ID is required.");
        if (dto.getQuantity() < 1) throw new IllegalArgumentException("Quantity must be at least 1.");

        Toy toy = toyService.readToy(dto.getToyId());
        CartItem existing = getCartItem(cartId, toy.getToyId());
        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + dto.getQuantity());
            existing.calculateTotalAmount();
            persist();

            if (cartService != null) { cartService.load(); }
            return existing;
        }

        CartItem item = new CartItem(cartId, toy.getToyId(), toy.getName(), toy.getPrice(), dto.getQuantity());
        items.add(item);
        persist();

        if (cartService != null) { cartService.load(); }
        return item;
    }



    public synchronized CartItem updateCartItem(String cartId, String toyId, int quantity) {
        requireCartId(cartId);
        if (quantity < 1) throw new IllegalArgumentException("Quantity must be at least 1.");
        CartItem item = getCartItem(cartId, toyId);
        if (item == null) throw new IllegalArgumentException("Toy is not in this cart.");
        item.setQuantity(quantity);
        item.calculateTotalAmount();
        persist();
        return item;
    }

    public synchronized void deleteCartItem(String cartId, String toyId) {
        requireCartId(cartId);
        boolean removed = items.removeIf(i -> i.getCartId().equals(cartId) && i.getToyId().equalsIgnoreCase(toyId));
        if (!removed) throw new IllegalArgumentException("Toy is not in this cart.");
        persist();
    }


    public synchronized void deleteAllCartItems(String cartId) {

        requireCartId(cartId);

        items.removeIf(i ->
                i.getCartId().equalsIgnoreCase(cartId)
        );

        persist();
    }

    public BigDecimal calculateTotalAmount(CartItem item) {
        if (item == null) throw new IllegalArgumentException("Cart item is required.");
        return item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
    }

    private void requireCartId(String cartId) {
        if (cartId == null || cartId.isBlank()) throw new IllegalArgumentException("No cart is associated with this session.");
    }
    private void persist() { fileHandler.writeAll(items); }
}
