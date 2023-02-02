package com.pointlion.mvc.admin.xdm.xdattendance;

import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.SysUser;
import com.pointlion.mvc.common.model.XdAttendance;
import com.pointlion.plugin.shiro.ShiroKit;
import com.pointlion.mvc.common.model.SysRoleOrg;
import com.pointlion.mvc.common.utils.DateUtil;

public class XdAttendanceService{
	public static final XdAttendanceService me = new XdAttendanceService();
	public static final String TABLE_NAME = XdAttendance.tableName;
	
	/***
	 * query by id
	 */
	public XdAttendance getById(String id){
		return XdAttendance.dao.findById(id);
	}
	
	/***
	 * get page
	 */
	public Page<Record> getPage(int pnum,int psize,String dept,String year,String month){
		String userId = ShiroKit.getUserId();
		SysUser user = SysUser.dao.findById(userId);
		String unitValue = (user.getUnitValue()==null?"":user.getUnitValue());
		String sql  = " from "+TABLE_NAME+" o  where 1=1";
		if(!"22".equals(unitValue)){
			sql=sql+" and o.dept_value='"+ShiroKit.getUserOrgId()+"'";
		}
		if(StrKit.notBlank(dept)){
			sql = sql + " and o.dept_value='"+ dept+"'";
		}
		if(StrKit.notBlank(year)){
			sql = sql + " and o.shedule_year='"+year+"'";
		}
		if(StrKit.notBlank(month)){
			sql = sql + " and o.shedule_month ='"+month+"'";
		}
		sql = sql + " order by o.create_date desc";
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
    		XdAttendance o = me.getById(id);
    		o.delete();
    	}
	}

	/**
	 * @Method getHomePageData
	 * @param pnum:
	 * @param psize:
	 * @Date 2023/1/14 16:53
	 * @Description  查询首页提醒列表数据
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
			scheduleSql = "  from   xd_steps s left join  xd_attendance x on s.oid=x.id" +
					" where  s.orgid ='"+ShiroKit.getUserOrgId()+"' and  s.finished='N'";
		}else{
			scheduleSql = "  from   xd_steps s left join  xd_attendance x on s.oid=x.id" +
					" where  s.userid ='"+ShiroKit.getUserId()+"' and  s.finished='N'";

		}
		System.out.println(scheduleSql);
		return Db.paginate(pnum, psize, " select s.*,x.dept_name,x.shedule_year_month,x.status ", scheduleSql);
	}
	
}