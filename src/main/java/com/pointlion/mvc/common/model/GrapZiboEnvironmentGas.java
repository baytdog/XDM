package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseGrapZiboEnvironmentGas;
@SuppressWarnings("serial")
public class GrapZiboEnvironmentGas extends BaseGrapZiboEnvironmentGas<GrapZiboEnvironmentGas> {
	public static final GrapZiboEnvironmentGas dao = new GrapZiboEnvironmentGas();
	public static final String tableName = "grap_zibo_environment_gas";
	
	/***
	 * 根据主键查询
	 */
	public GrapZiboEnvironmentGas getById(String id){
		return GrapZiboEnvironmentGas.dao.findById(id);
	}
	
	/***
	 * 删除
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		GrapZiboEnvironmentGas o = GrapZiboEnvironmentGas.dao.getById(id);
    		o.delete();
    	}
	}
	
}