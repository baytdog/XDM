package com.pointlion.mvc.admin.xdm.xdsteps;

import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.XdSteps;
import com.pointlion.plugin.shiro.ShiroKit;
import com.pointlion.mvc.common.model.SysRoleOrg;
import com.pointlion.mvc.common.utils.DateUtil;

public class XdStepsService{
	public static final XdStepsService me = new XdStepsService();
	public static final String TABLE_NAME = XdSteps.tableName;
	
	/***
	 * query by id
	 */
	public XdSteps getById(String id){
		return XdSteps.dao.findById(id);
	}
	
	/***
	 * get page
	 */
	public Page<Record> getPage(int pnum,int psize,String startTime,String endTime,String applyUser){
		String userId = ShiroKit.getUserId();
		String sql  = " from "+TABLE_NAME+" o   where finished='N' and stype='1'";
		if("1".equals(ShiroKit.getUserOrgId())){
			sql=sql+" and o.orgid=1";
		}else{
			sql=sql+" and  userid='"+userId+"'";
		}
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
    		XdSteps o = me.getById(id);
    		o.delete();
    	}
	}
	
}