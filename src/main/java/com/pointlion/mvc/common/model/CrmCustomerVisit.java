package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseCrmCustomerVisit;
@SuppressWarnings("serial")
public class CrmCustomerVisit extends BaseCrmCustomerVisit<CrmCustomerVisit> {
	public static final CrmCustomerVisit dao = new CrmCustomerVisit();
	public static final String tableName = "crm_customer_visit";
	
	/***
	 * query by id
	 */
	public CrmCustomerVisit getById(String id){
		return CrmCustomerVisit.dao.findById(id);
	}
	
	/***
	 * del
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		CrmCustomerVisit o = CrmCustomerVisit.dao.getById(id);
    		o.delete();
    	}
	}
	
}