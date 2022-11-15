package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseOaApplyBusinessCard;
@SuppressWarnings("serial")
public class OaApplyBusinessCard extends BaseOaApplyBusinessCard<OaApplyBusinessCard> {
	public static final OaApplyBusinessCard dao = new OaApplyBusinessCard();
	public static final String tableName = "oa_apply_business_card";
	
	/***
	 * 根据主键查询
	 */
	public OaApplyBusinessCard getById(String id){
		return OaApplyBusinessCard.dao.findById(id);
	}
	
	/***
	 * 删除
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		OaApplyBusinessCard o = OaApplyBusinessCard.dao.getById(id);
    		o.delete();
    	}
	}
	
}