package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseOaNodes;
@SuppressWarnings("serial")
public class OaNodes extends BaseOaNodes<OaNodes> {
	public static final OaNodes dao = new OaNodes();
	public static final String tableName = "oa_nodes";
	
	/***
	 * query by id
	 */
	public OaNodes getById(String id){
		return OaNodes.dao.findById(id);
	}
	
	/***
	 * del
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		OaNodes o = OaNodes.dao.getById(id);
    		o.delete();
    	}
	}
	
}