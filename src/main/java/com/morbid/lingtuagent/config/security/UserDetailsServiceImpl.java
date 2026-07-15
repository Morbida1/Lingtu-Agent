package com.morbid.lingtuagent.config.security;

import com.morbid.lingtuagent.model.entity.User;
import com.morbid.lingtuagent.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在: " + username);
        }
        // 根据 status 字段判断账号是否启用（假设 1 为启用）
        boolean enabled = user.getStatus() != null && user.getStatus() == 1;

        // 权限简单处理：所有用户默认 "ROLE_USER"
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                enabled,
                true, true, true,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }
}