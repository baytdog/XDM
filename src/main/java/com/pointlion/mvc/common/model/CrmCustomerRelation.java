package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseCrmCustomerRelation;
@SuppressWarnings("serial")
public class CrmCustomerRelation extends BaseCrmCustomerRelation<CrmCustomerRelation> {
	public static final CrmCustomerRelation dao = new CrmCustomerRelation();
	public static final String tableName = "crm_customer_relation";
	
	/***
	 * query by id
	 */
	public CrmCustomerRelation getById(String id){
		return CrmCustomerRelation.dao.findById(id);
	}
	
	/***
	 * del
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		CrmCustomerRelation o = CrmCustomerRelation.dao.getById(id);
    		o.delete();
    	}
	}
	
}