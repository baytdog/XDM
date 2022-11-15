package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseOaApplyWorkOvertime;
@SuppressWarnings("serial")
public class OaApplyWorkOvertime extends BaseOaApplyWorkOvertime<OaApplyWorkOvertime> {
	public static final OaApplyWorkOvertime dao = new OaApplyWorkOvertime();
	public static final String tableName = "oa_apply_work_overtime";
	
	/***
	 * query by id
	 */
	public OaApplyWorkOvertime getById(String id){
		return OaApplyWorkOvertime.dao.findById(id);
	}
	
	/***
	 * del
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		OaApplyWorkOvertime o = OaApplyWorkOvertime.dao.getById(id);
    		o.delete();
    	}
	}
	
}