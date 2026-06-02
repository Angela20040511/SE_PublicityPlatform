package com.se.publicityplatform.controller;

import com.se.publicityplatform.dto.LoginForm;
import com.se.publicityplatform.model.User;
import com.se.publicityplatform.service.AuthService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/login")
    public String loginForm(Model model) {
        model.addAttribute("loginForm", new LoginForm());
        return "login";
    }

    @PostMapping("/login")
    public String login(@Valid @ModelAttribute LoginForm loginForm, BindingResult bindingResult, HttpSession session, Model model) {
        if (bindingResult.hasErrors()) {
            return "login";
        }
        User user = authService.login(loginForm.getUsername(), loginForm.getPassword());
        if (user == null) {
            model.addAttribute("error", "账号或密码错误，或账号已停用");
            return "login";
        }
        session.setAttribute("currentUser", user);
        if ("admin".equals(user.getRole())) {
            return "redirect:/admin";
        }
        return "redirect:/dashboard";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
