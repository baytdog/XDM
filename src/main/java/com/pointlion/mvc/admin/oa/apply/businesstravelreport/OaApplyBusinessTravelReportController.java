package com.pointlion.mvc.admin.oa.apply.businesstravelreport;

import java.util.HashMap;
import java.util.Map;

import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.pointlion.mvc.common.base.BaseController;
import com.pointlion.mvc.admin.oa.workflow.WorkFlowService;
import com.pointlion.mvc.common.model.OaApplyBusinessTravel;
import com.pointlion.mvc.common.utils.StringUtil;
import com.pointlion.mvc.common.model.OaApplyBusinessTravelReport;
import com.pointlion.mvc.common.model.SysUser;
import com.pointlion.mvc.common.model.SysOrg;
import com.pointlion.mvc.common.utils.UuidUtil;
import com.pointlion.mvc.common.utils.Constants;
import com.pointlion.mvc.admin.oa.common.OAConstants;
import com.pointlion.mvc.common.utils.DateUtil;
import com.pointlion.plugin.shiro.ShiroKit;



public class OaApplyBusinessTravelReportController extends BaseController {
	public static final OaApplyBusinessTravelReportService service = OaApplyBusinessTravelReportService.me;
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
    	OaApplyBusinessTravelReport o = getModel(OaApplyBusinessTravelReport.class);
    	String btid = getPara("btid");
    	if(StrKit.isBlank(btid)){
    		renderError("出差申请单据信息不存在！");
		}else{
    		o.setBusinessTravelId(btid);
			if(StrKit.notBlank(o.getId())){
				o.update();
			}else{
				o.setId(UuidUtil.getUUID());
				o.setCreateTime(DateUtil.getCurrentTime());
				o.save();
			}
			renderSuccess();
		}
    }
    /***
     * edit page
     */
    public void getEditPage(){
    	String id = getPara("id");
    	String view = getPara("view");
		String btid = getPara("btid");//businessTravelId
		setAttr("view", view);
		if(StrKit.notBlank(btid)){
			OaApplyBusinessTravel bt = OaApplyBusinessTravel.dao.getById(btid);
			setAttr("bt",bt);
			setAttr("businessTravel",StringUtil.toLowerCaseFirstOne(OaApplyBusinessTravel.class.getSimpleName()));
		}
    	if(StrKit.notBlank(id)){//编辑
    		OaApplyBusinessTravelReport o = service.getById(id);
    		setAttr("o", o);
    		if("detail".equals(view)){
    			setAttr("view", view);
    			if(StrKit.notBlank(o.getProcInsId())){
    				setAttr("procInsId", o.getProcInsId());
    				setAttr("defId", wfservice.getDefIdByInsId(o.getProcInsId()));
    			}
    		}
    		//出差申请单信息
    		OaApplyBusinessTravel bt = OaApplyBusinessTravel.dao.getById(o.getBusinessTravelId());
			setAttr("bt", bt);
    		setAttr("businessTravel",StringUtil.toLowerCaseFirstOne(OaApplyBusinessTravel.class.getSimpleName()));
    	}else{//新增--暂时不允许存在，只允许从出差单列表添加
    		SysUser user = SysUser.dao.findById(ShiroKit.getUserId());
    		SysOrg org = SysOrg.dao.findById(user.getOrgid());
    		setAttr("user", user);
    		setAttr("org", org);
    	}
    	setAttr("formModelName",StringUtil.toLowerCaseFirstOne(OaApplyBusinessTravelReport.class.getSimpleName()));
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
    	OaApplyBusinessTravelReport o = OaApplyBusinessTravelReport.dao.getById(id);
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
    		OaApplyBusinessTravelReport o = OaApplyBusinessTravelReport.dao.getById(id);
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