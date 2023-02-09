package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseXdAttendanceLogs;
@SuppressWarnings("serial")
public class XdAttendanceLogs extends BaseXdAttendanceLogs<XdAttendanceLogs> {
	public static final XdAttendanceLogs dao = new XdAttendanceLogs();
	public static final String tableName = "xd_attendance_logs";
	
	/***
	 * query by id
	 */
	public XdAttendanceLogs getById(String id){
		return XdAttendanceLogs.dao.findById(id);
	}
	
	/***
	 * del
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		XdAttendanceLogs o = XdAttendanceLogs.dao.getById(id);
    		o.delete();
    	}
	}
	
}