package com.pointlion.mvc.admin.xdm.xdovertimesummary;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.hutool.json.JSON;
import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.upload.UploadFile;
import com.pointlion.mvc.common.base.BaseController;
import com.pointlion.mvc.admin.oa.workflow.WorkFlowService;
import com.pointlion.mvc.common.model.*;
import com.pointlion.mvc.common.utils.*;
import com.pointlion.mvc.admin.oa.common.OAConstants;
import com.pointlion.mvc.common.utils.office.excel.ExcelUtil;
import com.pointlion.plugin.shiro.ShiroKit;
import net.sf.json.JSONObject;


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
	/***
     * list page data
     **/
    public void listData() throws UnsupportedEncodingException {
    	String curr = getPara("pageNumber");
    	String pageSize = getPara("pageSize");
		String dept = getPara("dept","");
		String project = getPara("project","");
		String emp_name = java.net.URLDecoder.decode(getPara("emp_name",""),"UTF-8");
		String emp_num = getPara("emp_num","");
		String apply_date = getPara("apply_date","");
		String overtimeType = getPara("overtimeType","");
    	Page<Record> page = service.getPage(Integer.valueOf(curr),Integer.valueOf(pageSize),dept,project,emp_name,emp_num,apply_date,overtimeType);
    	renderPage(page.getList(),"",page.getTotalRow());
    }
    /***
     * save data
     */
    public void save(){
    	XdOvertimeSummary o = getModel(XdOvertimeSummary.class);
    	if(o.getId()!=null){
    		o.update();
    	}else{
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

		List<XdEmployee> emps = XdEmployee.dao.find("select * from  xd_employee");
		setAttr("emps",emps);
		List<SysOrg> orgList = SysOrg.dao.find("select * from  sys_org");
		setAttr("orgList",orgList);
		List<XdProjects> projects = XdProjects.dao.find("select * from  xd_projects");

		setAttr("projects",projects);



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
		String emp_name = new String(getPara("emp_name","").getBytes("ISO-8859-1"), "utf-8");

		String path = this.getSession().getServletContext().getRealPath("")+"/upload/export/"+ DateUtil.format(new Date(),21)+".xlsx";
		File file = service.exportExcel(path,dept,project,year,month,emp_name,overtimeType);
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

}