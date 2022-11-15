package com.pointlion.mvc.admin.oa.senddoc;

import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.OaSenddoc;
import com.pointlion.mvc.common.utils.Constants;
import com.pointlion.mvc.common.utils.DateUtil;
import com.pointlion.mvc.common.utils.StepUtil;
import com.pointlion.plugin.shiro.ShiroKit;

public class OaSenddocService{
	public static final OaSenddocService me = new OaSenddocService();
	public static final String TABLE_NAME = OaSenddoc.tableName;
	
	/***
	 * query by id
	 */
	public OaSenddoc getById(String id){
		return OaSenddoc.dao.findById(id);
	}
	
	/***
	 * get page
	 */
	public Page<Record> getPage(int pnum,int psize,String startTime,String endTime,String applyUser){
		String userId = ShiroKit.getUserId();
		String sql  = " from "+TABLE_NAME+" o   where 1=1";
		
		if(Constants.ADMIN_USER.indexOf(ShiroKit.getUsername())!=-1) {
			
		}else {
			sql=sql+" and o.cuserid='"+userId+"'";
		}
		
		//String sql  = " from "+TABLE_NAME+" o   where 1=1 and o.cuserid='"+userId+"'";
		//sql = sql + SysRoleOrg.dao.getRoleOrgSql(userId) ;
		if(StrKit.notBlank(startTime)){
			sql = sql + " and o.create_time>='"+ DateUtil.formatSearchTime(startTime,"0")+"'";
		}
		if(StrKit.notBlank(endTime)){
			sql = sql + " and o.create_time<='"+DateUtil.formatSearchTime(endTime,"1")+"'";
		}
		if(StrKit.notBlank(applyUser)){
			sql = sql + " and o.applyer_name like '%"+applyUser+"%'";
		}
		sql = sql + " order by o.ctime desc";
		return Db.paginate(pnum, psize, " select * ", sql);
	}
	public Page<Record> getPage(int pnum,int psize,String startTime,String endTime,String stype,String snum,String stitle,String sstate){
		String userId = ShiroKit.getUserId();
		String sql  = " from "+TABLE_NAME+" o   where 1=1";
		
		if(Constants.ADMIN_USER.indexOf(ShiroKit.getUsername())!=-1 ||Constants.RECEIVEDOCSEARCH.indexOf(ShiroKit.getUsername())!=-1) {
			if(ShiroKit.getUsername().equals("shenshuqiong")) {
				
				sql=sql+" and o.status='13'";
			}
			
			
			
		}else {
			sql=sql+" and o.cuserid='"+userId+"'";
		}
		
		//String sql  = " from "+TABLE_NAME+" o   where 1=1 and o.cuserid='"+userId+"'";
		//sql = sql + SysRoleOrg.dao.getRoleOrgSql(userId) ;
		if(StrKit.notBlank(startTime)){
			sql = sql + " and o.ctime>='"+ DateUtil.formatSearchTime(startTime,"0")+"'";
		}
		if(StrKit.notBlank(endTime)){
			sql = sql + " and o.ctime<='"+DateUtil.formatSearchTime(endTime,"1")+"'";
		}
		if(StrKit.notBlank(stype)){
			sql = sql + " and o.dtype ='"+stype+"'";
		}
		if(StrKit.notBlank(snum)){
			sql = sql + " and o.dnum like '%"+snum+"%'";
		}
		if(StrKit.notBlank(stitle)){
			sql = sql + " and o.dtitle like '%"+stitle+"%'";
		}
		if(StrKit.notBlank(sstate)){
			sql = sql + " and o.status = '"+sstate+"'";
		}
		sql = sql + " order by o.ctime desc";
		return Db.paginate(pnum, psize, " select * ", sql);
	}
	public Page<Record> getPage(int pnum,int psize){
		String userId = ShiroKit.getUserId();
		String sql  = " from "+TABLE_NAME+" o   where 1=1 and o.cuserid !='"+userId+"' "
				+ "and (o.dofficesure='"+ShiroKit.getUserOrgId()+"' or o.dhgksids='"+ShiroKit.getUserOrgId()+"')";
		 
		sql = sql + " order by o.ctime desc";
		return Db.paginate(pnum, psize, " select * ", sql);
	}
	
	
	
	public Page<Record> getPersonalPage(int pnum,int psize){
		String userId = ShiroKit.getUserId();
		String sql  = " from "+TABLE_NAME+" o  left join oa_steps s on o.id=s.oid where 1=1 and s.userid ='"+userId+"' "
				+ "and  s.ifcomplete='0'";
		
		sql = sql + " order by o.ctime desc";
		return Db.paginate(pnum, psize, " select o.*,s.id as sid, s.step as sstep ", sql);
	}
	
	/***
	 * del
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		OaSenddoc o = me.getById(id);
    		o.delete();
    		
    		StepUtil.deleteSteps(id);
    	}
	}
	
}