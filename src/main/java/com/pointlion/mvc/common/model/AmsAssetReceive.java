package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseAmsAssetReceive;
@SuppressWarnings("serial")
public class AmsAssetReceive extends BaseAmsAssetReceive<AmsAssetReceive> {
	public static final AmsAssetReceive dao = new AmsAssetReceive();
	public static final String tableName = "ams_asset_receive";
	/***
	 * 根据主键查询
	 */
	public AmsAssetReceive getById(String id){
		return AmsAssetReceive.dao.findById(id);
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
    		AmsAssetReceive o = AmsAssetReceive.dao.getById(id);
    		o.delete();
    	}
	}
	
}