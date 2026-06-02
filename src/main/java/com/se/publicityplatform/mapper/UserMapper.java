package com.se.publicityplatform.mapper;

import com.se.publicityplatform.model.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserMapper {

    @Select("select * from `user` where username = #{username} limit 1")
    User findByUsername(String username);

    @Select("select * from `user` where user_id = #{userId}")
    User findById(Integer userId);

    @Select("select * from `user` order by role, user_id")
    List<User> findAll();

    @Select("select * from `user` where role = #{role} and status = 'enabled' order by real_name")
    List<User> findByRole(String role);

    @Select("select * from `user` where department_id = #{departmentId} and role = #{role} and status = 'enabled' order by real_name")
    List<User> findByDepartmentAndRole(@Param("departmentId") Integer departmentId, @Param("role") String role);

    @Insert("""
            insert into `user` (username, password, real_name, role, department_id, phone, email, status)
            values (#{username}, #{password}, #{realName}, #{role}, #{departmentId}, #{phone}, #{email}, 'enabled')
            """)
    @Options(useGeneratedKeys = true, keyProperty = "userId")
    int insert(User user);
}
