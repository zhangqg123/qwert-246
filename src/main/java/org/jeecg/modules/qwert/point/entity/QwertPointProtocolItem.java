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
 * @Description: qwert_point_protocol_item
 * @Author: jeecg-boot
 * @Date:   2021-11-18
 * @Version: V1.0
 */
@Data
@TableName("qwert_point_protocol_item")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="qwert_point_protocol_item对象", description="qwert_point_protocol_item")
public class QwertPointProtocolItem implements Serializable {
    private static final long serialVersionUID = 1L;

	/**id*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "id")
    private java.lang.String id;
	/**项目编号*/
	@Excel(name = "项目编号", width = 15)
    @ApiModelProperty(value = "项目编号")
    private java.lang.String itemNo;
	/**项目名称*/
	@Excel(name = "项目名称", width = 15)
    @ApiModelProperty(value = "项目名称")
    private java.lang.String itemName;
	/**协议编号*/
	@Excel(name = "协议编号", width = 15)
    @ApiModelProperty(value = "协议编号")
	@Dict(dicCode = "id",dictTable="qwert_point_protocol",dicText="protocol_name")
    private java.lang.String protocolId;
    /**项目顺序*/
    @Excel(name = "项目顺序", width = 15)
    @ApiModelProperty(value = "项目顺序")
    private java.lang.Integer itemOrder;
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
