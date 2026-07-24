package com.morbid.lingtuagent.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.morbid.lingtuagent.common.Result;
import com.morbid.lingtuagent.common.ResultCode;
import com.morbid.lingtuagent.common.exception.BusinessException;
import com.morbid.lingtuagent.model.dto.UserDTO;
import com.morbid.lingtuagent.model.entity.User;
import com.morbid.lingtuagent.model.vo.UserVO;
import com.morbid.lingtuagent.service.UserService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping
    public Result<UserVO> createUser(@Valid @RequestBody UserDTO dto) {
        return Result.success(userService.createUser(dto));
    }

    @PutMapping("/{id}")
    public Result<UserVO> updateUser(@PathVariable Long id, @Valid @RequestBody UserDTO dto) {
        return Result.success(userService.updateUser(id, dto));
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return Result.success();
    }

    @GetMapping("/deleted")
    public Result<List<UserVO>> listDeleted() {
        return Result.success(userService.listDeleted());
    }

    @PutMapping("/{id}/restore")
    public Result<Void> restore(@PathVariable Long id) {
        userService.restore(id);
        return Result.success();
    }

    @DeleteMapping("/{id}/physical")
    public Result<Void> physicalDelete(@PathVariable Long id) {
        userService.physicalDelete(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<UserVO> getUser(@PathVariable Long id) {
        return Result.success(userService.getUserVOById(id));
    }

    @GetMapping("/list")
    public Result<List<UserVO>> list() {
        return Result.success(userService.listAllUser());
    }

    @GetMapping("/page")
    public Result<IPage<UserVO>> page(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword) {
        return Result.success(userService.page(pageNum, pageSize, keyword));
    }

    @GetMapping("/profile")
    public Result<UserVO> getProfile() {
        User currentUser = getCurrentUserEntity();
        return Result.success(toUserVO(currentUser));
    }

    @PutMapping("/profile")
    public Result<UserVO> updateProfile(@RequestBody @Validated UpdateProfileRequest request) {
        User currentUser = getCurrentUserEntity();
        if (request.getNickname() != null) {
            currentUser.setNickname(request.getNickname());
        }
        userService.updateById(currentUser);
        return Result.success(toUserVO(currentUser));
    }

    @PutMapping("/password")
    public Result<Void> changePassword(@RequestBody @Validated ChangePasswordRequest request) {
        User currentUser = getCurrentUserEntity();
        if (!passwordEncoder.matches(request.getOldPassword(), currentUser.getPassword())) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "旧密码错误");
        }
        currentUser.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userService.updateById(currentUser);
        return Result.success();
    }

    private User getCurrentUserEntity() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.findByUsername(userDetails.getUsername());
        if (user == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        return user;
    }

    private UserVO toUserVO(User user) {
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setNickname(user.getNickname());
        vo.setEmail(user.getEmail());
        vo.setStatus(user.getStatus());
        vo.setCreateTime(user.getCreateTime());
        vo.setRole(user.getRole());
        return vo;
    }

    public static class UpdateProfileRequest {
        @Size(max = 30, message = "昵称不能超过30字符")
        private String nickname;

        public String getNickname() { return nickname; }
        public void setNickname(String nickname) { this.nickname = nickname; }
    }

    public static class ChangePasswordRequest {
        @NotBlank(message = "旧密码不能为空")
        private String oldPassword;
        @NotBlank(message = "新密码不能为空")
        @Size(min = 6, max = 20, message = "新密码长度6-20位")
        private String newPassword;

        public String getOldPassword() { return oldPassword; }
        public void setOldPassword(String oldPassword) { this.oldPassword = oldPassword; }
        public String getNewPassword() { return newPassword; }
        public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    }
}