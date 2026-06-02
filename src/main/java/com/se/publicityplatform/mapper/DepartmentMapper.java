package com.se.publicityplatform.mapper;

import com.se.publicityplatform.model.Department;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface DepartmentMapper {

    @Select("select * from department order by department_id")
    List<Department> findAll();

    @Select("select * from department where department_id = #{departmentId}")
    Department findById(Integer departmentId);

    @Insert("insert into department (department_name, leader_id, description) values (#{departmentName}, #{leaderId}, #{description})")
    @Options(useGeneratedKeys = true, keyProperty = "departmentId")
    int insert(Department department);

    @Update("update department set leader_id = #{leaderId} where department_id = #{departmentId}")
    int updateLeader(@Param("departmentId") Integer departmentId, @Param("leaderId") Integer leaderId);
}
