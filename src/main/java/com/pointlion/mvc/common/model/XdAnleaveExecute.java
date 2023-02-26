package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseXdAnleaveExecute;
@SuppressWarnings("serial")
public class XdAnleaveExecute extends BaseXdAnleaveExecute<XdAnleaveExecute> {
	public static final XdAnleaveExecute dao = new XdAnleaveExecute();
	public static final String tableName = "xd_anleave_execute";
	
	/***
	 * query by id
	 */
	public XdAnleaveExecute getById(String id){
		return XdAnleaveExecute.dao.findById(id);
	}
	
	/***
	 * del
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		XdAnleaveExecute o = XdAnleaveExecute.dao.getById(id);
    		o.delete();
    	}
	}
	
}