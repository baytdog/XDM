package com.pointlion.mvc.admin.xdm.xdworkexper;

import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.SysRoleOrg;
import com.pointlion.mvc.common.model.XdEdutrain;
import com.pointlion.mvc.common.model.XdWorkExper;
import com.pointlion.mvc.common.utils.DateUtil;
import com.pointlion.plugin.shiro.ShiroKit;

import java.util.List;

public class XdWorkExperService{
	public static final XdWorkExperService me = new XdWorkExperService();
	public static final String TABLE_NAME = XdWorkExper.tableName;
	
	/***
	 * query by id
	 */
	public XdWorkExper getById(String id){
		return XdWorkExper.dao.findById(id);
	}
	
	/***
	 * get page
	 */
	public Page<Record> getPage(int pnum,int psize,String startTime,String endTime,String applyUser){
		String userId = ShiroKit.getUserId();
		String sql  = " from "+TABLE_NAME+" o LEFT JOIN act_hi_procinst p ON o.proc_ins_id=p.ID_  where 1=1";
		sql = sql + SysRoleOrg.dao.getRoleOrgSql(userId) ;
		if(StrKit.notBlank(startTime)){
			sql = sql + " and o.create_time>='"+ DateUtil.formatSearchTime(startTime,"0")+"'";
		}
		if(StrKit.notBlank(endTime)){
			sql = sql + " and o.create_time<='"+DateUtil.formatSearchTime(endTime,"1")+"'";
		}
		if(StrKit.notBlank(applyUser)){
			sql = sql + " and o.applyer_name like '%"+applyUser+"%'";
		}
		sql = sql + " order by o.create_time desc";
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
    		XdWorkExper o = me.getById(id);
    		o.delete();
    	}
	}




    public List<XdWorkExper> getWorkExperList(String employeeId) {
//		String sql="select * from "+TABLE_NAME;
 		String sql="select * from "+TABLE_NAME+" where eid='"+employeeId+"'";
		List<XdWorkExper> list = XdWorkExper.dao.find(sql);
		return  list;
    }
}