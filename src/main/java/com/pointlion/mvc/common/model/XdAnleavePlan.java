package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseXdAnleavePlan;
@SuppressWarnings("serial")
public class XdAnleavePlan extends BaseXdAnleavePlan<XdAnleavePlan> {
	public static final XdAnleavePlan dao = new XdAnleavePlan();
	public static final String tableName = "xd_anleave_plan";
	
	/***
	 * query by id
	 */
	public XdAnleavePlan getById(String id){
		return XdAnleavePlan.dao.findById(id);
	}
	
	/***
	 * del
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		XdAnleavePlan o = XdAnleavePlan.dao.getById(id);
    		o.delete();
    	}
	}
	
}