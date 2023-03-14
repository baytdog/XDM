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
import com.pointlion.util.CheckAttendanceUtil;

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
		setAttr("shiftList",xdShifts);
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
		String unitName = getPara("unitname","");
		String year = getPara("year","");
		String month = getPara("month","");
    	Page<Record> page = service.getPage(Integer.valueOf(curr),Integer.valueOf(pageSize),dept,unitName,emp_name,year,month);

    	renderPage(page.getList(),"",page.getTotalRow());
    }

    public void getTableHeader(){

		String year = getPara("year","");
		String month = getPara("month","");
		String yearMonth=year+month;
		LocalDate lastMontLastDay = LocalDate.parse(year+"-"+month+"-01").minusMonths(1);
		List<XdDayModel> xdDayModels =
				XdDayModel.dao.find("select * from  xd_day_model " +
										"where id like '"+yearMonth+"%'" +
										" or id='"+DateTimeFormatter.ofPattern("yyyyMMdd").format(lastMontLastDay)+"'"+
										" order by id");
		//String lastMonth = dtf.format(localDate);
		//XdDayModel lastMonLastDay = XdDayModel.dao.findFirst("select * from  xd_day_model where id like '" + lastMonth + "%' order by id desc");
		List<String> weekLists=new ArrayList<>();
		List<String> holidayLists=new ArrayList<>();
		int daySize=xdDayModels.size()-1;;

		for (int i = 0; i < xdDayModels.size(); i++) {
			if (i==0 && xdDayModels.get(0).getId().startsWith(yearMonth)) {
				//无当月前一天
				daySize=xdDayModels.size();
				weekLists.add("");
				holidayLists.add("-");
			}
			XdDayModel dayModel = xdDayModels.get(i);
			weekLists.add(dayModel.getWeeks());

			if(dayModel.getHolidays()==null||dayModel.getHolidays().equals("")){
				holidayLists.add("-");
			}else{
				holidayLists.add(dayModel.getHolidays());
			}
		}

		/*if(lastMonLastDay!=null){
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
*/
		/*xdDayModels.stream().forEach(
				xdDayModel-> {
					weekLists.add(xdDayModel.getWeeks());
					if(xdDayModel.getHolidays()==null||xdDayModel.getHolidays().equals("")){
						holidayLists.add("-");
					}else{

						holidayLists.add(xdDayModel.getHolidays());
					}
				});*/
		/*setAttr("daysNum",xdDayModels.size());
		setAttr("weekLists",weekLists);
		setAttr("holidayLists",holidayLists);*/
		JSONObject jsonObject=new JSONObject();
		jsonObject.put("daysNum",daySize);
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



	public void getOvTime(){
		String id = getPara("id");
		String field = getPara("field");
		XdScheduleSummary scheduleSummary  = XdScheduleSummary.dao.findById(id);
		String yearMonth=scheduleSummary.getScheduleYear()+"-"+scheduleSummary.getScheduleMonth();
		LocalDate lastMonLastDay = LocalDate.parse(yearMonth+"-01").minusDays(1);
		DateTimeFormatter dtf=DateTimeFormatter.ofPattern("yyyy-MM-dd");


		List<XdDayModel> xdDayModels = XdDayModel.dao.find("select * from  xd_day_model where days like '" + yearMonth + "%' or days='" + lastMonLastDay + "'");
		String day = field.replace("day", "");
		int index = Integer.parseInt(day);


		XdOvertimeSummary overtimeSummary = XdOvertimeSummary.dao.findFirst("select *from  xd_overtime_summary " +
				" where     apply_date='"+xdDayModels.get(index).getDays() + "' and source='2'");

		String returnOtHour="";
		if(overtimeSummary!=null && overtimeSummary.getApplyHours()!=null&& overtimeSummary.getApplyStart()!=null){
			String[] splitStart = overtimeSummary.getApplyStart().split(":");
			returnOtHour=returnOtHour+splitStart[0]+","+splitStart[1]+",";

			String[] splitEnd = overtimeSummary.getApplyEnd().split(":");
			returnOtHour=returnOtHour+splitEnd[0]+","+splitEnd[1]+",";
			returnOtHour=returnOtHour+overtimeSummary.getApplyHours();

		}


		renderSuccess(returnOtHour);

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
		String start = getPara("start");
		String end = getPara("end");
		String otHour = getPara("otHour");
 		String returnOtHour="";
		XdScheduleSummary scheduleSummary  = XdScheduleSummary.dao.findById(id);
		if("2".equals(type)){
			scheduleSummary.setRemarks(modValue);
			scheduleSummary.update();
		}else{
			Class  superclass = scheduleSummary.getClass().getSuperclass();
			try {
				Method method = superclass.getMethod("set" + field.substring(0, 1).toUpperCase() + field.substring(1),String.class);
				method.invoke(scheduleSummary, modValue);
				String day = field.replace("day", "");
				int index = Integer.parseInt(day);

				Map<String, XdShift> nameShiftObjMap= CheckAttendanceUtil.shfitsMap();

				String[] modifyFlags = scheduleSummary.getFlags().split(",");
				modifyFlags[index]="1";
				String modify="";
				for (String modifyFlag : modifyFlags) {
					modify=modify+","+modifyFlag;
				}
				scheduleSummary.setFlags(modify.replaceAll("^,",""));

				String ot="";
				String[] otFlags = scheduleSummary.getOtflags().split(",");
				String[] tipArr = scheduleSummary.getTips().split(",");
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
				scheduleSummary.setOtflags(ot.replaceAll("^,",""));

				String tipStr="";
				for (String tip : tipArr) {
					tipStr=tipStr+","+tip;
				}
				scheduleSummary.setTips(tipStr.replaceAll("^,",""));


				String yearMonth=scheduleSummary.getScheduleYear()+"-"+scheduleSummary.getScheduleMonth();
				LocalDate lastMonLastDay = LocalDate.parse(yearMonth+"-01").minusDays(1);
				DateTimeFormatter dtf=DateTimeFormatter.ofPattern("yyyy-MM-dd");


				List<XdDayModel> xdDayModels = XdDayModel.dao.find("select * from  xd_day_model where days like '" + yearMonth + "%' or days='" + lastMonLastDay + "'");

				String nextMonFirstDay = DateTimeFormatter.ofPattern("yyyyMMdd").format(
						LocalDate.parse(xdDayModels.get(xdDayModels.size() - 1).getDays()).plusDays(1));

				XdDayModel nextMonFirstModel = XdDayModel.dao.findById(nextMonFirstDay);

				StringBuffer sb =new StringBuffer();
				xdDayModels.stream().forEach( xdDayModel-> {
					if("1".equals(xdDayModel.getIsnatHoliday())){
						sb.append(xdDayModel.getId()).append(",");
					}
				});
				if("1".equals(nextMonFirstModel.getIsnatHoliday())){
					sb.append(nextMonFirstModel.getId());
				}

				List<XdOvertimeSummary> applyList =	XdOvertimeSummary.dao.find(
						"select * from  xd_overtime_summary where emp_name='"+scheduleSummary.getEmpName()+
								"' and super_days='"+xdDayModels.get(index).getDays()+"'");

				for (XdOvertimeSummary overtimeSummary : applyList) {
					if(overtimeSummary.getActStart()==null || "".equals(overtimeSummary.getActStart())){
						overtimeSummary.delete();
					}else{
						overtimeSummary.setApplyStart("");
						overtimeSummary.setApplyEnd("");
						overtimeSummary.setApplyHours("");
						overtimeSummary.update();
					}

				}

				XdShift shift = nameShiftObjMap.get(modValue);

				if(shift!=null && shift.getBusitime()!=null && !"".equals(shift.getBusitime())){
					boolean span = "1".equals(shift.getSpanDay());

					if(sb.indexOf(xdDayModels.get(index).getId())!=-1 ||"on".equals(overtime)){
						XdEmployee emp = XdEmployee.dao.findFirst("select * from xd_employee where name ='" + scheduleSummary.getEmpName() + "'");

						XdOvertimeSummary ots=new XdOvertimeSummary();
						ots.setEmpNum(scheduleSummary.getEmpNum());
						ots.setEmpName(scheduleSummary.getEmpName());
						ots.setEmpIdnum(emp==null?"":emp.getIdnum());
						ots.setProjectName(scheduleSummary.getProjectName());
						ots.setProjectId(scheduleSummary.getProjectValue());
						ots.setDeptName(scheduleSummary.getDeptName());
						ots.setDeptId(scheduleSummary.getDeptValue());
						ots.setCreateUser(ShiroKit.getUserId());
						ots.setCreateDate(DateUtil.getCurrentTime());
						ots.setApplyDate(xdDayModels.get(index).getDays());
						if(span){
							ots.setApplyStart(shift.getBusitime());
							ots.setApplyEnd("24:00");
							ots.setApplyHours(shift.getCurdayHours()+"");
						}else{
							ots.setApplyStart(shift.getBusitime());
							ots.setApplyEnd(shift.getUnbusitime());
							ots.setApplyHours(shift.getHours()+"");
						}
						ots.setSuperDays(xdDayModels.get(index).getDays());
						if(sb.indexOf(xdDayModels.get(index).getId())!=-1){
							ots.setApplyType("0");
						}else{
							ots.setApplyType("1");
						}

						//ots.setSource("1");


						XdOvertimeSummary overtimeSummary = XdOvertimeSummary.dao.findFirst("select *from  xd_overtime_summary " +
								" where   act_start='" + ots.getApplyStart()+"' and apply_date='"+ots.getApplyDate()
								+ "' and  act_end='" + ots.getApplyEnd() + "' and super_days='" + ots.getSuperDays() + "'");
						if(overtimeSummary==null){
							ots.save();
						}else{
							overtimeSummary.setApplyStart(ots.getApplyStart());
							overtimeSummary.setApplyEnd(ots.getApplyEnd());
							overtimeSummary.setApplyHours(ots.getApplyHours());
							overtimeSummary.update();
						}


					}

					if(span){
						LocalDate nextDay = LocalDate.parse(xdDayModels.get(index).getDays()).plusDays(1);
						String nextDayStr = dtf.format(nextDay);
						if(sb.indexOf(nextDayStr.replaceAll("-",""))!=-1 ||"on".equals(overtime)){
							XdEmployee emp = XdEmployee.dao.findFirst("select * from xd_employee where name ='" + scheduleSummary.getEmpName() + "'");
							XdOvertimeSummary ots=new XdOvertimeSummary();
							ots.setEmpNum(scheduleSummary.getEmpNum());
							ots.setEmpName(scheduleSummary.getEmpName());
							ots.setEmpIdnum(emp==null?"":emp.getIdnum());
							ots.setProjectName(scheduleSummary.getProjectName());
							ots.setProjectId(scheduleSummary.getProjectValue());
							ots.setDeptName(scheduleSummary.getDeptName());
							ots.setDeptId(scheduleSummary.getDeptValue());
							ots.setCreateUser(ShiroKit.getUserId());
							ots.setCreateDate(DateUtil.getCurrentTime());
							ots.setApplyDate(nextDayStr);
							ots.setApplyStart("0:00");
							ots.setApplyEnd(shift.getUnbusitime());
							ots.setApplyHours(shift.getSpanHours()+"");
							ots.setSuperDays(xdDayModels.get(index).getDays());
							if(sb.indexOf(nextDayStr.replaceAll("-",""))!=-1){
								ots.setApplyType("0");
							}else{
								ots.setApplyType("1");
							}
							ots.setSource("1");

							XdOvertimeSummary overtimeSummary = XdOvertimeSummary.dao.findFirst("select *from  xd_overtime_summary " +
									" where   act_start='" + ots.getApplyStart()+"' and apply_date='"+ots.getApplyDate()
									+ "' and  act_end='" + ots.getApplyEnd() + "' and super_days='" + ots.getSuperDays() + "'");
							if(overtimeSummary==null){
								ots.save();
							}else{
								overtimeSummary.setApplyStart(ots.getApplyStart());
								overtimeSummary.setApplyEnd(ots.getApplyEnd());
								overtimeSummary.setApplyHours(ots.getApplyHours());
								overtimeSummary.update();
							}

							//ots.save();
						}
					}
				}

				if(!"".equals(start)&&!"".equals(end)&&!"".equals(otHour)){
					XdOvertimeSummary overtimeSummary = XdOvertimeSummary.dao.findFirst("select *from  xd_overtime_summary " +
							" where   act_start='" + start+"' and apply_date='"+xdDayModels.get(index).getDays()
							+ "' and  act_end='" + end + "' and source='2'");
					if(overtimeSummary==null){

						XdEmployee emp = XdEmployee.dao.findFirst("select * from xd_employee where name ='" + scheduleSummary.getEmpName() + "'");
						XdOvertimeSummary ots=new XdOvertimeSummary();
						ots.setEmpNum(scheduleSummary.getEmpNum());
						ots.setEmpName(scheduleSummary.getEmpName());
						ots.setEmpIdnum(emp==null?"":emp.getIdnum());
						ots.setProjectName(scheduleSummary.getProjectName());
						ots.setProjectId(scheduleSummary.getProjectValue());
						ots.setDeptName(scheduleSummary.getDeptName());
						ots.setDeptId(scheduleSummary.getDeptValue());
						ots.setCreateUser(ShiroKit.getUserId());
						ots.setCreateDate(DateUtil.getCurrentTime());
						ots.setApplyDate(xdDayModels.get(index).getDays());
						ots.setApplyStart(start);
						ots.setApplyEnd(end);
						ots.setApplyHours(otHour+"");
						ots.setSuperDays(xdDayModels.get(index).getDays());
						if(sb.indexOf(xdDayModels.get(index).getId())!=-1){
							ots.setApplyType("0");
						}else{
							ots.setApplyType("1");
						}
						ots.setSource("2");
						ots.save();
					}else{
						overtimeSummary.setApplyStart(start);
						overtimeSummary.setApplyEnd(end);
						overtimeSummary.setApplyHours(otHour);
						overtimeSummary.update();
					}
				}


				//Map<String, String> holidaysMap = xdDayModels.stream().collect(Collectors.toMap(XdDayModel::getId, XdDayModel::getHolidays));
				double othours=0;//加班时间
				double natOTHours=0;//国定加班时间
				double work_hour=0;//出勤时间
				for (int i = 0; i < xdDayModels.size(); i++) {
					String suffix="";
					suffix=(i<10?"0"+i:i+"");
					Method getMethod = superclass.getMethod("getDay" + suffix);
					String  shiftName = (String) getMethod.invoke(scheduleSummary);
					XdDayModel dayModel = xdDayModels.get(i);

					if(shiftName!=null&&!shiftName.equals("")){
						XdShift xdShift = nameShiftObjMap.get(shiftName);
						if(xdShift!=null && xdShift.getBusitime()!=null&& !xdShift.getBusitime().equals("")){
							if(xdShift.getSpanDay().equals("1")){
								//跨天且   当天不是法定假日
								if(sb.indexOf(dayModel.getId())==-1 && i>0  && otFlags[i].equals("0") ){
									work_hour+=Double.valueOf(xdShift.getCurdayHours());
								}
								//跨天且   第二天不是法定假日
//								LocalDate localDate = ;
								String nextDate  = dtf.format(LocalDate.parse(dayModel.getDays()).plusDays(1));
								if(sb.indexOf(nextDate.replaceAll("-",""))==-1
								&& i<xdDayModels.size()-1 && otFlags[i].equals("0")){
										work_hour+=Double.valueOf(xdShift.getSpanHours());
								}
							}else{
								if(sb.indexOf(dayModel.getId())==-1 && i>0 && otFlags[i].equals("0")){
										work_hour+=Double.valueOf(xdShift.getHours());
								}
							}
						}
					}
				}
				List<XdOvertimeSummary> ovList =	XdOvertimeSummary.dao.find(
						"select * from  xd_overtime_summary where emp_name='"+scheduleSummary.getEmpName()+
								"' and apply_date like '"+yearMonth+"%'");
				for (XdOvertimeSummary overtimeSummary : ovList) {
					if(overtimeSummary.getApplyHours()!=null && !"".equals(overtimeSummary.getApplyHours())){
						if(overtimeSummary.getApplyType().equals("0")){
							natOTHours+=Double.valueOf(overtimeSummary.getApplyHours().equals("")?"0":overtimeSummary.getApplyHours());
						}else{
							othours+=Double.valueOf(overtimeSummary.getApplyHours().equals("")?"0":overtimeSummary.getApplyHours());
						}
					}

				}
				returnOtHour=natOTHours+","+othours;
				scheduleSummary.setOthours(othours);
				scheduleSummary.setNatOthours(natOTHours);
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


		renderSuccess(returnOtHour);
	}




	public void updateCell20230314(){
		String id = getPara("id");
		String oldValue = getPara("oldValue");
		String modValue = getPara("modValue");
		String overtime = getPara("overtime");
		String field = getPara("field");
		String type = getPara("type");
		String returnOtHour="";
//		double returnNatOTHour=0;
		XdScheduleSummary scheduleSummary  = XdScheduleSummary.dao.findById(id);
		if("2".equals(type)){
			scheduleSummary.setRemarks(modValue);
			scheduleSummary.update();
		}else{
			Class  superclass = scheduleSummary.getClass().getSuperclass();
			try {
				Method method = superclass.getMethod("set" + field.substring(0, 1).toUpperCase() + field.substring(1),String.class);
				method.invoke(scheduleSummary, modValue);
				String day = field.replace("day", "");
				int index = Integer.parseInt(day);

				Map<String, XdShift> nameShiftObjMap= CheckAttendanceUtil.shfitsMap();

				String[] modifyFlags = scheduleSummary.getFlags().split(",");
				modifyFlags[index]="1";
				String modify="";
				for (String modifyFlag : modifyFlags) {
					modify=modify+","+modifyFlag;
				}
				scheduleSummary.setFlags(modify.replaceAll("^,",""));

				String ot="";
				String[] otFlags = scheduleSummary.getOtflags().split(",");
				String[] tipArr = scheduleSummary.getTips().split(",");
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
					//tipArr[index]=tipArr[index].replaceAll(shift.getBusitime()+"-"+shift.getUnbusitime(),"0");
					//String tipArrIndex = tipArr[index].replaceAll(shift.getBusitime() + "-" + shift.getUnbusitime(), "");

					if(!"0".equals(tipIndex)){
						XdShift oldShift = nameShiftObjMap.get(oldValue);
						if(oldShift!=null){
							tipIndex=tipIndex.replaceAll(oldShift.getBusitime()+"-"+oldShift.getUnbusitime(),"");
						}
                       /* if(shift!=null){
                            tipIndex=shift.getBusitime()+"-"+shift.getUnbusitime()+tipIndex;
                        }*/
						if("".equals(tipIndex)){
							tipArr[index]="0";
						}else{
							tipArr[index]=tipIndex;
						}
					}

					/*if("".equals(tipArrIndex)){
						tipArr[index]="0";
					}else{
						tipArr[index]=tipArrIndex;
					}*/
				}
				for (String otFlag : otFlags) {
					ot=ot+","+otFlag;
				}
				scheduleSummary.setOtflags(ot.replaceAll("^,",""));

				String tipStr="";
				for (String tip : tipArr) {
					tipStr=tipStr+","+tip;
				}
				scheduleSummary.setTips(tipStr.replaceAll("^,",""));


				String yearMonth=scheduleSummary.getScheduleYear()+"-"+scheduleSummary.getScheduleMonth();
				LocalDate lastMonLastDay = LocalDate.parse(yearMonth+"-01").minusDays(1);
				DateTimeFormatter dtf=DateTimeFormatter.ofPattern("yyyy-MM-dd");
//				String lastMonthDay = dtf.format(lastMonLastDay);
				/*String modDay="";
				if("00".equals(day)){
					modDay= dtf.format(LocalDate.parse(yearMonth + "-" + "01").minusDays(1));
				}else{
					modDay=yearMonth+"-"+day;
				}*/


				List<XdDayModel> xdDayModels = XdDayModel.dao.find("select * from  xd_day_model where days like '" + yearMonth + "%' or days='" + lastMonLastDay + "'");

				String nextMonFirstDay = DateTimeFormatter.ofPattern("yyyyMMdd").format(
						LocalDate.parse(xdDayModels.get(xdDayModels.size() - 1).getDays()).plusDays(1));

				XdDayModel nextMonFirstModel = XdDayModel.dao.findById(nextMonFirstDay);

				StringBuffer sb =new StringBuffer();
				xdDayModels.stream().forEach( xdDayModel-> {
					if("1".equals(xdDayModel.getIsnatHoliday())){
						sb.append(xdDayModel.getId()).append(",");
					}
				});
				if("1".equals(nextMonFirstModel.getIsnatHoliday())){
					sb.append(nextMonFirstModel.getId());
				}

				List<XdOvertimeSummary> applyList =	XdOvertimeSummary.dao.find(
						"select * from  xd_overtime_summary where emp_name='"+scheduleSummary.getEmpName()+
								"' and source='1' and super_days='"+xdDayModels.get(index).getDays()+"'");

				for (XdOvertimeSummary overtimeSummary : applyList) {
					if(overtimeSummary.getActStart()==null || "".equals(overtimeSummary.getActStart())){
						overtimeSummary.delete();
					}else{
						overtimeSummary.setApplyStart("");
						overtimeSummary.setApplyEnd("");
						overtimeSummary.setApplyHours("");
						overtimeSummary.update();
					}

				}

				XdShift shift = nameShiftObjMap.get(modValue);

				if(shift!=null && shift.getBusitime()!=null && !"".equals(shift.getBusitime())){
					boolean span = "1".equals(shift.getSpanDay());

					if(sb.indexOf(xdDayModels.get(index).getId())!=-1 ||"on".equals(overtime)){
						XdEmployee emp = XdEmployee.dao.findFirst("select * from xd_employee where name ='" + scheduleSummary.getEmpName() + "'");

						XdOvertimeSummary ots=new XdOvertimeSummary();
						ots.setEmpNum(scheduleSummary.getEmpNum());
						ots.setEmpName(scheduleSummary.getEmpName());
						ots.setEmpIdnum(emp==null?"":emp.getIdnum());
						ots.setProjectName(scheduleSummary.getProjectName());
						ots.setProjectId(scheduleSummary.getProjectValue());
						ots.setDeptName(scheduleSummary.getDeptName());
						ots.setDeptId(scheduleSummary.getDeptValue());
						ots.setCreateUser(ShiroKit.getUserId());
						ots.setCreateDate(DateUtil.getCurrentTime());
						ots.setApplyDate(xdDayModels.get(index).getDays());
						if(span){
							ots.setApplyStart(shift.getBusitime());
							ots.setApplyEnd("24:00");
							ots.setApplyHours(shift.getCurdayHours()+"");
						}else{
							ots.setApplyStart(shift.getBusitime());
							ots.setApplyEnd(shift.getUnbusitime());
							ots.setApplyHours(shift.getHours()+"");
						}
						ots.setSuperDays(xdDayModels.get(index).getDays());
						if(sb.indexOf(xdDayModels.get(index).getId())!=-1){
							ots.setApplyType("0");
						}else{
							ots.setApplyType("1");
						}

						ots.setSource("1");


						XdOvertimeSummary overtimeSummary = XdOvertimeSummary.dao.findFirst("select *from  xd_overtime_summary " +
								" where source='1' and act_start='" + ots.getApplyStart()+"' and apply_date='"+ots.getApplyDate()
								+ "' and  act_end='" + ots.getApplyEnd() + "' and super_days='" + ots.getSuperDays() + "'");
						if(overtimeSummary==null){
							ots.save();
						}else{
							overtimeSummary.setApplyStart(ots.getApplyStart());
							overtimeSummary.setApplyEnd(ots.getApplyEnd());
							overtimeSummary.setApplyHours(ots.getApplyHours());
							overtimeSummary.update();
						}


					}

					if(span){
						LocalDate nextDay = LocalDate.parse(xdDayModels.get(index).getDays()).plusDays(1);
						String nextDayStr = dtf.format(nextDay);
						if(sb.indexOf(nextDayStr.replaceAll("-",""))!=-1 ||"on".equals(overtime)){
							XdEmployee emp = XdEmployee.dao.findFirst("select * from xd_employee where name ='" + scheduleSummary.getEmpName() + "'");
							XdOvertimeSummary ots=new XdOvertimeSummary();
							ots.setEmpNum(scheduleSummary.getEmpNum());
							ots.setEmpName(scheduleSummary.getEmpName());
							ots.setEmpIdnum(emp==null?"":emp.getIdnum());
							ots.setProjectName(scheduleSummary.getProjectName());
							ots.setProjectId(scheduleSummary.getProjectValue());
							ots.setDeptName(scheduleSummary.getDeptName());
							ots.setDeptId(scheduleSummary.getDeptValue());
							ots.setCreateUser(ShiroKit.getUserId());
							ots.setCreateDate(DateUtil.getCurrentTime());
							ots.setApplyDate(nextDayStr);
							ots.setApplyStart("0:00");
							ots.setApplyEnd(shift.getUnbusitime());
							ots.setApplyHours(shift.getSpanHours()+"");
							ots.setSuperDays(xdDayModels.get(index).getDays());
							if(sb.indexOf(nextDayStr.replaceAll("-",""))!=-1){
								ots.setApplyType("0");
							}else{
								ots.setApplyType("1");
							}
							ots.setSource("1");

							XdOvertimeSummary overtimeSummary = XdOvertimeSummary.dao.findFirst("select *from  xd_overtime_summary " +
									" where source='1' and act_start='" + ots.getApplyStart()+"' and apply_date='"+ots.getApplyDate()
									+ "' and  act_end='" + ots.getApplyEnd() + "' and super_days='" + ots.getSuperDays() + "'");
							if(overtimeSummary==null){
								ots.save();
							}else{
								overtimeSummary.setApplyStart(ots.getApplyStart());
								overtimeSummary.setApplyEnd(ots.getApplyEnd());
								overtimeSummary.setApplyHours(ots.getApplyHours());
								overtimeSummary.update();
							}

							//ots.save();
						}
					}
				}




				//Map<String, String> holidaysMap = xdDayModels.stream().collect(Collectors.toMap(XdDayModel::getId, XdDayModel::getHolidays));
				double othours=0;//加班时间
				double natOTHours=0;//国定加班时间
				double work_hour=0;//出勤时间
				for (int i = 0; i < xdDayModels.size(); i++) {
					String suffix="";
					suffix=(i<10?"0"+i:i+"");
					Method getMethod = superclass.getMethod("getDay" + suffix);
					String  shiftName = (String) getMethod.invoke(scheduleSummary);
					XdDayModel dayModel = xdDayModels.get(i);

					if(shiftName!=null&&!shiftName.equals("")){
						XdShift xdShift = nameShiftObjMap.get(shiftName);
						if(xdShift!=null && xdShift.getBusitime()!=null&& !xdShift.getBusitime().equals("")){
							if(xdShift.getSpanDay().equals("1")){
								//跨天且   当天不是法定假日
								if(sb.indexOf(dayModel.getId())==-1 && i>0  && otFlags[i].equals("0") ){
									work_hour+=Double.valueOf(xdShift.getCurdayHours());
								}
								//跨天且   第二天不是法定假日
//								LocalDate localDate = ;
								String nextDate  = dtf.format(LocalDate.parse(dayModel.getDays()).plusDays(1));
								if(sb.indexOf(nextDate.replaceAll("-",""))==-1
										&& i<xdDayModels.size()-1 && otFlags[i].equals("0")){
									work_hour+=Double.valueOf(xdShift.getSpanHours());
								}
							}else{
								if(sb.indexOf(dayModel.getId())==-1 && i>0 && otFlags[i].equals("0")){
									work_hour+=Double.valueOf(xdShift.getHours());
								}
							}
						}
					}
				}
				List<XdOvertimeSummary> ovList =	XdOvertimeSummary.dao.find(
						"select * from  xd_overtime_summary where emp_name='"+scheduleSummary.getEmpName()+
								"' and apply_date like '"+yearMonth+"%'");
				for (XdOvertimeSummary overtimeSummary : ovList) {
					if(overtimeSummary.getApplyType().equals("0")){
						natOTHours+=Double.valueOf(overtimeSummary.getApplyHours().equals("")?"0":overtimeSummary.getApplyHours());
					}else{
						othours+=Double.valueOf(overtimeSummary.getApplyHours().equals("")?"0":overtimeSummary.getApplyHours());
					}
				}
				returnOtHour=natOTHours+","+othours;
				scheduleSummary.setOthours(othours);
				scheduleSummary.setNatOthours(natOTHours);
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


		renderSuccess(returnOtHour);
	}

	public void updateCellV1(){
		String id = getPara("id");
		String oldValue = getPara("oldValue");
		String modValue = getPara("modValue");
		String overtime = getPara("overtime");
		String field = getPara("field");
		String type = getPara("type");
		double returnOtHour=0;
		double returnNatOTHour=0;
		XdScheduleSummary scheduleSummary  = XdScheduleSummary.dao.findById(id);
		if("2".equals(type)){
			scheduleSummary.setRemarks(modValue);
			scheduleSummary.update();
		}else{
			Class  superclass = scheduleSummary.getClass().getSuperclass();
			try {
				Method method = superclass.getMethod("set" + field.substring(0, 1).toUpperCase() + field.substring(1),String.class);
				method.invoke(scheduleSummary, modValue);
				int index = Integer.parseInt(field.replace("day", ""));
				List<XdShift> xdShifts = XdShift.dao.find("select * from  xd_shift");
				Map<String, XdShift> nameShiftObjMap = xdShifts.stream().collect(Collectors.toMap(XdShift::getShiftname, xdShift -> xdShift));
				String[] modifyFlags = scheduleSummary.getFlags().split(",");
				modifyFlags[index]="1";
				String modify="";
				for (String modifyFlag : modifyFlags) {
					modify=modify+","+modifyFlag;
				}
				scheduleSummary.setFlags(modify.replaceAll("^,",""));
				String ot="";
				String[] otFlags = scheduleSummary.getOtflags().split(",");
				String[] tipArr = scheduleSummary.getTips().split(",");
				if("on".equals(overtime)){
					otFlags[index]="1";
					XdShift shift = nameShiftObjMap.get(modValue);
					tipArr[index]=shift.getBusitime()+"-"+shift.getUnbusitime();
				}else{
					otFlags[index]="0";
					XdShift shift = nameShiftObjMap.get(modValue);
					tipArr[index]=tipArr[index].replaceAll(shift.getBusitime()+"-"+shift.getUnbusitime(),"0");
				}
				for (String otFlag : otFlags) {
					ot=ot+","+otFlag;
				}
				scheduleSummary.setOtflags(ot.replaceAll("^,",""));
				/*String tipStr="";
				for (String tip : tipArr) {
					tipStr=tipStr+","+tip;
				}
				scheduleSummary.setTips(tipStr.replaceAll("^,",""));*/



				String yearMonth=scheduleSummary.getScheduleYear()+"-"+scheduleSummary.getScheduleMonth();
				LocalDate lastMonLastDay = LocalDate.parse(yearMonth+"-01").minusDays(1);
				DateTimeFormatter dtf=DateTimeFormatter.ofPattern("yyyy-MM-dd");
				String lastMonthDay = dtf.format(lastMonLastDay);

				List<XdOvertimeSummary> applyList = XdOvertimeSummary.dao.find(
						"select * from  xd_overtime_summary where emp_name='"+scheduleSummary.getEmpName()+
								"' and apply_date like'" + yearMonth +
								"%' or apply_date='" + lastMonthDay + "'");
				for (XdOvertimeSummary overtimeSummary : applyList) {
					overtimeSummary.delete();
				}

				List<XdDayModel> xdDayModels = XdDayModel.dao.find("select * from  xd_day_model where days like '" + yearMonth + "%' or days='" + lastMonLastDay + "'");

				String nextMonFirstDay = DateTimeFormatter.ofPattern("yyyyMMdd").format(
						LocalDate.parse(xdDayModels.get(xdDayModels.size() - 1).getDays()).plusDays(1));

				XdDayModel nextMonFirstModel = XdDayModel.dao.findById(nextMonFirstDay);

				StringBuffer sb =new StringBuffer();
				xdDayModels.stream().forEach( xdDayModel-> {
					if("1".equals(xdDayModel.getIsnatHoliday())){
						sb.append(xdDayModel.getId()).append(",");
					}
				});
				if("1".equals(nextMonFirstModel.getIsnatHoliday())){
					sb.append(nextMonFirstModel.getId());
				}


				Map<String, String> holidaysMap = xdDayModels.stream().collect(Collectors.toMap(XdDayModel::getId, XdDayModel::getHolidays));
				double othours=0;//加班时间
				double natOTHours=0;//国定加班时间
				double work_hour=0;//出勤时间
				XdEmployee emp = XdEmployee.dao.findFirst("select * from xd_employee where name ='" + scheduleSummary.getEmpName() + "'");
				XdOvertimeSummary ots=new XdOvertimeSummary();
				ots.setEmpNum(scheduleSummary.getEmpNum());
				ots.setEmpName(scheduleSummary.getEmpName());
				ots.setEmpIdnum(emp==null?"":emp.getIdnum());
				ots.setProjectName(scheduleSummary.getProjectName());
				ots.setProjectId(scheduleSummary.getProjectValue());
				ots.setDeptName(scheduleSummary.getDeptName());
				ots.setDeptId(scheduleSummary.getDeptValue());
				ots.setCreateUser(ShiroKit.getUserId());
				ots.setCreateDate(DateUtil.getCurrentTime());
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
								//DateTimeFormatter dtf1=DateTimeFormatter.ofPattern("yyyy-MM-dd");
								String nextDate  = dtf.format(localDate);
								//是跨天且当天法定假日
								if(sb.indexOf(dayModel.getId())!=-1){
									ots.setId(null);
									ots.setApplyDate(dayModel.getDays());
									ots.setApplyStart(xdShift.getBusitime());
									ots.setApplyEnd("24:00");
									ots.setApplyHours(xdShift.getCurdayHours()+"");
									ots.setApplyType("0");
									ots.save();
									if(i>0){
										//othours+=Double.valueOf(xdShift.getCurdayHours());
										natOTHours+=Double.valueOf(xdShift.getCurdayHours());
									}
									//overTimeList.add(xdOvertimeSummary);
									//othours+=Double.valueOf(xdShift.getCurdayHours());
								}else{
									//跨天当天不是法定假日
									if(i>0){
										work_hour+=Double.valueOf(xdShift.getCurdayHours());
									}
									if(i==index && "on".equals(overtime)){
										//?
										ots.setId(null);
										ots.setApplyDate(dayModel.getDays());
										ots.setApplyStart(xdShift.getBusitime());
										ots.setApplyEnd("24:00");
										ots.setApplyHours(xdShift.getCurdayHours()+"");
										ots.setApplyType("1");
										ots.save();
									}


									//work_hour+=Double.valueOf(xdShift.getCurdayHours());
									//otFlags=otFlags+","+"0";
								}
								//是跨天且第二天是法定假日
								if(holidaysMap.get(nextDate)!=null && !"".equals(holidaysMap.get(nextDate))){
									ots.setId(null);
									ots.setApplyDate(nextDate);
									ots.setApplyStart("00:00");
									ots.setApplyEnd(xdShift.getUnbusitime());
									ots.setApplyHours(xdShift.getSpanHours()+"");
									ots.setApplyType("0");
									//overTimeList.add(xdOvertimeSummary);
									ots.save();
									if(i<xdDayModels.size()-1){
										othours+=Double.valueOf(xdShift.getSpanHours());
									}
								}else{//是跨天且第二天不是法定假日
									if(i<xdDayModels.size()-1){
										work_hour+=Double.valueOf(xdShift.getSpanHours());
									}

									if(i==index && "on".equals(overtime)){
										ots.setId(null);
										ots.setApplyDate(nextDate);
										ots.setApplyStart("00:00");
										ots.setApplyEnd(xdShift.getUnbusitime());
										ots.setApplyHours(xdShift.getSpanHours()+"");
										ots.setApplyType("1");
										ots.save();
									}
								}
							}else{

								if(holidaysMap.get(dayModel.getId())!=null && !"".equals(holidaysMap.get(dayModel.getId()))){
									ots.setId(null);
									ots.setApplyDate(dayModel.getDays());
									ots.setApplyStart(xdShift.getBusitime());
									ots.setApplyEnd(xdShift.getUnbusitime());
									ots.setApplyHours(xdShift.getHours()+"");
									ots.setApplyType("0");
									ots.save();
									//overTimeList.add(xdOvertimeSummary);
									if(i>0){
										othours+=Double.valueOf(xdShift.getHours());
									}

								}else{

									if(i==index){
										if( "on".equals(overtime)){
											ots.setId(null);
											ots.setApplyDate(dayModel.getDays());
											ots.setApplyStart(xdShift.getBusitime());
											ots.setApplyEnd(xdShift.getUnbusitime());
											ots.setApplyHours(xdShift.getHours()+"");
											ots.setApplyType("1");
											ots.save();
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
				returnOtHour=othours;
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


		renderSuccess(String.valueOf(returnOtHour));
	}





}