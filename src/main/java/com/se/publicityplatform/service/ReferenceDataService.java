package com.se.publicityplatform.service;

import com.se.publicityplatform.dto.TaskTypeForm;
import com.se.publicityplatform.dto.UserForm;
import com.se.publicityplatform.mapper.DepartmentMapper;
import com.se.publicityplatform.mapper.TaskTypeMapper;
import com.se.publicityplatform.mapper.UserMapper;
import com.se.publicityplatform.model.Department;
import com.se.publicityplatform.model.TaskType;
import com.se.publicityplatform.model.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReferenceDataService {

    private final UserMapper userMapper;
    private final DepartmentMapper departmentMapper;
    private final TaskTypeMapper taskTypeMapper;

    public ReferenceDataService(UserMapper userMapper, DepartmentMapper departmentMapper, TaskTypeMapper taskTypeMapper) {
        this.userMapper = userMapper;
        this.departmentMapper = departmentMapper;
        this.taskTypeMapper = taskTypeMapper;
    }

    public List<User> users() {
        return userMapper.findAll();
    }

    public List<Department> departments() {
        return departmentMapper.findAll();
    }

    public List<TaskType> taskTypes() {
        return taskTypeMapper.findAll();
    }

    public List<User> departmentMembers(Integer departmentId) {
        return userMapper.findByDepartmentAndRole(departmentId, "member");
    }

    public void createUser(UserForm form) {
        User user = new User();
        user.setUsername(form.getUsername());
        user.setPassword("encrypted_" + form.getPassword());
        user.setRealName(form.getRealName());
        user.setRole(form.getRole());
        user.setDepartmentId(form.getDepartmentId());
        user.setPhone(form.getPhone());
        user.setEmail(form.getEmail());
        userMapper.insert(user);
    }

    public void createTaskType(TaskTypeForm form) {
        TaskType taskType = new TaskType();
        taskType.setTypeName(form.getTypeName());
        taskType.setDefaultDepartmentId(form.getDefaultDepartmentId());
        taskType.setDescription(form.getDescription());
        taskTypeMapper.insert(taskType);
    }
}
