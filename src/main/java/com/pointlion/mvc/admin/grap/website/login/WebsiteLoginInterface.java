package com.pointlion.mvc.admin.grap.website.login;

import java.util.Map;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.pointlion.mvc.common.model.GrapWebsite;

public interface WebsiteLoginInterface {
	/***
	 * 网站登录接口
	 */
	public Map<String,Object> doLogin(GrapWebsite grapWebsite,String valicode,WebClient webclient,HtmlPage loginpage) throws Exception;
	/***
	 * 网站登录验证码
	 * @throws Exception 
	 */
	public Map<String,Object> getLoginValicode(GrapWebsite grapWebsite) throws Exception;
}
