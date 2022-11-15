package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseOaMeetroom;
@SuppressWarnings("serial")
public class OaMeetroom extends BaseOaMeetroom<OaMeetroom> {
	public static final OaMeetroom dao = new OaMeetroom();
	public static final String tableName = "oa_meetroom";
	
	/***
	 * query by id
	 */
	public OaMeetroom getById(String id){
		return OaMeetroom.dao.findById(id);
	}
	
	/***
	 * del
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		OaMeetroom o = OaMeetroom.dao.getById(id);
    		o.delete();
    	}
	}
	
}