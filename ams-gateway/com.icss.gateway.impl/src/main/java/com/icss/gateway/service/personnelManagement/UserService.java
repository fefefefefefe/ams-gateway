package com.icss.gateway.service.personnelManagement;

import com.baomidou.mybatisplus.extension.service.IService;
import com.icss.gateway.model.personnelManagement.User;
import com.icss.gateway.model.vo.Result;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author niqingjie
 * @since 2022-02-23
 */
public interface UserService extends IService<User> {

   /**
   *@Description 人员管理导入
   *@Param:[file] 文件
   *@return:com.icss.dataanalysis.model.vo.Result<com.icss.dataanalysis.model.personnelManagement.User>
   *@Author:weiyujie
   *@Date:2022/3/15
   */
    Result<User> importData(MultipartFile file) throws Exception;

}
