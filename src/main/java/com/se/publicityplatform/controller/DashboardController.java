package com.se.publicityplatform.controller;

import com.se.publicityplatform.model.User;
import com.se.publicityplatform.service.PublicityRequestService;
import com.se.publicityplatform.service.StatsService;
import com.se.publicityplatform.service.TaskService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final PublicityRequestService requestService;
    private final TaskService taskService;
    private final StatsService statsService;

    public DashboardController(PublicityRequestService requestService, TaskService taskService, StatsService statsService) {
        this.requestService = requestService;
        this.taskService = taskService;
        this.statsService = statsService;
    }

    @GetMapping("/")
    public String index() {
        return "redirect:/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("currentUser");
        if ("admin".equals(user.getRole())) {
            return "redirect:/admin";
        }
        model.addAttribute("requests", requestService.requestsFor(user));
        model.addAttribute("tasks", taskService.tasksFor(user));
        model.addAttribute("stats", statsService.summary());
        return "dashboard";
    }
}
