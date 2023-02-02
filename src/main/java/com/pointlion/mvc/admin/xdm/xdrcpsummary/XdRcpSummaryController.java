package com.pointlion.mvc.admin.xdm.xdrcpsummary;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.upload.UploadFile;
import com.pointlion.mvc.common.base.BaseController;
import com.pointlion.mvc.common.model.*;
import com.pointlion.mvc.common.utils.DateUtil;
import com.pointlion.mvc.common.utils.StringUtil;
import com.pointlion.mvc.common.utils.office.excel.DbImportExcelUtils;
import com.pointlion.mvc.common.utils.office.excel.ExcelUtil;
import com.pointlion.plugin.shiro.ShiroKit;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;


import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


public class XdRcpSummaryController extends BaseController {
	public static final XdRcpSummaryService service = XdRcpSummaryService.me;
	/***
	 * get list page
	 */
	public void getListPage(){

		List<XdDict> units = XdDict.dao.find("select * from  xd_dict where  type='unit'");
		setAttr("units",units);
		List<XdProjects> projects = XdProjects.dao.findAll();
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
		String unit = getPara("unit","");
		String project = getPara("project","");
		String empName = java.net.URLDecoder.decode(getPara("emp_name",""),"UTF-8");
		String workstation = java.net.URLDecoder.decode(getPara("workstation",""),"UTF-8");
    	Page<Record> page = service.getPage(Integer.valueOf(curr),Integer.valueOf(pageSize),dept,unit,project,empName,workstation);
    	renderPage(page.getList(),"",page.getTotalRow());
    }
    /***
     * save data
     */
    public void save(){
    	XdRcpSummary o = getModel(XdRcpSummary.class);
    	renderSuccess();
    }
    /***
     * edit page
     */
    public void getEditPage(){
    	String id = getPara("id");
    	String view = getPara("view");
		setAttr("view", view);
		XdRcpSummary o = new XdRcpSummary();
		if(StrKit.notBlank(id)){
    		o = service.getById(id);
    	}else{
    		SysUser user = SysUser.dao.findById(ShiroKit.getUserId());
    		SysOrg org = SysOrg.dao.findById(user.getOrgid());
    	}
		setAttr("o", o);
    	setAttr("formModelName",StringUtil.toLowerCaseFirstOne(XdRcpSummary.class.getSimpleName()));
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
	 * @Date 2023/1/15 16:43
	 * @Description  导入excle数据
	 * @Author king
	 * @Version  1.0
	 * @Return void
	 */
	public void importExcel() throws IOException, SQLException {
		UploadFile file = getFile("file","/content");
		List<List<String>> list = ExcelUtil.excelTextToList(file.getFile().getAbsolutePath());
		Map<String,Object> result = service.importExcel(list);
		if((Boolean)result.get("success")){
			renderSuccess((String)result.get("message"));
		}else{
			renderError((String)result.get("message"));
		}
	}





	/**
	 * @Method exportExcel
	 * @Date 2023/1/15 16:43
	 * @Description 导出excle 数据
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
	
}