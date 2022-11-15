package com.pointlion.mvc.admin.oa.departnotices;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.OaDepartNotices;
import com.pointlion.mvc.common.utils.Constants;
import com.pointlion.plugin.shiro.ShiroKit;

public class OaDepartNoticesService{
	public static final OaDepartNoticesService me = new OaDepartNoticesService();
	public static final String TABLE_NAME = OaDepartNotices.tableName;
	
	/***
	 * query by id
	 */
	public OaDepartNotices getById(String id){
		return OaDepartNotices.dao.findById(id);
	}
	
	/***
	 * get page
	 */
	public Page<Record> getPage(int pnum,int psize,String startTime,String endTime,String applyUser){
		String userId=ShiroKit.getUserId();
		String userName = ShiroKit.getUsername();
	/*	if(Constants.DEPARTHOME.indexOf(userName)!=-1) {
			
			return Db.paginate(pnum, psize, "select * "," FROM oa_depart_notices n ORDER BY n.publishdatetime DESC");
		}else {*/
			return Db.paginate(pnum, psize, "select * "," FROM oa_depart_notices n  where n.publisherid='"+userId+"' ORDER BY n.publishdatetime DESC");
		//}
	}
	public Page<Record> getMorePage(int pnum,int psize,String startTime,String endTime,String applyUser){
		String userName = ShiroKit.getUsername();
		if(Constants.DEPARTHOME.indexOf(userName)!=-1) {
		
			return Db.paginate(pnum, psize, "select * "," FROM oa_depart_notices n  where  n.sfpublish='1'  ORDER BY n.publishdatetime DESC");
		}else {
			return Db.paginate(pnum, psize, "select * "," FROM oa_depart_notices n  where n.departments='"+ShiroKit.getUserOrgId()+"' and n.sfpublish='1' ORDER BY n.publishdatetime DESC");
		}
			
		 
	}
	
	/***
	 * del
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		OaDepartNotices o = me.getById(id);
    		o.delete();
    	}
	}
	
}