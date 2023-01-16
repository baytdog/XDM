package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseXdAnleaveSummary;
@SuppressWarnings("serial")
public class XdAnleaveSummary extends BaseXdAnleaveSummary<XdAnleaveSummary> {
	public static final XdAnleaveSummary dao = new XdAnleaveSummary();
	public static final String tableName = "xd_anleave_summary";
	
	/***
	 * query by id
	 */
	public XdAnleaveSummary getById(String id){
		return XdAnleaveSummary.dao.findById(id);
	}
	
	/***
	 * del
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		XdAnleaveSummary o = XdAnleaveSummary.dao.getById(id);
    		o.delete();
    	}
	}
	
}