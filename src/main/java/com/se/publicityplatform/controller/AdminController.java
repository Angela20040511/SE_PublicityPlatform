package com.se.publicityplatform.controller;

import com.se.publicityplatform.dto.TaskTypeForm;
import com.se.publicityplatform.dto.UserForm;
import com.se.publicityplatform.model.User;
import com.se.publicityplatform.service.ReferenceDataService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AdminController {

    private final ReferenceDataService referenceDataService;

    public AdminController(ReferenceDataService referenceDataService) {
        this.referenceDataService = referenceDataService;
    }

    @GetMapping("/admin")
    public String admin(HttpSession session, Model model) {
        User user = (User) session.getAttribute("currentUser");
        if (!"admin".equals(user.getRole())) {
            return "redirect:/dashboard";
        }
        model.addAttribute("users", referenceDataService.users());
        model.addAttribute("departments", referenceDataService.departments());
        model.addAttribute("taskTypes", referenceDataService.taskTypes());
        model.addAttribute("userForm", new UserForm());
        model.addAttribute("taskTypeForm", new TaskTypeForm());
        return "admin/index";
    }

    @PostMapping("/admin/users")
    public String createUser(@Valid @ModelAttribute UserForm userForm,
                             BindingResult bindingResult,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("currentUser");
        if (!"admin".equals(user.getRole())) {
            return "redirect:/dashboard";
        }
        if (!bindingResult.hasErrors()) {
            referenceDataService.createUser(userForm);
            redirectAttributes.addFlashAttribute("message", "用户已新增");
        } else {
            redirectAttributes.addFlashAttribute("error", "用户信息不完整");
        }
        return "redirect:/admin";
    }

    @PostMapping("/admin/task-types")
    public String createTaskType(@Valid @ModelAttribute TaskTypeForm taskTypeForm,
                                 BindingResult bindingResult,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("currentUser");
        if (!"admin".equals(user.getRole())) {
            return "redirect:/dashboard";
        }
        if (!bindingResult.hasErrors()) {
            referenceDataService.createTaskType(taskTypeForm);
            redirectAttributes.addFlashAttribute("message", "任务类型已新增");
        } else {
            redirectAttributes.addFlashAttribute("error", "任务类型信息不完整");
        }
        return "redirect:/admin";
    }
}
