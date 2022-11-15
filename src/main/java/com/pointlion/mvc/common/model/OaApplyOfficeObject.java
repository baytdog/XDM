package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseOaApplyOfficeObject;
@SuppressWarnings("serial")
public class OaApplyOfficeObject extends BaseOaApplyOfficeObject<OaApplyOfficeObject> {
	public static final OaApplyOfficeObject dao = new OaApplyOfficeObject();
	public static final String tableName = "oa_apply_office_object";
	
	/***
	 * 根据主键查询
	 */
	public OaApplyOfficeObject getById(String id){
		return OaApplyOfficeObject.dao.findById(id);
	}
	
	
	/***
	 * 删除
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		OaApplyOfficeObject o = OaApplyOfficeObject.dao.getById(id);
    		o.delete();
    	}
	}
	
}