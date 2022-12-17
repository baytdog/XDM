package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseXdCertificate;
@SuppressWarnings("serial")
public class XdCertificate extends BaseXdCertificate<XdCertificate> {
	public static final XdCertificate dao = new XdCertificate();
	public static final String tableName = "xd_certificate";
	
	/***
	 * query by id
	 */
	public XdCertificate getById(String id){
		return XdCertificate.dao.findById(id);
	}
	
	/***
	 * del
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		XdCertificate o = XdCertificate.dao.getById(id);
    		o.delete();
    	}
	}
	
}