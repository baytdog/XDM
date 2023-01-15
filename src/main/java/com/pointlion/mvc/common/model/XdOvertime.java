package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseXdOvertime;
@SuppressWarnings("serial")
public class XdOvertime extends BaseXdOvertime<XdOvertime> {
	public static final XdOvertime dao = new XdOvertime();
	public static final String tableName = "xd_overtime";
	
	/***
	 * query by id
	 */
	public XdOvertime getById(String id){
		return XdOvertime.dao.findById(id);
	}
	
	/***
	 * del
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		XdOvertime o = XdOvertime.dao.getById(id);
    		o.delete();
    	}
	}
	
}