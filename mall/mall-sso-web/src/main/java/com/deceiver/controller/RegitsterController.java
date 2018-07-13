package com.deceiver.controller;

import com.deceiver.pojo.Results;
import com.deceiver.pojo.TbUser;
import com.deceiver.service.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: Deceiver
 * Date: 2018-07-06
 * Time: 11:11
 */
@Controller
@RequestMapping("/user")
public class RegitsterController {

    @Autowired
    private RegisterService registerService;

    @RequestMapping("/check/{param}/{type}")
    @ResponseBody
    public Results checkData(@PathVariable String param, @PathVariable Integer type){
        return this.registerService.checkData(param, type);
    }

    @RequestMapping("/register")
    @ResponseBody
    public Results register(TbUser user){
        return this.registerService.register(user);
    }

    @RequestMapping("/page/register")
    public String showRegister() {
        return "register";
    }
}
