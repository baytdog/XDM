package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseCrmOrder;
@SuppressWarnings("serial")
public class CrmOrder extends BaseCrmOrder<CrmOrder> {
	public static final CrmOrder dao = new CrmOrder();
	public static final String tableName = "crm_order";
	
	/***
	 * query by id
	 */
	public CrmOrder getById(String id){
		return CrmOrder.dao.findById(id);
	}
	
	/***
	 * del
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		CrmOrder o = CrmOrder.dao.getById(id);
    		o.delete();
    	}
	}
	
}