package com.zgy.common;

public class Const {
    //用户登录后用户信息存在sission中，以CURRENT_USER当key
    public  static  final String CURRENT_USER="currentUser";

    public  static  final  String EMAIL = "email";
    public  static  final  String USERNAME = "username";
    public interface Role{
        int ROLE_CUSTOMER=0; //普通用户
        int ROLE_ADMIN=1;//管理员
    }

}
