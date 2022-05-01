package org.jeecg.modules.qwert.point.service;

import org.jeecg.modules.qwert.point.entity.ProtocolTreeModel;
import org.jeecg.modules.qwert.point.entity.QwertPointProtocol;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.exception.JeecgBootException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import java.util.List;

/**
 * @Description: qwert_point_protocol
 * @Author: jeecg-boot
 * @Date:   2021-11-18
 * @Version: V1.0
 */
public interface IQwertPointProtocolService extends IService<QwertPointProtocol> {

	/**根节点父ID的值*/
	public static final String ROOT_PID_VALUE = "0";
	
	/**树节点有子节点状态值*/
	public static final String HASCHILD = "1";
	
	/**树节点无子节点状态值*/
	public static final String NOCHILD = "0";

	/**新增节点*/
	void addQwertPointProtocol(QwertPointProtocol qwertPointProtocol);
	
	/**修改节点*/
	void updateQwertPointProtocol(QwertPointProtocol qwertPointProtocol) throws JeecgBootException;
	
	/**删除节点*/
	void deleteQwertPointProtocol(String id) throws JeecgBootException;

	/**查询所有数据，无分页*/
    List<QwertPointProtocol> queryTreeListNoPage(QueryWrapper<QwertPointProtocol> queryWrapper);

	List<ProtocolTreeModel> queryProtocolTreeList();

}
