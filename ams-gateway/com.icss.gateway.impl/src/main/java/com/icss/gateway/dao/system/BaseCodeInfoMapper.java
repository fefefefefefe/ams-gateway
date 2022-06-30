package com.icss.gateway.dao.system;

import com.icss.gateway.model.system.BaseCodeInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 基础数据_通用代码表 Mapper 接口
 * </p>
 *
 * @author weiyujie
 * @since 2022-04-13
 */
@Repository
public interface BaseCodeInfoMapper extends BaseMapper<BaseCodeInfo> {

    List<BaseCodeInfo> selectWholeTree(@Param("codeSortValue") String codeSortValue);

    List<BaseCodeInfo> selectTree(@Param("baseCodeInfo") BaseCodeInfo baseCodeInfo);
}
