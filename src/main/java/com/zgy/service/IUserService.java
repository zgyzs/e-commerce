package com.zgy.service;

import com.zgy.common.ServerResponse;
import com.zgy.pojo.User;
//用户接口
public interface IUserService {
    //用户登录
    ServerResponse<User> login(String username, String password);
    //用户注册
      ServerResponse<String> register(User user);
    //校验邮箱或名字是否存在
     ServerResponse<String> checkValid(String str,String type);
    //用户忘记密码 获取找回（更改）密码问题
     ServerResponse<String> selectQuestion(String username);
    //校验用户找回（更改）密码问题的答案（answer）是否正确
     ServerResponse<String> checkAnswer(String username,String question,String answer);
    //忘记密码重置密码
     ServerResponse forgetRestPassword(String username,String passwordNew,String forgetToken);
    //登录状态下 重置密码
     ServerResponse<String> resetpassword(String passwordold,String passwordNew,User user);
    //更新个人信息
      ServerResponse<User> updateInfomtion(User user);
    //获取用户的详细信息
     ServerResponse<User> getInfomation(Integer userId);
    //backend  校验是否是管理员
    ServerResponse  checkAdminRole(User user);
}
