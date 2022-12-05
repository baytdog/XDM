package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseXdEdutrain;
import com.pointlion.mvc.common.utils.DateUtil;
import com.pointlion.mvc.common.utils.UuidUtil;
import com.pointlion.plugin.shiro.ShiroKit;

@SuppressWarnings("serial")
public class XdEdutrain extends BaseXdEdutrain<XdEdutrain> {
	public static final XdEdutrain dao = new XdEdutrain();
	public static final String tableName = "xd_edutrain";
	
	/***
	 * query by id
	 */
	public XdEdutrain getById(String id){
		return XdEdutrain.dao.findById(id);
	}
	
	/***
	 * del
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		XdEdutrain o = XdEdutrain.dao.getById(id);
    		o.delete();
    	}
	}

	public void save(XdEdutrain xdEdutrain){
		xdEdutrain.setId(UuidUtil.getUUID());
		//xdEdutrain.setEid(o.getId());
		xdEdutrain.setEnrolldate(xdEdutrain.getEnrolldate().length()>9?xdEdutrain.getEnrolldate().substring(0,10):"");
		xdEdutrain.setGraduatdate(xdEdutrain.getGraduatdate().length()>9?xdEdutrain.getGraduatdate().substring(0,10):"");
		xdEdutrain.setCuser(ShiroKit.getUserId());
		xdEdutrain.setCtime(DateUtil.getCurrentTime());
		xdEdutrain.save();
	}
	
}