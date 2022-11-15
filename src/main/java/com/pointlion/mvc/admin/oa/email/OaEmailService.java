package com.pointlion.mvc.admin.oa.email;

import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.OaEmail;
import com.pointlion.plugin.shiro.ShiroKit;
import com.pointlion.mvc.common.model.SysRoleOrg;
import com.pointlion.mvc.common.utils.DateUtil;

public class OaEmailService{
	public static final OaEmailService me = new OaEmailService();
	public static final String TABLE_NAME = OaEmail.tableName;
	
	/***
	 * query by id
	 */
	public OaEmail getById(String id){
		return OaEmail.dao.findById(id);
	}
	
	/***
	 * get page
	 */
	public Page<Record> getPage(int pnum,int psize,String type,String endTime,String applyUser){
		String userId = ShiroKit.getUserId();
		//String sql  = " from "+TABLE_NAME+" o LEFT JOIN act_hi_procinst p ON o.proc_ins_id=p.ID_  where 1=1";
		String sql ="";
		if(type.equals("1")) {
			//sql  = " from  oa_email_son s left join v_email o   on s.oid =o.id  where 1=1  and s.suserid = '"+ShiroKit.getUserId()+"'";
			sql  = " from  v_email o left join   oa_email_son s on s.oid =o.id  where 1=1  and s.suserid = '"+userId+"' and s.status='1'";
		}else {
			sql  = " from v_email o where 1=1  and fuserid = '"+userId+"'   and  o.status='1'";
		}
		//sql = sql + SysRoleOrg.dao.getRoleOrgSql(userId) ;
		/*if(StrKit.notBlank(startTime)){
			sql = sql + " and o.create_time>='"+ DateUtil.formatSearchTime(startTime,"0")+"'";
		}*/
		if(StrKit.notBlank(endTime)){
			sql = sql + " and o.create_time<='"+DateUtil.formatSearchTime(endTime,"1")+"'";
		}
		if(StrKit.notBlank(applyUser)){
			sql = sql + " and o.applyer_name like '%"+applyUser+"%'";
		}
		sql = sql + " order by  o.opstatis, o.timeflage desc";
		
		
		if(type.equals("1")) {
		return Db.paginate(pnum, psize, " select o.*,s.isreaded as  isreaded"  , sql);
		}else {
			return Db.paginate(pnum, psize, " select o.*"  , sql);
			
			}
		
		
	}
	
	
	
	
	/***
	 * get page
	 */
	public Page<Record> getPageREmail(int pnum,int psize,String startTime,String endTime,String applyUser){
		String userId = ShiroKit.getUserId();
		String sql  = " from "+TABLE_NAME+" o LEFT JOIN act_hi_procinst p ON o.proc_ins_id=p.ID_  where 1=1";
		sql = sql + SysRoleOrg.dao.getRoleOrgSql(userId) ;
		if(StrKit.notBlank(startTime)){
			sql = sql + " and o.create_time>='"+ DateUtil.formatSearchTime(startTime,"0")+"'";
		}
		if(StrKit.notBlank(endTime)){
			sql = sql + " and o.create_time<='"+DateUtil.formatSearchTime(endTime,"1")+"'";
		}
		if(StrKit.notBlank(applyUser)){
			sql = sql + " and o.applyer_name like '%"+applyUser+"%'";
		}
		sql = sql + " order by o.create_time desc";
		return Db.paginate(pnum, psize, " select * ", sql);
	}
	
	/***
	 * get page
	 */
	public Page<Record> getPageSEmail(int pnum,int psize,String startTime,String endTime,String applyUser){
		String userId = ShiroKit.getUserId();
		String sql  = " from "+TABLE_NAME+" o LEFT JOIN act_hi_procinst p ON o.proc_ins_id=p.ID_  where 1=1";
		sql = sql + SysRoleOrg.dao.getRoleOrgSql(userId) ;
		if(StrKit.notBlank(startTime)){
			sql = sql + " and o.create_time>='"+ DateUtil.formatSearchTime(startTime,"0")+"'";
		}
		if(StrKit.notBlank(endTime)){
			sql = sql + " and o.create_time<='"+DateUtil.formatSearchTime(endTime,"1")+"'";
		}
		if(StrKit.notBlank(applyUser)){
			sql = sql + " and o.applyer_name like '%"+applyUser+"%'";
		}
		sql = sql + " order by o.create_time desc";
		return Db.paginate(pnum, psize, " select * ", sql);
	}
	
	/***
	 * del
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		OaEmail o = me.getById(id);
    		o.delete();
    	}
	}
	
}