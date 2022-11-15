package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseOaTestresult;
@SuppressWarnings("serial")
public class OaTestresult extends BaseOaTestresult<OaTestresult> {
	public static final OaTestresult dao = new OaTestresult();
	public static final String tableName = "oa_testresult";
	
	/***
	 * query by id
	 */
	public OaTestresult getById(String id){
		return OaTestresult.dao.findById(id);
	}
	
	/***
	 * del
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		OaTestresult o = OaTestresult.dao.getById(id);
    		o.delete();
    	}
	}
	
}