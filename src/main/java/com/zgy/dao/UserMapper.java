package com.zgy.dao;

import com.zgy.pojo.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);
    //核对用户是否存在
    int checkUserName(String username);
    //核对邮箱是否存在
    int checkEmail(String email);
    //跟着账号和密码查询用户数据
    User SelectLogin(@Param("username") String username,@Param("password") String password);
    //获取找回（更改）密码问题
    String selectQuestionByUsername(String username);
    //查询找回（更改）密码问题及答案是否正确
   /* int checkAnswer(@Param("username") String username,@Param("question") String question,@Param("answer") String answer);*/
    String checkAnswer(@Param("username") String username,@Param("question") String question);
    //修改密码
    int updatePasswordByUsername(@Param("username")String username,@Param("passwordNow")String passwordNow);
    //登录状态下 重置密码
    int checkPassword(@Param("password")String password,@Param("userId")Integer userId);
    //校验email
    int checkEmailByUserId(@Param("email")String email,@Param("userId")Integer userId);
}
