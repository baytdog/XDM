package com.pointlion.mvc.admin.ams.asset;

import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.admin.oa.common.OAConstants;
import com.pointlion.mvc.common.model.AmsAsset;

public class AmsAssetService{
	public static final AmsAssetService me = new AmsAssetService();
	public static final String TABLE_NAME = AmsAsset.tableName;
	
	/***
	 * 根据主键查询
	 */
	public AmsAsset getById(String id){
		return AmsAsset.dao.findById(id);
	}
	
	/***
	 * 获取分页
	 */
	public Page<Record> getPage(String orgid,int pnum,int psize){
		String sql  = " from "+TABLE_NAME+" o where 1=1 ";
		if(StrKit.notBlank(orgid)){
			sql = sql + " and o.belong_org_id = '"+orgid+"' order by o.create_time desc";
		}
		return Db.paginate(pnum, psize, " select * ", sql);
	}
	/***
	 * 获取分页
	 */
	public Page<Record> getCanUsePage(String orgid,int pnum,int psize){
		String sql  = " from "+TABLE_NAME+" o where o.state='"+OAConstants.AMS_ASSET_STATE_0+"' ";
		if(StrKit.notBlank(orgid)){
			sql = sql + " and o.belong_org_id = '"+orgid+"' order by o.create_time desc";
		}
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
    		AmsAsset o = me.getById(id);
    		o.delete();
    	}
	}
	
}