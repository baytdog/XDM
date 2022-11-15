package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseDctResourceMeetroom;
@SuppressWarnings("serial")
public class DctResourceMeetroom extends BaseDctResourceMeetroom<DctResourceMeetroom> {
	public static final DctResourceMeetroom dao = new DctResourceMeetroom();
	
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
		String sql  = " from dct_resource_meetroom o ";
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
    		DctResourceMeetroom o = DctResourceMeetroom.dao.getById(id);
    		o.delete();
    	}
	}
	
}