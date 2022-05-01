package org.jeecg.modules.qwert.point.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.qwert.point.entity.QwertPointTarget;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: qwert_point_target
 * @Author: jeecg-boot
 * @Date:   2021-11-29
 * @Version: V1.0
 */
public interface QwertPointTargetMapper extends BaseMapper<QwertPointTarget> {

    List<QwertPointTarget> queryQptList(@Param("dev_no") String dev_no);
    public List<QwertPointTarget> queryJztList5(@Param("dev_no") String dev_no);
}
