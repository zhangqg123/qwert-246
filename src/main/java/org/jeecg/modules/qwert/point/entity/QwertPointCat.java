package org.jeecg.modules.qwert.point.entity;

import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecg.common.aspect.annotation.Dict;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.UnsupportedEncodingException;

/**
 * @Description: qwert_point_cat
 * @Author: jeecg-boot
 * @Date:   2021-11-13
 * @Version: V1.0
 */
@Data
@TableName("qwert_point_cat")
@ApiModel(value="qwert_point_cat对象", description="qwert_point_cat")
public class QwertPointCat implements Serializable {
    private static final long serialVersionUID = 1L;

	/**资产分类编号*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "资产分类编号")
    private java.lang.String id;
	/**资产分类名称*/
	@Excel(name = "资产分类名称", width = 15)
    @ApiModelProperty(value = "资产分类名称")
    private java.lang.String catName;
	/**原有编号*/
	@Excel(name = "原有编号", width = 15)
    @ApiModelProperty(value = "原有编号")
    private java.lang.String originId;
	/**采集协议*/
	@Excel(name = "采集协议", width = 15)
    @ApiModelProperty(value = "采集协议")
    private java.lang.String proType;
	/**上级id*/
	@Excel(name = "上级id", width = 15)
    @ApiModelProperty(value = "上级id")
    private java.lang.String pid;
	/**单位用户*/
	@Excel(name = "单位用户", width = 15)
    @ApiModelProperty(value = "单位用户")
    private java.lang.String orgUser;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    private java.lang.String createBy;
	/**创建日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "创建日期")
    private java.util.Date createTime;
	/**更新人*/
    @ApiModelProperty(value = "更新人")
    private java.lang.String updateBy;
	/**更新日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "更新日期")
    private java.util.Date updateTime;
	/**所属部门*/
    @ApiModelProperty(value = "所属部门")
    private java.lang.String sysOrgCode;
	/**是否有子节点*/
	@Excel(name = "是否有子节点", width = 15)
    @ApiModelProperty(value = "是否有子节点")
    private java.lang.String hasChild;
}
