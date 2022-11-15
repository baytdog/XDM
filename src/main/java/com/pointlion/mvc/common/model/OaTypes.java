package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseOaTypes;
@SuppressWarnings("serial")
public class OaTypes extends BaseOaTypes<OaTypes> {
	public static final OaTypes dao = new OaTypes();
	public static final String tableName = "oa_types";
	
	/***
	 * query by id
	 */
	public OaTypes getById(String id){
		return OaTypes.dao.findById(id);
	}
	
	/***
	 * del
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		OaTypes o = OaTypes.dao.getById(id);
    		o.delete();
    	}
	}
	
}