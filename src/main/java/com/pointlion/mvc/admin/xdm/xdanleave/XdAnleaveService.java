package com.pointlion.mvc.admin.xdm.xdanleave;

import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.admin.xdm.xdanleavesummary.XdAnleaveSummaryController;
import com.pointlion.mvc.common.model.XdAnleave;
import com.pointlion.mvc.common.model.XdAnleaveSummary;
import com.pointlion.plugin.shiro.ShiroKit;
import com.pointlion.mvc.common.model.SysRoleOrg;
import com.pointlion.mvc.common.utils.DateUtil;

import java.util.List;

public class XdAnleaveService{
	public static final XdAnleaveService me = new XdAnleaveService();
	public static final String TABLE_NAME = XdAnleave.tableName;
	
	/***
	 * query by id
	 */
	public XdAnleave getById(String id){
		return XdAnleave.dao.findById(id);
	}
	
	/***
	 * get page
	 */
	public Page<Record> getPage(int pnum,int psize,String name,String dept){
		String sql  = " from "+TABLE_NAME+" o   where 1=1";
		if(StrKit.notBlank(dept)){
			sql = sql + " and o.dept_id='"+ dept+"'";
		}
		if(StrKit.notBlank(name)){
			sql = sql + " and o.emp_name like '%"+name+"%'";
		}
		sql = sql + " order by o.dept_id,o.emp_name ";
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
    		XdAnleave o = me.getById(id);
    		o.delete();
			String empId = o.getEmpId();
			List<XdAnleaveSummary> xdAnleaveSummaries = XdAnleaveSummary.dao.find("select * from  xd_anleave_summary where emp_id='" + empId + "';");
			for (XdAnleaveSummary xdAnleaveSummary : xdAnleaveSummaries) {
				xdAnleaveSummary.delete();
			}
		}
	}
	
}