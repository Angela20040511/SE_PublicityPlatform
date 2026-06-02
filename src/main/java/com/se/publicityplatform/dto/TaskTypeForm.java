package com.se.publicityplatform.dto;

import jakarta.validation.constraints.NotBlank;

public class TaskTypeForm {

    @NotBlank(message = "请输入任务类型名称")
    private String typeName;

    private Integer defaultDepartmentId;
    private String description;

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public Integer getDefaultDepartmentId() {
        return defaultDepartmentId;
    }

    public void setDefaultDepartmentId(Integer defaultDepartmentId) {
        this.defaultDepartmentId = defaultDepartmentId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
