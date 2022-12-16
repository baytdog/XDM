package com.pointlion.mvc.admin.xdm.xdworkexper;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.pointlion.mvc.admin.oa.workflow.WorkFlowService;
import com.pointlion.mvc.common.base.BaseController;
import com.pointlion.mvc.common.model.SysOrg;
import com.pointlion.mvc.common.model.SysUser;
import com.pointlion.mvc.common.model.XdEdutrain;
import com.pointlion.mvc.common.model.XdWorkExper;
import com.pointlion.mvc.common.utils.StringUtil;
import com.pointlion.plugin.shiro.ShiroKit;

import java.util.List;


public class XdWorkExperController extends BaseController {
	public static final XdWorkExperService service = XdWorkExperService.me;
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
    public void listData(){
        String curr = getPara("pageNumber");
        String pageSize = getPara("pageSize");
        String endTime = getPara("endTime","");
        String startTime = getPara("startTime","");
        String applyUser = getPara("applyUser","");
        Page<Record> page = service.getPage(Integer.valueOf(curr),Integer.valueOf(pageSize),startTime,endTime,applyUser);
        renderPage(page.getList(),"",page.getTotalRow());
    }
    /***
     * save data
     */
    public void save(){
    	XdWorkExper o = getModel(XdWorkExper.class);
    	renderSuccess();
    }
    /***
     * edit page
     */
    public void getEditPage(){
    	String id = getPara("id");
    	String view = getPara("view");
		setAttr("view", view);
		XdWorkExper o = new XdWorkExper();
		if(StrKit.notBlank(id)){
    	}else{
    		SysUser user = SysUser.dao.findById(ShiroKit.getUserId());
    		SysOrg org = SysOrg.dao.findById(user.getOrgid());
    	}
		setAttr("o", o);
    	setAttr("formModelName",StringUtil.toLowerCaseFirstOne(XdWorkExper.class.getSimpleName()));
		renderIframe("edit.html");
    }
    /***
     * del
     * @throws Exception
     */
    public void delete() throws Exception{
		String ids = getPara("ids");
		service.deleteByIds(ids);
    	renderSuccess("删除成功!");
    }
	public void getWorkExperList(){
		String employeeId = getPara("employeeId");
		List<XdWorkExper> list = service.getWorkExperList(employeeId);
		renderJson(list);
	}



}