package com.iotmars.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @Author: CJ
 * @Data: 2020/6/9 10:24
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class CommonResult<T> {

    private Integer code;
    private String message;
    private T data;

    private CommonResult(Integer code, String message){
        this(code, message, null);
    }

    public static <T> CommonResult<T> success(T data){
        return new CommonResult<>(200, "success",data);
    }

    public static <T> CommonResult<T> error(String message){
        return new CommonResult<>(500, message);
    }

    public static <T> CommonResult<T> error(Integer code, String message){
        return new CommonResult<>(code, message);
    }

    @Override
    public String toString() {
        return "{" +
                "code: " + code +
                ", message:'" + message + '\'' +
                ", data:" + data +
                '}';
    }
}
