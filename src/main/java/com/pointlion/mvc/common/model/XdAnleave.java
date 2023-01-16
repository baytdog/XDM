package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseXdAnleave;
@SuppressWarnings("serial")
public class XdAnleave extends BaseXdAnleave<XdAnleave> {
	public static final XdAnleave dao = new XdAnleave();
	public static final String tableName = "xd_anleave";
	
	/***
	 * query by id
	 */
	public XdAnleave getById(String id){
		return XdAnleave.dao.findById(id);
	}
	
	/***
	 * del
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		XdAnleave o = XdAnleave.dao.getById(id);
    		o.delete();
    	}
	}
	
}