package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseOaBumphUser;
@SuppressWarnings("serial")
public class OaBumphUser extends BaseOaBumphUser<OaBumphUser> {
	public static final OaBumphUser dao = new OaBumphUser();
	public static final String TABLE_NAME = "oa_bumph_user";
	
	/***
	 * query by id
	 */
	public OaBumphUser getById(String id){
		return OaBumphUser.dao.findById(id);
	}
	
	/***
	 * del
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		OaBumphUser o = OaBumphUser.dao.getById(id);
    		o.delete();
    	}
	}
	
}