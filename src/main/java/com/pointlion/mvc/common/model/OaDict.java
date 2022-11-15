package com.pointlion.mvc.common.model;

import java.util.List;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseOaDict;
@SuppressWarnings("serial")
public class OaDict extends BaseOaDict<OaDict> {
	public static final OaDict dao = new OaDict();
	public static final String tableName = "oa_dict";
	public static final String groupTableName = OaDictGroup.tableName;
	
	/***
	 * query by id
	 */
	public OaDict getById(String id){
		return OaDict.dao.findById(id);
	}
	
	/***
	 * del
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		OaDict o = OaDict.dao.getById(id);
    		o.delete();
    	}
	}
	
	
	/***
	 * 查询某个分组下的所有字典
	 */
	public List<OaDict> getDctByGroupId(String groupId){
		return dao.find("select * from "+ tableName + " where group_id='"+groupId+"'");
	}
	
	/***
	 * 查询父级分组下所有分组
	 * @return
	 */
	public List<OaDictGroup> getGroupListByParentId(String groupId){
		return OaDictGroup.dao.find("select * from "+groupTableName+" where parent_id='"+groupId+"'");
	}
	
	/***
	 * 根据类型和键查询字典
	 * @return
	 */
	public OaDict getByKeyAndType(String key,String type){
		return dao.findFirst("select * from oa_dict d where d.type='"+type+"' and d.key='"+key+"'");
	}

	/***
	 * 根据类型和值查询字典
	 * @return
	 */
	public OaDict getByValueAndType(String value,String type){
		return dao.findFirst("select * from oa_dict d where d.type='"+type+"' and d.value='"+value+"'");
	}
	
	/***
	 * 查询某个类型的所有字典
	 * @param key
	 * @param type
	 * @return
	 */
	public List<OaDict> getByType(String type){
		return dao.find("select * from oa_dict d where d.type='"+type+"'");
	}
	
}