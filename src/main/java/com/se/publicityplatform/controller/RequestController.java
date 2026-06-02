package com.se.publicityplatform.controller;

import com.se.publicityplatform.dto.PublicityRequestForm;
import com.se.publicityplatform.model.User;
import com.se.publicityplatform.service.PublicityRequestService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class RequestController {

    private final PublicityRequestService requestService;

    public RequestController(PublicityRequestService requestService) {
        this.requestService = requestService;
    }

    @GetMapping("/requests")
    public String list(HttpSession session, Model model) {
        User user = (User) session.getAttribute("currentUser");
        model.addAttribute("requests", requestService.requestsFor(user));
        return "requests/list";
    }

    @GetMapping("/requests/new")
    public String newRequest(Model model) {
        model.addAttribute("requestForm", new PublicityRequestForm());
        return "requests/form";
    }

    @PostMapping("/requests")
    public String create(@Valid @ModelAttribute("requestForm") PublicityRequestForm form,
                         BindingResult bindingResult,
                         HttpSession session,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "requests/form";
        }
        User user = (User) session.getAttribute("currentUser");
        if (!"applicant".equals(user.getRole())) {
            redirectAttributes.addFlashAttribute("error", "只有申请人可以提交宣传需求");
            return "redirect:/requests";
        }
        requestService.submit(form, user);
        redirectAttributes.addFlashAttribute("message", "宣传需求已提交，等待审核");
        return "redirect:/requests";
    }

    @GetMapping("/requests/review")
    public String reviewList(Model model) {
        model.addAttribute("requests", requestService.pendingReviewRequests());
        return "requests/review";
    }

    @PostMapping("/requests/{requestId}/review")
    public String review(@PathVariable Integer requestId,
                         @RequestParam String result,
                         @RequestParam(required = false) String comment,
                         HttpSession session,
                         RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("currentUser");
        try {
            requestService.review(requestId, result, comment, user);
            redirectAttributes.addFlashAttribute("message", "审核处理完成");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/requests/review";
    }
}
