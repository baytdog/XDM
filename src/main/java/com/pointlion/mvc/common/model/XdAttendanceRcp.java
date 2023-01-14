package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseXdAttendanceRcp;
@SuppressWarnings("serial")
public class XdAttendanceRcp extends BaseXdAttendanceRcp<XdAttendanceRcp> {
	public static final XdAttendanceRcp dao = new XdAttendanceRcp();
	public static final String tableName = "xd_attendance_rcp";
	
	/***
	 * query by id
	 */
	public XdAttendanceRcp getById(String id){
		return XdAttendanceRcp.dao.findById(id);
	}
	
	/***
	 * del
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		XdAttendanceRcp o = XdAttendanceRcp.dao.getById(id);
    		o.delete();
    	}
	}
	
}