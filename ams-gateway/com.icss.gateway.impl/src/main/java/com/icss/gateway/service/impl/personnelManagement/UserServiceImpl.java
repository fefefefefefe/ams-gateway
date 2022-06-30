package com.icss.gateway.service.impl.personnelManagement;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.icss.gateway.dao.personnelManagement.UserMapper;
import com.icss.gateway.model.personnelManagement.User;
import com.icss.gateway.model.vo.Result;
import com.icss.gateway.service.personnelManagement.UserService;
import com.icss.gateway.utils.EasyPoiUtils;
import com.icss.gateway.utils.LoginUserInfo;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import utils.DateUtil;
import utils.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author niqingjie
 * @since 2022-02-23
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    /**
    *@Description 人员信息导入
    *@Param:[file] 文件
    *@return:com.icss.dataanalysis.model.vo.Result<com.icss.dataanalysis.model.personnelManagement.User>
    *@Author:weiyujie
    *@Date:2022/3/15
    */
    @Override
    public Result<User> importData(MultipartFile file) throws Exception {
        Result<User> resultMsg = new Result<User>();
        List<User> userList = new ArrayList<>();
        //获取Execl数据转成List<User>对象
        List<User> resultList = EasyPoiUtils.importExcel(file, 0, 1, User.class);
        //提示信息
        List<String> msgList = new ArrayList<>();
        for (int i = 0; i < resultList.size(); i++) {
            User user = resultList.get(i);
            QueryWrapper<User> queryWrapper = new QueryWrapper();
            queryWrapper.eq(StringUtil.isNotEmpty(user.getUserLoginName())," USER_LOGIN_NAME", user.getUserLoginName());
            queryWrapper.eq("IS_DELETE", 0);
            List<User> queryList = list(queryWrapper);
            if (queryList.size() > 0) {//角色不唯一
                //用户账号重复
                msgList.add("第" + (i+1) + "行用户账号已存在");
                continue;
            }
            user.setUserId(StringUtil.getGuuid());
            user.setCreateUserId(LoginUserInfo.getLoginUserId());
            user.setCreateUserName(LoginUserInfo.getLoginUserName());
            user.setUpdateUserId(LoginUserInfo.getLoginUserId());
            user.setUpdateUserName(LoginUserInfo.getLoginUserName());
            user.setCreateTime(DateUtil.getCurrentTime());
            user.setUpdateTime(DateUtil.getCurrentTime());
            user.setIsDelete(0);
            userList.add(user);
        }
        if (msgList.size() > 0) {//存在提示信息不插入数据，提示前台哪个数据有问题
            resultMsg.setSuccess(false);
            resultMsg.setMessage(msgList.toString());
        } else {
            boolean result = saveBatch(userList);
            if (result) {
                resultMsg.setSuccess(true);
                resultMsg.setCode(200);
            }
        }
        return resultMsg;
    }
}
