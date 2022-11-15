package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseOaQchoice;
@SuppressWarnings("serial")
public class OaQchoice extends BaseOaQchoice<OaQchoice> {
	public static final OaQchoice dao = new OaQchoice();
	public static final String tableName = "oa_qchoice";
	
	/***
	 * query by id
	 */
	public OaQchoice getById(String id){
		return OaQchoice.dao.findById(id);
	}
	
	/***
	 * del
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		OaQchoice o = OaQchoice.dao.getById(id);
    		o.delete();
    	}
	}
	
}