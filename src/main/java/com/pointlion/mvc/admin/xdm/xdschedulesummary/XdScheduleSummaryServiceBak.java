package com.pointlion.mvc.admin.xdm.xdschedulesummary;

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

public class XdScheduleSummaryServiceBak {
	public static final XdScheduleSummaryServiceBak me = new XdScheduleSummaryServiceBak();
	public static final String TABLE_NAME = XdScheduleSummary.tableName;

	/***
	 * query by id
	 */
	public XdScheduleSummary getById(String id){
		return XdScheduleSummary.dao.findById(id);
	}
	
	/***
	 * get page
	 */
	public Page<Record> getPage(int pnum,int psize,String dept,String unitname,String emp_name,String year,String month){
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
    		XdScheduleSummary o = me.getById(id);
			Db.delete("delete  from  xd_schedule_detail where schedule_id='" + id + "'");
			String yearMonth = o.getScheduleYear() + "-" + o.getScheduleMonth();
			Db.delete("delete  from xd_overtime_summary where emp_num='"+o.getEmpNum()+"' and apply_date like '"+yearMonth+"%'");

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
	 * @Version  1.0
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
					if(list.size()>3){
						SimpleUser user = ShiroKit.getLoginUser();
						String creatTime = DateUtil.getCurrentTime();
						List<String> title = list.get(0);
						String[] ny = title.get(0).replaceAll("年", "_").replaceAll("[^(0-9_)]", "").split("_");
						String year=ny[0];
						String month=ny[1];
						if(month.length()==1){
							month="0"+month;
						}
						String yearMonth=year+month;

						String lastMonLastDay = DateTimeFormatter.ofPattern("yyyy-MM-dd").format(LocalDate.parse(year+"-"+month+"-01").minusDays(1));
						String[] split = lastMonLastDay.split("-");
						String lastYear=split[0];
						String lastMonth=split[1];
						String lastDay=split[2];

						List<XdDayModel> xdDayModels = XdDayModel.dao.find("select * from  xd_day_model  where id like '" + yearMonth + "%' or days='"+lastMonLastDay+" ' order by id");
						int daysNum=xdDayModels.size();
						if(!xdDayModels.get(0).getId().startsWith(yearMonth)){
							daysNum=daysNum-1;
						}
						String nextMonFirstDay = DateTimeFormatter.ofPattern("yyyyMMdd").format(
								LocalDate.parse(xdDayModels.get(xdDayModels.size() - 1).getDays()).plusDays(1));

						XdDayModel nextMonFirstModel = XdDayModel.dao.findById(nextMonFirstDay);
						xdDayModels.add(nextMonFirstModel);
						StringBuffer sb =new StringBuffer();
						xdDayModels.stream().forEach( xdDayModel-> {
								if("1".equals(xdDayModel.getIsnatHoliday())){
									sb.append(xdDayModel.getId()).append(",");
								}
						});


						//Map<String, String> holidaysMap = xdDayModels.stream().collect(Collectors.toMap(XdDayModel::getId, XdDayModel::getHolidays));


						List<XdShift> xdShifts = XdShift.dao.find("select * from  xd_shift");
						Map<String, XdShift> nameShiftObjMap = xdShifts.stream().collect(Collectors.toMap(XdShift::getShiftname, xdShift -> xdShift));

						XdWorkHour workHours = XdWorkHour.dao.findFirst("select * from  xd_work_hour where year='" + year + "' and  month='" + month + "'");


						List <XdOvertimeSummary>overTimeList =new ArrayList();
						List <XdScheduleSummary>scheduleSummaryList =new ArrayList();
						//List <XdScheduleDetail>scheduleDetailList =new ArrayList();
//						XdDayModel lastMonLastDay = XdDayModel.dao.findFirst("select * from  xd_day_model where id like '" + lastMonth + "%' order by id desc");
						//lastmonlastday.getYear()
						//LocalDate lastmonlastday = LocalDate.parse(year+"-"+month+"-01").minusDays(1);
						//DateTimeFormatter lastdtf=DateTimeFormatter.ofPattern("yyyy-MM-dd");

						double curMonHours=(workHours==null?0:workHours.getWorkHour());//当月工时
						Class superclass = XdScheduleSummary.class.getSuperclass();
						for(int i = 3;i<list.size();i++){//从第四行开始取
							String otFlags="";
							String modifyFlags="";
							String tips="";
							double othours=0;//加班时间
							double work_hour=0;//出勤时间
							List<String> summaryList = list.get(i);
							XdScheduleSummary scheduleSummary=new XdScheduleSummary();
							String summaryId = UuidUtil.getUUID();
							scheduleSummary.setId(summaryId);
							String department = summaryList.get(0);
							String departId = orgMapping.get(department);
							scheduleSummary.setDeptName(department);
							scheduleSummary.setDeptValue(departId);
							String unitName = summaryList.get(1);
							String unitValue = unitMap.get(unitName);
							scheduleSummary.setUnitName(unitName);
							scheduleSummary.setUnitValue(unitValue);
							String projectName = summaryList.get(2);
							String projectValue = projectsMap.get(projectName);
							scheduleSummary.setProjectName(projectName);
							scheduleSummary.setProjectValue(projectValue);
							String empNum = summaryList.get(3);
							scheduleSummary.setEmpNum(empNum);
							String empName = summaryList.get(4);
							XdEmployee emp = XdEmployee.dao.findFirst("select * from  xd_employee where name='" + empName + "'");
							scheduleSummary.setEmpName(empName);
							scheduleSummary.setEmpId(emp==null?"":emp.getId());
							String workStation = summaryList.get(5);
							scheduleSummary.setWorkStation(workStation);



							modifyFlags=modifyFlags+","+"0";
							tips=tips+","+"0";

							String lastMonLastDayValue = summaryList.get(6);
							scheduleSummary.setDay00(lastMonLastDayValue);
							if(sb.indexOf(xdDayModels.get(0).getId())!=-1){
								otFlags=otFlags+","+"1";
							}else{
								otFlags=otFlags+","+"0";
							}

							if(!lastMonLastDayValue.equals("")){
								XdShift xdShift = nameShiftObjMap.get(lastMonLastDayValue);
								if(xdShift!=null && xdShift.getBusitime()!=null && !xdShift.getBusitime().equals("")){
									if("1".equals(xdShift.getSpanDay())){
										//跨天
										if(sb.indexOf(yearMonth + "01")!=-1){
											othours+=Double.valueOf(xdShift.getSpanHours());//加班时间
										}else{
											work_hour+=Double.valueOf(xdShift.getSpanHours());//出勤时间
										}
									}
								}

							}


							/*XdScheduleSummary lastMonSummary = XdScheduleSummary.dao.findFirst("select * from xd_schedule_summary " +
									"where emp_name='" + empName + "' " +
									"and  schedule_year='" + lastYear + "'" +
									"and schedule_month='" + lastMonth + "'");
							if(lastMonSummary==null){
								otFlags=otFlags+","+"0";
								scheduleSummary.setDay00("");
							}else{
								Method getLastMonLastDay = superclass.getMethod("getDay" + lastDay);
								String  lastMonShift = (String)getLastMonLastDay.invoke(lastMonSummary);
								scheduleSummary.setDay00(lastMonShift);

								String lastFlags = lastMonSummary.getOtflags().substring(lastMonSummary.getOtflags().length() - 1);
								otFlags=otFlags+","+lastFlags;

								if(lastMonShift!=null ||!lastMonShift.equals("")){
									XdShift xdShift = nameShiftObjMap.get(lastMonShift);
									if(xdShift!=null){
										if(xdShift.getSpanDay().equals("1")){
											//跨天
											if(sb.indexOf(yearMonth + "01")!=-1){
												othours+=Double.valueOf(xdShift.getSpanHours());//加班时间
											}else{
												work_hour+=Double.valueOf(xdShift.getSpanHours());//出勤时间
											}
										}
									}
								}
							}*/

							Method setMethod=null;
							String ymdNoLine="";
							String ymdInLine="";
							String cellValue="";
							for (int j = 1; j <=daysNum; j++) {
								modifyFlags=modifyFlags+","+"0";
								tips=tips+","+"0";

								 cellValue = summaryList.get(6 + j).trim();
								/*System.out.println(j);
								System.out.println(cellValue);*/
								if(j<10){
									setMethod = superclass.getMethod("setDay0" + j,String.class);
									ymdNoLine = yearMonth + '0' + j;
									ymdInLine=year+"-"+month+"-"+'0' + j;

								}else{
									setMethod = superclass.getMethod("setDay" + j,String.class);
									ymdNoLine = yearMonth  + j;
									ymdInLine=year+"-"+month+"-"+j;
								}

								setMethod.invoke(scheduleSummary,cellValue);


								if(!cellValue.equals("")){
									XdShift xdShift = nameShiftObjMap.get(cellValue);
									if(xdShift!=null){

										if(xdShift.getSpanDay().equals("1")){
											//是跨天且当天法定假日
											if(sb.indexOf(ymdNoLine)!=-1){
												XdOvertimeSummary ots = new XdOvertimeSummary();
												ots.setEmpNum(scheduleSummary.getEmpNum());
												ots.setEmpName(scheduleSummary.getEmpName());
												ots.setEmpIdnum(emp==null?"":emp.getIdnum());
												ots.setProjectName(projectName);
												ots.setProjectId(projectValue);
												ots.setDeptName(scheduleSummary.getDeptName());
												ots.setDeptId(scheduleSummary.getDeptValue());
												ots.setApplyDate(ymdInLine);
												ots.setApplyStart(xdShift.getBusitime());
												ots.setApplyEnd("24:00");
												ots.setApplyHours(xdShift.getCurdayHours()+"");
												ots.setApplyType("0");
												ots.setSource("0");
												ots.setSuperDays(ymdInLine);
												ots.setCreateUser(ShiroKit.getUserId());
												ots.setCreateDate(DateUtil.getCurrentTime());
												overTimeList.add(ots);
												othours+=Double.valueOf(xdShift.getCurdayHours());
												otFlags=otFlags+","+"1";
											}else{
												//跨天当天不是法定假日
												work_hour+=Double.valueOf(xdShift.getCurdayHours());
												otFlags=otFlags+","+"0";
											}
											//是跨天且第二天是法定假日
											LocalDate nextLocalDate = LocalDate.parse(ymdInLine).plusDays(1);
											String nextDate  = DateTimeFormatter.ofPattern("yyyyMMdd").format(nextLocalDate);
											if(sb.indexOf(nextDate)!=-1){
												XdOvertimeSummary otsObject=new XdOvertimeSummary();
												otsObject.setEmpNum(scheduleSummary.getEmpNum());
												otsObject.setEmpName(scheduleSummary.getEmpName());
												otsObject.setEmpIdnum(emp==null?"":emp.getIdnum());
												otsObject.setProjectName(projectName);
												otsObject.setProjectId(projectValue);
												otsObject.setDeptName(scheduleSummary.getDeptName());
												otsObject.setDeptId(scheduleSummary.getDeptValue());
												otsObject.setApplyDate(DateTimeFormatter.ofPattern("yyyy-MM-dd").format(nextLocalDate));
												otsObject.setApplyStart("0:00");
												otsObject.setApplyEnd(xdShift.getUnbusitime());
												otsObject.setApplyHours(xdShift.getSpanHours()+"");
												otsObject.setApplyType("0");
												otsObject.setSource("0");
												otsObject.setSuperDays(ymdInLine);
												otsObject.setCreateUser(ShiroKit.getUserId());
												otsObject.setCreateDate(DateUtil.getCurrentTime());
												overTimeList.add(otsObject);
												//othours+=Double.valueOf(xdShift.getSpanHours());
											}else{
												//是跨天且第二天不是法定假日
												work_hour+=Double.valueOf(xdShift.getSpanHours());
											}
										}else{
											if(xdShift.getBusitime()!=null && !xdShift.getBusitime().equals("")){
												if(sb.indexOf(ymdNoLine)!=-1){
													XdOvertimeSummary xdOvertimeSummary=new XdOvertimeSummary();
													xdOvertimeSummary.setEmpNum(scheduleSummary.getEmpNum());
													xdOvertimeSummary.setEmpName(scheduleSummary.getEmpName());
													xdOvertimeSummary.setEmpIdnum(emp==null?"":emp.getIdnum());
													xdOvertimeSummary.setProjectName(projectName);
													xdOvertimeSummary.setProjectId(projectValue);
													xdOvertimeSummary.setDeptName(scheduleSummary.getDeptName());
													xdOvertimeSummary.setDeptId(scheduleSummary.getDeptValue());
													xdOvertimeSummary.setApplyDate(ymdInLine);
													xdOvertimeSummary.setApplyStart(xdShift.getBusitime());
													xdOvertimeSummary.setApplyEnd(xdShift.getUnbusitime());
													xdOvertimeSummary.setApplyHours(xdShift.getHours()+"");
													xdOvertimeSummary.setApplyType("0");
													xdOvertimeSummary.setSource("0");
													xdOvertimeSummary.setSuperDays(ymdInLine);
													xdOvertimeSummary.setCreateUser(ShiroKit.getUserId());
													xdOvertimeSummary.setCreateDate(DateUtil.getCurrentTime());
													overTimeList.add(xdOvertimeSummary);
													othours+=Double.valueOf(xdShift.getHours());
													otFlags=otFlags+","+"1";
												}else{
													work_hour+=Double.valueOf(xdShift.getHours());
													otFlags=otFlags+","+"0";
												}
											}else{
												otFlags=otFlags+","+"0";
											}
										}
									}else{
										otFlags=otFlags+","+"0";
									}
								}else {
									otFlags=otFlags+","+"0";
								}
							}

							//scheduleSummary.setRemarks(summaryList.get(38));
							scheduleSummary.setCurMonHours(curMonHours);
							scheduleSummary.setWorkHour(work_hour);
							scheduleSummary.setOthours(0.0);
							scheduleSummary.setNatOthours(othours);
							scheduleSummary.setFlags(modifyFlags.replaceAll("^,",""));
							scheduleSummary.setOtflags(otFlags.replaceAll("^,",""));
							scheduleSummary.setTips(tips.replaceAll("^,",""));
							scheduleSummary.setScheduleMonth(month);
							scheduleSummary.setScheduleYear(year);
							scheduleSummary.setScheduleYearMonth(yearMonth);
							scheduleSummary.setCreateDate(creatTime);
							scheduleSummary.setCreateUser(user.getId());
							//scheduleSummary.save();
							scheduleSummaryList.add(scheduleSummary);

						}


						Db.batchSave(overTimeList,overTimeList.size());
						Db.batchSave(scheduleSummaryList,scheduleSummaryList.size());
						//Db.batchSave(scheduleDetailList,scheduleDetailList.size());

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


		List<XdScheduleSummary> list = XdScheduleSummary.dao.find(" select * "+sql);//查询全部


		Map<String, Map<String, String>> dictMap = DictMapping.dictMappingValueToName();
		Map<String, String> projectMap = DictMapping.projectsMappingValueToName();
		Map<String, String> orgMap = DictMapping.orgMapping("0");
		String departName = orgMap.get(dept)==null?"":orgMap.get(dept);
		Map<String, String> unit = dictMap.get("unit");
		String unitName = unit.get(unitname)==null?"":unit.get(unitname);
		String years="";
		String months="";
		String title="排班表";
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
		for (int i = 0; i < xdDayModels.size(); i++) {
			XdDayModel dayModel = xdDayModels.get(i);
			first.add(String.valueOf(i+1));
			second.add(dayModel.getWeeks());
			third.add(dayModel.getHolidays());
		}
		first.add("备注");
		second.add("");
		third.add("");
		rows.add(first);
		rows.add(second);
		rows.add(third);
		for(int j = 0; j < list.size(); j++){
			List<String> row = new ArrayList<String>();
			//DictMapping.fieldValueToName(emp,orgMap,projectMap,dictMap);
			row.add(String.valueOf(j+1));
			XdScheduleSummary summary = list.get(j);
			List<XdScheduleDetail> xdScheduleDetails = XdScheduleDetail.dao.find("select * from  xd_schedule_detail where schedule_id='"+summary.getId()+"' order by schedule_ymd");
			row.add(summary.getDeptName());//0
			row.add(summary.getUnitName());//0
			row.add(summary.getProjectName());//0
			row.add(summary.getEmpNum());
			row.add(summary.getEmpName());
			row.add(summary.getWorkStation());
			for (XdScheduleDetail detail : xdScheduleDetails) {
				row.add(detail.getShiftName());
			}
			row.add(summary.getRemarks());
			rows.add(row);
		}
		File file = ExcelUtil.scheduleFile(path,rows);
		return file;
	}
	
}