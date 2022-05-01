package org.jeecg.modules.qwert.point.mapper;

import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.qwert.point.entity.QwertPointPosition;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: qwert_point_position
 * @Author: jeecg-boot
 * @Date:   2021-11-23
 * @Version: V1.0
 */
public interface QwertPointPositionMapper extends BaseMapper<QwertPointPosition> {

	/**
	 * 编辑节点状态
	 * @param id
	 * @param status
	 */
	void updateTreeNodeStatus(@Param("id") String id,@Param("status") String status);

}
