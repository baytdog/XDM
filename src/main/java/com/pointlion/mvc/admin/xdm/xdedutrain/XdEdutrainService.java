package com.pointlion.mvc.admin.xdm.xdedutrain;

import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.XdEdutrain;
import com.pointlion.plugin.shiro.ShiroKit;
import com.pointlion.mvc.common.model.SysRoleOrg;
import com.pointlion.mvc.common.utils.DateUtil;

import java.util.List;

public class XdEdutrainService{
	public static final XdEdutrainService me = new XdEdutrainService();
	public static final String TABLE_NAME = XdEdutrain.tableName;
	
	/***
	 * query by id
	 */
	public XdEdutrain getById(String id){
		return XdEdutrain.dao.findById(id);
	}
	
	/***
	 * get page
	 */
	public Page<Record> getPage(int pnum,int psize,String startTime,String endTime,String applyUser){
		String userId = ShiroKit.getUserId();
		String sql  = " from "+TABLE_NAME+" o  where 1=1";
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
    		XdEdutrain o = me.getById(id);
    		o.delete();
    	}
	}

	public List<XdEdutrain> getEduTrainList(String  employeeId){
		String sql="select * from "+TABLE_NAME;
//		String sql="select * from "+TABLE_NAME+"where eid='"+employeeId+"'";
		//List<XdEdutrain> query = Db.query(sql);
		List<XdEdutrain> list = XdEdutrain.dao.find(sql);
		return  list;
	}
}