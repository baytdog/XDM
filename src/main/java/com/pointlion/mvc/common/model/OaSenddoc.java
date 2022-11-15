package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseOaSenddoc;
@SuppressWarnings("serial")
public class OaSenddoc extends BaseOaSenddoc<OaSenddoc> {
	public static final OaSenddoc dao = new OaSenddoc();
	public static final String tableName = "oa_senddoc";
	
	/***
	 * query by id
	 */
	public OaSenddoc getById(String id){
		return OaSenddoc.dao.findById(id);
	}
	
	/***
	 * del
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		OaSenddoc o = OaSenddoc.dao.getById(id);
    		o.delete();
    	}
	}
	
}