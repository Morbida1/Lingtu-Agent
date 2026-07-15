package com.morbid.lingtuagent.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.morbid.lingtuagent.model.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User>{}// 无需额外方法，MyBatis-Plus 已提供 CRUD
