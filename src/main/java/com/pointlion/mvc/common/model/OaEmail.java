package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseOaEmail;
@SuppressWarnings("serial")
public class OaEmail extends BaseOaEmail<OaEmail> {
	public static final OaEmail dao = new OaEmail();
	public static final String tableName = "oa_email";
	
	/***
	 * query by id
	 */
	public OaEmail getById(String id){
		return OaEmail.dao.findById(id);
	}
	
	/***
	 * del
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		OaEmail o = OaEmail.dao.getById(id);
    		o.delete();
    	}
	}
	
}