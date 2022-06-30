package com.icss.gateway.api.controller.system;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.icss.gateway.constant.CommonConstant;
import com.icss.gateway.utils.LoginUserInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.icss.gateway.model.vo.Result;

import com.icss.gateway.model.system.BaseCodeInfo;
import com.icss.gateway.service.system.BaseCodeInfoService;
import utils.CommonMethod;
import utils.DateUtil;
import utils.UuidUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 基础数据_通用代码表 前端控制器
 * </p>
 *
 * @author weiyujie
 * @since 2022-04-13
 */

@Api("基础数据_通用代码表 服务")
@RequestMapping("/baseCodeInfo")
@RestController
public class BaseCodeInfoController {

    @Autowired
    private BaseCodeInfoService baseCodeInfoService;

    @ApiOperation("创建单个BaseCodeInfo")
    @PostMapping("/add")
    public Result insert(@RequestBody BaseCodeInfo baseCodeInfo) {
        //校验代码类别编码是否存在
        BaseCodeInfo baseCodeInfo1 = new BaseCodeInfo();
        baseCodeInfo1.setDataSortId(baseCodeInfo.getDataSortId());
        baseCodeInfo1.setCodeValue(baseCodeInfo.getCodeValue());
        BaseCodeInfo zhBaseCodeInfo2 = baseCodeInfoService.getOne(new QueryWrapper<>(baseCodeInfo1));
        if (zhBaseCodeInfo2 != null) {
            return Result.error("代码类型编码已存在");
        }
        baseCodeInfo.setCodeId(UuidUtils.getUUID());
        baseCodeInfo.setCreateTime(DateUtil.getCurrentTime());
        baseCodeInfo.setCreateUserName(LoginUserInfo.getLoginUserName());
        baseCodeInfo.setCreateUserId(LoginUserInfo.getLoginUserId());
        baseCodeInfo.setIsDelete(CommonConstant.IS_DELETE_0);
        return Result.OK(baseCodeInfoService.save(baseCodeInfo));
    }


    @ApiOperation("删除单个BaseCodeInfo")
    @PostMapping("/delete/{uuid}")
    public Result<Boolean> deleteById(@PathVariable("uuid") String uuid) {
        return Result.OK(baseCodeInfoService.removeById(uuid));
    }

    @ApiOperation("niqingjie 假删除多个BaseCodeInfo")
    @DeleteMapping("/updateBatchById")
    public Result updateBatchById(@RequestBody List<BaseCodeInfo> baseCodeInfoList, HttpServletRequest request) {
        List<String> newRecordList=new ArrayList<>();
        for (BaseCodeInfo baseCodeInfo : baseCodeInfoList) {
            newRecordList.add(baseCodeInfo.getCodeId());
            List<BaseCodeInfo> zhBaseCodeInfoList = baseCodeInfoService.selectTree(baseCodeInfo);
            if (zhBaseCodeInfoList.size() > 0) {
                return Result.error(100000, "该" + baseCodeInfo.getCodeName() + "存在子分类");
            }
            baseCodeInfo.setUpdateTime(DateUtil.getCurrentTime());
            baseCodeInfo.setUpdateUserName(LoginUserInfo.getLoginUserName());
            baseCodeInfo.setUpdateUserId(LoginUserInfo.getLoginUserId());
            baseCodeInfo.setIsDelete(CommonConstant.IS_DELETE_1);
        }
        return Result.ok(baseCodeInfoService.updateBatchById(baseCodeInfoList));
    }

    @ApiOperation("编辑单个BaseCodeInfo")
    @PostMapping("/updateByUuId")
    public Result<Boolean> updateByUuId(@RequestBody BaseCodeInfo baseCodeInfo) {
        baseCodeInfo.setUpdateTime(DateUtil.getCurrentTime());
        baseCodeInfo.setUpdateUserName(LoginUserInfo.getLoginUserName());
        baseCodeInfo.setUpdateUserId(LoginUserInfo.getLoginUserId());
        return Result.OK(baseCodeInfoService.updateById(baseCodeInfo));
    }

    @ApiOperation("查询单个BaseCodeInfo")
    @GetMapping("/selectByUuid/{uuid}")
    public Result<BaseCodeInfo> selectByUuid(@PathVariable("uuid") String uuid) {
        return Result.OK(baseCodeInfoService.getById(uuid));
    }

    @ApiOperation("查询分页BaseCodeInfo")
    @GetMapping("/selectPage")
    public Result<IPage<BaseCodeInfo>> selectPage(@RequestParam(value = "pageNo", defaultValue = "1") int pageNo,
                                                  @RequestParam(value = "pageSize", defaultValue = "1000") int pageSize,
                                                  BaseCodeInfo baseCodeInfo) {
        baseCodeInfo.setIsDelete(CommonConstant.IS_DELETE_0);
        IPage<BaseCodeInfo> page = baseCodeInfoService.page(new Page<>(pageNo, pageSize), new QueryWrapper<>(baseCodeInfo).orderByAsc("CODE_VALUE"));
        return Result.OK(page);
    }


    @ApiOperation("zhangchuang 查询树整棵树")
    @PostMapping("/selectWholeTree")
    public Result selectWholeTree(String codeSortValue, HttpServletRequest request) {
        List<BaseCodeInfo> tree = baseCodeInfoService.selectWholeTree(codeSortValue);
        JSONArray jsonArray = CommonMethod.listToTree(JSONArray.parseArray(JSON.toJSONString(tree)), "codeUuid", "parentCodeUuid", "children");
        return Result.OK(jsonArray);
    }
}
