package org.jeecg.modules.qwert.point.service.impl;

import org.jeecg.modules.qwert.point.entity.QwertPointTarget;
import org.jeecg.modules.qwert.point.mapper.QwertPointTargetMapper;
import org.jeecg.modules.qwert.point.service.IQwertPointTargetService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Description: qwert_point_target
 * @Author: jeecg-boot
 * @Date:   2021-11-29
 * @Version: V1.0
 */
@Service
public class QwertPointTargetServiceImpl extends ServiceImpl<QwertPointTargetMapper, QwertPointTarget> implements IQwertPointTargetService {
    @Resource
    private QwertPointTargetMapper qwertPointTargetMapper;
    @Override
    public List<QwertPointTarget> queryQptList(String dev_no) {
        List<QwertPointTarget> qptList = this.qwertPointTargetMapper.queryQptList(dev_no);
        return qptList;
    }
    @Override
    public List<QwertPointTarget> queryJztList5(String dev_no) {
        List<QwertPointTarget> jztList = this.qwertPointTargetMapper.queryJztList5(dev_no);
        return jztList;
    }

}
