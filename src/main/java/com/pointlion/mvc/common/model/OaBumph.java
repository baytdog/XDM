package com.pointlion.mvc.common.model;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.pointlion.mvc.common.model.base.BaseOaBumph;
import com.pointlion.mvc.common.utils.Constants;
import com.pointlion.mvc.common.utils.DateUtil;
import com.pointlion.plugin.shiro.ShiroKit;
@SuppressWarnings("serial")
public class OaBumph extends BaseOaBumph<OaBumph> {
	public static final OaBumph dao = new OaBumph();
	public static final String TABLE_NAME = "oa_bumph";
	
	
	/***
	 * 获取分页
	 * 原版备注
	 */ 
	/*public Page<OaBumph> getPage(int pnum,int psize,String type){
		String sql = " from "+TABLE_NAME+" o LEFT JOIN act_hi_procinst p ON o.proc_ins_id=p.ID_ WHERE 1=1 ";
		String userId = ShiroKit.getUserId();
		//数据权限
		sql = sql + SysRoleOrg.dao.getRoleOrgSql(userId) ;
		if(StrKit.notBlank(type)){
			sql = sql + " AND o.type='"+type+"' ";
		}
		sql = sql + " ORDER BY create_time DESC";
		return OaBumph.dao.paginate(pnum, psize, "select o.*,p.PROC_DEF_ID_ defid", sql);
	}*/
	
	public Page<OaBumph> getPage(int pnum,int psize,String type){
		String sql = " from "+TABLE_NAME+" o LEFT JOIN act_hi_procinst p ON o.proc_ins_id=p.ID_ WHERE  o.backup2='1'  and (  (1=1 ";
		String userId = ShiroKit.getUserId();
		
		
		
		if(Constants.ADMIN_USER.indexOf(ShiroKit.getUsername())!=-1) {
			
		}else {
			//数据权限
			sql = sql + SysRoleOrg.dao.getRoleOrgSql(userId) ;
			
		}
		
		
		
		
		
		
		if(StrKit.notBlank(type)){
			//sql = sql + " AND o.type='"+type+"') ";
			sql+=")";
		}else {
			sql+=")";
		}
		sql+="or o.id in(select bumphid from oa_bumph_user where  lookornot ='1' and  username='"+ShiroKit.getUsername()+"'))";
		
		 
		sql = sql + " ORDER BY create_time DESC";
		return OaBumph.dao.paginate(pnum, psize, "select o.*,p.PROC_DEF_ID_ defid", sql);
	}
	public Page<OaBumph> getPage(int pnum,int psize,String type,String rdnum,String rtitle,String rFileNo,String rstate,String startTime,String endTime){
		//String sql = " from "+TABLE_NAME+" o   WHERE  o.backup2='1'   ";
		String sql = " from "+TABLE_NAME+" o   WHERE 1=1  ";
		String userId = ShiroKit.getUserId();
		
		
		
		if(Constants.ADMIN_USER.indexOf(ShiroKit.getUsername())!=-1) {
			
		}else {
			//数据权限
			sql = sql + SysRoleOrg.dao.getRoleOrgSql(userId) ;
			
		}
		if(StrKit.notBlank(startTime)){
			sql = sql + " and o.create_time>='"+ DateUtil.formatSearchTime(startTime,"0")+"'";
		}
		if(StrKit.notBlank(endTime)){
			sql = sql + " and o.create_time<='"+DateUtil.formatSearchTime(endTime,"1")+"'";
		}
		if(StrKit.notBlank(rdnum)){
			sql = sql + " and o.doc_num like '%"+rdnum+"%'";
		}
		if(StrKit.notBlank(rtitle)) {
			
			sql = sql + " and o.title like '%"+rtitle+"%'";
		}
		if(StrKit.notBlank(rFileNo)) {
			
			sql = sql + " and o.file_no like '%"+rFileNo+"%'";
		}
		if(StrKit.notBlank(rstate)) {
			
			sql = sql + " and o.status ='"+rstate+"'";
		}
	 
		
		
		sql = sql + " ORDER BY create_time DESC";
		return OaBumph.dao.paginate(pnum, psize, "select o.*", sql);
	}
	public Page<OaBumph> getHistoryPage(int pnum,int psize,String type,String rdnum,String rtitle,String rFileNo,String startTime,String endTime){
		String userId = ShiroKit.getUserId();
		
		String sql =" from  oa_steps  s left join  oa_bumph  o on  s.oid=o.id  where  o.backup2='1'   and  s.userid='"+userId+"' and  s.ifcomplete='1' and s.type='1'";
		
		
		
		
		
		
		
		
		
		
		
		
		//String sql = " from "+TABLE_NAME+" o   WHERE  o.backup2='1'   and id in (select  oid from  oa_step_history   where actorid='"+userId+"'  and type='1')   ";
 
		if(StrKit.notBlank(startTime)){
			sql = sql + " and o.create_time>='"+ DateUtil.formatSearchTime(startTime,"0")+"'";
		}
		if(StrKit.notBlank(endTime)){
			sql = sql + " and o.create_time<='"+DateUtil.formatSearchTime(endTime,"1")+"'";
		}
		if(StrKit.notBlank(rdnum)){
			sql = sql + " and o.doc_num like '%"+rdnum+"%'";
		}
		if(StrKit.notBlank(rtitle)) {
			
			sql = sql + " and o.title like '%"+rtitle+"%'";
		}
		if(StrKit.notBlank(rFileNo)) {
			
			sql = sql + " and o.file_no like '%"+rFileNo+"%'";
		}
		 
		
		
		
		sql = sql + " ORDER BY create_time DESC";
		return OaBumph.dao.paginate(pnum, psize, "select o.*,s.id as sid, s.step as sstep", sql);
	}
	public Page<OaBumph> getSelectPage(int pnum,int psize){
		String sql = " from "+TABLE_NAME+" o   WHERE  o.backup2='1'  and id in  (select oid from  oa_step_history where actorid='"+ShiroKit.getUserId()+"')";
	 
		
		
		sql = sql + " ORDER BY o.create_time DESC";
		return OaBumph.dao.paginate(pnum, psize, "select o.*", sql);
	}
	
	
	
	/***
	 * 获取分页
	 */
	public Page<OaBumph> getPage1(int pnum,int psize,String type){
		String sql = " from "+TABLE_NAME+" o  WHERE o.backup2='1' and 1=1 ";
		//String userId = ShiroKit.getUserId();
		//数据权限
		//sql = sql + SysRoleOrg.dao.getRoleOrgSql(userId) ;
		 
	    sql = sql + " AND o.id in(select bumphid from oa_bumph_user where  lookornot ='1' and  username='"+ShiroKit.getUsername()+"')";
		 
		sql = sql + " ORDER BY create_time DESC";
		
		 
		return OaBumph.dao.paginate(pnum, psize, "select o.*", sql);
	}
	/***
	 * 获取分页
	 */
	public Page<OaBumph> getPageData(int pnum,int psize){
		String sql = " from "+TABLE_NAME+" o  left join  oa_bumph_user bu on o.id=bu.bumphid  WHERE  o.if_complete<>'2' 1=1 ";
		//String userId = ShiroKit.getUserId();
		//数据权限
		//sql = sql + SysRoleOrg.dao.getRoleOrgSql(userId) ;
		
		sql = sql + " AND bu.userid='"+ShiroKit.getUserId()+"'";
		
		sql = sql + " ORDER BY o.create_time DESC ,bu.ifcomplete ";
		
		
		return OaBumph.dao.paginate(pnum, psize, "select o.*,bu.ifcomplete,bu.userposition,bu.step,bu.id as uid,bu.leaderremark as remark", sql);
	}
	/**
	 * 
	* @Title: getPReceivePageData 
	* @Description: 收文个人数据查询 
	* @param pnum
	* @param psize
	* @return Page<OaBumph>
	* @date 2020年11月7日下午5:14:46
	 */
	
	public Page<OaBumph> getPReceivePageData(int pnum,int psize){
		String sql = " from "+TABLE_NAME+" o  left join  oa_steps s on o.id=s.oid  WHERE  s.ifcomplete='0'  and  buttontype='1'";
		sql = sql + " AND s.userid='"+ShiroKit.getUserId()+"'";
		
		sql = sql + " ORDER BY s.buttontype desc ";
		
		
		return OaBumph.dao.paginate(pnum, psize, "select o.*,s.ifcomplete,s.step as sstep,s.id as sid,s.buttontype", sql);
	}
	
	/**
	 * 
	* @Title: getPReceivePageWaitData 
	* @Description: 待办收文查询 
	* @param pnum
	* @param psize
	* @return Page<OaBumph>
	* @date 2021年1月24日上午8:43:24
	 */
	public Page<OaBumph> getPReceivePageWaitData(int pnum,int psize){
		String sql = " from "+TABLE_NAME+" o  left join  oa_steps s on o.id=s.oid  WHERE  s.ifcomplete='0' ";
		sql = sql + " AND s.userid='"+ShiroKit.getUserId()+"'";
		
		sql = sql + " ORDER BY s.buttontype desc ";
		
		
		return OaBumph.dao.paginate(pnum, psize, "select o.*,s.ifcomplete,s.step as sstep,s.id as sid,s.buttontype", sql);
	}
	
	public Page<OaBumph> getPReceivesPageData(int pnum,int psize){
	/*	String sql = " from "+TABLE_NAME+" o  left join  ( select * from ( select * from oa_steps    order by ctime desc LIMIT 10000) bb  group by oid, userid) s  on o.id=s.oid  WHERE o.status<>'5'";
		sql = sql + " AND s.userid='"+ShiroKit.getUserId()+"'";
		
		sql = sql + " ORDER BY   s.ifcomplete,s.buttontype desc ";
		
		
		return OaBumph.dao.paginate(pnum, psize, "select o.*,s.ifcomplete,s.step as sstep,s.id as sid,s.buttontype", sql);*/
		
		String sql = " from "+TABLE_NAME+" o  left join  oa_steps s on o.id=s.oid  WHERE  s.ifcomplete='0' ";
		sql = sql + " AND s.userid='"+ShiroKit.getUserId()+"'";
		
		sql = sql + " ORDER BY s.buttontype desc ";
		
		
		return OaBumph.dao.paginate(pnum, psize, "select o.*,s.ifcomplete,s.step as sstep,s.id as sid,s.buttontype", sql);
	}
	
	
	
	/**
	 * 
	* @Title: getPReceiveOverPageData 
	* @Description: 收文已办查询 
	* @param pnum
	* @param psize
	* @return Page<OaBumph>
	* @date 2021年1月18日上午11:20:43
	 */
	public Page<OaBumph> getPReceiveOverPageData(int pnum,int psize,String startTime,String endTime,String rdnum,String rtitle,String rFileNo){
		String sql = " from "+TABLE_NAME+" o  left join  oa_step_history s on o.id=s.oid  WHERE  s.type='1' ";
		sql = sql + " AND s.actorid='"+ShiroKit.getUserId()+"'";
		
		
		if(StrKit.notBlank(startTime)){
			sql = sql + " and o.create_time>='"+ DateUtil.formatSearchTime(startTime,"0")+"'";
		}
		if(StrKit.notBlank(endTime)){
			sql = sql + " and o.create_time<='"+DateUtil.formatSearchTime(endTime,"1")+"'";
		}
		if(StrKit.notBlank(rdnum)){
			sql = sql + " and o.doc_num like '%"+rdnum+"%'";
		}
		if(StrKit.notBlank(rtitle)) {
			
			sql = sql + " and o.title like '%"+rtitle+"%'";
		}
		if(StrKit.notBlank(rFileNo)) {
			
			sql = sql + " and o.file_no like '%"+rFileNo+"%'";
		}
		
		
		sql = sql + " ORDER BY s.acttime desc ";
		
		
		return OaBumph.dao.paginate(pnum, psize, "select o.*,s.id as sid,(select count(1) from oa_nodes where cuserid='"+ShiroKit.getUserId()+"' and oid =o.id and  backup1='2') as cnum ", sql);
	}
	
	
	
	/***
	 * 获取分页
	 */
	public Page<OaBumph> getPage(int pnum,int psize){
		String sql = " from "+TABLE_NAME+" o  WHERE o.backup2='2'  and  o.status='5'";
		//String userId = ShiroKit.getUserId();
		//数据权限
		//sql = sql + SysRoleOrg.dao.getRoleOrgSql(userId) ;
		
	//	sql = sql + " AND o.id in(select bumphid from oa_bumph_user where  lookornot ='1' and  username='"+ShiroKit.getUsername()+"')";
		
		sql = sql + " ORDER BY create_time DESC";
		
		
		return OaBumph.dao.paginate(pnum, psize, "select o.*", sql);
	}
}