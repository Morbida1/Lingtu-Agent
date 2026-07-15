package com.morbid.lingtuagent.controller;

import com.morbid.lingtuagent.common.exception.BusinessException;
import com.morbid.lingtuagent.common.Result;
import com.morbid.lingtuagent.common.ResultCode;
import com.morbid.lingtuagent.model.entity.User;
import com.morbid.lingtuagent.model.dto.LoginRequest;
import com.morbid.lingtuagent.model.dto.RegisterRequest;
import com.morbid.lingtuagent.model.vo.LoginVO;
import com.morbid.lingtuagent.model.vo.UserVO;
import com.morbid.lingtuagent.service.UserService;
import com.morbid.lingtuagent.util.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public Result<UserVO> register(@Valid @RequestBody RegisterRequest request) {
        // 检查用户名是否已存在
        if (userService.findByUsername(request.getUsername()) != null) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "用户名已存在");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setNickname(request.getNickname());
        user.setEmail(request.getEmail());
        user.setStatus(1); // 默认启用
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());

        boolean saved = userService.save(user);
        if (!saved) {
            throw new BusinessException("注册失败，请稍后重试");
        }

        UserVO vo = toUserVO(user);
        return Result.success(vo);
    }

    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginRequest request) {
        // 1. 认证（用户名密码）
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 2. 生成 Token
        User user = userService.findByUsername(request.getUsername());
        String token = jwtUtils.generateToken(user.getId(), user.getUsername());

        // 3. 返回 UserVO + token
        UserVO userVO = toUserVO(user);
        LoginVO loginVO = new LoginVO(token, userVO);
        return Result.success(loginVO);
    }

    @GetMapping("/me")
    public Result<UserVO> getCurrentUser() {
        // 从 SecurityContext 获取当前用户信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.findByUsername(userDetails.getUsername());
        if (user == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        return Result.success(toUserVO(user));
    }

    // 转换方法
    private UserVO toUserVO(User user) {
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setNickname(user.getNickname());
        vo.setEmail(user.getEmail());
        vo.setStatus(user.getStatus());
        vo.setCreateTime(user.getCreateTime());
        return vo;
    }
}