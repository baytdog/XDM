package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseOaShowinfo;
@SuppressWarnings("serial")
public class OaShowinfo extends BaseOaShowinfo<OaShowinfo> {
	public static final OaShowinfo dao = new OaShowinfo();
	public static final String tableName = "oa_showinfo";
	
	/***
	 * query by id
	 */
	public OaShowinfo getById(String id){
		return OaShowinfo.dao.findById(id);
	}
	
	/***
	 * del
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		OaShowinfo o = OaShowinfo.dao.getById(id);
    		o.delete();
    	}
	}
	
}