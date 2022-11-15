package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseOaEdLetter;
@SuppressWarnings("serial")
public class OaEdLetter extends BaseOaEdLetter<OaEdLetter> {
	public static final OaEdLetter dao = new OaEdLetter();
	public static final String tableName = "oa_ed_letter";
	
	/***
	 * query by id
	 */
	public OaEdLetter getById(String id){
		return OaEdLetter.dao.findById(id);
	}
	
	/***
	 * del
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		OaEdLetter o = OaEdLetter.dao.getById(id);
    		o.delete();
    	}
	}
	
}