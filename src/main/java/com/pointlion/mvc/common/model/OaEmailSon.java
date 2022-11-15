package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseOaEmailSon;
@SuppressWarnings("serial")
public class OaEmailSon extends BaseOaEmailSon<OaEmailSon> {
	public static final OaEmailSon dao = new OaEmailSon();
	public static final String tableName = "oa_email_son";
	
	/***
	 * query by id
	 */
	public OaEmailSon getById(String id){
		return OaEmailSon.dao.findById(id);
	}
	
	/***
	 * del
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		OaEmailSon o = OaEmailSon.dao.getById(id);
    		o.delete();
    	}
	}
	
}