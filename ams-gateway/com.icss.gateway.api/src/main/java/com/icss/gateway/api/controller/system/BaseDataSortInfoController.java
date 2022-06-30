package com.icss.gateway.api.controller.system;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.icss.gateway.model.vo.Result;

import com.icss.gateway.model.system.BaseDataSortInfo;
import com.icss.gateway.service.system.BaseDataSortInfoService;
import utils.StringUtil;
import utils.UuidUtils;

/**
 * <p>
 * 基础数据_代码类别表 前端控制器
 * </p>
 *
 * @author weiyujie
 * @since 2022-04-13
 */

@Api("基础数据_代码类别表 服务")
@RequestMapping("/baseDataSortInfo")
@RestController
public class BaseDataSortInfoController {

    @Autowired
    private BaseDataSortInfoService baseDataSortInfoService;

    @ApiOperation("创建单个BaseDataSortInfo")
    @PostMapping("/add")
    public Result insert(@RequestBody BaseDataSortInfo baseDataSortInfo) {
        //校验代码类别编码是否存在
        BaseDataSortInfo baseDataSortInfo2 = new BaseDataSortInfo();
        baseDataSortInfo2.setDataSortValue(baseDataSortInfo.getDataSortValue());
        BaseDataSortInfo zhBaseDataSortInfo1 = baseDataSortInfoService.getOne(new QueryWrapper<>(baseDataSortInfo2));
        if (zhBaseDataSortInfo1 != null) {
            return Result.error("代码类型编码已存在");
        }
        baseDataSortInfo.setDataSortId(UuidUtils.getUUID());
        return Result.OK(baseDataSortInfoService.save(baseDataSortInfo));
    }


    @ApiOperation("删除单个BaseDataSortInfo")
    @PostMapping("/delete/{uuid}")
    public Result<Boolean> deleteById(@PathVariable("uuid") String uuid) {
        return Result.OK(baseDataSortInfoService.removeById(uuid));
    }

    @ApiOperation("编辑单个BaseDataSortInfo")
    @PostMapping("/updateByUuId")
    public Result<Boolean> updateByUuId( @RequestBody BaseDataSortInfo baseDataSortInfo) {
        return Result.OK(baseDataSortInfoService.updateById(baseDataSortInfo));
    }

    @ApiOperation("查询单个BaseDataSortInfo")
    @GetMapping("/selectByUuid/{uuid}")
    public Result<BaseDataSortInfo> selectByUuid(@PathVariable("uuid") String uuid) {
        return Result.OK(baseDataSortInfoService.getById(uuid));
    }

    @ApiOperation("查询分页BaseDataSortInfo")
    @GetMapping("/selectPage")
    public Result<IPage<BaseDataSortInfo>> selectPage(@RequestParam(value = "pageNo", defaultValue = "1") int pageNo,
                                                    @RequestParam(value = "pageSize", defaultValue = "1000") int pageSize,
                                                    BaseDataSortInfo baseDataSortInfo) {
        QueryWrapper<BaseDataSortInfo> queryWrapper = new QueryWrapper();
        if (!StringUtil.isEmpty(baseDataSortInfo.getDataSortName())){
            queryWrapper.like("DATA_SORT_NAME", baseDataSortInfo.getDataSortName());
        }
        if (!StringUtil.isEmpty(baseDataSortInfo.getDataSortValue())){
            queryWrapper.like("DATA_SORT_VALUE", baseDataSortInfo.getDataSortValue());
        }
        IPage<BaseDataSortInfo> page = baseDataSortInfoService.page(new Page<>(pageNo,pageSize), queryWrapper.orderByAsc("DATA_SORT_VALUE"));
        return Result.OK(page);
    }


    //获取登陆人信息
//    @ApiOperation("获取当前登陆人信息")
//    @GetMapping("/ps/loginUserInfo")
//    public Result loginUserInfo() {
//        User userInfo = LoginUserInfo.getLoginUser();
//        return Result.ok(userInfo);
//    }
}
