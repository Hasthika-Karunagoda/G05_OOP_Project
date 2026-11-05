package com.RockyToys.CartItem.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class CartItemDTO {
    @NotBlank(message = "Toy ID is required.")
    private String toyId;

    @Min(value = 1, message = "Quantity must be at least 1.")
    private int quantity = 1;

    public CartItemDTO() {}
    public CartItemDTO(String toyId, int quantity) { this.toyId = toyId; this.quantity = quantity; }
    public String getToyId() { return toyId; }
    public void setToyId(String toyId) { this.toyId = toyId; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}
