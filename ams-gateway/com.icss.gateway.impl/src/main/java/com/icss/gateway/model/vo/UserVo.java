package com.icss.gateway.model.vo;

import com.icss.gateway.model.personnelManagement.User;
import lombok.Data;


/**
 * @author wyj
 * @className: UserVo
 * @description:
 * @date 2022/3/18 12:49
 */
@Data
public class UserVo extends User {
    /**
     * 原密码
     */
    private String password;
    /**
     * 新密码
     */
    private String newPassword;
}
