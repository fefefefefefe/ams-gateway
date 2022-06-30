package com.icss.gateway.service.impl.personnelManagement;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.icss.gateway.dao.personnelManagement.MenuMapper;
import com.icss.gateway.model.personnelManagement.Menu;
import com.icss.gateway.service.personnelManagement.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 菜单表 服务实现类
 * </p>
 *
 * @author niqingjie
 * @since 2022-02-23
 */
@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {

    /**
     * 菜单dao接口
     */
    @Autowired
    private MenuMapper menuMapper;
    /**
     * 获取当前登录人所拥有的菜单权限
     * @param userId 用户编号
     * @return 返回菜单列表
     */
    @Override
    public List<Menu> getUserMenuList(String userId) {
        return menuMapper.getCurrentUserMenuList(userId);
    }
}
