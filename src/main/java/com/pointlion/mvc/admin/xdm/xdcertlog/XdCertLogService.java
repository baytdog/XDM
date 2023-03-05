package com.pointlion.mvc.admin.xdm.xdcertlog;

import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.XdCertLog;
import com.pointlion.plugin.shiro.ShiroKit;
import com.pointlion.mvc.common.model.SysRoleOrg;
import com.pointlion.mvc.common.utils.DateUtil;

public class XdCertLogService{
	public static final XdCertLogService me = new XdCertLogService();
	public static final String TABLE_NAME = XdCertLog.tableName;
	
	/***
	 * query by id
	 */
	public XdCertLog getById(String id){
		return XdCertLog.dao.findById(id);
	}
	
	/***
	 * get page
	 */
	public Page<Record> getPage(int pNum,int pSize,String dept,String year,String month){
		String userId = ShiroKit.getUserId();
		String sql  = " from "+TABLE_NAME+" o    where 1=1";
		if(StrKit.notBlank(dept)){
			sql = sql + " and o.dept_id='"+ dept+"'";
		}
		if(StrKit.notBlank(year)){
			sql = sql + " and o.year='"+year+"'";
		}
		if(StrKit.notBlank(month)){
			sql = sql + " and o.month ='"+month+"'";
		}
		sql = sql + " order by o.create_date desc";
		return Db.paginate(pNum, pSize, " select * ", sql);
	}
	
	/***
	 * del
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		XdCertLog o = me.getById(id);
    		o.delete();
    	}
	}
	
}