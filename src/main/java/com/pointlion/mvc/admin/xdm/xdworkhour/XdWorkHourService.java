package com.pointlion.mvc.admin.xdm.xdworkhour;

import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.XdWorkHour;
import com.pointlion.plugin.shiro.ShiroKit;
import com.pointlion.mvc.common.model.SysRoleOrg;
import com.pointlion.mvc.common.utils.DateUtil;

public class XdWorkHourService{
	public static final XdWorkHourService me = new XdWorkHourService();
	public static final String TABLE_NAME = XdWorkHour.tableName;
	
	/***
	 * query by id
	 */
	public XdWorkHour getById(String id){
		return XdWorkHour.dao.findById(id);
	}
	
	/***
	 * get page
	 */
	public Page<Record> getPage(int pnum,int psize,String year,String month){
		String userId = ShiroKit.getUserId();
		String sql  = " from "+TABLE_NAME+" o  where 1=1";
		if(StrKit.notBlank(year)){
			sql = sql + " and o.year='"+ year+"'";
		}
		if(StrKit.notBlank(month)){
			sql = sql + " and o.month='"+month+"'";
		}
		sql = sql + " order by o.year ,month";
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
    		XdWorkHour o = me.getById(id);
    		o.delete();
    	}
	}
	
}