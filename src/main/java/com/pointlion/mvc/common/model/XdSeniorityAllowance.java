package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseXdSeniorityAllowance;
@SuppressWarnings("serial")
public class XdSeniorityAllowance extends BaseXdSeniorityAllowance<XdSeniorityAllowance> {
	public static final XdSeniorityAllowance dao = new XdSeniorityAllowance();
	public static final String tableName = "xd_seniority_allowance";
	
	/***
	 * query by id
	 */
	public XdSeniorityAllowance getById(String id){
		return XdSeniorityAllowance.dao.findById(id);
	}
	
	/***
	 * del
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		XdSeniorityAllowance o = XdSeniorityAllowance.dao.getById(id);
    		o.delete();
    	}
	}
	
}