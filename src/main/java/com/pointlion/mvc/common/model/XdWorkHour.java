package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseXdWorkHour;
@SuppressWarnings("serial")
public class XdWorkHour extends BaseXdWorkHour<XdWorkHour> {
	public static final XdWorkHour dao = new XdWorkHour();
	public static final String tableName = "xd_work_hour";
	
	/***
	 * query by id
	 */
	public XdWorkHour getById(String id){
		return XdWorkHour.dao.findById(id);
	}
	
	/***
	 * del
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		XdWorkHour o = XdWorkHour.dao.getById(id);
    		o.delete();
    	}
	}
	
}