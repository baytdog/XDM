package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseXdScheduleDetail;
@SuppressWarnings("serial")
public class XdScheduleDetail extends BaseXdScheduleDetail<XdScheduleDetail> {
	public static final XdScheduleDetail dao = new XdScheduleDetail();
	public static final String tableName = "xd_schedule_detail";
	
	/***
	 * query by id
	 */
	public XdScheduleDetail getById(String id){
		return XdScheduleDetail.dao.findById(id);
	}
	
	/***
	 * del
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		XdScheduleDetail o = XdScheduleDetail.dao.getById(id);
    		o.delete();
    	}
	}
	
}