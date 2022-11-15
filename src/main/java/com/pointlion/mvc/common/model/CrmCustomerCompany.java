package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseCrmCustomerCompany;

import java.util.List;

@SuppressWarnings("serial")
public class CrmCustomerCompany extends BaseCrmCustomerCompany<CrmCustomerCompany> {
	public static final CrmCustomerCompany dao = new CrmCustomerCompany();
	public static final String tableName = "crm_customer_company";
	
	/***
	 * query by id
	 */
	public CrmCustomerCompany getById(String id){
		return CrmCustomerCompany.dao.findById(id);
	}
	
	/***
	 * del
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		CrmCustomerCompany o = CrmCustomerCompany.dao.getById(id);
    		o.delete();
    	}
	}

	/***
	 * 查询全部可用数据
	 */
	public List<CrmCustomerCompany> findCanUseCompany(){
//		return dao.find("select * from crm_customer_company c where c.if_agree='1'");
		return dao.find("select * from crm_customer_company c ");
	}
}