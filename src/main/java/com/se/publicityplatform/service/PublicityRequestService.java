package com.se.publicityplatform.service;

import com.se.publicityplatform.dto.PublicityRequestForm;
import com.se.publicityplatform.mapper.PublicityRequestMapper;
import com.se.publicityplatform.mapper.PublicityTaskMapper;
import com.se.publicityplatform.mapper.ReviewRecordMapper;
import com.se.publicityplatform.mapper.TaskTypeMapper;
import com.se.publicityplatform.model.PublicityRequest;
import com.se.publicityplatform.model.PublicityTask;
import com.se.publicityplatform.model.ReviewRecord;
import com.se.publicityplatform.model.TaskType;
import com.se.publicityplatform.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;

@Service
public class PublicityRequestService {

    private final PublicityRequestMapper requestMapper;
    private final TaskTypeMapper taskTypeMapper;
    private final PublicityTaskMapper taskMapper;
    private final ReviewRecordMapper reviewRecordMapper;

    public PublicityRequestService(
            PublicityRequestMapper requestMapper,
            TaskTypeMapper taskTypeMapper,
            PublicityTaskMapper taskMapper,
            ReviewRecordMapper reviewRecordMapper) {
        this.requestMapper = requestMapper;
        this.taskTypeMapper = taskTypeMapper;
        this.taskMapper = taskMapper;
        this.reviewRecordMapper = reviewRecordMapper;
    }

    public List<PublicityRequest> requestsFor(User user) {
        if ("applicant".equals(user.getRole())) {
            return requestMapper.findByApplicant(user.getUserId());
        }
        if (WorkflowRules.canReviewRequest(user.getRole())) {
            return requestMapper.findAll();
        }
        if ("leader".equals(user.getRole())) {
            return requestMapper.findByTaskDepartment(user.getDepartmentId());
        }
        if ("member".equals(user.getRole())) {
            return requestMapper.findByTaskAssignee(user.getUserId());
        }
        return List.of();
    }

    public List<PublicityRequest> pendingReviewRequests() {
        return requestMapper.findPendingReview();
    }

    public PublicityRequest findById(Integer requestId) {
        return requestMapper.findById(requestId);
    }

    @Transactional
    public PublicityRequest submit(PublicityRequestForm form, User applicant) {
        PublicityRequest request = new PublicityRequest();
        request.setApplicantId(applicant.getUserId());
        request.setActivityName(form.getActivityName().trim());
        request.setActivityTime(form.getActivityTime());
        request.setActivityLocation(form.getActivityLocation().trim());
        request.setActivityContent(form.getActivityContent());
        request.setPublicityTypes(form.getPublicityTypes().trim());
        request.setContactName(form.getContactName().trim());
        request.setContactPhone(form.getContactPhone().trim());
        request.setDeadline(form.getDeadline());
        request.setAttachmentUrl(form.getAttachmentUrl());
        request.setStatus("pending_review");
        requestMapper.insert(request);
        return request;
    }

    @Transactional
    public void review(Integer requestId, String result, String comment, User reviewer) {
        if (!WorkflowRules.canReviewRequest(reviewer.getRole())) {
            throw new IllegalStateException("当前角色无权审核宣传需求");
        }
        PublicityRequest request = requestMapper.findById(requestId);
        if (request == null) {
            throw new IllegalArgumentException("宣传需求不存在");
        }
        String nextStatus = WorkflowRules.requestStatusForReviewResult(result);
        requestMapper.updateStatus(requestId, nextStatus);
        saveReview("request", requestId, reviewer.getUserId(), result, comment);
        if ("approved".equals(nextStatus) && taskMapper.countByRequest(requestId) == 0) {
            createTasksForRequest(request);
        }
    }

    private void createTasksForRequest(PublicityRequest request) {
        Arrays.stream(request.getPublicityTypes().split("[,，、]"))
                .map(String::trim)
                .filter(StringUtils::hasText)
                .forEach(typeName -> {
                    TaskType taskType = taskTypeMapper.findByName(typeName);
                    if (taskType == null) {
                        return;
                    }
                    PublicityTask task = new PublicityTask();
                    task.setRequestId(request.getRequestId());
                    task.setTaskTypeId(taskType.getTaskTypeId());
                    task.setDepartmentId(taskType.getDefaultDepartmentId());
                    task.setTaskStatus("waiting_assignment");
                    task.setDeadline(request.getDeadline());
                    taskMapper.insert(task);
                });
    }

    private void saveReview(String targetType, Integer targetId, Integer reviewerId, String result, String comment) {
        ReviewRecord record = new ReviewRecord();
        record.setTargetType(targetType);
        record.setTargetId(targetId);
        record.setReviewerId(reviewerId);
        record.setReviewResult(result);
        record.setReviewComment(comment);
        reviewRecordMapper.insert(record);
    }
}
