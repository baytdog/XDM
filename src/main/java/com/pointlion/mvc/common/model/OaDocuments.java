package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseOaDocuments;
@SuppressWarnings("serial")
public class OaDocuments extends BaseOaDocuments<OaDocuments> {
	public static final OaDocuments dao = new OaDocuments();
	public static final String tableName = "oa_documents";
	
	/***
	 * query by id
	 */
	public OaDocuments getById(String id){
		return OaDocuments.dao.findById(id);
	}
	
	/***
	 * del
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		OaDocuments o = OaDocuments.dao.getById(id);
    		o.delete();
    	}
	}
	
}