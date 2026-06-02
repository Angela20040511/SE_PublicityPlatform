package com.se.publicityplatform.controller;

import com.se.publicityplatform.dto.PublicityRequestForm;
import com.se.publicityplatform.mapper.UserMapper;
import com.se.publicityplatform.model.PublicityRequest;
import com.se.publicityplatform.model.User;
import com.se.publicityplatform.service.PublicityRequestService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:leader_task_page_test;MODE=MySQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1")
@AutoConfigureMockMvc
@ActiveProfiles("test")
class LeaderTaskPageIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PublicityRequestService requestService;

    @Autowired
    private UserMapper userMapper;

    @Test
    void leaderTaskPageShowsDepartmentMemberOptions() throws Exception {
        User applicant = userMapper.findByUsername("applicant01");
        User teacher = userMapper.findByUsername("teacher01");
        User leader = userMapper.findByUsername("photo_leader");

        PublicityRequestForm form = new PublicityRequestForm();
        form.setActivityName("页面下拉框测试活动");
        form.setActivityTime(LocalDateTime.now().plusDays(1));
        form.setActivityLocation("学院报告厅");
        form.setActivityContent("验证部长任务页干事下拉框");
        form.setPublicityTypes("拍照");
        form.setContactName("张同学");
        form.setContactPhone("13800000001");
        form.setDeadline(LocalDateTime.now().plusDays(3));

        PublicityRequest request = requestService.submit(form, applicant);
        requestService.review(request.getRequestId(), "approve", "通过", teacher);

        mockMvc.perform(get("/tasks").sessionAttr("currentUser", leader))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("页面下拉框测试活动")))
                .andExpect(content().string(containsString("钱干事")));
    }

    @Test
    void missingUploadPathShowsFriendlyPage() throws Exception {
        mockMvc.perform(get("/upload/page-flow.docx"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("文件还没有上传")))
                .andExpect(content().string(not(containsString("page-flow.docx"))))
                .andExpect(content().string(not(containsString("/upload/"))));
    }
}
