package com.pointlion.mvc.admin.xdm.xdrecords;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.pointlion.mvc.admin.oa.workflow.WorkFlowService;
import com.pointlion.mvc.common.base.BaseController;
import com.pointlion.mvc.common.model.SysOrg;
import com.pointlion.mvc.common.model.SysUser;
import com.pointlion.mvc.common.model.XdEdutrain;
import com.pointlion.mvc.common.model.XdRecords;
import com.pointlion.mvc.common.utils.StringUtil;
import com.pointlion.plugin.shiro.ShiroKit;

import java.util.List;


public class XdRecordsController extends BaseController {
	public static final XdRecordsService service = XdRecordsService.me;
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
    	XdRecords o = getModel(XdRecords.class);
    	renderSuccess();
    }
    /***
     * edit page
     */
    public void getEditPage(){
    	String id = getPara("id");
    	String view = getPara("view");
		setAttr("view", view);
		XdRecords o = new XdRecords();
		if(StrKit.notBlank(id)){
    		o = service.getById(id);
    	}else{
    		SysUser user = SysUser.dao.findById(ShiroKit.getUserId());
    		SysOrg org = SysOrg.dao.findById(user.getOrgid());
    	}
		setAttr("o", o);
    	setAttr("formModelName",StringUtil.toLowerCaseFirstOne(XdRecords.class.getSimpleName()));
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

	/**
	 * @Method getRecordsList
	 * @Date 2023/1/6 16:41
	 * @Description 获取薪资岗位记录
	 * @Author king
	 * @Version  1.0
	 * @Return void
	 */
	public void getRecordsList(){
		String employeeId = getPara("employeeId");
		String filedType = getPara("filedType");
		List<XdRecords> list = service.getRecordsList(employeeId,filedType);
		renderJson(list);
	}
	
}