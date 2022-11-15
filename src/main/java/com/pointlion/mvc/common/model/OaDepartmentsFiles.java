package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseOaDepartmentsFiles;
@SuppressWarnings("serial")
public class OaDepartmentsFiles extends BaseOaDepartmentsFiles<OaDepartmentsFiles> {
	public static final OaDepartmentsFiles dao = new OaDepartmentsFiles();
	public static final String tableName = "oa_departments_files";
	
	/***
	 * query by id
	 */
	public OaDepartmentsFiles getById(String id){
		return OaDepartmentsFiles.dao.findById(id);
	}
	
	/***
	 * del
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		OaDepartmentsFiles o = OaDepartmentsFiles.dao.getById(id);
    		o.delete();
    	}
	}
	
}