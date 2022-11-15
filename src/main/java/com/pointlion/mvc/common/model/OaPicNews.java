package com.pointlion.mvc.common.model;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.base.BaseOaPicNews;
@SuppressWarnings("serial")
public class OaPicNews extends BaseOaPicNews<OaPicNews> {
	public static final OaPicNews dao = new OaPicNews();
	public static final String tableName = "oa_pic_news";
	
	/***
	 * query by id
	 */
	public OaPicNews getById(String id){
		return OaPicNews.dao.findById(id);
	}
	
	/***
	 * del
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		OaPicNews o = OaPicNews.dao.getById(id);
    		o.delete();
    	}
	}
	
	
	public Page<OaPicNews> getPage(int pnum,int psize){
		return OaPicNews.dao.paginate(pnum, psize, "select * "," FROM oa_pic_news n ORDER BY n.cdate DESC");
	}
	
}