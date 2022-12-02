package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseXdOplogSummary;
@SuppressWarnings("serial")
public class XdOplogSummary extends BaseXdOplogSummary<XdOplogSummary> {
	public static final XdOplogSummary dao = new XdOplogSummary();
	public static final String tableName = "xd_oplog_summary";
	
	/***
	 * query by id
	 */
	public XdOplogSummary getById(String id){
		return XdOplogSummary.dao.findById(id);
	}
	
	/***
	 * del
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		XdOplogSummary o = XdOplogSummary.dao.getById(id);
    		o.delete();
    	}
	}
	
}