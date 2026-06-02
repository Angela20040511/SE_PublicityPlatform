package com.se.publicityplatform.service;

import com.se.publicityplatform.dto.PublicityRequestForm;
import com.se.publicityplatform.mapper.PublicityAssetMapper;
import com.se.publicityplatform.mapper.PublicityRequestMapper;
import com.se.publicityplatform.mapper.SubmissionMapper;
import com.se.publicityplatform.mapper.UserMapper;
import com.se.publicityplatform.model.PublicityRequest;
import com.se.publicityplatform.model.Submission;
import com.se.publicityplatform.model.User;
import com.se.publicityplatform.vo.TaskView;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:full_workflow_test;MODE=MySQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1")
@ActiveProfiles("test")
class FullWorkflowIntegrationTest {

    @Autowired
    private PublicityRequestService requestService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PublicityRequestMapper requestMapper;

    @Autowired
    private SubmissionMapper submissionMapper;

    @Autowired
    private PublicityAssetMapper assetMapper;

    @Test
    void runsRequestToArchiveWorkflow() {
        User applicant = userMapper.findByUsername("applicant01");
        User teacher = userMapper.findByUsername("teacher01");
        User leader = userMapper.findByUsername("photo_leader");
        User member = userMapper.findByUsername("photo_member");
        assertNotNull(member, "演示数据初始化器应补齐摄影部干事");

        PublicityRequestForm form = new PublicityRequestForm();
        form.setActivityName("全流程测试活动");
        form.setActivityTime(LocalDateTime.now().plusDays(2));
        form.setActivityLocation("学院报告厅");
        form.setActivityContent("用于验证核心闭环");
        form.setPublicityTypes("拍照");
        form.setContactName("张同学");
        form.setContactPhone("13800000001");
        form.setDeadline(LocalDateTime.now().plusDays(4));
        form.setAttachmentUrl("/upload/test.docx");

        PublicityRequest request = requestService.submit(form, applicant);
        requestService.review(request.getRequestId(), "approve", "同意宣传", teacher);

        List<TaskView> leaderTasks = taskService.tasksFor(leader);
        assertFalse(leaderTasks.isEmpty());
        TaskView task = leaderTasks.stream()
                .filter(item -> item.getRequestId().equals(request.getRequestId()))
                .findFirst()
                .orElseThrow();
        assertEquals("waiting_assignment", task.getTaskStatus());

        taskService.assignMember(task.getTaskId(), member.getUserId(), "请按活动流程拍摄", leader);
        taskService.submitResult(task.getTaskId(), "https://example.com/photos", "现场照片合集", member);

        Submission submission = submissionMapper.findLatestByTask(task.getTaskId());
        assertNotNull(submission);
        taskService.leaderReview(submission.getSubmissionId(), "approve", "照片质量合格", leader);
        taskService.teacherReview(submission.getSubmissionId(), "approve", "复审通过", teacher);
        taskService.applicantConfirm(submission.getSubmissionId(), applicant);

        TaskView finishedTask = taskService.findTaskView(task.getTaskId());
        assertEquals("completed", finishedTask.getTaskStatus());
        assertEquals("confirmed", submissionMapper.findById(submission.getSubmissionId()).getStatus());
        assertEquals("archived", requestMapper.findById(request.getRequestId()).getStatus());
        assertEquals(1, assetMapper.countAll());
    }
}
