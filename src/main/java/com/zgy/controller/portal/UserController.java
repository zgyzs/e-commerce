package com.zgy.controller.portal;

import com.zgy.common.Const;
import com.zgy.common.ResponseCode;
import com.zgy.common.ServerResponse;
import com.zgy.pojo.User;
import com.zgy.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/user/")
public class UserController {

    @Autowired
    private IUserService iUserService;


    //用户登录
    @RequestMapping(value = "login.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> login(String username, String password, HttpSession session){
        //service-mybatis-dao
        ServerResponse<User> response = iUserService.login(username, password);
        if(response.isSuccess()){
            session.setAttribute(Const.CURRENT_USER,response.getData());
        }

        return response;
    }
    //用户退出
    @RequestMapping(value = "logout.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> logout(HttpSession session){
        session.removeAttribute(Const.CURRENT_USER);
         return ServerResponse.createBySuccess();
    }
    //用户注册
    @RequestMapping(value = "register.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> register(User user){
        return iUserService.register(user);
    }
    //校验信息（名字,邮箱等信息）
    @RequestMapping(value = "checkValid.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> checkValid(String str,String type){
        return  iUserService.checkValid(str,type);
    }
    //获取登陆用户信息
    @RequestMapping(value = "get_user_Info.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> getUserInfo(HttpSession session){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user!=null){
            return  ServerResponse.createBySuccess(user);
        }
            return  ServerResponse.createBySuccessMessage("用户未登录，无法获取当前用户信息");
    }
    //用户忘记密码 获取找回（更改）密码问题
    @RequestMapping(value = "forget_get_question.do",method = RequestMethod.POST)
    @ResponseBody
     public ServerResponse<String> forgetGetQuestion(String username){
        return iUserService.selectQuestion(username);
     }

    @RequestMapping(value = "forget_check_answer.do",method = RequestMethod.POST)
    @ResponseBody
     //校验用户找回（更改）密码问题是否正确 并初始化token设置有效时间（使用本地缓存）返回给前端
    public ServerResponse<String>  forgetCheckAnswer(String username,String question,String answer){
        return iUserService.checkAnswer(username,question,answer);
    }
    @RequestMapping(value = "forget_rest_password.do",method = RequestMethod.POST)
    @ResponseBody
    //忘记密码(token)重置密码
    public ServerResponse forgetRestPassword(String username,String passwordNew,String forgetToken)  {
      return   iUserService.forgetRestPassword(username,passwordNew,forgetToken);
    }

    @RequestMapping(value = "reset_password.do",method = RequestMethod.POST)
    @ResponseBody
    //登录状态下 重置密码
    public ServerResponse<String> resetPassword(HttpSession session,String passwordold,String passwordNew){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return  ServerResponse.createBySuccessMessage("用户未登录");
        }
        return iUserService.resetpassword(passwordold,passwordNew,user);
    }

    @RequestMapping(value = "update_infomtion.do",method = RequestMethod.POST)
    @ResponseBody
    //更新个人信息
    public  ServerResponse<User> update_infomtion(HttpSession session,User user){
        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
        if(currentUser == null){
            return  ServerResponse.createBySuccessMessage("用户未登录");
        }
        //因为前段传来的user对象中没有id，所以先把当前的user的id赋值到传进来的user中
        user.setId(currentUser.getId());
        //因为username也不能被更新，所以先把当前的username的id赋值到传进来的username中
        user.setUsername(currentUser.getUsername());
        ServerResponse<User> response = iUserService.updateInfomtion(user);
        if(response.isSuccess()){
            session.setAttribute(Const.CURRENT_USER,response.getData());
        }
        return  response;
    }

    @RequestMapping(value = "get_infomation.do",method = RequestMethod.POST)
    @ResponseBody
    //获取用户的详细信息
    public ServerResponse<User> get_infomation(HttpSession session){
        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
        if (currentUser == null){
            return  ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"未登录需要强制登录status=10");
        }
     return  iUserService.getInfomation(currentUser.getId());
    }


}
