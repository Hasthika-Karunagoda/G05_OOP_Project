package com.RockyToys.CartItem.controller;

import com.RockyToys.Cart.model.Cart;
import com.RockyToys.Cart.service.CartService;
import com.RockyToys.CartItem.dto.CartItemDTO;
import com.RockyToys.CartItem.service.CartItemService;
import com.RockyToys.User.model.User;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/cart/items")
public class CartItemController {
    private final CartItemService cartItemService;
    private final CartService cartService;

    public CartItemController(CartItemService cartItemService, CartService cartService) {
        this.cartItemService = cartItemService;
        this.cartService = cartService;
    }

    @GetMapping
    public String list(HttpSession session, Model model, RedirectAttributes redirect) {
        if (!isLoggedIn(session)) return "redirect:/users/login";
        String cartId = getCartId(session);
        if (cartId == null) {
            redirect.addFlashAttribute("error", "Your cart has not been created yet.");
            return "redirect:/cart";
        }
        model.addAttribute("cartItems", cartItemService.getCartItems(cartId));
        return "fragments/cart-items :: cartItems";
    }

    @PostMapping("/add")
    public String add(@Valid @ModelAttribute CartItemDTO cartItemDTO, BindingResult result, HttpSession session, RedirectAttributes redirect) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            redirect.addFlashAttribute("error", "Please log in before using your cart.");
            return "redirect:/users/login";
        }

        if (result.hasErrors()) {
            redirect.addFlashAttribute("error", result.getFieldError() == null ? "Invalid cart item." : result.getFieldError().getDefaultMessage());
            return "redirect:/toys";
        }
        try {

            Cart cart = cartService.getOrCreateCartForUser(user.getUserId());

            session.setAttribute("cartId", cart.getCartId());

            cartItemService.saveCartItem(cart.getCartId(), cartItemDTO);
            redirect.addFlashAttribute("message", "Toy added to your cart.");
        } catch (RuntimeException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/cart";
    }

    @PostMapping("/{toyId}/update")
    public String update(@PathVariable String toyId, @ModelAttribute CartItemDTO dto, HttpSession session, RedirectAttributes redirect) {
        String cartId = requireCart(session, redirect);
        if (cartId == null) return isLoggedIn(session) ? "redirect:/cart" : "redirect:/users/login";
        try {
            cartItemService.updateCartItem(cartId, toyId, dto.getQuantity());
            redirect.addFlashAttribute("message", "Cart quantity updated.");
        } catch (RuntimeException e) { redirect.addFlashAttribute("error", e.getMessage()); }
        return "redirect:/cart";
    }

    @PostMapping("/{toyId}/delete")
    public String delete(@PathVariable String toyId, HttpSession session, RedirectAttributes redirect) {
        String cartId = requireCart(session, redirect);
        if (cartId == null) return isLoggedIn(session) ? "redirect:/cart" : "redirect:/users/login";

        try {
            cartItemService.deleteCartItem(cartId, toyId);
            redirect.addFlashAttribute("message", "Item removed from your cart.");
        } catch (RuntimeException e) { redirect.addFlashAttribute("error", e.getMessage()); }
        return "redirect:/cart";
    }

    private boolean isLoggedIn(HttpSession session) { return session.getAttribute("loggedInUser") != null; }

    private String getCartId(HttpSession session) {
        if (session == null) return null;
        Object value = session.getAttribute("cartId");
        return value instanceof String ? (String) value : null;
    }

    private String requireCart(HttpSession session, RedirectAttributes redirect) {
        if (!isLoggedIn(session)) {
            redirect.addFlashAttribute("error", "Please log in before using your cart.");
            return null;
        }
        String cartId = getCartId(session);
        if (cartId == null || cartId.isBlank()) {
            redirect.addFlashAttribute("error", "Your cart has not been created yet.");
            return null;
        }
        return cartId;
    }
}
