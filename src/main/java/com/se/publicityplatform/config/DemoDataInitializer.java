package com.se.publicityplatform.config;

import com.se.publicityplatform.mapper.DepartmentMapper;
import com.se.publicityplatform.mapper.UserMapper;
import com.se.publicityplatform.model.Department;
import com.se.publicityplatform.model.User;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class DemoDataInitializer implements ApplicationRunner {

    private final UserMapper userMapper;
    private final DepartmentMapper departmentMapper;

    public DemoDataInitializer(UserMapper userMapper, DepartmentMapper departmentMapper) {
        this.userMapper = userMapper;
        this.departmentMapper = departmentMapper;
    }

    @Override
    public void run(ApplicationArguments args) {
        List<Department> departments = departmentMapper.findAll();
        ensureDepartmentUsers(departments, "摄影部", "photo_leader", "赵部长", "photo_member", "钱干事");
        ensureDepartmentUsers(departments, "新媒体部", "media_leader", "周部长", "media_member", "陈干事");
        ensureDepartmentUsers(departments, "新闻部", "news_leader", "吴部长", "news_member", "郑干事");
        ensureDepartmentUsers(departments, "美工部", "art_leader", "冯部长", "art_member", "王干事");
    }

    private void ensureDepartmentUsers(
            List<Department> departments,
            String departmentName,
            String leaderUsername,
            String leaderName,
            String memberUsername,
            String memberName) {
        Optional<Department> department = departments.stream()
                .filter(item -> departmentName.equals(item.getDepartmentName()))
                .findFirst();
        if (department.isEmpty()) {
            return;
        }
        Integer departmentId = department.get().getDepartmentId();
        User leader = ensureUser(leaderUsername, leaderName, "leader", departmentId);
        ensureUser(memberUsername, memberName, "member", departmentId);
        if (department.get().getLeaderId() == null && leader.getUserId() != null) {
            departmentMapper.updateLeader(departmentId, leader.getUserId());
        }
    }

    private User ensureUser(String username, String realName, String role, Integer departmentId) {
        User existing = userMapper.findByUsername(username);
        if (existing != null) {
            return existing;
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword("encrypted_123456");
        user.setRealName(realName);
        user.setRole(role);
        user.setDepartmentId(departmentId);
        user.setPhone(phoneFor(username));
        user.setEmail(username + "@example.com");
        userMapper.insert(user);
        return user;
    }

    private String phoneFor(String username) {
        Map<String, String> phones = Map.of(
                "photo_member", "13800000007",
                "media_leader", "13800000008",
                "news_leader", "13800000009",
                "news_member", "13800000010",
                "art_leader", "13800000011",
                "art_member", "13800000012"
        );
        return phones.getOrDefault(username, "13800000004");
    }
}
