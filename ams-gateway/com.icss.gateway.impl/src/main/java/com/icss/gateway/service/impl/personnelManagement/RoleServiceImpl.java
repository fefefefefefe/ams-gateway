package com.icss.gateway.service.impl.personnelManagement;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.icss.gateway.dao.personnelManagement.RoleMapper;
import com.icss.gateway.model.personnelManagement.Role;
import com.icss.gateway.service.personnelManagement.RoleService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 角色表 服务实现类
 * </p>
 *
 * @author niqingjie
 * @since 2022-02-23
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

}
