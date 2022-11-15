package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseDctResourceCar;
@SuppressWarnings("serial")
public class DctResourceCar extends BaseDctResourceCar<DctResourceCar> {
	public static final DctResourceCar dao = new DctResourceCar();
	
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
		String sql  = " from dct_resource_car o ";
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
    		DctResourceCar o = DctResourceCar.dao.getById(id);
    		o.delete();
    	}
	}
	
}