package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseOaApplyBankAccount;
@SuppressWarnings("serial")
public class OaApplyBankAccount extends BaseOaApplyBankAccount<OaApplyBankAccount> {
	public static final OaApplyBankAccount dao = new OaApplyBankAccount();
	public static final String tableName = "oa_apply_bank_account";
	
	/***
	 * 根据主键查询
	 */
	public OaApplyBankAccount getById(String id){
		return OaApplyBankAccount.dao.findById(id);
	}
	
	/***
	 * 删除
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		OaApplyBankAccount o = OaApplyBankAccount.dao.getById(id);
    		o.delete();
    	}
	}
	
}