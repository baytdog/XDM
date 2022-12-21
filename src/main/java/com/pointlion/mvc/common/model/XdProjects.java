package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseXdProjects;
@SuppressWarnings("serial")
public class XdProjects extends BaseXdProjects<XdProjects> {
	public static final XdProjects dao = new XdProjects();
	public static final String tableName = "xd_projects";
	
	/***
	 * query by id
	 */
	public XdProjects getById(String id){
		return XdProjects.dao.findById(id);
	}
	
	/***
	 * del
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		XdProjects o = XdProjects.dao.getById(id);
    		o.delete();
    	}
	}
	
}