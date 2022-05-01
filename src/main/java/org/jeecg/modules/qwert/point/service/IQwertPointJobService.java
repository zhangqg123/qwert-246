package org.jeecg.modules.qwert.point.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.qwert.point.entity.QwertPointDev;

import java.util.List;

/**
 * @Description: jst_zc_dev
 * @Author: jeecg-boot
 * @Date:   2020-07-24
 * @Version: V1.0
 */
public interface IQwertPointJobService extends IService<QwertPointDev> {
	public void readCat(String catOrigin);
	public void readDev(String devId) ;

	List<QwertPointDev> queryJzdList2(String catNo);
}
