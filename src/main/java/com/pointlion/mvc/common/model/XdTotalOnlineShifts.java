package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseXdTotalOnlineShifts;
@SuppressWarnings("serial")
public class XdTotalOnlineShifts extends BaseXdTotalOnlineShifts<XdTotalOnlineShifts> {
	public static final XdTotalOnlineShifts dao = new XdTotalOnlineShifts();
	public static final String tableName = "xd_total_online_shifts";
	
	/***
	 * query by id
	 */
	public XdTotalOnlineShifts getById(String id){
		return XdTotalOnlineShifts.dao.findById(id);
	}
	
	/***
	 * del
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		XdTotalOnlineShifts o = XdTotalOnlineShifts.dao.getById(id);
    		o.delete();
    	}
	}
	
}