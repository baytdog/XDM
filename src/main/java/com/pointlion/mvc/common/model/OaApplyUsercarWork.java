package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseOaApplyUsercarWork;
@SuppressWarnings("serial")
public class OaApplyUsercarWork extends BaseOaApplyUsercarWork<OaApplyUsercarWork> {
	public static final OaApplyUsercarWork dao = new OaApplyUsercarWork();
	public static final String tableName = "oa_apply_usercar_work";
	
	/***
	 * 根据主键查询
	 */
	public OaApplyUsercarWork getById(String id){
		return OaApplyUsercarWork.dao.findById(id);
	}
	
	
	/***
	 * 删除
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		OaApplyUsercarWork o = OaApplyUsercarWork.dao.getById(id);
    		o.delete();
    	}
	}
	
}