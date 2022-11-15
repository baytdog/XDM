package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseGrapUrl;
@SuppressWarnings("serial")
public class GrapUrl extends BaseGrapUrl<GrapUrl> {
	public static final GrapUrl dao = new GrapUrl();
	public static final String tableName = "grap_url";
	
	/***
	 * 根据主键查询
	 */
	public GrapUrl getById(String id){
		return GrapUrl.dao.findById(id);
	}
	
	/***
	 * 删除
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		GrapUrl o = GrapUrl.dao.getById(id);
    		o.delete();
    	}
	}
	
}