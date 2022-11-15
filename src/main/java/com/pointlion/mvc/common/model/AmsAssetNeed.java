package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseAmsAssetNeed;
@SuppressWarnings("serial")
public class AmsAssetNeed extends BaseAmsAssetNeed<AmsAssetNeed> {
	public static final AmsAssetNeed dao = new AmsAssetNeed();
	public static final String tableName = "ams_asset_need";
	
	/***
	 * 根据主键查询
	 */
	public AmsAssetNeed getById(String id){
		return AmsAssetNeed.dao.findById(id);
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
    		AmsAssetNeed o = AmsAssetNeed.dao.getById(id);
    		o.delete();
    	}
	}
	
}