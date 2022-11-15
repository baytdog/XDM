package com.pointlion.mvc.common.model;

import java.util.List;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseGrapDbSource;
@SuppressWarnings("serial")
public class GrapDbSource extends BaseGrapDbSource<GrapDbSource> {
	public static final GrapDbSource dao = new GrapDbSource();
	public static final String tableName = "grap_db_source";
	
	/***
	 * 根据主键查询
	 */
	public GrapDbSource getById(String id){
		return GrapDbSource.dao.findById(id);
	}
	
	/***
	 * 删除
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		GrapDbSource o = GrapDbSource.dao.getById(id);
    		o.delete();
    	}
	}
	
	/***
	 * 获取所有数据库连接
	 * @return
	 */
	public List<GrapDbSource> getAllDbsource(){
		return dao.find("select * from "+tableName);
	}
	
}