package com.RockyToys.Cart.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;

public class DiscountDTO {
    @DecimalMin(value = "0.0", message = "Discount cannot be below 0%.")
    @DecimalMax(value = "100.0", message = "Discount cannot exceed 100%.")
    private BigDecimal discountPercentage = BigDecimal.ZERO;

    public DiscountDTO() {}
    public BigDecimal getDiscountPercentage() { return discountPercentage; }
    public void setDiscountPercentage(BigDecimal discountPercentage) { this.discountPercentage = discountPercentage; }
}
