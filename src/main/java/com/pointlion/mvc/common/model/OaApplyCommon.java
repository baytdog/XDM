package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseOaApplyCommon;
@SuppressWarnings("serial")
public class OaApplyCommon extends BaseOaApplyCommon<OaApplyCommon> {
	public static final OaApplyCommon dao = new OaApplyCommon();
	public static final String tableName = "oa_apply_common";
	
	/***
	 * 根据主键查询
	 */
	public OaApplyCommon getById(String id){
		return OaApplyCommon.dao.findById(id);
	}
	
	/***
	 * 删除
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		OaApplyCommon o = OaApplyCommon.dao.getById(id);
    		o.delete();
    	}
	}
	
}