package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseXdShift;
@SuppressWarnings("serial")
public class XdShift extends BaseXdShift<XdShift> {
	public static final XdShift dao = new XdShift();
	public static final String tableName = "xd_shift";
	
	/***
	 * query by id
	 */
	public XdShift getById(String id){
		return XdShift.dao.findById(id);
	}
	
	/***
	 * del
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		XdShift o = XdShift.dao.getById(id);
    		o.delete();
    	}
	}
	
}