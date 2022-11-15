package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseOaApplyBorrowMoney;
@SuppressWarnings("serial")
public class OaApplyBorrowMoney extends BaseOaApplyBorrowMoney<OaApplyBorrowMoney> {
	public static final OaApplyBorrowMoney dao = new OaApplyBorrowMoney();
	public static final String tableName = "oa_apply_borrow_money";
	
	/***
	 * query by id
	 */
	public OaApplyBorrowMoney getById(String id){
		return OaApplyBorrowMoney.dao.findById(id);
	}
	
	/***
	 * del
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		OaApplyBorrowMoney o = OaApplyBorrowMoney.dao.getById(id);
    		o.delete();
    	}
	}
	
}