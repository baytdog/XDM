package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseXdEmployee;
@SuppressWarnings("serial")
public class XdEmployee extends BaseXdEmployee<XdEmployee> {
	public static final XdEmployee dao = new XdEmployee();
	public static final String tableName = "xd_employee";
	/***
	 * query by id
	 */
	public XdEmployee getById(String id){
		return XdEmployee.dao.findById(id);
	}
	
	/***
	 * del
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		XdEmployee o = XdEmployee.dao.getById(id);
    		o.delete();
    	}
	}
	
}