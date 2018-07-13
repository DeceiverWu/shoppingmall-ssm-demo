package com.deceiver.service;

import com.deceiver.pojo.Results;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: Deceiver
 * Date: 2018-07-06
 * Time: 16:11
 */
public interface TokenService {

    Results getUserByToken(String token);
}
