package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseOaDepartNotices;
@SuppressWarnings("serial")
public class OaDepartNotices extends BaseOaDepartNotices<OaDepartNotices> {
	public static final OaDepartNotices dao = new OaDepartNotices();
	public static final String tableName = "oa_depart_notices";
	
	/***
	 * query by id
	 */
	public OaDepartNotices getById(String id){
		return OaDepartNotices.dao.findById(id);
	}
	
	/***
	 * del
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		OaDepartNotices o = OaDepartNotices.dao.getById(id);
    		o.delete();
    	}
	}
	
}