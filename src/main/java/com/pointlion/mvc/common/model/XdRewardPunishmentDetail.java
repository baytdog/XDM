package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseXdRewardPunishmentDetail;
@SuppressWarnings("serial")
public class XdRewardPunishmentDetail extends BaseXdRewardPunishmentDetail<XdRewardPunishmentDetail> {
	public static final XdRewardPunishmentDetail dao = new XdRewardPunishmentDetail();
	public static final String tableName = "xd_reward_punishment_detail";
	
	/***
	 * query by id
	 */
	public XdRewardPunishmentDetail getById(String id){
		return XdRewardPunishmentDetail.dao.findById(id);
	}
	
	/***
	 * del
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		XdRewardPunishmentDetail o = XdRewardPunishmentDetail.dao.getById(id);
    		o.delete();
    	}
	}
	
}