package com.pointlion.mvc.admin.grap.website;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.admin.grap.website.login.WebsiteLoginInterface;
import com.pointlion.mvc.common.model.GrapWebsite;
import com.pointlion.mvc.common.utils.HtmlUnitUtil;

public class GrapWebsiteService{
	public static final GrapWebsiteService me = new GrapWebsiteService();
	public static final String TABLE_NAME = GrapWebsite.tableName;
	
	/***
	 * 根据主键查询
	 */
	public GrapWebsite getById(String id){
		return GrapWebsite.dao.findById(id);
	}
	
	/***
	 * 获取分页
	 */
	public Page<Record> getPage(int pnum,int psize){
		String sql  = " from "+TABLE_NAME+" o order by o.create_time desc";
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
    		GrapWebsite o = me.getById(id);
    		o.delete();
    	}
	}
	
	/***
	 * 获取验证码
	 * @param request
	 * @param grapWebsite
	 * @throws Exception
	 */
	public void getValiCode(HttpServletRequest request,GrapWebsite grapWebsite)throws Exception{
		String cla = "com.pointlion.mvc.admin.grap.website.login.impl."+grapWebsite.getLoginImplClass();
		Class clz = Class.forName(cla);
		Object obj = clz.newInstance();
		WebsiteLoginInterface login = (WebsiteLoginInterface)obj;
		Map<String, Object> result = login.getLoginValicode(grapWebsite);
		String valicode = result.get("valicode").toString();
		request.setAttribute("valicode", valicode);//验证码地址
		request.getSession().setAttribute("webclient", result.get("webclient"));
		request.getSession().setAttribute("loginpage", result.get("loginpage"));
	}
	
	/***
	 * 登录
	 * @throws Exception 
	 */
	public String login(HttpServletRequest request,GrapWebsite grapWebsite,String valicode) throws Exception{
		String indexUrl="";
		String cla = "com.pointlion.mvc.admin.grap.website.login.impl."+grapWebsite.getLoginImplClass();
		Class clz = Class.forName(cla);
		Object obj = clz.newInstance();
		WebsiteLoginInterface login = (WebsiteLoginInterface)obj;
		if("1".equals(grapWebsite.getIfLoginNeedValicode())){//需要验证码，需要从session中获取当前验证码的那个登录页。不能重新获取登录页，也证明会变化！！。
    		WebClient webclient = (WebClient)request.getSession().getAttribute("webclient");
    		HtmlPage loginpage = (HtmlPage)request.getSession().getAttribute("loginpage");
    		Map<String, Object> indexmap = login.doLogin(grapWebsite,valicode,webclient,loginpage);
    		indexUrl =  indexmap.get("indexUrl").toString();
		}else{
			WebClient webClient = HtmlUnitUtil.getWebClient();
			HtmlPage loginpage = webClient.getPage(grapWebsite.getLoginUrl());//登录页面
			Map<String, Object> indexmap = login.doLogin(grapWebsite,valicode,webClient,loginpage);
    		indexUrl =  indexmap.get("indexUrl").toString();
		}
		return indexUrl;
	}
}