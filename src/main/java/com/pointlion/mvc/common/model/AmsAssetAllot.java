package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseAmsAssetAllot;
@SuppressWarnings("serial")
public class AmsAssetAllot extends BaseAmsAssetAllot<AmsAssetAllot> {
	public static final AmsAssetAllot dao = new AmsAssetAllot();
	public static final String tableName = "ams_asset_allot";
	/***
	 * 根据主键查询
	 */
	public AmsAssetAllot getById(String id){
		return AmsAssetAllot.dao.findById(id);
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
    		AmsAssetAllot o = AmsAssetAllot.dao.getById(id);
    		o.delete();
    	}
	}
	
}