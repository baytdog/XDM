package com.pointlion.mvc.admin.oa.apply.businesscard;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.OaApplyBusinessCard;
import com.pointlion.mvc.common.model.SysRoleOrg;
import com.pointlion.plugin.shiro.ShiroKit;

public class OaApplyBusinessCardService{
	public static final OaApplyBusinessCardService me = new OaApplyBusinessCardService();
	private static final String TABLE_NAME = OaApplyBusinessCard.tableName;
	
	/***
	 * 根据主键查询
	 */
	public OaApplyBusinessCard getById(String id){
		return OaApplyBusinessCard.dao.findById(id);
	}
	
	/***
	 * 获取分页
	 */
	public Page<Record> getPage(int pnum,int psize){
		String sql  = " from "+TABLE_NAME+" o LEFT JOIN act_hi_procinst p ON o.proc_ins_id=p.ID_  where 1=1 ";
		String userId = ShiroKit.getUserId();
		//数据权限
		sql = sql + SysRoleOrg.dao.getRoleOrgSql(userId) ;
		sql = sql + " order by o.create_time desc";
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
    		OaApplyBusinessCard o = me.getById(id);
    		o.delete();
    	}
	}
	
}