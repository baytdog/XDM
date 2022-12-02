package com.pointlion.mvc.admin.xdm.xdemployee;

import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.enums.LogsEnum;
import com.pointlion.mvc.common.model.XdEmployee;
import com.pointlion.mvc.common.model.XdOplogSummary;
import com.pointlion.mvc.common.utils.JSONUtil;
import com.pointlion.mvc.common.utils.UuidUtil;
import com.pointlion.plugin.shiro.ShiroKit;
import com.pointlion.mvc.common.model.SysRoleOrg;
import com.pointlion.mvc.common.utils.DateUtil;

public class XdEmployeeService{
	public static final XdEmployeeService me = new XdEmployeeService();
	public static final String TABLE_NAME = XdEmployee.tableName;
	
	/***
	 * query by id
	 */
	public XdEmployee getById(String id){
		return XdEmployee.dao.findById(id);
	}
	
	/***
	 * get page
	 */
	public Page<Record> getPage(int pnum,int psize,String startTime,String endTime,String applyUser){
		String userId = ShiroKit.getUserId();
		String sql  = " from "+TABLE_NAME+" o where 1=1";
		sql = sql + SysRoleOrg.dao.getRoleOrgSql(userId) ;
		if(StrKit.notBlank(startTime)){
			sql = sql + " and o.create_time>='"+ DateUtil.formatSearchTime(startTime,"0")+"'";
		}
		if(StrKit.notBlank(endTime)){
			sql = sql + " and o.create_time<='"+DateUtil.formatSearchTime(endTime,"1")+"'";
		}
//		if(StrKit.notBlank(applyUser)){
//			sql = sql + " and o.applyer_name like '%"+applyUser+"%'";
//		}
		sql = sql + " order by o.ctime desc";
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
    		XdEmployee o = me.getById(id);
			String objStr = JSONUtil.beanToJsonString(o);
			XdOplogSummary logSum=new XdOplogSummary();
			logSum.setId(UuidUtil.getUUID());
			logSum.setOid(id);
			logSum.setTid(id);
			logSum.setTname(o.getClass().getSimpleName());
			logSum.setChangeb(objStr);
			logSum.setChangea("");
			logSum.setStatus(LogsEnum.WAITAPPRO.name());
			logSum.setOtype(LogsEnum.D.name());
			logSum.setCtime(DateUtil.getCurrentTime());
			logSum.setCuser(ShiroKit.getUserId());
			logSum.save();

			o.delete();
    	}
	}
	
}