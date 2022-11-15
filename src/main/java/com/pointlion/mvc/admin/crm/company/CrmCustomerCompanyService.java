package com.pointlion.mvc.admin.crm.company;

import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.CrmCustomerCompany;
import com.pointlion.plugin.shiro.ShiroKit;
import com.pointlion.mvc.common.model.SysRoleOrg;
import com.pointlion.mvc.common.utils.DateUtil;

import java.util.List;

public class CrmCustomerCompanyService{
	public static final CrmCustomerCompanyService me = new CrmCustomerCompanyService();
	public static final String TABLE_NAME = CrmCustomerCompany.tableName;
	
	/***
	 * query by id
	 */
	public CrmCustomerCompany getById(String id){
		return CrmCustomerCompany.dao.findById(id);
	}
	
	/***
	 * get page
	 */
	public Page<Record> getPage(int pnum,int psize,String startTime,String endTime,String companyName){
		String userId = ShiroKit.getUserId();
		String sql  = " from "+TABLE_NAME+" o LEFT JOIN act_hi_procinst p ON o.proc_ins_id=p.ID_  where 1=1";
		sql = sql + SysRoleOrg.dao.getRoleOrgSql(userId) ;
		if(StrKit.notBlank(startTime)){
			sql = sql + " and o.create_time>='"+ DateUtil.formatSearchTime(startTime,"0")+"'";
		}
		if(StrKit.notBlank(endTime)){
			sql = sql + " and o.create_time<='"+DateUtil.formatSearchTime(endTime,"1")+"'";
		}
		if(StrKit.notBlank(companyName)){
			sql = sql + " and o.name like '%"+companyName+"%'";
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
    		CrmCustomerCompany o = me.getById(id);
    		o.delete();
    	}
	}

	/***
	 * 获取所有的数据
	 */
	public List<CrmCustomerCompany> findAll(){
		return CrmCustomerCompany.dao.findAll();
	}

	/***
	 * 获取所有可用的数据
	 */
	public List<CrmCustomerCompany> findCanUseCompany(){
		return CrmCustomerCompany.dao.findCanUseCompany();
	}
}