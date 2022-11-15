package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseOaApplyUserChangeStation;
@SuppressWarnings("serial")
public class OaApplyUserChangeStation extends BaseOaApplyUserChangeStation<OaApplyUserChangeStation> {
	public static final OaApplyUserChangeStation dao = new OaApplyUserChangeStation();
	public static final String tableName = "oa_apply_user_change_station";
	/***
	 * 根据主键查询
	 */
	public OaApplyUserChangeStation getById(String id){
		return OaApplyUserChangeStation.dao.findById(id);
	}
	
	/***
	 * 删除
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		OaApplyUserChangeStation o = OaApplyUserChangeStation.dao.getById(id);
    		o.delete();
    	}
	}
	
}