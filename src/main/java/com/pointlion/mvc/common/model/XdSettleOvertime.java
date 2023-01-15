package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseXdSettleOvertime;
@SuppressWarnings("serial")
public class XdSettleOvertime extends BaseXdSettleOvertime<XdSettleOvertime> {
	public static final XdSettleOvertime dao = new XdSettleOvertime();
	public static final String tableName = "xd_settle_overtime";
	
	/***
	 * query by id
	 */
	public XdSettleOvertime getById(String id){
		return XdSettleOvertime.dao.findById(id);
	}
	
	/***
	 * del
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		XdSettleOvertime o = XdSettleOvertime.dao.getById(id);
    		o.delete();
    	}
	}
	
}