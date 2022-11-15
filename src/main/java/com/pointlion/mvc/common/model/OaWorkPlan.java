package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseOaWorkPlan;
@SuppressWarnings("serial")
public class OaWorkPlan extends BaseOaWorkPlan<OaWorkPlan> {
	public static final OaWorkPlan dao = new OaWorkPlan();
	public static final String tableName = "oa_work_plan";
	
	/***
	 * query by id
	 */
	public OaWorkPlan getById(String id){
		return OaWorkPlan.dao.findById(id);
	}
	
	/***
	 * del
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		OaWorkPlan o = OaWorkPlan.dao.getById(id);
    		o.delete();
    	}
	}
	
}