package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseXdLogsLeave;
@SuppressWarnings("serial")
public class XdLogsLeave extends BaseXdLogsLeave<XdLogsLeave> {
	public static final XdLogsLeave dao = new XdLogsLeave();
	public static final String tableName = "xd_logs_leave";
	
	/***
	 * query by id
	 */
	public XdLogsLeave getById(String id){
		return XdLogsLeave.dao.findById(id);
	}
	
	/***
	 * del
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		XdLogsLeave o = XdLogsLeave.dao.getById(id);
    		o.delete();
    	}
	}
	
}