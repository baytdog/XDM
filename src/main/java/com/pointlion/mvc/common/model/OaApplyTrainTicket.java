package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseOaApplyTrainTicket;
@SuppressWarnings("serial")
public class OaApplyTrainTicket extends BaseOaApplyTrainTicket<OaApplyTrainTicket> {
	public static final OaApplyTrainTicket dao = new OaApplyTrainTicket();
	public static final String tableName = "oa_apply_train_ticket";
	
	/***
	 * 根据主键查询
	 */
	public OaApplyTrainTicket getById(String id){
		return OaApplyTrainTicket.dao.findById(id);
	}
	
	/***
	 * 删除
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		OaApplyTrainTicket o = OaApplyTrainTicket.dao.getById(id);
    		o.delete();
    	}
	}
	
}