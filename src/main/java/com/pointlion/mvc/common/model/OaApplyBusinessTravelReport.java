package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseOaApplyBusinessTravelReport;
@SuppressWarnings("serial")
public class OaApplyBusinessTravelReport extends BaseOaApplyBusinessTravelReport<OaApplyBusinessTravelReport> {
	public static final OaApplyBusinessTravelReport dao = new OaApplyBusinessTravelReport();
	public static final String tableName = "oa_apply_business_travel_report";

	/***
	 * query by id
	 */
	public OaApplyBusinessTravelReport getById(String id){
		return OaApplyBusinessTravelReport.dao.findById(id);
	}

	/***
	 * del
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
		String idarr[] = ids.split(",");
		for(String id : idarr){
			OaApplyBusinessTravelReport o = OaApplyBusinessTravelReport.dao.getById(id);
			o.delete();
		}
	}

}