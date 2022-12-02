package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseXdOplogDetail;
@SuppressWarnings("serial")
public class XdOplogDetail extends BaseXdOplogDetail<XdOplogDetail> {
	public static final XdOplogDetail dao = new XdOplogDetail();
	public static final String tableName = "xd_oplog_detail";
	
	/***
	 * query by id
	 */
	public XdOplogDetail getById(String id){
		return XdOplogDetail.dao.findById(id);
	}
	
	/***
	 * del
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		XdOplogDetail o = XdOplogDetail.dao.getById(id);
    		o.delete();
    	}
	}
	
}