package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseXdAttendanceDetail;
@SuppressWarnings("serial")
public class XdAttendanceDetail extends BaseXdAttendanceDetail<XdAttendanceDetail> {
	public static final XdAttendanceDetail dao = new XdAttendanceDetail();
	public static final String tableName = "xd_attendance_detail";
	
	/***
	 * query by id
	 */
	public XdAttendanceDetail getById(String id){
		return XdAttendanceDetail.dao.findById(id);
	}
	
	/***
	 * del
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		XdAttendanceDetail o = XdAttendanceDetail.dao.getById(id);
    		o.delete();
    	}
	}
	
}