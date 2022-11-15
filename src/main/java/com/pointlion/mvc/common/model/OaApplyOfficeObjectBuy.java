package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseOaApplyOfficeObjectBuy;
@SuppressWarnings("serial")
public class OaApplyOfficeObjectBuy extends BaseOaApplyOfficeObjectBuy<OaApplyOfficeObjectBuy> {
	public static final OaApplyOfficeObjectBuy dao = new OaApplyOfficeObjectBuy();
	public static final String tableName = "oa_apply_office_object_buy";
	
	/***
	 */
	public OaApplyOfficeObjectBuy getById(String id){
		return OaApplyOfficeObjectBuy.dao.findById(id);
	}
	
	/***
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		OaApplyOfficeObjectBuy o = OaApplyOfficeObjectBuy.dao.getById(id);
    		o.delete();
    	}
	}
	
}