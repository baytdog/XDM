package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseXdDayModel;
@SuppressWarnings("serial")
public class XdDayModel extends BaseXdDayModel<XdDayModel> {
	public static final XdDayModel dao = new XdDayModel();
	public static final String tableName = "xd_day_model";
	
	/***
	 * query by id
	 */
	public XdDayModel getById(String id){
		return XdDayModel.dao.findById(id);
	}
	
	/***
	 * del
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		XdDayModel o = XdDayModel.dao.getById(id);
    		o.delete();
    	}
	}
	
}