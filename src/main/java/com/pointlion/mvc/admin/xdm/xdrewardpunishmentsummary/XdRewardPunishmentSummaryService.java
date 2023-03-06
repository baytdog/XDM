package com.pointlion.mvc.admin.xdm.xdrewardpunishmentsummary;

import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.XdRewardPunishmentSummary;
import com.pointlion.plugin.shiro.ShiroKit;
import com.pointlion.mvc.common.model.SysRoleOrg;
import com.pointlion.mvc.common.utils.DateUtil;

public class XdRewardPunishmentSummaryService{
	public static final XdRewardPunishmentSummaryService me = new XdRewardPunishmentSummaryService();
	public static final String TABLE_NAME = XdRewardPunishmentSummary.tableName;
	
	/***
	 * query by id
	 */
	public XdRewardPunishmentSummary getById(String id){
		return XdRewardPunishmentSummary.dao.findById(id);
	}
	
	/***
	 * get page
	 */
	public Page<Record> getPage(int pnum,int psize,String year ){
		String sql  = " from "+TABLE_NAME+" o   where 1=1";
		String userOrgId = ShiroKit.getUserOrgId();
		if(!"1".equals(userOrgId)){

			sql = sql + " and o.dept_id='"+ userOrgId+"'";
		}
		if(StrKit.notBlank(year)){
			sql = sql + " and o.year>='"+ year+"'";
		}
//		sql = sql + " order by o.create_time desc";
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
    		XdRewardPunishmentSummary o = me.getById(id);
    		o.delete();
    	}
	}
	
}