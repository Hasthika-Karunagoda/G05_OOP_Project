package com.RockyToys.User.controller;

import com.RockyToys.User.dto.LoginDTO;
import com.RockyToys.User.model.User;
import com.RockyToys.User.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final UserService users;
    private final String adminEmail;
    private final String adminPassword;

    public AdminController(UserService users,
            @Value("${app.admin.email:admin@rockytoys.local}") String adminEmail,
            @Value("${app.admin.password:ChangeMe123!}") String adminPassword) {
        this.users = users;
        this.adminEmail = adminEmail;
        this.adminPassword = adminPassword;
    }

    @GetMapping("/login")
    public String loginForm(Model model) {
        model.addAttribute("loginDTO", new LoginDTO());
        return "admin/login";
    }

    @PostMapping("/login")
    public String login(@Valid @ModelAttribute LoginDTO dto, BindingResult result,
                        HttpSession session, Model model) {
        if (result.hasErrors()) return "admin/login";
        if (adminEmail.equalsIgnoreCase(dto.getEmail().trim())
                && adminPassword.equals(dto.getPassword())) {
            session.setAttribute("loggedInUser",
                new User("ADMIN", "Toyshop Administrator", adminEmail, "", "", "", "ADMIN"));
            return "redirect:/users/admin-list";
        }
        model.addAttribute("error", "Invalid administrator credentials.");
        return "admin/login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/admin/login";
    }


}
