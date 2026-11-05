package com.RockyToys.Category.controller;

import com.RockyToys.Category.dto.CategoryDTO;
import com.RockyToys.Category.service.CategoryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {

        this.categoryService = categoryService;
    }

    @GetMapping("/categories")
    public String listCategories(Model model) {
        model.addAttribute("categories", categoryService.getAllCategories());
        return "categories";
    }

    @GetMapping("/admin/categories")
    public String manageCategories(Model model) {
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("categoryDTO", new CategoryDTO());
        return "admin/manage-categories";
    }

    @PostMapping("/admin/categories/add")
    public String addCategory(@RequestParam String categoryName,
                              @RequestParam(required = false, defaultValue = "") String description,
                              RedirectAttributes redirect) {
        try {
            categoryService.addCategory(new CategoryDTO(categoryName, description));
            redirect.addFlashAttribute("message", "Category added successfully.");
        } catch (RuntimeException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/categories";
    }

    @PostMapping("/admin/categories/update/{id}")
    public String updateCategory(@PathVariable String id,
                                 @RequestParam String categoryName,
                                 @RequestParam(required = false, defaultValue = "") String description,
                                 RedirectAttributes redirect) {
        try {
            categoryService.updateCategory(id, new CategoryDTO(categoryName, description));
            redirect.addFlashAttribute("message", "Category updated successfully.");
        } catch (RuntimeException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/categories";
    }

    @PostMapping("/admin/categories/delete/{id}")
    public String deleteCategory(@PathVariable String id, RedirectAttributes redirect) {
        try {
            categoryService.deleteCategory(id);
            redirect.addFlashAttribute("message", "Category deleted successfully.");
        } catch (RuntimeException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/categories";
    }
}
