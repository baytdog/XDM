package com.pointlion.mvc.admin.xdm.xdprojects;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.pointlion.mvc.common.base.BaseController;
import com.pointlion.mvc.admin.oa.workflow.WorkFlowService;
import com.pointlion.mvc.common.model.XdDict;
import com.pointlion.mvc.common.utils.StringUtil;
import com.pointlion.mvc.common.model.XdProjects;
import com.pointlion.mvc.common.model.SysUser;
import com.pointlion.mvc.common.model.SysOrg;
import com.pointlion.mvc.common.utils.UuidUtil;
import com.pointlion.mvc.common.utils.Constants;
import com.pointlion.mvc.admin.oa.common.OAConstants;
import com.pointlion.mvc.common.utils.DateUtil;
import com.pointlion.plugin.shiro.ShiroKit;



public class XdProjectsController extends BaseController {
	public static final XdProjectsService service = XdProjectsService.me;
	public static WorkFlowService wfservice = WorkFlowService.me;
	/***
	 * get list page
	 */
	public void getListPage(){
		renderIframe("list.html");
    }
	/***
     * list page data
     **/
    public void listData() throws UnsupportedEncodingException {
    	String curr = getPara("pageNumber");
    	String pageSize = getPara("pageSize");
		String proName = java.net.URLDecoder.decode(getPara("proName",""),"UTF-8");
    	Page<Record> page = service.getPage(Integer.valueOf(curr),Integer.valueOf(pageSize),proName);
    	renderPage(page.getList(),"",page.getTotalRow());
    }
    /***
     * save data
     */
    public void save(){
    	XdProjects o = getModel(XdProjects.class);
		List<XdProjects> xdProjects = XdProjects.dao.find("select * from xd_projects where project_name='" + o.getProjectName() + "'");
		if(xdProjects.size()>0){
    		o.update();
    	}else{
			o.setCtime(DateUtil.getCurrentTime());
			o.setCuser(ShiroKit.getUserId());
    		o.save();
    	}
    	renderSuccess();
    }
    /***
     * edit page
     */
    public void getEditPage(){
    	String id = getPara("id");
    	String view = getPara("view");
		setAttr("view", view);
		XdProjects o = new XdProjects();
		if(StrKit.notBlank(id)){
    		o = service.getById(id);
    	}else{
    		SysUser user = SysUser.dao.findById(ShiroKit.getUserId());
    		SysOrg org = SysOrg.dao.findById(user.getOrgid());
    	}


		List<SysOrg> sysOrgs = SysOrg.dao.find("select * from sys_org where id<>'root' order by sort");
		setAttr("sysOrgs",sysOrgs);
		List<XdDict> units = XdDict.dao.find("select * from xd_dict where type ='unit' order by sortnum");
		setAttr("units",units);
		setAttr("o", o);
    	setAttr("formModelName",StringUtil.toLowerCaseFirstOne(XdProjects.class.getSimpleName()));
		renderIframe("edit.html");
    }
    /***
     * del
     * @throws Exception
     */
    public void delete() throws Exception{
		String ids = getPara("ids");
		service.deleteByIds(ids);
    	renderSuccess("操作成功！");
    }

	
}