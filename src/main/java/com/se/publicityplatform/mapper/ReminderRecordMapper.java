package com.se.publicityplatform.mapper;

import com.se.publicityplatform.model.ReminderRecord;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ReminderRecordMapper {

    @Insert("""
            insert into reminder_record
            (task_id, receiver_id, reminder_type, reminder_content, send_status, planned_time, sent_at)
            values
            (#{taskId}, #{receiverId}, #{reminderType}, #{reminderContent}, #{sendStatus}, #{plannedTime}, #{sentAt})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "reminderId")
    int insert(ReminderRecord reminderRecord);

    @Select("""
            select * from reminder_record
            where receiver_id = #{receiverId}
            order by planned_time desc
            limit #{limit}
            """)
    List<ReminderRecord> findRecentByReceiver(@Param("receiverId") Integer receiverId, @Param("limit") int limit);
}
