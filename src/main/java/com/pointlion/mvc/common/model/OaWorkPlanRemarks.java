package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseOaWorkPlanRemarks;
@SuppressWarnings("serial")
public class OaWorkPlanRemarks extends BaseOaWorkPlanRemarks<OaWorkPlanRemarks> {
	public static final OaWorkPlanRemarks dao = new OaWorkPlanRemarks();
	public static final String tableName = "oa_work_plan_remarks";
	
	/***
	 * query by id
	 */
	public OaWorkPlanRemarks getById(String id){
		return OaWorkPlanRemarks.dao.findById(id);
	}
	
	/***
	 * del
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		OaWorkPlanRemarks o = OaWorkPlanRemarks.dao.getById(id);
    		o.delete();
    	}
	}
	
}