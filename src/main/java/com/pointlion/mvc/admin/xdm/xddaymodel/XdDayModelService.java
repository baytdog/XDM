package com.pointlion.mvc.admin.xdm.xddaymodel;

import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.XdDayModel;
import com.pointlion.plugin.shiro.ShiroKit;
import com.pointlion.mvc.common.model.SysRoleOrg;
import com.pointlion.mvc.common.utils.DateUtil;

public class XdDayModelService{
	public static final XdDayModelService me = new XdDayModelService();
	public static final String TABLE_NAME = XdDayModel.tableName;
	
	/***
	 * query by id
	 */
	public XdDayModel getById(String id){
		return XdDayModel.dao.findById(id);
	}
	
	/***
	 * get page
	 */
	public Page<Record> getPage(int pnum,int psize,String year,String month,String days){
		String userId = ShiroKit.getUserId();
		String sql  = " from "+TABLE_NAME+" o  where 1=1";
		if(StrKit.notBlank(year)){
			sql = sql + " and o.days like '"+year+"%'";
		}
		if(StrKit.notBlank(month)){
			sql = sql + " and o.days like'%-"+month+"-%'";
		}
		if(StrKit.notBlank(days)){
			sql = sql + " and o.days ='"+days+"'";
		}
		sql = sql + " order by o.id";
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
    		XdDayModel o = me.getById(id);
    		o.delete();
    	}
	}
	
}