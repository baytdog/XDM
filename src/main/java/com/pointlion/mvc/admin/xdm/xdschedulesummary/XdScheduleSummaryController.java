package com.pointlion.mvc.admin.xdm.xdschedulesummary;

import cn.hutool.json.JSONObject;
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
import com.pointlion.plugin.shiro.ShiroKit;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class XdScheduleSummaryController extends BaseController {
	public static final XdScheduleSummaryService service = XdScheduleSummaryService.me;
	/***
	 * get list page
	 */
	public void getListPage(){
		List<XdDict> units = XdDict.dao.find("select * from xd_dict where type ='unit' order by sortnum");
		setAttr("units",units);
		XdScheduleSummary last = XdScheduleSummary.dao.findFirst("select * from  xd_schedule_summary order by schedule_year_month desc");
		if(last!=null){
		setAttr("year",last==null?"":last.getScheduleYear());
		setAttr("month",last==null?"":last.getScheduleMonth());
		String yearMonth=(last==null?"":last.getScheduleYear())+(last==null?"":last.getScheduleMonth());
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
//			holidayLists.add(lastMonLastDay.getHolidays()==null?"":lastMonLastDay.getHolidays());
			if(lastMonLastDay.getHolidays()==null||lastMonLastDay.getHolidays().equals("")){
				holidayLists.add("-");
			}else{

				holidayLists.add(lastMonLastDay.getHolidays());
			}
		}else{
			weekLists.add("");
			holidayLists.add("-");
		}

		xdDayModels.stream().forEach(
				xdDayModel-> {
				weekLists.add(xdDayModel.getWeeks());
				if(xdDayModel.getHolidays()==null||xdDayModel.getHolidays().equals("")){
					holidayLists.add("-");
				}else{

					holidayLists.add(xdDayModel.getHolidays());
				}
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
		}
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

    public void getTableHeader(){
		String year = getPara("year","");
		String month = getPara("month","");
		String yearMonth=year+month;
		List<XdDayModel> xdDayModels = XdDayModel.dao.find("select * from  xd_day_model where id like '"+yearMonth+"%' order by id");
		LocalDate localDate = LocalDate.parse(year+"-"+month+"-01").minusMonths(1);
		DateTimeFormatter dtf=DateTimeFormatter.ofPattern("yyyyMM");
		String lastMonth = dtf.format(localDate);
		XdDayModel lastMonLastDay = XdDayModel.dao.findFirst("select * from  xd_day_model where id like '" + lastMonth + "%' order by id desc");
		List<String> weekLists=new ArrayList<>();
		List<String> holidayLists=new ArrayList<>();
		if(lastMonLastDay!=null){
			weekLists.add(lastMonLastDay.getWeeks());
			if(lastMonLastDay.getHolidays()==null||lastMonLastDay.getHolidays().equals("")){
				holidayLists.add("-");
			}else{

				holidayLists.add(lastMonLastDay.getHolidays());
			}
		}else{
			weekLists.add("");
			holidayLists.add("-");
		}

		xdDayModels.stream().forEach(
				xdDayModel-> {
					weekLists.add(xdDayModel.getWeeks());
					if(xdDayModel.getHolidays()==null||xdDayModel.getHolidays().equals("")){
						holidayLists.add("-");
					}else{

						holidayLists.add(xdDayModel.getHolidays());
					}
				});
		setAttr("daysNum",xdDayModels.size());
		setAttr("weekLists",weekLists);
		setAttr("holidayLists",holidayLists);
		/*List<XdShift> xdShifts = XdShift.dao.find("select * from  xd_shift order by shiftname");
		String shifts="";
		for (XdShift xdShift : xdShifts) {
			shifts=shifts+","+xdShift.getShiftname();
		}
		setAttr("shifts",shifts.replaceAll("^,", ""));*/
	/*	JsonObject jsonObject=new JsonObject();
		jsonObject.ad
		jsonObject.add("daysNum",xdDayModels.size());*/
		JSONObject jsonObject=new JSONObject();
		jsonObject.put("daysNum",xdDayModels.size());
		jsonObject.put("weekLists",weekLists);
		jsonObject.put("holidayLists",holidayLists);
		renderSuccess(jsonObject,"");
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
		String id = getPara("id");
		String oldValue = getPara("oldValue");
		String modValue = getPara("modValue");
		String overtime = getPara("overtime");
		String field = getPara("field");
		String type = getPara("type");
		double returunOtHour=0;
		XdScheduleSummary scheduleSummary  = XdScheduleSummary.dao.findById(id);
		if("2".equals(type)){
			scheduleSummary.setRemarks(modValue);
		}else{
			Class  superclass = scheduleSummary.getClass().getSuperclass();
			try {
				Method method = superclass.getMethod("set" + field.substring(0, 1).toUpperCase() + field.substring(1),String.class);
				method.invoke(scheduleSummary, modValue);
				int index = Integer.parseInt(field.replace("day", ""));

				String[] moidfyFlags = scheduleSummary.getFlags().split(",");
				moidfyFlags[index]="1";
				String modify="";
				for (String moidfyFlag : moidfyFlags) {
					modify=modify+","+moidfyFlag;
				}
				scheduleSummary.setFlags(modify.replaceAll("^,",""));
				String ot="";
				String[] otFlags = scheduleSummary.getOtflas().split(",");
				if("on".equals(overtime)){
					otFlags[index]="1";
				}else{
					otFlags[index]="0";
				}
				for (String otFlag : otFlags) {
					ot=ot+","+otFlag;
				}
				scheduleSummary.setOtflas(ot.replaceAll("^,",""));

				String yearMonth=scheduleSummary.getScheduleYear()+"-"+scheduleSummary.getScheduleMonth();
				LocalDate lastMonLastDay = LocalDate.parse(yearMonth+"-01").minusDays(1);
				DateTimeFormatter dtf=DateTimeFormatter.ofPattern("yyyy-MM-dd");
				String lastMonthDay = dtf.format(lastMonLastDay);

				List<XdOvertimeSummary> applyList = XdOvertimeSummary.dao.find("select * from  xd_overtime_summary where emp_name='"+scheduleSummary.getEmpName()+"' and apply_date like'" + yearMonth + "%' or apply_date='" + lastMonthDay + "'");
				for (XdOvertimeSummary overtimeSummary : applyList) {
					overtimeSummary.delete();
				}

				List<XdDayModel> xdDayModels = XdDayModel.dao.find("select * from  xd_day_model where days like '" + yearMonth + "%' or days='" + lastMonLastDay + "'");
				List<XdShift> xdShifts = XdShift.dao.find("select * from  xd_shift");
				Map<String, XdShift> nameShiftObjMap = xdShifts.stream().collect(Collectors.toMap(XdShift::getShiftname, xdShift -> xdShift));
				Map<String, String> holidaysMap = xdDayModels.stream().collect(Collectors.toMap(XdDayModel::getId, XdDayModel::getHolidays));
				double othours=0;//加班时间
				double work_hour=0;//出勤时间
				XdEmployee emp = XdEmployee.dao.findFirst("select * from xd_employee where name ='" + scheduleSummary.getEmpName() + "'");
				XdOvertimeSummary xdOvertimeSummary=new XdOvertimeSummary();
				xdOvertimeSummary.setEmpNum(scheduleSummary.getEmpNum());
				xdOvertimeSummary.setEmpName(scheduleSummary.getEmpName());
				xdOvertimeSummary.setEmpIdnum(emp==null?"":emp.getIdnum());
				xdOvertimeSummary.setProjectName(scheduleSummary.getProjectName());
				xdOvertimeSummary.setProjectId(scheduleSummary.getProjectValue());
				xdOvertimeSummary.setDeptName(scheduleSummary.getDeptName());
				xdOvertimeSummary.setDeptId(scheduleSummary.getDeptValue());
				xdOvertimeSummary.setCreateUser(ShiroKit.getUserId());
				xdOvertimeSummary.setCreateDate(DateUtil.getCurrentTime());
				for (int i = 0; i < xdDayModels.size(); i++) {
					String suffix="";
					if(i<10){
						suffix="0"+i;
					}else{
						suffix=i+"";
					}
					//	XdShift xdShift = nameShiftObjMap.get(cellValue);
					Method getMethod = superclass.getMethod("getDay" + suffix);
					String  shiftName = (String) getMethod.invoke(scheduleSummary);
					XdDayModel dayModel = xdDayModels.get(i);

					if(shiftName!=null&&!shiftName.equals("")){
						XdShift xdShift = nameShiftObjMap.get(shiftName);
						if(xdShift!=null && xdShift.getBusitime()!=null&& !xdShift.getBusitime().equals("")){
							if(xdShift.getSpanDay().equals("1")){
								LocalDate localDate = LocalDate.parse(dayModel.getDays()).plusDays(1);
								DateTimeFormatter dtf1=DateTimeFormatter.ofPattern("yyyy-MM-dd");
								String nextDate  = dtf.format(localDate);
								//是跨天且当天法定假日
								if(holidaysMap.get(dayModel.getId())!=null && !"".equals(holidaysMap.get(dayModel.getId()))){
									xdOvertimeSummary.setId(null);
									xdOvertimeSummary.setApplyDate(dayModel.getDays());
									xdOvertimeSummary.setApplyStart(xdShift.getBusitime());
									xdOvertimeSummary.setApplyEnd("24:00");
									xdOvertimeSummary.setApplyHours(xdShift.getCurdayHours());
									xdOvertimeSummary.setApplyType("0");
									xdOvertimeSummary.save();
									if(i>0){
										othours+=Double.valueOf(xdShift.getCurdayHours());
									}
									//overTimeList.add(xdOvertimeSummary);
									//othours+=Double.valueOf(xdShift.getCurdayHours());
								}else{//跨天当天不是法定假日
									if(i>0){
										work_hour+=Double.valueOf(xdShift.getCurdayHours());
									}
									if(i==index && "on".equals(overtime)){
										xdOvertimeSummary.setId(null);
										xdOvertimeSummary.setApplyDate(dayModel.getDays());
										xdOvertimeSummary.setApplyStart(xdShift.getBusitime());
										xdOvertimeSummary.setApplyEnd("24:00");
										xdOvertimeSummary.setApplyHours(xdShift.getCurdayHours());
										xdOvertimeSummary.setApplyType("1");
										xdOvertimeSummary.save();
									}


									//work_hour+=Double.valueOf(xdShift.getCurdayHours());
									//otFlags=otFlags+","+"0";
								}
								//是跨天且第二天是法定假日
								if(holidaysMap.get(nextDate)!=null && !"".equals(holidaysMap.get(nextDate))){
									xdOvertimeSummary.setId(null);
									xdOvertimeSummary.setApplyDate(nextDate);
									xdOvertimeSummary.setApplyStart("00:00");
									xdOvertimeSummary.setApplyEnd(xdShift.getUnbusitime());
									xdOvertimeSummary.setApplyHours(xdShift.getSpanHours());
									xdOvertimeSummary.setApplyType("0");
									//overTimeList.add(xdOvertimeSummary);
									xdOvertimeSummary.save();
									if(i<xdDayModels.size()-1){
										othours+=Double.valueOf(xdShift.getSpanHours());
									}
								}else{//是跨天且第二天不是法定假日
									if(i<xdDayModels.size()-1){
										work_hour+=Double.valueOf(xdShift.getSpanHours());
									}

									if(i==index && "on".equals(overtime)){
										xdOvertimeSummary.setId(null);
										xdOvertimeSummary.setApplyDate(nextDate);
										xdOvertimeSummary.setApplyStart("00:00");
										xdOvertimeSummary.setApplyEnd(xdShift.getUnbusitime());
										xdOvertimeSummary.setApplyHours(xdShift.getSpanHours());
										xdOvertimeSummary.setApplyType("1");
										xdOvertimeSummary.save();
									}
								}
							}else{

								if(holidaysMap.get(dayModel.getId())!=null && !"".equals(holidaysMap.get(dayModel.getId()))){
									xdOvertimeSummary.setId(null);
									xdOvertimeSummary.setApplyDate(dayModel.getDays());
									xdOvertimeSummary.setApplyStart(xdShift.getBusitime());
									xdOvertimeSummary.setApplyEnd(xdShift.getUnbusitime());
									xdOvertimeSummary.setApplyHours(xdShift.getHours());
									xdOvertimeSummary.setApplyType("0");
									xdOvertimeSummary.save();
									//overTimeList.add(xdOvertimeSummary);
									if(i>0){
										othours+=Double.valueOf(xdShift.getHours());
									}

								}else{

									if(i==index){
										if( "on".equals(overtime)){
											xdOvertimeSummary.setId(null);
											xdOvertimeSummary.setApplyDate(dayModel.getDays());
											xdOvertimeSummary.setApplyStart(xdShift.getBusitime());
											xdOvertimeSummary.setApplyEnd(xdShift.getUnbusitime());
											xdOvertimeSummary.setApplyHours(xdShift.getHours());
											xdOvertimeSummary.setApplyType("1");
											xdOvertimeSummary.save();
											othours+=Double.valueOf(xdShift.getHours());
										}

									}else {
										if(i>0){
											work_hour+=Double.valueOf(xdShift.getHours());
										}
									}

								}

							}
						}

					}


					
				}
				returunOtHour=othours;
				scheduleSummary.setOthours(othours);
				scheduleSummary.setWorkHour(work_hour);
				scheduleSummary.update();

			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}

		}


		renderSuccess(String.valueOf(returunOtHour));
	}





}