package org.jeecg.modules.qwert.point.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 部门表 存储树结构数据的实体类
 * <p>
 * 
 * @Author Steve
 * @Since 2019-01-22 
 */
public class PositionTreeModel implements Serializable{

    private static final long serialVersionUID = 1L;

    /** 对应SysDepart中的id字段,前端数据树中的key*/
    private String key;

    /** 对应SysDepart中的id字段,前端数据树中的value*/
    private String value;

    /** 对应depart_name字段,前端数据树中的title*/
    private String title;


    private boolean isLeaf;
    // 以下所有字段均与SysDepart相同

    private String id;

    private String parentId;

    private String name;
//    private String orgCode;
    private List<PositionTreeModel> children = new ArrayList<>();


    /**
     * 将SysDepart对象转换成SysDepartTreeModel对象
     * @param QwertPointProtocol
     */
	public PositionTreeModel(QwertPointPosition qpp) {
		this.key = qpp.getId();
        this.value = qpp.getId();
        this.title = qpp.getPosName();
        this.id = qpp.getId();
        this.parentId = qpp.getPid();
        this.name = qpp.getPosName();
//        this.orgCode = sysDepart.getOrgCode();
    }
    public PositionTreeModel(QwertPointDev qpd) {
        this.key = qpd.getId();
//        this.value = qpd.getId();
        this.value = "dev";
        this.title = qpd.getDevName();
        this.id = qpd.getId();
        this.parentId = qpd.getDevPos();
        this.name = qpd.getDevName();
//        this.orgCode = sysDepart.getOrgCode();
    }

    public boolean getIsLeaf() {
        return isLeaf;
    }

    public void setIsLeaf(boolean isleaf) {
         this.isLeaf = isleaf;
    }

    public String getKey() {
		return key;
	}


	public void setKey(String key) {
		this.key = key;
	}


	public String getValue() {
		return value;
	}


	public void setValue(String value) {
		this.value = value;
	}


	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}


	public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<PositionTreeModel> getChildren() {
        return children;
    }

    public void setChildren(List<PositionTreeModel> children) {
        if (children==null){
            this.isLeaf=true;
        }
        this.children = children;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public PositionTreeModel() { }

    /**
     * 重写equals方法
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
			return true;
		}
        if (o == null || getClass() != o.getClass()) {
			return false;
		}
        PositionTreeModel model = (PositionTreeModel) o;
        return Objects.equals(id, model.id) &&
                Objects.equals(parentId, model.parentId) &&
                Objects.equals(name, model.name) &&
       //         Objects.equals(orgCode, model.orgCode) &&
                Objects.equals(children, model.children);
    }
    
    /**
     * 重写hashCode方法
     */
    @Override
    public int hashCode() {

        return Objects.hash(id, parentId, name,
         //       orgCode,
        		children);
    }

}
