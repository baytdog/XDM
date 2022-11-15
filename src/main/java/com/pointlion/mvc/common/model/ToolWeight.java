package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseToolWeight;
@SuppressWarnings("serial")
public class ToolWeight extends BaseToolWeight<ToolWeight> {
	public static final ToolWeight dao = new ToolWeight();
	public static final String tableName = "tool_weight";


	public ToolWeight getById(String id){
		return ToolWeight.dao.findById(id);
	}


	@Before(Tx.class)
	public void deleteByIds(String ids){
		String idarr[] = ids.split(",");
		for(String id : idarr){
			ToolWeight o = ToolWeight.dao.getById(id);
			o.delete();
		}
	}

}