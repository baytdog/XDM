package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseOaQuestions;
@SuppressWarnings("serial")
public class OaQuestions extends BaseOaQuestions<OaQuestions> {
	public static final OaQuestions dao = new OaQuestions();
	public static final String tableName = "oa_questions";
	
	/***
	 * query by id
	 */
	public OaQuestions getById(String id){
		return OaQuestions.dao.findById(id);
	}
	
	/***
	 * del
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		OaQuestions o = OaQuestions.dao.getById(id);
    		o.delete();
    	}
	}
	
}