package com.pointlion.mvc.admin.oa.apply.common;

import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.OaApplyCommon;
import com.pointlion.mvc.common.model.SysRoleOrg;
import com.pointlion.mvc.common.utils.DateUtil;
import com.pointlion.plugin.shiro.ShiroKit;

public class OaApplyCommonService{
	public static final OaApplyCommonService me = new OaApplyCommonService();
	public static final String TABLE_NAME = OaApplyCommon.tableName;

	/***
	 * 根据主键查询
	 */
	public OaApplyCommon getById(String id){
		return OaApplyCommon.dao.findById(id);
	}

	/***
	 * 获取分页
	 */
	public Page<Record> getPage(int pnum,int psize,String startTime,String endTime,String applyUser,String type){
		String sql  = " from "+TABLE_NAME+" o LEFT JOIN act_hi_procinst p ON o.proc_ins_id=p.ID_  where 1=1";
		String userId = ShiroKit.getUserId();
		//数据权限
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
		if(StrKit.notBlank(type)){
			sql = sql + " and o.type = '"+type+"'";
		}
		sql = sql + " order by o.create_time desc";
		return Db.paginate(pnum, psize, " select * ", sql);
	}

	/***
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
		String idarr[] = ids.split(",");
		for(String id : idarr){
			OaApplyCommon o = me.getById(id);
			o.delete();
		}
	}

}