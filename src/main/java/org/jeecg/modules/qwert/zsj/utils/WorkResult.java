package org.jeecg.modules.qwert.zsj.utils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.constant.CommonConstant;

import java.io.Serializable;

/**
 *   接口返回数据格式
 * @author scott
 * @email jeecgos@163.com
 * @date  2019年1月19日
 */
@Data
@ApiModel(value="接口返回对象", description="接口返回对象")
public class WorkResult<T> implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 成功标志
	 */
	@ApiModelProperty(value = "成功标志")
	private boolean success = true;

	/**
	 * 返回处理消息
	 */
	@ApiModelProperty(value = "返回处理消息")
	private String message = "成功！";
	/**
	 * 返回代码
	 */
	@ApiModelProperty(value = "返回代码")
	private String code = "000000";
	
	/**
	 * 返回数据对象 data
	 */
	@ApiModelProperty(value = "返回数据对象")
	private T data;
	
	/**
	 * 时间戳
	 */
	@ApiModelProperty(value = "时间戳")
	private long timestamp = System.currentTimeMillis();

	public WorkResult() {
		
	}
	
	public WorkResult<T> success(String message) {
		this.message = message;
		this.code = "000000";
		this.success = true;
		return this;
	}

	@Deprecated
	public static WorkResult<Object> ok() {
		WorkResult<Object> r = new WorkResult<Object>();
		r.setSuccess(true);
		r.setCode("000000");
		r.setMessage("成功");
		return r;
	}

	@Deprecated
	public static WorkResult<Object> ok(String msg) {
		WorkResult<Object> r = new WorkResult<Object>();
		r.setSuccess(true);
		r.setCode("000000");
		r.setMessage(msg);
		return r;
	}

	@Deprecated
	public static WorkResult<Object> ok(Object data) {
		WorkResult<Object> r = new WorkResult<Object>();
		r.setSuccess(true);
		r.setData(data);
		return r;
	}

	public static<T> WorkResult<T> OK() {
		WorkResult<T> r = new WorkResult<T>();
		r.setSuccess(true);
		r.setCode("000000");
		r.setMessage("成功");
		return r;
	}

	public static<T> WorkResult<T> OK(T data) {
		WorkResult<T> r = new WorkResult<T>();
		r.setSuccess(true);
		r.setCode("000000");
		r.setData(data);
		return r;
	}
	public static<T> WorkResult<T> error(String msg, T data) {
		WorkResult<T> r = new WorkResult<T>();
		r.setSuccess(false);
		r.setCode("999999");
		r.setMessage(msg);
		r.setData(data);
		return r;
	}

	public static WorkResult<Object> error(String msg) {
		return error("999999", msg);
	}

	public static WorkResult<Object> error(String code, String msg) {
		WorkResult<Object> r = new WorkResult<Object>();
		r.setCode(code);
		r.setMessage(msg);
		r.setSuccess(false);
		return r;
	}
}