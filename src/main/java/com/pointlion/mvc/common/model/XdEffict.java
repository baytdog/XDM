package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseXdEffict;
@SuppressWarnings("serial")
public class XdEffict extends BaseXdEffict<XdEffict> {
	public static final XdEffict dao = new XdEffict();
	public static final String tableName = "xd_effict";
	
	/***
	 * query by id
	 */
	public XdEffict getById(String id){
		return XdEffict.dao.findById(id);
	}
	
	/***
	 * del
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		XdEffict o = XdEffict.dao.getById(id);
    		o.delete();
    	}
	}
	
}