package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseOaNotices;
import com.pointlion.plugin.shiro.ShiroKit;
@SuppressWarnings("serial")
public class OaNotices extends BaseOaNotices<OaNotices> {
	public static final OaNotices dao = new OaNotices();
	public static final String tableName = "oa_notices";
	
	/***
	 * query by id
	 */
	public OaNotices getById(String id){
		return OaNotices.dao.findById(id);
	}
	
	/***
	 * del
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		OaNotices o = OaNotices.dao.getById(id);
    		o.delete();
    	}
	}
	

	public Page<OaNotices> getPage(int pnum,int psize){
		String userId=ShiroKit.getUserId();
		String userName = ShiroKit.getUsername();
		if(userName.equals("admin")) {
			
			return OaNotices.dao.paginate(pnum, psize, "select * "," FROM oa_notices n ORDER BY n.publishdatetime DESC");
		}else {
			
			
			return OaNotices.dao.paginate(pnum, psize, "select * "," FROM oa_notices n  where n.publisherid='"+userId+"' ORDER BY n.publishdatetime DESC");
		}
	}
	
}