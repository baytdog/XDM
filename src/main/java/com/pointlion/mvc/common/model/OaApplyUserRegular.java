package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseOaApplyUserRegular;
@SuppressWarnings("serial")
public class OaApplyUserRegular extends BaseOaApplyUserRegular<OaApplyUserRegular> {
	public static final OaApplyUserRegular dao = new OaApplyUserRegular();
	public static final String tableName = "oa_apply_user_regular";
	/***
	 * 根据主键查询
	 */
	public OaApplyUserRegular getById(String id){
		return OaApplyUserRegular.dao.findById(id);
	}
	
	/***
	 * 删除
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		OaApplyUserRegular o = OaApplyUserRegular.dao.getById(id);
    		o.delete();
    	}
	}
	
}