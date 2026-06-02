package com.se.publicityplatform.mapper;

import com.se.publicityplatform.model.Submission;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface SubmissionMapper {

    @Insert("""
            insert into submission (task_id, submitter_id, file_url, description, version_no, status)
            values (#{taskId}, #{submitterId}, #{fileUrl}, #{description}, #{versionNo}, #{status})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "submissionId")
    int insert(Submission submission);

    @Select("select * from submission where submission_id = #{submissionId}")
    Submission findById(Integer submissionId);

    @Select("""
            select * from submission
            where task_id = #{taskId}
            order by version_no desc, submitted_at desc
            limit 1
            """)
    Submission findLatestByTask(Integer taskId);

    @Select("select coalesce(max(version_no), 0) from submission where task_id = #{taskId}")
    int maxVersionNo(Integer taskId);

    @Select("select * from submission where task_id = #{taskId} order by version_no desc")
    List<Submission> findByTask(Integer taskId);

    @Update("update submission set status = #{status} where submission_id = #{submissionId}")
    int updateStatus(@Param("submissionId") Integer submissionId, @Param("status") String status);
}
