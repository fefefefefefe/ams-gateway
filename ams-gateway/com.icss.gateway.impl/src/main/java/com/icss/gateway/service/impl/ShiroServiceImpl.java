/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package com.icss.gateway.service.impl;

import com.icss.gateway.dao.personnelManagement.UserMapper;
import com.icss.gateway.dao.personnelManagement.UserTokenMapper;
import com.icss.gateway.model.personnelManagement.User;
import com.icss.gateway.model.personnelManagement.UserToken;
import com.icss.gateway.service.ShiroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ShiroServiceImpl implements ShiroService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserTokenMapper userTokenMapper;

    /**
     * 查询当前用户的token信息
     * @param token　tokenid
     * @return
     */
    @Override
    public UserToken queryByToken(String token) {
        return userTokenMapper.queryByToken(token);
    }

    /**
     * 根据用户ID，查询用户
     * @param userId 用户id
     * @return
     */
    @Override
    public User queryUser(String userId) {
        return userMapper.selectById(userId);
    }
}
