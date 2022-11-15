package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseOaSteps;
@SuppressWarnings("serial")
public class OaSteps extends BaseOaSteps<OaSteps> {
	public static final OaSteps dao = new OaSteps();
	public static final String tableName = "oa_steps";
	
	/***
	 * query by id
	 */
	public OaSteps getById(String id){
		return OaSteps.dao.findById(id);
	}
	
	/***
	 * del
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		OaSteps o = OaSteps.dao.getById(id);
    		o.delete();
    	}
	}
	
}