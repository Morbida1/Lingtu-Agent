package com.morbid.lingtuagent.common.exception;

import lombok.extern.slf4j.Slf4j;
import com.morbid.lingtuagent.common.Result;
import com.morbid.lingtuagent.common.ResultCode;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice// 全局异常处理器
public class GlobalExceptionHandler {
    //处理自定义业务异常
    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e) {
        log.error("业务异常：{}", e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

    //处理认证异常（用户名密码错误等）
    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Result<Void> handleAuthenticationException(AuthenticationException e) {
        log.warn("认证失败：{}", e.getMessage());
        if (e instanceof BadCredentialsException) {
            return Result.error(ResultCode.UNAUTHORIZED.getCode(), "用户名或密码错误");
        }
        return Result.error(ResultCode.UNAUTHORIZED.getCode(), "认证失败：" + e.getMessage());
    }
    //处理 @Valid 校验异常（用于 RequestBody）
    @Valid
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Map<String,String>> handleException(MethodArgumentNotValidException e) {
        // 将错误信息封装成 Map，方便前端根据字段名精准提示
        Map<String, String> errors = e.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        FieldError::getDefaultMessage,
                        (existing, replacement) -> existing + "; " + replacement // 处理同一个字段多个错误的情况
                ));
        log.warn("参数校验异常：{}", errors);
        return Result.error(ResultCode.BAD_REQUEST.getCode(), "参数校验失败", errors);
    }
    // 处理 @Validated 校验异常（用于 RequestParam/PathVariable 等）
    @ExceptionHandler(ConstraintViolationException.class)
    public Result<Void> handleConstraintViolation(ConstraintViolationException e) {
        String msg = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining("；"));
        log.error("参数校验异常：{}", msg);
        return Result.error(ResultCode.BAD_REQUEST.getCode(), msg);
    }
    // 处理静态资源未找到异常（如 favicon.ico）
    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Result<Void> handleNoResourceFound(NoResourceFoundException e) {
        log.debug("静态资源未找到：{}", e.getResourcePath());
        return Result.error(ResultCode.NOT_FOUND);
    }
    // 处理通用异常（兜底）
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        log.error("系统异常：{} - {}", e.getClass().getName(), e.getMessage(), e);
        return Result.error(ResultCode.INTERNAL_ERROR);
    }
}