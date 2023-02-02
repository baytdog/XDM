package com.pointlion.mvc.admin.xdm.xdsettleovertime;

import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.SysUser;
import com.pointlion.mvc.common.model.XdSettleOvertime;
import com.pointlion.plugin.shiro.ShiroKit;
import com.pointlion.mvc.common.model.SysRoleOrg;
import com.pointlion.mvc.common.utils.DateUtil;

public class XdSettleOvertimeService{
	public static final XdSettleOvertimeService me = new XdSettleOvertimeService();
	public static final String TABLE_NAME = XdSettleOvertime.tableName;
	
	/***
	 * query by id
	 */
	public XdSettleOvertime getById(String id){
		return XdSettleOvertime.dao.findById(id);
	}
	
	/***
	 * get page
	 */
	public Page<Record> getPage(int pnum,int psize,String dept,String year,String month){
		String userId = ShiroKit.getUserId();
		String sql  = " from "+TABLE_NAME+" o where 1=1";
		SysUser user = SysUser.dao.findById(userId);
		String unitValue = (user.getUnitValue()==null?"":user.getUnitValue());
		if(!"22".equals(unitValue)){
			sql=sql+" and o.dept_value='"+ShiroKit.getUserOrgId()+"'";
		}
		if(StrKit.notBlank(dept)){
			sql = sql + " and o.dept_value='"+ dept+"'";
		}
		if(StrKit.notBlank(year)){
			sql = sql + " and o.overtime_year='"+year+"'";
		}
		if(StrKit.notBlank(month)){
			sql = sql + " and o.overtime_month='"+month+"'";
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
    		XdSettleOvertime o = me.getById(id);
    		o.delete();
    	}
	}



	public Page<Record> getHomePageData(int pnum,int psize){
		String userId = ShiroKit.getUserId();
		SysUser sysUser = SysUser.dao.findById(userId);
		String overtimeSql="";
		if(sysUser.getUnitValue()!=null && sysUser.getUnitValue().equals("22")){
			overtimeSql = "  from   xd_steps s left join  xd_settle_overtime x on s.oid=x.id" +
					" where  s.orgid ='"+ShiroKit.getUserOrgId()+"' and  s.finished='N'";
		}else{
			overtimeSql = "  from   xd_steps s left join  xd_settle_overtime x on s.oid=x.id" +
					" where  s.userid ='"+ShiroKit.getUserId()+"' and  s.finished='N'";

		}
		return Db.paginate(pnum, psize, " select s.*,x.dept_name,x.overtime_year_month,x.status ", overtimeSql);
	}
}