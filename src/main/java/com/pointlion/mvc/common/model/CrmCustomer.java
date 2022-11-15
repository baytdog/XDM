package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseCrmCustomer;
@SuppressWarnings("serial")
public class CrmCustomer extends BaseCrmCustomer<CrmCustomer> {
	public static final CrmCustomer dao = new CrmCustomer();
	public static final String tableName = "crm_customer";
	
	/***
	 * query by id
	 */
	public CrmCustomer getById(String id){
		return CrmCustomer.dao.findById(id);
	}
	
	/***
	 * del
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		CrmCustomer o = CrmCustomer.dao.getById(id);
    		o.delete();
    	}
	}
	
}