package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseOaOutm;
import com.pointlion.plugin.shiro.ShiroKit;
@SuppressWarnings("serial")
public class OaOutm extends BaseOaOutm<OaOutm> {
	public static final OaOutm dao = new OaOutm();
	public static final String TABLE_NAME = "oa_outm";
	
	/***
	 * query by id
	 */
	public OaOutm getById(String id){
		return OaOutm.dao.findById(id);
	}
	
	/***
	 * del
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		OaOutm o = OaOutm.dao.getById(id);
    		o.delete();
    	}
	}
 
	
	
	
	
	/***
	 * 获取分页
	 */
	public Page<OaOutm> getPage(int pnum,int psize,String type){
		String sql = " from "+TABLE_NAME+" o LEFT JOIN act_hi_procinst p ON o.proc_ins_id=p.ID_ WHERE 1=1 ";
		String userId = ShiroKit.getUserId();
		//数据权限
		sql = sql + SysRoleOrg.dao.getRoleOrgSql(userId) ;
		if(StrKit.notBlank(type)){
			sql = sql + " AND o.type='"+type+"' ";
		}
		sql = sql + " ORDER BY create_time DESC";
		return OaOutm.dao.paginate(pnum, psize, "select o.*,p.PROC_DEF_ID_ defid", sql);
	}
}