package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseXdLeave;
@SuppressWarnings("serial")
public class XdLeave extends BaseXdLeave<XdLeave> {
	public static final XdLeave dao = new XdLeave();
	public static final String tableName = "xd_leave";
	
	/***
	 * query by id
	 */
	public XdLeave getById(String id){
		return XdLeave.dao.findById(id);
	}
	
	/***
	 * del
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		XdLeave o = XdLeave.dao.getById(id);
    		o.delete();
    	}
	}
	
}