package org.jeecg.modules.qwert.point.mapper;

import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.qwert.point.entity.QwertPointCat;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: qwert_point_cat
 * @Author: jeecg-boot
 * @Date:   2021-11-13
 * @Version: V1.0
 */
public interface QwertPointCatMapper extends BaseMapper<QwertPointCat> {

	/**
	 * 编辑节点状态
	 * @param id
	 * @param status
	 */
	void updateTreeNodeStatus(@Param("id") String id,@Param("status") String status);

}
