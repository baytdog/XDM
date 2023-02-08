package com.pointlion.mvc.admin.xdm.xdattendancesummary;

import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.*;
import com.pointlion.mvc.common.utils.DateUtil;
import com.pointlion.mvc.common.utils.UuidUtil;
import com.pointlion.mvc.common.utils.office.excel.ExcelUtil;
import com.pointlion.plugin.shiro.ShiroKit;
import com.pointlion.plugin.shiro.ext.SimpleUser;
import com.pointlion.util.DictMapping;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class XdAttendanceSummaryService{
	public static final XdAttendanceSummaryService me = new XdAttendanceSummaryService();
	public static final String TABLE_NAME = XdAttendanceSummary.tableName;

	/***
	 * query by id
	 */
	public XdAttendanceSummary getById(String id){
		return XdAttendanceSummary.dao.findById(id);
	}

	/***
	 * get page
	 */
	public Page<Record> getPage(int pnum,int psize,String dept,String emp_name,String unitname,String year,String month){
		String sql  = " from "+TABLE_NAME+" o where 1=1";
		if(StrKit.notBlank(dept)){
			sql = sql + " and o.dept_value='"+ dept+"'";
		}
		if(StrKit.notBlank(unitname)){
			sql = sql + " and o.unit_value='"+ unitname+"'";
		}
		if(StrKit.notBlank(year)){
			sql = sql + " and o.schedule_year='"+ year+"'";
		}
		if(StrKit.notBlank(month)){
			sql = sql + " and o.schedule_month='"+ month+"'";
		}
		if(StrKit.notBlank(emp_name)){
			sql = sql + " and o.emp_name like'%"+emp_name+"%'";
		}

		sql = sql + " order by o.create_date desc,emp_num";
		return Db.paginate(pnum, psize, " select * ", sql);
	}

	/***
	 * del
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
		String idarr[] = ids.split(",");
		for(String id : idarr){
			XdAttendanceSummary o = me.getById(id);
			o.delete();
		}
	}

	/**
	 * @Method importExcel
	 * @param list:
	 * @Date 2023/1/13 14:35
	 * @Exception
	 * @Description  加班申请导入
	 * @Author king
	 * @Version  1.1
	 * @Return java.util.Map<java.lang.String,java.lang.Object>
	 */
	public Map<String,Object> importExcel(List<List<String>> list) throws SQLException {
		final List<String> message = new ArrayList<String>();
		final Map<String,Object> result = new HashMap<String,Object>();
		Map<String, String> orgMapping = DictMapping.orgMapping("1");
		Map<String, Map<String, String>> stringMapMap = DictMapping.dictMapping();
		Map<String, String> unitMap = stringMapMap.get("unit");
		Map<String, String> projectsMap = DictMapping.projectsMapping();

		Db.tx(new IAtom() {
			@Override
			public boolean run() throws SQLException {
				try{
					if(list.size()>4){
						SimpleUser user = ShiroKit.getLoginUser();
						String time = DateUtil.getCurrentTime();
						List<String> title = list.get(0);
						String[] ny = title.get(0).replaceAll("年", "_").replaceAll("[^(0-9_)]", "").split("_");
						String year=ny[0];
						String month=ny[1];
						if(month.length()==1){
							month="0"+month;
						}
						String yearMonth=year+month;
						List<XdDayModel> xdDayModels = XdDayModel.dao.find("select * from  xd_day_model where id like '" + yearMonth + "%' order by id");
						Map<String, String> holidaysMap = xdDayModels.stream().collect(Collectors.toMap(XdDayModel::getId, XdDayModel::getHolidays));
						List<XdShift> xdShifts = XdShift.dao.find("select * from  xd_shift");
						Map<String, XdShift> nameShiftObjMap = xdShifts.stream().collect(Collectors.toMap(XdShift::getShiftname, xdShift -> xdShift));




						for(int i = 4;i<list.size();i++){//从第四行开始取

							int daysNum=xdDayModels.size();
							double actWorkHours=0;//本月实际工时
							double ordinaryOTHours=0;//平时加班
							double nationlOTHours=0;//国定加班
							double dutyCharge=0;//值班费
							int middleShiftDays=0; //中班天数
							int nightShiftDays=0;//夜班天数
							double highTempCharge=0;//高温费
							int  monthWorkDays=0;//每月 应工作天数
							int sickLeaveDays=0;//病假天数
							int personalLeaveDays=0;//事假天数
							int newLeaveDays=0;//新离缺勤天数
							int absenteeismDays=0;//旷工天数
							double alreayAnnualLeave=0;//已休年假
							boolean shift18A=false;
							List<String> summaryList = list.get(i);
							XdAttendanceSummary attendanceSummary=new XdAttendanceSummary();
							String summaryId = UuidUtil.getUUID();
							attendanceSummary.setId(summaryId);
							String department = summaryList.get(1);
							String depeId = orgMapping.get(department);
							attendanceSummary.setDeptName(department);
							attendanceSummary.setDeptValue(depeId);
							String unitName = summaryList.get(2);
							String unitValue = unitMap.get(unitName);
							attendanceSummary.setUnitName(unitName);
							attendanceSummary.setUnitValue(unitValue);
							String projectName = summaryList.get(3);
							String projectValue = projectsMap.get(projectName);
							attendanceSummary.setProjectName(projectName);
							attendanceSummary.setProjectValue(projectValue);
							String empNum = summaryList.get(4);
							attendanceSummary.setEmpNum(empNum);
							String empName = summaryList.get(5);
							attendanceSummary.setEmpName(empName);
							XdEmployee emp=XdEmployee.dao.findFirst("select * from  xd_employee where name='" + empName + "'");
							String workStation = summaryList.get(6);
							attendanceSummary.setWorkStation(workStation);
							attendanceSummary.setScheduleMonth(month);
							attendanceSummary.setScheduleYear(year);
							attendanceSummary.setScheduleYearMonth(yearMonth);

							if(emp==null){
								attendanceSummary.setAnnualleaveDays("");
							}else{
								XdAnleaveSummary leave=	XdAnleaveSummary.dao.findFirst("select *from  xd_anleave_summary where emp_name='"+empName+"'");
								if(leave==null){
									attendanceSummary.setAnnualleaveDays("");
								}else{
									attendanceSummary.setAnnualleaveDays(leave.getLeaveDays());
								}
							}
							attendanceSummary.setRemarks("");
							attendanceSummary.setWeeks("");
							attendanceSummary.setSpecialDesc("");
							attendanceSummary.setPerformRewardpunish("");
							attendanceSummary.setHireDate(emp==null?"":emp.getEntrytime());
							attendanceSummary.setDimissionDate(emp==null?"":emp.getDepartime());
							Class superclass = attendanceSummary.getClass().getSuperclass();
							for (int j = 1; j <=daysNum ; j++) {
								String ymr="";
								String ymd="";
								String methodsuffix="";
								if(j<10){
									ymd = yearMonth + '0' + j;
									ymr=year+"-"+month+"-"+'0' + j;
									methodsuffix="0" + j;
								}else {
									ymd = yearMonth  + j;
									ymr=year+"-"+month+"-"+j;
									methodsuffix=j+"";
								}


								XdAttendanceDetail attDetail =new XdAttendanceDetail();
								attDetail.setAttendidId(summaryId);
								String cellValue = summaryList.get(6 + j);
								if("18A".equals(cellValue)){
									shift18A=true;
								}
								Method method = superclass.getMethod("setDay" + methodsuffix,String.class);
								method.invoke(attendanceSummary,cellValue);
								if(!"".equals(cellValue)){
									XdShift shfit = nameShiftObjMap.get(cellValue);
									//加班结算开始
									if (shfit != null) {
										XdScheduleSummary scheduleSummary= XdScheduleSummary.dao.findFirst("select * from  xd_schedule_summary  where schedule_year='"+year
												+"' and schedule_month='"+month+"' and  emp_name='"+empName+"'");

										if(scheduleSummary==null){
											if(shfit.getSpanDay().equals("1")){
												LocalDate localDate = LocalDate.parse(ymr).plusDays(1);
												DateTimeFormatter dtf=DateTimeFormatter.ofPattern("yyyyMMdd");
												String nextDate  = dtf.format(localDate);
												DateTimeFormatter dtf3=DateTimeFormatter.ofPattern("yyyy-MM-dd");
												//是跨天且当天法定假日
												if(holidaysMap.get(ymd)!=null && !"".equals(holidaysMap.get(ymd))){
													XdOvertimeSummary overtimeSummary=new XdOvertimeSummary();
													overtimeSummary.setDeptId(depeId);
													overtimeSummary.setDeptName(department);
													overtimeSummary.setEmpNum(empNum);
													overtimeSummary.setEmpName(empNum);
													overtimeSummary.setProjectId(attendanceSummary.getProjectValue());
													overtimeSummary.setProjectName(attendanceSummary.getProjectName());
													overtimeSummary.setApplyDate(ymr);
													overtimeSummary.setApplyStart("");
													overtimeSummary.setApplyEnd("");
													overtimeSummary.setApplyHours("");
													overtimeSummary.setApplyType("0");
													overtimeSummary.setActStart(shfit.getBusitime());
													overtimeSummary.setActEnd("24:00");
													overtimeSummary.setActHours(shfit.getCurdayHours());
													overtimeSummary.setRemarks("");
													overtimeSummary.setCreateDate(DateUtil.getCurrentTime());
													overtimeSummary.setCreateUser(ShiroKit.getUserId());
													overtimeSummary.save();
												}else{
													XdOvertimeSummary overtimeSummary=new XdOvertimeSummary();
													overtimeSummary.setDeptId(depeId);
													overtimeSummary.setDeptName(department);
													overtimeSummary.setEmpNum(empNum);
													overtimeSummary.setEmpName(empNum);
													overtimeSummary.setProjectId(attendanceSummary.getProjectValue());
													overtimeSummary.setProjectName(attendanceSummary.getProjectName());
													overtimeSummary.setApplyDate(ymr);
													overtimeSummary.setApplyStart("");
													overtimeSummary.setApplyEnd("");
													overtimeSummary.setApplyHours("");
													overtimeSummary.setApplyType("1");
													overtimeSummary.setActStart(shfit.getBusitime());
													overtimeSummary.setActEnd("24:00");
													overtimeSummary.setActHours(shfit.getCurdayHours());
													overtimeSummary.setRemarks("");
													overtimeSummary.setCreateDate(DateUtil.getCurrentTime());
													overtimeSummary.setCreateUser(ShiroKit.getUserId());
													overtimeSummary.save();
												}
												//是跨天且第二天是法定假日
												if(holidaysMap.get(nextDate)!=null && !"".equals(holidaysMap.get(nextDate))){
													XdOvertimeSummary overtimeSummary=new XdOvertimeSummary();
													overtimeSummary.setDeptId(depeId);
													overtimeSummary.setDeptName(department);
													overtimeSummary.setEmpNum(empNum);
													overtimeSummary.setEmpName(empNum);
													overtimeSummary.setProjectId(attendanceSummary.getProjectValue());
													overtimeSummary.setProjectName(attendanceSummary.getProjectName());
													overtimeSummary.setApplyDate(dtf3.format(localDate));
													overtimeSummary.setApplyStart("");
													overtimeSummary.setApplyEnd("");
													overtimeSummary.setApplyHours("");
													overtimeSummary.setApplyType("0");
													overtimeSummary.setActStart("00:00");
													overtimeSummary.setActEnd(shfit.getUnbusitime());
													overtimeSummary.setActHours(shfit.getCurdayHours());
													overtimeSummary.setRemarks("");
													overtimeSummary.setCreateDate(DateUtil.getCurrentTime());
													overtimeSummary.setCreateUser(ShiroKit.getUserId());
													overtimeSummary.save();

												}else{

													XdOvertimeSummary overtimeSummary=new XdOvertimeSummary();
													overtimeSummary.setDeptId(depeId);
													overtimeSummary.setDeptName(department);
													overtimeSummary.setEmpNum(empNum);
													overtimeSummary.setEmpName(empNum);
													overtimeSummary.setProjectId(attendanceSummary.getProjectValue());
													overtimeSummary.setProjectName(attendanceSummary.getProjectName());
													overtimeSummary.setApplyDate(dtf3.format(localDate));
													overtimeSummary.setApplyStart("");
													overtimeSummary.setApplyEnd("");
													overtimeSummary.setApplyHours("");
													overtimeSummary.setApplyType("1");
													overtimeSummary.setActStart("00:00");
													overtimeSummary.setActEnd(shfit.getUnbusitime());
													overtimeSummary.setActHours(shfit.getCurdayHours());
													overtimeSummary.setRemarks("");
													overtimeSummary.setCreateDate(DateUtil.getCurrentTime());
													overtimeSummary.setCreateUser(ShiroKit.getUserId());
													overtimeSummary.save();

												}
											}else{
												XdOvertimeSummary overtimeSummary=new XdOvertimeSummary();
												overtimeSummary.setDeptId(depeId);
												overtimeSummary.setDeptName(department);
												overtimeSummary.setEmpNum(empNum);
												overtimeSummary.setEmpName(empNum);
												overtimeSummary.setProjectId(attendanceSummary.getProjectValue());
												overtimeSummary.setProjectName(attendanceSummary.getProjectName());
												overtimeSummary.setApplyDate(ymr);
												overtimeSummary.setApplyStart("");
												overtimeSummary.setApplyEnd("");
												overtimeSummary.setApplyHours("");
												overtimeSummary.setApplyType("1");
												overtimeSummary.setRemarks("");
												overtimeSummary.setCreateDate(DateUtil.getCurrentTime());
												overtimeSummary.setCreateUser(ShiroKit.getUserId());

												if(holidaysMap.get(ymd)!=null && !"".equals(holidaysMap.get(ymd))){
													/*if(shfit.getHours()!=null && !shfit.getHours().equals("")){
														nationlOTHours=nationlOTHours+ Double.valueOf(shfit.getHours());
													}*/
													overtimeSummary.setApplyType("0");
												}else{
													overtimeSummary.setApplyType("1");
												}
												overtimeSummary.setActStart(shfit.getBusitime());
												overtimeSummary.setActEnd(shfit.getUnbusitime());
												overtimeSummary.setActHours(shfit.getHours());
												overtimeSummary.save();
											}



										}else{
											Method getMethod = scheduleSummary.getClass().getSuperclass().getMethod("getDay" + methodsuffix);
											String values = (String)getMethod.invoke(scheduleSummary);
											if(cellValue.equals(values)){//和排班相同
												if(shfit.getSpanDay().equals("1")){//是跨天
													XdOvertimeSummary appOT=
															XdOvertimeSummary.dao.findFirst("select * from  xd_overtime_summary where apply_date='"
																	+ymr+"' and  emp_name='"+empName+"'");
													if(appOT!=null){
														appOT.setActStart(appOT.getApplyStart());
														appOT.setActEnd(appOT.getApplyEnd());
														appOT.setApplyHours(appOT.getApplyHours());
														appOT.update();
													}


													LocalDate localDate = LocalDate.parse(ymr).plusDays(1);
													DateTimeFormatter dtf=DateTimeFormatter.ofPattern("yyyy-MM-dd");
													String nextDate  = dtf.format(localDate);
													XdOvertimeSummary appOTNext=
															XdOvertimeSummary.dao.findFirst("select * from  xd_overtime_summary where apply_date='"
																	+nextDate+"' and  emp_name='"+empName+"'");
													if(appOTNext!=null){
														appOTNext.setActStart(appOTNext.getActStart());
														appOTNext.setActEnd(appOTNext.getApplyEnd());
														appOTNext.setActHours(appOTNext.getApplyHours());
														appOTNext.update();
													}



												}else{//不是跨天
													XdOvertimeSummary appOT=
															XdOvertimeSummary.dao.findFirst("select * from  xd_overtime_summary where apply_date='"
																	+ymr+"' and  emp_name='"+empName+"'");
													if(appOT!=null){
														appOT.setActStart(appOT.getApplyStart());
														appOT.setActEnd(appOT.getApplyEnd());
														appOT.setApplyHours(appOT.getApplyHours());
														appOT.update();
													}

												}
											}else{//和排班不同



											}
										}



									}


									//加班结算结束






									if (shfit != null) {
										attDetail.setShiftName(cellValue);
										attDetail.setWorkHours(shfit.getHours());
										attDetail.setMiddleNightShift("");
										if(shfit.getMiddleshift()!=null&& !shfit.getMiddleshift().equals("")){
											attDetail.setMiddleNightShift(shfit.getMiddleshift());
										}
										if(shfit.getNigthshift()!=null&& !shfit.getNigthshift().equals("")){
											attDetail.setMiddleNightShift(shfit.getNigthshift());
										}
										System.out.println(shfit.getHours());
										if(shfit.getHours()!=null && !shfit.getHours().equals("")){
											actWorkHours=actWorkHours+Double.valueOf(shfit.getHours());//实际工作时间
										}



										if(shfit.getSpanDay().equals("1")){
											LocalDate localDate = LocalDate.parse(ymr).plusDays(1);
											DateTimeFormatter dtf=DateTimeFormatter.ofPattern("yyyyMMdd");
											String nextDate  = dtf.format(localDate);
											//是跨天且当天法定假日
											if(holidaysMap.get(ymd)!=null && !"".equals(holidaysMap.get(ymd))){
												if(shfit.getCurdayHours()!=null && !shfit.getCurdayHours().equals("")){
													nationlOTHours=nationlOTHours+ Double.valueOf(shfit.getCurdayHours());
												}
											}
											//是跨天且第二天是法定假日
											if(holidaysMap.get(nextDate)!=null && !"".equals(holidaysMap.get(nextDate))){
												if(shfit.getSpanHours()!=null && !shfit.getSpanHours().equals("")){
													nationlOTHours=nationlOTHours+ Double.valueOf(shfit.getSpanHours());
												}
											}
										}else{
											if(holidaysMap.get(ymd)!=null && !"".equals(holidaysMap.get(ymd)) && !cellValue.equals("")){
												if(shfit.getHours()!=null && !shfit.getHours().equals("")){
													nationlOTHours=nationlOTHours+ Double.valueOf(shfit.getHours());
												}
											}
										}

										dutyCharge=dutyCharge+0;//值班费

										if(shfit.getMiddleshift()!=null&& !shfit.getMiddleshift().equals("")){
											middleShiftDays++;//中班天数
										}
										if(shfit.getNigthshift()!=null&& !shfit.getNigthshift().equals("")){
											 nightShiftDays++;//夜班天数
										}

										if(month.endsWith("7")||month.endsWith("8")||month.endsWith("9")){
											highTempCharge=300;//高温费
										}


									}
									monthWorkDays++;
									if(cellValue.equals("病")){
										sickLeaveDays++;
									}
									if(cellValue.equals("事")){
										personalLeaveDays++;
									}
									if(cellValue.equals("新离")){
										newLeaveDays++;
									}
									/*if(cellValue.equals("年")){
										alreayAnnualLeave++;
									}*/
									if(cellValue.contains("年")){
										String annual = cellValue.replace("年", "");
										if(annual.equals("")){
											alreayAnnualLeave=alreayAnnualLeave+1;
										}else{
											alreayAnnualLeave=alreayAnnualLeave+Double.valueOf(annual);
										}
									}

								}else{
									attDetail.setShiftName("");
									attDetail.setWorkHours("");
									attDetail.setMiddleNightShift("");
								}
								attDetail.setScheduleYear(year);
								attDetail.setScheduleMonth(month);
								attDetail.setScheduleDay(String.valueOf(j));
								attDetail.setScheduleYmd(ymr);
								attDetail.setWeekDay(xdDayModels.get(j - 1).getWeeks());
								attDetail.setHolidays(xdDayModels.get(j - 1).getHolidays());
								attDetail.setCreateDate(DateUtil.getCurrentTime());
								attDetail.setCreateUser(ShiroKit.getUserId());
								attDetail.save();
							}
							attendanceSummary.setCurmonActworkhours(String.valueOf(actWorkHours));
							attendanceSummary.setCurmonWorkhours(String.valueOf(actWorkHours));
							attendanceSummary.setCurmonBalancehours(String.valueOf(actWorkHours));//本月工时结余

							XdAttendanceSummary beforAttendanceSummary =XdAttendanceSummary.dao.findFirst("select * from xd_attendance_summary where emp_name='' order by   schedule_year_month desc");
							attendanceSummary.setPremonAccbalancehours(beforAttendanceSummary==null?"":beforAttendanceSummary.getCurmonAccbalancehours());//上月累计工时结余
							attendanceSummary.setCurmonAccbalancehours(String.valueOf(actWorkHours));//本月累计工时结余

							attendanceSummary.setCurmonSettlehours(String.valueOf(actWorkHours));//当月待结算工时
							attendanceSummary.setAccSettlehours(String.valueOf(actWorkHours));//累计待结算工时
							attendanceSummary.save();
							if(shift18A){
								XdRcpSummary rcp =new XdRcpSummary();
								rcp.setSummaryId(attendanceSummary.getId());
								rcp.setDeptValue(attendanceSummary.getDeptValue());
								rcp.setDeptName(attendanceSummary.getDeptName());
								rcp.setUnitValue(attendanceSummary.getUnitValue());
								rcp.setUnitName(attendanceSummary.getUnitName());
								rcp.setProjectValue(attendanceSummary.getProjectValue());
								rcp.setProjectName(attendanceSummary.getProjectName());
								rcp.setEmpName(empName);
								rcp.setIdnum(emp==null?"":emp.getIdnum());
								rcp.setWorStation(attendanceSummary.getWorkStation());
								rcp.setShfitName("18A");
								int mnShiftDays = middleShiftDays + nightShiftDays;
								rcp.setRcpDays(String.valueOf(mnShiftDays));
								rcp.setWorkDays(String.valueOf(monthWorkDays));
								rcp.setRcpStandard("100");
								if(mnShiftDays>=26){
									rcp.setRental("100");
								}else{
									//DecimalFormat df = new DecimalFormat("0.0");
									//String rcptenal = df.format(mnShiftDays*100/ (double) (26));
									String rcptenal = String.valueOf(mnShiftDays * 100 / (double) 26).substring(0,4);
									rcp.setRental(rcptenal);
								}
								rcp.setRcpYear(year);
								rcp.setRcpMonthYear(month);
								rcp.setRcpMonthYear(yearMonth);
								rcp.setStatus("0");
								rcp.setCreateUser("出勤生成");
								rcp.setCreateDate(time);
								rcp.save();


							}


//							for (int m = 1; m <= 12; m++) {
								XdAttendanceDays days=new XdAttendanceDays();
//								int i1 = 13 + 3 * daysNum + m;
								days.setAttendidId(summaryId);
								days.setOrdinaryOvertime(String.valueOf(ordinaryOTHours));
								days.setNationalOvertime(String.valueOf(nationlOTHours));
								days.setDutyCharge(String.valueOf(dutyCharge));
								days.setMidshiftDays(String.valueOf(middleShiftDays));
								days.setNightshiftDays(String.valueOf(nightShiftDays));
								days.setHightempAllowance(String.valueOf(highTempCharge));
								days.setMonshouldWorkdays(String.valueOf(monthWorkDays));
								days.setSickeleaveDays(String.valueOf(sickLeaveDays));
								days.setCasualleaveDays(String.valueOf(personalLeaveDays));
								days.setAbsencedutyDays(String.valueOf(newLeaveDays));
								days.setAbsentworkDays(String.valueOf(absenteeismDays));
								days.setRestanleaveDays(String.valueOf(alreayAnnualLeave));
								days.setCreateDate(DateUtil.getCurrentTime());
								days.setCreateUser(ShiroKit.getUserId());
								days.save();

							XdAttendanceDays days1=new XdAttendanceDays();
							days1.setAttendidId(summaryId);
							days1.setOrdinaryOvertime(String.valueOf(ordinaryOTHours));
							days1.setNationalOvertime(String.valueOf(nationlOTHours));
							days1.setDutyCharge(String.valueOf(dutyCharge));
							days1.setMidshiftDays(String.valueOf(middleShiftDays));
							days1.setNightshiftDays(String.valueOf(nightShiftDays));
							days1.setHightempAllowance(String.valueOf(highTempCharge));
							days1.setMonshouldWorkdays(String.valueOf(monthWorkDays));
							days1.setSickeleaveDays(String.valueOf(sickLeaveDays));
							days1.setCasualleaveDays(String.valueOf(personalLeaveDays));
							days1.setAbsencedutyDays(String.valueOf(newLeaveDays));
							days1.setAbsentworkDays(String.valueOf(absenteeismDays));
							days1.setRestanleaveDays(String.valueOf(alreayAnnualLeave));
							days1.setCreateDate(DateUtil.getCurrentTime());
							days1.setCreateUser(ShiroKit.getUserId());
							days1.save();

							XdAttendanceDays days2=new XdAttendanceDays();
							days2.setAttendidId(summaryId);

							days2.setOrdinaryOvertime(String.valueOf(ordinaryOTHours));
							days2.setNationalOvertime(String.valueOf(nationlOTHours));
							days2.setDutyCharge(String.valueOf(dutyCharge));
							days2.setMidshiftDays(String.valueOf(middleShiftDays));
							days2.setNightshiftDays(String.valueOf(nightShiftDays));
							days2.setHightempAllowance(String.valueOf(highTempCharge));
							days2.setMonshouldWorkdays(String.valueOf(monthWorkDays));
							days2.setSickeleaveDays(String.valueOf(sickLeaveDays));
							days2.setCasualleaveDays(String.valueOf(personalLeaveDays));
							days2.setAbsencedutyDays(String.valueOf(newLeaveDays));
							days2.setAbsentworkDays(String.valueOf(absenteeismDays));
							days2.setRestanleaveDays(String.valueOf(alreayAnnualLeave));
							days2.setCreateDate(DateUtil.getCurrentTime());
							days2.setCreateUser(ShiroKit.getUserId());
							days2.save();



							/*XdAttendanceRcp rcp =new XdAttendanceRcp();
							rcp.setAttendidId(summaryId);
							rcp.setMidnightDays(summaryList.get(58 + 3 * daysNum));
							rcp.setMonworkDays(summaryList.get(59 + 3 * daysNum));
							rcp.setAllowanceAmount(summaryList.get(60 + 3 * daysNum));
							rcp.setShiftName(summaryList.get(61 + 3 * daysNum));
							rcp.setRemarks(summaryList.get(62 + 3 * daysNum));
							rcp.setCreateDate(DateUtil.getCurrentTime());
							rcp.setCreateUser(ShiroKit.getUserId());
							rcp.save();*/

//							}


						}
						if(result.get("success")==null){
							result.put("success",true);//正常执行完毕
						}
					}else{
						result.put("success",false);//正常执行完毕
						message.add("excel中无内容");
						result.put("message", StringUtils.join(message," "));
					}
					result.put("message",StringUtils.join(message," "));
					if((Boolean) result.get("success")){//正常执行完毕
						return true;
					}else{//回滚
						return false;
					}
				}catch(Exception e){
					return false;
				}
			}
		});
		return result;
	}
	/**
	 * @Method importExcelV1
	 * @param list:
	 * @Date 2023/2/6 9:59
	 * @Exception
	 * @Description  出勤核算V1
	 * @Author king
	 * @Version  1.0
	 * @Return java.util.Map<java.lang.String,java.lang.Object>
	 */
	public Map<String,Object> importExcelV1(List<List<String>> list) throws SQLException {
		final List<String> message = new ArrayList<String>();
		final Map<String,Object> result = new HashMap<String,Object>();
		Map<String, String> orgMapping = DictMapping.orgMapping("1");
		Map<String, Map<String, String>> stringMapMap = DictMapping.dictMapping();
		Map<String, String> unitMap = stringMapMap.get("unit");
		Map<String, String> projectsMap = DictMapping.projectsMapping();

		Db.tx(new IAtom() {
			@Override
			public boolean run() throws SQLException {
				try{
					if(list.size()>4){
						SimpleUser user = ShiroKit.getLoginUser();
						String time = DateUtil.getCurrentTime();
						List<String> title = list.get(0);
						String[] ny = title.get(0).replaceAll("年", "_").replaceAll("[^(0-9_)]", "").split("_");
						String year=ny[0];
						String month=ny[1];
						if(month.length()==1){
							month="0"+month;
						}
						String yearMonth=year+month;
						List<XdDayModel> xdDayModels = XdDayModel.dao.find("select * from  xd_day_model where id like '" + yearMonth + "%' order by id");
						int daysNum=xdDayModels.size();
						for(int i = 4;i<list.size();i++){//从第四行开始取
							List<String> summaryList = list.get(i);
							XdAttendanceSummary attendanceSummary=new XdAttendanceSummary();
							String summaryId = UuidUtil.getUUID();
							attendanceSummary.setId(summaryId);
							String department = summaryList.get(1);
							String depeId = orgMapping.get(department);
							attendanceSummary.setDeptName(department);
							attendanceSummary.setDeptValue(depeId);
							String unitName = summaryList.get(2);
							String unitValue = unitMap.get(unitName);
							attendanceSummary.setUnitName(unitName);
							attendanceSummary.setUnitValue(unitValue);
							String projectName = summaryList.get(3);
							String projectValue = projectsMap.get(projectName);
							attendanceSummary.setProjectName(projectName);
							attendanceSummary.setProjectValue(projectValue);
							String empNum = summaryList.get(4);
							attendanceSummary.setEmpNum(empNum);
							String empName = summaryList.get(5);
							//XdEmployee emp = XdEmployee.dao.findFirst("select * from  xd_employee where name='" + empName + "'");
							attendanceSummary.setEmpName(empName);
							//attendanceSummary.setEmpId(emp.getId());
							String workStation = summaryList.get(6);
							attendanceSummary.setWorkStation(workStation);
							attendanceSummary.setScheduleMonth(month);
							attendanceSummary.setScheduleYear(year);
							attendanceSummary.setScheduleYearMonth(yearMonth);
							int colNum=7+3*daysNum;
							attendanceSummary.setCurmonActworkhours(summaryList.get(7+3*daysNum));
							attendanceSummary.setAnnualleaveDays(summaryList.get(7+3*daysNum+1));
							attendanceSummary.setCurmonWorkhours(summaryList.get(7+3*daysNum+2));
							attendanceSummary.setCurmonBalancehours(summaryList.get(7+3*daysNum+3));
							attendanceSummary.setPremonAccbalancehours(summaryList.get(7+3*daysNum+4));
							attendanceSummary.setCurmonAccbalancehours(summaryList.get(7+3*daysNum+5));
							attendanceSummary.setRemarks(summaryList.get(7+3*daysNum+6));
							attendanceSummary.setPerformRewardpunish(summaryList.get(7+3*daysNum+6+12*2));
							attendanceSummary.setHireDate(summaryList.get(7+3*daysNum+6+12*2+2));
							attendanceSummary.setDimissionDate(summaryList.get(7+3*daysNum+6+12*2+3));
							attendanceSummary.setSpecialDesc(summaryList.get(7+3*daysNum+7+12*3+4));
							attendanceSummary.setCurmonSettlehours(summaryList.get(7+3*daysNum+7+12*3+5));
							attendanceSummary.setAccSettlehours(summaryList.get(7+3*daysNum+7+12*3+6));
							attendanceSummary.setWeeks(summaryList.get(7+3*daysNum+7+12*3+7));
							attendanceSummary.save();
							for (int j = 1; j <=daysNum ; j++) {
								XdAttendanceDetail attDetail =new XdAttendanceDetail();
								attDetail.setAttendidId(summaryId);
								String cellValue1 = summaryList.get(6 + j);
								String cellValue2 = summaryList.get(6 + j+daysNum);
								String cellValue3 = summaryList.get(6 + j+2*daysNum);
								attDetail.setShiftName(cellValue1);
								attDetail.setWorkHours(cellValue2);
								attDetail.setMiddleNightShift(cellValue3);

								attDetail.setScheduleYear(year);
								attDetail.setScheduleMonth(month);
								attDetail.setScheduleDay(String.valueOf(j));
								String day="";
								if(j<10){
									day="0"+j;
								} else{
									day=j+"";
								}
								attDetail.setScheduleYmd(year+"-"+month+"-"+day);
								attDetail.setWeekDay(xdDayModels.get(j - 1).getWeeks());
								attDetail.setHolidays(xdDayModels.get(j - 1).getHolidays());
								attDetail.setCreateDate(DateUtil.getCurrentTime());
								attDetail.setCreateUser(ShiroKit.getUserId());
								attDetail.save();


							}

//							for (int m = 1; m <= 12; m++) {
								XdAttendanceDays days=new XdAttendanceDays();
//								int i1 = 13 + 3 * daysNum + m;
								days.setAttendidId(summaryId);
								days.setOrdinaryOvertime(summaryList.get(14 + 3 * daysNum));
								days.setNationalOvertime(summaryList.get(15 + 3 * daysNum));
								days.setDutyCharge(summaryList.get(16 + 3 * daysNum));
								days.setMidshiftDays(summaryList.get(17 + 3 * daysNum));
								days.setNightshiftDays(summaryList.get(18 + 3 * daysNum));
								days.setHightempAllowance(summaryList.get(19 + 3 * daysNum));
								days.setMonshouldWorkdays(summaryList.get(20 + 3 * daysNum));
								days.setSickeleaveDays(summaryList.get(21 + 3 * daysNum));
								days.setCasualleaveDays(summaryList.get(22 + 3 * daysNum));
								days.setAbsencedutyDays(summaryList.get(23 + 3 * daysNum));
								days.setAbsentworkDays(summaryList.get(24 + 3 * daysNum));
								days.setRestanleaveDays(summaryList.get(25 + 3 * daysNum));
								days.setCreateDate(DateUtil.getCurrentTime());
								days.setCreateUser(ShiroKit.getUserId());
								days.save();

							XdAttendanceDays days1=new XdAttendanceDays();
							days1.setAttendidId(summaryId);
							days1.setOrdinaryOvertime(summaryList.get(26 + 3 * daysNum));
							days1.setNationalOvertime(summaryList.get(27 + 3 * daysNum));
							days1.setDutyCharge(summaryList.get(28 + 3 * daysNum));
							days1.setMidshiftDays(summaryList.get(29 + 3 * daysNum));
							days1.setNightshiftDays(summaryList.get(30 + 3 * daysNum));
							days1.setHightempAllowance(summaryList.get(31 + 3 * daysNum));
							days1.setMonshouldWorkdays(summaryList.get(32 + 3 * daysNum));
							days1.setSickeleaveDays(summaryList.get(33 + 3 * daysNum));
							days1.setCasualleaveDays(summaryList.get(34 + 3 * daysNum));
							days1.setAbsencedutyDays(summaryList.get(35 + 3 * daysNum));
							days1.setAbsentworkDays(summaryList.get(36 + 3 * daysNum));
							days1.setRestanleaveDays(summaryList.get(38 + 3 * daysNum));
							days1.setCreateDate(DateUtil.getCurrentTime());
							days1.setCreateUser(ShiroKit.getUserId());
							days1.save();

							XdAttendanceDays days2=new XdAttendanceDays();
							days2.setAttendidId(summaryId);
							days2.setOrdinaryOvertime(summaryList.get(41 + 3 * daysNum));
							days2.setNationalOvertime(summaryList.get(42 + 3 * daysNum));
							days2.setDutyCharge(summaryList.get(43 + 3 * daysNum));
							days2.setMidshiftDays(summaryList.get(44 + 3 * daysNum));
							days2.setNightshiftDays(summaryList.get(45 + 3 * daysNum));
							days2.setHightempAllowance(summaryList.get(46 + 3 * daysNum));
							days2.setMonshouldWorkdays(summaryList.get(47 + 3 * daysNum));
							days2.setSickeleaveDays(summaryList.get(48 + 3 * daysNum));
							days2.setCasualleaveDays(summaryList.get(49 + 3 * daysNum));
							days2.setAbsencedutyDays(summaryList.get(50 + 3 * daysNum));
							days2.setAbsentworkDays(summaryList.get(51 + 3 * daysNum));
							days2.setRestanleaveDays(summaryList.get(52 + 3 * daysNum));
							days2.setCreateDate(DateUtil.getCurrentTime());
							days2.setCreateUser(ShiroKit.getUserId());
							days2.save();



							XdAttendanceRcp rcp =new XdAttendanceRcp();
							rcp.setAttendidId(summaryId);
							rcp.setMidnightDays(summaryList.get(58 + 3 * daysNum));
							rcp.setMonworkDays(summaryList.get(59 + 3 * daysNum));
							rcp.setAllowanceAmount(summaryList.get(60 + 3 * daysNum));
							rcp.setShiftName(summaryList.get(61 + 3 * daysNum));
							rcp.setRemarks(summaryList.get(62 + 3 * daysNum));
							rcp.setCreateDate(DateUtil.getCurrentTime());
							rcp.setCreateUser(ShiroKit.getUserId());
							rcp.save();

//							}


						}
						if(result.get("success")==null){
							result.put("success",true);//正常执行完毕
						}
					}else{
						result.put("success",false);//正常执行完毕
						message.add("excel中无内容");
						result.put("message", StringUtils.join(message," "));
					}
					result.put("message",StringUtils.join(message," "));
					if((Boolean) result.get("success")){//正常执行完毕
						return true;
					}else{//回滚
						return false;
					}
				}catch(Exception e){
					return false;
				}
			}
		});
		return result;
	}



	public File exportExcel(String path, String dept, String unitname, String year, String month, String emp_name){
		String sql  = " from "+TABLE_NAME+" o   where 1=1";
		if(StrKit.notBlank(dept)){
			sql = sql + " and o.dept_value='"+ dept+"'";
		}
		if(StrKit.notBlank(unitname)){
			sql = sql + " and o.unit_value='"+ unitname+"'";
		}
		if(StrKit.notBlank(year)){
			sql = sql + " and o.schedule_year='"+ year+"'";
		}
		if(StrKit.notBlank(month)){
			sql = sql + " and o.schedule_month='"+ month+"'";
		}
		if(StrKit.notBlank(emp_name)){
			sql = sql + " and o.emp_name like'%"+emp_name+"%'";
		}

		sql = sql + " order by o.create_date desc,emp_num";


		List<XdAttendanceSummary> list = XdAttendanceSummary.dao.find(" select * "+sql);//查询全部


		Map<String, Map<String, String>> dictMap = DictMapping.dictMappingValueToName();
		Map<String, String> projectMap = DictMapping.projectsMappingValueToName();
		Map<String, String> orgMap = DictMapping.orgMapping("0");
		String departName = orgMap.get(dept)==null?"":orgMap.get(dept);
		Map<String, String> unit = dictMap.get("unit");
		String unitName = unit.get(unitname)==null?"":unit.get(unitname);
		String years="";
		String months="";
		String title="出勤表";
		if(StrKit.notBlank(year)){
			years=year+"年";
		}
		if(StrKit.notBlank(month)){
			months=month+"月";
		}
		title=departName+unitName+years+months+title;
		List<XdDayModel> xdDayModels = XdDayModel.dao.find("select * from  xd_day_model where id like'" + year + month + "%' order by id");
		List<List<String>> rows = new ArrayList<List<String>>();
		List<String> titleRow=new ArrayList<String>();
		titleRow.add(title);
		rows.add(titleRow);

		List<String> first = new ArrayList<String>();
		List<String> second = new ArrayList<String>();
		List<String> third = new ArrayList<String>();
		first.add("序号");
		first.add("部门");//0
		first.add("单元");
		first.add("项目");//1
		first.add("工号");//2
		first.add("姓名");//3
		first.add("岗位");//3
		for (int i = 0; i <7 ; i++) {
			second.add("");
			third.add("");
		}
		for (int j = 0; j <3 ; j++) {
			for (int i = 0; i < xdDayModels.size(); i++) {
				XdDayModel dayModel = xdDayModels.get(i);
				first.add(String.valueOf(i+1));
				second.add(dayModel.getWeeks());
				third.add(dayModel.getHolidays());
			}
		}


		first.add("本月\n" +
				"实际工时");
		second.add("");
		third.add("");
		first.add("年假天数");
		second.add("");
		third.add("");
		first.add("本月工时");
		second.add("");
		third.add("");
		first.add("本月\n" +
				"工时结余");
		second.add("");
		third.add("");
		first.add("上月累计工时结余");
		second.add("");
		third.add("");
		first.add("本月累计工时结余");
		second.add("");
		third.add("");
		first.add("备注\n（调整说明及其他说明）");
		second.add("");
		third.add("");
		/*second.add("");
		third.add("");*/
		first.add("平时\n加班");
		second.add("");
		third.add("");
		first.add("国定");
		second.add("");
		third.add("");
		first.add("值班费");
		second.add("");
		third.add("");
		first.add("中班");
		second.add("");
		third.add("天数");
		first.add("夜班");
		second.add("");
		third.add("天数");
		first.add("高温费");
		second.add("");
		third.add("");
		first.add("每月\n应工作\n天数");
		second.add("");
		third.add("");
		first.add("病假\n天数");
		second.add("");
		third.add("");
		first.add("事假\n天数");
		second.add("");
		third.add("");
		first.add("新离缺\n勤天数");
		second.add("");
		third.add("");
		first.add("旷工\n天数");
		second.add("");
		third.add("");
		first.add("已休\n年假");
		second.add("");
		third.add("");
		first.add("平时\n加班");
		second.add("");
		third.add("");
		first.add("国定");
		second.add("");
		third.add("");
		first.add("值班费");
		second.add("");
		third.add("");
		first.add("中班");
		second.add("");
		third.add("");
		first.add("夜班");
		second.add("");
		third.add("");
		first.add("高温费");
		second.add("");
		third.add("");
		first.add("每月\n应工作\n天数");
		second.add("");
		third.add("");
		first.add("病假\n天数");
		second.add("");
		third.add("");
		first.add("事假\n天数");
		second.add("");
		third.add("");
		first.add("新离缺\n勤天数");
		second.add("");
		third.add("");
		first.add("旷工\n天数");
		second.add("");
		third.add("");
		first.add("绩效奖惩");
		second.add("");
		third.add("");
		first.add("已休\n年假");
		second.add("");
		third.add("");
		first.add("入职日期");
		second.add("");
		third.add("");
		first.add("离职日期");
		second.add("");
		third.add("");
		first.add("平时\n" +
				"加班");
		second.add("");
		third.add("");
		first.add("国定");
		second.add("");
		third.add("");
		first.add("值班费");
		second.add("");
		third.add("");
		first.add("中班");
		second.add("");
		third.add("");
		first.add("夜班");
		second.add("");
		third.add("");
		first.add("高温费");
		second.add("");
		third.add("");
		first.add("每月\n" +
				"应工作\n" +
				"天数");
		second.add("");
		third.add("");
		first.add("病假\n" +
				"天数");
		second.add("");
		third.add("");
		first.add("事假\n" +
				"天数");
		second.add("");
		third.add("");
		first.add("新离缺\n" +
				"勤天数");
		second.add("");
		third.add("");
		first.add("旷工\n" +
				"天数");
		second.add("");
		third.add("");
		first.add("已休\n" +
				"年假");
		second.add("");
		third.add("");
		first.add("校对");
		second.add("");
		third.add("");
		first.add("特殊情况说明");
		second.add("");
		third.add("");
		first.add("当月待结算工时");
		second.add("");
		third.add("");
		first.add("累计待结算工时");
		second.add("");
		third.add("");
		first.add("周数");
		second.add("");
		third.add("");
		first.add("中夜班上班天数");
		second.add("");
		third.add("");
		first.add("每月上班天数");
		second.add("");
		third.add("");
		first.add("津贴金额");
		second.add("");
		third.add("");
		first.add("班次");
		second.add("");
		third.add("");
		first.add("备注");
		second.add("");
		third.add("");
		rows.add(first);
		rows.add(second);
		rows.add(third);

		for(int j = 0; j < list.size(); j++){
			List<String> row = new ArrayList<String>();
			row.add(String.valueOf(j+1));
			XdAttendanceSummary summary = list.get(j);
			List<XdAttendanceDetail> xdAttendanceDetails = XdAttendanceDetail.dao.find("select * from  xd_attendance_detail where attendid_id='"+summary.getId()+"' order by schedule_ymd");
			row.add(summary.getDeptName());//0
			row.add(summary.getUnitName());//0
			row.add(summary.getProjectName());//0
			row.add(summary.getEmpNum());
			row.add(summary.getEmpName());
			row.add(summary.getWorkStation());
			for (int i = 0; i <3 ; i++) {
				for (XdAttendanceDetail detail : xdAttendanceDetails) {
					if(i==0){
						row.add(detail.getShiftName());
					}else if(i==1){
						row.add(detail.getWorkHours());
					}else{
						row.add(detail.getMiddleNightShift());
					}

				}
			}


			row.add(summary.getCurmonActworkhours());
			row.add(summary.getAnnualleaveDays());
			row.add(summary.getCurmonWorkhours());
			row.add(summary.getCurmonBalancehours());
			row.add(summary.getPremonAccbalancehours());
			row.add(summary.getCurmonAccbalancehours());
			row.add(summary.getRemarks());
			List<XdAttendanceDays> xdAttendanceDays = XdAttendanceDays.dao.find("select * from  xd_attendance_days   where  attendid_id='" + summary.getId() + "' order by id");
			for (int i = 0; i < xdAttendanceDays.size(); i++) {
				XdAttendanceDays  attendanceDays = xdAttendanceDays.get(i);
				row.add(attendanceDays.getOrdinaryOvertime());
				row.add(attendanceDays.getNationalOvertime());
				row.add(attendanceDays.getDutyCharge());
				row.add(attendanceDays.getMidshiftDays());
				row.add(attendanceDays.getNightshiftDays());
				row.add(attendanceDays.getHightempAllowance());
				row.add(attendanceDays.getMonshouldWorkdays());
				row.add(attendanceDays.getSickeleaveDays());
				row.add(attendanceDays.getCasualleaveDays());
				row.add(attendanceDays.getAbsencedutyDays());
				row.add(attendanceDays.getAbsentworkDays());
				if(i==1){
					row.add(summary.getPerformRewardpunish());
				}
				row.add(attendanceDays.getRestanleaveDays());
				if(i==1){
					row.add(summary.getHireDate());
					row.add(summary.getDimissionDate());
				}
				if(i==2){
					row.add(attendanceDays.getProofreadDesc());
				}
			}

			row.add(summary.getSpecialDesc());
			row.add(summary.getCurmonSettlehours());
			row.add(summary.getAccSettlehours());
			row.add(summary.getWeeks());

			XdRcpSummary rcp= XdRcpSummary.dao.findFirst("select * from  xd_rcp_summary  where  summary_id='"+summary.getId()+"'");
			row.add(rcp==null?"":rcp.getRcpDays());
			row.add(rcp==null?"":rcp.getWorkDays());
			row.add(rcp==null?"":rcp.getRental());
			row.add(rcp==null?"":rcp.getShfitName());
			row.add(rcp==null?"":rcp.getRemarks());

			rows.add(row);
		}
		File file = ExcelUtil.attendanceFile(path,rows,xdDayModels.size());
		return file;
	}


	public File exportRewardPunishExcel(String path, String dept,String year, String month){
		String sql  = " from "+TABLE_NAME+" o   where  perform_rewardpunish !=''";
		if(StrKit.notBlank(dept)){
			sql = sql + " and o.dept_value='"+ dept+"'";
		}
		if(StrKit.notBlank(year)){
			sql = sql + " and o.schedule_year='"+ year+"'";
		}
		if(StrKit.notBlank(month)){
			sql = sql + " and o.schedule_month='"+ month+"'";
		}

		sql = sql + " order by o.create_date desc,emp_num";


		List<XdAttendanceSummary> list = XdAttendanceSummary.dao.find(" select * "+sql);//查询全部


		Map<String, Map<String, String>> dictMap = DictMapping.dictMappingValueToName();
		Map<String, String> projectMap = DictMapping.projectsMappingValueToName();
		Map<String, String> orgMap = DictMapping.orgMapping("0");
		String departName = orgMap.get(dept)==null?"":orgMap.get(dept);
		Map<String, String> unit = dictMap.get("unit");
//		String unitName = unit.get(unitname)==null?"":unit.get(unitname);
		String years="";
		String months="";
		//String title="出勤表";
		if(StrKit.notBlank(year)){
			years=year+"年";
		}
		if(StrKit.notBlank(month)){
			months=month+"月";
		}
		List<String> titleFirstRow=new ArrayList<String>();
		List<String> titleSecondRow=new ArrayList<String>();
		titleFirstRow.add("上海东方欣迪商务服务有限公司");
		for (int i = 0; i <6 ; i++) {
			titleFirstRow.add("");
		}

		SysOrg org = SysOrg.dao.findById(dept);
		titleSecondRow.add("");
		titleSecondRow.add("");
		titleSecondRow.add("");
		titleSecondRow.add(org.getName());
		titleSecondRow.add("绩效奖惩表");
		titleSecondRow.add("");
		titleSecondRow.add(years+months);

/*
		for (int i =1; i <=7; i++) {
			if(i==12){
				titleFirstRow.add("上海东方欣迪商务服务有限公司");
//				titleSecondRow.add("RCP项目津贴申请表");
//				titleThirdRow.add("");

			}else if(i==11){
				titleFirstRow.add("");
				titleSecondRow.add("");
				titleThirdRow.add(ny);
			}else{
				titleFirstRow.add("");
				titleSecondRow.add("");
				titleThirdRow.add("");
			}

		}*/
//		title=departName+unitName+years+months+title;
//		List<XdDayModel> xdDayModels = XdDayModel.dao.find("select * from  xd_day_model where id like'" + year + month + "%' order by id");
		List<List<String>> rows = new ArrayList<List<String>>();
		List<String> titleRow=new ArrayList<String>();
		//titleRow.add(title);
		rows.add(titleFirstRow);
		rows.add(titleSecondRow);

		List<String> first = new ArrayList<String>();
		first.add("条线");
		first.add("所在单元");//0
		first.add("项目");//1
		first.add("姓名");//3
		first.add("身份证号");//2
		first.add("绩效奖惩金额");//3
		first.add("备注");//3


		rows.add(first);

		double sum=0;
		for(int j = 0; j < list.size(); j++){
			List<String> row = new ArrayList<String>();
			//row.add(String.valueOf(j+1));
			XdAttendanceSummary summary = list.get(j);
			//List<XdAttendanceDetail> xdAttendanceDetails = XdAttendanceDetail.dao.find("select * from  xd_attendance_detail where attendid_id='"+summary.getId()+"' order by schedule_ymd");
			row.add(org.getName());//0
			row.add(summary.getUnitName());//0
			row.add(summary.getProjectName());//0
			row.add(summary.getEmpName());
			XdEmployee emp = XdEmployee.dao.findFirst("select * from  xd_employee where name='" + summary.getEmpName() + "' ");
			row.add(emp==null?"":emp.getIdnum());
			row.add(summary.getPerformRewardpunish());
			row.add(summary.getSpecialDesc());
			sum=sum+Math.abs(Double.valueOf(summary.getPerformRewardpunish()));
			rows.add(row);
		}
		List<String> countRow = new ArrayList<String>();
		countRow.add("");
		countRow.add("");
		countRow.add("");
		countRow.add("");
		countRow.add("");
		countRow.add(String.valueOf(sum));
		countRow.add("");
		rows.add(countRow);

		List<String> blankRow = new ArrayList<String>();
		rows.add(blankRow);
		rows.add(blankRow);
		List<String> firstFootRow = new ArrayList<String>();
		firstFootRow.add("分管领导：");
		firstFootRow.add("");
		firstFootRow.add("");
		firstFootRow.add("人力资源部：");
		firstFootRow.add("");
		firstFootRow.add("部门：");
		firstFootRow.add("");
		List<String> secondFootRow = new ArrayList<String>();
		secondFootRow.add("日    期：");
		secondFootRow.add("");
		secondFootRow.add("");
		secondFootRow.add("日    期：");
		secondFootRow.add("");
		secondFootRow.add("日    期：");
		secondFootRow.add("");
		rows.add(firstFootRow);
		rows.add(secondFootRow);
		File file = ExcelUtil.exportRewardPunishFile(path,rows);
		return file;
	}


	public File exportCheckInExcel(String path, String dept,String year, String month){
		String sql  = " from "+TABLE_NAME+" o   where  1=1";
		if(StrKit.notBlank(dept)){
			sql = sql + " and o.dept_value='"+ dept+"'";
		}
		if(StrKit.notBlank(year)){
			sql = sql + " and o.schedule_year='"+ year+"'";
		}
		if(StrKit.notBlank(month)){
			sql = sql + " and o.schedule_month='"+ month+"'";
		}

		sql = sql + " order by o.create_date desc,emp_num";


		List<XdAttendanceSummary> list = XdAttendanceSummary.dao.find(" select * "+sql);//查询全部


		Map<String, Map<String, String>> dictMap = DictMapping.dictMappingValueToName();
		Map<String, String> projectMap = DictMapping.projectsMappingValueToName();
		Map<String, String> orgMap = DictMapping.orgMapping("0");
		String departName = orgMap.get(dept)==null?"":orgMap.get(dept);
		Map<String, String> unit = dictMap.get("unit");
//		String unitName = unit.get(unitname)==null?"":unit.get(unitname);
		String years="";
		String months="";
		//String title="出勤表";
		if(StrKit.notBlank(year)){
			years=year+"年";
		}
		if(StrKit.notBlank(month)){
			months=month+"月";
		}
		List<String> titleFirstRow=new ArrayList<String>();
		List<String> titleSecondRow=new ArrayList<String>();
		List<String> titleThirdRow=new ArrayList<String>();
		titleFirstRow.add("上海东方欣迪商务服务有限公司");
		titleSecondRow.add("考勤统计表");
		for (int i = 0; i <28 ; i++) {
			titleFirstRow.add("");
			titleSecondRow.add("");
		}
		for (int i = 0; i <8 ; i++) {
			titleThirdRow.add("");
		}
		SysOrg org = SysOrg.dao.findById(dept);
		titleThirdRow.add("部门：");
		titleThirdRow.add(org.getName());
		titleThirdRow.add("");
		titleThirdRow.add("");
		titleThirdRow.add("统计月份：");
		titleThirdRow.add("");
		titleThirdRow.add(year);
		titleThirdRow.add("年");
		titleThirdRow.add(month);
		titleThirdRow.add("月");
		for (int i = 0; i < 11; i++) {
			titleThirdRow.add("");
		}



		List<List<String>> rows = new ArrayList<List<String>>();
		List<String> titleRow=new ArrayList<String>();
		//titleRow.add(title);
		rows.add(titleFirstRow);
		rows.add(titleSecondRow);
		rows.add(titleThirdRow);

		List<String> first = new ArrayList<String>();
		first.add("序号");
		first.add("部门");//0
		first.add("单元名称");//1
		first.add("项目名称");//1
		first.add("员工工号");//1
		first.add("姓名");//3
		first.add("职员代码");//3
		first.add("平时加班");//2
		first.add("双休日加班");//2
		first.add("节假日加班");//3
		first.add("值班费");//3
		first.add("中班天数");//3
		first.add("夜班天数");//3
		first.add("高温费");//3
		first.add("工龄津贴");//3
		first.add("兼项津贴");//3
		first.add("其他津贴");//3
		first.add("其他调整");//3
		first.add("每月应工作天数");//3
		first.add("病假天数");//3
		first.add("事假天数");//3
		first.add("新离缺勤天数");//3
		first.add("旷工天数");//3
		first.add("试用期扣款");//3
		first.add("绩效奖惩");//3
		first.add("备注");//3
		first.add("已年假天数");//3
		first.add("入职时间");//3
		first.add("离职时间");//3
		rows.add(first);

		double sum=0;
		for(int j = 0; j < list.size(); j++){
			List<String> row = new ArrayList<String>();
			XdAttendanceSummary summary = list.get(j);
			row.add(String.valueOf(j+1));
			row.add(summary.getDeptName());
			row.add(summary.getUnitName());
			row.add(summary.getProjectName());
			row.add(summary.getEmpNum());
			row.add(summary.getEmpName());
			row.add("");//职员代码
			XdAttendanceDays days = XdAttendanceDays.dao.findFirst("select * from  xd_attendance_days where attendid_id='" + summary.getId() + "'");
			row.add(days.getOrdinaryOvertime());
			row.add("");//双休日加班
			row.add(days.getNationalOvertime());
			row.add(days.getDutyCharge());
			row.add(days.getMidshiftDays());
			row.add(days.getNightshiftDays());
			row.add(days.getHightempAllowance());
			row.add("");//工龄津贴
			row.add("");//兼项津贴
			row.add("");//其他津贴
			row.add("");//其他调整
			row.add(days.getMonshouldWorkdays());
			row.add(days.getSickeleaveDays());
			row.add(days.getCasualleaveDays());
			row.add(days.getAbsencedutyDays());
			row.add(days.getAbsentworkDays());
			row.add("");//试用期扣款
			row.add(summary.getPerformRewardpunish());
			row.add(summary.getSpecialDesc());
			row.add(days.getRestanleaveDays());
			row.add(summary.getHireDate());
			row.add(summary.getDimissionDate());
			rows.add(row);
		}

		File file = ExcelUtil.exportCheckInExcelFile(path,rows);
		return file;
	}




	
}