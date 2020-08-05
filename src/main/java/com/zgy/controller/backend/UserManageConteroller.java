package com.zgy.controller.backend;

import com.zgy.common.Const;
import com.zgy.common.ServerResponse;
import com.zgy.pojo.User;
import com.zgy.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;



//后台管理员
@Controller
@RequestMapping("/manage/user")
public class UserManageConteroller {

    @Autowired
    private IUserService iUserService;

    //后台管理员登录
    @RequestMapping(value = "/login.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> login(String username, String password, HttpSession session){
        ServerResponse<User> response = iUserService.login(username, password);
        if (response.isSuccess()){
            User user = response.getData();
            if(user.getRole() == Const.Role.ROLE_ADMIN){
                //说明登录的是管理员
                session.setAttribute(Const.CURRENT_USER,user);
                return  response;
            }else {
                return ServerResponse.createByErrorMessage("不是管理员，无法登录");
            }
        }
            return response;
    }
}
