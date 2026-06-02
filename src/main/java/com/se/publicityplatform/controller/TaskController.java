package com.se.publicityplatform.controller;

import com.se.publicityplatform.model.User;
import com.se.publicityplatform.service.ReferenceDataService;
import com.se.publicityplatform.service.TaskService;
import com.se.publicityplatform.vo.TaskView;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class TaskController {

    private final TaskService taskService;
    private final ReferenceDataService referenceDataService;

    public TaskController(TaskService taskService, ReferenceDataService referenceDataService) {
        this.taskService = taskService;
        this.referenceDataService = referenceDataService;
    }

    @GetMapping("/tasks")
    public String tasks(HttpSession session, Model model) {
        User user = (User) session.getAttribute("currentUser");
        List<TaskView> tasks = taskService.tasksFor(user);
        model.addAttribute("tasks", tasks);
        model.addAttribute("departments", referenceDataService.departments());
        model.addAttribute("members", user.getDepartmentId() == null ? null : referenceDataService.departmentMembers(user.getDepartmentId()));
        model.addAttribute("showTaskActions", showTaskActions(user, tasks));
        return "tasks/list";
    }

    @PostMapping("/tasks/{taskId}/department")
    public String assignDepartment(@PathVariable Integer taskId,
                                   @RequestParam Integer departmentId,
                                   HttpSession session,
                                   RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("currentUser");
        try {
            taskService.assignDepartment(taskId, departmentId, user);
            redirectAttributes.addFlashAttribute("message", "部门派单已更新");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/tasks";
    }

    @PostMapping("/tasks/{taskId}/collaboration-department")
    public String addCollaboratingDepartment(@PathVariable Integer taskId,
                                             @RequestParam Integer departmentId,
                                             HttpSession session,
                                             RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("currentUser");
        try {
            taskService.addCollaboratingDepartment(taskId, departmentId, user);
            redirectAttributes.addFlashAttribute("message", "协作部门已追加");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/tasks";
    }

    @PostMapping("/tasks/{taskId}/assignee")
    public String assignMember(@PathVariable Integer taskId,
                               @RequestParam Integer assigneeId,
                               @RequestParam(required = false) String remark,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("currentUser");
        try {
            taskService.assignMember(taskId, assigneeId, remark, user);
            redirectAttributes.addFlashAttribute("message", "干事安排已完成");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/tasks";
    }

    @PostMapping("/tasks/{taskId}/submit")
    public String submit(@PathVariable Integer taskId,
                         @RequestParam String fileUrl,
                         @RequestParam(required = false) String description,
                         HttpSession session,
                         RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("currentUser");
        try {
            taskService.submitResult(taskId, fileUrl, description, user);
            redirectAttributes.addFlashAttribute("message", "成果已提交，等待部长初审");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/tasks";
    }

    @PostMapping("/submissions/{submissionId}/leader-review")
    public String leaderReview(@PathVariable Integer submissionId,
                               @RequestParam String result,
                               @RequestParam(required = false) String comment,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("currentUser");
        try {
            taskService.leaderReview(submissionId, result, comment, user);
            redirectAttributes.addFlashAttribute("message", "部长初审已处理");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/tasks";
    }

    @PostMapping("/submissions/{submissionId}/teacher-review")
    public String teacherReview(@PathVariable Integer submissionId,
                                @RequestParam String result,
                                @RequestParam(required = false) String comment,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("currentUser");
        try {
            taskService.teacherReview(submissionId, result, comment, user);
            redirectAttributes.addFlashAttribute("message", "复审已处理");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/tasks";
    }

    @PostMapping("/submissions/{submissionId}/confirm")
    public String confirm(@PathVariable Integer submissionId,
                          HttpSession session,
                          RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("currentUser");
        try {
            taskService.applicantConfirm(submissionId, user);
            redirectAttributes.addFlashAttribute("message", "成果已确认并归档");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/tasks";
    }

    private boolean showTaskActions(User user, List<TaskView> tasks) {
        if ("teacher".equals(user.getRole()) || "director".equals(user.getRole()) || "leader".equals(user.getRole()) || "member".equals(user.getRole())) {
            return true;
        }
        return "applicant".equals(user.getRole())
                && tasks.stream().anyMatch(task -> "teacher_approved".equals(task.getSubmissionStatus()));
    }
}
