package com.deceiver.service.impl;

import com.deceiver.dao.TbUserMapper;
import com.deceiver.jedis.JedisClient;
import com.deceiver.pojo.Results;
import com.deceiver.pojo.TbUser;
import com.deceiver.pojo.TbUserExample;
import com.deceiver.service.LoginService;
import com.deceiver.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.List;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: Deceiver
 * Date: 2018-07-06
 * Time: 12:47
 */
@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private TbUserMapper userMapper;

    @Autowired
    private JedisClient jedisClient;

    private static final Integer SESSION_EXPIRE = 1800;

    @Override
    public Results userLogin(String username, String password) {
        //判断用户名和密码是否正确
        TbUserExample example = new TbUserExample();
        TbUserExample.Criteria criteria = example.createCriteria();
        //设置查询条件
        criteria.andUsernameEqualTo(username);
        //执行查询
        List<TbUser> list = this.userMapper.selectByExample(example);
        //判断数据库中是否有该用户存在
        if (list == null || list.size() == 0){
            return Results.build(400, "用户名或密码错误");
        }
        //读取用户信息
        TbUser user = list.get(0);
        //判断密码是否正确
        if (!DigestUtils.md5DigestAsHex(password.getBytes()).equals(user.getPassword()))){
            return Results.build(400, "用户名或密码错误");
        }
        //密码正确，生成token替换JSESSIONID存入redis
        String token = UUID.randomUUID().toString();
        //由于已经验证完毕，不再需要存储密码
        user.setPassword(null);

        jedisClient.set("SESSION:" + token, JsonUtils.objectToJson(user));
        jedisClient.expire("SESSION:" + token, SESSION_EXPIRE);
        //返回JSESSIONID
        return Results.ok(token);
    }
}
