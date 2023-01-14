package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseXdAttendanceDays;
@SuppressWarnings("serial")
public class XdAttendanceDays extends BaseXdAttendanceDays<XdAttendanceDays> {
	public static final XdAttendanceDays dao = new XdAttendanceDays();
	public static final String tableName = "xd_attendance_days";
	
	/***
	 * query by id
	 */
	public XdAttendanceDays getById(String id){
		return XdAttendanceDays.dao.findById(id);
	}
	
	/***
	 * del
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		XdAttendanceDays o = XdAttendanceDays.dao.getById(id);
    		o.delete();
    	}
	}
	
}