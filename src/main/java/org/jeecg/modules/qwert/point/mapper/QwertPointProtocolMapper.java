package org.jeecg.modules.qwert.point.mapper;

import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.qwert.point.entity.QwertPointProtocol;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: qwert_point_protocol
 * @Author: jeecg-boot
 * @Date:   2021-11-18
 * @Version: V1.0
 */
public interface QwertPointProtocolMapper extends BaseMapper<QwertPointProtocol> {

	/**
	 * 编辑节点状态
	 * @param id
	 * @param status
	 */
	void updateTreeNodeStatus(@Param("id") String id,@Param("status") String status);

}
