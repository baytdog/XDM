package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseXdAttendanceSummary;
@SuppressWarnings("serial")
public class XdAttendanceSummary extends BaseXdAttendanceSummary<XdAttendanceSummary> {
	public static final XdAttendanceSummary dao = new XdAttendanceSummary();
	public static final String tableName = "xd_attendance_summary";
	
	/***
	 * query by id
	 */
	public XdAttendanceSummary getById(String id){
		return XdAttendanceSummary.dao.findById(id);
	}
	
	/***
	 * del
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		XdAttendanceSummary o = XdAttendanceSummary.dao.getById(id);
    		o.delete();
    	}
	}
	
}