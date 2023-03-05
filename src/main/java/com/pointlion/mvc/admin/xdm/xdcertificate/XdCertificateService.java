package com.pointlion.mvc.admin.xdm.xdcertificate;

import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.XdCertificate;
import com.pointlion.plugin.shiro.ShiroKit;
import com.pointlion.mvc.common.model.SysRoleOrg;
import com.pointlion.mvc.common.utils.DateUtil;

public class XdCertificateService{
	public static final XdCertificateService me = new XdCertificateService();
	public static final String TABLE_NAME = XdCertificate.tableName;
	
	/***
	 * query by id
	 */
	public XdCertificate getById(String id){
		return XdCertificate.dao.findById(id);
	}
	
	/***
	 * get page
	 */
	public Page<Record> getPage(int pNum,int pSize,String title,String certType){
		String userId = ShiroKit.getUserId();
		String sql  = " from "+TABLE_NAME+" o   where cert_type like '%"+certType+"%'";
		/*if(StrKit.notBlank(startTime)){
			sql = sql + " and o.create_time>='"+ DateUtil.formatSearchTime(startTime,"0")+"'";
		}
		if(StrKit.notBlank(endTime)){
			sql = sql + " and o.create_time<='"+DateUtil.formatSearchTime(endTime,"1")+"'";
		}*/
		if(StrKit.notBlank(title)){
			sql = sql + " and o.certificateTitle like '%"+title+"%'";
		}
		sql = sql + " order by o.certificateTitle";
		return Db.paginate(pNum, pSize, " select * ", sql);
	}
	
	public Page<Record> getPage(int pNum,int pSize){
		String sql  = "  from  (" +
				"select 1 as cert_type,count(*) as count_num  from  xd_certificate where cert_type like '%1%'" +
				" UNION " +
				"select 2 as cert_type,count(*) as count_num from  xd_certificate where cert_type like '%2%'" +
				" UNION " +
				"select 3 as cert_type,count(*) as count_num from  xd_certificate where cert_type like '%3%' " +
				" UNION " +
				"select 4 as cert_type,count(*) as count_num from  xd_certificate where cert_type like '%4%' " +
				" UNION " +
				"select 5 as cert_type,count(*)  as count_num from  xd_certificate where cert_type like '%5%' " +
				") as total ";
		/*if(StrKit.notBlank(startTime)){
			sql = sql + " and o.create_time>='"+ DateUtil.formatSearchTime(startTime,"0")+"'";
		}
		if(StrKit.notBlank(endTime)){
			sql = sql + " and o.create_time<='"+DateUtil.formatSearchTime(endTime,"1")+"'";
		}*/

		sql = sql + " order by cert_type";
		return Db.paginate(pNum, pSize, "select *", sql);
	}

	/***
	 * del
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		XdCertificate o = me.getById(id);
    		o.delete();
    	}
	}
	
}