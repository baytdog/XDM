package com.pointlion.mvc.admin.grap.url;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.gargoylesoftware.htmlunit.WebClient;
import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.admin.grap.url.specialgetdata.SpecialGetDataInterface;
import com.pointlion.mvc.common.model.GrapUrl;
import com.pointlion.mvc.common.model.GrapWebsite;
import com.pointlion.mvc.common.utils.HtmlUnitUtil;

public class GrapUrlService{
	public static final GrapUrlService me = new GrapUrlService();
	public static final String TABLE_NAME = GrapUrl.tableName;
	
	/***
	 * 根据主键查询
	 */
	public GrapUrl getById(String id){
		return GrapUrl.dao.findById(id);
	}
	
	/***
	 * 获取分页
	 */
	public Page<Record> getPage(int pnum,int psize,String pid){
		String sql  = " from "+TABLE_NAME+" o ,"+GrapWebsite.tableName+" t where 1=1 and o.website_id=t.id";
		if(StrKit.notBlank(pid)){
			sql = sql + " and o.website_id = '"+pid+"'";
		}
		sql = sql + " order by o.create_time desc";
		return Db.paginate(pnum, psize, " select o.*,t.if_login_need_valicode,t.id websiteid ", sql);
	}
	
	/***
	 * 删除
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		GrapUrl o = me.getById(id);
    		o.delete();
    	}
	}
	
	public void getData(HttpServletRequest request,GrapUrl o,String start,String end)throws Exception{
		String websiteId = o.getWebsiteId();
		GrapWebsite w = GrapWebsite.dao.getById(websiteId);
		String implClass = o.getGetDataImplClass();
		if(StrKit.notBlank(implClass)){
			String cla = "com.pointlion.mvc.admin.grap.url.specialgetdata.impl."+implClass;
    		Class clz = Class.forName(cla);
    		Object obj = clz.newInstance();
    		SpecialGetDataInterface getdata = (SpecialGetDataInterface)obj;
    		WebClient webclient = (WebClient)request.getSession().getAttribute("webclient");
    		if("1".equals(w.getIfNeedLogin())){//是否需要登录
        		if(webclient==null){
        			throw new Exception("该链接需要登陆，请进入站点管理进行登录。");
        		}else{
        			Map<String, Integer> indexmap = getdata.getData(o,webclient,start,end);
        		}
    		}else{
    			if(webclient==null){
    				webclient = HtmlUnitUtil.getWebClient();
        		}
    			Map<String, Integer> indexmap = getdata.getData(o,webclient,start,end);
    		}
		}else{
			throw new Exception("该Url没有后台实现类");
		}
	}
}