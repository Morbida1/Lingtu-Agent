package com.morbid.lingtuagent.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.morbid.lingtuagent.common.exception.BusinessException;
import com.morbid.lingtuagent.mapper.UserMapper;
import com.morbid.lingtuagent.model.dto.UserDTO;
import com.morbid.lingtuagent.model.entity.User;
import com.morbid.lingtuagent.model.vo.UserVO;
import com.morbid.lingtuagent.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final PasswordEncoder passwordEncoder;

    @Override
    public UserVO createUser(UserDTO userDTO) {
        User user = new User();
        BeanUtils.copyProperties(userDTO, user);
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        if (user.getStatus() == null) {
            user.setStatus(1);
        }
        this.save(user);
        return convertToVO(user);
    }

    @Override
    public UserVO updateUser(Long id, UserDTO userDTO) {
        User user = this.getById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        BeanUtils.copyProperties(userDTO, user);
        if (StringUtils.hasText(userDTO.getPassword())) {
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }
        this.updateById(user);
        return convertToVO(user);
    }

    @Override
    public void deleteUser(Long id) {
        User user = this.getById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(User::getId, id)
               .set(User::getDeleted, 1)
               .set(User::getDeleteTime, LocalDateTime.now());
        this.update(wrapper);
    }

    @Override
    public UserVO getUserVOById(Long id) {
        User user = this.getById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return convertToVO(user);
    }

    @Override
    public List<UserVO> listAllUser() {
        return this.list().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public IPage<UserVO> page(int pageNum, int pageSize, String keyword) {
        Page<User> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(User::getUsername, keyword)
                    .or()
                    .like(User::getNickname, keyword);
        }
        IPage<User> userIPage = this.page(page, wrapper);
        return userIPage.convert(this::convertToVO);
    }

    @Override
    public User findByUsername(String username) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username)
                .last("LIMIT 1");
        return this.getOne(wrapper);
    }

    private UserVO convertToVO(User user) {
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        return vo;
    }

    @Override
    public List<UserVO> listDeleted() {
        return baseMapper.selectDeleted().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public void restore(Long id) {
        if (baseMapper.restoreById(id) == 0) {
            throw new BusinessException("恢复失败");
        }
    }

    @Override
    public void physicalDelete(Long id) {
        if (baseMapper.physicalDeleteById(id) == 0) {
            throw new BusinessException("物理删除失败，数据不存在");
        }
    }
}