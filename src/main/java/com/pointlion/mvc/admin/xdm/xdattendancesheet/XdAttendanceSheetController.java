package com.pointlion.mvc.admin.xdm.xdattendancesheet;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.*;

import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.pointlion.enums.XdOperEnum;
import com.pointlion.mvc.common.base.BaseController;
import com.pointlion.mvc.admin.oa.workflow.WorkFlowService;
import com.pointlion.mvc.common.model.*;
import com.pointlion.mvc.common.utils.*;
import com.pointlion.mvc.admin.oa.common.OAConstants;
import com.pointlion.plugin.shiro.ShiroKit;
import com.pointlion.util.DictMapping;
import net.sf.json.JSONObject;


public class XdAttendanceSheetController extends BaseController {
	public static final XdAttendanceSheetService service = XdAttendanceSheetService.me;
	public static final String OBJECT_NAME="XdAttendanceSheet";
	public static final String STYPE="7";
	/***
	 * get list page
	 */
	public void getListPage(){
		List<SysOrg> orgList = SysOrg.dao.find("select * from  sys_org where id<>'root' order by sort");
		setAttr("orgs",orgList);
		renderIframe("list.html");
    }
	/***
     * list page data
     **/
    public void listData() throws UnsupportedEncodingException {
    	String curr = getPara("pageNumber");
    	String pageSize = getPara("pageSize");
		String year = getPara("year","");
		String month = getPara("month","");
		String deptId = getPara("deptId","");
		String empName = java.net.URLDecoder.decode(getPara("empName",""),"UTF-8");
    	Page<Record> page = service.getPage(Integer.valueOf(curr),Integer.valueOf(pageSize),deptId,year,month,empName);
    	renderPage(page.getList(),"",page.getTotalRow());
    }
    /***
     * edit page
     */
    public void getEditPage(){
    	String id = getPara("id");
    	String view = getPara("view");
		setAttr("view", view);
		XdAttendanceSheet o = new XdAttendanceSheet();
		if(StrKit.notBlank(id)){
    		o = service.getById(id);
    	}else{
    		SysUser user = SysUser.dao.findById(ShiroKit.getUserId());
    		SysOrg org = SysOrg.dao.findById(user.getOrgid());
    	}

		if (ShiroKit.getUserOrgId().equals("1")){
			setAttr("rsb","1");
		}else{
			setAttr("rsb","0");
		}
		setAttr("o", o);
    	setAttr("formModelName",StringUtil.toLowerCaseFirstOne(XdAttendanceSheet.class.getSimpleName()));
		renderIframe("edit.html");
    }



    public void  save(){
		XdAttendanceSheet o = getModel(XdAttendanceSheet.class);
		XdAttendanceSheet oldSheet = XdAttendanceSheet.dao.findById(o.getId());
		List<XdOplogSummary> summaries = XdOplogSummary.dao.find("select *from  xd_oplog_summary where oid='" + o.getId() + "'");
		for (XdOplogSummary summary : summaries) {

			summary.setLastversion(summary.getLastversion()+1);
			summary.update();
		}

		boolean rs ="1".equals(ShiroKit.getUserOrgId());
		XdOplogSummary logSummary=new XdOplogSummary();
		String uuid = UuidUtil.getUUID();
		logSummary.setId(uuid);
		logSummary.setOid(o.getId()+"");
		logSummary.setTid(o.getId()+"");
		logSummary.setTname(OBJECT_NAME);
		logSummary.setChangeb(JSONUtil.beanToJsonString(oldSheet));
		logSummary.setChangea(JSONUtil.beanToJsonString(o));
		logSummary.setOtype("U");
		logSummary.setLastversion(0);
		logSummary.setCtime(DateUtil.getCurrentTime());
		logSummary.setCuser(ShiroKit.getUserId());
		if(rs){
			logSummary.setStatus("UNAPPRO");
			logSummary.save();
			o.update();
		}else {
			logSummary.setStatus("WAITAPPRO");
			XdSteps steps=new XdSteps();
			steps.setId(UuidUtil.getUUID());
			steps.setOid(o.getId()+"");
			steps.setStype(STYPE);
			steps.setParentid("");
			steps.setStep("");
			steps.setStepdesc("");
			steps.setOrgid("1");
			steps.setUserid("");
			steps.setUsername("");
			steps.setFinished("N");
			//steps.setBackup1(otype);
			steps.setSoptye("U");
			steps.setAuditresult("WA");//待审批
			steps.setCuserid(ShiroKit.getUserId());
			steps.setCusername(ShiroKit.getUserName());
			steps.setCtime(DateUtil.getCurrentTime());
			steps.save();

			String sheetChanges = XdOperUtil.getChangedMetheds(o, oldSheet);
			sheetChanges = sheetChanges.replaceAll("--$", "");
			List<XdOplogDetail> list = new ArrayList<>();
			if (!"".equals(sheetChanges)) {
				String[] sheetCArray = sheetChanges.split("--");
				for (String change : sheetCArray) {
					change = "{" + change + "}";
					XdOplogDetail logDetail = JSONUtil.jsonToBean(change, XdOplogDetail.class);
					logDetail.setRsid(uuid);
					logDetail.setId(UuidUtil.getUUID());
					list.add(logDetail);
				}
				Db.batchSave(list,list.size());
				logSummary.save();
				oldSheet.setStatus("1");
				oldSheet.update();
			}
		}

		renderSuccess();

    }


    public void getHomeSheetListPage(){
		renderIframe("homeList.html");
	}


	public void getHomeSheetList(){
		String curr = getPara("pageNumber","");
		String pageSize = getPara("pageSize","");
		String endTime = getPara("endTime","");
		String startTime = getPara("startTime","");
		String applyUser = getPara("applyUser","");
		Page<Record> page = service.getHomePageData(Integer.valueOf(curr),Integer.valueOf(pageSize));
		renderPage(page.getList(),"",page.getTotalRow());
	}

	public void openHomeSheetPage(){
		boolean rs = ShiroKit.getUserOrgId().equals("1");
		String sid = getPara("id");
		keepPara("id");
		XdSteps step = XdSteps.dao.findById(sid);
		String s = (step.getRemarks() == null ? "" : step.getRemarks());
		setAttr("comments",s);
		String oid = step.getOid();
		XdAttendanceSheet o=XdAttendanceSheet.dao.findById(oid);
		XdOplogSummary first = XdOplogSummary.dao.findFirst("select * from xd_oplog_summary where oid='" + oid + "' and lastversion='0'");
		List<XdOplogDetail> xdOplogDetails = XdOplogDetail.dao.find("select *from  xd_oplog_detail where rsid='" + first.getId() + "'");
		System.out.println(JSONUtil.listToJson(xdOplogDetails));
		setAttr("opList",JSONUtil.listToJson(xdOplogDetails));

		setAttr("o",o);
		if(ShiroKit.getUserOrgId().equals("1")){
			setAttr("oper","approver");
		}
		setAttr("formModelName",StringUtil.toLowerCaseFirstOne(XdAttendanceSheet.class.getSimpleName()));//模型名称
		renderIframe("pEdit.html");

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

	public void exportCheckInExcel() throws UnsupportedEncodingException {

		String dept=getPara("dept","");
		String year=getPara("year","");
		String month = getPara("month","");
//		String emp_name = new String(getPara("emp_name","").getBytes("ISO-8859-1"), "utf-8");

		String path = this.getSession().getServletContext().getRealPath("")+"/upload/export/"+ DateUtil.format(new Date(),21)+".xlsx";
		File file = service.exportCheckInExcel(path,dept,year,month);
		renderFile(file);
	}

	public void pass(){
		String stepsId = getPara("stepsId");
		XdSteps steps = XdSteps.dao.findById(stepsId);
		String oid = steps.getOid();
		String comment = getPara("comment");
		steps.setFinished("Y");
		steps.setFinishtime(DateUtil.getCurrentTime());
		steps.setRemarks(comment);
		steps.update();
		XdOplogSummary first = XdOplogSummary.dao.findFirst("select * from xd_oplog_summary where oid='" + oid + "' and lastversion='0'");
		List<XdOplogDetail> xdOplogDetails = XdOplogDetail.dao.find("select *from  xd_oplog_detail where rsid='" + first.getId() + "'");
		XdAttendanceSheet sheet = JSONUtil.jsonToBean(first.getChangea(), XdAttendanceSheet.class);

		sheet.update();
		renderSuccess("");


	}
	public void sure(){
		String stepsId = getPara("stepsId");
		XdSteps steps = XdSteps.dao.findById(stepsId);
		String oid = steps.getOid();
//		String comment = getPara("comment");
		steps.setFinished("Y");
		steps.setFinishtime(DateUtil.getCurrentTime());
//		steps.setRemarks(comment);
		steps.update();
		renderSuccess("");


	}


	public void noPass(){
		String stepsId = getPara("stepsId");
		XdSteps steps = XdSteps.dao.findById(stepsId);
		String oid = steps.getOid();
		String comment = getPara("comment");
		steps.setFinished("Y");
		steps.setFinishtime(DateUtil.getCurrentTime());
		steps.setRemarks(comment);
		steps.update();
		XdSteps newStep=new XdSteps();
		newStep.setId(UuidUtil.getUUID());
		newStep.setOid(steps.getOid());
		newStep.setStype(STYPE);
		newStep.setParentid(stepsId);
		newStep.setStep("");
		newStep.setStepdesc("");
		//SysUser user = SysUser.dao.findById(steps.getCuserid());
		newStep.setUserid(steps.getCuserid());
		newStep.setUsername(steps.getCusername());
		newStep.setFinished("N");
		newStep.setSoptye("U");
		newStep.setRemarks(comment);
		newStep.setCuserid(ShiroKit.getUserId());
		newStep.setCusername(ShiroKit.getUserName());
		newStep.setCtime(DateUtil.getCurrentTime());
		newStep.save();
		renderSuccess("");

	}
}