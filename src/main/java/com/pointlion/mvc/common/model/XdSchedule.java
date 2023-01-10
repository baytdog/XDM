package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseXdSchedule;
@SuppressWarnings("serial")
public class XdSchedule extends BaseXdSchedule<XdSchedule> {
	public static final XdSchedule dao = new XdSchedule();
	public static final String tableName = "xd_schedule";
	
	/***
	 * query by id
	 */
	public XdSchedule getById(String id){
		return XdSchedule.dao.findById(id);
	}
	
	/***
	 * del
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		XdSchedule o = XdSchedule.dao.getById(id);
    		o.delete();
    	}
	}
	
}