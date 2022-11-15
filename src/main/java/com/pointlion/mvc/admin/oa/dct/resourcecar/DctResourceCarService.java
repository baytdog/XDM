package com.pointlion.mvc.admin.oa.dct.resourcecar;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.DctResourceCar;

public class DctResourceCarService{
	public static final DctResourceCarService me = new DctResourceCarService();
	public static final String TABLE_NAME = "dct_resource_car";
	
	/***
	 * 根据主键查询
	 */
	public DctResourceCar getById(String id){
		return DctResourceCar.dao.findById(id);
	}
	
	/***
	 * 获取分页
	 */
	public Page<Record> getPage(int pnum,int psize){
		String sql  = " from "+TABLE_NAME+" o ";
		return Db.paginate(pnum, psize, " select * ", sql);
	}
	
	/***
	 * 获取选择数据分页
	 */
	public Page<Record> getSelectPage(int pnum,int psize){
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
    		DctResourceCar o = me.getById(id);
    		o.delete();
    	}
	}
	
}