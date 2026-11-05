package com.RockyToys.ToyManagement.controller;

import com.RockyToys.Store.ToyManagement.dto.ToyDTO;
import com.RockyToys.Store.ToyManagement.service.ToyService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;

import com.RockyToys.Store.User.model.User;

@Controller
@RequestMapping("/toys")
public class ToyController {
    private final ToyService toyService;
    public ToyController(ToyService toyService) { this.toyService = toyService; }

    @GetMapping
    public String list(@RequestParam(required = false) String q,
                       @RequestParam(required = false) String categoryId, Model model, HttpSession session) {
        model.addAttribute("toys", categoryId != null && !categoryId.isBlank()
            ? toyService.getToysByCategory(categoryId)
            : toyService.searchToy(q));
        model.addAttribute("q", q == null ? "" : q);
        model.addAttribute("categoryId", categoryId == null ? "" : categoryId);
        model.addAttribute("isAdmin", isAdmin(session));
        return "toys";
    }

    @GetMapping("/{id}")
    public String details(@PathVariable("id") String id, Model model, RedirectAttributes redirect, HttpSession session) {
        try {
            model.addAttribute("toy", toyService.readToy(id));
            model.addAttribute("isAdmin", isAdmin(session));
            return "toy-details";
        } catch (IllegalArgumentException e) {
            redirect.addFlashAttribute("error", e.getMessage());
            return "redirect:/toys";
        }
    }

    @GetMapping("/sort")
    public String sort(Model model) {
        model.addAttribute("toys", toyService.sortToysByAgeGroup());
        model.addAttribute("q", "");
        model.addAttribute("categoryId", "");
        model.addAttribute("sorted", true);
        return "toys";
    }

    @PostMapping("/add")
    public String add(@RequestParam String toyId, @RequestParam String name,
        @RequestParam(required = false, defaultValue = "") String description,
        @RequestParam String categoryId, @RequestParam BigDecimal price,
        @RequestParam String ageGroup, RedirectAttributes redirect, HttpSession session) {

        if (!isAdmin(session)) {
            return deny(redirect);
        }

        try {
            toyService.addToy(new ToyDTO(toyId, name, description, categoryId, price, ageGroup));
            redirect.addFlashAttribute("message", "Toy added successfully.");
        } catch (RuntimeException e) { redirect.addFlashAttribute("error", e.getMessage()); }
        return "redirect:/toys";
    }

    @PostMapping("/update/{id}")
    public String update(@PathVariable("id") String id, @RequestParam String name,
        @RequestParam(required = false, defaultValue = "") String description,
        @RequestParam String categoryId, @RequestParam BigDecimal price,
        @RequestParam String ageGroup, RedirectAttributes redirect, HttpSession session) {

        if (!isAdmin(session)) {
            return deny(redirect);
        }

        try {
            toyService.updateToy(id, new ToyDTO(id, name, description, categoryId, price, ageGroup));
            redirect.addFlashAttribute("message", "Toy updated successfully.");
        } catch (RuntimeException e) { redirect.addFlashAttribute("error", e.getMessage()); }
        return "redirect:/toys";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable("id") String id, RedirectAttributes redirect, HttpSession session) {

        if (!isAdmin(session)) {
           return deny(redirect);
       }

        try {
            toyService.deleteToy(id);
            redirect.addFlashAttribute("message", "Toy deleted successfully.");
        } catch (RuntimeException e) { redirect.addFlashAttribute("error", e.getMessage()); }
        return "redirect:/toys";
    }


    private boolean isAdmin(HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        return user != null && "ADMIN".equals(user.getRole());
    }

    private String deny(RedirectAttributes redirect) {
        redirect.addFlashAttribute(
                "error",
                "Only administrators can modify toys."
        );
        return "redirect:/toys";
    }


}




