package com.iotmars.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @description:
 * @author: xsh
 * @time: 2020/2/11 18:35
 */
@Data
public class Result<T> implements Serializable {

    // 响应业务状态
    private Integer status;

    // 响应消息
    private String msg;

    // 响应中的数据
    private T data;

    private Result(Integer status,String msg,T data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    public static Result ok(){
        return new Result<>(200,"success",null);
    }

    public static Result error(Integer code,String msg){
        return new Result<>(code,msg,null);
    }

}
