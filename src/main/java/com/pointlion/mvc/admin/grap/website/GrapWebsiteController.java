package com.pointlion.mvc.admin.grap.website;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import com.pointlion.mvc.admin.grap.website.login.WebsiteLoginInterface;
import com.pointlion.mvc.admin.oa.workflow.WorkFlowService;
import com.pointlion.mvc.common.base.BaseController;
import com.pointlion.mvc.common.dto.ZtreeNode;
import com.pointlion.mvc.common.model.GrapWebsite;
import com.pointlion.mvc.common.model.SysOrg;
import com.pointlion.mvc.common.model.SysUser;
import com.pointlion.mvc.common.utils.*;
import com.pointlion.plugin.shiro.ShiroKit;




public class GrapWebsiteController extends BaseController {
	public static final GrapWebsiteService service = GrapWebsiteService.me;
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
    	Page<Record> page = service.getPage(Integer.valueOf(curr),Integer.valueOf(pageSize));
    	renderPage(page.getList(),"",page.getTotalRow());
    }
    /***
     * 保存
     */
    public void save(){
    	GrapWebsite o = getModel(GrapWebsite.class);
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
    		GrapWebsite o = service.getById(id);
    		setAttr("o", o);
    		//是否是查看详情页面
    	}else{
    		SysUser user = SysUser.dao.findById(ShiroKit.getUserId());
    		SysOrg org = SysOrg.dao.findById(user.getOrgid());
    		setAttr("user", user);
    		setAttr("org", org);
    	}
    	setAttr("formModelName",StringUtil.toLowerCaseFirstOne(GrapWebsite.class.getSimpleName()));//模型名称
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
     * 网站测试登录---获取验证码
     * @throws ClassNotFoundException 
     * @throws IllegalAccessException 
     * @throws InstantiationException 
     */
	public void getValiCodePage() throws Exception{
    	String id = getPara("id","");
    	if(StrKit.notBlank(id)){
    		GrapWebsite grapWebsite = GrapWebsite.dao.getById(id);
    		service.getValiCode(this.getRequest(), grapWebsite);
    	}
    	renderIframe("login/valicode.html");
    }
    
    /****
     * 检查登录
     * @throws Exception 
     */
	public void checkLogin() throws Exception{
    	String id = getPara("id","");
    	String valicode = getPara("valicode","");
    	String indexUrl = "";
    	if(StrKit.notBlank(id)){
    		GrapWebsite grapWebsite = GrapWebsite.dao.getById(id);
    		service.login(this.getRequest(), grapWebsite, valicode);
    	}
    	renderSuccess(indexUrl,"测试完毕！将要打开登录之后首页的片段，请自行判断是否登录成功！");
    }
    
    /***
     * 获取站点树
     */
    public void getWebSiteTree(){
    	List<GrapWebsite> menuList = GrapWebsite.dao.getAllWebSite();
    	List<ZtreeNode> nodelist = GrapWebsite.dao.toZTreeNode(menuList,true,false);//数据库中的菜单
    	List<ZtreeNode> rootList = new ArrayList<ZtreeNode>();//页面展示的,带根节点
    	//声明根节点
    	ZtreeNode root = new ZtreeNode();
    	root.setId(Constants.SYS_MENU_ROOT);
    	root.setName("爬取站点");
    	root.setChildren(nodelist);
    	root.setOpen(true);
    	root.setIcon(ContextUtil.getCtx()+"/common/plugins/zTree_v3/css/zTreeStyle/img/diy/9.png");
    	rootList.add(root);
    	renderJson(rootList);
    }
    /***
     * 获取弹层
     * 选择一个站点
     */
    public void selectOneWebsitePage(){
    	renderIframe("selectOneWebsite.html");
    }
}