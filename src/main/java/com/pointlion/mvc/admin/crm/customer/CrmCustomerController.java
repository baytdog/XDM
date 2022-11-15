package com.pointlion.mvc.admin.crm.customer;

import java.util.List;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.pointlion.mvc.admin.crm.company.CrmCustomerCompanyService;
import com.pointlion.mvc.admin.oa.workflow.WorkFlowService;
import com.pointlion.mvc.common.base.BaseController;
import com.pointlion.mvc.common.model.CrmCustomer;
import com.pointlion.mvc.common.model.CrmCustomerCompany;
import com.pointlion.mvc.common.model.SysOrg;
import com.pointlion.mvc.common.model.SysUser;
import com.pointlion.mvc.common.utils.Constants;
import com.pointlion.mvc.common.utils.DateUtil;
import com.pointlion.mvc.common.utils.StringUtil;
import com.pointlion.mvc.common.utils.UuidUtil;
import com.pointlion.plugin.shiro.ShiroKit;



public class CrmCustomerController extends BaseController {
	public static final CrmCustomerService service = CrmCustomerService.me;
	public static final CrmCustomerCompanyService companyService = CrmCustomerCompanyService.me;
	public static WorkFlowService wfservice = WorkFlowService.me;
	/***
	 * get list page
	 */
	public void getListPage(){
		List<CrmCustomerCompany> companyList = companyService.findCanUseCompany();
		setAttr("companyList",companyList);
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
		String customerName = getPara("customerName","");
		String companyId = getPara("companyId","");

    	Page<Record> page = service.getPage(Integer.valueOf(curr),Integer.valueOf(pageSize),startTime,endTime,customerName,companyId);
    	renderPage(page.getList(),"",page.getTotalRow());
    }
    /***
     * save data
     */
    public void save(){
    	CrmCustomer o = getModel(CrmCustomer.class);
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
     * edit page
     */
    public void getEditPage(){
    	String id = getPara("id");
    	String view = getPara("view");
    	String companyId = getPara("companyId","");
		setAttr("view", view);
		CrmCustomer o = new CrmCustomer();
    	if(StrKit.notBlank(id)){//编辑---可能会有流程信息
    		o = service.getById(id);
    		if("detail".equals(view)){
    			setAttr("view", view);
    			if(StrKit.notBlank(o.getProcInsId())){
    				setAttr("procInsId", o.getProcInsId());
    				setAttr("defId", wfservice.getDefIdByInsId(o.getProcInsId()));
    			}
    		}
    	}else{//新增---默认创建人，创建人部门
    		SysUser user = SysUser.dao.findById(ShiroKit.getUserId());
    		SysOrg org = SysOrg.dao.findById(user.getOrgid());
    		o.setUserid(user.getId());
    		o.setCreateUserName(user.getName());
    		o.setOrgId(org.getId());
    		o.setCreateOrgName(org.getName());
    		setAttr("user", user);
    		setAttr("org", org);
    	}
    	//赋值公司信息
		if(StrKit.notBlank(companyId)){
			CrmCustomerCompany company = CrmCustomerCompany.dao.getById(companyId);
			o.setCompanyId(company.getId());
			o.setCompanyName(company.getName());
		}
		setAttr("o", o);
    	setAttr("formModelName",StringUtil.toLowerCaseFirstOne(CrmCustomer.class.getSimpleName()));
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
    /***
     * submit
     */
    public void startProcess(){
    	String id = getPara("id");
    	CrmCustomer o = CrmCustomer.dao.getById(id);
    	o.setIfSubmit(Constants.IF_SUBMIT_YES);
   		String insId = wfservice.startProcess(id, o,null,null);
    	o.setProcInsId(insId);
    	o.update();
    	renderSuccess("提交成功");
    }
    /***
     * callBack
     */
    public void callBack(){
    	String id = getPara("id");
    	try{
    		CrmCustomer o = CrmCustomer.dao.getById(id);
        	wfservice.callBack(o.getProcInsId());
        	o.setIfSubmit(Constants.IF_SUBMIT_NO);
        	o.setProcInsId("");
        	o.update();
    		renderSuccess("撤回成功");
    	}catch(Exception e){
    		e.printStackTrace();
    		renderError("撤回失败");
    	}
    }

	
}