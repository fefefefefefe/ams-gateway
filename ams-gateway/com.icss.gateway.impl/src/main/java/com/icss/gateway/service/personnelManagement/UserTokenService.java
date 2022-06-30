package com.icss.gateway.service.personnelManagement;

import com.baomidou.mybatisplus.extension.service.IService;
import com.icss.gateway.model.personnelManagement.UserToken;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author niqingjie
 * @since 2022-02-25
 */
public interface UserTokenService extends IService<UserToken> {

    UserToken createToken(String userId);

    void logout(String userId);
}
