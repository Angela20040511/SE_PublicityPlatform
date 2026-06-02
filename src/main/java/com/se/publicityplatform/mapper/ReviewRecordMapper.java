package com.se.publicityplatform.mapper;

import com.se.publicityplatform.model.ReviewRecord;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ReviewRecordMapper {

    @Insert("""
            insert into review_record (target_type, target_id, reviewer_id, review_result, review_comment)
            values (#{targetType}, #{targetId}, #{reviewerId}, #{reviewResult}, #{reviewComment})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "reviewId")
    int insert(ReviewRecord reviewRecord);

    @Select("""
            select * from review_record
            where target_type = #{targetType} and target_id = #{targetId}
            order by reviewed_at desc
            """)
    List<ReviewRecord> findByTarget(@Param("targetType") String targetType, @Param("targetId") Integer targetId);
}
