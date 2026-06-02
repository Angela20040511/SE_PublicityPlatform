package com.se.publicityplatform.mapper;

import com.se.publicityplatform.model.PublicityTask;
import com.se.publicityplatform.vo.TaskView;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface PublicityTaskMapper {

    String TASK_VIEW_SQL = """
            select
                t.task_id, t.request_id, t.task_type_id, t.department_id, t.assignee_id,
                t.task_status, t.deadline,
                r.activity_name, r.activity_location, r.activity_time, r.applicant_id,
                tt.type_name, d.department_name, u.real_name as assignee_name,
                s.submission_id as latest_submission_id, s.status as submission_status,
                s.file_url, s.description as submission_description
            from publicity_task t
            join publicity_request r on r.request_id = t.request_id
            join task_type tt on tt.task_type_id = t.task_type_id
            left join department d on d.department_id = t.department_id
            left join `user` u on u.user_id = t.assignee_id
            left join submission s on s.submission_id = (
                select s2.submission_id from submission s2
                where s2.task_id = t.task_id
                order by s2.version_no desc, s2.submitted_at desc
                limit 1
            )
            """;

    @Insert("""
            insert into publicity_task (request_id, task_type_id, department_id, assignee_id, task_status, deadline)
            values (#{requestId}, #{taskTypeId}, #{departmentId}, #{assigneeId}, #{taskStatus}, #{deadline})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "taskId")
    int insert(PublicityTask task);

    @Select("select * from publicity_task where task_id = #{taskId}")
    PublicityTask findById(Integer taskId);

    @Select(TASK_VIEW_SQL + " order by t.created_at desc")
    List<TaskView> findAllViews();

    @Select(TASK_VIEW_SQL + " where t.department_id = #{departmentId} order by t.deadline")
    List<TaskView> findViewsByDepartment(Integer departmentId);

    @Select(TASK_VIEW_SQL + " where t.assignee_id = #{assigneeId} order by t.deadline")
    List<TaskView> findViewsByAssignee(Integer assigneeId);

    @Select(TASK_VIEW_SQL + " where r.applicant_id = #{applicantId} order by t.deadline")
    List<TaskView> findViewsByApplicant(Integer applicantId);

    @Select(TASK_VIEW_SQL + " where t.task_id = #{taskId}")
    TaskView findViewById(Integer taskId);

    @Select("select count(*) from publicity_task where request_id = #{requestId}")
    int countByRequest(Integer requestId);

    @Select("""
            select count(*) from publicity_task
            where request_id = #{requestId}
              and task_type_id = #{taskTypeId}
              and department_id = #{departmentId}
            """)
    int countByRequestTypeDepartment(@Param("requestId") Integer requestId, @Param("taskTypeId") Integer taskTypeId, @Param("departmentId") Integer departmentId);

    @Select("select count(*) from publicity_task where request_id = #{requestId} and task_status <> 'completed'")
    int countOpenByRequest(Integer requestId);

    @Select("select count(*) from publicity_task")
    int countAll();

    @Select("select count(*) from publicity_task where task_status = #{status}")
    int countByStatus(String status);

    @Select("select count(*) from publicity_task where task_status <> 'completed' and deadline < current_timestamp")
    int countOverdueOpenTasks();

    @Update("update publicity_task set department_id = #{departmentId}, task_status = #{status}, updated_at = current_timestamp where task_id = #{taskId}")
    int updateDepartmentAndStatus(@Param("taskId") Integer taskId, @Param("departmentId") Integer departmentId, @Param("status") String status);

    @Update("update publicity_task set assignee_id = #{assigneeId}, task_status = #{status}, updated_at = current_timestamp where task_id = #{taskId}")
    int updateAssigneeAndStatus(@Param("taskId") Integer taskId, @Param("assigneeId") Integer assigneeId, @Param("status") String status);

    @Update("update publicity_task set task_status = #{status}, updated_at = current_timestamp where task_id = #{taskId}")
    int updateStatus(@Param("taskId") Integer taskId, @Param("status") String status);
}
