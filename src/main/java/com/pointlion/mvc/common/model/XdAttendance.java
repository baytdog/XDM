package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseXdAttendance;
@SuppressWarnings("serial")
public class XdAttendance extends BaseXdAttendance<XdAttendance> {
	public static final XdAttendance dao = new XdAttendance();
	public static final String tableName = "xd_attendance";
	
	/***
	 * query by id
	 */
	public XdAttendance getById(String id){
		return XdAttendance.dao.findById(id);
	}
	
	/***
	 * del
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		XdAttendance o = XdAttendance.dao.getById(id);
    		o.delete();
    	}
	}
	
}