package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseXdOvertimeSummary;
@SuppressWarnings("serial")
public class XdOvertimeSummary extends BaseXdOvertimeSummary<XdOvertimeSummary> {
	public static final XdOvertimeSummary dao = new XdOvertimeSummary();
	public static final String tableName = "xd_overtime_summary";
	
	/***
	 * query by id
	 */
	public XdOvertimeSummary getById(String id){
		return XdOvertimeSummary.dao.findById(id);
	}
	
	/***
	 * del
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		XdOvertimeSummary o = XdOvertimeSummary.dao.getById(id);
    		o.delete();
    	}
	}
	
}