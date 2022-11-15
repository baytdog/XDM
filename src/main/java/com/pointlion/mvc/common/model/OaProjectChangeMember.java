package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseOaProjectChangeMember;
@SuppressWarnings("serial")
public class OaProjectChangeMember extends BaseOaProjectChangeMember<OaProjectChangeMember> {
	public static final OaProjectChangeMember dao = new OaProjectChangeMember();
	public static final String tableName = "oa_project_change_member";
	
	/***
	 * 根据主键查询
	 */
	public OaProjectChangeMember getById(String id){
		return OaProjectChangeMember.dao.findById(id);
	}
	
	/***
	 * 获取分页
	 */
	public Page<Record> getPage(int pnum,int psize){
		String sql  = " from "+tableName+" o ";
		return Db.paginate(pnum, psize, " select * ", sql);
	}
	
	/***
	 * 删除
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		OaProjectChangeMember o = OaProjectChangeMember.dao.getById(id);
    		o.delete();
    	}
	}
	
}