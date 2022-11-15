package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseOaBumphRz;
@SuppressWarnings("serial")
public class OaBumphRz extends BaseOaBumphRz<OaBumphRz> {
	public static final OaBumphRz dao = new OaBumphRz();
	public static final String tableName = "oa_bumph_rz";
	
	/***
	 * query by id
	 */
	public OaBumphRz getById(String id){
		return OaBumphRz.dao.findById(id);
	}
	
	/***
	 * del
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		OaBumphRz o = OaBumphRz.dao.getById(id);
    		o.delete();
    	}
	}
	
}