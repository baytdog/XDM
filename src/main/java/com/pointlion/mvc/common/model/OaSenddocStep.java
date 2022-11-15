package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseOaSenddocStep;
@SuppressWarnings("serial")
public class OaSenddocStep extends BaseOaSenddocStep<OaSenddocStep> {
	public static final OaSenddocStep dao = new OaSenddocStep();
	public static final String tableName = "oa_senddoc_step";
	
	/***
	 * query by id
	 */
	public OaSenddocStep getById(String id){
		return OaSenddocStep.dao.findById(id);
	}
	
	/***
	 * del
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		OaSenddocStep o = OaSenddocStep.dao.getById(id);
    		o.delete();
    	}
	}
	
}