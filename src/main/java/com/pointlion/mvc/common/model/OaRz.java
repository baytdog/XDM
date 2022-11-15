package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseOaRz;
@SuppressWarnings("serial")
public class OaRz extends BaseOaRz<OaRz> {
	public static final OaRz dao = new OaRz();
	public static final String tableName = "oa_rz";
	
	/***
	 * query by id
	 */
	public OaRz getById(String id){
		return OaRz.dao.findById(id);
	}
	
	/***
	 * del
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		OaRz o = OaRz.dao.getById(id);
    		o.delete();
    	}
	}
	
}