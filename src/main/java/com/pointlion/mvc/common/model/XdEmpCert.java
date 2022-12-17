package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseXdEmpCert;
@SuppressWarnings("serial")
public class XdEmpCert extends BaseXdEmpCert<XdEmpCert> {
	public static final XdEmpCert dao = new XdEmpCert();
	public static final String tableName = "xd_emp_cert";
	
	/***
	 * query by id
	 */
	public XdEmpCert getById(String id){
		return XdEmpCert.dao.findById(id);
	}
	
	/***
	 * del
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		XdEmpCert o = XdEmpCert.dao.getById(id);
    		o.delete();
    	}
	}
	
}