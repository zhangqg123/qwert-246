package org.jeecg.modules.qwert.point.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.qwert.point.entity.ProtocolTreeModel;
import org.jeecg.modules.qwert.point.entity.QwertPointProtocol;
import org.jeecg.modules.qwert.point.mapper.QwertPointProtocolMapper;
import org.jeecg.modules.qwert.point.service.IQwertPointProtocolService;
import org.jeecg.modules.qwert.point.utils.FindsProtocolChildrenUtil;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: qwert_point_protocol
 * @Author: jeecg-boot
 * @Date:   2021-11-18
 * @Version: V1.0
 */
@Service
public class QwertPointProtocolServiceImpl extends ServiceImpl<QwertPointProtocolMapper, QwertPointProtocol> implements IQwertPointProtocolService {

	@Override
	public void addQwertPointProtocol(QwertPointProtocol qwertPointProtocol) {
	   //新增时设置hasChild为0
	    qwertPointProtocol.setHasChild(IQwertPointProtocolService.NOCHILD);
		if(oConvertUtils.isEmpty(qwertPointProtocol.getPid())){
			qwertPointProtocol.setPid(IQwertPointProtocolService.ROOT_PID_VALUE);
		}else{
			//如果当前节点父ID不为空 则设置父节点的hasChildren 为1
			QwertPointProtocol parent = baseMapper.selectById(qwertPointProtocol.getPid());
			if(parent!=null && !"1".equals(parent.getHasChild())){
				parent.setHasChild("1");
				baseMapper.updateById(parent);
			}
		}
		baseMapper.insert(qwertPointProtocol);
	}
	
	@Override
	public void updateQwertPointProtocol(QwertPointProtocol qwertPointProtocol) {
		QwertPointProtocol entity = this.getById(qwertPointProtocol.getId());
		if(entity==null) {
			throw new JeecgBootException("未找到对应实体");
		}
		String old_pid = entity.getPid();
		String new_pid = qwertPointProtocol.getPid();
		if(!old_pid.equals(new_pid)) {
			updateOldParentNode(old_pid);
			if(oConvertUtils.isEmpty(new_pid)){
				qwertPointProtocol.setPid(IQwertPointProtocolService.ROOT_PID_VALUE);
			}
			if(!IQwertPointProtocolService.ROOT_PID_VALUE.equals(qwertPointProtocol.getPid())) {
				baseMapper.updateTreeNodeStatus(qwertPointProtocol.getPid(), IQwertPointProtocolService.HASCHILD);
			}
		}
		baseMapper.updateById(qwertPointProtocol);
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void deleteQwertPointProtocol(String id) throws JeecgBootException {
		//查询选中节点下所有子节点一并删除
        id = this.queryTreeChildIds(id);
        if(id.indexOf(",")>0) {
            StringBuffer sb = new StringBuffer();
            String[] idArr = id.split(",");
            for (String idVal : idArr) {
                if(idVal != null){
                    QwertPointProtocol qwertPointProtocol = this.getById(idVal);
                    String pidVal = qwertPointProtocol.getPid();
                    //查询此节点上一级是否还有其他子节点
                    List<QwertPointProtocol> dataList = baseMapper.selectList(new QueryWrapper<QwertPointProtocol>().eq("pid", pidVal).notIn("id",Arrays.asList(idArr)));
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
            QwertPointProtocol qwertPointProtocol = this.getById(id);
            if(qwertPointProtocol==null) {
                throw new JeecgBootException("未找到对应实体");
            }
            updateOldParentNode(qwertPointProtocol.getPid());
            baseMapper.deleteById(id);
        }
	}
	
	@Override
    public List<QwertPointProtocol> queryTreeListNoPage(QueryWrapper<QwertPointProtocol> queryWrapper) {
        List<QwertPointProtocol> dataList = baseMapper.selectList(queryWrapper);
        List<QwertPointProtocol> mapList = new ArrayList<>();
        for(QwertPointProtocol data : dataList){
            String pidVal = data.getPid();
            //递归查询子节点的根节点
            if(pidVal != null && !"0".equals(pidVal)){
                QwertPointProtocol rootVal = this.getTreeRoot(pidVal);
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
		if(!IQwertPointProtocolService.ROOT_PID_VALUE.equals(pid)) {
			Integer count = baseMapper.selectCount(new QueryWrapper<QwertPointProtocol>().eq("pid", pid));
			if(count==null || count<=1) {
				baseMapper.updateTreeNodeStatus(pid, IQwertPointProtocolService.NOCHILD);
			}
		}
	}

	/**
     * 递归查询节点的根节点
     * @param pidVal
     * @return
     */
    private QwertPointProtocol getTreeRoot(String pidVal){
        QwertPointProtocol data =  baseMapper.selectById(pidVal);
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
        List<QwertPointProtocol> dataList = baseMapper.selectList(new QueryWrapper<QwertPointProtocol>().eq("pid", pidVal));
        if(dataList != null && dataList.size()>0){
            for(QwertPointProtocol tree : dataList) {
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
    public List<ProtocolTreeModel> queryProtocolTreeList() {
        LambdaQueryWrapper<QwertPointProtocol> query = new LambdaQueryWrapper<QwertPointProtocol>();
    //    query.eq(QwertPointProtocol::getDelFlag, CommonConstant.DEL_FLAG_0.toString());
        query.orderByAsc(QwertPointProtocol::getProtocolNo);
        List<QwertPointProtocol> list = this.list(query);
        // 调用wrapTreeDataToTreeList方法生成树状数据
        List<ProtocolTreeModel> listResult = FindsProtocolChildrenUtil.wrapProtocolTreeDataToTreeList(list);
        return listResult;
    }

}
