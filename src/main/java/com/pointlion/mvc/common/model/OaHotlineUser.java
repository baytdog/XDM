package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseOaHotlineUser;
@SuppressWarnings("serial")
public class OaHotlineUser extends BaseOaHotlineUser<OaHotlineUser> {
	public static final OaHotlineUser dao = new OaHotlineUser();
	public static final String tableName = "oa_hotline_user";
	
	/***
	 * query by id
	 */
	public OaHotlineUser getById(String id){
		return OaHotlineUser.dao.findById(id);
	}
	
	/***
	 * del
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		OaHotlineUser o = OaHotlineUser.dao.getById(id);
    		o.delete();
    	}
	}
	
}