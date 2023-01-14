package com.pointlion.mvc.admin.xdm.xdattendancesummary;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.upload.UploadFile;
import com.pointlion.mvc.common.base.BaseController;
import com.pointlion.mvc.admin.oa.workflow.WorkFlowService;
import com.pointlion.mvc.common.model.*;
import com.pointlion.mvc.common.utils.StringUtil;
import com.pointlion.mvc.common.utils.UuidUtil;
import com.pointlion.mvc.common.utils.Constants;
import com.pointlion.mvc.admin.oa.common.OAConstants;
import com.pointlion.mvc.common.utils.DateUtil;
import com.pointlion.mvc.common.utils.office.excel.ExcelUtil;
import com.pointlion.plugin.shiro.ShiroKit;



public class XdAttendanceSummaryController extends BaseController {
	public static final XdAttendanceSummaryService service = XdAttendanceSummaryService.me;
	/***
	 * get list page
	 */
	public void getListPage(){
		List<XdDict> units = XdDict.dao.find("select * from xd_dict where type ='unit' order by sortnum");
		setAttr("units",units);
		renderIframe("list.html");
	}
	/***
	 * list page data
	 **/
	public void listData() throws UnsupportedEncodingException {
		String pageSize = getPara("pageSize");
		String curr = getPara("pageNumber");
		String emp_name = java.net.URLDecoder.decode(getPara("emp_name",""),"UTF-8");
		String dept = getPara("dept","");
		String unitname = getPara("unitname","");
		String year = getPara("year","");
		String month = getPara("month","");
		Page<Record> page = service.getPage(Integer.valueOf(curr),Integer.valueOf(pageSize),dept,unitname,emp_name,year,month);
		renderPage(page.getList(),"",page.getTotalRow());
	}
	/***
	 * save data
	 */
	public void save(){
		XdScheduleSummary o = getModel(XdScheduleSummary.class);
		renderSuccess();
	}
	/***
	 * edit page
	 */
	public void getEditPage(){
		String id = getPara("id");
		String view = getPara("view");
		setAttr("view", view);
		XdAttendanceSummary summary =service.getById(id);
		setAttr("o", summary);
		setAttr("formModelName",StringUtil.toLowerCaseFirstOne(XdAttendanceSummary.class.getSimpleName()));
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
	 * @Method importExcel
	 * @Date 2023/1/13 14:34
	 * @Exception
	 * @Description 加班申请导入
	 * @Author king
	 * @Version  1.0
	 * @Return void
	 */
	public void importExcel() throws IOException, SQLException {
		UploadFile file = getFile("file","/content");
		List<List<String>> list = ExcelUtil.excelToStringList(file.getFile().getAbsolutePath());
		Map<String,Object> result = service.importExcel(list);
		if((Boolean)result.get("success")){
			renderSuccess((String)result.get("message"));
		}else{
			renderError((String)result.get("message"));
		}
	}

	/**
	 * @Method exportExcel
	 * @Date 2023/1/14 11:33
	 * @Exception
	 * @Description  导出
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


	public void getRcpList() {
		String id = getPara("id");
		List<XdAttendanceRcp> rcpList = XdAttendanceRcp.dao.find("select * from  xd_attendance_rcp where  attendid_id='" + id + "' order by id");
		renderJson(rcpList);
	}

	public void getDaysList() {
		String id = getPara("id");
		List<XdAttendanceRcp> rcpList = XdAttendanceRcp.dao.find("select * from  xd_attendance_days where  attendid_id='" + id + "' order by id");
		renderJson(rcpList);
	}
	
}