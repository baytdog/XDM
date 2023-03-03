package com.pointlion.mvc.admin.xdm.xdattendancesheet;

import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.XdAttendanceSheet;
import com.pointlion.plugin.shiro.ShiroKit;
import com.pointlion.mvc.common.model.SysRoleOrg;
import com.pointlion.mvc.common.utils.DateUtil;

public class XdAttendanceSheetService{
	public static final XdAttendanceSheetService me = new XdAttendanceSheetService();
	public static final String TABLE_NAME = XdAttendanceSheet.tableName;
	
	/***
	 * query by id
	 */
	public XdAttendanceSheet getById(String id){
		return XdAttendanceSheet.dao.findById(id);
	}
	
	/***
	 * get page
	 */
	public Page<Record> getPage(int pnum,int psize,String deptId,String year,String month){
		String userId = ShiroKit.getUserId();
		String sql  = " from "+TABLE_NAME+"   where 1=1";
		if(StrKit.notBlank(deptId)){
			sql = sql + " and o.dept_id='"+ deptId+"'";
		}
		if(StrKit.notBlank(year)){
			sql = sql + " and o.year='"+year+"'";
		}
		if(StrKit.notBlank(month)){
			sql = sql + " and o.month ='"+month+"'";
		}
		sql = sql + " order by o.dept_id, unit_id";
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
    		XdAttendanceSheet o = me.getById(id);
    		o.delete();
    	}
	}
	
}