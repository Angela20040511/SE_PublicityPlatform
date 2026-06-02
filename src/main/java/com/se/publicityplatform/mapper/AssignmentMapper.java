package com.se.publicityplatform.mapper;

import com.se.publicityplatform.model.Assignment;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;

@Mapper
public interface AssignmentMapper {

    @Insert("""
            insert into assignment (task_id, assigner_id, assignee_id, remark)
            values (#{taskId}, #{assignerId}, #{assigneeId}, #{remark})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "assignmentId")
    int insert(Assignment assignment);
}
