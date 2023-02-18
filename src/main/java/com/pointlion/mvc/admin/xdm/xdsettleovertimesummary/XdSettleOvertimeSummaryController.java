package com.pointlion.mvc.admin.xdm.xdsettleovertimesummary;

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
import java.util.Date;
import java.util.List;
import java.util.Map;



public class XdSettleOvertimeSummaryController extends BaseController {
	public static final XdSettleOvertimeSummaryService service = XdSettleOvertimeSummaryService.me;
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
    	XdSettleOvertimeSummary o = getModel(XdSettleOvertimeSummary.class);
		XdOvertimeSummary overtimeSummary = XdOvertimeSummary.dao.
				findFirst("select * from  xd_overtime_summary where apply_date='"+o.getApplyDate()+"' and emp_name='"+o.getEmpName()+"' and apply_type='"+o.getApplyType()+"'");
		if(overtimeSummary!=null){
			o.setApplyStart(overtimeSummary.getApplyStart());
			o.setApplyEnd(overtimeSummary.getApplyEnd());
			o.setApplyHours(Double.valueOf(overtimeSummary.getApplyHours()));
		}
		String days = o.getApplyDate();
		String[] ymd=null;
		if (days != null) {
			ymd = days.split("-");
		}


		if(o.getId()!=null){

			XdSettleOvertimeSummary summary = XdSettleOvertimeSummary.dao.findById(o.getId());

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
		}else{

			int index = Integer.parseInt(ymd[2]);
			XdAttendanceSummary attendanceSummary =
					XdAttendanceSummary.dao.findFirst("select * from  xd_attendance_summary where emp_name='"+o.getEmpName()
							+"' and schedule_year='" + ymd[0]+ "' and schedule_month='" + ymd[1] + "'");

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
				attendanceSummary.setNatOthours(attendanceSummary.getNatOthours() + o.getActHours());
			}else{
				//Double othours = attendanceSummary.getOthours();
				attendanceSummary.setOthours(attendanceSummary.getOthours() + o.getActHours());
			}




			attendanceSummary.setTips(sb.replaceAll(",$",""));
			attendanceSummary.update();


			o.setCreateDate(DateUtil.getCurrentTime());
			o.setCreateUser(ShiroKit.getUserId());
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
		XdSettleOvertimeSummary summary =service.getById(id);
		setAttr("o", summary);
		List<XdEmployee> emps = XdEmployee.dao.find("select * from  xd_employee");
		setAttr("emps",emps);
		List<SysOrg> orgList = SysOrg.dao.find("select * from  sys_org");
		setAttr("orgList",orgList);
		List<XdProjects> projects = XdProjects.dao.find("select * from  xd_projects");

		setAttr("projects",projects);
		setAttr("formModelName",StringUtil.toLowerCaseFirstOne(XdSettleOvertimeSummary.class.getSimpleName()));
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
	 * @Method importExcel
	 * @Date 2023/1/15 15:11
	 * @Description 导入excle数据
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
	  * @Date 2023/1/15 15:11
	  * @Description 导出excel 数据
	  * @Author king
	  * @Version  1.0
	  * @Return void
	  */
	public void exportExcel() throws UnsupportedEncodingException {

		String dept=getPara("dept","");
		String unitname=getPara("unitname","");
		String year=getPara("year","");
		String month = getPara("month","");
		String emp_name = new String(getPara("emp_name","").getBytes("ISO-8859-1"), "utf-8");

		String path = this.getSession().getServletContext().getRealPath("")+"/upload/export/"+ DateUtil.format(new Date(),21)+".xlsx";
		File file = service.exportExcel(path,dept,unitname,year,month,emp_name);
		renderFile(file);
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
	public void getUserinfo(){

		String ename = getPara("ename");

		XdEmployee emp = XdEmployee.dao.findFirst("select * from  xd_employee where name='" + ename + "'");

		SysOrg org = SysOrg.dao.findById(emp.getDepartment());
		XdProjects project = XdProjects.dao.findById(emp.getCostitem());

		cn.hutool.json.JSONObject jsonObject=new cn.hutool.json.JSONObject();
		jsonObject.put("empNum",emp.getEmpnum());
		jsonObject.put("idnum",emp.getIdnum());
		jsonObject.put("orgId",org.getId());
		jsonObject.put("orgName",org.getName());
		jsonObject.put("projectId",project.getId()+"");
		jsonObject.put("projectName",project.getProjectName());
		renderJson(jsonObject);


	}
	
}