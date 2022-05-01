package org.jeecg.modules.qwert.point.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.qwert.point.entity.QwertPointDev;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: qwert_point_dev
 * @Author: jeecg-boot
 * @Date:   2021-11-16
 * @Version: V1.0
 */
public interface QwertPointDevMapper extends BaseMapper<QwertPointDev> {
    public List<QwertPointDev> queryJzdList2(@Param("dev_cat") String catNo);

}
