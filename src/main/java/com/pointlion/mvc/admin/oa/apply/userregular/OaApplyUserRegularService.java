package com.pointlion.mvc.admin.oa.apply.userregular;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.OaApplyUserRegular;

public class OaApplyUserRegularService{
	public static final OaApplyUserRegularService me = new OaApplyUserRegularService();
	private static final String TABLE_NAME = OaApplyUserRegular.tableName;
	
	/***
	 * 根据主键查询
	 */
	public OaApplyUserRegular getById(String id){
		return OaApplyUserRegular.dao.findById(id);
	}
	
	/***
	 * 获取分页
	 */
	public Page<Record> getPage(int pnum,int psize){
		String sql  = " from "+TABLE_NAME+" o LEFT JOIN act_hi_procinst p ON o.proc_ins_id=p.ID_ where 1=1 order by o.create_time desc";
		return Db.paginate(pnum, psize, " select o.*,p.PROC_DEF_ID_ defid ", sql);
	}
	
	/***
	 * 删除
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		OaApplyUserRegular o = me.getById(id);
    		o.delete();
    	}
	}
	
}