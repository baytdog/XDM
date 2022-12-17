package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseXdDictGroup;
@SuppressWarnings("serial")
public class XdDictGroup extends BaseXdDictGroup<XdDictGroup> {
	public static final XdDictGroup dao = new XdDictGroup();
	public static final String tableName = "xd_dict_group";
	
	/***
	 * query by id
	 */
	public XdDictGroup getById(String id){
		return XdDictGroup.dao.findById(id);
	}
	
	/***
	 * del
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		XdDictGroup o = XdDictGroup.dao.getById(id);
    		o.delete();
    	}
	}
	
}