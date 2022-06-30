package com.icss.gateway.service.system;

import com.icss.gateway.model.system.BaseCodeInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 基础数据_通用代码表 服务类
 * </p>
 *
 * @author weiyujie
 * @since 2022-04-13
 */
public interface BaseCodeInfoService extends IService<BaseCodeInfo> {

    List<BaseCodeInfo> selectWholeTree(String codeSortValue);

    List<BaseCodeInfo> selectTree(BaseCodeInfo baseCodeInfo);
}
