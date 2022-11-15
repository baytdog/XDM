package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseOaAnswers;
@SuppressWarnings("serial")
public class OaAnswers extends BaseOaAnswers<OaAnswers> {
	public static final OaAnswers dao = new OaAnswers();
	public static final String tableName = "oa_answers";
	
	/***
	 * query by id
	 */
	public OaAnswers getById(String id){
		return OaAnswers.dao.findById(id);
	}
	
	/***
	 * del
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		OaAnswers o = OaAnswers.dao.getById(id);
    		o.delete();
    	}
	}
	
}