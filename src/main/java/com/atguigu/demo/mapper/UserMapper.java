package com.atguigu.demo.mapper;

import com.atguigu.demo.pojo.User;
import com.github.abel533.mapper.Mapper;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMapper extends Mapper<User> {
}
