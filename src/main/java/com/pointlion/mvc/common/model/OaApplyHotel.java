package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseOaApplyHotel;
@SuppressWarnings("serial")
public class OaApplyHotel extends BaseOaApplyHotel<OaApplyHotel> {
	public static final OaApplyHotel dao = new OaApplyHotel();
	public static final String tableName = "oa_apply_hotel";
	
	/***
	 * 根据主键查询
	 */
	public OaApplyHotel getById(String id){
		return OaApplyHotel.dao.findById(id);
	}
	
	/***
	 * 删除
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		OaApplyHotel o = OaApplyHotel.dao.getById(id);
    		o.delete();
    	}
	}
	
}