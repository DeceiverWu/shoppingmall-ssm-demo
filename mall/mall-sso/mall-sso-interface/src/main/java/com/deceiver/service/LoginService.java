package com.deceiver.service;

import com.deceiver.pojo.Results;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: Deceiver
 * Date: 2018-07-06
 * Time: 12:02
 */
public interface LoginService {

    Results userLogin(String username, String password);
}
