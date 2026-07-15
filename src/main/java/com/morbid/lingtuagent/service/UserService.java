package com.morbid.lingtuagent.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.morbid.lingtuagent.model.dto.UserDTO;
import com.morbid.lingtuagent.model.entity.User;
import com.morbid.lingtuagent.model.vo.UserVO;

import java.util.List;

public interface UserService extends IService<User> {
    UserVO createUser(UserDTO userDTO);
    UserVO updateUser(Long id, UserDTO userDTO);
    void deleteUser(Long id);
    UserVO getUserVOById(Long id);
    List<UserVO> listAllUser();
    IPage<UserVO> page(int pageNum, int pageSize, String keyword);
    User findByUsername(String username);
}