package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseOaApplyCost;
@SuppressWarnings("serial")
public class OaApplyCost extends BaseOaApplyCost<OaApplyCost> {
	public static final OaApplyCost dao = new OaApplyCost();
	public static final String tableName = "oa_apply_cost";
	
	/***
	 * 根据主键查询
	 */
	public OaApplyCost getById(String id){
		return OaApplyCost.dao.findById(id);
	}
	
	/***
	 * 删除
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		OaApplyCost o = OaApplyCost.dao.getById(id);
    		o.delete();
    	}
	}
	
}