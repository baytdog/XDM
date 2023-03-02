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
import com.pointlion.util.CheckAttendanceUtil;
import com.pointlion.util.DictMapping;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
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



	public Page<Record> getPage(int pnum,int psize,String day,String hours,String min)  {
		String sql  = " from "+TABLE_NAME+" o where 1=1";

		String shfitName="";
		if(StrKit.notBlank(hours)){
		if(min.equals("")){
			min="00";
		}
		String hm="";
		if(StrKit.notBlank(min)){
			if(min.length()<2){
				hm=hours+":"+"0"+min;
			}else{
				hm=hours+":"+min;
			}
		}
		;



		List<XdShift> xdShifts = XdShift.dao.find("select * from  xd_shift");

		String start="";
		String end="";

		Long startL=0L;
		Long endL=0L;
		Long inputSecond=0L;

			SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			try {
				inputSecond = sdf.parse(day+" "+hm+":00").getTime();
			} catch (ParseException e) {
				e.printStackTrace();
			}
			for (XdShift shift : xdShifts) {
			if(shift.getBusitime()!=null && !"".equals(shift.getBusitime())){
				if(shift.getBusitime().split(":")[0].length()==1){
					start="0"+shift.getBusitime().split(":")[0]+shift.getBusitime().split(":")[1];
				}else{
					start=shift.getBusitime();
				}

				if(shift.getUnbusitime().split(":")[0].length()==1){
					end="0"+shift.getUnbusitime().split(":")[0]+shift.getUnbusitime().split(":")[1];
				}else{
					end=shift.getUnbusitime();
				}

				try {

					startL=sdf.parse(day+" "+start+":00").getTime();
					endL=sdf.parse(day+" "+end+":00").getTime();
				} catch (ParseException e) {
					e.printStackTrace();
				}

				if(shift.getSpanDay().equals("1")){

					try {
						endL=sdf.parse(DateTimeFormatter.ofPattern("yyyy-MM-dd").format(LocalDate.parse(day).plusDays(1))+" "+end+":00").getTime();
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}
			}

			if(inputSecond>=startL && inputSecond<=endL){
				startL=0L;
				endL=0L;
				shfitName=shfitName+"'"+shift.getShiftname()+"'"+",";
			}

		}

		shfitName=shfitName.replaceAll(",$","");
		}
		String s = day.split("-")[2];
		String yearMonth=day.split("-")[0]+day.split("-")[1];

//		select * from  xd_attendance_summary where schedule_year_month=''
 		sql=sql+ " and schedule_year_month='"+yearMonth+"' ";

 		if(StrKit.notBlank(min)){
 			sql=sql+"and day"+day.split("-")[2]+" in ("+shfitName+")";
		}




		sql = sql + " order by o.create_date desc,emp_num";
		return Db.paginate(pnum, psize, " select emp_name,emp_num,schedule_year_month,day"+day.split("-")[2]+" as shift", sql);
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

		final Map<String,Object> result = new HashMap<String,Object>();
		final List<String> message = new ArrayList<String>();
		Map<String, String> orgMapping = DictMapping.orgMapping("1");
		Map<String, Map<String, String>> stringMapMap = DictMapping.dictMapping();
		Map<String, String> unitMap = stringMapMap.get("unit");
		Map<String, String> projectsMap = DictMapping.projectsMapping();

		Db.tx(new IAtom() {
			@Override
			public boolean run() throws SQLException {
				try{
					if(list.size()>3){
						List<String> title = list.get(0);
						String[] ny = title.get(0).replaceAll("年", "_").replaceAll("[^(0-9_)]", "").split("_");
						String year=ny[0];
						String month=ny[1];
						month=month.length()==1?("0"+month):month;


						String yearMonth=year+month;

						DateTimeFormatter dtf=DateTimeFormatter.ofPattern("yyyy-MM-dd");

						String lastMonLastDay = dtf.format(LocalDate.parse(year+"-"+month+"-01").minusDays(1));


						List<XdDayModel> xdDayModels = XdDayModel.dao.find("select * from  xd_day_model where id like '" + yearMonth + "%' or days='"+lastMonLastDay+"'  order by id");

						StringBuffer sb=CheckAttendanceUtil.getHolidays(xdDayModels);

						XdDayModel lastModel = xdDayModels.get(0);




						Map<String, XdShift> nameShiftObjMap = CheckAttendanceUtil.shfitsMap();

						double cur_mon_hours=CheckAttendanceUtil.getWokrHours(year,month);//当月工时
						Map<String,String> settleMap=new HashMap<>();
						for(int i = 3;i<list.size();i++){
							Class superclass = XdAttendanceSummary.class.getSuperclass();
							String otFlags="";
							String modifyFlags="";
							String tips="";
							double othours=0;//加班时间
							int daysNum=xdDayModels.size()-1;
							int holidays=0;
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
							int commWorkdDay=0;
							boolean shift18A=false;
							List<String> summaryList = list.get(i);
							XdAttendanceSummary attendanceSummary=new XdAttendanceSummary();
							String summaryId = UuidUtil.getUUID();
							attendanceSummary.setId(summaryId);
							String department = summaryList.get(0);
							String deptId = orgMapping.get(department);
							attendanceSummary.setDeptName(department);
							attendanceSummary.setDeptValue(deptId);
							String unitName = summaryList.get(1);
							String unitValue = unitMap.get(unitName);
							attendanceSummary.setUnitName(unitName);
							attendanceSummary.setUnitValue(unitValue);
							String projectName = summaryList.get(2);
							String projectValue = projectsMap.get(projectName);
							attendanceSummary.setProjectName(projectName);
							attendanceSummary.setProjectValue(projectValue);
							String empNum = summaryList.get(3);
							attendanceSummary.setEmpNum(empNum);
							String empName = summaryList.get(4);
							attendanceSummary.setEmpName(empName);
							XdEmployee emp=XdEmployee.dao.findFirst("select * from  xd_employee where name='" + empName + "'");
							String workStation = summaryList.get(5);
							attendanceSummary.setWorkStation(workStation);
							attendanceSummary.setScheduleMonth(month);
							attendanceSummary.setScheduleYear(year);
							attendanceSummary.setScheduleYearMonth(yearMonth);

							attendanceSummary.setRemarks("");
							//attendanceSummary.setWeeks("");
							attendanceSummary.setSpecialDesc("");
							attendanceSummary.setPerformRewardpunish("");
							attendanceSummary.setHireDate(emp==null?"":emp.getEntrytime());
							attendanceSummary.setDimissionDate(emp==null?"":emp.getDepartime());


							modifyFlags=modifyFlags+","+"0";
							tips=tips+","+"0";
							otFlags=otFlags+","+"0";

							String lastMonLastValue = summaryList.get(6);
							attendanceSummary.setDay00(lastMonLastValue);

							XdShift xdShift = nameShiftObjMap.get(lastMonLastValue);
							if(xdShift!=null){
								if("1".equals(xdShift.getSpanDay())){//跨天
									if(xdShift.getBusitime()!=null && !"".equals(xdShift.getBusitime())){
										if(sb.indexOf(yearMonth + "01")!=-1){
											nationlOTHours+=nationlOTHours+Double.valueOf(xdShift.getSpanHours());
										}else{
											actWorkHours+=Double.valueOf(xdShift.getSpanHours());//实际工时
										}
									}

								}
							}

							String ymdInLine="";
							String ymdNoLine="";
							String methodSuffix="";
							for (int j = 1; j <=daysNum ; j++) {
								modifyFlags=modifyFlags+","+"0";
								tips=tips+","+"0";
								otFlags=otFlags+","+"0";

								if(j<10){
									ymdNoLine = yearMonth + '0' + j;
									ymdInLine=year+"-"+month+"-"+'0' + j;
									methodSuffix="0" + j;
								}else {
									ymdNoLine = yearMonth  + j;
									ymdInLine=year+"-"+month+"-"+j;
									methodSuffix=j+"";
								}


								String cellValue = summaryList.get(6 + j);

								XdAttendanceDetail attDetail =new XdAttendanceDetail();
								attDetail.setAttendidId(summaryId);

								if("18A".equals(cellValue)){
									shift18A=true;
								}
								Method method = superclass.getMethod("setDay" + methodSuffix,String.class);
								method.invoke(attendanceSummary,cellValue);
								List<XdOvertimeSummary> otSummaryList=null;

								XdSettleOvertimeSummary setOTSummary=new XdSettleOvertimeSummary();
								setOTSummary.setEmpNum(empNum);
								setOTSummary.setEmpName(empName);
								setOTSummary.setEmpIdnum(emp==null?"":emp.getIdnum());
								setOTSummary.setDeptId(deptId);
								setOTSummary.setDeptName(department);
								setOTSummary.setProjectId(projectValue);
								setOTSummary.setProjectName(projectName);
								setOTSummary.setCreateDate(DateUtil.getCurrentTime());
								setOTSummary.setCreateUser(ShiroKit.getUserId());
								setOTSummary.setSuperDays(ymdInLine);
								setOTSummary.setApplyType("0");
								setOTSummary.setSource("0");
								String nextDay  = dtf.format(LocalDate.parse(ymdInLine).plusDays(1));
								if(!"".equals(cellValue)){
									monthWorkDays++;//排班天数
									XdShift shift = nameShiftObjMap.get(cellValue);

									attDetail.setShiftName(shift==null?"":shift.getShiftname());
									attDetail.setWorkHours(shift==null?"":String.valueOf(shift.getHours()));



//									if (shift != null&& fileteShift.indexOf(cellValue)==-1 ) {
									if (shift != null&& shift.getBusitime()!=null && !"".equals(shift.getBusitime()) ) {
										String mn="";
										if(shift.getMiddleshift()!=null && !"".equals(shift.getMiddleshift())){
											mn=shift.getMiddleshift();
										}
										if(shift.getNigthshift()!=null && !"".equals(shift.getNigthshift())){
											mn=shift.getNigthshift();
										}
										attDetail.setMiddleNightShift(mn);

										if(shift.getDutyamount()!=null && !"".equals(shift.getDutyamount())){
											dutyCharge+=Double.valueOf(shift.getDutyamount());//值班费
										}



										//加班结算开始//调整开始============================================

										if(sb.indexOf(ymdNoLine)!=-1){
											//当天是法定节日
											holidays++;
//											otFlags=otFlags+","+"1";


											if("1".equals(shift.getSpanDay())){
												//法定节日且跨天
												nationlOTHours+= shift.getCurdayHours();

												settleMap.put(empName+","+ymdInLine+","+shift.getBusitime()+","+"24:00",shift.getCurdayHours()+","+ymdInLine);


												if(sb.indexOf(nextDay.replaceAll("-",""))!=-1){
													//跨天第二天是法定节日
													settleMap.put(empName+","+nextDay+","+"0:00"+","+shift.getUnbusitime(),shift.getSpanHours()+","+ymdInLine);
													if(j<daysNum){
														nationlOTHours+= shift.getSpanHours();
													}

												}else{//第二天不是法定节日
													if(j<daysNum) {
														actWorkHours +=  shift.getSpanHours();
													}
												}
											}else{
												//不跨天
												nationlOTHours+= Double.valueOf(shift.getHours());
												settleMap.put(empName+","+ymdInLine+","+shift.getBusitime()+","+shift.getUnbusitime(),shift.getHours()+","+ymdInLine);
											}

										}else{
											//当天不是法定节日
											commWorkdDay++;
//											otFlags=otFlags+","+"0";


											if("1".equals(shift.getSpanDay())){
												//跨天
												actWorkHours+= shift.getCurdayHours();

												//if(nextDayHolidays!=null && !"".equals(nextDayHolidays)){
												if(sb.indexOf(nextDay.replaceAll("-",""))!=-1){
													//第二天是法定节日


													settleMap.put(empName+","+nextDay+","+"0:00"+","+shift.getUnbusitime(),shift.getSpanHours()+","+ymdInLine);


													if(j<daysNum){
														nationlOTHours+=shift.getSpanHours();
													}

												}else{//第二天不是法定节日
													if(j<daysNum){
														actWorkHours+=shift.getSpanHours();
													}
												}

											}else{//不跨天
												actWorkHours+=Double.valueOf(shift.getHours());
											}
										}


										//加班结算结束//调整结束=============================
										if(shift.getMiddleshift()!=null&& !shift.getMiddleshift().equals("")){
											middleShiftDays++;//中班天数
										}
										if(shift.getNigthshift()!=null&& !shift.getNigthshift().equals("")){
											nightShiftDays++;//夜班天数
										}




									}else{
//										otFlags=otFlags+","+"0";
									}

									if(cellValue.equals("病")){
										sickLeaveDays++;
									}
									if(cellValue.equals("事")){
										personalLeaveDays++;
									}
									if(cellValue.equals("新离")){
										newLeaveDays++;
									}
									if(cellValue.equals("旷")){
										absenteeismDays++;
									}
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
//									otFlags=otFlags+","+"0";

								}
								attDetail.setScheduleYear(year);
								attDetail.setScheduleMonth(month);
								attDetail.setScheduleDay(String.valueOf(j));
								attDetail.setScheduleYmd(ymdInLine);
								attDetail.setWeekDay(xdDayModels.get(j).getWeeks());
								attDetail.setHolidays(xdDayModels.get(j).getHolidays());
								attDetail.setCreateDate(DateUtil.getCurrentTime());
								attDetail.setCreateUser(ShiroKit.getUserId());
								attDetail.save();
							}




							if(month.endsWith("7")||month.endsWith("8")||month.endsWith("9")){
							/*	int needWorkday = monthWorkDays - holidays;
								if(needWorkday<=commWorkdDay){
									highTempCharge=300;
								}else{
									highTempCharge=300*commWorkdDay/(double)needWorkday;
								}*/
								if(monthWorkDays<=commWorkdDay){
									highTempCharge=300;
								}else{
									highTempCharge=300*commWorkdDay/(double)monthWorkDays;
								}
							}

							attendanceSummary.setCurmonActworkhours(String.valueOf(actWorkHours));//本月实际工时
							//attendanceSummary.setCurmonActworkhours(String.valueOf(work_hour));//本月实际工时
							attendanceSummary.setCurmonWorkhours(String.valueOf(cur_mon_hours));//本月工时
//							attendanceSummary.setCurmonWorkhours(String.valueOf(actWorkHours));
							attendanceSummary.setCurmonBalancehours(String.valueOf(actWorkHours-cur_mon_hours));//本月工时结余

							List<XdAttendanceSummary> xdAttendanceSummaries = XdAttendanceSummary.dao.find("select * from xd_attendance_summary where emp_name='" + empName + "' and schedule_year='" + year + "' order by   schedule_year_month desc");
							if(xdAttendanceSummaries.size()<=1){
								attendanceSummary.setPremonAccbalancehours(String.valueOf(0.0));//上月累计工时结余
								attendanceSummary.setCurmonAccbalancehours(String.valueOf(actWorkHours-cur_mon_hours));//本月累计工时结余
							}else{
								double  accbalancehours=0;
								if(xdAttendanceSummaries.get(1).getCurmonAccbalancehours()!=null && !"".equals(xdAttendanceSummaries.get(1).getCurmonAccbalancehours())){
									accbalancehours+=Double.valueOf(xdAttendanceSummaries.get(1).getCurmonAccbalancehours());
								}
								attendanceSummary.setPremonAccbalancehours(String.valueOf(accbalancehours));//上月累计工时结余
								attendanceSummary.setCurmonAccbalancehours(String.valueOf(accbalancehours+actWorkHours-cur_mon_hours));//本月累计工时结余
							}
							//attendanceSummary.setCurmonAccbalancehours(String.valueOf(actWorkHours));//本月累计工时结余

							attendanceSummary.setCurmonSettlehours("");//当月待结算工时
							if(xdAttendanceSummaries.size()<=1){
								attendanceSummary.setAccSettlehours("");//累计待结算工时
							}else{
								XdAttendanceSummary summary = xdAttendanceSummaries.get(1);
								String accSettlehours = summary.getAccSettlehours();
								if(accSettlehours!=null && !"".equals(accSettlehours)){
									attendanceSummary.setAccSettlehours(accSettlehours);//累计待结算工时
								}else{
									attendanceSummary.setAccSettlehours("");//累计待结算工时
								}
							}

							attendanceSummary.setOthours(othours);
							attendanceSummary.setNatOthours(nationlOTHours);
							attendanceSummary.setFlags(modifyFlags.replaceAll("^,",""));
							attendanceSummary.setOtflags(otFlags.replaceAll("^,",""));
							attendanceSummary.setTips(tips.replaceAll("^,",""));
							attendanceSummary.save();

							XdAnleaveSummary.dao.findFirst("select  * from  xd_anleave_summary where  emp_name='' and year=''");

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
								rcp.setCreateDate(DateUtil.getCurrentTime());
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
//								days.setMonshouldWorkdays(String.valueOf(monthWorkDays- holidays));
								days.setMonshouldWorkdays(String.valueOf(monthWorkDays));
								days.setSickeleaveDays(String.valueOf(sickLeaveDays));
								days.setCasualleaveDays(String.valueOf(personalLeaveDays));
								days.setAbsencedutyDays(String.valueOf(newLeaveDays));
								days.setAbsentworkDays(String.valueOf(absenteeismDays));
								days.setRestanleaveDays(String.valueOf(alreayAnnualLeave));
								days.setCreateDate(DateUtil.getCurrentTime());
								days.setCreateUser(ShiroKit.getUserId());
								days.save();
								days.setId(null);
								days.save();
								days.setId(null);
								days.save();

								/*XdAnleaveSummary.dao.findFirst("select * from  xd_anleave_summary where year='' and emp_name=''")*/

							XdAnleaveSummary leave=	XdAnleaveSummary.dao.findFirst("select  * from  xd_anleave_summary where  emp_name='"+empName+"' and year='"+year+"'");
							if(leave!=null){
								leave.setSurplusDays(String.valueOf(Double.valueOf(leave.getSurplusDays())-alreayAnnualLeave));

								Class clazz=XdAnleaveSummary.class.getSuperclass();
								Method getMethod = clazz.getMethod("getMonth" + Integer.parseInt(month));
								double leaveDays= (double)getMethod.invoke(leave);
								Method setMethod =clazz.getMethod("setMonth"+Integer.parseInt(month),Double.class);
								setMethod.invoke(leave,leaveDays+alreayAnnualLeave);
								leave.setSum(leave.getSum()+alreayAnnualLeave);
								leave.update();
							}

							if(deptId!=null && !"".equals(deptId)){
								XdAnleaveExecute	execute	 = XdAnleaveExecute.dao.findFirst("select * from  xd_anleave_execute where year='"+year+"' and dept_id='"+deptId+"'");
								Class  executeClazz= XdAnleaveExecute.class.getSuperclass();
								if(execute!=null){
									Method getMethod =executeClazz.getMethod("getMonth"+ Integer.parseInt(month));
									double leaveDays= (double) getMethod.invoke(execute);
									Method setMethod =executeClazz.getMethod("setMonth"+Integer.parseInt(month),Double.class);
									setMethod.invoke(execute,leaveDays+alreayAnnualLeave);

									execute.setSum(execute.getSum()+alreayAnnualLeave);
									execute.setSurplus(execute.getAnleaveDays()-execute.getSum());
									DecimalFormat decimalFormat=new DecimalFormat(".00");
									execute.setExecutionRate(Double.valueOf(decimalFormat.format((execute.getSum()+alreayAnnualLeave)*100/execute.getAnleaveDays())));
									execute.update();
								}

							}



						}


						List<XdAnleaveExecute> executesList = XdAnleaveExecute.dao.find("select * from  xd_anleave_execute where year='" + year + "'");
						Class exeClazz= XdAnleaveExecute.class.getSuperclass();
						double monthAnual=0;
						double sumExecute=0;
						double surplusExecute=0;
						Method getMethod=exeClazz.getMethod("getMonth"+ Integer.parseInt(month));
						Method setMethod =exeClazz.getMethod("setMonth"+Integer.parseInt(month),Double.class);


						XdAnleaveExecute totleExecute =null;

						for (XdAnleaveExecute execute : executesList) {
							if(execute.getDeptName().equals("合计")){
								totleExecute=execute;
							}else{
								monthAnual+= (double) getMethod.invoke(execute);
								sumExecute+=execute.getSum();
								surplusExecute+=execute.getSurplus();
							}

						}
						setMethod.invoke(totleExecute,monthAnual);
						totleExecute.setSum(sumExecute);
						totleExecute.setSurplus(surplusExecute);
						DecimalFormat decimalFormat=new DecimalFormat(".00");
						totleExecute.setExecutionRate(Double.valueOf(decimalFormat.format(sumExecute*100/totleExecute.getAnleaveDays())));
						totleExecute.update();








				/*		for (String s : set) {
							System.out.println("============"+s+settleMap.get(s).toString());
						}*/
						List<XdOvertimeSummary> otSummaryList = CheckAttendanceUtil.getOtSummaryList(year + "-" + month, "0");

						for (XdOvertimeSummary os : otSummaryList) {
							String key= os.getEmpName()+","+os.getApplyDate()
									+","+os.getApplyStart()+","+os.getApplyEnd();
							if(settleMap.get(key)==null){
								XdSettleOvertimeSummary sos=new XdSettleOvertimeSummary();
								BeanUtils.copyProperties(sos,os);
							/*	sos.setEmpName(os.getEmpName());
								sos.setEmpNum(os.getEmpNum());
								sos.setEmpIdnum(os.getEmpIdnum());
								sos.setDeptId(os.getDeptId());
								sos.setDeptName(os.getDeptName());
								sos.setProjectId(os.getProjectId());
								sos.setProjectName(os.getProjectName());*/
								//sos.set
								sos.setId(null);
								sos.setCreateDate(DateUtil.getCurrentTime());
								sos.setCreateUser(ShiroKit.getUserId());
								sos.save();

							}else{
								XdSettleOvertimeSummary sos=new XdSettleOvertimeSummary();
								BeanUtils.copyProperties(sos,os);
								sos.setId(null);
								sos.setCreateUser(ShiroKit.getUserId());
								sos.setCreateDate(DateUtil.getCurrentTime());
								sos.setActHours(Double.valueOf(os.getApplyHours()));
								sos.setActStart(os.getApplyStart());
								sos.setActEnd(os.getApplyEnd());
								sos.setSuperDays(settleMap.get(key).split(",")[1]);
								sos.save();
								settleMap.remove(key);
							}

						}

						Set<String> set = settleMap.keySet();
						for (String settle : set) {
							String[] keyArr=settle.split(",");
							String value = settleMap.get(settle);
							String[] valueArr = value.split(",");
							XdSettleOvertimeSummary sos=new XdSettleOvertimeSummary();
							sos.setEmpName(keyArr[0]);
							sos.setApplyDate(keyArr[1]);
							sos.setActStart(keyArr[2]);
							sos.setActEnd(keyArr[3]);
							sos.setActHours(Double.valueOf(valueArr[0]));
							sos.setApplyType("0");
							sos.setSuperDays(valueArr[1]);
							sos.setCreateDate(DateUtil.getCurrentTime());
							sos.setCreateUser(ShiroKit.getUserId());
							sos.save();
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


	public Map<String,Object> importExcel20230228Bak(List<List<String>> list) throws SQLException {

		final Map<String,Object> result = new HashMap<String,Object>();
		final List<String> message = new ArrayList<String>();
		Map<String, String> orgMapping = DictMapping.orgMapping("1");
		Map<String, Map<String, String>> stringMapMap = DictMapping.dictMapping();
		Map<String, String> unitMap = stringMapMap.get("unit");
		Map<String, String> projectsMap = DictMapping.projectsMapping();

		Db.tx(new IAtom() {
			@Override
			public boolean run() throws SQLException {
				try{
					if(list.size()>3){
						List<String> title = list.get(0);
						String[] ny = title.get(0).replaceAll("年", "_").replaceAll("[^(0-9_)]", "").split("_");
						String year=ny[0];
						String month=ny[1];
						month=month.length()==1?("0"+month):month;
						/*if(month.length()==1){
							month="0"+month;
						}*/


						String yearMonth=year+month;

						DateTimeFormatter dtf=DateTimeFormatter.ofPattern("yyyy-MM-dd");
//						LocalDate lastmonlastday = LocalDate.parse(year+"-"+month+"-01").minusDays(1);

						String lastMonLastDay = dtf.format(LocalDate.parse(year+"-"+month+"-01").minusDays(1));

						String[] split = lastMonLastDay.split("-");
						String lastYear=split[0];
						String lastMonth=split[1];
						String lastDay=split[2];


						List<XdDayModel> xdDayModels = XdDayModel.dao.find("select * from  xd_day_model where id like '" + yearMonth + "%' or days='"+lastMonLastDay+"'  order by id");
//						Map<String, String> holidaysMap = xdDayModels.stream().collect(Collectors.toMap(XdDayModel::getId, XdDayModel::getHolidays));

						StringBuffer sb=CheckAttendanceUtil.getHolidays(xdDayModels);

						//if(xdDayModels.get(0).getId().startsWith(yearMonth)){
						XdDayModel lastModel = xdDayModels.get(0);




						/*List<XdShift> xdShifts = XdShift.dao.find("select * from  xd_shift");
						Map<String, XdShift> nameShiftObjMap = xdShifts.stream().collect(Collectors.toMap(XdShift::getShiftname, xdShift -> xdShift));
*/
						Map<String, XdShift> nameShiftObjMap = CheckAttendanceUtil.shfitsMap();

						//DateTimeFormatter dtfNoLine=DateTimeFormatter.ofPattern("yyyyMMdd");

						//XdWorkHour workHours = XdWorkHour.dao.findFirst("select * from  xd_work_hour where year='" + year + "' and  month='" + month + "'");
						double cur_mon_hours=CheckAttendanceUtil.getWokrHours(year,month);//当月工时
						Map<String,String> settleMap=new HashMap<>();
						for(int i = 3;i<list.size();i++){
							Class superclass = XdAttendanceSummary.class.getSuperclass();
							String otFlags="";
							String modifyFlags="";
							String tips="";
							//double natOtHours=0;
							double othours=0;//加班时间
							//double work_hour=0;//出勤时间
							int daysNum=xdDayModels.size()-1;
							int holidays=0;
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
							int commWorkdDay=0;
							boolean shift18A=false;
							List<String> summaryList = list.get(i);
							XdAttendanceSummary attendanceSummary=new XdAttendanceSummary();
							String summaryId = UuidUtil.getUUID();
							attendanceSummary.setId(summaryId);
							String department = summaryList.get(0);
							String deptId = orgMapping.get(department);
							attendanceSummary.setDeptName(department);
							attendanceSummary.setDeptValue(deptId);
							String unitName = summaryList.get(1);
							String unitValue = unitMap.get(unitName);
							attendanceSummary.setUnitName(unitName);
							attendanceSummary.setUnitValue(unitValue);
							String projectName = summaryList.get(2);
							String projectValue = projectsMap.get(projectName);
							attendanceSummary.setProjectName(projectName);
							attendanceSummary.setProjectValue(projectValue);
							String empNum = summaryList.get(3);
							attendanceSummary.setEmpNum(empNum);
							String empName = summaryList.get(4);
							attendanceSummary.setEmpName(empName);
							XdEmployee emp=XdEmployee.dao.findFirst("select * from  xd_employee where name='" + empName + "'");
							String workStation = summaryList.get(5);
							attendanceSummary.setWorkStation(workStation);
							attendanceSummary.setScheduleMonth(month);
							attendanceSummary.setScheduleYear(year);
							attendanceSummary.setScheduleYearMonth(yearMonth);

							attendanceSummary.setRemarks("");
							//attendanceSummary.setWeeks("");
							attendanceSummary.setSpecialDesc("");
							attendanceSummary.setPerformRewardpunish("");
							attendanceSummary.setHireDate(emp==null?"":emp.getEntrytime());
							attendanceSummary.setDimissionDate(emp==null?"":emp.getDepartime());


							modifyFlags=modifyFlags+","+"0";
							tips=tips+","+"0";



							if(lastModel!=null && "1".equals(lastModel.getIsnatHoliday())){
								otFlags=otFlags+","+"1";
							}else{
								otFlags=otFlags+","+"0";
							}

							String lastMonLastValue = summaryList.get(6);
							attendanceSummary.setDay00(lastMonLastValue);

							XdShift xdShift = nameShiftObjMap.get(lastMonLastValue);
							if(xdShift!=null){
								if("1".equals(xdShift.getSpanDay())){//跨天
									if(xdShift.getBusitime()!=null && !"".equals(xdShift.getBusitime())){
										if(sb.indexOf(yearMonth + "01")!=-1){
											nationlOTHours+=nationlOTHours+Double.valueOf(xdShift.getSpanHours());
										}else{
											//work_hour+=Double.valueOf(xdShift.getSpanHours());//出勤时间
											actWorkHours+=Double.valueOf(xdShift.getSpanHours());//实际工时
										}
									}

								}
							}

//							处理上个月的最后一天day00 start
							/*XdAttendanceSummary lastMonSummary = XdAttendanceSummary.dao.findFirst("select * from xd_attendance_summary " +
									"where emp_name='" + empName + "' " +
									"and  schedule_year='" + lastYear + "'" +
									"and schedule_month='" + lastMonth + "'");



							if(lastMonSummary==null){
								attendanceSummary.setDay00("");
							}else{
								String  lastMonLastShift = (String)superclass.getMethod("getDay" + lastDay).invoke(lastMonSummary);
								attendanceSummary.setDay00(lastMonLastShift);

								XdShift xdShift = nameShiftObjMap.get(lastMonLastShift);
								if(xdShift!=null){
									if("1".equals(xdShift.getSpanDay())){//跨天
										if(sb.indexOf(yearMonth + "01")!=-1){
											nationlOTHours+=nationlOTHours+Double.valueOf(xdShift.getSpanHours());
										}else{
											//work_hour+=Double.valueOf(xdShift.getSpanHours());//出勤时间
											actWorkHours+=Double.valueOf(xdShift.getSpanHours());//实际工时
										}
									}
								}


							}*/
//							处理上个月的最后一天day00 end








							String ymdInLine="";
							String ymdNoLine="";
							String methodSuffix="";
							for (int j = 1; j <=daysNum ; j++) {
								modifyFlags=modifyFlags+","+"0";
								tips=tips+","+"0";
								if(j<10){
									ymdNoLine = yearMonth + '0' + j;
									ymdInLine=year+"-"+month+"-"+'0' + j;
									methodSuffix="0" + j;
								}else {
									ymdNoLine = yearMonth  + j;
									ymdInLine=year+"-"+month+"-"+j;
									methodSuffix=j+"";
								}


								String cellValue = summaryList.get(6 + j);

								XdAttendanceDetail attDetail =new XdAttendanceDetail();
								attDetail.setAttendidId(summaryId);

								if("18A".equals(cellValue)){
									shift18A=true;
								}
								Method method = superclass.getMethod("setDay" + methodSuffix,String.class);
								method.invoke(attendanceSummary,cellValue);
								List<XdOvertimeSummary> otSummaryList=null;

								XdSettleOvertimeSummary setOTSummary=new XdSettleOvertimeSummary();
								setOTSummary.setEmpNum(empNum);
								setOTSummary.setEmpName(empName);
								setOTSummary.setEmpIdnum(emp==null?"":emp.getIdnum());
								setOTSummary.setDeptId(deptId);
								setOTSummary.setDeptName(department);
								setOTSummary.setProjectId(projectValue);
								setOTSummary.setProjectName(projectName);
								setOTSummary.setCreateDate(DateUtil.getCurrentTime());
								setOTSummary.setCreateUser(ShiroKit.getUserId());
								setOTSummary.setSuperDays(ymdInLine);
								setOTSummary.setApplyType("0");
								setOTSummary.setSource("0");
								String nextDay  = dtf.format(LocalDate.parse(ymdInLine).plusDays(1));
								if(!"".equals(cellValue)){
									monthWorkDays++;//排班天数
									XdShift shift = nameShiftObjMap.get(cellValue);

									attDetail.setShiftName(shift==null?"":shift.getShiftname());
									attDetail.setWorkHours(shift==null?"":String.valueOf(shift.getHours()));



//									if (shift != null&& fileteShift.indexOf(cellValue)==-1 ) {
									if (shift != null&& shift.getBusitime()!=null && !"".equals(shift.getBusitime()) ) {
										String mn="";
										if(shift.getMiddleshift()!=null && !"".equals(shift.getMiddleshift())){
											mn=shift.getMiddleshift();
										}
										if(shift.getNigthshift()!=null && !"".equals(shift.getNigthshift())){
											mn=shift.getNigthshift();
										}
										attDetail.setMiddleNightShift(mn);

										if(shift.getDutyamount()!=null && !"".equals(shift.getDutyamount())){
											dutyCharge+=Double.valueOf(shift.getDutyamount());//值班费
										}



										//加班结算开始//调整开始============================================

										if(sb.indexOf(ymdNoLine)!=-1){
											//当天是法定节日
											holidays++;
											otFlags=otFlags+","+"1";

											// otSummaryList = CheckAttendanceUtil.getOtSummaryList(empName, ymdInLine, "0");

											if("1".equals(shift.getSpanDay())){
												//法定节日且跨天
												nationlOTHours+= shift.getCurdayHours();

												settleMap.put(empName+","+ymdInLine+","+shift.getBusitime()+","+"24:00",shift.getCurdayHours()+","+ymdInLine);

//												dealSettleOvertime(ymdInLine,setOTSummary, otSummaryList,shift.getBusitime(),"24:00",shift.getCurdayHours());

												if(sb.indexOf(nextDay.replaceAll("-",""))!=-1){
													//跨天第二天是法定节日
													//otSummaryList = CheckAttendanceUtil.getOtSummaryList(empName, nextDay, "0");
													//setOTSummary.setApplyDate(nextDay);

													//dealSettleOvertime(nextDay,setOTSummary, otSummaryList,"0:00",shift.getUnbusitime(),shift.getSpanHours());

													settleMap.put(empName+","+nextDay+","+"0:00"+","+shift.getUnbusitime(),shift.getSpanHours()+","+ymdInLine);

													/*for (XdOvertimeSummary ots : otSummaryList) {
														setOTSummary.setId(null);
														setOTSummary.setApplyStart(ots.getApplyStart());
														setOTSummary.setApplyEnd(ots.getApplyEnd());
														setOTSummary.setApplyHours(Double.valueOf(ots.getApplyHours()));
														if ("0:00".equals(ots.getApplyStart()) && ots.getApplyEnd().equals(shift.getUnbusitime())) {
															setOTSummary.setActStart("0:00");
															setOTSummary.setActEnd(shift.getUnbusitime());
															setOTSummary.setActHours(shift.getSpanHours());
														}
														setOTSummary.setCreateDate(DateUtil.getCurrentTime());
														setOTSummary.setCreateUser(ShiroKit.getUserId());
														setOTSummary.save();
													}*/

													if(j<daysNum){
														nationlOTHours+= shift.getSpanHours();
													}

												}else{//第二天不是法定节日
													if(j<daysNum) {
														actWorkHours +=  shift.getSpanHours();
													}
												}
											}else{
												//不跨天
												nationlOTHours+= Double.valueOf(shift.getHours());


												settleMap.put(empName+","+ymdInLine+","+shift.getBusitime()+","+shift.getUnbusitime(),shift.getHours()+","+ymdInLine);

												//dealSettleOvertime(ymdInLine,setOTSummary,otSummaryList,shift.getBusitime(),shift.getUnbusitime(),Double.valueOf(shift.getHours()));

											/*	setOTSummary.setId(null);
												setOTSummary.setActStart(shift.getBusitime());
												setOTSummary.setActEnd(shift.getUnbusitime());
												setOTSummary.setActHours(Double.valueOf(shift.getHours()));
												setOTSummary.save();*/
											}

										}else{
											//当天不是法定节日
											commWorkdDay++;
											otFlags=otFlags+","+"0";


											if("1".equals(shift.getSpanDay())){
												//跨天
												actWorkHours+= shift.getCurdayHours();

												//if(nextDayHolidays!=null && !"".equals(nextDayHolidays)){
												if(sb.indexOf(nextDay.replaceAll("-",""))!=-1){
													//第二天是法定节日
													/*XdOvertimeSummary	ots = XdOvertimeSummary.dao.findFirst(
															"select * from  xd_overtime_summary where apply_date='" + nextDay +
																	"' and emp_name='" + empName +
																	"' and apply_type='1'");*/
												/*	XdOvertimeSummary ots =
															CheckAttendanceUtil.getOverTimeSummary(empName,nextDay,"0");*/
/*
													XdSettleOvertimeSummary sots=new XdSettleOvertimeSummary();
													sots.setEmpNum(empNum);
													sots.setEmpName(empName);
													sots.setEmpIdnum(emp==null?"":emp.getIdnum());
													sots.setDeptId(deptId);
													sots.setDeptName(department);
													sots.setProjectId(projectValue);
													sots.setProjectName(projectName);*/
													//otSummaryList = CheckAttendanceUtil.getOtSummaryList(empName, nextDay, "0");

													//dealSettleOvertime(nextDay,setOTSummary,otSummaryList,"0:00",shift.getUnbusitime(),shift.getSpanHours());

													settleMap.put(empName+","+nextDay+","+"0:00"+","+shift.getUnbusitime(),shift.getSpanHours()+","+ymdInLine);




													/*setOTSummary.setApplyDate(nextDay);
													setOTSummary.setApplyStart(ots==null?"":ots.getApplyStart());
													setOTSummary.setApplyEnd(ots==null?"":ots.getApplyEnd());
													setOTSummary.setApplyHours(ots==null?0:Double.valueOf(ots.getApplyHours()));
													setOTSummary.setActHours(shift.getSpanHours());
													setOTSummary.setActStart("00:00");
													setOTSummary.setActEnd(shift.getUnbusitime());
													setOTSummary.save();*/


													if(j<daysNum){
														nationlOTHours+=shift.getSpanHours();
													}

												}else{//第二天不是法定节日
													if(j<daysNum){
														actWorkHours+=shift.getSpanHours();
													}
												}

											}else{//不跨天
												actWorkHours+=Double.valueOf(shift.getHours());
											}
										}


										//加班结算结束//调整结束=============================
										if(shift.getMiddleshift()!=null&& !shift.getMiddleshift().equals("")){
											middleShiftDays++;//中班天数
										}
										if(shift.getNigthshift()!=null&& !shift.getNigthshift().equals("")){
											nightShiftDays++;//夜班天数
										}




									}else{
										otFlags=otFlags+","+"0";
									}

									if(cellValue.equals("病")){
										sickLeaveDays++;
									}
									if(cellValue.equals("事")){
										personalLeaveDays++;
									}
									if(cellValue.equals("新离")){
										newLeaveDays++;
									}
									if(cellValue.equals("旷")){
										absenteeismDays++;
									}
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
									otFlags=otFlags+","+"0";

								/*	if(sb.indexOf(ymdNoLine)!=-1){
										otSummaryList = CheckAttendanceUtil.getOtSummaryList(empName, ymdInLine, "0");
										dealSettleOvertime(ymdInLine,setOTSummary,otSummaryList," ","",0.0);
									}

									if(sb.indexOf(nextDay.replaceAll("-",""))!=-1){
										otSummaryList = CheckAttendanceUtil.getOtSummaryList(empName, nextDay, "0");
										dealSettleOvertime(nextDay,setOTSummary,otSummaryList," ","",0.0);
									}*/

//									otSummaryList = CheckAttendanceUtil.getOtSummaryList(empName, ymdInLine, "0");
//									dealSettleOvertime(ymdInLine,setOTSummary,otSummaryList,"0:00",shift.getUnbusitime(),shift.getSpanHours());

									/*otSummaryList = CheckAttendanceUtil.getOtSummaryList(empName, ymdInLine, "0");

									dealSettleOvertime(nextDay,setOTSummary, otSummaryList,"0:00",shift.getUnbusitime(),shift.getSpanHours());
*/
								}
								attDetail.setScheduleYear(year);
								attDetail.setScheduleMonth(month);
								attDetail.setScheduleDay(String.valueOf(j));
								attDetail.setScheduleYmd(ymdInLine);
								attDetail.setWeekDay(xdDayModels.get(j).getWeeks());
								attDetail.setHolidays(xdDayModels.get(j).getHolidays());
								attDetail.setCreateDate(DateUtil.getCurrentTime());
								attDetail.setCreateUser(ShiroKit.getUserId());
								attDetail.save();
							}




							if(month.endsWith("7")||month.endsWith("8")||month.endsWith("9")){
							/*	int needWorkday = monthWorkDays - holidays;
								if(needWorkday<=commWorkdDay){
									highTempCharge=300;
								}else{
									highTempCharge=300*commWorkdDay/(double)needWorkday;
								}*/
								if(monthWorkDays<=commWorkdDay){
									highTempCharge=300;
								}else{
									highTempCharge=300*commWorkdDay/(double)monthWorkDays;
								}
							}

							attendanceSummary.setCurmonActworkhours(String.valueOf(actWorkHours));//本月实际工时
							//attendanceSummary.setCurmonActworkhours(String.valueOf(work_hour));//本月实际工时
							attendanceSummary.setCurmonWorkhours(String.valueOf(cur_mon_hours));//本月工时
//							attendanceSummary.setCurmonWorkhours(String.valueOf(actWorkHours));
							attendanceSummary.setCurmonBalancehours(String.valueOf(actWorkHours-cur_mon_hours));//本月工时结余

							List<XdAttendanceSummary> xdAttendanceSummaries = XdAttendanceSummary.dao.find("select * from xd_attendance_summary where emp_name='" + empName + "' and schedule_year='" + year + "' order by   schedule_year_month desc");
							if(xdAttendanceSummaries.size()<=1){
								attendanceSummary.setPremonAccbalancehours(String.valueOf(0.0));//上月累计工时结余
								attendanceSummary.setCurmonAccbalancehours(String.valueOf(actWorkHours-cur_mon_hours));//本月累计工时结余
							}else{
								double  accbalancehours=0;
								if(xdAttendanceSummaries.get(1).getCurmonAccbalancehours()!=null && !"".equals(xdAttendanceSummaries.get(1).getCurmonAccbalancehours())){
									accbalancehours+=Double.valueOf(xdAttendanceSummaries.get(1).getCurmonAccbalancehours());
								}
								attendanceSummary.setPremonAccbalancehours(String.valueOf(accbalancehours));//上月累计工时结余
								attendanceSummary.setCurmonAccbalancehours(String.valueOf(accbalancehours+actWorkHours-cur_mon_hours));//本月累计工时结余
							}
							//attendanceSummary.setCurmonAccbalancehours(String.valueOf(actWorkHours));//本月累计工时结余

							attendanceSummary.setCurmonSettlehours("");//当月待结算工时
							if(xdAttendanceSummaries.size()<=1){
								attendanceSummary.setAccSettlehours("");//累计待结算工时
							}else{
								XdAttendanceSummary summary = xdAttendanceSummaries.get(1);
								String accSettlehours = summary.getAccSettlehours();
								if(accSettlehours!=null && !"".equals(accSettlehours)){
									attendanceSummary.setAccSettlehours(accSettlehours);//累计待结算工时
								}else{
									attendanceSummary.setAccSettlehours("");//累计待结算工时
								}
							}

							attendanceSummary.setOthours(othours);
							attendanceSummary.setNatOthours(nationlOTHours);
							attendanceSummary.setFlags(modifyFlags.replaceAll("^,",""));
							attendanceSummary.setOtflags(otFlags.replaceAll("^,",""));
							attendanceSummary.setTips(tips.replaceAll("^,",""));
							attendanceSummary.save();

							XdAnleaveSummary.dao.findFirst("select  * from  xd_anleave_summary where  emp_name='' and year=''");

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
								rcp.setCreateDate(DateUtil.getCurrentTime());
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
//								days.setMonshouldWorkdays(String.valueOf(monthWorkDays- holidays));
							days.setMonshouldWorkdays(String.valueOf(monthWorkDays));
							days.setSickeleaveDays(String.valueOf(sickLeaveDays));
							days.setCasualleaveDays(String.valueOf(personalLeaveDays));
							days.setAbsencedutyDays(String.valueOf(newLeaveDays));
							days.setAbsentworkDays(String.valueOf(absenteeismDays));
							days.setRestanleaveDays(String.valueOf(alreayAnnualLeave));
							days.setCreateDate(DateUtil.getCurrentTime());
							days.setCreateUser(ShiroKit.getUserId());
							days.save();
							days.setId(null);
							days.save();
							days.setId(null);
							days.save();

							XdAnleaveSummary leave=	XdAnleaveSummary.dao.findFirst("select  * from  xd_anleave_summary where  emp_name='' and year=''");
							if(leave!=null){
								leave.setSurplusDays(String.valueOf(Double.valueOf(leave.getSurplusDays())-alreayAnnualLeave));
								leave.update();
							}
						}



				/*		for (String s : set) {
							System.out.println("============"+s+settleMap.get(s).toString());
						}*/
						List<XdOvertimeSummary> otSummaryList = CheckAttendanceUtil.getOtSummaryList(year + "-" + month, "0");

						for (XdOvertimeSummary os : otSummaryList) {
							String key= os.getEmpName()+","+os.getApplyDate()
									+","+os.getApplyStart()+","+os.getApplyEnd();
							if(settleMap.get(key)==null){
								XdSettleOvertimeSummary sos=new XdSettleOvertimeSummary();
								BeanUtils.copyProperties(sos,os);
							/*	sos.setEmpName(os.getEmpName());
								sos.setEmpNum(os.getEmpNum());
								sos.setEmpIdnum(os.getEmpIdnum());
								sos.setDeptId(os.getDeptId());
								sos.setDeptName(os.getDeptName());
								sos.setProjectId(os.getProjectId());
								sos.setProjectName(os.getProjectName());*/
								//sos.set
								sos.setId(null);
								sos.setCreateDate(DateUtil.getCurrentTime());
								sos.setCreateUser(ShiroKit.getUserId());
								sos.save();

							}else{
								XdSettleOvertimeSummary sos=new XdSettleOvertimeSummary();
								BeanUtils.copyProperties(sos,os);
								sos.setId(null);
								sos.setCreateUser(ShiroKit.getUserId());
								sos.setCreateDate(DateUtil.getCurrentTime());
								sos.setActHours(Double.valueOf(os.getApplyHours()));
								sos.setActStart(os.getApplyStart());
								sos.setActEnd(os.getApplyEnd());
								sos.setSuperDays(settleMap.get(key).split(",")[1]);
								sos.save();
								settleMap.remove(key);
							}

						}

						Set<String> set = settleMap.keySet();
						for (String settle : set) {
							String[] keyArr=settle.split(",");
							String value = settleMap.get(settle);
							String[] valueArr = value.split(",");
							XdSettleOvertimeSummary sos=new XdSettleOvertimeSummary();
							sos.setEmpName(keyArr[0]);
							sos.setApplyDate(keyArr[1]);
							sos.setActStart(keyArr[2]);
							sos.setActEnd(keyArr[3]);
							sos.setActHours(Double.valueOf(valueArr[0]));
							sos.setApplyType("0");
							sos.setSuperDays(valueArr[1]);
							sos.setCreateDate(DateUtil.getCurrentTime());
							sos.setCreateUser(ShiroKit.getUserId());
							sos.save();
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
	 * @Method dealSettleOvertime
	 * @param setOTSummary:
	 * @param otSummaryList:
	 * @param startTime:
	 * @param endTime:
	 * @Date 2023/2/22 13:44
	 * @Description 处理加班结算
	 * @Author king
	 * @Version  1.0
	 * @Return void
	 */
	/*private void dealSettleOvertime(String applyDate,XdSettleOvertimeSummary setOTSummary, List<XdOvertimeSummary> otSummaryList,String startTime,String endTime,double hours) {

		boolean hasOt=true;
		setOTSummary.setApplyDate(applyDate);
		for (XdOvertimeSummary ots : otSummaryList) {
			setOTSummary.setId(null);
			setOTSummary.setApplyStart(ots.getApplyStart());
			setOTSummary.setApplyEnd(ots.getApplyEnd());
			setOTSummary.setApplyHours(Double.valueOf(ots.getApplyHours()));
			if(startTime.equals(ots.getApplyStart()) && endTime.equals(ots.getApplyEnd())){
				hasOt=false;
				setOTSummary.setActStart(startTime);
				setOTSummary.setActEnd(endTime);
				setOTSummary.setActHours(hours);
			}
			setOTSummary.save();
		}

		if(hasOt){
			setOTSummary.setId(null);
			setOTSummary.setApplyStart("");
			setOTSummary.setApplyEnd("");
			setOTSummary.setApplyHours(0.0);
			setOTSummary.setActStart(startTime);
			setOTSummary.setActEnd(endTime);
			setOTSummary.setActHours(hours);
			setOTSummary.save();
		}
	}
*/

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
	/*public Map<String,Object> importExcelV2(List<List<String>> list) throws SQLException {
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
												//DateTimeFormatter dtf=DateTimeFormatter.ofPattern("yyyyMMdd");
												String nextDate  = DateTimeFormatter.ofPattern("yyyyMMdd").format(localDate);
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
													*//*if(shfit.getHours()!=null && !shfit.getHours().equals("")){
														nationlOTHours=nationlOTHours+ Double.valueOf(shfit.getHours());
													}*//*
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
											String nextDate  = DateTimeFormatter.ofPattern("yyyyMMdd").format(localDate);
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
									*//*if(cellValue.equals("年")){
										alreayAnnualLeave++;
									}*//*
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



							*//*XdAttendanceRcp rcp =new XdAttendanceRcp();
							rcp.setAttendidId(summaryId);
							rcp.setMidnightDays(summaryList.get(58 + 3 * daysNum));
							rcp.setMonworkDays(summaryList.get(59 + 3 * daysNum));
							rcp.setAllowanceAmount(summaryList.get(60 + 3 * daysNum));
							rcp.setShiftName(summaryList.get(61 + 3 * daysNum));
							rcp.setRemarks(summaryList.get(62 + 3 * daysNum));
							rcp.setCreateDate(DateUtil.getCurrentTime());
							rcp.setCreateUser(ShiroKit.getUserId());
							rcp.save();*//*

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
	}*/
	/**
	 * @Method importExcelV1
	 * @Date 2023/2/6 9:59
	 * @Exception
	 * @Description  出勤核算V1
	 * @Author king
	 * @Version  1.0
	 * @Return java.util.Map<java.lang.String,java.lang.Object>
	 */
	/*public Map<String,Object> importExcelV1(List<List<String>> list) throws SQLException {
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
	}*/



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

		sql = sql + " order by o.dept_value ,emp_num";


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
		titleThirdRow.add(org==null?"全部门":org.getName());
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
		XdEmployee emp=null;
		XdSeniorityAllowance allowance=null;
		for(int j = 0; j < list.size(); j++){
			List<String> row = new ArrayList<String>();
			XdAttendanceSummary summary = list.get(j);
			row.add(String.valueOf(j+1));
			row.add(summary.getDeptName());
			row.add(summary.getUnitName());
			row.add(summary.getProjectName());
			row.add(summary.getEmpNum());
			row.add(summary.getEmpName());
			emp=XdEmployee.dao.findFirst("select * from xd_employee where name ='"+summary.getEmpName()+"'");
			allowance=XdSeniorityAllowance.dao.findFirst("select * from  xd_seniority_allowance where year='"+year+"' and emp_name='"+summary.getEmpName()+"'");
			row.add(emp==null?"":emp.getIdnum());//职员代码
			XdAttendanceDays days = XdAttendanceDays.dao.findFirst("select * from  xd_attendance_days where attendid_id='" + summary.getId() + "'");
			row.add(days.getOrdinaryOvertime());
			row.add("");//双休日加班
			row.add(days.getNationalOvertime());
			row.add(days.getDutyCharge());
			row.add(days.getMidshiftDays());
			row.add(days.getNightshiftDays());
			row.add(days.getHightempAllowance());
			row.add(allowance==null?"":allowance.getSeniorityAllowance()+"");//工龄津贴
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


	public File exportOnDuty(String path, String dept,String year, String month){
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
		List<String> titleFourRow=new ArrayList<String>();
		titleFirstRow.add("上海东方欣迪商务服务有限公司");
		titleSecondRow.add("值班汇总表");
		titleFourRow.add("值班汇总明细");
		for (int i = 0; i <6 ; i++) {
			titleFirstRow.add("");
			titleSecondRow.add("");
			titleFourRow.add("");
		}
		SysOrg org = SysOrg.dao.findById(dept);
		titleThirdRow.add("部门");
		titleThirdRow.add(departName);
		titleThirdRow.add("");
		titleThirdRow.add("");
		titleThirdRow.add("统计月份：");
		titleThirdRow.add("");
		titleThirdRow.add(years+months);
		List<XdDayModel> dayModels = XdDayModel.dao.find("select *from  xd_day_model where id like '" + year + month + "%'");
		int daySize = dayModels.size();
		List<XdShift> xdShifts = XdShift.dao.find("select * from  xd_shift");
		Map<String, XdShift> nameShiftObjMap = xdShifts.stream().collect(Collectors.toMap(XdShift::getShiftname, xdShift -> xdShift));

		String fileteShift="事,病,旷,新离,调,丧,控,婚,陪产,育";
		List<List<String>> rows = new ArrayList<List<String>>();
		List<String> titleRow=new ArrayList<String>();
		//titleRow.add(title);
		rows.add(titleFirstRow);
		rows.add(titleSecondRow);
		rows.add(titleThirdRow);
		rows.add(titleFourRow);

		List<String> first = new ArrayList<String>();
		first.add("姓名");
		first.add("身份证");//0
		first.add("所在部门");//1
		first.add("职务岗位");//1
		first.add("值班时间");//1
		first.add("值班费");//3
		first.add("备注");//3
		rows.add(first);

		double sum=0;
		Map<String,XdAttendanceSummary> summaryMap=new HashMap<>();
		Map<String,Map<String,String>> empNameShiftMap=new HashMap<>();
		Map<String,Map<String,List<String>>> dayMap=new HashMap<>();
		Class superclass = XdAttendanceSummary.class.getSuperclass();
		String suffix="";
		Method method=null;
		for(int j = 0; j < list.size(); j++){

			//List<String> row = new ArrayList<String>();
			XdAttendanceSummary summary = list.get(j);
			String empName = summary.getEmpName();
			Map<String,String> shiftMap=new HashMap<>();
			Map<String,List<String>> shiftDaysMap=new HashMap<>();
			summaryMap.put(empName,summary);

			for (int i = 1; i <=daySize ; i++) {
				if(i<10){
					suffix="0"+i;
				}else{
					suffix=i+"";
				}
				try {
					method= superclass.getMethod("getDay" + suffix);
					String  methodValue = (String) method.invoke(summary);
					if(methodValue!=null && !methodValue.equals("")&& fileteShift.indexOf(methodValue)==-1){
						XdShift shift = nameShiftObjMap.get(methodValue);
						if(shift!=null){
							shiftMap.put(shift.getShiftname(),shift.getShiftname());
							List<String> days = shiftDaysMap.get(shift.getShiftname());
							if(days==null){
								days=new ArrayList<>();
								days.add(month+"/"+i);
							}else{
								days.add(month+"/"+i);
							}
							shiftDaysMap.put(shift.getShiftname(),days);
						}
					}



				} catch (NoSuchMethodException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
			empNameShiftMap.put(empName,shiftMap);
			dayMap.put(empName,shiftDaysMap);
			//rows.add(row);
		}
		double cost=0;
		Set<String> empNames = summaryMap.keySet();
		for (String name : empNames) {
			XdAttendanceSummary summary = summaryMap.get(name);
			Map<String, String> shiftsMap = empNameShiftMap.get(name);
			Set<String> shiftNameSet = shiftsMap.keySet();
			Map<String, List<String>> shiftDaysMap = dayMap.get(name);
			for (String sname : shiftNameSet) {
				List<String> row = new ArrayList<String>();
				List<String> days = shiftDaysMap.get(sname);
				row.add(name);
				XdEmployee emp = XdEmployee.dao.findFirst("select * from  xd_employee where name='" + name + "'");
				row.add(emp==null?"":emp.getIdnum());
				row.add(summary.getDeptName());
				row.add(summary.getWorkStation());
				XdShift shift = nameShiftObjMap.get(sname);

				row.add("值班时间");
				if(shift.getDutyamount()!=null && !"".equals(shift.getDutyamount())){
					cost=Double.valueOf(shift.getDutyamount());
				}
				sum+=cost*days.size();
				row.add(String.valueOf(cost*days.size()));
				String remarks="";
				int entNum=0;
				for (String day : days) {
					entNum++;
					remarks=remarks+"、"+day;
					if(entNum%5==0){
						remarks=remarks+"\n";
					}
				}
				row.add(remarks.replaceAll("^、","")+"。"+"共计"+days.size()+"天");

				rows.add(row);

			}



		}
		List<String> sumRow = new ArrayList<String>();
		sumRow.add("合计");
		sumRow.add("");
		sumRow.add("");
		sumRow.add("");
		sumRow.add("");
		sumRow.add(sum+"");
		sumRow.add("");
		rows.add(sumRow);
		List<String> footerFirstRow = new ArrayList<String>();
		List<String> footerSecondRow = new ArrayList<String>();
		List<String> footerThirdRow = new ArrayList<String>();
		footerFirstRow.add("考勤人员\n签 字");
		footerFirstRow.add("");
		footerFirstRow.add("");
		footerFirstRow.add("");
		footerFirstRow.add("");
		footerFirstRow.add("日期：      年     月     日");
		footerFirstRow.add("");
		footerSecondRow.add("部门经理\n签 字");
		footerSecondRow.add("");
		footerSecondRow.add("");
		footerSecondRow.add("");
		footerSecondRow.add("");
		footerSecondRow.add("日期：      年     月     日");
		footerSecondRow.add("");
		footerThirdRow.add("分管领导\n签 字");
		footerThirdRow.add("");
		footerThirdRow.add("");
		footerThirdRow.add("");
		footerThirdRow.add("");
		footerThirdRow.add("日期：      年     月     日");
		footerThirdRow.add("");
		rows.add(footerFirstRow);
		rows.add(footerSecondRow);
		rows.add(footerThirdRow);




		File file = ExcelUtil.exportOnDutyFile(path,rows);
		return file;
	}




	
}