package org.jeecg.modules.qwert.point.entity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
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
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: qwert_point_dev
 * @Author: jeecg-boot
 * @Date:   2021-11-16
 * @Version: V1.0
 */
@Data
@TableName("qwert_point_dev")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="qwert_point_dev对象", description="qwert_point_dev")
public class QwertPointDev implements Serializable {
    private static final long serialVersionUID = 1L;

	/**id*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "id")
    private java.lang.String id;
	/**设备名称*/
	@Excel(name = "设备名称", width = 15)
    @ApiModelProperty(value = "设备名称")
    private java.lang.String devName;
	/**设备分类*/
	@Excel(name = "设备分类", width = 15)
    @ApiModelProperty(value = "设备分类")
	@Dict(dicCode = "id",dictTable="qwert_point_cat",dicText="cat_name")
	private java.lang.String devCat;
	/**设备编号*/
	@Excel(name = "设备编号", width = 15)
    @ApiModelProperty(value = "设备编号")
    private java.lang.String devNo;
	/**设备位置*/
	@Excel(name = "设备位置", width = 15)
    @ApiModelProperty(value = "设备位置")
    @Dict(dicCode = "id",dictTable="qwert_point_position",dicText="pos_name")
    private java.lang.String devPos;
	/**orgUser*/
	@Excel(name = "orgUser", width = 15)
    @ApiModelProperty(value = "orgUser")
    private java.lang.String orgUser;
	/**模型编号*/
	@Excel(name = "模型编号", width = 15)
    @ApiModelProperty(value = "模型编号")
    private java.lang.String modNo;
	/**位置*/
	@Excel(name = "位置", width = 15)
    @ApiModelProperty(value = "位置")
    private java.lang.String position;
	/**状态*/
	@Excel(name = "状态", width = 15)
    @ApiModelProperty(value = "状态")
    private java.lang.Integer status;
    /**连接类型*/
    @Excel(name = "连接类型", width = 15)
    @ApiModelProperty(value = "连接类型")
//    @Dict(dicCode = "id",dictTable="qwert_point_protocol",dicText="protocol_name")
    private java.lang.String connType;
	/**连接类型*/
	@Excel(name = "连接类型", width = 15)
    @ApiModelProperty(value = "连接类型")
	@Dict(dicCode = "id",dictTable="qwert_point_protocol",dicText="protocol_name")
    private java.lang.String type;
	/**扩展信息*/
	@Excel(name = "扩展信息", width = 15)
    @ApiModelProperty(value = "扩展信息")
    private java.lang.String extInfo;
	/**连接信息*/
	@Excel(name = "连接信息", width = 15)
    @ApiModelProperty(value = "连接信息")
    private java.lang.String conInfo;
	/**属性信息*/
	@Excel(name = "属性信息", width = 15)
    @ApiModelProperty(value = "属性信息")
    private java.lang.String proInfo;
	/**备注*/
	@Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
    private java.lang.String remark;
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
}
