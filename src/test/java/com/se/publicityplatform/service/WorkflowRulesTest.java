package com.se.publicityplatform.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WorkflowRulesTest {

    @Test
    void requestReviewResultMapsToRequestStatus() {
        assertEquals("approved", WorkflowRules.requestStatusForReviewResult("approve"));
        assertEquals("returned", WorkflowRules.requestStatusForReviewResult("return"));
        assertEquals("rejected", WorkflowRules.requestStatusForReviewResult("reject"));
    }

    @Test
    void reviewPermissionsMatchPlannedRoles() {
        assertTrue(WorkflowRules.canReviewRequest("teacher"));
        assertTrue(WorkflowRules.canReviewRequest("director"));
        assertFalse(WorkflowRules.canReviewRequest("member"));
    }

    @Test
    void submissionReviewMapsStageAndResult() {
        assertEquals("leader_approved", WorkflowRules.submissionStatusForReview("leader", "approve"));
        assertEquals("teacher_approved", WorkflowRules.submissionStatusForReview("teacher", "approve"));
        assertEquals("revision_required", WorkflowRules.submissionStatusForReview("teacher", "revision"));
        assertEquals("approved", WorkflowRules.taskStatusForSubmissionReview("teacher", "approve"));
        assertEquals("revision_required", WorkflowRules.taskStatusForSubmissionReview("leader", "revision"));
    }

    @Test
    void invalidReviewResultFailsFast() {
        assertThrows(IllegalArgumentException.class, () -> WorkflowRules.requestStatusForReviewResult("maybe"));
    }
}
