package com.se.publicityplatform.service;

import java.util.Set;

public final class WorkflowRules {

    private static final Set<String> REVIEW_ROLES = Set.of("teacher", "director");

    private WorkflowRules() {
    }

    public static boolean canReviewRequest(String role) {
        return REVIEW_ROLES.contains(role);
    }

    public static boolean canManageTask(String role) {
        return "director".equals(role) || "teacher".equals(role);
    }

    public static boolean canLeaderReview(String role) {
        return "leader".equals(role);
    }

    public static boolean canTeacherReview(String role) {
        return REVIEW_ROLES.contains(role);
    }

    public static boolean canConfirmResult(String role) {
        return "applicant".equals(role);
    }

    public static String requestStatusForReviewResult(String result) {
        return switch (result) {
            case "approve" -> "approved";
            case "return" -> "returned";
            case "reject" -> "rejected";
            default -> throw new IllegalArgumentException("未知审核结果: " + result);
        };
    }

    public static String taskStatusForSubmissionReview(String stage, String result) {
        if ("revision".equals(result)) {
            return "revision_required";
        }
        if ("leader".equals(stage) && "approve".equals(result)) {
            return "submitted";
        }
        if ("teacher".equals(stage) && "approve".equals(result)) {
            return "approved";
        }
        throw new IllegalArgumentException("未知成果审核动作: " + stage + "/" + result);
    }

    public static String submissionStatusForReview(String stage, String result) {
        if ("revision".equals(result)) {
            return "revision_required";
        }
        if ("leader".equals(stage) && "approve".equals(result)) {
            return "leader_approved";
        }
        if ("teacher".equals(stage) && "approve".equals(result)) {
            return "teacher_approved";
        }
        throw new IllegalArgumentException("未知成果审核动作: " + stage + "/" + result);
    }
}
