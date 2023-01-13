package com.pointlion.mvc.admin.xdm.xdschedule;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.pointlion.mvc.admin.oa.workflow.WorkFlowService;
import com.pointlion.mvc.common.base.BaseController;
import com.pointlion.mvc.common.model.*;
import com.pointlion.mvc.common.utils.*;
import com.pointlion.plugin.shiro.ShiroKit;
import com.pointlion.util.DictMapping;
import org.joda.time.format.DateTimeFormat;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


public class XdScheduleController extends BaseController {
	public static final XdScheduleService service = XdScheduleService.me;
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
    	XdSchedule o = getModel(XdSchedule.class);
		XdSchedule schedule = XdSchedule.dao.findById(o.getId());
		String sheduleYear = o.getSheduleYear();
		String sheduleMonth = o.getSheduleMonth();
		o.setSheduleYearMonth(sheduleYear+"-"+sheduleMonth);
		if(schedule==null){
			o.setCreateDate(DateUtil.getCurrentTime());
			o.setCreateUser(ShiroKit.getUserId());
			o.save();
			//steps 操作记录
			XdSteps steps=new XdSteps();
			steps.setId(UuidUtil.getUUID());
			steps.setOid(o.getId());
			steps.setStype("2");
			steps.setUserid(ShiroKit.getUserId());
			steps.setUsername(ShiroKit.getUsername());
			steps.setOrgid(ShiroKit.getUserOrgId());
			steps.setOrgname(ShiroKit.getUserOrgName());
			steps.setFinished("Y");
			steps.setFinishtime(DateUtil.getCurrentTime());
			steps.setCuserid(ShiroKit.getUserId());
			steps.setCusername(ShiroKit.getUserName());
			steps.setCtime(DateUtil.getCurrentTime());

			steps.save();
			XdSteps apprSteps=new XdSteps();
			apprSteps.setId(UuidUtil.getUUID());
			apprSteps.setOid(o.getId());
			apprSteps.setStype("2");
			apprSteps.setUserid(o.getApproverId());
			apprSteps.setUsername(o.getApproverName());
			SysUser apprUser = SysUser.dao.findById(o.getApproverId());
			SysOrg apprOrg = SysOrg.dao.findById(apprUser.getOrgid());
			apprSteps.setOrgid(apprOrg.getId());
			apprSteps.setOrgname(apprOrg.getName());
			apprSteps.setFinished("N");
			apprSteps.setParentid(steps.getId());
			apprSteps.setCuserid(ShiroKit.getUserId());
			apprSteps.setCusername(ShiroKit.getUserName());
			apprSteps.setCtime(DateUtil.getCurrentTime());
			apprSteps.save();




		}else{
			o.update();
		}
		/*if(StrKit.notBlank(o.getId())){
    		o.update();
    	}else{
    		o.setId(UuidUtil.getUUID());
    		o.save();
    	}*/
    	renderSuccess();
    }
    /***
     * edit page
     */
    public void getEditPage(){
    	String id = getPara("id");
    	String view = getPara("view");
		setAttr("view", view);
		XdSchedule o = new XdSchedule();
		SysUser user = SysUser.dao.findById(ShiroKit.getUserId());
		if(StrKit.notBlank(id)){
    		o = service.getById(id);
    	}else{
			o.setId(UuidUtil.getUUID());
			LocalDate now = LocalDate.now();
			int year = now.getYear();
			int month = now.getMonth().plus(1).getValue();
			o.setSheduleMonth(String.valueOf(month).length()==1?("0"+month):String.valueOf(month));
			o.setSheduleYear(String.valueOf(year));

			SysOrg org = SysOrg.dao.findById(user.getOrgid());
			o.setDeptValue(org.getId());
			o.setDeptName(org.getName());
    	}

		List<SysUser> sysUsers = SysUser.dao.find("select * from sys_user where orgid='" + user.getOrgid()
				+ "' and position in('1')");
		setAttr("sysUsers",sysUsers);
		List<SysOrg> sysOrgs = SysOrg.dao.find("select * from sys_org where id<>'root' order by sort");
		setAttr("sysOrgs",sysOrgs);




		setAttr("o", o);
    	setAttr("formModelName",StringUtil.toLowerCaseFirstOne(XdSchedule.class.getSimpleName()));
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
	 * @Method getHomeScheduleListPage
	 * @Date 2023/1/12 13:23
	 * @Description  首页加班申请列表
	 * @Author king
	 * @Version  1.0
	 * @Return void
	 */
    public void getHomeScheduleListPage(){
		renderIframe("homeList.html");
	}
	/**
	 * @Method getHomeScheduleList
	 * @Date 2023/1/12 13:23
	 * @Description  首页加班申请列表数据
	 * @Author king
	 * @Version  1.0
	 * @Return void
	 */
	public void getHomeScheduleList(){
		String curr = getPara("pageNumber");
		String pageSize = getPara("pageSize");
		String endTime = getPara("endTime","");
		String startTime = getPara("startTime","");
		String applyUser = getPara("applyUser","");
		Page<Record> page = service.getHomePageData(Integer.valueOf(curr),Integer.valueOf(pageSize));
		renderPage(page.getList(),"",page.getTotalRow());
	}
	/**
	 * @Method openSchedulePage
	 * @Date 2023/1/12 13:24
	 * @Description 首页加班申请列表打开详情页
	 * @Author king
	 * @Version  1.0
	 * @Return void
	 */
	public void openSchedulePage(){
		boolean rs = ShiroKit.getUserOrgId().equals("1");
		String sid = getPara("id");
		keepPara("id");
		XdSteps step = XdSteps.dao.findById(sid);
		String s = (step.getRemarks() == null ? "" : step.getRemarks());
		String oid = step.getOid();
		XdSchedule o = XdSchedule.dao.findById(oid);
		o.getCreateUser();
		SysUser user = SysUser.dao.findById(o.getCreateUser());
		setAttr("creatUser",user.getName());



		List<SysOrg> sysOrgs = SysOrg.dao.find("select * from sys_org where id<>'root' order by sort");
		setAttr("sysOrgs",sysOrgs);
		List<XdDict> units = XdDict.dao.find("select * from xd_dict where type ='unit' order by sortnum");
		setAttr("units",units);
		setAttr("o",o);

/*		List<SysUser> sysUsers = SysUser.dao.find("select * from sys_user where orgid='" + user.getOrgid()
				+ "' and position in('1')");*/
	/*	setAttr("sysUsers",sysUsers);*/
		setAttr("formModelName",StringUtil.toLowerCaseFirstOne(XdSchedule.class.getSimpleName()));//模型名称
		renderIframe("homeEdit.html");
	}

	/**
	 * @Method apprPass
	 * @Date 2023/1/12 14:43
	 * @Description  加班计划审批通过
	 * @Author king
	 * @Version  1.0
	 * @Return void
	 */
	public void apprPass(){
		String stepsId = getPara("stepsId");
		XdSteps steps = XdSteps.dao.findById(stepsId);
		String oid = steps.getOid();
		String suggestions = getPara("suggestions");
		XdSchedule xdSchedule = XdSchedule.dao.findById(oid);
		String status = xdSchedule.getStatus();
		if(status.equals("0")){
			XdSteps nextSteps=new XdSteps();
			nextSteps.setId(UuidUtil.getUUID());
			nextSteps.setOid(oid);
			nextSteps.setStype("2");
			nextSteps.setParentid(stepsId);
			SysUser huReDept = SysUser.dao.findFirst("select * from sys_user where unit_name='人力资源部'");
			nextSteps.setOrgid(huReDept.getOrgid());
			nextSteps.setFinished("N");
			nextSteps.setCuserid(ShiroKit.getUserId());
			nextSteps.setCusername(ShiroKit.getUserName());
			nextSteps.setCtime(DateUtil.getCurrentTime());
			nextSteps.save();
		}


		if(status.equals("1")){
			steps.setUserid(ShiroKit.getUserId());
			steps.setUsername(ShiroKit.getUserName());
		}
		steps.setRemarks(suggestions);
		steps.setFinishtime(DateUtil.getCurrentTime());
		steps.setAuditresult("D");//通过
		steps.setStep(status);
		steps.setFinished("Y");
		steps.update();

		if(status.equals("0")){
			xdSchedule.setStatus("1");//部门领导审批通过
		}else if(status.equals("1")){
			xdSchedule.setStatus("2");//人事部审批通过
		}
		DateTimeFormatter dtf =  DateTimeFormatter.ofPattern("yyyy-MM-dd");
		String format = LocalDate.now().format(dtf);
		String remarks=xdSchedule.getRemarks()==null?"":xdSchedule.getRemarks();
		//if(suggestions!=null && !suggestions.equals("")){
			if(remarks.equals("")){
				remarks=suggestions+"\t-- "+ShiroKit.getUserName()+"("+format+")";
			}else{
				remarks=remarks+"\n"+suggestions+"\t-- "+ShiroKit.getUserName()+"("+format+")";
			}
		//}
		xdSchedule.setRemarks(remarks);
		xdSchedule.update();
		renderSuccess();

	}
	/**
	 * @Method apprrNoPass
	 * @Date 2023/1/12 14:44
	 * @Description  加班计划审批不通过
	 * @Author king
	 * @Version  1.0
	 * @Return void
	 */
	public void apprrNoPass(){
		String stepsId = getPara("stepsId");
		String suggestions = getPara("suggestions");
		XdSteps steps = XdSteps.dao.findById(stepsId);
		String oid = steps.getOid();
		XdSchedule xdSchedule = XdSchedule.dao.findById(oid);
		String status = xdSchedule.getStatus();
		XdSteps nextSteps=new XdSteps();
		nextSteps.setId(UuidUtil.getUUID());
		nextSteps.setOid(oid);
		nextSteps.setStype("2");
		XdSteps firstSteps = XdSteps.dao.findFirst("select * from  xd_steps  where stype='2' and parentid is null ");
		nextSteps.setUserid(firstSteps.getUserid());
		nextSteps.setUsername(firstSteps.getUsername());
		nextSteps.setOrgid(firstSteps.getOrgid());
		nextSteps.setOrgname(firstSteps.getOrgname());
		nextSteps.setParentid(stepsId);
		nextSteps.setFinished("N");
		nextSteps.setCuserid(ShiroKit.getUserId());
		nextSteps.setCusername(ShiroKit.getUserName());
		nextSteps.setCtime(DateUtil.getCurrentTime());
		nextSteps.save();
		steps.setRemarks(suggestions);
		steps.setFinishtime(DateUtil.getCurrentTime());
		steps.setFinished("Y");
		steps.setAuditresult("B");//不通过
		steps.setStep(status);
		steps.update();

		if(status.equals("0")){
			xdSchedule.setStatus("3");//部门领导审批不通过
		}else if(status.equals("1")){
			xdSchedule.setStatus("4");//人事部审批不通过
		}

		String remarks=xdSchedule.getRemarks()==null?"":xdSchedule.getRemarks();
		DateTimeFormatter dtf =  DateTimeFormatter.ofPattern("yyyy-MM-dd");
		String format = LocalDate.now().format(dtf);
		if(remarks.equals("")){
			remarks=suggestions+"\t-- "+ShiroKit.getUserName()+"("+format+")";
		}else{
			remarks=remarks+"\n"+suggestions+"\t-- "+ShiroKit.getUserName()+"("+format+")";
		}

		xdSchedule.setRemarks(remarks);
		xdSchedule.update();

		renderSuccess();

	}
	/**
	 * @Method sendAppr
	 * @Date 2023/1/12 16:43
	 * @Description 审批不通过继续送审批
	 * @Author king
	 * @Version  1.0
	 * @Return void
	 */
	public void sendAppr(){
		String stepsId = getPara("stepsId");
		String suggestions = getPara("suggestions");
		XdSteps steps = XdSteps.dao.findById(stepsId);
		String oid = steps.getOid();
		XdSchedule xdSchedule = XdSchedule.dao.findById(oid);
		String status = xdSchedule.getStatus();

		XdSteps nextSteps=new XdSteps();
		nextSteps.setId(UuidUtil.getUUID());
		nextSteps.setOid(oid);
		nextSteps.setStype("2");
		String approverId = xdSchedule.getApproverId();
		SysUser apprUser = SysUser.dao.findById(approverId);

		nextSteps.setUserid(apprUser.getId());
		nextSteps.setUsername(apprUser.getName());
		nextSteps.setOrgid(apprUser.getOrgid());
		SysOrg appOrg = SysOrg.dao.findById(apprUser.getOrgid());
		nextSteps.setOrgname(appOrg.getName());
		nextSteps.setParentid(stepsId);
		nextSteps.setFinished("N");
		nextSteps.setCuserid(ShiroKit.getUserId());
		nextSteps.setCusername(ShiroKit.getUserName());
		nextSteps.setCtime(DateUtil.getCurrentTime());
		nextSteps.save();
		steps.setRemarks(suggestions);
		steps.setFinishtime(DateUtil.getCurrentTime());
		steps.setFinished("Y");
		steps.setStep(status);
		steps.update();

		/*if(status.equals("0")){
			xdSchedule.setStatus("3");//部门领导审批不通过
		}else if(status.equals("1")){
			xdSchedule.setStatus("4");//人事部审批不通过
		}*/
		xdSchedule.setStatus("0");
		xdSchedule.update();

		renderSuccess();

	}
	
}