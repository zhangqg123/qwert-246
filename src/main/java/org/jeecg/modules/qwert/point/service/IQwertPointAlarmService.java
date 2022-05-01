package org.jeecg.modules.qwert.point.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.qwert.point.entity.QwertPointAlarm;

import java.util.List;

/**
 * @Description: jst_zc_alarm
 * @Author: jeecg-boot
 * @Date:   2020-09-05
 * @Version: V1.0
 */
public interface IQwertPointAlarmService extends IService<QwertPointAlarm> {
	public void saveSys(QwertPointAlarm qwertPointAlarm);

	public List<QwertPointAlarm> queryJzaList(String send_type);

	public void updateSys(QwertPointAlarm qwertPointAlarm);

	public void deleteSys(String id);
}
