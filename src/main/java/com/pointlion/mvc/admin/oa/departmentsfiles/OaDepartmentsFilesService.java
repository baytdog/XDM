package com.pointlion.mvc.admin.oa.departmentsfiles;

import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.OaDepartmentsFiles;
import com.pointlion.mvc.common.model.SysAttachment;
import com.pointlion.plugin.shiro.ShiroKit;
import com.pointlion.mvc.common.model.SysRoleOrg;
import com.pointlion.mvc.common.utils.Constants;
import com.pointlion.mvc.common.utils.DateUtil;

public class OaDepartmentsFilesService{
	public static final OaDepartmentsFilesService me = new OaDepartmentsFilesService();
	public static final String TABLE_NAME = OaDepartmentsFiles.tableName;
	public static final String TABLE_NAME1 = SysAttachment.tableName;
	
	/***
	 * query by id
	 */
	public OaDepartmentsFiles getById(String id){
		return OaDepartmentsFiles.dao.findById(id);
	}
	
	/***
	 * get page
	 */
	public Page<Record> getPage(int pnum,int psize,String startTime,String endTime,String applyUser){
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
	public Page<Record> getFilePageList(int pnum,int psize,String startTime,String endTime,String fileName){
		
		
		String userId = ShiroKit.getUserId();

		String sql  = " from "+TABLE_NAME1+" o   where create_user_id='"+userId+"'  and  moduel_from='"+ShiroKit.getUserOrgId()+"'";
	/*	if(Constants.DEPARTHOME.indexOf(username)!=-1) {
			
			sql  = " from "+TABLE_NAME1+" o   where   business_id in (select  id from oa_departments_files)";
		}
		*/
		
		
		//String sql  = " from "+TABLE_NAME1+" o   where create_user_id='"+userId+"'  and  moduel_from='"+ShiroKit.getUserOrgId()+"'";
		if(StrKit.notBlank(startTime)){
			sql = sql + " and o.create_time>='"+ DateUtil.formatSearchTime(startTime,"0")+"'";
		}
		if(StrKit.notBlank(endTime)){
			sql = sql + " and o.create_time<='"+DateUtil.formatSearchTime(endTime,"1")+"'";
		}
		if(StrKit.notBlank(fileName)){
			sql = sql + " and o.file_name  like '%"+fileName+"%'";
		}
		sql = sql + " order by o.create_time desc";
		return Db.paginate(pnum, psize, " select * ", sql);
	}
	public Page<Record> getMoreFilePageList(int pnum,int psize,String startTime,String endTime,String fileName){
		String username = ShiroKit.getUsername();
		String sql  = " from "+TABLE_NAME1+" o   where  moduel_from='"+ShiroKit.getUserOrgId()+"'";
		if(Constants.DEPARTHOME.indexOf(username)!=-1) {
			
			sql  = " from "+TABLE_NAME1+" o   where   business_id in (select  id from oa_departments_files)";
		}
		
		if(StrKit.notBlank(startTime)){
			sql = sql + " and o.create_time>='"+ DateUtil.formatSearchTime(startTime,"0")+"'";
		}
		if(StrKit.notBlank(endTime)){
			sql = sql + " and o.create_time<='"+DateUtil.formatSearchTime(endTime,"1")+"'";
		}
		if(StrKit.notBlank(fileName)){
			sql = sql + " and o.file_name  like '%"+fileName+"%'";
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
    		OaDepartmentsFiles o = me.getById(id);
    		o.delete();
    	}
	}
	
}