package com.RockyToys.User.controller;

import com.RockyToys.User.dto.LoginDTO;
import com.RockyToys.User.dto.RegisterDTO;
import com.RockyToys.User.dto.UserUpdateDTO;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/users")
public class UserController {
 private final UserService users; private final String adminEmail,adminPassword;

 public UserController(UserService users, @Value("${app.admin.email:admin@rockytoys.local}") String adminEmail, @Value("${app.admin.password:ChangeMe123!}") String adminPassword){
  this.users=users;
  this.adminEmail=adminEmail;
  this.adminPassword=adminPassword;}

 @GetMapping("/register")
 public String registerForm(Model m){m.addAttribute("registerDTO",new RegisterDTO());return "register";}
 @PostMapping("/register")
 public String register(@Valid @ModelAttribute RegisterDTO dto,BindingResult result,Model m,RedirectAttributes ra){if(result.hasErrors())return "register";try{users.registerUser(dto);ra.addFlashAttribute("message","Account created. Please log in.");return "redirect:/users/login";}catch(IllegalArgumentException e){m.addAttribute("error",e.getMessage());return "register";}}
 @GetMapping("/login")
 public String loginForm(Model m){m.addAttribute("loginDTO",new LoginDTO());return "login";}
 @PostMapping("/login")
 public String login(@Valid @ModelAttribute LoginDTO dto,BindingResult result,HttpSession session,Model m){if(result.hasErrors())return "login";try{User u=users.loginUser(dto);session.setAttribute("loggedInUser",u);return "redirect:/users/profile";}catch(IllegalArgumentException e){m.addAttribute("error",e.getMessage());return "login";}}
 @GetMapping("/logout")
 public String logout(HttpSession s){s.invalidate();return "redirect:/";}
 @GetMapping("/profile")
 public String profile(HttpSession s,Model m){User u=current(s);if(u==null)return "redirect:/users/login";m.addAttribute("user",u);UserUpdateDTO dto=new UserUpdateDTO();dto.setFullName(u.getFullName());dto.setPhone(u.getPhone());dto.setAddress(u.getAddress());m.addAttribute("userUpdateDTO",dto);return "profile";}
 @PostMapping("/profile")
 public String update(@Valid @ModelAttribute UserUpdateDTO dto,BindingResult result,HttpSession s,Model m,RedirectAttributes ra){User u=current(s);if(u==null)return "redirect:/users/login";if(result.hasErrors()){m.addAttribute("user",u);return "profile";}try{users.updateUser(u.getUserId(),dto);s.setAttribute("loggedInUser",users.getUserById(u.getUserId()));ra.addFlashAttribute("message","Profile updated.");return "redirect:/users/profile";}catch(IllegalArgumentException e){m.addAttribute("user",u);m.addAttribute("error",e.getMessage());return "profile";}}
 @PostMapping("/delete")
 public String delete(HttpSession s){User u=current(s);if(u!=null&&"CUSTOMER".equals(u.getRole()))users.deleteUser(u.getUserId());s.invalidate();return "redirect:/users/register";}
 @GetMapping("/admin-list")
 public String listUsers(HttpSession s,Model m){User u=current(s);if(u==null||!"ADMIN".equals(u.getRole()))return "redirect:/admin/login";m.addAttribute("users",users.getAllUsers());return "admin/users";}
 public static User current(HttpSession s){return (User)s.getAttribute("loggedInUser");}
}
