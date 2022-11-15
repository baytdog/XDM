package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseAmsAssetBorrow;
@SuppressWarnings("serial")
public class AmsAssetBorrow extends BaseAmsAssetBorrow<AmsAssetBorrow> {
	public static final AmsAssetBorrow dao = new AmsAssetBorrow();
	public static final String tableName = "ams_asset_borrow";
	/***
	 * 根据主键查询
	 */
	public AmsAssetBorrow getById(String id){
		return AmsAssetBorrow.dao.findById(id);
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
    		AmsAssetBorrow o = AmsAssetBorrow.dao.getById(id);
    		o.delete();
    	}
	}
	
}