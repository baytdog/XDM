package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseXdTotalTimeOnline;
@SuppressWarnings("serial")
public class XdTotalTimeOnline extends BaseXdTotalTimeOnline<XdTotalTimeOnline> {
	public static final XdTotalTimeOnline dao = new XdTotalTimeOnline();
	public static final String tableName = "xd_total_time_online";
	
	/***
	 * query by id
	 */
	public XdTotalTimeOnline getById(String id){
		return XdTotalTimeOnline.dao.findById(id);
	}
	
	/***
	 * del
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		XdTotalTimeOnline o = XdTotalTimeOnline.dao.getById(id);
    		o.delete();
    	}
	}
	
}