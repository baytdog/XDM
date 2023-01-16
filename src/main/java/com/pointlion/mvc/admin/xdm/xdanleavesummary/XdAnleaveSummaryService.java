package com.pointlion.mvc.admin.xdm.xdanleavesummary;

import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.XdAnleaveSummary;
import com.pointlion.plugin.shiro.ShiroKit;
import com.pointlion.mvc.common.model.SysRoleOrg;
import com.pointlion.mvc.common.utils.DateUtil;

public class XdAnleaveSummaryService{
	public static final XdAnleaveSummaryService me = new XdAnleaveSummaryService();
	public static final String TABLE_NAME = XdAnleaveSummary.tableName;
	
	/***
	 * query by id
	 */
	public XdAnleaveSummary getById(String id){
		return XdAnleaveSummary.dao.findById(id);
	}
	
	/***
	 * get page
	 */
	public Page<Record> getPage(int pnum,int psize,String name,String dept,String year){
		String sql  = " from "+TABLE_NAME+" o    where 1=1";
		if(StrKit.notBlank(name)){
			sql = sql + " and o.emp_name like '%"+ name+"%'";
		}
		if(StrKit.notBlank(dept)){
			sql = sql + " and o.dept_id='"+dept+"'";
		}
		if(StrKit.notBlank(year)){
			sql = sql + " and o.year ='"+year+"'";
		}
		sql = sql + " order by o.emp_name,o.year desc ";
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
    		XdAnleaveSummary o = me.getById(id);
    		o.delete();
    	}
	}
	
}