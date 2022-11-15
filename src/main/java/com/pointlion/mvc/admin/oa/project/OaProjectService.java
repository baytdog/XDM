package com.pointlion.mvc.admin.oa.project;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.OaProject;
import com.pointlion.mvc.common.model.OaProjectBuild;
import com.pointlion.mvc.common.model.SysUser;

public class OaProjectService{
	public static final OaProjectService me = new OaProjectService();
	public static final String TABLE_NAME = OaProject.tableName;
	
	/***
	 * 根据主键查询
	 */
	public OaProjectBuild getById(String id){
		return OaProjectBuild.dao.findById(id);
	}
	
	/***
	 * 获取分页
	 */
	public Page<Record> getPage(int pnum,int psize){
		String sql  = " from "+TABLE_NAME+" o ";
		Page<Record> page = Db.paginate(pnum, psize, " select * ", sql);
		List<Record> list = page.getList();
		for(Record r : list){
			List<String> leaderStr = new ArrayList<String>();
			String leader = r.getStr("leader");
			if(StrKit.notBlank(leader)){//如果有项目经理
				String leaderArr[] = leader.split(",");
				for(String id : leaderArr){
					SysUser user = SysUser.dao.getById(id);
					if(user!=null){
						leaderStr.add(user.getName());
					}
				}
			}
			r.set("leaderName", StringUtils.join(leaderStr, ","));
		}
		return page;
	}
	
	/***
	 * 删除
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		OaProjectBuild o = me.getById(id);
    		o.delete();
    	}
	}
	
}