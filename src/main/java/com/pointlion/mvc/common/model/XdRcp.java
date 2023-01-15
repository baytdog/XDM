package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseXdRcp;
@SuppressWarnings("serial")
public class XdRcp extends BaseXdRcp<XdRcp> {
	public static final XdRcp dao = new XdRcp();
	public static final String tableName = "xd_rcp";
	
	/***
	 * query by id
	 */
	public XdRcp getById(String id){
		return XdRcp.dao.findById(id);
	}
	
	/***
	 * del
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		XdRcp o = XdRcp.dao.getById(id);
    		o.delete();
    	}
	}
	
}