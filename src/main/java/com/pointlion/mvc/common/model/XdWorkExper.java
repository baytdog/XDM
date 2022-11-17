package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseXdWorkExper;
@SuppressWarnings("serial")
public class XdWorkExper extends BaseXdWorkExper<XdWorkExper> {
	public static final XdWorkExper dao = new XdWorkExper();
	public static final String tableName = "xd_work_exper";
	
	/***
	 * query by id
	 */
	public XdWorkExper getById(String id){
		return XdWorkExper.dao.findById(id);
	}
	
	/***
	 * del
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		XdWorkExper o = XdWorkExper.dao.getById(id);
    		o.delete();
    	}
	}
	
}