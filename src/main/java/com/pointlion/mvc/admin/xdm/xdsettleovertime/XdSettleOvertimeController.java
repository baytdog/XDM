package com.pointlion.mvc.admin.xdm.xdsettleovertime;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.pointlion.mvc.common.base.BaseController;
import com.pointlion.mvc.admin.oa.workflow.WorkFlowService;
import com.pointlion.mvc.common.model.*;
import com.pointlion.mvc.common.utils.StringUtil;
import com.pointlion.mvc.common.utils.UuidUtil;
import com.pointlion.mvc.common.utils.Constants;
import com.pointlion.mvc.admin.oa.common.OAConstants;
import com.pointlion.mvc.common.utils.DateUtil;
import com.pointlion.plugin.shiro.ShiroKit;



public class XdSettleOvertimeController extends BaseController {
	public static final XdSettleOvertimeService service = XdSettleOvertimeService.me;
	public static final String STYPE="5";
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
    	XdSettleOvertime o = getModel(XdSettleOvertime.class);
		XdSettleOvertime settleOvertime = XdSettleOvertime.dao.findById(o.getId());
		String overtimeYear = o.getOvertimeYear();
		String overtimeMonth = o.getOvertimeMonth();
		o.setOvertimeYearMonth(overtimeYear+"-"+overtimeMonth);
		if(settleOvertime==null){
			o.setCreateDate(DateUtil.getCurrentTime());
			o.setCreateUser(ShiroKit.getUserId());
			o.save();
			//steps 操作记录
			XdSteps steps=new XdSteps();
			steps.setId(UuidUtil.getUUID());
			steps.setOid(o.getId());
			steps.setStype(STYPE);
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
			apprSteps.setStype(STYPE);
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
    	renderSuccess();
    }
    /***
     * edit page
     */
    public void getEditPage(){
		String id = getPara("id");
		String view = getPara("view");
		setAttr("view", view);
		XdSettleOvertime o = new XdSettleOvertime();
		SysUser user = SysUser.dao.findById(ShiroKit.getUserId());
		if(StrKit.notBlank(id)){
			o = service.getById(id);
		}else{
			o.setId(UuidUtil.getUUID());
			LocalDate now = LocalDate.now();
			int year = now.getYear();
			int month = now.getMonth().plus(1).getValue();
			o.setOvertimeMonth(String.valueOf(month).length()==1?("0"+month):String.valueOf(month));
			o.setOvertimeYear(String.valueOf(year));

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
		setAttr("formModelName",StringUtil.toLowerCaseFirstOne(XdSettleOvertime.class.getSimpleName()));
		renderIframe("edit.html");
    }
    /***
     * del
     * @throws Exception
     */
    public void delete() throws Exception{
		String ids = getPara("ids");
		service.deleteByIds(ids);
    	renderSuccess("操作成功!");
    }


    /**
     * @Method getHomeSettleOvertimeListPage
     * @Date 2023/1/15 14:41
     * @Description  获取首页列表界面
     * @Author king
     * @Version  1.0
     * @Return void
     */
	public void getHomeSettleOvertimeListPage(){
		renderIframe("homeList.html");
	}
	/**
	 * @Method getHomeSettleOvertimeList
	 * @Date 2023/1/15 14:41
	 * @Description 获取首页列表数据
	 * @Author king
	 * @Version  1.0
	 * @Return void
	 */
	public void getHomeSettleOvertimeList(){
		String curr = getPara("pageNumber");
		String pageSize = getPara("pageSize");
		String endTime = getPara("endTime","");
		String startTime = getPara("startTime","");
		String applyUser = getPara("applyUser","");
		Page<Record> page = service.getHomePageData(Integer.valueOf(curr),Integer.valueOf(pageSize));
		renderPage(page.getList(),"",page.getTotalRow());
	}

	 /**
	  * @Method openHomeSettleOvertimePage
	  * @Date 2023/1/15 14:43
	  * @Description 打开首页表单
	  * @Author king
	  * @Version  1.0
	  * @Return void
	  */
	public void openHomeSettleOvertimePage(){
		boolean rs = ShiroKit.getUserOrgId().equals("1");
		String sid = getPara("id");
		keepPara("id");
		XdSteps step = XdSteps.dao.findById(sid);
		String s = (step.getRemarks() == null ? "" : step.getRemarks());
		String oid = step.getOid();
		XdSettleOvertime o = XdSettleOvertime.dao.findById(oid);
		o.getCreateUser();
		SysUser user = SysUser.dao.findById(o.getCreateUser());
		setAttr("creatUser",user.getName());

		List<SysOrg> sysOrgs = SysOrg.dao.find("select * from sys_org where id<>'root' order by sort");
		setAttr("sysOrgs",sysOrgs);
		List<XdDict> units = XdDict.dao.find("select * from xd_dict where type ='unit' order by sortnum");
		setAttr("units",units);
		setAttr("o",o);

		setAttr("formModelName",StringUtil.toLowerCaseFirstOne(XdSettleOvertime.class.getSimpleName()));//模型名称
		renderIframe("homeEdit.html");
	}

	 /**
	  * @Method apprPass
	  * @Date 2023/1/15 14:45
	  * @Description  审批通过
	  * @Author king
	  * @Version  1.0
	  * @Return void
	  */
	public void apprPass(){
		String stepsId = getPara("stepsId");
		XdSteps steps = XdSteps.dao.findById(stepsId);
		String oid = steps.getOid();
		String suggestions = getPara("suggestions");
		XdSettleOvertime overtime = XdSettleOvertime.dao.findById(oid);
		String status = overtime.getStatus();
		if(status.equals("0")){
			XdSteps nextSteps=new XdSteps();
			nextSteps.setId(UuidUtil.getUUID());
			nextSteps.setOid(oid);
			nextSteps.setStype(STYPE);
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
			overtime.setStatus("1");//部门领导审批通过
		}else if(status.equals("1")){
			overtime.setStatus("2");//人事部审批通过
		}
		DateTimeFormatter dtf =  DateTimeFormatter.ofPattern("yyyy-MM-dd");
		String format = LocalDate.now().format(dtf);
		String remarks=overtime.getRemarks()==null?"":overtime.getRemarks();
		if(remarks.equals("")){
			remarks=suggestions+"\t-- "+ShiroKit.getUserName()+"("+format+")";
		}else{
			remarks=remarks+"\n"+suggestions+"\t-- "+ShiroKit.getUserName()+"("+format+")";
		}
		overtime.setRemarks(remarks);
		overtime.update();
		renderSuccess();

	}
	 /**
	  * @Method apprrNoPass
	  * @Date 2023/1/15 14:45
	  * @Description  审批不通过
	  * @Author king
	  * @Version  1.0
	  * @Return void
	  */
	public void apprrNoPass(){
		String stepsId = getPara("stepsId");
		String suggestions = getPara("suggestions");
		XdSteps steps = XdSteps.dao.findById(stepsId);
		String oid = steps.getOid();
		XdSettleOvertime overtime = XdSettleOvertime.dao.findById(oid);
		String status = overtime.getStatus();
		XdSteps nextSteps=new XdSteps();
		nextSteps.setId(UuidUtil.getUUID());
		nextSteps.setOid(oid);
		nextSteps.setStype(STYPE);
		XdSteps firstSteps = XdSteps.dao.findFirst("select * from  xd_steps  where stype='"+STYPE+"' and parentid is null ");
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
			overtime.setStatus("3");//部门领导审批不通过
		}else if(status.equals("1")){
			overtime.setStatus("4");//人事部审批不通过
		}

		String remarks=overtime.getRemarks()==null?"":overtime.getRemarks();
		DateTimeFormatter dtf =  DateTimeFormatter.ofPattern("yyyy-MM-dd");
		String format = LocalDate.now().format(dtf);
		if(remarks.equals("")){
			remarks=suggestions+"\t-- "+ShiroKit.getUserName()+"("+format+")";
		}else{
			remarks=remarks+"\n"+suggestions+"\t-- "+ShiroKit.getUserName()+"("+format+")";
		}

		overtime.setRemarks(remarks);
		overtime.update();

		renderSuccess();

	}

	/**
	 * @Method sendAppr
	 * @Date 2023/1/15 10:23
	 * @Description  送审批
	 * @Author king
	 * @Version  1.0
	 * @Return void
	 */
	public void sendAppr(){
		String stepsId = getPara("stepsId");
		String suggestions = getPara("suggestions");
		XdSteps steps = XdSteps.dao.findById(stepsId);
		String oid = steps.getOid();
		XdSettleOvertime overtime = XdSettleOvertime.dao.findById(oid);
		String status = overtime.getStatus();

		XdSteps nextSteps=new XdSteps();
		nextSteps.setId(UuidUtil.getUUID());
		nextSteps.setOid(oid);
		nextSteps.setStype(STYPE);
		String approverId = overtime.getApproverId();
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

		overtime.setStatus("0");
		overtime.update();

		renderSuccess();

	}



}