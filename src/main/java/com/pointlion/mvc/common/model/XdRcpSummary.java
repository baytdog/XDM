package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseXdRcpSummary;
@SuppressWarnings("serial")
public class XdRcpSummary extends BaseXdRcpSummary<XdRcpSummary> {
	public static final XdRcpSummary dao = new XdRcpSummary();
	public static final String tableName = "xd_rcp_summary";
	
	/***
	 * query by id
	 */
	public XdRcpSummary getById(String id){
		return XdRcpSummary.dao.findById(id);
	}
	
	/***
	 * del
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		XdRcpSummary o = XdRcpSummary.dao.getById(id);
    		o.delete();
    	}
	}
	
}