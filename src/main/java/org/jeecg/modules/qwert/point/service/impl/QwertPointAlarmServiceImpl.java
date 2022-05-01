package org.jeecg.modules.qwert.point.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.qwert.point.entity.QwertPointAlarm;
import org.jeecg.modules.qwert.point.mapper.QwertPointAlarmMapper;
import org.jeecg.modules.qwert.point.service.IQwertPointAlarmService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Description: jst_zc_alarm
 * @Author: jeecg-boot
 * @Date:   2020-09-05
 * @Version: V1.0
 */
@Service
public class QwertPointAlarmServiceImpl extends ServiceImpl<QwertPointAlarmMapper, QwertPointAlarm> implements IQwertPointAlarmService {
	@Resource
	private QwertPointAlarmMapper qwertPointAlarmMapper;

	synchronized public void saveSys(QwertPointAlarm qwertPointAlarm) {
		this.save(qwertPointAlarm);
	}
	synchronized public void updateSys(QwertPointAlarm qwertPointAlarm) {
		this.updateById(qwertPointAlarm);
	}
	public List<QwertPointAlarm> queryJzaList(String send_type) {
		return this.qwertPointAlarmMapper.queryJzaList(send_type);
	}
	@Override
	synchronized public void deleteSys(String id) {
		// TODO Auto-generated method stub
		this.removeById(id);
		
	}

}
