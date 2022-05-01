package org.jeecg.modules.qwert.point.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.qwert.point.entity.PositionTreeModel;
import org.jeecg.modules.qwert.point.entity.QwertPointDev;
import org.jeecg.modules.qwert.point.entity.QwertPointPosition;
import org.jeecg.modules.qwert.point.mapper.QwertPointPositionMapper;
import org.jeecg.modules.qwert.point.service.IQwertPointPositionService;
import org.jeecg.modules.qwert.point.utils.FindsProtocolChildrenUtil;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: qwert_point_position
 * @Author: jeecg-boot
 * @Date:   2021-11-23
 * @Version: V1.0
 */
@Service
public class QwertPointPositionServiceImpl extends ServiceImpl<QwertPointPositionMapper, QwertPointPosition> implements IQwertPointPositionService {

	@Override
	public void addQwertPointPosition(QwertPointPosition qwertPointPosition) {
	   //新增时设置hasChild为0
	    qwertPointPosition.setHasChild(IQwertPointPositionService.NOCHILD);
		if(oConvertUtils.isEmpty(qwertPointPosition.getPid())){
			qwertPointPosition.setPid(IQwertPointPositionService.ROOT_PID_VALUE);
		}else{
			//如果当前节点父ID不为空 则设置父节点的hasChildren 为1
			QwertPointPosition parent = baseMapper.selectById(qwertPointPosition.getPid());
			if(parent!=null && !"1".equals(parent.getHasChild())){
				parent.setHasChild("1");
				baseMapper.updateById(parent);
			}
		}
		baseMapper.insert(qwertPointPosition);
	}
	
	@Override
	public void updateQwertPointPosition(QwertPointPosition qwertPointPosition) {
		QwertPointPosition entity = this.getById(qwertPointPosition.getId());
		if(entity==null) {
			throw new JeecgBootException("未找到对应实体");
		}
		String old_pid = entity.getPid();
		String new_pid = qwertPointPosition.getPid();
		if(!old_pid.equals(new_pid)) {
			updateOldParentNode(old_pid);
			if(oConvertUtils.isEmpty(new_pid)){
				qwertPointPosition.setPid(IQwertPointPositionService.ROOT_PID_VALUE);
			}
			if(!IQwertPointPositionService.ROOT_PID_VALUE.equals(qwertPointPosition.getPid())) {
				baseMapper.updateTreeNodeStatus(qwertPointPosition.getPid(), IQwertPointPositionService.HASCHILD);
			}
		}
		baseMapper.updateById(qwertPointPosition);
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void deleteQwertPointPosition(String id) throws JeecgBootException {
		//查询选中节点下所有子节点一并删除
        id = this.queryTreeChildIds(id);
        if(id.indexOf(",")>0) {
            StringBuffer sb = new StringBuffer();
            String[] idArr = id.split(",");
            for (String idVal : idArr) {
                if(idVal != null){
                    QwertPointPosition qwertPointPosition = this.getById(idVal);
                    String pidVal = qwertPointPosition.getPid();
                    //查询此节点上一级是否还有其他子节点
                    List<QwertPointPosition> dataList = baseMapper.selectList(new QueryWrapper<QwertPointPosition>().eq("pid", pidVal).notIn("id",Arrays.asList(idArr)));
                    if((dataList == null || dataList.size()==0) && !Arrays.asList(idArr).contains(pidVal)
                            && !sb.toString().contains(pidVal)){
                        //如果当前节点原本有子节点 现在木有了，更新状态
                        sb.append(pidVal).append(",");
                    }
                }
            }
            //批量删除节点
            baseMapper.deleteBatchIds(Arrays.asList(idArr));
            //修改已无子节点的标识
            String[] pidArr = sb.toString().split(",");
            for(String pid : pidArr){
                this.updateOldParentNode(pid);
            }
        }else{
            QwertPointPosition qwertPointPosition = this.getById(id);
            if(qwertPointPosition==null) {
                throw new JeecgBootException("未找到对应实体");
            }
            updateOldParentNode(qwertPointPosition.getPid());
            baseMapper.deleteById(id);
        }
	}
	
	@Override
    public List<QwertPointPosition> queryTreeListNoPage(QueryWrapper<QwertPointPosition> queryWrapper) {
        List<QwertPointPosition> dataList = baseMapper.selectList(queryWrapper);
        List<QwertPointPosition> mapList = new ArrayList<>();
        for(QwertPointPosition data : dataList){
            String pidVal = data.getPid();
            //递归查询子节点的根节点
            if(pidVal != null && !"0".equals(pidVal)){
                QwertPointPosition rootVal = this.getTreeRoot(pidVal);
                if(rootVal != null && !mapList.contains(rootVal)){
                    mapList.add(rootVal);
                }
            }else{
                if(!mapList.contains(data)){
                    mapList.add(data);
                }
            }
        }
        return mapList;
    }
	
	/**
	 * 根据所传pid查询旧的父级节点的子节点并修改相应状态值
	 * @param pid
	 */
	private void updateOldParentNode(String pid) {
		if(!IQwertPointPositionService.ROOT_PID_VALUE.equals(pid)) {
			Integer count = baseMapper.selectCount(new QueryWrapper<QwertPointPosition>().eq("pid", pid));
			if(count==null || count<=1) {
				baseMapper.updateTreeNodeStatus(pid, IQwertPointPositionService.NOCHILD);
			}
		}
	}

	/**
     * 递归查询节点的根节点
     * @param pidVal
     * @return
     */
    private QwertPointPosition getTreeRoot(String pidVal){
        QwertPointPosition data =  baseMapper.selectById(pidVal);
        if(data != null && !"0".equals(data.getPid())){
            return this.getTreeRoot(data.getPid());
        }else{
            return data;
        }
    }

    /**
     * 根据id查询所有子节点id
     * @param ids
     * @return
     */
    private String queryTreeChildIds(String ids) {
        //获取id数组
        String[] idArr = ids.split(",");
        StringBuffer sb = new StringBuffer();
        for (String pidVal : idArr) {
            if(pidVal != null){
                if(!sb.toString().contains(pidVal)){
                    if(sb.toString().length() > 0){
                        sb.append(",");
                    }
                    sb.append(pidVal);
                    this.getTreeChildIds(pidVal,sb);
                }
            }
        }
        return sb.toString();
    }

    /**
     * 递归查询所有子节点
     * @param pidVal
     * @param sb
     * @return
     */
    private StringBuffer getTreeChildIds(String pidVal,StringBuffer sb){
        List<QwertPointPosition> dataList = baseMapper.selectList(new QueryWrapper<QwertPointPosition>().eq("pid", pidVal));
        if(dataList != null && dataList.size()>0){
            for(QwertPointPosition tree : dataList) {
                if(!sb.toString().contains(tree.getId())){
                    sb.append(",").append(tree.getId());
                }
                this.getTreeChildIds(tree.getId(),sb);
            }
        }
        return sb;
    }
    //	@Cacheable(value = CacheConstant.SYS_DEPARTS_CACHE)
    @Override
    public List<PositionTreeModel> queryPositionTreeList(List<QwertPointDev> qpdList) {
        LambdaQueryWrapper<QwertPointPosition> query = new LambdaQueryWrapper<QwertPointPosition>();
        List<QwertPointPosition> list = this.list(query);
        // 调用wrapTreeDataToTreeList方法生成树状数据
        List<PositionTreeModel> listResult = FindsProtocolChildrenUtil.wrapPositionTreeDataToTreeList(list,qpdList);
        return listResult;
    }

}
