package com.se.publicityplatform.mapper;

import com.se.publicityplatform.model.PublicityRequest;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface PublicityRequestMapper {

    @Insert("""
            insert into publicity_request
            (applicant_id, activity_name, activity_time, activity_location, activity_content,
             publicity_types, contact_name, contact_phone, deadline, attachment_url, status)
            values
            (#{applicantId}, #{activityName}, #{activityTime}, #{activityLocation}, #{activityContent},
             #{publicityTypes}, #{contactName}, #{contactPhone}, #{deadline}, #{attachmentUrl}, #{status})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "requestId")
    int insert(PublicityRequest request);

    @Select("select * from publicity_request where request_id = #{requestId}")
    PublicityRequest findById(Integer requestId);

    @Select("select * from publicity_request order by created_at desc")
    List<PublicityRequest> findAll();

    @Select("select * from publicity_request where applicant_id = #{applicantId} order by created_at desc")
    List<PublicityRequest> findByApplicant(Integer applicantId);

    @Select("""
            select distinct r.* from publicity_request r
            join publicity_task t on t.request_id = r.request_id
            where t.department_id = #{departmentId}
            order by r.created_at desc
            """)
    List<PublicityRequest> findByTaskDepartment(Integer departmentId);

    @Select("""
            select distinct r.* from publicity_request r
            join publicity_task t on t.request_id = r.request_id
            where t.assignee_id = #{assigneeId}
            order by r.created_at desc
            """)
    List<PublicityRequest> findByTaskAssignee(Integer assigneeId);

    @Select("select * from publicity_request where status = 'pending_review' order by created_at")
    List<PublicityRequest> findPendingReview();

    @Update("update publicity_request set status = #{status}, updated_at = current_timestamp where request_id = #{requestId}")
    int updateStatus(@Param("requestId") Integer requestId, @Param("status") String status);

    @Select("select count(*) from publicity_request")
    int countAll();
}
