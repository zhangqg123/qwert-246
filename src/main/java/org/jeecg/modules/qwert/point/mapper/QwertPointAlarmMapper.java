package org.jeecg.modules.qwert.point.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.qwert.point.entity.QwertPointAlarm;

import java.util.List;

/**
 * @Description: jst_zc_alarm
 * @Author: jeecg-boot
 * @Date:   2020-09-05
 * @Version: V1.0
 */
public interface QwertPointAlarmMapper extends BaseMapper<QwertPointAlarm> {

	public List<QwertPointAlarm> queryJzaList(@Param("send_type") String send_type);

}
