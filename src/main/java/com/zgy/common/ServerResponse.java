package com.zgy.common;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.Serializable;
//通用响应 @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)表示序列化Json的时候，如果是null的对象，key也会消失
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ServerResponse<T> implements Serializable {
    //状态
    private int status;
    //提示
    private String msg;
    //返回值
    private T data;

    private  ServerResponse(int status){
        this.status=status;
    }

    private  ServerResponse(int status,T data){
        this.status=status;
        this.data=data;
    }

    private  ServerResponse(int status,String msg,T data){
        this.status=status;
        this.msg=msg;
        this.data=data;
    }
    private  ServerResponse(int status,String msg){
        this.status=status;
        this.msg=msg;

    }
    //如果是成功的响应，则回枚举中的 SUCCESS.getCode()  加JsonIgnore使该方法不会出现返回给前端的json对象中
    @JsonIgnore
    public boolean isSuccess(){
        return this.status == ResponseCode.SUCCESS.getCode();
    }

    public int getStatus(){
        return status;
    }

    public  T getData(){
        return  data;
    }

    public  String getMsg(){
        return  msg;
    }
  //成功的回调的几种情况（需要返回的参数不同）
    public static <T>  ServerResponse<T> createBySuccess(){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode());
    }

    public static <T> ServerResponse<T> createBySuccessMessage(String msg){
            return  new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),msg);
    }
    public static <T> ServerResponse<T> createBySuccess(T data){
        return  new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),data);
    }

    public static <T> ServerResponse<T> createBySuccess(String msg,T data){
        return  new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),msg,data);
    }

    //失败的回调几种情况
    public  static  <T> ServerResponse<T>  createByError(){
        return new ServerResponse<T>(ResponseCode.ERROR.getCode(),ResponseCode.ERROR.getDesc());
    }

    public  static  <T> ServerResponse<T>  createByErrorMessage(String errorMessage){
        return new ServerResponse<T>(ResponseCode.ERROR.getCode(),errorMessage);
    }

    public  static  <T> ServerResponse<T>  createByErrorCodeMessage(int errorCode ,String errorMessage){
        return new ServerResponse<T>(errorCode,errorMessage);
    }

}


