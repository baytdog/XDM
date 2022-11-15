package com.pointlion.mvc.admin.grap.url.bus;

import java.util.ArrayList;
import java.util.List;

import com.jfinal.aop.Clear;
import com.jfinal.kit.StrKit;
import com.pointlion.mvc.admin.grap.url.GrapUrlService;
import com.pointlion.mvc.admin.grap.website.GrapWebsiteService;
import com.pointlion.mvc.admin.oa.workflow.WorkFlowService;
import com.pointlion.mvc.common.base.BaseController;
import com.pointlion.mvc.common.model.GrapUrl;
import com.pointlion.mvc.common.model.GrapWebsite;
import com.pointlion.mvc.common.utils.DateUtil;



@Clear
public class GrapBusinessController extends BaseController {
	public static final GrapUrlService service = GrapUrlService.me;
	public static final GrapWebsiteService webSiteService = GrapWebsiteService.me;
	public static WorkFlowService wfservice = WorkFlowService.me;
	
    /****
     * 齐鲁石化----房产税----验证码
     */
    public void getQilushihuaFangchanshuiValiCode()throws Exception{
    	String id = getPara("id","");
    	if(StrKit.notBlank(id)){
    		GrapUrl o = GrapUrl.dao.getById(id);
    		GrapWebsite grapWebsite = GrapWebsite.dao.getById(o.getWebsiteId());
    		webSiteService.getValiCode(this.getRequest(), grapWebsite);
    		setAttr("id", o.getId());//主键
    	}
    	renderIframe("qilushihuafangchanshui/valicode.html");
    }
    /***
     * 齐鲁石化----房产税----取数
     */
    public void qilushihuaFangchanshuiGetData()throws Exception{
    	String id = getPara("id","");
    	String valicode = getPara("valicode","");
    	if(StrKit.notBlank(id)){
    		GrapUrl o = GrapUrl.dao.getById(id);
    		GrapWebsite grapWebsite = GrapWebsite.dao.getById(o.getWebsiteId());
    		webSiteService.login(this.getRequest(), grapWebsite, valicode);//尝试登录
        	if(o!=null){
        		service.getData(this.getRequest(), o,"","");
        	}
    	}
    	renderSuccess();
    }
    
    /****
     * 齐鲁石化----环保税----验证码
     */
    public void getQilushihuaHuanbaoshuiValiCode()throws Exception{
    	String id = getPara("id","");
    	if(StrKit.notBlank(id)){
    		GrapUrl o = GrapUrl.dao.getById(id);
    		GrapWebsite grapWebsite = GrapWebsite.dao.getById(o.getWebsiteId());
    		webSiteService.getValiCode(this.getRequest(), grapWebsite);
    		setAttr("id", o.getId());//主键
    	}
    	String year = DateUtil.getCurrentYear();
    	List<Integer> yearList = new ArrayList<Integer>();
    	Integer y = Integer.parseInt(year);
    	for(int i =0;i<=5;i++){
    		yearList.add(y-i);
    	}
    	setAttr("yearList", yearList);
    	renderIframe("qilushihuahuanbaoshui/valicode.html");
    }
    /***
     * 齐鲁石化----环保税----取数
     */
    public void qilushihuaHuanbaoshuiGetData()throws Exception{
    	String id = getPara("id","");
    	String valicode = getPara("valicode","");
    	String start = getPara("start","");
    	String end = getPara("end","");
    	if(StrKit.notBlank(id)){
    		GrapUrl o = GrapUrl.dao.getById(id);
    		GrapWebsite grapWebsite = GrapWebsite.dao.getById(o.getWebsiteId());
    		webSiteService.login(this.getRequest(), grapWebsite, valicode);//尝试登录
        	if(o!=null){
        		service.getData(this.getRequest(), o,start,end);
        	}
    	}
    	renderSuccess();
    }
    
}



