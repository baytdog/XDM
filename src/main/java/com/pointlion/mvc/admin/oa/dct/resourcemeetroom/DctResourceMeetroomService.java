package com.pointlion.mvc.admin.oa.dct.resourcemeetroom;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.DctResourceMeetroom;

public class DctResourceMeetroomService{
	public static final DctResourceMeetroomService me = new DctResourceMeetroomService();
	public static final String TABLE_NAME = "dct_resource_meetroom";
	
	/***
	 * 根据主键查询
	 */
	public DctResourceMeetroom getById(String id){
		return DctResourceMeetroom.dao.findById(id);
	}
	
	/***
	 * 获取分页
	 */
	public Page<Record> getPage(int pnum,int psize){
		String sql  = " from "+TABLE_NAME+" o ";
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
    		DctResourceMeetroom o = me.getById(id);
    		o.delete();
    	}
	}
	
}