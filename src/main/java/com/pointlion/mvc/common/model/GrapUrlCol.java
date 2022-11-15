package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseGrapUrlCol;
@SuppressWarnings("serial")
public class GrapUrlCol extends BaseGrapUrlCol<GrapUrlCol> {
	public static final GrapUrlCol dao = new GrapUrlCol();
	public static final String tableName = "grap_url_col";
	
	/***
	 * 根据主键查询
	 */
	public GrapUrlCol getById(String id){
		return GrapUrlCol.dao.findById(id);
	}
	
	/***
	 * 删除
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		GrapUrlCol o = GrapUrlCol.dao.getById(id);
    		o.delete();
    	}
	}
	
}