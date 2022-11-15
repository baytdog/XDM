package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseOaApplyGift;
@SuppressWarnings("serial")
public class OaApplyGift extends BaseOaApplyGift<OaApplyGift> {
	public static final OaApplyGift dao = new OaApplyGift();
	public static final String tableName = "oa_apply_gift";
	
	/***
	 * 根据主键查询
	 */
	public OaApplyGift getById(String id){
		return OaApplyGift.dao.findById(id);
	}
	
	
	/***
	 * 删除
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		OaApplyGift o = OaApplyGift.dao.getById(id);
    		o.delete();
    	}
	}
	
}