package com.RockyToys.Cart.dsa;

import com.RockyToys.CartItem.model.CartItem;
import java.util.ArrayList;
import java.util.List;

public class CartLinkedList {
    private CartNode head;
    private CartNode tail;
    private int size;

    public void add(CartItem item) {
        CartNode node = new CartNode(item);
        if (head == null) head = tail = node;
        else { tail.next = node; tail = node; }
        size++;
    }

    public CartItem find(String cartId, String toyId) {
        for (CartNode current = head; current != null; current = current.next) {
            CartItem item = current.data;
            if (item.getCartId().equals(cartId) && item.getToyId().equalsIgnoreCase(toyId)) return item;
        }
        return null;
    }

    public boolean remove(String cartId, String toyId) {
        CartNode previous = null;
        for (CartNode current = head; current != null; previous = current, current = current.next) {
            if (current.data.getCartId().equals(cartId) && current.data.getToyId().equalsIgnoreCase(toyId)) {
                if (previous == null) head = current.next;
                else previous.next = current.next;
                if (current == tail) tail = previous;
                size--;
                return true;
            }
        }
        return false;
    }

    public int size() { return size; }
    public List<CartItem> toList() {
        List<CartItem> result = new ArrayList<>();
        for (CartNode current = head; current != null; current = current.next) result.add(current.data);
        return result;
    }
}
