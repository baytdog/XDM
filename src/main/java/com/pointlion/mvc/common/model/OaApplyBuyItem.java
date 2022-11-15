package com.pointlion.mvc.common.model;

import java.util.List;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseOaApplyBuyItem;
@SuppressWarnings("serial")
public class OaApplyBuyItem extends BaseOaApplyBuyItem<OaApplyBuyItem> {
	public static final OaApplyBuyItem dao = new OaApplyBuyItem();
	
	/***
	 * 根据主键查询
	 */
	public OaApplyBuyItem getById(String id){
		return OaApplyBuyItem.dao.findById(id);
	}
	
	/***
	 * 获取分页
	 */
	public Page<Record> getPage(int pnum,int psize){
		String sql  = " from oa_apply_buy_item o ";
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
    		OaApplyBuyItem o = OaApplyBuyItem.dao.getById(id);
    		o.delete();
    	}
	}
	
	/***
	 * 根据采购id获取所有采购项目 
	 * @param buyId
	 * @return
	 */
	public List<OaApplyBuyItem> getAllItemByBuyId(String buyId){
		return OaApplyBuyItem.dao.find("select * from oa_apply_buy_item b where b.buy_id='"+buyId+"'");
	}
	
}