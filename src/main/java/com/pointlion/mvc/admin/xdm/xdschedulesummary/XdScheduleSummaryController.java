package com.pointlion.mvc.admin.xdm.xdschedulesummary;

import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.upload.UploadFile;
import com.pointlion.mvc.common.base.BaseController;
import com.pointlion.mvc.common.model.XdDict;
import com.pointlion.mvc.common.model.XdScheduleSummary;
import com.pointlion.mvc.common.utils.DateUtil;
import com.pointlion.mvc.common.utils.StringUtil;
import com.pointlion.mvc.common.utils.office.excel.ExcelUtil;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;


public class XdScheduleSummaryController extends BaseController {
	public static final XdScheduleSummaryService service = XdScheduleSummaryService.me;
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
    	String curr = getPara("pageNumber");
    	String pageSize = getPara("pageSize");
		String dept = getPara("dept","");
		String emp_name = java.net.URLDecoder.decode(getPara("emp_name",""),"UTF-8");
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
    /*	if(StrKit.notBlank(o.getId())){
    		o.update();
    	}else{
    		o.setId(UuidUtil.getUUID());
    		o.setCreateTime(DateUtil.getCurrentTime());
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
		/*XdScheduleSummary o = new XdScheduleSummary();
		if(StrKit.notBlank(id)){
    		o = service.getById(id);
    	}else{
    		SysUser user = SysUser.dao.findById(ShiroKit.getUserId());
    		SysOrg org = SysOrg.dao.findById(user.getOrgid());
    	}*/
		XdScheduleSummary summary =service.getById(id);
		setAttr("o", summary);
    	setAttr("formModelName",StringUtil.toLowerCaseFirstOne(XdScheduleSummary.class.getSimpleName()));
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

//		String empnum = java.net.URLDecoder.decode(getPara("empnum",""),"UTF-8");
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