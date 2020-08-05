package com.zgy.service.impl;

import com.zgy.common.Const;
import com.zgy.common.ServerResponse;
import com.zgy.common.TokenCache;
import com.zgy.dao.UserMapper;
import com.zgy.pojo.User;
import com.zgy.service.IUserService;
import com.zgy.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.UUID;

@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;
    //用户登录
    @Override
    public ServerResponse<User> login(String username, String password) {
        //校验用户名是否存在
        int resultCount = userMapper.checkUserName(username);
        if(resultCount==0){
            return ServerResponse.createByErrorMessage("用户名不存在");
        }
        //预留 密码登录MD5
        String md5Password = MD5Util.MD5EncodeUtf8(password);
        User user = userMapper.SelectLogin(username, md5Password);
        if(user == null){
            return ServerResponse.createByErrorMessage("密码错误");
        }
        //吧user对象密码设置为开空
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess("登录成功",user);
    }
    //用户注册
    @Override
    public  ServerResponse<String> register(User user){
        //校验用户名是否存在
   /*     int resultCount = userMapper.checkUserName(user.getUsername());
        if(resultCount>0){
            return ServerResponse.createByErrorMessage("用户名已存在");
        }*/
        //校验用户名是否存在
        ServerResponse validResponse = checkValid(user.getUsername(), Const.USERNAME);
        if(!validResponse.isSuccess()){
            return validResponse;
        }
        //验证邮箱是否存在
   /*     resultCount= userMapper.checkEmail(user.getEmail());
        if(resultCount>0){
            return ServerResponse.createByErrorMessage("emali已存在");
        }*/
        //验证邮箱是否存在
        validResponse = checkValid(user.getEmail(), Const.EMAIL);
        if(!validResponse.isSuccess()){
            return  validResponse;
        }
        //设置为普通用户
        user.setRole(Const.Role.ROLE_CUSTOMER);
        //MD5加密 存入数据库中
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
        int resultCount = userMapper.insert(user);
        if(resultCount==0){
            return ServerResponse.createByErrorMessage("注册失败");
        }
        return  ServerResponse.createBySuccessMessage("注册成功");
    }

    //校验信息是否存在（名字,邮箱信息）
    @Override
    public ServerResponse<String> checkValid(String str,String type){
        //判断type是否为空
        if(StringUtils.isNotBlank(type)) {
            if (Const.USERNAME.equals(type)) {
                int resultCount = userMapper.checkUserName(str);
                if (resultCount > 0) {
                    return ServerResponse.createByErrorMessage("用户名已存在");
                }
            }
            if(Const.EMAIL.equals(str)){
                int resultCount = userMapper.checkEmail(str);
                if(resultCount>0){
                    return ServerResponse.createByErrorMessage("email已存在");
                }
            }
            return ServerResponse.createBySuccessMessage("校验成功");
        }else{
            return ServerResponse.createByErrorMessage("参数错误");
        }

    }

    //用户忘记密码 获取找回(更改)密码问题
    @Override
    public ServerResponse<String> selectQuestion(String username){
        //首先笑校验下用户是否存在
        ServerResponse validResponse = this.checkValid(username,Const.USERNAME);
        if(validResponse.isSuccess()){
            //用户不存在
            return ServerResponse.createByErrorMessage("用户不存在");
        }
        String question = userMapper.selectQuestionByUsername(username);
        if(org.apache.commons.lang3.StringUtils.isNotBlank(question)){
            return ServerResponse.createBySuccess(question);
        }
        return ServerResponse.createByErrorMessage("找回密码的问题是空的");

    }

    //校验用户找回（更改）密码问题的答案（answer）是否正确 并初始化token设置有效时间（使用本地缓存）
    @Override
    public ServerResponse<String>  checkAnswer(String username,String question,String answer){

        String resultCount = userMapper.checkAnswer(username,question);
        //当resultCount大于0，说明该用户的密码及密码答案是正确的
        if (resultCount.equals(answer)){
            //初始化令牌
            String forgetToken = UUID.randomUUID().toString();
            //调用本地初始化方法设置forgetToken
            TokenCache.setKey(TokenCache.TOKEN_PREFIX+username,forgetToken);
            return ServerResponse.createBySuccess(forgetToken);

        }
        return ServerResponse.createByErrorMessage("问题答案错误");
    }
    //忘记密码重置密码
    @Override
    public ServerResponse forgetRestPassword(String username,String passwordNew,String forgetToken){
        if (StringUtils.isBlank(forgetToken)){
            return ServerResponse.createByErrorMessage("参数错误，token需要传递");
        }
        ServerResponse checkValidUsername = checkValid(username,Const.USERNAME);
        if(checkValidUsername.isSuccess()){
            //如果用户不存在
            return  ServerResponse.createByErrorMessage("用户不存在");
        }
        String token = TokenCache.getKey(TokenCache.TOKEN_PREFIX+username);
        if(StringUtils.isBlank(token)){
            return ServerResponse.createByErrorMessage("token无效或者过期");
        }
        //比较传forgetToken和本地缓存拿出来token 是否相等
        if(StringUtils.equals(forgetToken,token)){
            //如果相等 使用md5加密新密码
            String md5Password = MD5Util.MD5EncodeUtf8(passwordNew);
            int rowCount = userMapper.updatePasswordByUsername(username,md5Password);
            if(rowCount>0){
                return ServerResponse.createBySuccessMessage("修改密码成功");
            }
        }else{
             return ServerResponse.createByErrorMessage("token错误，请重新获取重置密码的token");
        }
        return ServerResponse.createBySuccessMessage("修改密码失败");
    }
    //登录状态下 重置密码
    public ServerResponse<String> resetpassword(String passwordold,String passwordNew,User user){
        //防止横向越权，要校验一下这个的旧密码，一定要指定是这个用户，因为我们会查询一个count（1），如果不指定id，那么结果就是true count>0
        int resultCount = userMapper.checkPassword(MD5Util.MD5EncodeUtf8(passwordold), user.getId());
        //如果resultCount==0 说明用密码和id没查到数据 就是数据错误的
        if(resultCount==0){
            return  ServerResponse.createByErrorMessage("旧密码错误");
        }
        user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
        int updateCount = userMapper.updateByPrimaryKeySelective(user);
        if(updateCount>0){
            return  ServerResponse.createBySuccessMessage("密码更新成功");
        }
        return  ServerResponse.createByErrorMessage("旧密码错误");
    }

    //更新个人信息
    public  ServerResponse<User> updateInfomtion(User user){
        //username是不能被更新的
        //email也要进行校验，校验新的email是否已经存在（校验时排除当前这个用的email）
        int resultCount =userMapper.checkEmailByUserId(user.getEmail(),user.getId());
        if(resultCount > 0){
            return ServerResponse.createByErrorMessage("email已存在，请更换email再尝试更新");
        }
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setAnswer(user.getAnswer());
        int updateCount = userMapper.updateByPrimaryKeySelective(updateUser);
        if (updateCount>0) {
            return ServerResponse.createBySuccess("更新个人信息成功",updateUser);
        }
            return ServerResponse.createByErrorMessage("更新个人信息失败");
    }

    //获取用户的详细信息
    public ServerResponse<User> getInfomation(Integer userId){
        User user = userMapper.selectByPrimaryKey(userId);
        if(user==null){
            return  ServerResponse.createByErrorMessage("找不到当前用户");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess(user);

    }

    //backend  校验是否是管理员
    public ServerResponse  checkAdminRole(User user){
        if(user != null && user.getRole().intValue()== Const.Role.ROLE_ADMIN){
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }
}
