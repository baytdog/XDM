package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseOaApplyUserDimission;
@SuppressWarnings("serial")
public class OaApplyUserDimission extends BaseOaApplyUserDimission<OaApplyUserDimission> {
	public static final OaApplyUserDimission dao = new OaApplyUserDimission();
	public static final String tableName = "oa_apply_user_dimission";
	/***
	 * 根据主键查询
	 */
	public OaApplyUserDimission getById(String id){
		return OaApplyUserDimission.dao.findById(id);
	}
	
	/***
	 * 删除
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		OaApplyUserDimission o = OaApplyUserDimission.dao.getById(id);
    		o.delete();
    	}
	}
	
}