package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseOaContract;
@SuppressWarnings("serial")
public class OaContract extends BaseOaContract<OaContract> {
	public static final OaContract dao = new OaContract();
	public static final String tableName = "oa_contract";
	
	/***
	 * 根据主键查询
	 */
	public OaContract getById(String id){
		return OaContract.dao.findById(id);
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
    		OaContract o = OaContract.dao.getById(id);
    		o.delete();
    	}
	}
	
}