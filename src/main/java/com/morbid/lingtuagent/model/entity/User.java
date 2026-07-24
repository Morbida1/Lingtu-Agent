package com.morbid.lingtuagent.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user")
public class User {
    @TableId(type = IdType.AUTO)  // 自增主键
    private Long id;

    @NotBlank(message = "用户名不能为空")
    private String username;
    private String password;
    private String nickname;
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;
    private String avatar;
    private String role;

    private Integer status; // 0-禁用，1-启用
    
    @TableLogic
    private Integer deleted;

    private LocalDateTime deleteTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE) // 插入和更新时自动填充更新时间
    private LocalDateTime updateTime;

}