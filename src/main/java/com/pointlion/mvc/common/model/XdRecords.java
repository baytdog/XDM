package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseXdRecords;
@SuppressWarnings("serial")
public class XdRecords extends BaseXdRecords<XdRecords> {
	public static final XdRecords dao = new XdRecords();
	public static final String tableName = "xd_records";
	
	/***
	 * query by id
	 */
	public XdRecords getById(String id){
		return XdRecords.dao.findById(id);
	}
	
	/***
	 * del
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		XdRecords o = XdRecords.dao.getById(id);
    		o.delete();
    	}
	}
	
}