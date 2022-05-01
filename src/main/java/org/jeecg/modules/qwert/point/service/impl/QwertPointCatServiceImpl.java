package org.jeecg.modules.qwert.point.service.impl;

import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.qwert.point.entity.QwertPointCat;
import org.jeecg.modules.qwert.point.mapper.QwertPointCatMapper;
import org.jeecg.modules.qwert.point.service.IQwertPointCatService;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: qwert_point_cat
 * @Author: jeecg-boot
 * @Date:   2021-11-13
 * @Version: V1.0
 */
@Service
public class QwertPointCatServiceImpl extends ServiceImpl<QwertPointCatMapper, QwertPointCat> implements IQwertPointCatService {

	@Override
	public void addQwertPointCat(QwertPointCat qwertPointCat) {
	   //新增时设置hasChild为0
	    qwertPointCat.setHasChild(IQwertPointCatService.NOCHILD);
		if(oConvertUtils.isEmpty(qwertPointCat.getPid())){
			qwertPointCat.setPid(IQwertPointCatService.ROOT_PID_VALUE);
		}else{
			//如果当前节点父ID不为空 则设置父节点的hasChildren 为1
			QwertPointCat parent = baseMapper.selectById(qwertPointCat.getPid());
			if(parent!=null && !"1".equals(parent.getHasChild())){
				parent.setHasChild("1");
				baseMapper.updateById(parent);
			}
		}
		baseMapper.insert(qwertPointCat);
	}
	
	@Override
	public void updateQwertPointCat(QwertPointCat qwertPointCat) {
		QwertPointCat entity = this.getById(qwertPointCat.getId());
		if(entity==null) {
			throw new JeecgBootException("未找到对应实体");
		}
		String old_pid = entity.getPid();
		String new_pid = qwertPointCat.getPid();
		if(!old_pid.equals(new_pid)) {
			updateOldParentNode(old_pid);
			if(oConvertUtils.isEmpty(new_pid)){
				qwertPointCat.setPid(IQwertPointCatService.ROOT_PID_VALUE);
			}
			if(!IQwertPointCatService.ROOT_PID_VALUE.equals(qwertPointCat.getPid())) {
				baseMapper.updateTreeNodeStatus(qwertPointCat.getPid(), IQwertPointCatService.HASCHILD);
			}
		}
		baseMapper.updateById(qwertPointCat);
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void deleteQwertPointCat(String id) throws JeecgBootException {
		//查询选中节点下所有子节点一并删除
        id = this.queryTreeChildIds(id);
        if(id.indexOf(",")>0) {
            StringBuffer sb = new StringBuffer();
            String[] idArr = id.split(",");
            for (String idVal : idArr) {
                if(idVal != null){
                    QwertPointCat qwertPointCat = this.getById(idVal);
                    String pidVal = qwertPointCat.getPid();
                    //查询此节点上一级是否还有其他子节点
                    List<QwertPointCat> dataList = baseMapper.selectList(new QueryWrapper<QwertPointCat>().eq("pid", pidVal).notIn("id",Arrays.asList(idArr)));
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
            QwertPointCat qwertPointCat = this.getById(id);
            if(qwertPointCat==null) {
                throw new JeecgBootException("未找到对应实体");
            }
            updateOldParentNode(qwertPointCat.getPid());
            baseMapper.deleteById(id);
        }
	}
	
	@Override
    public List<QwertPointCat> queryTreeListNoPage(QueryWrapper<QwertPointCat> queryWrapper) {
        List<QwertPointCat> dataList = baseMapper.selectList(queryWrapper);
        List<QwertPointCat> mapList = new ArrayList<>();
        for(QwertPointCat data : dataList){
            String pidVal = data.getPid();
            //递归查询子节点的根节点
            if(pidVal != null && !"0".equals(pidVal)){
                QwertPointCat rootVal = this.getTreeRoot(pidVal);
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
		if(!IQwertPointCatService.ROOT_PID_VALUE.equals(pid)) {
			Integer count = baseMapper.selectCount(new QueryWrapper<QwertPointCat>().eq("pid", pid));
			if(count==null || count<=1) {
				baseMapper.updateTreeNodeStatus(pid, IQwertPointCatService.NOCHILD);
			}
		}
	}

	/**
     * 递归查询节点的根节点
     * @param pidVal
     * @return
     */
    private QwertPointCat getTreeRoot(String pidVal){
        QwertPointCat data =  baseMapper.selectById(pidVal);
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
        List<QwertPointCat> dataList = baseMapper.selectList(new QueryWrapper<QwertPointCat>().eq("pid", pidVal));
        if(dataList != null && dataList.size()>0){
            for(QwertPointCat tree : dataList) {
                if(!sb.toString().contains(tree.getId())){
                    sb.append(",").append(tree.getId());
                }
                this.getTreeChildIds(tree.getId(),sb);
            }
        }
        return sb;
    }

}
