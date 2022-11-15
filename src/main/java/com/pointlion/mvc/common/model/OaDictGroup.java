package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseOaDictGroup;
@SuppressWarnings("serial")
public class OaDictGroup extends BaseOaDictGroup<OaDictGroup> {
	public static final OaDictGroup dao = new OaDictGroup();
	public static final String tableName = "oa_dict_group";
	
	/***
	 * query by id
	 */
	public OaDictGroup getById(String id){
		return OaDictGroup.dao.findById(id);
	}
	
	/***
	 * del
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		OaDictGroup o = OaDictGroup.dao.getById(id);
    		o.delete();
    	}
	}
	
}