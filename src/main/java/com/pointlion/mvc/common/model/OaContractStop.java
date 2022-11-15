package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseOaContractStop;
@SuppressWarnings("serial")
public class OaContractStop extends BaseOaContractStop<OaContractStop> {
	public static final OaContractStop dao = new OaContractStop();
	public static final String tableName = "oa_contract_stop";
	
	/***
	 * 根据主键查询
	 */
	public OaContractStop getById(String id){
		return OaContractStop.dao.findById(id);
	}
	
	/***
	 * 获取分页
	 */
	public Page<Record> getPage(int pnum,int psize){
		String sql  = " from "+tableName+" o ";
		return Db.paginate(pnum, psize, " select * ", sql);
	}
	
	/***
	 * 删除
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		OaContractStop o = OaContractStop.dao.getById(id);
    		o.delete();
    	}
	}
	
}