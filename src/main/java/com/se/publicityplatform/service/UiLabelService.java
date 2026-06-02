package com.se.publicityplatform.service;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component("ui")
public class UiLabelService {

    private static final Map<String, String> STATUS_LABELS = Map.ofEntries(
            Map.entry("pending_review", "待审核"),
            Map.entry("returned", "退回补充"),
            Map.entry("rejected", "已驳回"),
            Map.entry("approved", "已通过"),
            Map.entry("archived", "已归档"),
            Map.entry("waiting_assignment", "待安排"),
            Map.entry("assigned", "已派单"),
            Map.entry("in_progress", "执行中"),
            Map.entry("submitted", "待初审"),
            Map.entry("revision_required", "需修改"),
            Map.entry("leader_approved", "初审通过"),
            Map.entry("teacher_approved", "复审通过"),
            Map.entry("confirmed", "已确认"),
            Map.entry("completed", "已完成"),
            Map.entry("enabled", "启用")
    );

    private static final Map<String, String> ROLE_LABELS = Map.of(
            "applicant", "申请人",
            "teacher", "负责老师",
            "director", "主任",
            "leader", "部长",
            "member", "干事",
            "admin", "系统管理员"
    );

    public String status(String value) {
        if (value == null || value.isBlank()) {
            return "未设置";
        }
        return STATUS_LABELS.getOrDefault(value, value);
    }

    public String role(String value) {
        if (value == null || value.isBlank()) {
            return "未设置";
        }
        return ROLE_LABELS.getOrDefault(value, value);
    }

    public String statusClass(String value) {
        if (value == null || value.isBlank()) {
            return "status-neutral";
        }
        return switch (value) {
            case "approved", "leader_approved", "teacher_approved", "confirmed", "completed", "archived", "enabled" -> "status-success";
            case "pending_review", "waiting_assignment", "assigned", "submitted" -> "status-info";
            case "in_progress" -> "status-progress";
            case "returned", "revision_required" -> "status-warning";
            case "rejected" -> "status-danger";
            default -> "status-neutral";
        };
    }
}
