package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseOaApplyMeetroom;
@SuppressWarnings("serial")
public class OaApplyMeetroom extends BaseOaApplyMeetroom<OaApplyMeetroom> {
	public static final OaApplyMeetroom dao = new OaApplyMeetroom();
	public static final String tableName = "oa_apply_meetroom";
	
	/***
	 * 根据主键查询
	 */
	public OaApplyMeetroom getById(String id){
		return OaApplyMeetroom.dao.findById(id);
	}
	
	/***
	 * 删除
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		OaApplyMeetroom o = OaApplyMeetroom.dao.getById(id);
    		o.delete();
    	}
	}
	
}