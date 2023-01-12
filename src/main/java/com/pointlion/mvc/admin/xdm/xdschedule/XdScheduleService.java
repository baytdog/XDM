package com.pointlion.mvc.admin.xdm.xdschedule;

import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.SysUser;
import com.pointlion.mvc.common.model.XdSchedule;
import com.pointlion.plugin.shiro.ShiroKit;
import com.pointlion.mvc.common.model.SysRoleOrg;
import com.pointlion.mvc.common.utils.DateUtil;

public class XdScheduleService{
	public static final XdScheduleService me = new XdScheduleService();
	public static final String TABLE_NAME = XdSchedule.tableName;
	
	/***
	 * query by id
	 */
	public XdSchedule getById(String id){
		return XdSchedule.dao.findById(id);
	}
	
	/***
	 * get page
	 */
	public Page<Record> getPage(int pnum,int psize,String startTime,String endTime,String applyUser){
		String userId = ShiroKit.getUserId();
		String sql  = " from "+TABLE_NAME+" o  where 1=1";
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
		sql = sql + " order by o.create_date desc";
		return Db.paginate(pnum, psize, " select * ", sql);
	}

	/**
	 * @Method getHomePageData
	 * @param pnum:
 	 * @param psize:
	 * @Date 2023/1/12 10:59
	 * @Description  获取首页列表数据
	 * @Author king
	 * @Version  1.0
	 * @Return com.jfinal.plugin.activerecord.Page<com.jfinal.plugin.activerecord.Record>
	 */
	public Page<Record> getHomePageData(int pnum,int psize){
		String userId = ShiroKit.getUserId();
		//String sql  = " from "+TABLE_NAME+" o  where 1=1";
		SysUser sysUser = SysUser.dao.findById(userId);
		String scheduleSql="";
		if(sysUser.getUnitValue()!=null && sysUser.getUnitValue().equals("22")){
			scheduleSql = "  from   xd_steps s left join  xd_schedule x on s.oid=x.id" +
					" where  s.orgid ='"+ShiroKit.getUserOrgId()+"' and  s.finished='N'";
		}else{
			scheduleSql = "  from   xd_steps s left join  xd_schedule x on s.oid=x.id" +
					" where  s.userid ='"+ShiroKit.getUserId()+"' and  s.finished='N'";

		}
		System.out.println(scheduleSql);
		//sql = sql + SysRoleOrg.dao.getRoleOrgSql(userId) ;
		/*if(StrKit.notBlank(startTime)){
			sql = sql + " and o.create_time>='"+ DateUtil.formatSearchTime(startTime,"0")+"'";
		}
		if(StrKit.notBlank(endTime)){
			sql = sql + " and o.create_time<='"+DateUtil.formatSearchTime(endTime,"1")+"'";
		}
		if(StrKit.notBlank(applyUser)){
			sql = sql + " and o.applyer_name like '%"+applyUser+"%'";
		}*/
		//sql = sql + " order by o.create_date desc";
		return Db.paginate(pnum, psize, " select s.*,x.dept_name,x.shedule_year_month,x.status ", scheduleSql);
	}
	
	/***
	 * del
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		XdSchedule o = me.getById(id);
    		o.delete();
    	}
	}
	
}