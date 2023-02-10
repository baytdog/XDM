package com.pointlion.mvc.admin.xdm.xdschedulesummary;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.upload.UploadFile;
import com.pointlion.mvc.common.base.BaseController;
import com.pointlion.mvc.common.model.*;
import com.pointlion.mvc.common.utils.DateUtil;
import com.pointlion.mvc.common.utils.StringUtil;
import com.pointlion.mvc.common.utils.UuidUtil;
import com.pointlion.mvc.common.utils.office.excel.ExcelUtil;
import org.joda.time.format.DateTimeFormat;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;


public class XdScheduleSummaryController extends BaseController {
	public static final XdScheduleSummaryService service = XdScheduleSummaryService.me;
	/***
	 * get list page
	 */
	public void getListPage(){
		List<XdDict> units = XdDict.dao.find("select * from xd_dict where type ='unit' order by sortnum");
		setAttr("units",units);
		XdScheduleSummary last = XdScheduleSummary.dao.findFirst("select * from  xd_schedule_summary order by schedule_year_month desc");
		setAttr("year",last.getScheduleYear());
		setAttr("month",last.getScheduleMonth());
		String yearMonth=last.getScheduleYear()+last.getScheduleMonth();
		List<XdDayModel> xdDayModels = XdDayModel.dao.find("select * from  xd_day_model where id like '"+yearMonth+"%' order by id");
		LocalDate localDate = LocalDate.parse(last.getScheduleYear()+"-"+last.getScheduleMonth()+"-01").minusMonths(1);
		DateTimeFormatter dtf=DateTimeFormatter.ofPattern("yyyyMM");
		String lastMonth = dtf.format(localDate);
		XdDayModel lastMonLastDay = XdDayModel.dao.findFirst("select * from  xd_day_model where id like '" + lastMonth + "%' order by id desc");
		List<String> weekLists=new ArrayList<>();
		List<String> holidayLists=new ArrayList<>();
		String firstRow="0";
		if(lastMonLastDay!=null){
			weekLists.add(lastMonLastDay.getWeeks());
			holidayLists.add(lastMonLastDay.getHolidays()==null?"":lastMonLastDay.getHolidays());
		}else{
			weekLists.add("");
			holidayLists.add("");
		}

		xdDayModels.stream().forEach(
				xdDayModel-> {
				weekLists.add(xdDayModel.getWeeks());
				holidayLists.add(xdDayModel.getHolidays()==null?"":xdDayModel.getHolidays());
		});
	/*	for (int i = 1; i <= xdDayModels.size(); i++) {
			firstRow=firstRow+","+i;
		}
		System.out.println(firstRow);*/

		/*if(xdDayModels.size()<31){
			for (int i = 31; i> xdDayModels.size(); i--) {
				weekLists.add("");
				holidayLists.add("");
			}

		}*/
		setAttr("daysNum",xdDayModels.size());
		setAttr("weekLists",weekLists);
		setAttr("holidayLists",holidayLists);
		List<XdShift> xdShifts = XdShift.dao.find("select * from  xd_shift order by shiftname");
		String shifts="";
		for (XdShift xdShift : xdShifts) {
			shifts=shifts+","+xdShift.getShiftname();
		}
		setAttr("shifts",shifts.replaceAll("^,", ""));
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
    	if(StrKit.notBlank(o.getId())){
    		o.update();
    	}else{
    		o.setId(UuidUtil.getUUID());
    		/*o.setCreateTime(DateUtil.getCurrentTime());
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
		/*XdScheduleSummary o = new XdScheduleSummary();
		if(StrKit.notBlank(id)){
    		o = service.getById(id);
    	}else{
    		SysUser user = SysUser.dao.findById(ShiroKit.getUserId());
    		SysOrg org = SysOrg.dao.findById(user.getOrgid());
    	}*/
		XdScheduleSummary summary =service.getById(id);
		String scheduleYearMonth = summary.getScheduleYearMonth();
//		XdDayModel.dao.find("")

		List<XdDayModel> xdDayModels = XdDayModel.dao.find("select * from  xd_day_model where id like '" + scheduleYearMonth + "%' order by id");
		int daysNum=xdDayModels.size();
		setAttr("daysNum",daysNum+1);
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

	/**
	 * @Method updateShifts
	 * @Date 2023/2/10 11:27
	 * @Exception
	 * @Description  修改班次
	 * @Author king
	 * @Version  1.0
	 * @Return void
	 */
	public void updateCell(){
		System.out.println(getPara("id"));
		System.out.println(getPara("modValue"));
		System.out.println(getPara("overtime"));
		renderSuccess();
	}





}