package com.RockyToys.Cart.controller;

import com.RockyToys.Cart.dto.DiscountDTO;
import com.RockyToys.Cart.model.Cart;
import com.RockyToys.Cart.service.CartService;
import com.RockyToys.User.model.User;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.lang.reflect.Method;
import java.math.BigDecimal;

@Controller
@RequestMapping
public class CartController {
    private final CartService cartService;

    public CartController(CartService cartService) { this.cartService = cartService; }

    @GetMapping("/cart")
    public String getCartById(HttpSession session, Model model,
                              RedirectAttributes redirect) {

        User user = (User) session.getAttribute("loggedInUser");

        if (user == null) {
            return "redirect:/users/login";
        }

        String userId = user.getUserId();

        if (userId == null || userId.isBlank()) {
            redirect.addFlashAttribute("error",
                    "Your account ID could not be identified. Please log in again.");
            return "redirect:/users/login";
        }

        try {
            Cart cart = cartService.getOrCreateCartForUser(userId);

            session.setAttribute("cartId", cart.getCartId());

            Object discountValue = session.getAttribute("cartDiscountPercentage");

            BigDecimal discount = discountValue instanceof BigDecimal value
                    ? value
                    : BigDecimal.ZERO;

            cart.setDiscountPercentage(discount);
            cart.recalculateTotals();

            model.addAttribute("cart", cart);
            model.addAttribute("discountDTO", new DiscountDTO());

            return "carts";

        } catch (RuntimeException ex) {
            redirect.addFlashAttribute("error", ex.getMessage());
            return "redirect:/toys";
        }
    }

    @PostMapping("/cart/discount")
    public String calculateDiscount(@Valid @ModelAttribute DiscountDTO dto, BindingResult result,
                                    HttpSession session, RedirectAttributes redirect) {
        if (session.getAttribute("loggedInUser") == null) return "redirect:/users/login";
        if (result.hasErrors()) {
            redirect.addFlashAttribute("error", result.getFieldError() == null ? "Invalid discount." : result.getFieldError().getDefaultMessage());
            return "redirect:/carts";
        }
        try {
            Object cartId = session.getAttribute("cartId");
            if (!(cartId instanceof String id) || id.isBlank()) return "redirect:/cart";
            Cart cart = cartService.getCartById(id);
            cartService.calculateDiscount(cart.getSubTotal(), dto.getDiscountPercentage());
            session.setAttribute("cartDiscountPercentage", dto.getDiscountPercentage());
            redirect.addFlashAttribute("message", "Discount calculated.");
        } catch (RuntimeException ex) {
            redirect.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/carts";
    }

    @PostMapping("/admin/carts/delete/{id}")
    public String deleteCart(@org.springframework.web.bind.annotation.PathVariable("id") String cartId,
                                  HttpSession session,
                                  RedirectAttributes redirect) {

        if (getLoggedInUser(session) == null) {
            return "redirect:/users/login";
        }
        if (!isAdmin(session)) {
            redirect.addFlashAttribute("error", "Access Denied: Administrative privileges required.");
            return "redirect:/cart";
        }

        try {
            cartService.deleteCart(cartId);
            redirect.addFlashAttribute("message", "Cart " + cartId + " cleared successfully.");
        } catch (RuntimeException ex) {
            redirect.addFlashAttribute("error", "Could not delete cart: " + ex.getMessage());
        }
        return "redirect:/admin/carts";
    }

    @GetMapping("/admin/carts")
    public String getAllCarts(HttpSession session, Model model) {
        User user = getLoggedInUser(session);

        if (user == null) {
            return "redirect:/users/login";
        }

        if (!isAdmin(session)) {
            return "redirect:/carts";
        }

        model.addAttribute("carts", cartService.getAllCarts());

        return "admin/AdminCarts";
    }

    private String readStringProperty(Object target, String getterName) {
        try {
            Method getter = target.getClass().getMethod(getterName);
            Object value = getter.invoke(target);
            return value instanceof String text ? text : null;
        } catch (ReflectiveOperationException ex) {
            return null;
        }
    }

    private User getLoggedInUser(HttpSession session) {
        return (User) session.getAttribute("loggedInUser");
    }

    private boolean isAdmin(HttpSession session) {
        User user = getLoggedInUser(session);

        return user != null && "ADMIN".equals(user.getRole());
    }
}
