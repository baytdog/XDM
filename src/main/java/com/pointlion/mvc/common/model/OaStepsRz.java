package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseOaStepsRz;
@SuppressWarnings("serial")
public class OaStepsRz extends BaseOaStepsRz<OaStepsRz> {
	public static final OaStepsRz dao = new OaStepsRz();
	public static final String tableName = "oa_steps_rz";
	
	/***
	 * query by id
	 */
	public OaStepsRz getById(String id){
		return OaStepsRz.dao.findById(id);
	}
	
	/***
	 * del
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		OaStepsRz o = OaStepsRz.dao.getById(id);
    		o.delete();
    	}
	}
	
}