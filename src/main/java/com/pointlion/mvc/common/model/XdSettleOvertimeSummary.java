package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseXdSettleOvertimeSummary;
@SuppressWarnings("serial")
public class XdSettleOvertimeSummary extends BaseXdSettleOvertimeSummary<XdSettleOvertimeSummary> {
	public static final XdSettleOvertimeSummary dao = new XdSettleOvertimeSummary();
	public static final String tableName = "xd_settle_overtime_summary";
	
	/***
	 * query by id
	 */
	public XdSettleOvertimeSummary getById(String id){
		return XdSettleOvertimeSummary.dao.findById(id);
	}
	
	/***
	 * del
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		XdSettleOvertimeSummary o = XdSettleOvertimeSummary.dao.getById(id);
    		o.delete();
    	}
	}
	
}