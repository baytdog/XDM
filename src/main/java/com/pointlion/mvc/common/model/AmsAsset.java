package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseAmsAsset;
@SuppressWarnings("serial")
public class AmsAsset extends BaseAmsAsset<AmsAsset> {
	public static final AmsAsset dao = new AmsAsset();
	public static final String tableName = "ams_asset";
	
	/***
	 * 根据主键查询
	 */
	public AmsAsset getById(String id){
		return AmsAsset.dao.findById(id);
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
    		AmsAsset o = AmsAsset.dao.getById(id);
    		o.delete();
    	}
	}
	
	/***
	 * 根据录入id查询资产信息
	 */
	public AmsAsset getBySigninId(String signinid){
		return dao.findFirst("select * from ams_asset a where a.signin_id='"+signinid+"'");
	}
}