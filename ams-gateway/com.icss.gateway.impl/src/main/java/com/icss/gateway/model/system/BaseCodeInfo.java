package com.icss.gateway.model.system;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>
 * 基础数据_通用代码表
 * </p>
 *
 * @author weiyujie
 * @since 2022-04-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("GG_BASE_CODE_INFO")
public class BaseCodeInfo {

    private static final long serialVersionUID = 1L;

    /**
     * 通用代码表主键ID
     */
    @TableId("CODE_ID")
    @ApiModelProperty(value = "通用代码表主键ID")
    private String codeId;
    /**
     * 代码类别ID
     */
    @TableField("DATA_SORT_ID")
    @ApiModelProperty(value = "代码类别ID")
    private String dataSortId;
    /**
     * 通用代码编码
     */
    @TableField("CODE_VALUE")
    @ApiModelProperty(value = "通用代码编码")
    private String codeValue;
    /**
     * 通用代码名称
     */
    @TableField("CODE_NAME")
    @ApiModelProperty(value = "通用代码名称")
    private String codeName;
    /**
     * 通用代码描述
     */
    @TableField("CODE_DESC")
    @ApiModelProperty(value = "通用代码描述")
    private String codeDesc;
    /**
     * 通用代码状态(0：有效，1：无效)
     */
    @TableField("CODE_STATE")
    @ApiModelProperty(value = "通用代码状态(0：有效，1：无效)")
    private Integer codeState;
    /**
     * 通用代码排序号
     */
    @TableField("CODE_INDEX")
    @ApiModelProperty(value = "通用代码排序号")
    private Integer codeIndex;
    /**
     * 删除标识（0：未删除，1：已删除）
     */
    @TableField("IS_DELETE")
    @ApiModelProperty(value = "删除标识（0：未删除，1：已删除）")
    private Integer isDelete;
    /**
     * 父节点ID
     */
    @TableField("PARENT_CODE_ID")
    @ApiModelProperty(value = "父节点ID")
    private String parentCodeId;
    /**
     * 创建者ID
     */
    @TableField("CREATE_USER_ID")
    @ApiModelProperty(value = "创建者ID")
    private String createUserId;
    /**
     * 创建者姓名
     */
    @TableField(value = "CREATE_USER_NAME", fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建者姓名")
    private String createUserName;
    /**
     * 创建时间
     */
    @TableField(value = "CREATE_TIME", fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private Date createTime;
    /**
     * 更新者ID
     */
    @TableField("UPDATE_USER_ID")
    @ApiModelProperty(value = "更新者ID")
    private String updateUserId;
    /**
     * 更新者姓名
     */
    @TableField(value = "UPDATE_USER_NAME", fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新者姓名")
    private String updateUserName;
    /**
     * 更新者时间
     */
    @TableField(value = "UPDATE_TIME", fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新者时间")
    private Date updateTime;
    /**
     * 扩展字段 树形扩展使用
     */
    @TableField("CODE_INFO_EXT1")
    @ApiModelProperty(value = "扩展字段 树形扩展使用")
    private String codeInfoExt1;
    /**
     * 备注
     */
    @TableField("REMARK")
    @ApiModelProperty(value = "备注")
    private String remark;


}
