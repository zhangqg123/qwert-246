package org.jeecg.modules.qwert.point.service;

import org.jeecg.modules.qwert.point.entity.QwertPointCat;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.exception.JeecgBootException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import java.util.List;

/**
 * @Description: qwert_point_cat
 * @Author: jeecg-boot
 * @Date:   2021-11-13
 * @Version: V1.0
 */
public interface IQwertPointCatService extends IService<QwertPointCat> {

	/**根节点父ID的值*/
	public static final String ROOT_PID_VALUE = "0";
	
	/**树节点有子节点状态值*/
	public static final String HASCHILD = "1";
	
	/**树节点无子节点状态值*/
	public static final String NOCHILD = "0";

	/**新增节点*/
	void addQwertPointCat(QwertPointCat qwertPointCat);
	
	/**修改节点*/
	void updateQwertPointCat(QwertPointCat qwertPointCat) throws JeecgBootException;
	
	/**删除节点*/
	void deleteQwertPointCat(String id) throws JeecgBootException;

	/**查询所有数据，无分页*/
    List<QwertPointCat> queryTreeListNoPage(QueryWrapper<QwertPointCat> queryWrapper);

}
