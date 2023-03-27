package com.pointlion.mvc.admin.xdm.xdattendancesummary;

import cn.hutool.json.JSONObject;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.upload.UploadFile;
import com.pointlion.mvc.common.base.BaseController;
import com.pointlion.mvc.common.model.*;
import com.pointlion.mvc.common.utils.DateUtil;
import com.pointlion.mvc.common.utils.StringUtil;
import com.pointlion.mvc.common.utils.office.excel.ExcelUtil;
import com.pointlion.plugin.shiro.ShiroKit;
import com.pointlion.util.CheckAttendanceUtil;
import org.apache.commons.beanutils.BeanUtils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


public class XdAttendanceSummaryController extends BaseController {
	public static final XdAttendanceSummaryService service = XdAttendanceSummaryService.me;


	public void getlistWorkPage(){
		setAttr("now",DateTimeFormatter.ofPattern("yyyy-MM-dd").format(LocalDate.now()));
		renderIframe("listWork.html");
	}


	public void listWorkData() throws UnsupportedEncodingException {
		String pageSize = getPara("pageSize");
		String curr = getPara("pageNumber");
		String emp_name = java.net.URLDecoder.decode(getPara("emp_name",""),"UTF-8");
		String work_date = getPara("work_date","");
		String hours = getPara("hours","");
		String min = getPara("min","");
		Page<Record> page = service.getPage(Integer.valueOf(curr),Integer.valueOf(pageSize),work_date, hours,min);
		renderPage(page.getList(),"",page.getTotalRow());
	}

	/***
	 * get list page
	 */
	public void getListPage(){
		/*List<XdDict> units = XdDict.dao.find("select * from xd_dict where type ='unit' order by sortnum");
		setAttr("units",units);
		XdAttendanceSummary last = XdAttendanceSummary.dao.findFirst("select * from  xd_schedule_summary order by schedule_year_month desc");
		setAttr("year",last.getScheduleYear());
		setAttr("month",last.getScheduleMonth());*/
		List<SysOrg> orgList = SysOrg.dao.find("select * from  sys_org where id<>'root' order by sort");
		setAttr("orgs",orgList);
		List<XdDict> units = XdDict.dao.find("select * from xd_dict where type ='unit' order by sortnum");
		setAttr("units",units);
		XdAttendanceSummary last = XdAttendanceSummary.dao.findFirst("select * from  xd_attendance_summary order by schedule_year_month desc");
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
			List<XdShift> xdShifts = XdShift.dao.find("select * from  xd_shift order by shiftname");
			String shifts="";
			for (XdShift xdShift : xdShifts) {
				shifts=shifts+","+xdShift.getShiftname();
			}

			setAttr("shiftList",xdShifts);
			setAttr("shifts",shifts.replaceAll("^,", ""));
		}






		renderIframe("list.html");
	}
	/***
	 * list page data
	 **/
	public void listData() throws UnsupportedEncodingException {
		String pageSize = getPara("pageSize");
		String curr = getPara("pageNumber");
		String empName = java.net.URLDecoder.decode(getPara("emp_name",""),"UTF-8");
		String dept = getPara("dept","");
		String unitName = getPara("unitname","");
		String year = getPara("year","");
		String month = getPara("month","");
		Page<Record> page = service.getPage(Integer.valueOf(curr),Integer.valueOf(pageSize),dept,empName,unitName,year,month);
		renderPage(page.getList(),"",page.getTotalRow());
	}
	/***
	 * save data
	 */
	public void save(){
		XdAttendanceSummary o = getModel(XdAttendanceSummary.class);
		XdAttendanceSummary summary = XdAttendanceSummary.dao.findById(o.getId());
		Double newCurmonBalance=Double.valueOf(o.getCurmonBalancehours()==null?"0":o.getCurmonBalancehours());
		Double oldCurmonBalance=Double.valueOf(summary.getCurmonBalancehours()==null?"0":summary.getCurmonBalancehours());


		double newAccBalanceHours = Double.valueOf(o.getCurmonAccbalancehours()) + newCurmonBalance - oldCurmonBalance;
		o.setCurmonAccbalancehours(String.valueOf(newAccBalanceHours));

		o.update();

		String ewardPunish = getPara("ewardPunish");

		String scheduleMonth = summary.getScheduleMonth();
		String scheduleYear = summary.getScheduleYear();




		XdRewardPunishmentSummary rps = XdRewardPunishmentSummary.dao.findFirst(
				"select  * from  xd_reward_punishment_summary where year='" + scheduleYear + "' and dept_id='" + summary.getDeptValue() + "'");
		String performRewardpunish = o.getPerformRewardpunish()==null?"0":o.getPerformRewardpunish().trim();
		if(!"".equals(performRewardpunish)&& Math.abs(Double.valueOf(performRewardpunish))>0){
			XdRewardPunishmentDetail detail=new XdRewardPunishmentDetail();
			detail.setYear(scheduleYear);
			detail.setMonth(scheduleMonth);
			detail.setDeptId(summary.getDeptValue());
			detail.setDeptName(summary.getDeptName());
			detail.setUnitId(summary.getUnitValue());
			detail.setUnitName(summary.getUnitName());
			detail.setProjectId(Integer.valueOf(summary.getProjectValue()));
			detail.setProjectName(summary.getProjectName());
			detail.setEmpName(summary.getEmpName());
			XdEmployee emp = XdEmployee.dao.findFirst("select * from  xd_employee where  name='" + summary.getEmpName() + "'");
			detail.setIdnum(emp==null?"":emp.getIdnum());
			detail.setRewardPunishment(Double.valueOf(performRewardpunish));
			detail.setRemarks(ewardPunish);
			detail.setCanDistribution("否");
			detail.setCreateDate(DateUtil.getCurrentTime());
			detail.setCreateUser(ShiroKit.getUserId());
			detail.save();
			if(rps!=null){
				rps.setQuota(rps.getQuota()+Double.valueOf(performRewardpunish));
				rps.setDisableQuota(rps.getDisableQuota()+Double.valueOf(performRewardpunish));
				rps.update();
			}else{
				rps=new XdRewardPunishmentSummary();
				rps.setYear(scheduleYear);
				rps.setMonth(scheduleMonth);
				rps.setDeptId(summary.getDeptValue());
				rps.setDeptName(summary.getDeptName());
				rps.setQuota(Double.valueOf(performRewardpunish));
				rps.setDisableQuota(Double.valueOf(performRewardpunish));
				rps.setAbleQuota(0.0);
				rps.save();
			}
		}else{

			XdRewardPunishmentDetail first = XdRewardPunishmentDetail.dao.findFirst(
					"select *from xd_reward_punishment_detail where year='" + scheduleYear + "' and  month='" + scheduleMonth + "' and emp_name='" + summary.getEmpName() + "'");
			if(first!=null){
				if(first.getCanDistribution().equals("是")){
					rps.setQuota(rps.getQuota()-first.getRewardPunishment());
					rps.setAbleQuota(rps.getAbleQuota()-first.getRewardPunishment());
				}else{
					rps.setQuota(rps.getQuota()-first.getRewardPunishment());
					rps.setDisableQuota(rps.getDisableQuota()-first.getRewardPunishment());
				}
				rps.update();
				first.delete();
			}
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
		XdAttendanceSummary summary =service.getById(id);
		String deptValue = summary.getDeptValue();
	/*	SysOrg org = SysOrg.dao.findById(deptValue);
		String operate = org.getOperate();
		int integer = Integer.valueOf(operate);*/
		String year = summary.getScheduleYear();
		XdRewardPunishmentSummary rps = XdRewardPunishmentSummary.dao.findFirst(
				"select  * from  xd_reward_punishment_summary where year='" + year + "' and dept_id='" + deptValue + "'");
		double ableQuota = rps.getAbleQuota();
		int ableQuota1 = (int) ableQuota;

		if(ableQuota1==0){
			setAttr("less","0");//最大
			setAttr("greate","-2000000");//最小
		}else if(ableQuota1<0){
			setAttr("less",Math.abs(ableQuota1));//最大
			setAttr("greate","-2000000");//最小
		}
		/*String scheduleYearMonth = summary.getScheduleYearMonth();
		List<XdDayModel> xdDayModels = XdDayModel.dao.find("select * from  xd_day_model where id like '" + scheduleYearMonth + "%' order by id");
		int daysNum=xdDayModels.size();
		setAttr("daysNum",daysNum+1);*/

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
	/**
	 * @Method exportRewardPunishExcel
	 * @Date 2023/2/6 20:43
	 * @Exception
	 * @Description  导出绩效奖惩
	 * @Author king
	 * @Version  1.0
	 * @Return void
	 */
	public void exportRewardPunishExcel() throws UnsupportedEncodingException {

		String dept=getPara("dept","");
		String year=getPara("year","");
		String month = getPara("month","");
//		String emp_name = new String(getPara("emp_name","").getBytes("ISO-8859-1"), "utf-8");

		String path = this.getSession().getServletContext().getRealPath("")+"/upload/export/"+ DateUtil.format(new Date(),21)+".xlsx";
		File file = service.exportRewardPunishExcel(path,dept,year,month);
		renderFile(file);
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
	public void exportOnDuty() throws UnsupportedEncodingException {

		String dept=getPara("dept","");
		String year=getPara("year","");
		String month = getPara("month","");
//		String emp_name = new String(getPara("emp_name","").getBytes("ISO-8859-1"), "utf-8");

		String path = this.getSession().getServletContext().getRealPath("")+"/upload/export/"+ DateUtil.format(new Date(),21)+".xlsx";
		File file = service.exportOnDuty(path,dept,year,month);
		renderFile(file);
	}


	/*public void getRcpList() {
		String id = getPara("id");
		List<XdAttendanceRcp> rcpList = XdAttendanceRcp.dao.find("select * from  xd_attendance_rcp where  attendid_id='" + id + "' order by id");
		renderJson(rcpList);
	}

	public void getDaysList() {
		String id = getPara("id");
		List<XdAttendanceRcp> rcpList = XdAttendanceRcp.dao.find("select * from  xd_attendance_days where  attendid_id='" + id + "' order by id");
		renderJson(rcpList);
	}*/

	public void getTableHeader(){
		String year = getPara("year","");
		String month = getPara("month","");
		String yearMonth=year+month;
		List<XdDayModel> xdDayModels = XdDayModel.dao.find("select * from  xd_day_model where id like '"+yearMonth+"%' order by id");
		LocalDate localDate = LocalDate.parse(year+"-"+month+"-01").minusMonths(1);
		String lastMonth = DateTimeFormatter.ofPattern("yyyyMM").format(localDate);
		XdDayModel lastMonLastDay = XdDayModel.dao.findFirst("select * from  xd_day_model where id like '" + lastMonth + "%' order by id desc");
		List<String> weekLists=new ArrayList<>();
		List<String> holidayLists=new ArrayList<>();
		if(lastMonLastDay!=null){
			weekLists.add(lastMonLastDay.getWeeks());
			if("0".equals(lastMonLastDay.getIsnatHoliday())){
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
					if("0".equals(xdDayModel.getIsnatHoliday())){
						holidayLists.add("-");
					}else{

						holidayLists.add(xdDayModel.getHolidays());
					}
				});
		setAttr("daysNum",xdDayModels.size());
		setAttr("weekLists",weekLists);
		setAttr("holidayLists",holidayLists);
		JSONObject jsonObject=new JSONObject();
		jsonObject.put("daysNum",xdDayModels.size());
		jsonObject.put("weekLists",weekLists);
		jsonObject.put("holidayLists",holidayLists);
		renderSuccess(jsonObject,"");
	}



	public void getOvTime(){
		String id = getPara("id");
		String field = getPara("field");
		XdAttendanceSummary summary  = XdAttendanceSummary.dao.findById(id);
		String yearMonth=summary.getScheduleYear()+"-"+summary.getScheduleMonth();
		LocalDate lastMonLastDay = LocalDate.parse(yearMonth+"-01").minusDays(1);
		DateTimeFormatter dtf=DateTimeFormatter.ofPattern("yyyy-MM-dd");


		List<XdDayModel> xdDayModels = XdDayModel.dao.find("select * from  xd_day_model where days like '" + yearMonth + "%' or days='" + lastMonLastDay + "'");
		String day = field.replace("day", "");
		int index = Integer.parseInt(day);


		XdOvertimeSummary overtimeSummary = XdOvertimeSummary.dao.findFirst("select *from  xd_overtime_summary " +
				" where   emp_name='"+summary.getEmpName()+"' and  apply_date='"+xdDayModels.get(index).getDays() + "' and source='2' and act_hours is not null and act_hours!=''");

		String returnOtHour="";
		if(overtimeSummary!=null && overtimeSummary.getActHours()!=null&& !overtimeSummary.getActHours().equals("")){
			String[] splitStart = overtimeSummary.getActStart().split(":");
			returnOtHour=returnOtHour+splitStart[0]+","+splitStart[1]+",";

			String[] splitEnd = overtimeSummary.getActEnd().split(":");
			returnOtHour=returnOtHour+splitEnd[0]+","+splitEnd[1]+",";
			returnOtHour=returnOtHour+overtimeSummary.getActHours();

		}


		renderSuccess(returnOtHour);

	}
	/**
	 * @Method updateCell
	 * @Date 2023/2/13 15:57
	 * @Description 更新单元格内容
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
		String start = getPara("start");
		String end = getPara("end");
//		String otHour = getPara("otHour");

		String otHour="";
		String [] startHM=null;
		String [] endHM=null;
		if(start!=null && start!=null){
			if( !"".equals(start)){
				startHM=start.split(":");
			}
			if(!"".equals(end)){
				endHM=end.split(":");
			}

			LocalDateTime startTime = LocalDateTime.of(2023, 01, 01, Integer.parseInt(startHM[0]), Integer.parseInt(startHM[1]), 00);
			LocalDateTime endTime = LocalDateTime.of(2023, 01, 01, Integer.parseInt(endHM[0]), Integer.parseInt(endHM[1]), 00);

			otHour =String.format("%.1f",( Duration.between(startTime, endTime).toMinutes()/ 60.0));
		}


		String returnOtHour="";

		XdAttendanceSummary summary  = XdAttendanceSummary.dao.findById(id);

		if("2".equals(type)){
			summary.setRemarks(modValue);
			summary.update();
		}else{

//			处理年假时间
			if(modValue.contains("年")|| oldValue.contains("年")){


				String scheduleYear = summary.getScheduleYear();
				String scheduleMonth = summary.getScheduleMonth();
				XdAnleaveSummary leave =XdAnleaveSummary.
						dao.findFirst("select  * from  xd_anleave_summary where  emp_name='"+summary.getEmpName()+"' and year='"+scheduleYear+"'");

				XdAttendanceDays attendanceDays= XdAttendanceDays.dao.findFirst("select  * from  xd_attendance_days where attendid_id='"+summary.getId()+"'");
				Double anDays = Double.valueOf(attendanceDays.getRestanleaveDays());
				if(leave!=null){

					if(attendanceDays!=null){
						leave.setSurplusDays(String.valueOf(Double.valueOf(leave.getSurplusDays())+anDays));
						leave.setSum(leave.getSum()-anDays);
					}
					leave.update();
				}

				XdAnleaveExecute	execute	 = XdAnleaveExecute.dao.findFirst("select * from  xd_anleave_execute where year='"+scheduleYear+"' and dept_id='"+summary.getDeptValue()+"'");
				Class  executeClazz= XdAnleaveExecute.class.getSuperclass();
				if(execute!=null){
					Method getMethod = null;
					try {
						getMethod = executeClazz.getMethod("getMonth"+ Integer.parseInt(scheduleMonth));
						double leaveDays= (double) getMethod.invoke(execute);
						Method setMethod =executeClazz.getMethod("setMonth"+Integer.parseInt(scheduleMonth),Double.class);
						setMethod.invoke(execute,leaveDays-anDays);

						execute.setSum(execute.getSum()-anDays);
						execute.setSurplus(execute.getSurplus()+anDays);
						DecimalFormat decimalFormat=new DecimalFormat(".00");
						execute.setExecutionRate(Double.valueOf(decimalFormat.format((execute.getSum()-anDays)/execute.getAnleaveDays())));
						execute.update();
					} catch (NoSuchMethodException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					}

				}
			}
//			处理年假时间

			Class  superclass = XdAttendanceSummary.class.getSuperclass();

			try {
				Method method = superclass.getMethod("set" + field.substring(0, 1).toUpperCase() + field.substring(1),String.class);

				method.invoke(summary, modValue);

				int index = Integer.parseInt(field.replace("day", ""));
				Map<String, XdShift> nameShiftObjMap= CheckAttendanceUtil.shfitsMap();

				String[] modifyFlags = summary.getFlags().split(",");
				String modify="";
				if(!oldValue.equals(modValue)){
					modifyFlags[index]="1";
					for (String modifyFlag : modifyFlags) {
						modify=modify+","+modifyFlag;
					}
					summary.setFlags(modify.replaceAll("^,",""));
				}


				String ot="";
				String dayOt="";
				String[] otFlags = summary.getOtflags().split(",");
				String[] dayOtFlags = summary.getDayOtFlags().split(",");
				String[] tipArr = summary.getTips().split(",");

				String tipIndex=tipArr[index];

				if(start!=null|| "on".equals(overtime)){
					otFlags[index]="1";
				}else{
					otFlags[index]="0";
				}


				if("on".equals(overtime)){

					dayOtFlags[index]="1";
					XdShift shift = nameShiftObjMap.get(modValue);

					if("0".equals(tipIndex)){
						tipIndex=shift.getBusitime()+"-"+shift.getUnbusitime();
					}else{
						XdShift oldShift = nameShiftObjMap.get(oldValue);
						if(oldShift!=null){
							tipIndex=tipIndex.replaceAll(oldShift.getBusitime()+"-"+oldShift.getUnbusitime(),"");
						}
						if(shift!=null){
							tipIndex=shift.getBusitime()+"-"+shift.getUnbusitime()+tipIndex;
						}
					}
					if("".equals(tipIndex)){
						tipArr[index]="0";
					}else{
						tipArr[index]=tipIndex;
					}
				}else{
					dayOtFlags[index]="0";
					if(!"0".equals(tipIndex)){
						XdShift oldShift = nameShiftObjMap.get(oldValue);
						if(oldShift!=null){
							tipIndex=tipIndex.replaceAll(oldShift.getBusitime()+"-"+oldShift.getUnbusitime(),"");
						}
						if("".equals(tipIndex)){
							tipArr[index]="0";
						}else{
							tipArr[index]=tipIndex;
						}
					}

				}

				for (String otFlag : otFlags) {
					ot=ot+","+otFlag;
				}
				summary.setOtflags(ot.replaceAll("^,",""));


				for (String otDay : dayOtFlags) {
					dayOt=dayOt+","+otDay;
				}
				summary.setDayOtFlags(dayOt.replaceAll("^,",""));

				String tipStr="";
				for (String tip : tipArr) {
					tipStr=tipStr+","+tip;
				}
				summary.setTips(tipStr.replaceAll("^,",""));



				String yearMonth=summary.getScheduleYear()+"-"+summary.getScheduleMonth();

				DateTimeFormatter dtf=DateTimeFormatter.ofPattern("yyyy-MM-dd");

				LocalDate lastMonLastDay = LocalDate.parse(yearMonth+"-01").minusDays(1);
				String lastMonthDay = dtf.format(lastMonLastDay);


				List<XdDayModel> dayModels =
						XdDayModel.dao.find("select * from  xd_day_model where days like '" + yearMonth + "%' or days='" + lastMonthDay + "'");



				String nextMonFirstDay = DateTimeFormatter.ofPattern("yyyyMMdd")
						.format(LocalDate.parse(dayModels.get(dayModels.size() - 1).getDays()).plusDays(1));

				XdDayModel nextMonFirstModel = XdDayModel.dao.findById(nextMonFirstDay);


				StringBuffer sb =new StringBuffer();
				dayModels.stream().forEach( xdDayModel-> {
					if("1".equals(xdDayModel.getIsnatHoliday())){
						sb.append(xdDayModel.getId()).append(",");
					}
				});
				if("1".equals(nextMonFirstModel.getIsnatHoliday())){
					sb.append(nextMonFirstModel.getId());
				}


				List<XdOvertimeSummary> settleList = XdOvertimeSummary.dao.find(
						"select * from  xd_overtime_summary where emp_name='"+summary.getEmpName()+
								"' and  super_days='"+dayModels.get(index).getDays()+"'");

				for (XdOvertimeSummary settle : settleList) {
					if(settle.getApplyStart()==null|| "".equals(settle.getApplyStart())){
						settle.delete();
					}else{
						settle.setActStart("");
						settle.setActEnd("");
						settle.setActHours("");
						settle.update();
					}
				}


				XdShift shift = nameShiftObjMap.get(modValue);

				XdEmployee emp = XdEmployee.dao.findFirst("select * from xd_employee where name ='" + summary.getEmpName() + "'");

				String indexDayId = dayModels.get(index).getId();
				String indexDays = dayModels.get(index).getDays();
				String startTime = (shift==null?"": shift.getBusitime());

				List<XdOvertimeSummary> otsList = XdOvertimeSummary.dao.find("select * from xd_overtime_summary" +
						" where super_days='" + indexDays
						+ "' and emp_name='" + summary.getEmpName()+"'");


				StringBuffer needSettle= new StringBuffer();

				Map<String,String >settleMap=new HashMap<>();

				if(shift!=null && startTime!=null && !"".equals(startTime)){
					boolean span = "1".equals(shift.getSpanDay());
					if(span){
						if(sb.indexOf(indexDayId)!=-1){
							settleMap.put(indexDays+","+shift.getBusitime()+",24:00",shift.getCurdayHours()+",0");
						}else{
							if("on".equals(overtime)){
								settleMap.put(indexDays+","+shift.getBusitime()+",24:00",shift.getCurdayHours()+",1");
							}
						}
						LocalDate nextDay = LocalDate.parse(indexDays).plusDays(1);
						String nextDayStr = dtf.format(nextDay);

						if(sb.indexOf(nextDayStr.replaceAll("-",""))!=-1){
							settleMap.put(nextDayStr+",0:00,"+shift.getUnbusitime(),shift.getSpanHours()+",0");
						}else{
							if("on".equals(overtime)){
								settleMap.put(nextDayStr+",0:00,"+shift.getUnbusitime(),shift.getSpanHours()+",1");
							}
						}


					}else{
						if(sb.indexOf(indexDayId)!=-1){
							settleMap.put(indexDays+","+shift.getBusitime()+","+shift.getUnbusitime(),shift.getHours()+",0");
						}else{
							if("on".equals(overtime)){
								settleMap.put(indexDays+","+shift.getBusitime()+","+shift.getUnbusitime(),shift.getHours()+",1");
							}
						}
					}
				}

				for (XdOvertimeSummary ots : otsList) {

					String ovStr = ots.getApplyDate() + "," + ots.getApplyStart() + "," + ots.getApplyEnd();

//					XdOvertimeSummary sos= new XdOvertimeSummary();
//					BeanUtils.copyProperties(sos,ots);
					if(settleMap.get(ovStr)!=null){
						ots.setActHours(ots.getApplyHours());
						ots.setActStart(ots.getApplyStart());
						ots.setActEnd(ots.getApplyEnd());
						//sos.setApplyType(settleMap.get(ovStr).split(",")[1]);
						ots.update();
						settleMap.remove(ovStr);
					}
				}
				Set<String> keySet = settleMap.keySet();
				for (String key : keySet) {
					String[] split = key.split(",");
					String s = settleMap.get(key);
					String[] split1 = s.split(",");

					XdOvertimeSummary sots= new XdOvertimeSummary();
					sots.setApplyDate(split[0]);
					sots.setActStart(split[1]);
					sots.setActEnd(split[2]);
					sots.setActHours(split1[0]);
					sots.setApplyType(split1[1]);
					sots.setSuperDays(indexDays);
					sots.setEmpName(summary.getEmpName());
					sots.setEmpNum(summary.getEmpNum());
					XdEmployee employee=XdEmployee.dao.findFirst("select * from  xd_employee where name='" + summary.getEmpName() + "'");
					sots.setEmpIdnum(employee==null?"":employee.getIdnum());
					sots.setCreateUser(ShiroKit.getUserId());
					sots.setCreateDate(DateUtil.getCurrentTime());
					sots.setProjectName(summary.getProjectName());
					sots.setProjectId(summary.getProjectValue());
					sots.setDeptName(summary.getDeptName());
					sots.setDeptId(summary.getDeptValue());
				//	sots.setSource("1");
					sots.save();
				}



				if(!"".equals(start)&&!"".equals(end)&&!"".equals(otHour)){
					XdOvertimeSummary overtimeSummary = XdOvertimeSummary.dao.findFirst("select *from  xd_overtime_summary " +
							" where   apply_start='" + start+"' and apply_date='"+dayModels.get(index).getDays()
							+ "' and  apply_end='" + end + "' and source='2'");
					if(overtimeSummary==null){

						//XdEmployee emp = XdEmployee.dao.findFirst("select * from xd_employee where name ='" + summary.getEmpName() + "'");
						XdOvertimeSummary ots=new XdOvertimeSummary();
						ots.setEmpNum(summary.getEmpNum());
						ots.setEmpName(summary.getEmpName());
						ots.setEmpIdnum(emp==null?"":emp.getIdnum());
						ots.setProjectName(summary.getProjectName());
						ots.setProjectId(summary.getProjectValue());
						ots.setDeptName(summary.getDeptName());
						ots.setDeptId(summary.getDeptValue());
						ots.setCreateUser(ShiroKit.getUserId());
						ots.setCreateDate(DateUtil.getCurrentTime());
						ots.setApplyDate(dayModels.get(index).getDays());
						ots.setActStart(start);
						ots.setActEnd(end);
						ots.setActHours(otHour+"");
						ots.setSuperDays(dayModels.get(index).getDays());
						if(sb.indexOf(dayModels.get(index).getId())!=-1){
							ots.setApplyType("0");
						}else{
							ots.setApplyType("1");
						}
						ots.setSource("2");
						ots.save();
					}else{
						overtimeSummary.setActStart(start);
						overtimeSummary.setActEnd(end);
						overtimeSummary.setActHours(otHour);
						overtimeSummary.update();
					}
				}




				double othours=0;//平时加班时间
				double naOTHours=0;
				double work_hour=0;//出勤时间
				int middleShiftDays=0;
				int nightShiftDays=0;
				double alreayAnnualLeave=0;
				double dutyCharge=0;
				int monthWorkDays=0;
				int commWorkdDay=0;
				double highTempCharge=0;
				int sickLeaveDays=0;
				int personalLeaveDays=0;
				int newLeaveDays=0;

				int absentworkDays=0;//旷工天数
				boolean shift18A=false;

				String canOt="0";//本月可加班


				for (int i = 0; i < dayModels.size(); i++) {
					String suffix=(i<10?"0"+i:i+"");
					String  shiftName = (String) superclass.getMethod("getDay" + suffix).invoke(summary);
					XdDayModel dayModel = dayModels.get(i);

					shift = nameShiftObjMap.get(shiftName);

					if(i==0){
//					上个月最后一天
						if(shift!=null){
							if("1".equals(shift.getSpanDay())){
								//是跨天
								XdDayModel firstDay = dayModels.get(1);
								if(sb.indexOf(firstDay.getId())==-1 && "0".equals(dayOtFlags[i])){
									work_hour+=Double.valueOf(shift.getSpanHours());
								}
							}
						}
					}else{
						//本月

						//结算开始
						if(shiftName!=null&&!shiftName.equals("")){
							if("18A".equals(shiftName)){
								shift18A=true;
							}
							monthWorkDays++;

							XdShift xdShift = nameShiftObjMap.get(shiftName);
							if(xdShift!=null && xdShift.getBusitime()!=null&& !xdShift.getBusitime().equals("")){
								commWorkdDay++;
								if("1".equals(xdShift.getSpanDay())){

									if(sb.indexOf(dayModel.getId())==-1 && "0".equals(dayOtFlags[i])){
										work_hour+=shift.getCurdayHours();
									}
									LocalDate localDate = LocalDate.parse(dayModel.getDays()).plusDays(1);
									String nextDay  = dtf.format(localDate);
									//是跨天且第二天是法定假日
									if(sb.indexOf(nextDay.replaceAll("-",""))==-1 && "0".equals(dayOtFlags[i]) && i<dayModels.size()-1){

										work_hour+=shift.getSpanHours();
									}
								}else{
//									不是跨天
									if(sb.indexOf(dayModel.getId())==-1 &&  "0".equals(dayOtFlags[i])){
										work_hour+=Double.valueOf(shift.getHours());
									}
								}
							}else{

								if(shiftName.contains("年")){
									String annual = shiftName.replace("年", "");
									if(annual.equals("")){
										alreayAnnualLeave+=1;
									}else{
										alreayAnnualLeave+=Double.valueOf(annual);
									}
								}

								if(shiftName.equals("病")){
									sickLeaveDays++;
								}
								if(shiftName.equals("事")){
									personalLeaveDays++;
								}
								if(shiftName.equals("新离")){
									newLeaveDays++;
								}
								if(shiftName.equals("旷")){
									newLeaveDays++;
								}

								if(shiftName.contains("年")||shiftName.contains("婚")||shiftName.contains("陪产")||shiftName.contains("丧")||shiftName.contains("育")){
									work_hour+=Double.valueOf(shift.getHours());
								}

								if("事,病,旷,新离".indexOf(shiftName)!=-1){
									canOt="1";//不可加班
								}

							}
							//计算开始
							if(shift.getMiddleshift()!=null&& !shift.getMiddleshift().equals("")){
								middleShiftDays++;//中班天数
							}
							if(shift.getNigthshift()!=null&& !shift.getNigthshift().equals("")){
								nightShiftDays++;//夜班天数
							}



							if(shift.getDutyamount()!=null && !"".equals(shift.getDutyamount())){
								dutyCharge+=Double.valueOf(shift.getDutyamount());//值班费
							}
							/*attendancesummary表
							本月工时 ：人力资源部设置的
							本月工时结余=本月实际工时-本月工时
							上月累计工时=上个月的工时结余
							本月累计工时=所有的累积工时相加

							当月待结算工时=平时加班小数-36（上线36）
							累计待结算工时=所有月份相加*/

							//计算结束
						}
						//结算结束

					}

				}
				int needWorkday=0;
				int otNum=0;
				for (int i = 1; i <=dayOtFlags.length-1; i++) {
					if(dayOtFlags[i].equals("1")){
						otNum++;
					}
				}
				needWorkday = monthWorkDays - otNum;
				if(summary.getScheduleMonth().endsWith("7")||summary.getScheduleMonth().endsWith("8")||summary.getScheduleMonth().endsWith("9")){

					if(needWorkday<=commWorkdDay){
						highTempCharge=300;
					}else{
						highTempCharge=300*commWorkdDay/needWorkday;
					}
				}


				/*本月工时结余=本月实际工时-本月工时
				上月累计工时=上个月的工时结余
				本月累计工时=所有的累积工时相加

				当月待结算工时=平时加班小数-36（上线36）
				累计待结算工时=所有月份相加*/
				summary.setCurmonActworkhours(String.valueOf(work_hour));//本月实际工时

				double curmonBalancehours = work_hour - Double.valueOf(summary.getCurmonWorkhours());//本月工时结余=本月实际工时-本月工时

				summary.setCurmonBalancehours(String.valueOf(curmonBalancehours));


				List<XdAttendanceSummary> xdAttendanceSummaries = XdAttendanceSummary.dao.find("select * from  xd_attendance_summary where schedule_year='" + summary.getScheduleYear()
						+ "' and emp_name='" + summary.getEmpName() + "' order by schedule_month desc");
				if(xdAttendanceSummaries.size()==1){
					summary.setPremonAccbalancehours("0");
				}else{
					summary.setPremonAccbalancehours(xdAttendanceSummaries.get(1).getPremonAccbalancehours());//上月累计工时=上个月的工时结余
				}
				//计算加班时长 开始
				List<XdOvertimeSummary> ovList =	XdOvertimeSummary.dao.find(
						"select * from  xd_overtime_summary where emp_name='"+summary.getEmpName()+
								"' and apply_date like '"+yearMonth+"%'");
				for (XdOvertimeSummary ovs : ovList) {
					String hours="0";
					if(ovs.getActHours()!=null && !"".equals(ovs.getActHours())){
						hours=ovs.getActHours();
					}
					if(ovs.getApplyType().equals("0")){
						naOTHours+=Double.valueOf(hours);
					}else{
						othours+=Double.valueOf(hours);
					}
				}
				//计算加班时长结束


				double gcurmonBalancehours=0;
				double settlehours=0;
				for (XdAttendanceSummary xdAttendanceSummary : xdAttendanceSummaries) {
					gcurmonBalancehours+=Double.valueOf(xdAttendanceSummary.getCurmonBalancehours());

					settlehours+=Double.valueOf(xdAttendanceSummary.getCurmonSettlehours().equals("")?"0":xdAttendanceSummary.getCurmonSettlehours());
				}
				summary.setCurmonAccbalancehours(String.valueOf(gcurmonBalancehours));

				summary.setCurmonSettlehours(othours-36 <0?"0":String.valueOf(othours-36));
				summary.setAccSettlehours(String.valueOf(settlehours));
				summary.setCanOvertime(canOt);
				summary.setOthours(othours);
				summary.setAnnualleaveDays(String.valueOf(alreayAnnualLeave));

				List<XdOvertimeSummary> overtTimeList =	XdOvertimeSummary.dao.find(
						"select * from  xd_overtime_summary where emp_name='"+summary.getEmpName()+
								"' and super_days='"+dayModels.get(index).getDays()+"'");

				String tips="";
				for (XdOvertimeSummary ots : overtTimeList) {
					if(ots.getActHours()!=null && !ots.getActHours().equals("")){

						tips=tips+ots.getActStart()+"-"+ots.getActEnd()+"、";
					}
				}
				String[] tipsA = summary.getTips().split(",");
				tipsA[index]=tips.replaceAll("、$","");

				String tip="";
				for (String t : tipsA) {
					tip=tip+t+",";
				}


				summary.setTips(tip.replaceAll(",$",""));


				//summary.setWorkHour(work_hour);
				returnOtHour=naOTHours+"*"+othours+"*"+summary.getCurmonActworkhours()+"*"+canOt
						+"*"+summary.getFlags()+"*"+summary.getOtflags()+"*"+summary.getDayOtFlags()+"*"+summary.getTips()+"*"+modValue;
				summary.update();
				Db.update("update  xd_attendance_days" +
						" set ordinary_overtime='"+othours+"' ,national_overtime='"+naOTHours+"',duty_charge='"+dutyCharge+"',midshift_days='"+middleShiftDays+"'," +
						"nightshift_days='"+nightShiftDays+"',hightemp_allowance='"+highTempCharge+"',monshould_workdays='"+needWorkday+"',sickeleave_days='"+sickLeaveDays+"'," +
						"casualleave_days='"+personalLeaveDays+"',absenceduty_days='"+newLeaveDays+"',absentwork_days='"+absentworkDays+"',restanleave_days='"+alreayAnnualLeave+"'" +
						"where attendid_id='"+summary.getId()+"'");

				Db.update("update xd_attendance_sheet set ordinary_ot='"+othours+"',national_ot='"+naOTHours+"'," +
						"duty_charge='"+dutyCharge+"',mid_shifts='"+middleShiftDays+"',night_shifts='"+nightShiftDays+"'," +
						"hightemp_allowance='"+highTempCharge+"',should_workdays='"+needWorkday+"'" +
						",sick_leave='"+sickLeaveDays+"',casual_leave='"+personalLeaveDays+"',absence_duty='"+newLeaveDays+"',absent_work='"+absentworkDays+"',rest_anleave='"+alreayAnnualLeave+"'" +
						" where summary_id='"+summary.getId()+"'");

				if(shift18A){
					String ym = summary.getScheduleYear() + summary.getScheduleMonth();
					Db.delete("delete from  xd_rcp_summary where summary_id='"+summary.getId()+"' and   rcp_month_year='"+ym+"'");

					XdRcpSummary rcp =new XdRcpSummary();
					rcp.setSummaryId(summary.getId());
					rcp.setDeptValue(summary.getDeptValue());
					rcp.setDeptName(summary.getDeptName());
					rcp.setUnitValue(summary.getUnitValue());
					rcp.setUnitName(summary.getUnitName());
					rcp.setProjectValue(summary.getProjectValue());
					rcp.setProjectName(summary.getProjectName());
					rcp.setEmpName(summary.getEmpName());
					rcp.setIdnum(emp==null?"":emp.getIdnum());
					rcp.setWorStation(summary.getWorkStation());
					rcp.setShfitName("18A");
					int mnShiftDays = middleShiftDays + nightShiftDays;
					rcp.setRcpDays(String.valueOf(mnShiftDays));
					rcp.setWorkDays(String.valueOf(monthWorkDays));
					rcp.setRcpStandard("100");
					if(mnShiftDays>=26){
						rcp.setRental("100");
					}else{
						String rcptenal = String.valueOf(mnShiftDays * 100 / (double) 26).substring(0,4);
						rcp.setRental(rcptenal);
					}
					rcp.setRcpYear(summary.getScheduleYear());
					rcp.setRcpMonthYear(summary.getScheduleMonth());
					rcp.setRcpMonthYear(ym);
					rcp.setStatus("0");
					rcp.setCreateUser("出勤生成");
					rcp.setCreateDate(DateUtil.getCurrentTime());
					rcp.save();

				}
//				select  * from  xd_anleave_summary where  emp_name='' and year=''
				if(modValue.contains("年")|| oldValue.contains("年")) {
					XdAnleaveSummary annLeave = XdAnleaveSummary.dao.findFirst("select  * from  xd_anleave_summary where  emp_name='' and year=''");
					if (annLeave != null) {
						annLeave.setSurplusDays(String.valueOf(Double.valueOf(annLeave.getSurplusDays()) - alreayAnnualLeave));
						annLeave.setSum(annLeave.getSum()+alreayAnnualLeave);
						annLeave.update();
					}

					String scheduleYear = summary.getScheduleYear();
					String scheduleMonth = summary.getScheduleMonth();
					XdAnleaveExecute	execute	 = XdAnleaveExecute.dao.findFirst("select * from  xd_anleave_execute where year='"+scheduleYear+"' and dept_id='"+summary.getDeptValue()+"'");
					Class  executeClazz= XdAnleaveExecute.class.getSuperclass();
					if(execute!=null){
						Method getMethod =executeClazz.getMethod("getMonth"+ Integer.parseInt(scheduleMonth));
						double leaveDays= (double) getMethod.invoke(execute);
						Method setMethod =executeClazz.getMethod("setMonth"+Integer.parseInt(scheduleMonth),Double.class);
						setMethod.invoke(execute,leaveDays+alreayAnnualLeave);

						execute.setSum(execute.getSum()+alreayAnnualLeave);
						execute.setSurplus(execute.getAnleaveDays()-execute.getSum());
						DecimalFormat decimalFormat=new DecimalFormat(".00");
						execute.setExecutionRate(Double.valueOf(decimalFormat.format((execute.getSum()+alreayAnnualLeave)*100/execute.getAnleaveDays())));
						execute.update();
					}

					List<XdAnleaveExecute> executesList = XdAnleaveExecute.dao.find("select * from  xd_anleave_execute where year='" + scheduleYear + "'");
					//Class exeClazz= XdAnleaveExecute.class.getSuperclass();
					double monthAnual=0;
					double sumExecute=0;
					double surplusExecute=0;
					Method getMethod=executeClazz.getMethod("getMonth"+ Integer.parseInt(scheduleMonth));
					Method setMethod =executeClazz.getMethod("setMonth"+Integer.parseInt(scheduleMonth),Double.class);


					XdAnleaveExecute totleExecute =null;

					for (XdAnleaveExecute exe : executesList) {
						if(exe.getDeptName().equals("合计")){
							totleExecute=exe;
						}else{
							monthAnual+= (double) getMethod.invoke(exe);
							sumExecute+=exe.getSum();
							surplusExecute+=exe.getSurplus();
						}

					}
					setMethod.invoke(totleExecute,monthAnual);
					totleExecute.setSum(sumExecute);
					totleExecute.setSurplus(surplusExecute);
					DecimalFormat decimalFormat=new DecimalFormat(".00");
					totleExecute.setExecutionRate(Double.valueOf(decimalFormat.format(sumExecute*100/totleExecute.getAnleaveDays())));
					totleExecute.update();

				}


			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}

		}


		renderSuccess(String.valueOf(returnOtHour));

	}


	public void updateCell20230314(){

		String id = getPara("id");
		String oldValue = getPara("oldValue");
		String modValue = getPara("modValue");
		String overtime = getPara("overtime");
		String field = getPara("field");
		String type = getPara("type");

		String returnOtHour="";

		XdAttendanceSummary summary  = XdAttendanceSummary.dao.findById(id);

		if("2".equals(type)){
			summary.setRemarks(modValue);
			summary.update();
		}else{

//			处理年假时间
			if(modValue.contains("年")|| oldValue.contains("年")){


				String scheduleYear = summary.getScheduleYear();
				String scheduleMonth = summary.getScheduleMonth();
				XdAnleaveSummary leave =XdAnleaveSummary.
						dao.findFirst("select  * from  xd_anleave_summary where  emp_name='"+summary.getEmpName()+"' and year='"+scheduleYear+"'");

				XdAttendanceDays attendanceDays= XdAttendanceDays.dao.findFirst("select  * from  xd_attendance_days where attendid_id='"+summary.getId()+"'");
				Double anDays = Double.valueOf(attendanceDays.getRestanleaveDays());
				if(leave!=null){

					if(attendanceDays!=null){
						leave.setSurplusDays(String.valueOf(Double.valueOf(leave.getSurplusDays())+anDays));
						leave.setSum(leave.getSum()-anDays);
					}
					leave.update();
				}

				XdAnleaveExecute	execute	 = XdAnleaveExecute.dao.findFirst("select * from  xd_anleave_execute where year='"+scheduleYear+"' and dept_id='"+summary.getDeptValue()+"'");
				Class  executeClazz= XdAnleaveExecute.class.getSuperclass();
				if(execute!=null){
					Method getMethod = null;
					try {
						getMethod = executeClazz.getMethod("getMonth"+ Integer.parseInt(scheduleMonth));
						double leaveDays= (double) getMethod.invoke(execute);
						Method setMethod =executeClazz.getMethod("setMonth"+Integer.parseInt(scheduleMonth),Double.class);
						setMethod.invoke(execute,leaveDays-anDays);

						execute.setSum(execute.getSum()-anDays);
						execute.setSurplus(execute.getSurplus()+anDays);
						DecimalFormat decimalFormat=new DecimalFormat(".00");
						execute.setExecutionRate(Double.valueOf(decimalFormat.format((execute.getSum()-anDays)/execute.getAnleaveDays())));
						execute.update();
					} catch (NoSuchMethodException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					}

				}
			}



//			处理年假时间

			Class  superclass = XdAttendanceSummary.class.getSuperclass();

			try {
				Method method = superclass.getMethod("set" + field.substring(0, 1).toUpperCase() + field.substring(1),String.class);
				method.invoke(summary, modValue);

				int index = Integer.parseInt(field.replace("day", ""));
				Map<String, XdShift> nameShiftObjMap= CheckAttendanceUtil.shfitsMap();

				String[] modifyFlags = summary.getFlags().split(",");
				modifyFlags[index]="1";

				String modify="";
				for (String modifyFlag : modifyFlags) {
					modify=modify+","+modifyFlag;
				}
				summary.setFlags(modify.replaceAll("^,",""));

				String ot="";
				String[] otFlags = summary.getOtflags().split(",");

				String[] tipArr = summary.getTips().split(",");
				String tipIndex=tipArr[index];

				if("on".equals(overtime)){

					otFlags[index]="1";
					XdShift shift = nameShiftObjMap.get(modValue);

					if("0".equals(tipIndex)){
						tipIndex=shift.getBusitime()+"-"+shift.getUnbusitime();
					}else{
						XdShift oldShift = nameShiftObjMap.get(oldValue);
						if(oldShift!=null){
							tipIndex=tipIndex.replaceAll(oldShift.getBusitime()+"-"+oldShift.getUnbusitime(),"");
						}
						if(shift!=null){
							tipIndex=shift.getBusitime()+"-"+shift.getUnbusitime()+tipIndex;
						}
					}
					if("".equals(tipIndex)){
						tipArr[index]="0";
					}else{
						tipArr[index]=tipIndex;
					}
				}else{

					otFlags[index]="0";
					if(!"0".equals(tipIndex)){
						XdShift oldShift = nameShiftObjMap.get(oldValue);
						if(oldShift!=null){
							tipIndex=tipIndex.replaceAll(oldShift.getBusitime()+"-"+oldShift.getUnbusitime(),"");
						}
						if("".equals(tipIndex)){
							tipArr[index]="0";
						}else{
							tipArr[index]=tipIndex;
						}
					}

				}

				for (String otFlag : otFlags) {
					ot=ot+","+otFlag;
				}
				summary.setOtflags(ot.replaceAll("^,",""));

				String tipStr="";
				for (String tip : tipArr) {
					tipStr=tipStr+","+tip;
				}
				summary.setTips(tipStr.replaceAll("^,",""));



				String yearMonth=summary.getScheduleYear()+"-"+summary.getScheduleMonth();

				DateTimeFormatter dtf=DateTimeFormatter.ofPattern("yyyy-MM-dd");

				LocalDate lastMonLastDay = LocalDate.parse(yearMonth+"-01").minusDays(1);
				String lastMonthDay = dtf.format(lastMonLastDay);


				List<XdDayModel> dayModels =
						XdDayModel.dao.find("select * from  xd_day_model where days like '" + yearMonth + "%' or days='" + lastMonthDay + "'");



				String nextMonFirstDay = DateTimeFormatter.ofPattern("yyyyMMdd")
						.format(LocalDate.parse(dayModels.get(dayModels.size() - 1).getDays()).plusDays(1));

				XdDayModel nextMonFirstModel = XdDayModel.dao.findById(nextMonFirstDay);


				StringBuffer sb =new StringBuffer();
				dayModels.stream().forEach( xdDayModel-> {
					if("1".equals(xdDayModel.getIsnatHoliday())){
						sb.append(xdDayModel.getId()).append(",");
					}
				});
				if("1".equals(nextMonFirstModel.getIsnatHoliday())){
					sb.append(nextMonFirstModel.getId());
				}


				List<XdOvertimeSummary> settleList = XdOvertimeSummary.dao.find(
						"select * from  xd_overtime_summary where emp_name='"+summary.getEmpName()+
								"' and source='1' and super_days='"+dayModels.get(index).getDays()+"'");

				for (XdOvertimeSummary settle : settleList) {
					if(settle.getApplyStart()==null|| "".equals(settle.getApplyStart())){
						settle.delete();
					}else{
						settle.setActHours("");
						settle.setActEnd("");
						settle.setActHours("");
						settle.update();
					}
				}


				XdShift shift = nameShiftObjMap.get(modValue);

				XdEmployee emp = XdEmployee.dao.findFirst("select * from xd_employee where name ='" + summary.getEmpName() + "'");

				String indexDayId = dayModels.get(index).getId();
				String indexDays = dayModels.get(index).getDays();
				String startTime = (shift==null?"": shift.getBusitime());

				List<XdOvertimeSummary> otsList = XdOvertimeSummary.dao.find("select * from xd_overtime_summary" +
						" where super_days='" + indexDays
						+ "' and emp_name='" + summary.getEmpName()+"' and source='1'");


				StringBuffer needSettle= new StringBuffer();

				Map<String,String >settleMap=new HashMap<>();

				if(shift!=null && startTime!=null && !"".equals(startTime)){
					boolean span = "1".equals(shift.getSpanDay());
					if(span){
						if(sb.indexOf(indexDayId)!=-1){
							settleMap.put(indexDays+","+shift.getBusitime()+",24:00",shift.getCurdayHours()+",0");
						}else{
							if("on".equals(overtime)){
								settleMap.put(indexDays+","+shift.getBusitime()+",24:00",shift.getCurdayHours()+",1");
							}
						}
						LocalDate nextDay = LocalDate.parse(indexDays).plusDays(1);
						String nextDayStr = dtf.format(nextDay);

						if(sb.indexOf(nextDayStr.replaceAll("-",""))!=-1){
							settleMap.put(nextDayStr+",0:00,"+shift.getUnbusitime(),shift.getSpanHours()+",0");
						}else{
							if("on".equals(overtime)){
								settleMap.put(indexDays+",0:00,"+shift.getUnbusitime(),shift.getSpanHours()+",1");
							}
						}


					}else{
						if(sb.indexOf(indexDayId)!=-1){
							settleMap.put(indexDays+","+shift.getBusitime()+","+shift.getUnbusitime(),shift.getHours()+",0");
						}else{
							if("on".equals(overtime)){
								settleMap.put(indexDays+","+shift.getBusitime()+","+shift.getUnbusitime(),shift.getHours()+",1");
							}
						}
					}
				}

				for (XdOvertimeSummary ots : otsList) {

					String ovStr = ots.getApplyDate() + "," + ots.getApplyStart() + "," + ots.getApplyEnd();

//					XdOvertimeSummary sos= new XdOvertimeSummary();
//					BeanUtils.copyProperties(sos,ots);
					if(settleMap.get(ovStr)!=null){
						ots.setActHours(ots.getApplyHours());
						ots.setActStart(ots.getApplyStart());
						ots.setActEnd(ots.getApplyEnd());
						//sos.setApplyType(settleMap.get(ovStr).split(",")[1]);
						ots.update();
						settleMap.remove(ovStr);
					}
				}
				Set<String> keySet = settleMap.keySet();
				for (String key : keySet) {
					String[] split = key.split(",");
					String s = settleMap.get(key);
					String[] split1 = s.split(",");

					XdOvertimeSummary sots= new XdOvertimeSummary();
					sots.setApplyDate(split[0]);
					sots.setActStart(split[1]);
					sots.setActEnd(split[2]);
					sots.setActHours(split1[0]);
					sots.setApplyType(split1[1]);
					sots.setSuperDays(indexDays);
					sots.setEmpName(summary.getEmpName());
					sots.setEmpNum(summary.getEmpNum());
					XdEmployee employee=XdEmployee.dao.findFirst("select * from  xd_employee where name='" + summary.getEmpName() + "'");
					sots.setEmpIdnum(employee==null?"":employee.getIdnum());
					sots.setCreateUser(ShiroKit.getUserId());
					sots.setCreateDate(DateUtil.getCurrentTime());
					sots.setProjectName(summary.getProjectName());
					sots.setProjectId(summary.getProjectValue());
					sots.setDeptName(summary.getDeptName());
					sots.setDeptId(summary.getDeptValue());
					sots.setSource("1");
					sots.save();
				}



				double othours=0;//平时加班时间
				double naOTHours=0;
				double work_hour=0;//出勤时间
				int middleShiftDays=0;
				int nightShiftDays=0;
				double alreayAnnualLeave=0;
				double dutyCharge=0;
				int monthWorkDays=0;
				int commWorkdDay=0;
				double highTempCharge=0;
				int sickLeaveDays=0;
				int personalLeaveDays=0;
				int newLeaveDays=0;

				int absentworkDays=0;//旷工天数
				boolean shift18A=false;


				for (int i = 0; i < dayModels.size(); i++) {
					String suffix=(i<10?"0"+i:i+"");
					String  shiftName = (String) superclass.getMethod("getDay" + suffix).invoke(summary);
					XdDayModel dayModel = dayModels.get(i);

					shift = nameShiftObjMap.get(shiftName);

					if(i==0){
//					上个月最后一天
						if(shift!=null){
							if("1".equals(shift.getSpanDay())){
								//是跨天
								XdDayModel firstDay = dayModels.get(1);
								if(sb.indexOf(firstDay.getId())==-1 && "0".equals(otFlags[i])){
									work_hour+=Double.valueOf(shift.getSpanHours());
								}
							}
						}
					}else{
						//本月

						//结算开始
						if(shiftName!=null&&!shiftName.equals("")){
							if("18A".equals(shiftName)){
								shift18A=true;
							}
							monthWorkDays++;

							XdShift xdShift = nameShiftObjMap.get(shiftName);
							if(xdShift!=null && xdShift.getBusitime()!=null&& !xdShift.getBusitime().equals("")){
								commWorkdDay++;
								if("1".equals(xdShift.getSpanDay())){

									if(sb.indexOf(dayModel.getId())==-1 && "0".equals(otFlags[i])){
										work_hour+=shift.getCurdayHours();
									}
									LocalDate localDate = LocalDate.parse(dayModel.getDays()).plusDays(1);
									String nextDay  = dtf.format(localDate);
									//是跨天且第二天是法定假日
									if(sb.indexOf(nextDay.replaceAll("-",""))==-1 && "0".equals(otFlags[i]) && i<dayModels.size()-1){

										work_hour+=shift.getSpanHours();
									}
								}else{
//									不是跨天
									if(sb.indexOf(dayModel.getId())==-1 &&  "0".equals(otFlags[i])){
										work_hour+=Double.valueOf(shift.getHours());
									}
								}
							}
							//计算开始
							if(shift.getMiddleshift()!=null&& !shift.getMiddleshift().equals("")){
								middleShiftDays++;//中班天数
							}
							if(shift.getNigthshift()!=null&& !shift.getNigthshift().equals("")){
								nightShiftDays++;//夜班天数
							}


							if(shiftName.contains("年")){
								String annual = shiftName.replace("年", "");
								if(annual.equals("")){
									alreayAnnualLeave+=1;
								}else{
									alreayAnnualLeave+=Double.valueOf(annual);
								}
							}
							if(shift.getDutyamount()!=null && !"".equals(shift.getDutyamount())){
								dutyCharge+=Double.valueOf(shift.getDutyamount());//值班费
							}
							/*attendancesummary表
							本月工时 ：人力资源部设置的
							本月工时结余=本月实际工时-本月工时
							上月累计工时=上个月的工时结余
							本月累计工时=所有的累积工时相加

							当月待结算工时=平时加班小数-36（上线36）
							累计待结算工时=所有月份相加*/
							if(shiftName.equals("病")){
								sickLeaveDays++;
							}
							if(shiftName.equals("事")){
								personalLeaveDays++;
							}
							if(shiftName.equals("新离")){
								newLeaveDays++;
							}
							if(shiftName.equals("旷")){
								newLeaveDays++;
							}
							//计算结束
						}
						//结算结束

					}

				}
				int needWorkday=0;
				int otNum=0;
				for (int i = 1; i <=otFlags.length-1; i++) {
					if(otFlags[i].equals("1")){
						otNum++;
					}
				}
				needWorkday = monthWorkDays - otNum;
				if(summary.getScheduleMonth().endsWith("7")||summary.getScheduleMonth().endsWith("8")||summary.getScheduleMonth().endsWith("9")){

					if(needWorkday<=commWorkdDay){
						highTempCharge=300;
					}else{
						highTempCharge=300*commWorkdDay/needWorkday;
					}
				}


				/*本月工时结余=本月实际工时-本月工时
				上月累计工时=上个月的工时结余
				本月累计工时=所有的累积工时相加

				当月待结算工时=平时加班小数-36（上线36）
				累计待结算工时=所有月份相加*/
				summary.setCurmonActworkhours(String.valueOf(work_hour));

				double curmonBalancehours = work_hour - Double.valueOf(summary.getCurmonWorkhours());//本月工时结余=本月实际工时-本月工时

				summary.setCurmonBalancehours(String.valueOf(curmonBalancehours));


				List<XdAttendanceSummary> xdAttendanceSummaries = XdAttendanceSummary.dao.find("select * from  xd_attendance_summary where schedule_year='" + summary.getScheduleYear()
						+ "' and emp_name='" + summary.getEmpName() + "' order by schedule_month desc");
				if(xdAttendanceSummaries.size()==1){
					summary.setPremonAccbalancehours("0");
				}else{
					summary.setPremonAccbalancehours(xdAttendanceSummaries.get(1).getPremonAccbalancehours());//上月累计工时=上个月的工时结余
				}
				//计算加班时长 开始
				List<XdOvertimeSummary> ovList =	XdOvertimeSummary.dao.find(
						"select * from  xd_overtime_summary where emp_name='"+summary.getEmpName()+
								"' and apply_date like '"+yearMonth+"%'");
				for (XdOvertimeSummary ovs : ovList) {
					String hours="0";
					if(ovs.getActHours()!=null && !"".equals(ovs.getActHours())){
						hours=ovs.getActHours();
					}
					if(ovs.getApplyType().equals("0")){
						naOTHours+=Double.valueOf(hours);
					}else{
						othours+=Double.valueOf(hours);
					}
				}
				//计算加班时长结束


				double gcurmonBalancehours=0;
				double settlehours=0;
				for (XdAttendanceSummary xdAttendanceSummary : xdAttendanceSummaries) {
					gcurmonBalancehours+=Double.valueOf(xdAttendanceSummary.getCurmonBalancehours());

					settlehours+=Double.valueOf(xdAttendanceSummary.getCurmonSettlehours().equals("")?"0":xdAttendanceSummary.getCurmonSettlehours());
				}
				summary.setCurmonAccbalancehours(String.valueOf(gcurmonBalancehours));

				summary.setCurmonSettlehours(othours-36 <0?"0":String.valueOf(othours-36));
				summary.setAccSettlehours(String.valueOf(settlehours));
				returnOtHour=naOTHours+","+othours;
				summary.setOthours(othours);
				//summary.setWorkHour(work_hour);
				summary.update();
				Db.update("update  xd_attendance_days" +
						" set ordinary_overtime='"+othours+"' ,national_overtime='"+naOTHours+"',duty_charge='"+dutyCharge+"',midshift_days='"+middleShiftDays+"'," +
						"nightshift_days='"+nightShiftDays+"',hightemp_allowance='"+highTempCharge+"',monshould_workdays='"+needWorkday+"',sickeleave_days='"+sickLeaveDays+"'," +
						"casualleave_days='"+personalLeaveDays+"',absenceduty_days='"+newLeaveDays+"',absentwork_days='"+absentworkDays+"',restanleave_days='"+alreayAnnualLeave+"'" +
						"where attendid_id='"+summary.getId()+"'");


				if(shift18A){
					String ym = summary.getScheduleYear() + summary.getScheduleMonth();
					Db.delete("delete from  xd_rcp_summary where summary_id='"+summary.getId()+"' and   rcp_month_year='"+ym+"'");

					XdRcpSummary rcp =new XdRcpSummary();
					rcp.setSummaryId(summary.getId());
					rcp.setDeptValue(summary.getDeptValue());
					rcp.setDeptName(summary.getDeptName());
					rcp.setUnitValue(summary.getUnitValue());
					rcp.setUnitName(summary.getUnitName());
					rcp.setProjectValue(summary.getProjectValue());
					rcp.setProjectName(summary.getProjectName());
					rcp.setEmpName(summary.getEmpName());
					rcp.setIdnum(emp==null?"":emp.getIdnum());
					rcp.setWorStation(summary.getWorkStation());
					rcp.setShfitName("18A");
					int mnShiftDays = middleShiftDays + nightShiftDays;
					rcp.setRcpDays(String.valueOf(mnShiftDays));
					rcp.setWorkDays(String.valueOf(monthWorkDays));
					rcp.setRcpStandard("100");
					if(mnShiftDays>=26){
						rcp.setRental("100");
					}else{
						String rcptenal = String.valueOf(mnShiftDays * 100 / (double) 26).substring(0,4);
						rcp.setRental(rcptenal);
					}
					rcp.setRcpYear(summary.getScheduleYear());
					rcp.setRcpMonthYear(summary.getScheduleMonth());
					rcp.setRcpMonthYear(ym);
					rcp.setStatus("0");
					rcp.setCreateUser("出勤生成");
					rcp.setCreateDate(DateUtil.getCurrentTime());
					rcp.save();

				}
//				select  * from  xd_anleave_summary where  emp_name='' and year=''
				if(modValue.contains("年")|| oldValue.contains("年")) {
					XdAnleaveSummary annLeave = XdAnleaveSummary.dao.findFirst("select  * from  xd_anleave_summary where  emp_name='' and year=''");
					if (annLeave != null) {
						annLeave.setSurplusDays(String.valueOf(Double.valueOf(annLeave.getSurplusDays()) - alreayAnnualLeave));
						annLeave.setSum(annLeave.getSum()+alreayAnnualLeave);
						annLeave.update();
					}

					String scheduleYear = summary.getScheduleYear();
					String scheduleMonth = summary.getScheduleMonth();
					XdAnleaveExecute	execute	 = XdAnleaveExecute.dao.findFirst("select * from  xd_anleave_execute where year='"+scheduleYear+"' and dept_id='"+summary.getDeptValue()+"'");
					Class  executeClazz= XdAnleaveExecute.class.getSuperclass();
					if(execute!=null){
						Method getMethod =executeClazz.getMethod("getMonth"+ Integer.parseInt(scheduleMonth));
						double leaveDays= (double) getMethod.invoke(execute);
						Method setMethod =executeClazz.getMethod("setMonth"+Integer.parseInt(scheduleMonth),Double.class);
						setMethod.invoke(execute,leaveDays+alreayAnnualLeave);

						execute.setSum(execute.getSum()+alreayAnnualLeave);
						execute.setSurplus(execute.getAnleaveDays()-execute.getSum());
						DecimalFormat decimalFormat=new DecimalFormat(".00");
						execute.setExecutionRate(Double.valueOf(decimalFormat.format((execute.getSum()+alreayAnnualLeave)*100/execute.getAnleaveDays())));
						execute.update();
					}

					List<XdAnleaveExecute> executesList = XdAnleaveExecute.dao.find("select * from  xd_anleave_execute where year='" + scheduleYear + "'");
					//Class exeClazz= XdAnleaveExecute.class.getSuperclass();
					double monthAnual=0;
					double sumExecute=0;
					double surplusExecute=0;
					Method getMethod=executeClazz.getMethod("getMonth"+ Integer.parseInt(scheduleMonth));
					Method setMethod =executeClazz.getMethod("setMonth"+Integer.parseInt(scheduleMonth),Double.class);


					XdAnleaveExecute totleExecute =null;

					for (XdAnleaveExecute exe : executesList) {
						if(exe.getDeptName().equals("合计")){
							totleExecute=exe;
						}else{
							monthAnual+= (double) getMethod.invoke(exe);
							sumExecute+=exe.getSum();
							surplusExecute+=exe.getSurplus();
						}

					}
					setMethod.invoke(totleExecute,monthAnual);
					totleExecute.setSum(sumExecute);
					totleExecute.setSurplus(surplusExecute);
					DecimalFormat decimalFormat=new DecimalFormat(".00");
					totleExecute.setExecutionRate(Double.valueOf(decimalFormat.format(sumExecute*100/totleExecute.getAnleaveDays())));
					totleExecute.update();

				}


			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}

		}


		renderSuccess(String.valueOf(returnOtHour));

	}


	public void updateCell20230228Bak(){

		String id = getPara("id");
		String oldValue = getPara("oldValue");
		String modValue = getPara("modValue");
		String overtime = getPara("overtime");
		String field = getPara("field");
		String type = getPara("type");

		String returnOtHour="";

		XdAttendanceSummary summary  = XdAttendanceSummary.dao.findById(id);

		if("2".equals(type)){
			summary.setRemarks(modValue);
			summary.update();
		}else{

//			处理年假时间
			XdAnleaveSummary leave =XdAnleaveSummary.
					dao.findFirst("select  * from  xd_anleave_summary where  emp_name='"+summary.getEmpName()+"' and year='"+summary.getScheduleYear()+"'");

			if(leave!=null){
				XdAttendanceDays attendanceDays= XdAttendanceDays.dao.findFirst("select  * from  xd_attendance_days where attendid_id='"+summary.getId()+"'");
				if(attendanceDays!=null){
					leave.setSurplusDays(String.valueOf(Double.valueOf(leave.getSurplusDays())+Double.valueOf(attendanceDays.getRestanleaveDays())));
				}
				leave.update();
			}
//			处理年假时间

			Class  superclass = XdAttendanceSummary.class.getSuperclass();

			try {
				Method method = superclass.getMethod("set" + field.substring(0, 1).toUpperCase() + field.substring(1),String.class);
				method.invoke(summary, modValue);

				int index = Integer.parseInt(field.replace("day", ""));
				Map<String, XdShift> nameShiftObjMap= CheckAttendanceUtil.shfitsMap();

				String[] modifyFlags = summary.getFlags().split(",");
				modifyFlags[index]="1";

				String modify="";
				for (String modifyFlag : modifyFlags) {
					modify=modify+","+modifyFlag;
				}
				summary.setFlags(modify.replaceAll("^,",""));

				String ot="";
				String[] otFlags = summary.getOtflags().split(",");

				String[] tipArr = summary.getTips().split(",");
				String tipIndex=tipArr[index];

				if("on".equals(overtime)){

					otFlags[index]="1";
					XdShift shift = nameShiftObjMap.get(modValue);

					if("0".equals(tipIndex)){
						tipIndex=shift.getBusitime()+"-"+shift.getUnbusitime();
					}else{
						XdShift oldShift = nameShiftObjMap.get(oldValue);
						if(oldShift!=null){
							tipIndex=tipIndex.replaceAll(oldShift.getBusitime()+"-"+oldShift.getUnbusitime(),"");
						}
						if(shift!=null){
							tipIndex=shift.getBusitime()+"-"+shift.getUnbusitime()+tipIndex;
						}
					}
					if("".equals(tipIndex)){
						tipArr[index]="0";
					}else{
						tipArr[index]=tipIndex;
					}
				}else{

					otFlags[index]="0";
//					XdShift shift = nameShiftObjMap.get(modValue);
					if(!"0".equals(tipIndex)){
						XdShift oldShift = nameShiftObjMap.get(oldValue);
						if(oldShift!=null){
							tipIndex=tipIndex.replaceAll(oldShift.getBusitime()+"-"+oldShift.getUnbusitime(),"");
						}
						if("".equals(tipIndex)){
							tipArr[index]="0";
						}else{
							tipArr[index]=tipIndex;
						}
					}

				}

				for (String otFlag : otFlags) {
					ot=ot+","+otFlag;
				}
				summary.setOtflags(ot.replaceAll("^,",""));

				String tipStr="";
				for (String tip : tipArr) {
					tipStr=tipStr+","+tip;
				}
				summary.setTips(tipStr.replaceAll("^,",""));



				String yearMonth=summary.getScheduleYear()+"-"+summary.getScheduleMonth();

				DateTimeFormatter dtf=DateTimeFormatter.ofPattern("yyyy-MM-dd");

				LocalDate lastMonLastDay = LocalDate.parse(yearMonth+"-01").minusDays(1);
				String lastMonthDay = dtf.format(lastMonLastDay);


				List<XdDayModel> dayModels =
						XdDayModel.dao.find("select * from  xd_day_model where days like '" + yearMonth + "%' or days='" + lastMonthDay + "'");



				String nextMonFirstDay = DateTimeFormatter.ofPattern("yyyyMMdd")
						.format(LocalDate.parse(dayModels.get(dayModels.size() - 1).getDays()).plusDays(1));

				XdDayModel nextMonFirstModel = XdDayModel.dao.findById(nextMonFirstDay);


				StringBuffer sb =new StringBuffer();
				dayModels.stream().forEach( xdDayModel-> {
					if("1".equals(xdDayModel.getIsnatHoliday())){
						sb.append(xdDayModel.getId()).append(",");
					}
				});
				if("1".equals(nextMonFirstModel.getIsnatHoliday())){
					sb.append(nextMonFirstModel.getId());
				}


				List<XdSettleOvertimeSummary> settleList = XdSettleOvertimeSummary.dao.find("select * from  xd_settle_overtime_summary " +
						"where emp_name='"+summary.getEmpName()+
						"' and super_days='"+dayModels.get(index).getDays()+"'");
				for (XdSettleOvertimeSummary settle : settleList) {
					settle.delete();
				}


				XdShift shift = nameShiftObjMap.get(modValue);
				XdEmployee emp = XdEmployee.dao.findFirst("select * from xd_employee where name ='" + summary.getEmpName() + "'");

				String indexDayId = dayModels.get(index).getId();
				String indexDays = dayModels.get(index).getDays();
				String startTime = (shift==null?"": shift.getBusitime());
				List<XdOvertimeSummary> otsList = XdOvertimeSummary.dao.find("select * from xd_overtime_summary" +
						" where super_days='" + indexDays
						+ "' and emp_name='" + summary.getEmpName()+"'");


				StringBuffer needSettle= new StringBuffer();

				Map<String,String >settleMap=new HashMap<>();

				if(shift!=null && startTime!=null && !"".equals(startTime)){
					boolean span = "1".equals(shift.getSpanDay());
					if(span){
						if(sb.indexOf(indexDayId)!=-1){
							settleMap.put(indexDays+","+shift.getBusitime()+",24:00",shift.getCurdayHours()+",0");
						}else{
							if("on".equals(overtime)){
								settleMap.put(indexDays+","+shift.getBusitime()+",24:00",shift.getCurdayHours()+",1");
							}
						}
						LocalDate nextDay = LocalDate.parse(indexDays).plusDays(1);
						String nextDayStr = dtf.format(nextDay);

						if(sb.indexOf(nextDayStr.replaceAll("-",""))!=-1){
							settleMap.put(nextDayStr+",0:00,"+shift.getUnbusitime(),shift.getSpanHours()+",0");
						}else{
							if("on".equals(overtime)){
								settleMap.put(indexDays+",0:00,"+shift.getUnbusitime(),shift.getSpanHours()+",1");
							}
						}


					}else{
						if(sb.indexOf(indexDayId)!=-1){
							settleMap.put(indexDays+","+shift.getBusitime()+","+shift.getBusitime(),shift.getHours()+",0");
						}else{
							if("on".equals(overtime)){
								settleMap.put(indexDays+","+shift.getBusitime()+","+shift.getBusitime(),shift.getHours()+",1");
							}
						}
					}
				/*	if(sb.indexOf(indexDayId)!=-1 ||"on".equals(overtime)){

						if(span){
							if(sb.indexOf(indexDayId)!=-1){
								settleMap.put(indexDays+","+shift.getBusitime()+",24:00",shift.getCurdayHours()+",0");
							}else{
								settleMap.put(indexDays+","+shift.getBusitime()+",24:00",shift.getCurdayHours()+",1");
							}

							LocalDate nextDay = LocalDate.parse(indexDays).plusDays(1);
							String nextDayStr = dtf.format(nextDay);

							if(sb.indexOf(nextDayStr.replaceAll("-",""))!=-1){
								settleMap.put(nextDayStr+",0:00,"+shift.getUnbusitime(),shift.getSpanHours()+",0");
							}else{
								if("on".equals(overtime)){
									settleMap.put(indexDays+",0:00,"+shift.getUnbusitime(),shift.getHours()+",1");
								}
							}
						}else{

						}
					}*/


				/*	if(sb.indexOf(indexDayId)!=-1 ||"on".equals(overtime)){

						sos.setId(null);
						sos.setApplyDate(dayModels.get(index).getDays());
						sos.setSuperDays(dayModels.get(index).getDays());

						if(sb.indexOf(indexDayId)!=-1){
							sos.setApplyType("0");
							sos.setSource("0");
						}else{
							sos.setSource("1");
							sos.setApplyType("1");
						}
						XdOvertimeSummary overTime =
								XdOvertimeSummary.dao.findFirst("select * from xd_overtime_summary" +
								" where apply_date='" + indexDays
								+ "' and emp_name='" + summary.getEmpName() + "' and apply_type='"+sos.getApplyType()+"'");

						sos.setApplyStart(overTime==null?"":overTime.getApplyStart());
						sos.setApplyEnd(overTime==null?"":overTime.getApplyEnd());
						sos.setApplyHours(overTime==null?0:Double.valueOf(overTime.getApplyHours()));

						if(span){


							sos.setActStart(startTime);
							sos.setActEnd("24:00");
							sos.setActHours(shift.getCurdayHours());

						}else{


							sos.setActStart(shift.getBusitime());
							sos.setActEnd(shift.getUnbusitime());
							sos.setActHours(Double.valueOf(shift.getHours()));
						}

						sos.save();
					}

					if(span){
						LocalDate nextDay = LocalDate.parse(dayModels.get(index).getDays()).plusDays(1);
						String nextDayStr = dtf.format(nextDay);
						if(sb.indexOf(nextDayStr.replaceAll("-",""))!=-1 ||"on".equals(overtime)){
							sos.setId(null);
							sos.setApplyDate(nextDayStr);
							sos.setSuperDays(dayModels.get(index).getDays());
							if(sb.indexOf(nextDayStr.replaceAll("-",""))!=-1){
								sos.setApplyType("0");
							}else{
								sos.setApplyType("1");
							}
							XdOvertimeSummary overTime =
									XdOvertimeSummary.dao.findFirst("select * from xd_overtime_summary where apply_date='" + nextDayStr
									+ "' and emp_name='" + summary.getEmpName() + "' and apply_type='"+sos.getApplyType()+"'");

							sos.setApplyStart(overTime==null?"":overTime.getApplyStart());
							sos.setApplyEnd(overTime==null?"":overTime.getApplyEnd());
							sos.setApplyHours(Double.valueOf(overTime.getApplyHours()));

							sos.setActStart("00:00");
							sos.setActEnd(shift.getUnbusitime());
							sos.setActHours(shift.getSpanHours());
							sos.save();
						}
					}*/
				}

				for (XdOvertimeSummary ots : otsList) {

					String ovStr = ots.getApplyDate() + "," + ots.getApplyStart() + "," + ots.getApplyEnd();

					XdSettleOvertimeSummary sos= new XdSettleOvertimeSummary();
					BeanUtils.copyProperties(sos,ots);
					if(settleMap.get(ovStr)!=null){
						sos.setId(null);
						sos.setActHours(Double.valueOf(ots.getApplyHours()));
						sos.setActStart(ots.getApplyStart());
						sos.setActEnd(ots.getApplyEnd());
						sos.setApplyType(settleMap.get(ovStr).split(",")[1]);
						sos.save();
						settleMap.remove(ovStr);
					}else{
						sos.setId(null);
						sos.save();
					}
				}
				Set<String> keySet = settleMap.keySet();
				for (String key : keySet) {
					String[] split = key.split(",");
					String s = settleMap.get(key);
					String[] split1 = s.split(",");

					XdSettleOvertimeSummary sots= new XdSettleOvertimeSummary();
					sots.setApplyDate(split[0]);
					sots.setActStart(split[1]);
					sots.setActEnd(split[2]);
					sots.setActHours(Double.valueOf(split1[0]));
					sots.setApplyType(split1[1]);
					sots.setSuperDays(indexDays);
					sots.setEmpName(summary.getEmpName());
					sots.setEmpNum(summary.getEmpNum());
					XdEmployee employee=XdEmployee.dao.findFirst("select * from  xd_employee where name='" + summary.getEmpName() + "'");
					sots.setEmpIdnum(employee==null?"":employee.getIdnum());
					sots.setCreateUser(ShiroKit.getUserId());
					sots.setCreateDate(DateUtil.getCurrentTime());
					sots.setProjectName(summary.getProjectName());
					sots.setProjectId(summary.getProjectValue());
					sots.setDeptName(summary.getDeptName());
					sots.setDeptId(summary.getDeptValue());
					sots.setSource("1");
					sots.save();
				}



				double othours=0;//平时加班时间
				double naOTHours=0;
				double work_hour=0;//出勤时间
//				double needWorkDay=0;//应工作天数
//				double shouldWorkDay=0;//希望工作天数
//				double actWorkDay=0;//实际工作天数
				int middleShiftDays=0;
				int nightShiftDays=0;
				double alreayAnnualLeave=0;
				double dutyCharge=0;
				int monthWorkDays=0;
				int commWorkdDay=0;
				double highTempCharge=0;
				int sickLeaveDays=0;
				int personalLeaveDays=0;
				int newLeaveDays=0;

				int absentworkDays=0;//旷工天数
				boolean shift18A=false;


				for (int i = 0; i < dayModels.size(); i++) {
					String suffix=(i<10?"0"+i:i+"");
					//	XdShift xdShift = nameShiftObjMap.get(cellValue);
					//	Method getMethod = superclass.getMethod("getDay" + suffix);
					String  shiftName = (String) superclass.getMethod("getDay" + suffix).invoke(summary);
					XdDayModel dayModel = dayModels.get(i);

					shift = nameShiftObjMap.get(shiftName);

					if(i==0){
//					上个月最后一天
						if(shift!=null){
							if("1".equals(shift.getSpanDay())){
								//是跨天
								XdDayModel firstDay = dayModels.get(1);
								if(sb.indexOf(firstDay.getId())==-1 && "0".equals(otFlags[i])){
									//naOTHours+=Double.valueOf(shift.getSpanHours());
									work_hour+=Double.valueOf(shift.getSpanHours());
								}
							}
						}
					}else{
						//本月

						//结算开始
						if(shiftName!=null&&!shiftName.equals("")){
							if("18A".equals(shiftName)){
								shift18A=true;
							}
							monthWorkDays++;

							XdShift xdShift = nameShiftObjMap.get(shiftName);
							if(xdShift!=null && xdShift.getBusitime()!=null&& !xdShift.getBusitime().equals("")){
								commWorkdDay++;
								if("1".equals(xdShift.getSpanDay())){

									if(sb.indexOf(dayModel.getId())==-1 && "0".equals(otFlags[i])){
										work_hour+=shift.getCurdayHours();
									}
									LocalDate localDate = LocalDate.parse(dayModel.getDays()).plusDays(1);
									String nextDay  = dtf.format(localDate);
									//是跨天且第二天是法定假日
									if(sb.indexOf(nextDay.replaceAll("-",""))==-1 && "0".equals(otFlags[i]) && i<dayModels.size()-1){

										work_hour+=shift.getSpanHours();
									}
								}else{
//									不是跨天
									if(sb.indexOf(dayModel.getId())==-1 &&  "0".equals(otFlags[i])){
										work_hour+=Double.valueOf(shift.getHours());
									}
								}
							}
							//计算开始
							if(shift.getMiddleshift()!=null&& !shift.getMiddleshift().equals("")){
								middleShiftDays++;//中班天数
							}
							if(shift.getNigthshift()!=null&& !shift.getNigthshift().equals("")){
								nightShiftDays++;//夜班天数
							}


							if(shiftName.contains("年")){
								String annual = shiftName.replace("年", "");
								if(annual.equals("")){
									alreayAnnualLeave+=1;
								}else{
									alreayAnnualLeave+=Double.valueOf(annual);
								}
							}
							if(shift.getShiftcost()!=null && !"".equals(shift.getShiftcost())){
								dutyCharge+=Double.valueOf(shift.getShiftcost());//值班费
							}
							/*attendancesummary表
							本月工时 ：人力资源部设置的
							本月工时结余=本月实际工时-本月工时
							上月累计工时=上个月的工时结余
							本月累计工时=所有的累积工时相加

							当月待结算工时=平时加班小数-36（上线36）
							累计待结算工时=所有月份相加*/
							if(shiftName.equals("病")){
								sickLeaveDays++;
							}
							if(shiftName.equals("事")){
								personalLeaveDays++;
							}
							if(shiftName.equals("新离")){
								newLeaveDays++;
							}
							if(shiftName.equals("旷")){
								newLeaveDays++;
							}
							//计算结束
						}
						//结算结束

					}

				}
				int needWorkday=0;

				if(summary.getScheduleMonth().endsWith("7")||summary.getScheduleMonth().endsWith("8")||summary.getScheduleMonth().endsWith("9")){
					int otNum=0;
					for (int i = 1; i <=otFlags.length-1; i++) {
						if(otFlags[i].equals("1")){
							otNum++;
						}
					}
					needWorkday = monthWorkDays - otNum;
					if(needWorkday<=commWorkdDay){
						highTempCharge=300;
					}else{
						highTempCharge=300*commWorkdDay/needWorkday;
					}
				}


				/*本月工时结余=本月实际工时-本月工时
				上月累计工时=上个月的工时结余
				本月累计工时=所有的累积工时相加

				当月待结算工时=平时加班小数-36（上线36）
				累计待结算工时=所有月份相加*/
				summary.setCurmonActworkhours(String.valueOf(work_hour));

				double curmonBalancehours = work_hour - Double.valueOf(summary.getCurmonWorkhours());//本月工时结余=本月实际工时-本月工时

				summary.setCurmonBalancehours(String.valueOf(curmonBalancehours));


				List<XdAttendanceSummary> xdAttendanceSummaries = XdAttendanceSummary.dao.find("select * from  xd_attendance_summary where schedule_year='" + summary.getScheduleYear()
						+ "' and emp_name='" + summary.getEmpName() + "' order by schedule_month desc");
				if(xdAttendanceSummaries.size()==1){
					summary.setPremonAccbalancehours("0");
				}else{
					summary.setPremonAccbalancehours(xdAttendanceSummaries.get(1).getPremonAccbalancehours());//上月累计工时=上个月的工时结余
				}
				//计算加班时长 开始
				List<XdSettleOvertimeSummary> ovList =	XdSettleOvertimeSummary.dao.find(
						"select * from  xd_settle_overtime_summary where emp_name='"+summary.getEmpName()+
								"' and apply_date like '"+yearMonth+"%'");
				for (XdSettleOvertimeSummary ovs : ovList) {
					if(ovs.getApplyType().equals("0")){
						naOTHours+=ovs.getActHours();
					}else{
						othours+=ovs.getActHours();
					}
				}
				//计算加班时长结束


				double gcurmonBalancehours=0;
				double settlehours=0;
				for (XdAttendanceSummary xdAttendanceSummary : xdAttendanceSummaries) {
					gcurmonBalancehours+=Double.valueOf(xdAttendanceSummary.getCurmonBalancehours());

					settlehours+=Double.valueOf(xdAttendanceSummary.getCurmonSettlehours().equals("")?"0":xdAttendanceSummary.getCurmonSettlehours());
				}
				summary.setCurmonAccbalancehours(String.valueOf(gcurmonBalancehours));

				summary.setCurmonSettlehours(othours-36 <0?"0":String.valueOf(othours-36));
				summary.setAccSettlehours(String.valueOf(settlehours));
				returnOtHour=naOTHours+","+othours;
				summary.setOthours(othours);
				//summary.setWorkHour(work_hour);
				summary.update();
				Db.update("update  xd_attendance_days" +
						" set ordinary_overtime='"+othours+"' ,national_overtime='"+naOTHours+"',duty_charge='"+dutyCharge+"',midshift_days='"+middleShiftDays+"'," +
						"nightshift_days='"+nightShiftDays+"',hightemp_allowance='"+highTempCharge+"',monshould_workdays='"+needWorkday+"',sickeleave_days='"+sickLeaveDays+"'," +
						"casualleave_days='"+personalLeaveDays+"',absenceduty_days='"+newLeaveDays+"',absentwork_days='"+absentworkDays+"',restanleave_days='"+alreayAnnualLeave+"'" +
						"where attendid_id='"+summary.getId()+"'");


				if(shift18A){
					String ym = summary.getScheduleYear() + summary.getScheduleMonth();
					Db.delete("delete from  xd_rcp_summary where summary_id='"+summary.getId()+"' and   rcp_month_year='"+ym+"'");

					XdRcpSummary rcp =new XdRcpSummary();
					rcp.setSummaryId(summary.getId());
					rcp.setDeptValue(summary.getDeptValue());
					rcp.setDeptName(summary.getDeptName());
					rcp.setUnitValue(summary.getUnitValue());
					rcp.setUnitName(summary.getUnitName());
					rcp.setProjectValue(summary.getProjectValue());
					rcp.setProjectName(summary.getProjectName());
					rcp.setEmpName(summary.getEmpName());
					rcp.setIdnum(emp==null?"":emp.getIdnum());
					rcp.setWorStation(summary.getWorkStation());
					rcp.setShfitName("18A");
					int mnShiftDays = middleShiftDays + nightShiftDays;
					rcp.setRcpDays(String.valueOf(mnShiftDays));
					rcp.setWorkDays(String.valueOf(monthWorkDays));
					rcp.setRcpStandard("100");
					if(mnShiftDays>=26){
						rcp.setRental("100");
					}else{
						String rcptenal = String.valueOf(mnShiftDays * 100 / (double) 26).substring(0,4);
						rcp.setRental(rcptenal);
					}
					rcp.setRcpYear(summary.getScheduleYear());
					rcp.setRcpMonthYear(summary.getScheduleMonth());
					rcp.setRcpMonthYear(ym);
					rcp.setStatus("0");
					rcp.setCreateUser("出勤生成");
					rcp.setCreateDate(DateUtil.getCurrentTime());
					rcp.save();

				}
//				select  * from  xd_anleave_summary where  emp_name='' and year=''

				XdAnleaveSummary annLeave = XdAnleaveSummary.dao.findFirst("select  * from  xd_anleave_summary where  emp_name='' and year=''");
				if(annLeave!=null){
					annLeave.setSurplusDays(String.valueOf(Double.valueOf(annLeave.getSurplusDays())-alreayAnnualLeave));
					annLeave.update();
				}


			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}

		}


		renderSuccess(String.valueOf(returnOtHour));

	}

	/*public void updateCellV1(){

		String id = getPara("id");
		String oldValue = getPara("oldValue");
		String modValue = getPara("modValue");
		String overtime = getPara("overtime");
		String field = getPara("field");
		String type = getPara("type");
		double returnOtHour=0;
		double returnNatOtHour=0;
		XdAttendanceSummary summary  = XdAttendanceSummary.dao.findById(id);
		if("2".equals(type)){
			summary.setRemarks(modValue);
			summary.update();
		}else{
			Class  superclass = XdAttendanceSummary.class.getSuperclass();
			try {
				Method method = superclass.getMethod("set" + field.substring(0, 1).toUpperCase() + field.substring(1),String.class);
				method.invoke(summary, modValue);
				int index = Integer.parseInt(field.replace("day", ""));

				Map<String, XdShift> nameShiftObjMap= CheckAttendanceUtil.shfitsMap();

				String[] modifyFlags = summary.getFlags().split(",");
				modifyFlags[index]="1";

				String modify="";
				for (String modifyFlag : modifyFlags) {
					modify=modify+","+modifyFlag;
				}
				summary.setFlags(modify.replaceAll("^,",""));

				String ot="";
				String[] otFlags = summary.getOtflags().split(",");

				String[] tipArr = summary.getTips().split(",");
				String tipIndex=tipArr[index];

				if("on".equals(overtime)){
					otFlags[index]="1";

					XdShift shift = nameShiftObjMap.get(modValue);

					if("0".equals(tipIndex)){
						tipIndex=shift.getBusitime()+"-"+shift.getUnbusitime();
					}else{
						XdShift oldShift = nameShiftObjMap.get(oldValue);
						if(oldShift!=null){
							tipIndex=tipIndex.replaceAll(oldShift.getBusitime()+"-"+oldShift.getUnbusitime(),"");
						}
						if(shift!=null){
							tipIndex=shift.getBusitime()+"-"+shift.getUnbusitime()+tipIndex;
						}
					}
					if("".equals(tipIndex)){
						tipArr[index]="0";
					}else{
						tipArr[index]=tipIndex;
					}
				}else{
					otFlags[index]="0";

					XdShift shift = nameShiftObjMap.get(modValue);
					if(!"0".equals(tipIndex)){
						XdShift oldShift = nameShiftObjMap.get(oldValue);
						if(oldShift!=null){
							tipIndex=tipIndex.replaceAll(oldShift.getBusitime()+"-"+oldShift.getUnbusitime(),"");
						}
						if("".equals(tipIndex)){
							tipArr[index]="0";
						}else{
							tipArr[index]=tipIndex;
						}
					}

				}
				for (String otFlag : otFlags) {
					ot=ot+","+otFlag;
				}

				summary.setOtflags(ot.replaceAll("^,",""));
				String tipStr="";
				for (String tip : tipArr) {
					tipStr=tipStr+","+tip;
				}
				summary.setTips(tipStr.replaceAll("^,",""));


				String[] otArr = ot.replaceAll("^,", "").split(",");
				System.out.println("otArr.length==================="+otArr.length);


				String yearMonth=summary.getScheduleYear()+"-"+summary.getScheduleMonth();

				LocalDate lastMonLastDay = LocalDate.parse(yearMonth+"-01").minusDays(1);
				DateTimeFormatter dtf=DateTimeFormatter.ofPattern("yyyy-MM-dd");
				String lastMonthDay = dtf.format(lastMonLastDay);

				//删除当前记录对应的加班核算记录
				List<XdSettleOvertimeSummary> settleList = XdSettleOvertimeSummary.dao.find("select * from  xd_settle_overtime_summary where emp_name='"+summary.getEmpName()+"' and apply_date like'" + yearMonth + "%' ");
				for (XdSettleOvertimeSummary settle : settleList) {
					settle.delete();
				}

				List<XdDayModel> dayModels =
						XdDayModel.dao.find("select * from  xd_day_model where days like '" + yearMonth + "%' or days='" + lastMonthDay + "'");


				Map<String, String> holidaysMap = dayModels.stream().collect(Collectors.toMap(XdDayModel::getId, XdDayModel::getHolidays));


				XdEmployee emp = XdEmployee.dao.findFirst("select * from xd_employee where name ='" + summary.getEmpName() + "'");

				XdSettleOvertimeSummary setOTSummary=new XdSettleOvertimeSummary();
				setOTSummary.setEmpNum(summary.getEmpNum());
				setOTSummary.setEmpName(summary.getEmpName());
				setOTSummary.setEmpIdnum(emp==null?"":emp.getIdnum());
				setOTSummary.setProjectName(summary.getProjectName());
				setOTSummary.setProjectId(summary.getProjectValue());
				setOTSummary.setDeptName(summary.getDeptName());
				setOTSummary.setDeptId(summary.getDeptValue());
				setOTSummary.setCreateUser(ShiroKit.getUserId());
				setOTSummary.setCreateDate(DateUtil.getCurrentTime());
				double othours=0;//平时加班时间
				double naOTHours=0;
				double work_hour=0;//出勤时间
				double needWorkDay=0;//应工作天数
				double shouldWorkDay=0;//希望工作天数
				double actWorkDay=0;//实际工作天数
				int middleShiftDays=0;
				int nightShiftDays=0;
				double alreayAnnualLeave=0;
				double dutyCharge=0;
				int monthWorkDays=0;
				int commWorkdDay=0;
				double highTempCharge=0;
				int sickLeaveDays=0;
				int personalLeaveDays=0;
				int newLeaveDays=0;

				int absentworkDays=0;//旷工天数



				for (int i = 0; i < dayModels.size(); i++) {
					String suffix="";
					if(i<10){
						suffix="0"+i;
					}else{
						suffix=i+"";
					}
					//	XdShift xdShift = nameShiftObjMap.get(cellValue);
					Method getMethod = superclass.getMethod("getDay" + suffix);
					String  shiftName = (String) getMethod.invoke(summary);
					XdDayModel dayModel = dayModels.get(i);
					XdShift shift = nameShiftObjMap.get(shiftName);
					if(i==0){//上个月最后一天
						if(shift!=null){
							if("1".equals(shift.getSpanDay())){//是跨天
								XdDayModel firstDay = dayModels.get(1);
								if(holidaysMap.get(firstDay)!=null&& !"".equals(holidaysMap.get(firstDay))){
									XdSettleOvertimeSummary otSummary=new XdSettleOvertimeSummary();
									XdOvertimeSummary overTime = XdOvertimeSummary.dao.findFirst("select * from xd_overtime_summary where apply_date='" + firstDay.getDays() + "' and emp_name='" + summary.getEmpName() + "' and apply_type='0'");
									otSummary.setEmpName(summary.getEmpName());
									otSummary.setEmpIdnum(emp==null?"":emp.getIdnum());
									otSummary.setEmpNum(summary.getEmpNum());
									otSummary.setDeptName(summary.getDeptName());
									otSummary.setDeptId(summary.getDeptValue());
									otSummary.setProjectName(summary.getProjectName());
									otSummary.setProjectId(summary.getProjectValue());
									otSummary.setApplyType("0");
									otSummary.setApplyDate(firstDay.getDays());
									otSummary.setApplyStart(overTime==null?"":overTime.getApplyStart());
									otSummary.setApplyEnd(overTime==null?"":overTime.getApplyEnd());
									otSummary.setApplyHours(overTime==null?0:Double.valueOf(overTime.getApplyHours()));
									otSummary.setActStart("00:00");
									otSummary.setActEnd(shift.getUnbusitime());
									otSummary.setActHours(Double.valueOf(shift.getSpanHours()));
									otSummary.setCreateUser(ShiroKit.getUserId());
									otSummary.setCreateDate(DateUtil.getCurrentTime());
									otSummary.save();
									naOTHours+=Double.valueOf(shift.getSpanHours());
								}else{

									if( otArr[i].equals("1")){
										othours+=Double.valueOf(shift.getSpanHours());
									}else{
										work_hour+=Double.valueOf(shift.getSpanHours());
									}

								}


							}
						}
					}else{//本月

						//结算开始
						if(shiftName!=null&&!shiftName.equals("")){
							monthWorkDays++;


							XdShift xdShift = nameShiftObjMap.get(shiftName);
							if(xdShift!=null && xdShift.getBusitime()!=null&& !xdShift.getBusitime().equals("")){
								commWorkdDay++;
								if(xdShift.getSpanDay().equals("1")){
									LocalDate localDate = LocalDate.parse(dayModel.getDays()).plusDays(1);
									String nextDay  = dtf.format(localDate);
									//是跨天且当天法定假日
									if(holidaysMap.get(dayModel.getId())!=null && !"".equals(holidaysMap.get(dayModel.getId()))){
										XdOvertimeSummary overTime = XdOvertimeSummary.dao.findFirst("select * from xd_overtime_summary where apply_date='" + dayModel.getDays() + "' and emp_name='" + summary.getEmpName() + "' and apply_type='0'");

										setOTSummary.setId(null);
										setOTSummary.setApplyDate(overTime.getApplyDate());
										setOTSummary.setApplyStart(overTime.getApplyStart());
										setOTSummary.setApplyEnd(overTime.getApplyEnd());
										setOTSummary.setApplyHours(Double.valueOf(overTime.getApplyHours()));
										setOTSummary.setApplyType("0");
										setOTSummary.setActHours(Double.valueOf(shift.getCurdayHours()));
										setOTSummary.setActStart(shift.getBusitime());
										setOTSummary.setActEnd("24:00");
										setOTSummary.save();
										naOTHours+=Double.valueOf(xdShift.getCurdayHours());
										//overTimeList.add(xdOvertimeSummary);
										//othours+=Double.valueOf(xdShift.getCurdayHours());
									}else{//跨天当天不是法定假日

										if(otArr[i].equals("1")){
											XdOvertimeSummary overTime = XdOvertimeSummary.dao.findFirst("select * from xd_overtime_summary where apply_date='" + dayModel.getDays() + "' and emp_name='" + summary.getEmpName() + "' and apply_type='1'");
											setOTSummary.setId(null);
											setOTSummary.setApplyDate(overTime.getApplyDate());
											setOTSummary.setApplyStart(overTime.getApplyStart());
											setOTSummary.setApplyEnd(overTime.getApplyEnd());
											setOTSummary.setApplyHours(Double.valueOf(overTime.getApplyHours()));
											setOTSummary.setApplyType("1");
											setOTSummary.setActStart(shift.getBusitime());
											setOTSummary.setActEnd("24:00");
											setOTSummary.setActHours(Double.valueOf(shift.getCurdayHours()));
											setOTSummary.save();
											othours+=Double.valueOf(shift.getSpanHours());
										}else{
											work_hour+=Double.valueOf(shift.getSpanHours());
										}


									}
									//是跨天且第二天是法定假日
									if(holidaysMap.get(nextDay.replaceAll("-",""))!=null && !"".equals(holidaysMap.get(nextDay.replaceAll("-","")))){

										XdOvertimeSummary overTime = XdOvertimeSummary.dao.findFirst("select * from xd_overtime_summary where apply_date='" + nextDay + "' and emp_name='" + summary.getEmpName() + "' and apply_type='0'");
										setOTSummary.setId(null);
										setOTSummary.setApplyDate(nextDay);
										setOTSummary.setApplyStart(overTime==null?"":overTime.getApplyStart());
										setOTSummary.setApplyEnd(overTime==null?"":overTime.getApplyEnd());
										setOTSummary.setApplyHours(overTime==null?0:Double.valueOf(overTime.getApplyHours()));
										setOTSummary.setActStart("00:00");
										setOTSummary.setActEnd(shift.getUnbusitime());
										setOTSummary.setActHours(Double.valueOf(shift.getSpanHours()));
										setOTSummary.setApplyType("0");
										//overTimeList.add(xdOvertimeSummary);
										setOTSummary.save();
										if(i<dayModels.size()-1){
											naOTHours+=Double.valueOf(xdShift.getSpanHours());
										}
									}else{//是跨天且第二天不是法定假日


										if(otArr[i].equals("1")){
											XdOvertimeSummary overTime = XdOvertimeSummary.dao.findFirst("select * from xd_overtime_summary where apply_date='" + nextDay + "' and emp_name='" + summary.getEmpName() + "' and apply_type='1'");
											setOTSummary.setId(null);
											setOTSummary.setApplyStart(overTime==null?"":overTime.getApplyStart());
											setOTSummary.setApplyEnd(overTime==null?"":overTime.getApplyEnd());
											setOTSummary.setApplyHours(overTime==null?0:Double.valueOf(overTime.getApplyHours()));
											setOTSummary.setApplyDate(nextDay);
											setOTSummary.setActStart("00:00");
											setOTSummary.setActEnd(shift.getUnbusitime());
											setOTSummary.setActHours(Double.valueOf(shift.getSpanHours()));
											setOTSummary.setApplyType("1");
											setOTSummary.save();
											if(i<dayModels.size()-1){
												othours+=Double.valueOf(xdShift.getSpanHours());
											}
										}else{
											if(i<dayModels.size()-1){
												work_hour+=Double.valueOf(xdShift.getSpanHours());
											}
										}
									}
								}else{

									if(holidaysMap.get(dayModel.getId())!=null && !"".equals(holidaysMap.get(dayModel.getId()))){

										XdOvertimeSummary overTime = XdOvertimeSummary.dao.findFirst("select * from xd_overtime_summary where apply_date='" + dayModel.getDays() + "' and emp_name='" + summary.getEmpName() + "' and apply_type='0'");

										setOTSummary.setId(null);
										setOTSummary.setApplyDate(dayModel.getDays());
										setOTSummary.setApplyStart(overTime==null?"":overTime.getApplyStart());
										setOTSummary.setApplyEnd(overTime==null?"":overTime.getApplyEnd());
										setOTSummary.setApplyHours(overTime==null?0:Double.valueOf(overTime.getApplyHours()));
										setOTSummary.setActStart(shift.getBusitime());
										setOTSummary.setActEnd(shift.getUnbusitime());
										setOTSummary.setActHours(Double.valueOf(shift.getHours()));
										setOTSummary.setApplyType("0");
										setOTSummary.save();
										naOTHours+=Double.valueOf(xdShift.getHours());

									}else{
										XdOvertimeSummary overTime = XdOvertimeSummary.dao.findFirst("select * from xd_overtime_summary where apply_date='" + dayModel.getDays() + "' and emp_name='" + summary.getEmpName() + "' and apply_type='1'");

										if(otArr[i].equals("1")){
											setOTSummary.setId(null);
											setOTSummary.setApplyDate(dayModel.getDays());
											setOTSummary.setApplyStart(overTime==null?"":overTime.getApplyStart());
											setOTSummary.setApplyEnd(overTime==null?"":overTime.getApplyEnd());
											setOTSummary.setApplyHours(overTime==null?0:Double.valueOf(overTime.getApplyHours()));
											setOTSummary.setActStart(shift.getBusitime());
											setOTSummary.setActEnd(shift.getUnbusitime());
											setOTSummary.setActHours(Double.valueOf(shift.getHours()));
											setOTSummary.setApplyType("1");
											setOTSummary.save();
											othours+=Double.valueOf(xdShift.getHours());

										}else {
											work_hour+=Double.valueOf(xdShift.getHours());
										}

									}

								}
							}
							//计算开始
							if(shift.getMiddleshift()!=null&& !shift.getMiddleshift().equals("")){
								middleShiftDays++;//中班天数
							}
							if(shift.getNigthshift()!=null&& !shift.getNigthshift().equals("")){
								nightShiftDays++;//夜班天数
							}


							if(shiftName.contains("年")){
								String annual = shiftName.replace("年", "");
								if(annual.equals("")){
									alreayAnnualLeave+=1;
								}else{
									alreayAnnualLeave+=Double.valueOf(annual);
								}
							}
							if(shift.getShiftcost()!=null && !"".equals(shift.getShiftcost())){
								dutyCharge+=Double.valueOf(shift.getShiftcost());//值班费
							}
							*//*attendancesummary表
							本月工时 ：人力资源部设置的
							本月工时结余=本月实际工时-本月工时
							上月累计工时=上个月的工时结余
							本月累计工时=所有的累积工时相加

							当月待结算工时=平时加班小数-36（上线36）
							累计待结算工时=所有月份相加*//*
							if(shiftName.equals("病")){
								sickLeaveDays++;
							}
							if(shiftName.equals("事")){
								personalLeaveDays++;
							}
							if(shiftName.equals("新离")){
								newLeaveDays++;
							}
							if(shiftName.equals("旷")){
								newLeaveDays++;
							}
							//计算结束
						}else{
							XdOvertimeSummary overTime = XdOvertimeSummary.dao.findFirst("select * from xd_overtime_summary where apply_date='" + dayModel.getDays() + "' and emp_name='" + summary.getEmpName() + "'");
							if(overTime!=null){

								setOTSummary.setApplyStart(overTime.getApplyStart());
								setOTSummary.setApplyEnd(overTime.getApplyEnd());
								setOTSummary.setApplyHours(Double.valueOf(overTime.getApplyHours()));
								setOTSummary.setApplyType(overTime.getApplyType());
								setOTSummary.save();
							}

						}
						//结算结束

					}

				}
				int needWorkday=0;

				if(summary.getScheduleMonth().endsWith("7")||summary.getScheduleMonth().endsWith("8")||summary.getScheduleMonth().endsWith("9")){
					int holidays=0;
					for (int i = 1; i <=otArr.length-1; i++) {
						if(otArr[i].equals("1")){
							holidays++;
						}
					}
					needWorkday = monthWorkDays - holidays;
					if(needWorkday<=commWorkdDay){
						highTempCharge=300;
					}else{
						highTempCharge=300*commWorkdDay/needWorkday;
					}
				}


				*//*本月工时结余=本月实际工时-本月工时
				上月累计工时=上个月的工时结余
				本月累计工时=所有的累积工时相加

				当月待结算工时=平时加班小数-36（上线36）
				累计待结算工时=所有月份相加*//*
				summary.setCurmonActworkhours(String.valueOf(work_hour));
				double curmonBalancehours = work_hour - Double.valueOf(summary.getCurmonWorkhours());//本月工时结余=本月实际工时-本月工时
				summary.setCurmonBalancehours(String.valueOf(curmonBalancehours));


				List<XdAttendanceSummary> xdAttendanceSummaries = XdAttendanceSummary.dao.find("select * from  xd_attendance_summary where schedule_year='" + summary.getScheduleYear()
						+ "' and emp_name='" + summary.getEmpName() + "' order by schedule_month desc");
				if(xdAttendanceSummaries.size()==1){
					summary.setPremonAccbalancehours("0");
				}else{
					summary.setPremonAccbalancehours(xdAttendanceSummaries.get(1).getPremonAccbalancehours());//上月累计工时=上个月的工时结余
				}
				double gcurmonBalancehours=0;
				double settlehours=0;
				for (XdAttendanceSummary xdAttendanceSummary : xdAttendanceSummaries) {
					gcurmonBalancehours+=Double.valueOf(xdAttendanceSummary.getCurmonBalancehours());
					settlehours+=Double.valueOf(xdAttendanceSummary.getCurmonSettlehours());
				}
				summary.setCurmonAccbalancehours(String.valueOf(gcurmonBalancehours));

				summary.setCurmonSettlehours(String.valueOf(othours-36));
				summary.setAccSettlehours(String.valueOf(settlehours));
				returnOtHour=othours;
				summary.setOthours(othours);
				//summary.setWorkHour(work_hour);
				summary.update();
				Db.update("update  xd_attendance_days" +
						" set ordinary_overtime='"+othours+"' ,national_overtime='"+naOTHours+"',duty_charge='"+dutyCharge+"',midshift_days='"+middleShiftDays+"'," +
						"nightshift_days='"+nightShiftDays+"',hightemp_allowance='"+highTempCharge+"',monshould_workdays='"+needWorkday+"',sickeleave_days='"+sickLeaveDays+"'," +
						"casualleave_days='"+personalLeaveDays+"',absenceduty_days='"+newLeaveDays+"',absentwork_days='"+absentworkDays+"',restanleave_days='"+alreayAnnualLeave+"'" +
						"where attendid_id='"+summary.getId()+"'");

			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}

		}


		renderSuccess(String.valueOf(returnOtHour));

	}*/
	
}