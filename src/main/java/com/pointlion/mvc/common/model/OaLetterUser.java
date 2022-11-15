package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseOaLetterUser;
@SuppressWarnings("serial")
public class OaLetterUser extends BaseOaLetterUser<OaLetterUser> {
	public static final OaLetterUser dao = new OaLetterUser();
	public static final String tableName = "oa_letter_user";
	
	/***
	 * query by id
	 */
	public OaLetterUser getById(String id){
		return OaLetterUser.dao.findById(id);
	}
	
	/***
	 * del
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		OaLetterUser o = OaLetterUser.dao.getById(id);
    		o.delete();
    	}
	}
	
}