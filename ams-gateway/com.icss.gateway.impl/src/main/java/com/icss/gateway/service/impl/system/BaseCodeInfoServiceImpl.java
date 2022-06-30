package com.icss.gateway.service.impl.system;

import com.icss.gateway.model.system.BaseCodeInfo;
import com.icss.gateway.dao.system.BaseCodeInfoMapper;
import com.icss.gateway.service.system.BaseCodeInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 基础数据_通用代码表 服务实现类
 * </p>
 *
 * @author weiyujie
 * @since 2022-04-13
 */
@Service
public class BaseCodeInfoServiceImpl extends ServiceImpl<BaseCodeInfoMapper, BaseCodeInfo> implements BaseCodeInfoService {

    @Autowired
    private BaseCodeInfoMapper baseCodeInfoMapper;

    @Override
    public List<BaseCodeInfo> selectWholeTree(String codeSortValue) {
        return baseCodeInfoMapper.selectWholeTree(codeSortValue);
    }

    @Override
    public List<BaseCodeInfo> selectTree(BaseCodeInfo baseCodeInfo) {
        //查询树结构
        List<BaseCodeInfo> treeList = baseCodeInfoMapper.selectTree(baseCodeInfo);
        return treeList;
    }
}
