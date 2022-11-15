package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseOaProject;
@SuppressWarnings("serial")
public class OaProject extends BaseOaProject<OaProject> {
	public static final OaProject dao = new OaProject();
	public static final String tableName = "oa_project";
	
	/***
	 * 根据主键查询
	 */
	public OaProject getById(String id){
		return OaProject.dao.findById(id);
	}
	
	
	/***
	 * 删除
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		OaProject o = OaProject.dao.getById(id);
    		o.delete();
    	}
	}
	
	/***
	 * 根据立项申请id查询项目
	 */
	public OaProject getByBuildId(String buildid){
		return dao.findFirst("select * from oa_project p where p.build_id='"+buildid+"'");
	}
	
}