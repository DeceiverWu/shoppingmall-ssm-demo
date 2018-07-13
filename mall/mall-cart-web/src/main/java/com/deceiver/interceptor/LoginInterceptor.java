package com.deceiver.interceptor;

import com.deceiver.pojo.Results;
import com.deceiver.pojo.TbUser;
import com.deceiver.service.TokenService;
import com.deceiver.utils.CookieUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: Deceiver
 * Date: 2018-07-12
 * Time: 15:18
 */
public class LoginInterceptor implements HandlerInterceptor {

    @Autowired
    private TokenService tokenService;

    /**
     * 拦截所有请求，从用户子系统获取登录标识
     * 三种判断情况
     * 1.没有token，即登录标识不存在，放行创建
     * 2.redis中不存在用户信息，说明登录标识已经过期，放行创建
     * 3.不存在 1 和 2 的问题，说明登录信息有效，写入request, 防止重复创建用户标识
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 前处理，执行handler之前执行此方法。
        //返回true，放行	false：拦截

        //从Cookie中读取token(即JSESSIONID)
        String token = CookieUtils.getCookieValue(request, "token");
        //如果没有token，未登录状态，直接放行
        if (StringUtils.isBlank(token)){
            return true;
        }

        //取到token，需要调用sso系统的服务，根据token取用户信息
        Results results = this.tokenService.getUserByToken(token);
        //如果没有获取到用户信息，表示登录过期
        if (results.getStatus() != 200){
            return true;
        }

        //如果取得用户信息，将用户信息保存在request中
        TbUser user = (TbUser)results.getData();
        request.setAttribute("user", user);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        //handler执行之后，返回ModeAndView之前
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //完成处理，返回ModelAndView之后。
        //可以再此处理异常
    }
}
