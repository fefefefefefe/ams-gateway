package com.icss.gateway.model.system;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>
 * 基础数据_代码类别表
 * </p>
 *
 * @author weiyujie
 * @since 2022-04-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("GG_BASE_DATA_SORT_INFO")
public class BaseDataSortInfo {

    private static final long serialVersionUID = 1L;

    /**
     * 代码类别主键ID
     */
    @TableId("DATA_SORT_ID")
    @ApiModelProperty(value = "代码类别主键ID")
    private String dataSortId;
    /**
     * 代码类别名称
     */
    @TableField("DATA_SORT_NAME")
    @ApiModelProperty(value = "代码类别名称")
    private String dataSortName;
    /**
     * 代码类别描述
     */
    @TableField("DATA_SORT_DESC")
    @ApiModelProperty(value = "代码类别描述")
    private String dataSortDesc;
    /**
     * 代码类别编码
     */
    @TableField("DATA_SORT_VALUE")
    @ApiModelProperty(value = "代码类别编码")
    private String dataSortValue;
    /**
     * 是否可编辑
     */
    @TableField("EDIT_TAG")
    @ApiModelProperty(value = "是否可编辑")
    private Integer editTag;
    /**
     * 0:非树形扩展，其他：可树形扩展
     */
    @TableField("EXTEND_TAG")
    @ApiModelProperty(value = "0:非树形扩展，其他：可树形扩展")
    private Integer extendTag;
    /**
     * 备注
     */
    @TableField("REMARK")
    @ApiModelProperty(value = "备注")
    private String remark;


}
