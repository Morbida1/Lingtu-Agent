package com.morbid.lingtuagent.common;

import lombok.Data;

@Data
public class Result<T> {
    private Integer code;
    private String message;
    private T data;
    // 私有构造函数，只能通过静态方法获取对象
    private Result() {}
    // 静态方法，用于创建成功的结果对象
    // 成功时，data不为null，用于返回成功的结果
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(ResultCode.SUCCESS.getCode());
        result.setMessage(ResultCode.SUCCESS.getMessage());
        result.setData(data);
        return result;
    }
    // 成功时，data为null，用于返回成功的结果
    public static <T> Result<T> success() {
        return success(null);
    }
    // 失败（自定义状态码和消息）
    public static <T> Result<T> error(Integer code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }
    // 失败（自定义状态码和消息）
    public static <T> Result<T> error(Integer code, String message,T data) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        result.setData(data);// 允许在失败时携带详细数据
        return result;
    }
    // 失败（使用 ResultCode 枚举）
    public static <T> Result<T> error(ResultCode resultCode) {
        return error(resultCode.getCode(), resultCode.getMessage());
    }
}

