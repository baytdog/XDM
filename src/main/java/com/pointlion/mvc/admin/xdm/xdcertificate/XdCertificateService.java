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
	public Page<Record> getPage(int pnum,int psize,String title,String haveCertificate,String haveEndDate,String continuEdu){
		String userId = ShiroKit.getUserId();
		String sql  = " from "+TABLE_NAME+" o   where 1=1";
		/*if(StrKit.notBlank(startTime)){
			sql = sql + " and o.create_time>='"+ DateUtil.formatSearchTime(startTime,"0")+"'";
		}
		if(StrKit.notBlank(endTime)){
			sql = sql + " and o.create_time<='"+DateUtil.formatSearchTime(endTime,"1")+"'";
		}*/
		if(StrKit.notBlank(title)){
			sql = sql + " and o.certificateTitle like '%"+title+"%'";
		}
		if(StrKit.notBlank(haveCertificate)){
			sql = sql + " and o.haveCertificate ='"+haveCertificate+"'";
		}
		if(StrKit.notBlank(haveEndDate)){
			sql = sql + " and o.haveEndDate = '"+haveEndDate+"'";
		}
		if(StrKit.notBlank(continuEdu)){
			sql = sql + " and o.continuEdu = '"+continuEdu+"'";
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
    		XdCertificate o = me.getById(id);
    		o.delete();
    	}
	}
	
}