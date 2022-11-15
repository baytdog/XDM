package com.pointlion.mvc.admin.oa.showinfo;

import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.OaShowinfo;
import com.pointlion.mvc.common.utils.DateUtil;
import com.pointlion.plugin.shiro.ShiroKit;

public class OaShowinfoService{
	public static final OaShowinfoService me = new OaShowinfoService();
	public static final String TABLE_NAME = OaShowinfo.tableName;
	
	/***
	 * query by id
	 */
	public OaShowinfo getById(String id){
		return OaShowinfo.dao.findById(id);
	}
	
	/***
	 * get page
	 */
	public Page<Record> getPage(int pnum,int psize,String startTime,String endTime,String applyUser,String title,String secondmenu){
		String userId = ShiroKit.getUserId();
		String  userName=ShiroKit.getUsername();
		String sql  = " from "+TABLE_NAME+" o  where 1=1 and  publisherid='"+userId+"'";
		if(userName.equals("admin")) {
			
			 sql  = " from "+TABLE_NAME+" o  where 1=1";
		}
		//sql = sql + SysRoleOrg.dao.getRoleOrgSql(userId) ;
		if(StrKit.notBlank(startTime)){
			sql = sql + " and o.publishdatetime>='"+ DateUtil.formatSearchTime(startTime,"0")+"'";
		}
		if(StrKit.notBlank(endTime)){
			sql = sql + " and o.publishdatetime<='"+DateUtil.formatSearchTime(endTime,"1")+"'";
		}
		if(StrKit.notBlank(applyUser)){
			sql = sql + " and o.publisher like '%"+applyUser+"%'";
		}
		if(StrKit.notBlank(title)) {
			
			sql = sql + " and o.infotitle like '%"+title+"%'";
		}
		if(StrKit.notBlank(secondmenu)) {
			
			sql = sql + " and o.menuid = '"+secondmenu+"'";
		}
		sql = sql + " order by o.publishdatetime desc";
		return Db.paginate(pnum, psize, " select * ", sql);
	}
	public Page<Record> getPageByMenuId(int pnum,int psize,String menuid){
		String userId = ShiroKit.getUserId();
		String sql  = " from "+TABLE_NAME+" o  where 1=1";
		//sql = sql + SysRoleOrg.dao.getRoleOrgSql(userId) ;
	/*	if(StrKit.notBlank(startTime)){
			sql = sql + " and o.publishdatetime>='"+ DateUtil.formatSearchTime(startTime,"0")+"'";
		}
		if(StrKit.notBlank(endTime)){
			sql = sql + " and o.publishdatetime<='"+DateUtil.formatSearchTime(endTime,"1")+"'";
		}
		if(StrKit.notBlank(applyUser)){
			sql = sql + " and o.publisher like '%"+applyUser+"%'";
		}
		if(StrKit.notBlank(title)) {
			
			sql = sql + " and o.infotitle like '%"+title+"%'";
		}
		if(StrKit.notBlank(secondmenu)) {
			
			sql = sql + " and o.menuid = '"+secondmenu+"'";
		}*/
		if(StrKit.notBlank(menuid)) {
			sql=sql+ " and o.menuid='"+menuid+"'";
		}
		
		sql = sql + " order by o.publishdatetime desc";
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
    		OaShowinfo o = me.getById(id);
    		o.delete();
    	}
	}
	
}