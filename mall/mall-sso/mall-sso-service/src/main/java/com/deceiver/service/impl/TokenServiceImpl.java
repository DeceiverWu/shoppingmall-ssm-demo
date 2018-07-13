package com.deceiver.service.impl;

import com.deceiver.jedis.JedisClient;
import com.deceiver.pojo.Results;
import com.deceiver.pojo.TbUser;
import com.deceiver.service.TokenService;
import com.deceiver.utils.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: Deceiver
 * Date: 2018-07-06
 * Time: 16:11
 */
public class TokenServiceImpl implements TokenService {

    @Autowired
    private JedisClient jedisClient;

    private static final Integer SESSION_EXPIRE = 1800;

    @Override
    public Results getUserByToken(String token) {
        //获取redis中存储的用户信息
        String json = this.jedisClient.get("SESSION:" + token);
        //判断用户信息是否已登录
        if (StringUtils.isBlank(json)){
            return Results.build(201, "用户登录已经过期");
        }
        //取到用户信息更新token的过期时间
        this.jedisClient.expire("SESSION:" + token, SESSION_EXPIRE);
        TbUser user = JsonUtils.jsonToPojo(json, TbUser.class);
        return Results.ok(user);
    }
}
