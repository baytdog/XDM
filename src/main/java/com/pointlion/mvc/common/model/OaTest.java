package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseOaTest;
@SuppressWarnings("serial")
public class OaTest extends BaseOaTest<OaTest> {
	public static final OaTest dao = new OaTest();
	public static final String tableName = "oa_test";
	
	/***
	 * query by id
	 */
	public OaTest getById(String id){
		return OaTest.dao.findById(id);
	}
	
	/***
	 * del
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		OaTest o = OaTest.dao.getById(id);
    		o.delete();
    	}
	}
	
}