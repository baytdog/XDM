package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseXdSchoolinfo;
@SuppressWarnings("serial")
public class XdSchoolinfo extends BaseXdSchoolinfo<XdSchoolinfo> {
	public static final XdSchoolinfo dao = new XdSchoolinfo();
	public static final String tableName = "xd_schoolinfo";
	
	/***
	 * query by id
	 */
	public XdSchoolinfo getById(String id){
		return XdSchoolinfo.dao.findById(id);
	}
	
	/***
	 * del
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		XdSchoolinfo o = XdSchoolinfo.dao.getById(id);
    		o.delete();
    	}
	}
	
}