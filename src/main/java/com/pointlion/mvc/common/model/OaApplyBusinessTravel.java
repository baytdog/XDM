package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseOaApplyBusinessTravel;
@SuppressWarnings("serial")
public class OaApplyBusinessTravel extends BaseOaApplyBusinessTravel<OaApplyBusinessTravel> {
	public static final OaApplyBusinessTravel dao = new OaApplyBusinessTravel();
	public static final String tableName = "oa_apply_business_travel";

	/***
	 * query by id
	 */
	public OaApplyBusinessTravel getById(String id){
		return OaApplyBusinessTravel.dao.findById(id);
	}

	/***
	 * del
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
		String idarr[] = ids.split(",");
		for(String id : idarr){
			OaApplyBusinessTravel o = OaApplyBusinessTravel.dao.getById(id);
			o.delete();
		}
	}

}