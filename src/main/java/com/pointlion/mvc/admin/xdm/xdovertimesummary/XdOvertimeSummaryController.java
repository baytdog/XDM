package com.pointlion.mvc.admin.xdm.xdovertimesummary;

import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.upload.UploadFile;
import com.pointlion.mvc.common.base.BaseController;
import com.pointlion.mvc.common.model.*;
import com.pointlion.mvc.common.utils.DateUtil;
import com.pointlion.mvc.common.utils.StringUtil;
import com.pointlion.mvc.common.utils.office.excel.ExcelUtil;
import com.pointlion.plugin.shiro.ShiroKit;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.List;
import java.util.Map;


public class XdOvertimeSummaryController extends BaseController {
	public static final XdOvertimeSummaryService service = XdOvertimeSummaryService.me;
	/***
	 * get list page
	 */
	public void getListPage(){

		List<XdProjects> projects = XdProjects.dao.find("select * from  xd_projects");
		setAttr("projects",projects);


		renderIframe("list.html");
    }

	public void getSettleListPage(){

		List<XdProjects> projects = XdProjects.dao.find("select * from  xd_projects");
		setAttr("projects",projects);


		renderIframe("settleList.html");
	}


	public void getSettleEditPage(){
		String id = getPara("id");
		String view = getPara("view");
		setAttr("view", view);
		XdOvertimeSummary summary =service.getById(id);
		setAttr("o", summary);
		List<XdEmployee> emps = XdEmployee.dao.find("select * from  xd_employee order by empnum");
		setAttr("emps",emps);
		List<SysOrg> orgList = SysOrg.dao.find("select * from  sys_org");
		setAttr("orgList",orgList);
		List<XdProjects> projects = XdProjects.dao.find("select * from  xd_projects");

		if(!ShiroKit.getUserOrgId().equals("1")){
			XdAttendanceSummary first = XdAttendanceSummary.dao.findFirst("select * from  xd_attendance_summary  " +
					"where dept_value='" + ShiroKit.getUserOrgId() + "' and STATUS='1' order by schedule_year_month desc");

			LocalDate localDate = LocalDate.parse(first.getScheduleYear() + "-" + first.getScheduleMonth() + "-01").plusMonths(1);

			String format = DateTimeFormatter.ofPattern("yyyy-MM-dd").format(localDate);
			setAttr("startD",format);


			XdScheduleSummary end = XdScheduleSummary.dao.findFirst("select * from  xd_schedule_summary  " +
					"where dept_value='" + ShiroKit.getUserOrgId() + "'  order by schedule_year_month desc");
			String scheduleYearMonth = end.getScheduleYearMonth();
			XdDayModel dayModel = XdDayModel.dao.findFirst("select *from  xd_day_model where id like '%" + scheduleYearMonth + "%' order by id desc");
			setAttr("endD",dayModel.getDays());
		}else{
			setAttr("startD","2023-01-01");
			XdScheduleSummary end = XdScheduleSummary.dao.findFirst("select * from  xd_schedule_summary    order by schedule_year_month desc");
			String scheduleYearMonth = end.getScheduleYearMonth();
			XdDayModel dayModel = XdDayModel.dao.findFirst("select *from  xd_day_model where id like '%" + scheduleYearMonth + "%' order by id desc");
			setAttr("endD",dayModel.getDays());
		}
		setAttr("projects",projects);
		setAttr("formModelName",StringUtil.toLowerCaseFirstOne(XdOvertimeSummary.class.getSimpleName()));
		renderIframe("settleEdit.html");
	}

	/***
     * list page data
     **/
    public void listData() throws UnsupportedEncodingException {
		System.out.println(getPara("otType"));
		String otType  =getPara("otType");
		String curr = getPara("pageNumber");
    	String pageSize = getPara("pageSize");
		String dept = getPara("dept","");
		String project = getPara("project","");
		System.out.println(getPara("emp_name", ""));
		String empName = java.net.URLDecoder.decode(getPara("emp_name",""),"UTF-8");

		String emp_num = getPara("emp_num","");
		String apply_date = getPara("apply_date","");
		String overtimeType = getPara("overtimeType","");
    	Page<Record> page = service.getPage(Integer.valueOf(curr),Integer.valueOf(pageSize),dept,project,empName,emp_num,apply_date,overtimeType,otType);
    	renderPage(page.getList(),"",page.getTotalRow());
    }
    /***
     * save data
     */
    public void save()  {
    	XdOvertimeSummary o = getModel(XdOvertimeSummary.class);

		String[] startHM = o.getApplyStart().split(":");
		String[] endHM = o.getApplyEnd().split(":");

		LocalDateTime startTime = LocalDateTime.of(2023, 01, 01, Integer.parseInt(startHM[0]), Integer.parseInt(startHM[1]), 00);
		LocalDateTime endTime = LocalDateTime.of(2023, 01, 01, Integer.parseInt(endHM[0]), Integer.parseInt(endHM[1]), 00);

		o.setApplyHours(String.format("%.1f",( Duration.between(startTime, endTime).toMinutes()/ 60.0)));
    	
		String days = o.getApplyDate();
		String[] ymd=null;
		if (days != null) {
			ymd = days.split("-");
		}

		if(o.getId()!=null){

			XdOvertimeSummary summary = XdOvertimeSummary.dao.findById(o.getId());

			String applyDate = summary.getApplyDate();

			String[] appDateArr = applyDate.split("-");

			int oldIndex = Integer.parseInt(appDateArr[2]);

			XdScheduleSummary scheduleSummary =
					XdScheduleSummary.dao.findFirst("select * from  xd_schedule_summary where emp_name='"+summary.getEmpName()
							+"' and schedule_year='" + appDateArr[0]+ "' and schedule_month='" + appDateArr[1] + "'");
			String[] oldTips = scheduleSummary.getTips().split(",");

			String oldTip = oldTips[oldIndex];
			oldTip=oldTip.replaceAll(summary.getApplyStart()+"-"+summary.getApplyEnd(),"");
			if("".equals(oldTip)){
				oldTip="0";
			}
			oldTips[oldIndex]=oldTip;
			oldTip="";
			for (String tip : oldTips) {
				oldTip=oldTip+tip+",";
			}
			scheduleSummary.setTips(oldTip.replaceAll(",$",""));
			if("0".equals(summary.getApplyType())){
				//Double aDouble = Double.valueOf(summary.getApplyHours());
				scheduleSummary.setNatOthours(scheduleSummary.getNatOthours()-Double.valueOf(summary.getApplyHours()));
			}else{
				scheduleSummary.setOthours(scheduleSummary.getOthours()-Double.valueOf(summary.getApplyHours()));
			}
			scheduleSummary.update();


			scheduleSummary =XdScheduleSummary.dao.findFirst("select * from  xd_schedule_summary where emp_name='"+o.getEmpName()
					+"' and schedule_year='" + ymd[0]+ "' and schedule_month='" + ymd[1] + "'");
			int index = Integer.parseInt(ymd[2]);
			String tips = scheduleSummary.getTips();
			String[] tipsArr = tips.split(",");
			String indexTip = tipsArr[index];
			if("0".equals(indexTip)){
				tipsArr[index]=o.getApplyStart()+"-"+o.getApplyEnd();
			}else{
				tipsArr[index]=tipsArr[index]+"、"+o.getApplyStart()+"-"+o.getApplyEnd();
			}
			String sb="";
			for (String s : tipsArr) {
				sb=sb+s+",";
			}



			scheduleSummary.setTips(sb.replaceAll(",$",""));

			String[] otFlags = scheduleSummary.getOtflags().split(",");

			if("0".equals(tipsArr[index])){
				otFlags[index]="0";
			}else{
				otFlags[index]="1";
			}

			String ot="";
			for (String s : otFlags) {
				ot=ot+s+",";
			}
			scheduleSummary.setOtflags(ot.replaceAll(",$",""));

			if("0".equals(o.getApplyType())){
				//Double aDouble = Double.valueOf(summary.getApplyHours());
				scheduleSummary.setNatOthours(scheduleSummary.getNatOthours()+Double.valueOf(o.getApplyHours()));
			}else{
				scheduleSummary.setOthours(scheduleSummary.getOthours()+Double.valueOf(o.getApplyHours()));
			}
			scheduleSummary.update();

			o.update();
    	}else{
			int index = Integer.parseInt(ymd[2]);
			XdScheduleSummary scheduleSummary =
					XdScheduleSummary.dao.findFirst("select * from  xd_schedule_summary where emp_name='"+o.getEmpName()
							+"' and schedule_year='" + ymd[0]+ "' and schedule_month='" + ymd[1] + "'");
			if(scheduleSummary!=null){
				String tips = scheduleSummary.getTips();
				String[] tipsArr = tips.split(",");
				String indexTip = tipsArr[index];
				if("0".equals(indexTip)){
					tipsArr[index]=o.getApplyStart()+"-"+o.getApplyEnd();
				}else{
					tipsArr[index]=tipsArr[index]+"、"+o.getApplyStart()+"-"+o.getApplyEnd();
				}
				String sb="";
				for (String s : tipsArr) {
					sb=sb+s+",";
				}
				if("0".equals(o.getApplyType())){
					Double natOthours = scheduleSummary.getNatOthours();
					scheduleSummary.setNatOthours(natOthours + Double.valueOf(o.getApplyHours()));
				}else{
					Double othours = scheduleSummary.getOthours();
					scheduleSummary.setOthours(othours + Double.valueOf(o.getApplyHours()));
				}



				String[] otFlags = scheduleSummary.getOtflags().split(",");

				if("0".equals(tipsArr[index])){
					otFlags[index]="0";
				}else{
					otFlags[index]="1";
				}

				String ot="";
				for (String s : otFlags) {
					ot=ot+s+",";
				}
				scheduleSummary.setOtflags(ot.replaceAll(",$",""));


				scheduleSummary.setTips(sb.replaceAll(",$",""));
				scheduleSummary.update();
			}


			//tipsArr[index]=

			XdOvertimeSummary first = XdOvertimeSummary.dao.findFirst("select *from  xd_overtime_summary  " +
					"where source='2' and act_start='" + o.getApplyStart() + "' and apply_date='" + o.getApplyDate()
					+ "' and  act_end='" + o.getApplyEnd() + "' and apply_type='" + o.getApplyType() + "'");
			if(first==null){
				o.setSource("2");
				o.setCreateDate(DateUtil.getCurrentTime());
				o.setCreateUser(ShiroKit.getUserId());
				o.setSuperDays(o.getApplyType());
				o.save();
			}else{
				first.setApplyStart(o.getApplyStart());
				first.setApplyEnd(o.getApplyEnd());
				first.setApplyHours(o.getApplyHours());
				first.update();
			}

    	}
    	renderSuccess();
    }

	public void saveSettle(){
		XdOvertimeSummary o = getModel(XdOvertimeSummary.class);

		String[] startHM = o.getActStart().split(":");
		String[] endHM = o.getActEnd().split(":");

		LocalDateTime startTime = LocalDateTime.of(2023, 01, 01, Integer.parseInt(startHM[0]), Integer.parseInt(startHM[1]), 00);
		LocalDateTime endTime = LocalDateTime.of(2023, 01, 01, Integer.parseInt(endHM[0]), Integer.parseInt(endHM[1]), 00);

		o.setActHours(String.format("%.1f",( Duration.between(startTime, endTime).toMinutes()/ 60.0)));

	/*	XdOvertimeSummary overtimeSummary = XdOvertimeSummary.dao.
				findFirst("select * from  xd_overtime_summary where apply_date='"+o.getApplyDate()
						+"' and emp_name='"+o.getEmpName()+"' and apply_type='"+o.getApplyType()
						+"' and  apply_start='"+o.getActStart()+"' and apply_end='"+o.getActEnd()+"' and ");

		if(overtimeSummary!=null){
			o.setApplyStart(overtimeSummary.getApplyStart());
			o.setApplyEnd(overtimeSummary.getApplyEnd());
			o.setApplyHours(Double.valueOf(overtimeSummary.getApplyHours()));
		}*/
		String days = o.getApplyDate();
		String[] ymd=null;
		if (days != null) {
			ymd = days.split("-");
		}


		if(o.getId()!=null){

			/*XdSettleOvertimeSummary summary = XdSettleOvertimeSummary.dao.findById(o.getId());
			String applyDate = summary.getApplyDate();
			String[] appDateArr = applyDate.split("-");
			int oldIndex = Integer.parseInt(appDateArr[2]);
			XdAttendanceSummary attendanceSummary =
					XdAttendanceSummary.dao.findFirst("select * from  xd_attendance_summary where emp_name='"+summary.getEmpName()
							+"' and schedule_year='" + appDateArr[0]+ "' and schedule_month='" + appDateArr[1] + "'");
			String[] oldTips = attendanceSummary.getTips().split(",");
			String oldTip = oldTips[oldIndex];
			oldTip=oldTip.replaceAll(summary.getActStart()+"-"+summary.getActEnd(),"");
			if("".equals(oldTip)){
				oldTip="0";
			}
			oldTips[oldIndex]=oldTip;
			oldTip="";
			for (String tip : oldTips) {
				oldTip=oldTip+tip+",";
			}
			attendanceSummary.setTips(oldTip.replaceAll(",$",""));


			if("0".equals(summary.getApplyType())){
				//Double aDouble = Double.valueOf(summary.getApplyHours());
				attendanceSummary.setNatOthours(attendanceSummary.getNatOthours()-summary.getActHours());
			}else{
				attendanceSummary.setOthours(attendanceSummary.getOthours()-summary.getActHours());
			}
			attendanceSummary.update();


			attendanceSummary =XdAttendanceSummary.dao.findFirst("select * from  xd_attendance_summary where emp_name='"+o.getEmpName()
					+"' and schedule_year='" + ymd[0]+ "' and schedule_month='" + ymd[1] + "'");
			int index = Integer.parseInt(ymd[2]);
			String tips = attendanceSummary.getTips();
			String[] tipsArr = tips.split(",");
			String indexTip = tipsArr[index];
			if("0".equals(indexTip)){
				tipsArr[index]=o.getActStart()+"-"+o.getActEnd();
			}else{
				tipsArr[index]=tipsArr[index]+o.getActStart()+"-"+o.getActEnd();
			}
			String sb="";
			for (String s : tipsArr) {
				sb=sb+s+",";
			}
			attendanceSummary.setTips(sb.replaceAll(",$",""));

			if("0".equals(o.getApplyType())){
				//Double aDouble = Double.valueOf(summary.getApplyHours());
				attendanceSummary.setNatOthours(attendanceSummary.getNatOthours()+o.getActHours());
			}else{
				attendanceSummary.setOthours(attendanceSummary.getOthours()+o.getActHours());
			}
			attendanceSummary.update();
			o.update();
*/

		}else{

			int index = Integer.parseInt(ymd[2]);
			XdAttendanceSummary attendanceSummary =
					XdAttendanceSummary.dao.findFirst("select * from  xd_attendance_summary where emp_name='"+o.getEmpName()
							+"' and schedule_year='" + ymd[0]+ "' and schedule_month='" + ymd[1] + "'");

			if(attendanceSummary!=null){
				String tips = attendanceSummary.getTips();
				String[] tipsArr = tips.split(",");
				String indexTip = tipsArr[index];
				if("0".equals(indexTip)){
					tipsArr[index]=o.getActStart()+"-"+o.getActEnd();
				}else{
					tipsArr[index]=tipsArr[index]+o.getActStart()+"-"+o.getActEnd();
				}
				String sb="";
				for (String s : tipsArr) {
					sb=sb+s+",";
				}
				if("0".equals(o.getApplyType())){
					//Double natOthours = attendanceSummary.getNatOthours();
					attendanceSummary.setNatOthours(attendanceSummary.getNatOthours() + Double.valueOf(o.getActHours()));
				}else{
					//Double othours = attendanceSummary.getOthours();
					attendanceSummary.setOthours(attendanceSummary.getOthours() + Double.valueOf(o.getActHours()));
				}




				attendanceSummary.setTips(sb.replaceAll(",$",""));

				String[] otFlags = attendanceSummary.getOtflags().split(",");

				if("0".equals(tipsArr[index])){
					otFlags[index]="0";
				}else{
					otFlags[index]="1";
				}

				String ot="";
				for (String s : otFlags) {
					ot=ot+s+",";
				}
				attendanceSummary.setOtflags(ot.replaceAll(",$",""));
				attendanceSummary.update();
			}


			XdOvertimeSummary first = XdOvertimeSummary.dao.findFirst("select *from  xd_overtime_summary  " +
					"where source='2' and apply_start='" + o.getActStart() + "' and apply_date='" + o.getApplyDate()
					+ "' and  apply_end='" + o.getActEnd() + "' and apply_type='" + o.getApplyType() + "'");
			if(first==null){
				o.setSource("2");
				o.setCreateDate(DateUtil.getCurrentTime());
				o.setCreateUser(ShiroKit.getUserId());
				o.setSuperDays( o.getApplyType());
				o.save();
			}else{
				first.setActStart(o.getActStart());
				first.setActEnd(o.getActStart());
				first.setActHours(o.getActHours());
				first.update();
			}
			/*o.setCreateDate(DateUtil.getCurrentTime());
			o.setCreateUser(ShiroKit.getUserId());
			o.save();*/
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

		List<XdEmployee> emps = XdEmployee.dao.find("select * from  xd_employee order by empnum");
		setAttr("emps",emps);
		List<SysOrg> orgList = SysOrg.dao.find("select * from  sys_org");
		setAttr("orgList",orgList);
		List<XdProjects> projects = XdProjects.dao.find("select * from  xd_projects");

		setAttr("projects",projects);

		if(!ShiroKit.getUserOrgId().equals("1")){
			XdScheduleSummary first = XdScheduleSummary.dao.findFirst("select * from  xd_schedule_summary  " +
					"where dept_value='" + ShiroKit.getUserOrgId() + "' and STATUS='1' order by schedule_year_month desc");

			LocalDate localDate = LocalDate.parse(first.getScheduleYear() + "-" + first.getScheduleMonth() + "-01").plusMonths(1);

			String format = DateTimeFormatter.ofPattern("yyyy-MM-dd").format(localDate);
			setAttr("startD",format);


			XdScheduleSummary end = XdScheduleSummary.dao.findFirst("select * from  xd_schedule_summary  " +
					"where dept_value='" + ShiroKit.getUserOrgId() + "'  order by schedule_year_month desc");
			String scheduleYearMonth = end.getScheduleYearMonth();
			XdDayModel dayModel = XdDayModel.dao.findFirst("select *from  xd_day_model where id like '%" + scheduleYearMonth + "%' order by id desc");
			setAttr("endD",dayModel.getDays());

		}else{
			setAttr("startD","2023-01-01");

			XdScheduleSummary end = XdScheduleSummary.dao.findFirst("select * from  xd_schedule_summary    order by schedule_year_month desc");
			String scheduleYearMonth = end.getScheduleYearMonth();
			XdDayModel dayModel = XdDayModel.dao.findFirst("select *from  xd_day_model where id like '%" + scheduleYearMonth + "%' order by id desc");
			setAttr("endD",dayModel.getDays());
		}



		XdOvertimeSummary summary =service.getById(id);
		setAttr("o", summary);
		setAttr("formModelName",StringUtil.toLowerCaseFirstOne(XdOvertimeSummary.class.getSimpleName()));
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
    public void deleteSettle() throws Exception{
		String ids = getPara("ids");
		service.deleteSettleByIds(ids);
    	renderSuccess("操作成功!");
    }

	/**
	 * @Method importExcel
	 * @Date 2023/1/15 11:01
	 * @Description 加班申请导入
	 * @Author king
	 * @Version  1.0
	 * @Return void
	 */
	public void importExcel() throws IOException, SQLException {
		UploadFile file = getFile("file","/content");
		List<List<String>> list = ExcelUtil.excelStringList(file.getFile().getAbsolutePath());
		Map<String,Object> result = service.importExcel(list);
		if((Boolean)result.get("success")){
			renderSuccess((String)result.get("message"));
		}else{
			renderError((String)result.get("message"));
		}
	}

 	/**
 	 * @Method exportExcel
 	 * @Date 2023/1/15 11:01
 	 * @Description  加班申请导出
 	 * @Author king
 	 * @Version  1.0
 	 * @Return void
 	 */
	public void exportExcel() throws UnsupportedEncodingException {

		String dept=getPara("dept","");
		String project=getPara("project","");
		String year=getPara("year","");
		String month = getPara("month","");
		String overtimeType = getPara("overtimeType","");
		String otType = getPara("otType","");
		String emp_name = new String(getPara("emp_name","").getBytes("ISO-8859-1"), "utf-8");

		String path = this.getSession().getServletContext().getRealPath("")+"/upload/export/"+ DateUtil.format(new Date(),21)+".xlsx";
		File file = service.exportExcel(path,dept,project,year,month,emp_name,overtimeType,otType);
		renderFile(file);
	}
	
	public void exportJBJSExcel() throws UnsupportedEncodingException {

		String dept=getPara("dept","");
		String year=getPara("year","");
		String month = getPara("month","");
		String overtimeType = getPara("overtimeType","");

		String path = this.getSession().getServletContext().getRealPath("")+"/upload/export/"+ DateUtil.format(new Date(),21)+".xlsx";
		File file = service.exportJBJSExcel(path,dept,year,month,overtimeType);
		renderFile(file);
	}

	public void getUserinfo(){

		String ename = getPara("ename");

		XdEmployee emp = XdEmployee.dao.findFirst("select * from  xd_employee where name='" + ename + "'");

		SysOrg org = SysOrg.dao.findById(emp.getDepartment());
		XdProjects project = XdProjects.dao.findById(emp.getCostitem());
		/*Map<String,String> map =new HashMap<>();
		map.put("empNum",emp.getEmpnum());
		map.put("idnum",emp.getIdnum());
		map.put("orgId",org.getId());
		map.put("orgName",org.getName());
		map.put("projectId",project.getId()+"");
		map.put("projectName",project.getProjectName());*/
	/*	String s = JSONUtil.mapToJson(map,null,true);
		System.out.println(s);

		System.out.println(jsonObject.toString());*/
	/*
		jsonObject.put("org",org);
		jsonObject.put("project",project);
		renderSuccess(jsonObject,"");*/
		/*renderJson(map);*/

		cn.hutool.json.JSONObject jsonObject=new cn.hutool.json.JSONObject();
		jsonObject.put("empNum",emp.getEmpnum());
		jsonObject.put("idnum",emp.getIdnum());
		jsonObject.put("orgId",org.getId());
		jsonObject.put("orgName",org.getName());
		jsonObject.put("projectId",project.getId()+"");
		jsonObject.put("projectName",project.getProjectName());
//		renderSuccess(jsonObject,"");
		renderJson(jsonObject);


	}



	public void exportApportionExcel() throws UnsupportedEncodingException {

		String dept=getPara("dept","");
		String unitname=getPara("unitname","");
		String year=getPara("year","");
		String month = getPara("month","");
		String emp_name = new String(getPara("emp_name","").getBytes("ISO-8859-1"), "utf-8");

		String path = this.getSession().getServletContext().getRealPath("")+"/upload/export/"+ DateUtil.format(new Date(),21)+".xlsx";
		File file = service.exportApportionExcel(path,dept,unitname,year,month,emp_name);
		renderFile(file);
	}

}