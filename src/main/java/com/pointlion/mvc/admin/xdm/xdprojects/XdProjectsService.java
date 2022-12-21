package com.pointlion.mvc.admin.xdm.xdprojects;

import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.XdProjects;
import com.pointlion.plugin.shiro.ShiroKit;
import com.pointlion.mvc.common.model.SysRoleOrg;
import com.pointlion.mvc.common.utils.DateUtil;

public class XdProjectsService{
	public static final XdProjectsService me = new XdProjectsService();
	public static final String TABLE_NAME = XdProjects.tableName;
	
	/***
	 * query by id
	 */
	public XdProjects getById(String id){
		return XdProjects.dao.findById(id);
	}
	
	/***
	 * get page
	 */
	public Page<Record> getPage(int pnum,int psize,String proName){
		String sql  = " from "+TABLE_NAME+" o   where status=1";
		if(StrKit.notBlank(proName)){
			sql = sql + " and o.project_name like '%"+proName+"%'";
		}
		sql = sql + " order by o.ctime";
		return Db.paginate(pnum, psize, " select * ", sql);
	}
	
	/***
	 * del
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		XdProjects o = me.getById(id);
    		o.setStatus(0);
			o.update();
    	}
	}
	
}