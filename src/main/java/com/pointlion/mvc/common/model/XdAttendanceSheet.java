package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseXdAttendanceSheet;
@SuppressWarnings("serial")
public class XdAttendanceSheet extends BaseXdAttendanceSheet<XdAttendanceSheet> {
	public static final XdAttendanceSheet dao = new XdAttendanceSheet();
	public static final String tableName = "xd_attendance_sheet";
	
	/***
	 * query by id
	 */
	public XdAttendanceSheet getById(String id){
		return XdAttendanceSheet.dao.findById(id);
	}
	
	/***
	 * del
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		XdAttendanceSheet o = XdAttendanceSheet.dao.getById(id);
    		o.delete();
    	}
	}
	
}