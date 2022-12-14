package com.pointlion.mvc.common.model;

import java.util.List;

import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseOaBumphOrg;

/**
 * Generated by JFinal.
 */
@SuppressWarnings("serial")
public class OaBumphOrg extends BaseOaBumphOrg<OaBumphOrg> {
	public static final OaBumphOrg dao = new OaBumphOrg();
	
	/***
	 * 根据公文id和类型获取主送抄送单位
	 */
	public List<OaBumphOrg> getList(String bumphId,String type){
		String sql = "select * from oa_bumph_org where 1=1 ";
		if(StrKit.notBlank(bumphId)){
			sql = sql + " and bumph_id='"+bumphId+"' ";
		}
		if(StrKit.notBlank(type)){
			sql = sql+ " and type = '"+type+"'";
		}
		return OaBumphOrg.dao.find(sql);
	}
	
	/***
	 * 根据公文ID删除所有单位
	 */
	@Before(Tx.class)
	public void deleteOrgByBumphId(String bumphid){
		Db.update("delete from oa_bumph_org  where bumph_id='"+bumphid+"'");
	}
}
