package com.pointlion.mvc.admin.oa.stephistory;

import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.OaStepHistory;
import com.pointlion.mvc.common.utils.DateUtil;
import com.pointlion.plugin.shiro.ShiroKit;

public class OaStepHistoryService{
	public static final OaStepHistoryService me = new OaStepHistoryService();
	public static final String TABLE_NAME = OaStepHistory.tableName;
	
	/***
	 * query by id
	 */
	public OaStepHistory getById(String id){
		return OaStepHistory.dao.findById(id);
	}
	
	/***
	 * get page
	 */
	public Page<Record> getPage(int pnum,int psize,String startTime,String endTime,String type,String ftitle){
		String userId = ShiroKit.getUserId();
		String sql  = " from "+TABLE_NAME+" o  where actorid='"+userId+"'";
		if(StrKit.notBlank(startTime)){
			sql = sql + " and o.ctime>='"+ DateUtil.formatSearchTime(startTime,"0")+"'";
		}
		if(StrKit.notBlank(endTime)){
			sql = sql + " and o.ctime<='"+DateUtil.formatSearchTime(endTime,"1")+"'";
		}
		if(StrKit.notBlank(type)){
			sql = sql + " and o.type='"+type+"'";
		}
		if(StrKit.notBlank(ftitle)){
			sql = sql + " and o.title  like '%"+ftitle+"%'";
		}
		sql = sql + " order by o.acttime desc";
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
    		OaStepHistory o = me.getById(id);
    		o.delete();
    	}
	}
	
}