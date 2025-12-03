package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

@Mapper
public interface UserMapper {

    @Select("SELECT * FROM user WHERE openid = #{openid} ")
    User getByOpenid(String openid);

    void insert(User user);

    @Select("SELECT * FROM user WHERE id = #{userId} ")
    User getById(Long userId);

    @Select("select count(*) from user where create_time >= #{begin} and create_time <= #{end}")
    Long countNewUserByMap(Map map);

    @Select("select count(*) from user where create_time < #{end}")
    Long countTotalUserByMap(Map totalMap);
}
