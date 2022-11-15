package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseOaMenuDict;
@SuppressWarnings("serial")
public class OaMenuDict extends BaseOaMenuDict<OaMenuDict> {
	public static final OaMenuDict dao = new OaMenuDict();
	public static final String tableName = "oa_menu_dict";
	
	/***
	 * query by id
	 */
	public OaMenuDict getById(String id){
		return OaMenuDict.dao.findById(id);
	}
	
	/***
	 * del
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		OaMenuDict o = OaMenuDict.dao.getById(id);
    		o.delete();
    	}
	}
	
}