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
 * @Description: qwert_point_target
 * @Author: jeecg-boot
 * @Date:   2021-11-29
 * @Version: V1.0
 */
@Data
@TableName("qwert_point_target")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="qwert_point_target对象", description="qwert_point_target")
public class QwertPointTarget implements Serializable {
    private static final long serialVersionUID = 1L;
	/**id*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "id")
    private java.lang.String id;
	/**设备编号*/
	@Excel(name = "设备编号", width = 15)
    @ApiModelProperty(value = "设备编号")
    @Dict(dicCode = "id",dictTable="qwert_point_dev",dicText="dev_name")
    private java.lang.String devNo;
    /**设备类型*/
    @Excel(name = "设备类型cat", width = 15)
    @ApiModelProperty(value = "设备类型cat")
    private java.lang.String devCat;
	/**设备类型*/
	@Excel(name = "设备类型", width = 15)
    @ApiModelProperty(value = "设备类型")
    private java.lang.String devType;
    /**设备类型*/
    @Excel(name = "区域编号", width = 15)
    @ApiModelProperty(value = "区域编号")
    private java.lang.String positionId;
	/**点编号*/
	@Excel(name = "点编号", width = 15)
    @ApiModelProperty(value = "点编号")
    private java.lang.String targetNo;
	/**点名称*/
	@Excel(name = "点名称", width = 15)
    @ApiModelProperty(value = "点名称")
    private java.lang.String targetName;
	/**显示样式*/
	@Excel(name = "显示样式", width = 15)
    @ApiModelProperty(value = "显示样式")
    private java.lang.String displayMode;
	/**单位*/
	@Excel(name = "单位", width = 15)
    @ApiModelProperty(value = "单位")
    private java.lang.String unit;
	/**是否采集*/
	@Excel(name = "是否采集", width = 15)
    @ApiModelProperty(value = "是否采集")
    private java.lang.String ifGet;
	/**主指令*/
	@Excel(name = "主指令", width = 15)
    @ApiModelProperty(value = "主指令")
    private java.lang.String instruct;
	/**寄存器地址*/
	@Excel(name = "寄存器地址", width = 15)
    @ApiModelProperty(value = "寄存器地址")
    private java.lang.String address;
	/**寄存器地址格式*/
	@Excel(name = "寄存器地址格式", width = 15)
    @ApiModelProperty(value = "寄存器地址格式")
    private java.lang.String addressType;
	/**数据类型*/
	@Excel(name = "数据类型", width = 15)
    @ApiModelProperty(value = "数据类型")
    private java.lang.String dataType;
	/**信息点类型*/
	@Excel(name = "信息点类型", width = 15)
    @ApiModelProperty(value = "信息点类型")
    private java.lang.String infoType;
	/**信息点数值类型*/
	@Excel(name = "信息点数值类型", width = 15)
    @ApiModelProperty(value = "信息点数值类型")
    private java.lang.String infoDatatype;
	/**信息点数值精度*/
	@Excel(name = "信息点数值精度", width = 15)
    @ApiModelProperty(value = "信息点数值精度")
    private java.lang.String infoDataaccurate;
	/**报警点*/
	@Excel(name = "报警点", width = 15)
    @ApiModelProperty(value = "报警点")
    private java.lang.String alarmPoint;
	/**计算因子*/
	@Excel(name = "计算因子", width = 15)
    @ApiModelProperty(value = "计算因子")
    private java.lang.String yinzi;
	/**收到报警次数*/
	@Excel(name = "收到报警次数", width = 15)
    @ApiModelProperty(value = "收到报警次数")
    private java.lang.Integer alarmAccept;
	/**报警恢复次数*/
	@Excel(name = "报警恢复次数", width = 15)
    @ApiModelProperty(value = "报警恢复次数")
    private java.lang.Integer alarmRestore;
	/**报警重复次数*/
	@Excel(name = "报警重复次数", width = 15)
    @ApiModelProperty(value = "报警重复次数")
    private java.lang.Integer alarmRepeat;
	/**报警场景*/
	@Excel(name = "报警场景", width = 15)
    @ApiModelProperty(value = "报警场景")
    private java.lang.String alarmScheme;
	/**报警配置*/
	@Excel(name = "报警配置", width = 15)
    @ApiModelProperty(value = "报警配置")
    private java.lang.String alarmConfig;
	/**指令顺序*/
	@Excel(name = "指令顺序", width = 15)
    @ApiModelProperty(value = "指令顺序")
    private java.lang.String targetOrder;
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
