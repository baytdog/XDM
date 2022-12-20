package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseXdContractInfo;
@SuppressWarnings("serial")
public class XdContractInfo extends BaseXdContractInfo<XdContractInfo> {
	public static final XdContractInfo dao = new XdContractInfo();
	public static final String tableName = "xd_contract_info";
	
	/***
	 * query by id
	 */
	public XdContractInfo getById(String id){
		return XdContractInfo.dao.findById(id);
	}
	
	/***
	 * del
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		XdContractInfo o = XdContractInfo.dao.getById(id);
    		o.delete();
    	}
	}
	
}