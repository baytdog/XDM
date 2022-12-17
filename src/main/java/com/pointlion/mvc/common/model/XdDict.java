package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseXdDict;

import java.util.List;

@SuppressWarnings("serial")
public class XdDict extends BaseXdDict<XdDict> {
	public static final XdDict dao = new XdDict();
	public static final String tableName = "xd_dict";
	public static final String groupTableName = XdDictGroup.tableName;
	
	/***
	 * query by id
	 */
	public XdDict getById(String id){
		return XdDict.dao.findById(id);
	}
	
	/***
	 * del
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		XdDict o = XdDict.dao.getById(id);
    		o.delete();
    	}
	}

	/***
	 * 查询某个分组下的所有字典
	 */
	public List<XdDict> getDctByGroupId(String groupId){
		return dao.find("select * from "+ tableName + " where group_id='"+groupId+"'");
	}


	public List<XdDictGroup> getGroupListByParentId(String groupId){
		return XdDictGroup.dao.find("select * from "+groupTableName+" where parent_id='"+groupId+"'");
	}
}