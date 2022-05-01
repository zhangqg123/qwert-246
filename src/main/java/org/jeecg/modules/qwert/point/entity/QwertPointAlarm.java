package org.jeecg.modules.qwert.point.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description: jst_zc_alarm
 * @Author: jeecg-boot
 * @Date:   2020-09-05
 * @Version: V1.0
 */
@Data
@TableName("qwert_point_alarm")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="qwert_point_alarm对象", description="qwert_point_alarm")
public class QwertPointAlarm implements Serializable {
    private static final long serialVersionUID = 1L;

	/**id*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "id")
    private String id;
	/**设备编号*/
	@Excel(name = "设备编号", width = 15)
    @ApiModelProperty(value = "设备编号")
    private String devNo;
	/**设备名称*/
	@Excel(name = "设备名称", width = 15)
    @ApiModelProperty(value = "设备名称")
    private String devName;
    @Excel(name = "设备地址", width = 15)
    @ApiModelProperty(value = "设备地址")
    private String devPos;
	/**数据点编号*/
	@Excel(name = "分类编号", width = 15)
    @ApiModelProperty(value = "分类编号")
    private String catNo;
	/**数据点编号*/
	@Excel(name = "数据点编号", width = 15)
    @ApiModelProperty(value = "数据点编号")
    private String targetNo;
	/**报警值*/
	@Excel(name = "报警值", width = 15)
    @ApiModelProperty(value = "报警值")
    private String alarmValue;
	/**报警级别*/
	@Excel(name = "报警级别", width = 15)
    @ApiModelProperty(value = "报警级别")
    private String alarmLevel;
	/**报警状态*/
	@Excel(name = "报警状态", width = 15)
    @ApiModelProperty(value = "报警状态")
	@Dict(dicCode = "send_type")
    private String sendType;
	/**报警时间*/
	@Excel(name = "报警时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "报警时间")
    private Date sendTime;
	/**处理状态*/
	@Excel(name = "处理状态", width = 15)
    @ApiModelProperty(value = "处理状态")
    private String dealType;
	/**处理时间*/
	@Excel(name = "处理时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "处理时间")
    private Date dealTime;
	/**上报类型*/
	@Excel(name = "上报类型", width = 15)
    @ApiModelProperty(value = "上报类型")
    private String reportType;
	/**上报时间*/
	@Excel(name = "上报时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "上报时间")
    private Date reportTime;
	/**创建人*/
	@Excel(name = "创建人", width = 15)
    @ApiModelProperty(value = "创建人")
    private String createBy;
	/**创建日期*/
	@Excel(name = "创建日期", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "创建日期")
    private Date createTime;
	/**更新人*/
	@Excel(name = "更新人", width = 15)
    @ApiModelProperty(value = "更新人")
    private String updateBy;
	/**更新日期*/
	@Excel(name = "更新日期", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "更新日期")
    private Date updateTime;
	/**所属部门*/
	@Excel(name = "所属部门", width = 15)
    @ApiModelProperty(value = "所属部门")
    private String sysOrgCode;
}
