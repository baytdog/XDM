package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseVEmail;
@SuppressWarnings("serial")
public class VEmail extends BaseVEmail<VEmail> {
	public static final VEmail dao = new VEmail();
	public static final String tableName = "v_email";
	
	/***
	 * query by id
	 */
	public VEmail getById(String id){
		return VEmail.dao.findById(id);
	}
	
	/***
	 * del
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		VEmail o = VEmail.dao.getById(id);
    		o.delete();
    	}
	}
	
}