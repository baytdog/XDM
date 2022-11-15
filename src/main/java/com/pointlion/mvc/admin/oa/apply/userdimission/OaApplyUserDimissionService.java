package com.pointlion.mvc.admin.oa.apply.userdimission;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.OaApplyUserDimission;

public class OaApplyUserDimissionService{
	public static final OaApplyUserDimissionService me = new OaApplyUserDimissionService();
	private static final String TABLE_NAME = OaApplyUserDimission.tableName;
	
	/***
	 * 根据主键查询
	 */
	public OaApplyUserDimission getById(String id){
		return OaApplyUserDimission.dao.findById(id);
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
    		OaApplyUserDimission o = me.getById(id);
    		o.delete();
    	}
	}
	
}