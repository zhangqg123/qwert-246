package org.jeecg.modules.qwert.point.utils;

import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.qwert.point.entity.*;

import java.util.ArrayList;
import java.util.List;

/**
 * <P>
 * 对应部门的表,处理并查找树级数据
 * <P>
 * 
 * @Author: Steve
 * @Date: 2019-01-22
 */
public class FindsProtocolChildrenUtil {

	//部门树信息-树结构
	//private static List<SysDepartTreeModel> sysDepartTreeList = new ArrayList<SysDepartTreeModel>();
	
	//部门树id-树结构
    //private static List<DepartIdModel> idList = new ArrayList<>();


    public static List<ProtocolTreeModel> wrapProtocolTreeDataToTreeList(List<QwertPointProtocol> recordList) {
        // 在该方法每请求一次,都要对全局list集合进行一次清理
        //idList.clear();
        List<ProtocolIdModel> idList = new ArrayList<ProtocolIdModel>();
        List<ProtocolTreeModel> records = new ArrayList<>();
        for (int i = 0; i < recordList.size(); i++) {
            QwertPointProtocol protocol = recordList.get(i);
            records.add(new ProtocolTreeModel(protocol));
        }
        List<ProtocolTreeModel> tree = findProtocolChildren(records, idList);
        setProtocolEmptyChildrenAsNull(tree);
        return tree;
    }
    public static List<PositionTreeModel> wrapPositionTreeDataToTreeList(List<QwertPointPosition> recordList, List<QwertPointDev> qpdList) {
        // 在该方法每请求一次,都要对全局list集合进行一次清理
        //idList.clear();
        List<PositionIdModel> idList = new ArrayList<PositionIdModel>();
        List<PositionTreeModel> records = new ArrayList<>();
        for (int i = 0; i < recordList.size(); i++) {
            QwertPointPosition position = recordList.get(i);
            records.add(new PositionTreeModel(position));
        }
        for (int i = 0; i < qpdList.size(); i++) {
            QwertPointDev dev = qpdList.get(i);
            records.add(new PositionTreeModel(dev));
        }
        List<PositionTreeModel> tree = findPositionChildren(records, idList);
        setPositionEmptyChildrenAsNull(tree);
        return tree;
    }

    private static List<ProtocolTreeModel> findProtocolChildren(List<ProtocolTreeModel> recordList,
                                                         List<ProtocolIdModel> protocolIdList) {

        List<ProtocolTreeModel> treeList = new ArrayList<>();
        for (int i = 0; i < recordList.size(); i++) {
            ProtocolTreeModel branch = recordList.get(i);
            if ("0".equals(branch.getParentId().trim()) || oConvertUtils.isEmpty(branch.getParentId())) {
                treeList.add(branch);
                ProtocolIdModel protocolIdModel = new ProtocolIdModel().convert(branch);
                protocolIdList.add(protocolIdModel);
            }
        }
        getProtocolGrandChildren(treeList,recordList,protocolIdList);

        //idList = departIdList;
        return treeList;
    }

    private static List<PositionTreeModel> findPositionChildren(List<PositionTreeModel> recordList,
                                                                List<PositionIdModel> positionIdList) {

        List<PositionTreeModel> treeList = new ArrayList<>();
        for (int i = 0; i < recordList.size(); i++) {
            PositionTreeModel branch = recordList.get(i);
            if ( oConvertUtils.isEmpty(branch.getParentId())||"0".equals(branch.getParentId().trim())) {
                treeList.add(branch);
                PositionIdModel positionIdModel = new PositionIdModel().convert(branch);
                positionIdList.add(positionIdModel);
            }
        }
        getPositionGrandChildren(treeList,recordList,positionIdList);

        //idList = departIdList;
        return treeList;
    }

    private static void getProtocolGrandChildren(List<ProtocolTreeModel> treeList,List<ProtocolTreeModel> recordList,List<ProtocolIdModel> idList) {

        for (int i = 0; i < treeList.size(); i++) {
            ProtocolTreeModel model = treeList.get(i);
            ProtocolIdModel idModel = idList.get(i);
            for (int i1 = 0; i1 < recordList.size(); i1++) {
                ProtocolTreeModel m = recordList.get(i1);
                if (m.getParentId()!=null && m.getParentId().equals(model.getId())) {
                    model.getChildren().add(m);
                    ProtocolIdModel dim = new ProtocolIdModel().convert(m);
                    idModel.getChildren().add(dim);
                }
            }
            getProtocolGrandChildren(treeList.get(i).getChildren(), recordList, idList.get(i).getChildren());
        }

    }

    private static void getPositionGrandChildren(List<PositionTreeModel> treeList,List<PositionTreeModel> recordList,List<PositionIdModel> idList) {

        for (int i = 0; i < treeList.size(); i++) {
            PositionTreeModel model = treeList.get(i);
            PositionIdModel idModel = idList.get(i);
            for (int i1 = 0; i1 < recordList.size(); i1++) {
                PositionTreeModel m = recordList.get(i1);
                if (m.getParentId()!=null && m.getParentId().equals(model.getId())) {
                    model.getChildren().add(m);
                    PositionIdModel dim = new PositionIdModel().convert(m);
                    idModel.getChildren().add(dim);
                }
            }
            getPositionGrandChildren(treeList.get(i).getChildren(), recordList, idList.get(i).getChildren());
        }

    }


    private static void setProtocolEmptyChildrenAsNull(List<ProtocolTreeModel> treeList) {

        for (int i = 0; i < treeList.size(); i++) {
            ProtocolTreeModel model = treeList.get(i);
            if (model.getChildren().size() == 0) {
                model.setChildren(null);
                model.setIsLeaf(true);
            }else{
                setProtocolEmptyChildrenAsNull(model.getChildren());
                model.setIsLeaf(false);
            }
        }
        // sysDepartTreeList = treeList;
    }
    private static void setPositionEmptyChildrenAsNull(List<PositionTreeModel> treeList) {

        for (int i = 0; i < treeList.size(); i++) {
            PositionTreeModel model = treeList.get(i);
            if (model.getChildren().size() == 0) {
                model.setChildren(null);
                model.setIsLeaf(true);
            }else{
                setPositionEmptyChildrenAsNull(model.getChildren());
                model.setIsLeaf(false);
            }
        }
        // sysDepartTreeList = treeList;
    }
}
