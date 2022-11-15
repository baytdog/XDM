package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseOaLetter;
@SuppressWarnings("serial")
public class OaLetter extends BaseOaLetter<OaLetter> {
	public static final OaLetter dao = new OaLetter();
	public static final String tableName = "oa_letter";
	
	/***
	 * query by id
	 */
	public OaLetter getById(String id){
		return OaLetter.dao.findById(id);
	}
	
	/***
	 * del
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		OaLetter o = OaLetter.dao.getById(id);
    		o.delete();
    	}
	}
	
}