package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseOaSearch;
import com.pointlion.plugin.shiro.ShiroKit;
@SuppressWarnings("serial")
public class OaSearch extends BaseOaSearch<OaSearch> {
	public static final OaSearch dao = new OaSearch();
	public static final String tableName = "oa_search";
	
	/***
	 * query by id
	 */
	public OaSearch getById(String id){
		return OaSearch.dao.findById(id);
	}
	
	/***
	 * del
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		OaSearch o = OaSearch.dao.getById(id);
    		o.delete();
    	}
	}
	
	public Page<OaSearch> getPage(int pnum,int psize,String keyWords){
		//String sql = " from "+TABLE_NAME+" o   WHERE  o.backup2='1'   ";
		String userId = ShiroKit.getUserId();
		String sql = " from "+tableName+" o   WHERE 1=1  and  userid ='"+userId+"' and keywords='"+keyWords+"'";
		sql = sql + " ORDER BY otime DESC";
		return OaSearch.dao.paginate(pnum, psize, "select o.*", sql);
	}
	
	
	
	
	
	
}