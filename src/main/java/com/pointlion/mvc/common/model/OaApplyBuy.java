package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseOaApplyBuy;
@SuppressWarnings("serial")
public class OaApplyBuy extends BaseOaApplyBuy<OaApplyBuy> {
	public static final OaApplyBuy dao = new OaApplyBuy();
	public static final String tableName = "oa_apply_buy";
	/***
	 * 根据主键查询
	 */
	public OaApplyBuy getById(String id){
		return OaApplyBuy.dao.findById(id);
	}
	
	/***
	 * 获取分页
	 */
	public Page<Record> getPage(int pnum,int psize){
		String sql  = " from oa_apply_buy o ";
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
    		OaApplyBuy o = OaApplyBuy.dao.getById(id);
    		o.delete();
    	}
	}
}