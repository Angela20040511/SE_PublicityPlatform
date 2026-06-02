package com.se.publicityplatform.service;

import com.se.publicityplatform.mapper.AssignmentMapper;
import com.se.publicityplatform.mapper.PublicityAssetMapper;
import com.se.publicityplatform.mapper.PublicityRequestMapper;
import com.se.publicityplatform.mapper.PublicityTaskMapper;
import com.se.publicityplatform.mapper.ReminderRecordMapper;
import com.se.publicityplatform.mapper.ReviewRecordMapper;
import com.se.publicityplatform.mapper.SubmissionMapper;
import com.se.publicityplatform.model.Assignment;
import com.se.publicityplatform.model.PublicityAsset;
import com.se.publicityplatform.model.PublicityTask;
import com.se.publicityplatform.model.ReminderRecord;
import com.se.publicityplatform.model.ReviewRecord;
import com.se.publicityplatform.model.Submission;
import com.se.publicityplatform.model.User;
import com.se.publicityplatform.vo.TaskView;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TaskService {

    private final PublicityTaskMapper taskMapper;
    private final SubmissionMapper submissionMapper;
    private final AssignmentMapper assignmentMapper;
    private final ReviewRecordMapper reviewRecordMapper;
    private final PublicityAssetMapper assetMapper;
    private final PublicityRequestMapper requestMapper;
    private final ReminderRecordMapper reminderRecordMapper;

    public TaskService(
            PublicityTaskMapper taskMapper,
            SubmissionMapper submissionMapper,
            AssignmentMapper assignmentMapper,
            ReviewRecordMapper reviewRecordMapper,
            PublicityAssetMapper assetMapper,
            PublicityRequestMapper requestMapper,
            ReminderRecordMapper reminderRecordMapper) {
        this.taskMapper = taskMapper;
        this.submissionMapper = submissionMapper;
        this.assignmentMapper = assignmentMapper;
        this.reviewRecordMapper = reviewRecordMapper;
        this.assetMapper = assetMapper;
        this.requestMapper = requestMapper;
        this.reminderRecordMapper = reminderRecordMapper;
    }

    public List<TaskView> tasksFor(User user) {
        return switch (user.getRole()) {
            case "teacher", "director" -> taskMapper.findAllViews();
            case "leader" -> taskMapper.findViewsByDepartment(user.getDepartmentId());
            case "member" -> taskMapper.findViewsByAssignee(user.getUserId());
            case "applicant" -> taskMapper.findViewsByApplicant(user.getUserId());
            default -> List.of();
        };
    }

    public TaskView findTaskView(Integer taskId) {
        return taskMapper.findViewById(taskId);
    }

    @Transactional
    public void assignDepartment(Integer taskId, Integer departmentId, User operator) {
        if (!WorkflowRules.canManageTask(operator.getRole())) {
            throw new IllegalStateException("当前角色无权进行部门派单");
        }
        PublicityTask task = taskMapper.findById(taskId);
        if (task == null) {
            throw new IllegalArgumentException("宣传任务不存在");
        }
        if ("completed".equals(task.getTaskStatus())) {
            throw new IllegalStateException("已完成任务不能重新派单");
        }
        taskMapper.updateDepartmentAndStatus(taskId, departmentId, "assigned");
    }

    @Transactional
    public void addCollaboratingDepartment(Integer taskId, Integer departmentId, User operator) {
        if (!WorkflowRules.canManageTask(operator.getRole())) {
            throw new IllegalStateException("当前角色无权追加协作部门");
        }
        PublicityTask source = taskMapper.findById(taskId);
        if (source == null) {
            throw new IllegalArgumentException("宣传任务不存在");
        }
        if ("completed".equals(source.getTaskStatus())) {
            throw new IllegalStateException("已完成任务不能追加协作部门");
        }
        if (taskMapper.countByRequestTypeDepartment(source.getRequestId(), source.getTaskTypeId(), departmentId) > 0) {
            throw new IllegalStateException("该部门已经参与此任务");
        }
        PublicityTask task = new PublicityTask();
        task.setRequestId(source.getRequestId());
        task.setTaskTypeId(source.getTaskTypeId());
        task.setDepartmentId(departmentId);
        task.setTaskStatus("waiting_assignment");
        task.setDeadline(source.getDeadline());
        taskMapper.insert(task);
    }

    @Transactional
    public void assignMember(Integer taskId, Integer assigneeId, String remark, User leader) {
        if (!"leader".equals(leader.getRole())) {
            throw new IllegalStateException("当前角色无权安排干事");
        }
        PublicityTask task = taskMapper.findById(taskId);
        if (task == null || !leader.getDepartmentId().equals(task.getDepartmentId())) {
            throw new IllegalStateException("只能安排本部门任务");
        }
        if ("completed".equals(task.getTaskStatus())) {
            throw new IllegalStateException("任务已完成，无需继续安排");
        }
        if (!"waiting_assignment".equals(task.getTaskStatus()) && task.getAssigneeId() != null) {
            throw new IllegalStateException("已安排干事，无需重复派单");
        }
        taskMapper.updateAssigneeAndStatus(taskId, assigneeId, "in_progress");
        Assignment assignment = new Assignment();
        assignment.setTaskId(taskId);
        assignment.setAssignerId(leader.getUserId());
        assignment.setAssigneeId(assigneeId);
        assignment.setRemark(remark);
        assignmentMapper.insert(assignment);
        createReminder(taskId, assigneeId, "assignment", "你有新的宣传任务，请按截止时间提交成果");
    }

    @Transactional
    public void submitResult(Integer taskId, String fileUrl, String description, User member) {
        PublicityTask task = taskMapper.findById(taskId);
        if (task == null || !member.getUserId().equals(task.getAssigneeId())) {
            throw new IllegalStateException("只能提交本人负责的任务成果");
        }
        if (!StringUtils.hasText(fileUrl)) {
            throw new IllegalArgumentException("请填写成果文件地址或外部链接");
        }
        Submission submission = new Submission();
        submission.setTaskId(taskId);
        submission.setSubmitterId(member.getUserId());
        submission.setFileUrl(fileUrl.trim());
        submission.setDescription(description);
        submission.setVersionNo(submissionMapper.maxVersionNo(taskId) + 1);
        submission.setStatus("submitted");
        submissionMapper.insert(submission);
        taskMapper.updateStatus(taskId, "submitted");
    }

    @Transactional
    public void leaderReview(Integer submissionId, String result, String comment, User leader) {
        if (!WorkflowRules.canLeaderReview(leader.getRole())) {
            throw new IllegalStateException("当前角色无权进行部长初审");
        }
        Submission submission = requireSubmission(submissionId);
        PublicityTask task = taskMapper.findById(submission.getTaskId());
        if (task == null || !leader.getDepartmentId().equals(task.getDepartmentId())) {
            throw new IllegalStateException("只能初审本部门任务成果");
        }
        applySubmissionReview(submission, "leader", result, comment, leader);
    }

    @Transactional
    public void teacherReview(Integer submissionId, String result, String comment, User reviewer) {
        if (!WorkflowRules.canTeacherReview(reviewer.getRole())) {
            throw new IllegalStateException("当前角色无权进行老师复审");
        }
        Submission submission = requireSubmission(submissionId);
        if (!"leader_approved".equals(submission.getStatus()) && !"revision".equals(result)) {
            throw new IllegalStateException("成果需要先通过部长初审");
        }
        applySubmissionReview(submission, "teacher", result, comment, reviewer);
    }

    @Transactional
    public void applicantConfirm(Integer submissionId, User applicant) {
        if (!WorkflowRules.canConfirmResult(applicant.getRole())) {
            throw new IllegalStateException("当前角色无权确认成果");
        }
        Submission submission = requireSubmission(submissionId);
        PublicityTask task = taskMapper.findById(submission.getTaskId());
        TaskView view = taskMapper.findViewById(task.getTaskId());
        if (view == null || !applicant.getUserId().equals(view.getApplicantId())) {
            throw new IllegalStateException("只能确认自己申请的宣传成果");
        }
        if (!"teacher_approved".equals(submission.getStatus())) {
            throw new IllegalStateException("成果需先通过复审");
        }
        submissionMapper.updateStatus(submissionId, "confirmed");
        taskMapper.updateStatus(task.getTaskId(), "completed");
        archiveTaskAsset(task, submission, applicant);
        if (taskMapper.countOpenByRequest(task.getRequestId()) == 0) {
            requestMapper.updateStatus(task.getRequestId(), "archived");
        }
    }

    private void applySubmissionReview(Submission submission, String stage, String result, String comment, User reviewer) {
        String nextSubmissionStatus = WorkflowRules.submissionStatusForReview(stage, result);
        String nextTaskStatus = WorkflowRules.taskStatusForSubmissionReview(stage, result);
        submissionMapper.updateStatus(submission.getSubmissionId(), nextSubmissionStatus);
        taskMapper.updateStatus(submission.getTaskId(), nextTaskStatus);
        saveReview("submission", submission.getSubmissionId(), reviewer.getUserId(), result, comment);
    }

    private Submission requireSubmission(Integer submissionId) {
        Submission submission = submissionMapper.findById(submissionId);
        if (submission == null) {
            throw new IllegalArgumentException("成果提交记录不存在");
        }
        return submission;
    }

    private void archiveTaskAsset(PublicityTask task, Submission submission, User applicant) {
        TaskView view = taskMapper.findViewById(task.getTaskId());
        PublicityAsset asset = new PublicityAsset();
        asset.setRequestId(task.getRequestId());
        asset.setTaskId(task.getTaskId());
        asset.setAssetType(view == null ? "宣传成果" : view.getTypeName());
        asset.setFileUrl(submission.getFileUrl());
        asset.setArchivedBy(applicant.getUserId());
        asset.setDescription(submission.getDescription());
        assetMapper.insert(asset);
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

    private void createReminder(Integer taskId, Integer receiverId, String type, String content) {
        ReminderRecord reminder = new ReminderRecord();
        reminder.setTaskId(taskId);
        reminder.setReceiverId(receiverId);
        reminder.setReminderType(type);
        reminder.setReminderContent(content);
        reminder.setSendStatus("waiting");
        reminder.setPlannedTime(LocalDateTime.now());
        reminderRecordMapper.insert(reminder);
    }
}
