package com.pointlion.mvc.admin.oa.contract.apply;

import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.OaContractApply;
import com.pointlion.mvc.common.model.SysRoleOrg;
import com.pointlion.plugin.shiro.ShiroKit;

public class OaContractApplyService{
	public static final OaContractApplyService me = new OaContractApplyService();
	public static final String TABLE_NAME = OaContractApply.tableName;
	
	/***
	 * 根据主键查询
	 */
	public OaContractApply getById(String id){
		return OaContractApply.dao.findById(id);
	}
	
	/***
	 * 获取分页
	 */
	public Page<Record> getPage(int pnum,int psize,String type,String name,String startTime,String endTime){
		String sql  = " from "+TABLE_NAME+" o LEFT JOIN act_hi_procinst p ON o.proc_ins_id=p.ID_  where 1=1 ";
		if(StrKit.notBlank(type)){
			sql = sql + " and o.type = '"+type+"'";
		}
		String userId = ShiroKit.getUserId();
		//数据权限
		sql = sql + SysRoleOrg.dao.getRoleOrgSql(userId) ;
		sql = sql + getQuerySql(type,name,startTime,endTime);
		sql = sql + " order by o.create_time desc";
		return Db.paginate(pnum, psize, " select o.*,p.PROC_DEF_ID_ defid ", sql);
	}
	/****
	 * 获取查询sql
	 * @param type
	 * @param name
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public String  getQuerySql(String type,String name,String startTime,String endTime){
		String sql = " ";
		if(StrKit.notBlank(type)){
			sql = sql + " and o.type='"+type+"' ";
		}
		if(StrKit.notBlank(name)){
			sql = sql + " and (o.applyer_name like '%"+name+"%' or o.org_name like '%"+name+"%')";
		}
		if(StrKit.notBlank(startTime)){
			sql = sql + "  and o.create_time >= '"+startTime+" 00:00:00'";
		}
		if(StrKit.notBlank(endTime)){
			sql = sql + "  and o.create_time <= '"+endTime+" 23:59:59'";
		}
		return sql;
	}
	
	/***
	 * 删除
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		OaContractApply o = me.getById(id);
    		o.delete();
    	}
	}
	
	/***
	 * 获取申请类型对应的名称
	 */
	public String getTypeName(String type){
		String name = "";
		if("1".equals(type)){
			name = "物业管理顾问服务合同";
		}else if("2".equals(type)){
			name = "物业管理早期介入协议";
		}else if("3".equals(type)){
			name = "前期物业服务合同及补充协议";
		}else if("4".equals(type)){
			name = "常态化物业管理服务合同及补充协议";
		}else if("5".equals(type)){
			name = "拓展物业管理项目合作协议及补充协议";
		}else if("6".equals(type)){
			name = "采购方案-多种经营类采购方案";
		}else if("7".equals(type)){
			name = "采购方案-物资类采购方案";
		}else if("8".equals(type)){
			name = "采购方案-工程、服务分包类采购方案";
		}else if("9".equals(type)){
			name = "中介合作协议";
		}else if("10".equals(type)){
			name = "销售案场物业管理服务合同";
		}else if("11".equals(type)){
			name = "公共经营方案";
		}else if("12".equals(type)){
			name = "公共经营合同";
		}else if("13".equals(type)){
			name = "配套增值服务开发合同";
		}else if("14".equals(type)){
			name = "多种经营类采购合同";
		}else if("15".equals(type)){
			name = "物资类采购合同";
		}else if("16".equals(type)){
			name = "工程、服务分包类采购合同";
		}else if("17".equals(type)){
			name = "行政管理类合同";
		}else{
			name = "合同申请";
		}
		return name;
	}
	
	/***
	 * 获取申请类型对应的名称
	 */
	public String getDefKeyByType(String type){
		String key = "ContractApply";
//		if(StrKit.notBlank(type)){
//			key = key +"_"+type;
//		}
		return key;
	}

}
