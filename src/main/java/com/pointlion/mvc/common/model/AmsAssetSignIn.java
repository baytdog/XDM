package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseAmsAssetSignIn;
@SuppressWarnings("serial")
public class AmsAssetSignIn extends BaseAmsAssetSignIn<AmsAssetSignIn> {
	public static final AmsAssetSignIn dao = new AmsAssetSignIn();
	public static final String tableName = "ams_asset_sign_in";
	
	/***
	 * 根据主键查询
	 */
	public AmsAssetSignIn getById(String id){
		return AmsAssetSignIn.dao.findById(id);
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
    		AmsAssetSignIn o = AmsAssetSignIn.dao.getById(id);
    		o.delete();
    	}
	}
	
}