package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseXdSteps;
@SuppressWarnings("serial")
public class XdSteps extends BaseXdSteps<XdSteps> {
	public static final XdSteps dao = new XdSteps();
	public static final String tableName = "xd_steps";
	
	/***
	 * query by id
	 */
	public XdSteps getById(String id){
		return XdSteps.dao.findById(id);
	}
	
	/***
	 * del
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		XdSteps o = XdSteps.dao.getById(id);
    		o.delete();
    	}
	}
	
}