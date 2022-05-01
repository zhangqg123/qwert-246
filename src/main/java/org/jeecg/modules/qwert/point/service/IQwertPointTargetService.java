package org.jeecg.modules.qwert.point.service;

import org.jeecg.modules.qwert.point.entity.QwertPointTarget;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description: qwert_point_target
 * @Author: jeecg-boot
 * @Date:   2021-11-29
 * @Version: V1.0
 */
public interface IQwertPointTargetService extends IService<QwertPointTarget> {

    List<QwertPointTarget> queryQptList(String devNo);
    public List<QwertPointTarget> queryJztList5(String dev_no);

}
