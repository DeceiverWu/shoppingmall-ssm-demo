package com.deceiver.controller;

import com.deceiver.pojo.Results;
import com.deceiver.service.LoginService;
import com.deceiver.utils.CookieUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: Deceiver
 * Date: 2018-07-06
 * Time: 13:01
 */
@Controller
public class LoginController {

    @Autowired
    private LoginService loginService;

    private static final String TOKEN_KEY = "token";

    @RequestMapping(value = "/user/login", method = RequestMethod.POST)
    @ResponseBody
    public Results login(String username, String password, HttpServletRequest request, HttpServletResponse response){
        Results results = this.loginService.userLogin(username, password);
        //判断是否成功
        if (results.getStatus() == 200){
            String token = results.getData().toString();
            //登录成功，将token写入Cookie代替JSESSIONID
            CookieUtils.setCookie(request, response, TOKEN_KEY, token);
        }
        return results;
    }

    @RequestMapping("/page/login")
    public String showLogin(String redirect, Model model) {
        model.addAttribute("redirect", redirect);
        return "login";
    }
}
