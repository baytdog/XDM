package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseXdEdutrain;
@SuppressWarnings("serial")
public class XdEdutrain extends BaseXdEdutrain<XdEdutrain> {
	public static final XdEdutrain dao = new XdEdutrain();
	public static final String tableName = "xd_edutrain";
	
	/***
	 * query by id
	 */
	public XdEdutrain getById(String id){
		return XdEdutrain.dao.findById(id);
	}
	
	/***
	 * del
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		XdEdutrain o = XdEdutrain.dao.getById(id);
    		o.delete();
    	}
	}
	
}