package com.deceiver.service;

import com.deceiver.pojo.Results;
import com.deceiver.pojo.TbUser;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: Deceiver
 * Date: 2018-07-06
 * Time: 10:26
 */
public interface RegisterService {

    Results checkData(String param, int type);
    Results register(TbUser user);
}
