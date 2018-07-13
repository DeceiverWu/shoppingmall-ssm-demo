package com.deceiver.service.impl;

import com.deceiver.dao.TbUserMapper;
import com.deceiver.pojo.Results;
import com.deceiver.pojo.TbUser;
import com.deceiver.pojo.TbUserExample;
import com.deceiver.service.RegisterService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: Deceiver
 * Date: 2018-07-06
 * Time: 10:30
 */
@Service
public class RegisterServiceImpl implements RegisterService {

    @Autowired
    private TbUserMapper userMapper;

    @Override
    public Results checkData(String param, int type) {
        TbUserExample example = new TbUserExample();
        TbUserExample.Criteria criteria = example.createCriteria();
        //1：用户名 2：手机号 3：邮箱
        if (type == 1){
            criteria.andUsernameEqualTo(param);
        }else if (type == 2){
            criteria.andPhoneEqualTo(param);
        }else if (type == 3){
            criteria.andEmailEqualTo(param);
        }else {
            return Results.build(400, "数据类型错误");
        }
        //执行查询
        List<TbUser> list = this.userMapper.selectByExample(example);
        if (list != null && list.size() > 0){
            return Results.ok(false);
        }
        return Results.ok(true);
    }

    @Override
    public Results register(TbUser user) {
        //数据有效性校验
        if (StringUtils.isBlank(user.getUsername()) || StringUtils.isBlank(user.getPassword()) || StringUtils.isBlank(user.getPhone())){
            return Results.build(400, "用户数据不完整，注册失效");
        }
        //1：用户名 2：手机号 3：邮箱
        Results results = checkData(user.getUsername(), 1);
        if (!(boolean)results.getData()){
            return Results.build(400, "此用户名已被占用");
        }
        results = checkData(user.getPhone(), 2);
        if (!(boolean)results.getData()){
            return Results.build(400, "此手机已经被注册");
        }
        //补全pojo属性
        user.setCreated(new Date());
        user.setUpdated(new Date());
        //密码进行加密
        String md5Pass = DigestUtils.md5DigestAsHex(user.getPassword().getBytes());
        user.setPassword(md5Pass);
        //将用户数据存入数据库
        this.userMapper.insert(user);
        return Results.ok();
    }
}
