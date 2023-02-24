package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseXdRewardPunishmentSummary;
@SuppressWarnings("serial")
public class XdRewardPunishmentSummary extends BaseXdRewardPunishmentSummary<XdRewardPunishmentSummary> {
	public static final XdRewardPunishmentSummary dao = new XdRewardPunishmentSummary();
	public static final String tableName = "xd_reward_punishment_summary";
	
	/***
	 * query by id
	 */
	public XdRewardPunishmentSummary getById(String id){
		return XdRewardPunishmentSummary.dao.findById(id);
	}
	
	/***
	 * del
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		XdRewardPunishmentSummary o = XdRewardPunishmentSummary.dao.getById(id);
    		o.delete();
    	}
	}
	
}