package com.pointlion.mvc.admin.grap.url;

import java.util.HashMap;
import java.util.Map;

import com.gargoylesoftware.htmlunit.WebClient;
import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import com.pointlion.mvc.admin.grap.url.specialgetdata.SpecialGetDataInterface;
import com.pointlion.mvc.admin.grap.website.GrapWebsiteService;
import com.pointlion.mvc.admin.oa.workflow.WorkFlowService;
import com.pointlion.mvc.common.base.BaseController;
import com.pointlion.mvc.common.model.GrapUrl;
import com.pointlion.mvc.common.model.GrapWebsite;
import com.pointlion.mvc.common.model.SysOrg;
import com.pointlion.mvc.common.model.SysUser;
import com.pointlion.mvc.common.utils.DateUtil;
import com.pointlion.mvc.common.utils.HtmlUnitUtil;
import com.pointlion.mvc.common.utils.StringUtil;
import com.pointlion.mvc.common.utils.UuidUtil;
import com.pointlion.plugin.shiro.ShiroKit;




public class GrapUrlController extends BaseController {
	public static final GrapUrlService service = GrapUrlService.me;
	public static final GrapWebsiteService webSiteService = GrapWebsiteService.me;
	public static WorkFlowService wfservice = WorkFlowService.me;
	/***
	 * 列表页面
	 */
	public void getListPage(){

    	renderIframe("list.html");
    }
	/***
     * 获取分页数据
     **/
    public void listData(){
    	String curr = getPara("pageNumber");
    	String pageSize = getPara("pageSize");
    	String pid = getPara("pid","");
    	Page<Record> page = service.getPage(Integer.valueOf(curr),Integer.valueOf(pageSize),pid);
    	renderPage(page.getList(),"",page.getTotalRow());
    }
    /***
     * 保存
     */
    public void save(){
    	GrapUrl o = getModel(GrapUrl.class);
    	if(StrKit.notBlank(o.getId())){
    		o.update();
    	}else{
    		o.setId(UuidUtil.getUUID());
    		o.setCreateTime(DateUtil.getCurrentTime());
    		o.save();
    	}
    	renderSuccess();
    }
    /***
     * 获取编辑页面
     */
    public void getEditPage(){

    	String id = getPara("id");
    	String view = getPara("view");
		setAttr("view", view);
    	if(StrKit.notBlank(id)){//修改
    		GrapUrl o = service.getById(id);
    		setAttr("o", o);
    		//是否是查看详情页面
    		if("detail".equals(view)){
    			setAttr("view", view);
    		}
    		if(StrKit.notBlank(o.getWebsiteId())){
    			GrapWebsite website = GrapWebsite.dao.getById(o.getWebsiteId());
    			if(website!=null){
    				o.put("website_name", website.getName());
    			}
    		}
    	}else{
    		SysUser user = SysUser.dao.findById(ShiroKit.getUserId());
    		SysOrg org = SysOrg.dao.findById(user.getOrgid());
    		setAttr("user", user);
    		setAttr("org", org);
    	}
    	setAttr("formModelName",StringUtil.toLowerCaseFirstOne(GrapUrl.class.getSimpleName()));//模型名称
    	renderIframe("edit.html");
    }
    /***
     * 删除
     * @throws Exception
     */
    public void delete() throws Exception{
		String ids = getPara("ids");
    	//执行删除
		service.deleteByIds(ids);
    	renderSuccess("删除成功!");
    }

    /***
     * 获取数据
     */
	public void grapUrlData()throws Exception{
    	String id = getPara("id","");
    	GrapUrl o = GrapUrl.dao.getById(id);
    	if(o!=null){
    		service.getData(this.getRequest(), o,"2018-01","2018-12");
    	}
    	renderSuccess();
    }
    
}



