package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseOaStepHistory;
@SuppressWarnings("serial")
public class OaStepHistory extends BaseOaStepHistory<OaStepHistory> {
	public static final OaStepHistory dao = new OaStepHistory();
	public static final String tableName = "oa_step_history";
	
	/***
	 * query by id
	 */
	public OaStepHistory getById(String id){
		return OaStepHistory.dao.findById(id);
	}
	
	/***
	 * del
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		OaStepHistory o = OaStepHistory.dao.getById(id);
    		o.delete();
    	}
	}
	
}