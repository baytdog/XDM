package com.pointlion.mvc.admin.oa.apply.buy;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.OaApplyBuy;
import com.pointlion.mvc.common.model.OaApplyBuyItem;
import com.pointlion.mvc.common.model.SysRoleOrg;
import com.pointlion.plugin.shiro.ShiroKit;

import java.util.List;

public class OaApplyBuyService{
	public static final OaApplyBuyService me = new OaApplyBuyService();
	public static final String TABLE_NAME = OaApplyBuy.tableName;
	
	/***
	 * 根据主键查询
	 */
	public OaApplyBuy getById(String id){
		return OaApplyBuy.dao.findById(id);
	}
	
	/***
	 * 获取分页
	 */
	public Page<Record> getPage(int pnum,int psize){
		String sql  = " from "+TABLE_NAME+" o LEFT JOIN act_hi_procinst p ON o.proc_ins_id=p.ID_  where 1=1 ";
		String userId = ShiroKit.getUserId();
		//数据权限
		sql = sql + SysRoleOrg.dao.getRoleOrgSql(userId) ;
		sql = sql + " order by o.create_time desc";
		return Db.paginate(pnum, psize, " select o.*,p.PROC_DEF_ID_ defid ", sql);
	}
	
	/***
	 * 删除
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		OaApplyBuy o = me.getById(id);
    		deleteByBuyId(o.getId());
    		o.delete();
    	}
	}
	/***
	 * 根据主表删除
	 */
	
	public void deleteByBuyId(String buyId){
		List<OaApplyBuyItem> oldItemList = OaApplyBuyItem.dao.getAllItemByBuyId(buyId);//明细信息
		for(OaApplyBuyItem i : oldItemList){
			i.delete();
		}
	}
}