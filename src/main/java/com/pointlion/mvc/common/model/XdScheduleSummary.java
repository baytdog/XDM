package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseXdScheduleSummary;
@SuppressWarnings("serial")
public class XdScheduleSummary extends BaseXdScheduleSummary<XdScheduleSummary> {
	public static final XdScheduleSummary dao = new XdScheduleSummary();
	public static final String tableName = "xd_schedule_summary";
	
	/***
	 * query by id
	 */
	public XdScheduleSummary getById(String id){
		return XdScheduleSummary.dao.findById(id);
	}
	
	/***
	 * del
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		XdScheduleSummary o = XdScheduleSummary.dao.getById(id);
    		o.delete();
    	}
	}
	
}