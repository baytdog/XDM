package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseXdCertLog;
@SuppressWarnings("serial")
public class XdCertLog extends BaseXdCertLog<XdCertLog> {
	public static final XdCertLog dao = new XdCertLog();
	public static final String tableName = "xd_cert_log";
	
	/***
	 * query by id
	 */
	public XdCertLog getById(String id){
		return XdCertLog.dao.findById(id);
	}
	
	/***
	 * del
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		XdCertLog o = XdCertLog.dao.getById(id);
    		o.delete();
    	}
	}
	
}