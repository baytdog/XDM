package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseOaHotline;
@SuppressWarnings("serial")
public class OaHotline extends BaseOaHotline<OaHotline> {
	public static final OaHotline dao = new OaHotline();
	public static final String tableName = "oa_hotline";
	
	/***
	 * query by id
	 */
	public OaHotline getById(String id){
		return OaHotline.dao.findById(id);
	}
	
	/***
	 * del
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		OaHotline o = OaHotline.dao.getById(id);
    		o.delete();
    	}
	}
	
}