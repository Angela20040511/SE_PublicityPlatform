package com.se.publicityplatform.mapper;

import com.se.publicityplatform.model.TaskType;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TaskTypeMapper {

    @Select("select * from task_type order by task_type_id")
    List<TaskType> findAll();

    @Select("select * from task_type where task_type_id = #{taskTypeId}")
    TaskType findById(Integer taskTypeId);

    @Select("select * from task_type where type_name = #{typeName} limit 1")
    TaskType findByName(String typeName);

    @Insert("insert into task_type (type_name, default_department_id, description) values (#{typeName}, #{defaultDepartmentId}, #{description})")
    @Options(useGeneratedKeys = true, keyProperty = "taskTypeId")
    int insert(TaskType taskType);
}
