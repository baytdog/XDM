package com.pointlion.mvc.admin.xdm.xdschedulesummary;

import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.log.Log4jLog;
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
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class XdScheduleSummaryService{
	public static final XdScheduleSummaryService me = new XdScheduleSummaryService();
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
						Map<String, String> holidaysMap = xdDayModels.stream().collect(Collectors.toMap(XdDayModel::getId, XdDayModel::getHolidays));
						List<XdShift> xdShifts = XdShift.dao.find("select * from  xd_shift");
						Map<String, XdShift> nameShiftObjMap = xdShifts.stream().collect(Collectors.toMap(XdShift::getShiftname, xdShift -> xdShift));

						List <XdOvertimeSummary>overTimeList =new ArrayList();
						List <XdScheduleSummary>scheduleSummaryList =new ArrayList();
						List <XdScheduleDetail>scheduleDetailList =new ArrayList();


						for(int i = 4;i<list.size();i++){//从第四行开始取
							List<String> summaryList = list.get(i);
							XdScheduleSummary scheduleSummary=new XdScheduleSummary();
							String summaryId = UuidUtil.getUUID();
							scheduleSummary.setId(summaryId);
							String department = summaryList.get(1);
							String depeId = orgMapping.get(department);
							scheduleSummary.setDeptName(department);
							scheduleSummary.setDeptValue(depeId);
							String unitName = summaryList.get(2);
							String unitValue = unitMap.get(unitName);
							scheduleSummary.setUnitName(unitName);
							scheduleSummary.setUnitValue(unitValue);
							String projectName = summaryList.get(3);
							String projectValue = projectsMap.get(projectName);
							scheduleSummary.setProjectName(projectName);
							scheduleSummary.setProjectValue(projectValue);
							String empNum = summaryList.get(4);
							scheduleSummary.setEmpNum(empNum);
							String empName = summaryList.get(5);
							XdEmployee emp = XdEmployee.dao.findFirst("select * from  xd_employee where name='" + empName + "'");
							scheduleSummary.setEmpName(empName);
							scheduleSummary.setEmpId(emp==null?"":emp.getId());
							String workStation = summaryList.get(6);
							scheduleSummary.setWorkStation(workStation);
							scheduleSummary.setDay01(summaryList.get(7));
							scheduleSummary.setDay02(summaryList.get(8));
							scheduleSummary.setDay03(summaryList.get(9));
							scheduleSummary.setDay04(summaryList.get(10));
							scheduleSummary.setDay05(summaryList.get(11));
							scheduleSummary.setDay06(summaryList.get(12));
							scheduleSummary.setDay07(summaryList.get(13));
							scheduleSummary.setDay08(summaryList.get(14));
							scheduleSummary.setDay09(summaryList.get(15));
							scheduleSummary.setDay10(summaryList.get(16));
							scheduleSummary.setDay11(summaryList.get(17));
							scheduleSummary.setDay12(summaryList.get(18));
							scheduleSummary.setDay13(summaryList.get(19));
							scheduleSummary.setDay14(summaryList.get(20));
							scheduleSummary.setDay15(summaryList.get(21));
							scheduleSummary.setDay16(summaryList.get(22));
							scheduleSummary.setDay17(summaryList.get(23));
							scheduleSummary.setDay18(summaryList.get(24));
							scheduleSummary.setDay19(summaryList.get(25));
							scheduleSummary.setDay20(summaryList.get(26));
							scheduleSummary.setDay21(summaryList.get(27));
							scheduleSummary.setDay22(summaryList.get(28));
							scheduleSummary.setDay23(summaryList.get(29));
							scheduleSummary.setDay24(summaryList.get(30));
							scheduleSummary.setDay25(summaryList.get(31));
							scheduleSummary.setDay26(summaryList.get(32));
							scheduleSummary.setDay27(summaryList.get(33));
							scheduleSummary.setDay28(summaryList.get(34));
							scheduleSummary.setDay29(summaryList.get(35));
							scheduleSummary.setDay30(summaryList.get(36));
							scheduleSummary.setDay31(summaryList.get(37));

							scheduleSummary.setRemarks(summaryList.get(38));
							scheduleSummary.setScheduleMonth(month);
							scheduleSummary.setScheduleYear(year);
							scheduleSummary.setScheduleYearMonth(yearMonth);
							scheduleSummary.setCreateDate(time);
							scheduleSummary.setCreateUser(user.getId());
							//scheduleSummary.save();
							scheduleSummaryList.add(scheduleSummary);
							String ymd="";
							String ymr="";
//							 节假日加班
							for (int j = 1; j <= daysNum; j++) {
								if(j<10){
									ymd = yearMonth + '0' + j;
									ymr=year+"-"+month+"-"+'0' + j;
								}else {
									ymd = yearMonth  + j;
									ymr=year+"-"+month+"-"+j;
								}
								String cellValue = summaryList.get(6 + j);
								if(!cellValue.equals("")){
									XdShift xdShift = nameShiftObjMap.get(cellValue);
									if(xdShift!=null){
										if(xdShift.getSpanDay().equals("1")){
											LocalDate localDate = LocalDate.parse(ymr).plusDays(1);
											DateTimeFormatter dtf=DateTimeFormatter.ofPattern("yyyyMMdd");
											DateTimeFormatter dtf1=DateTimeFormatter.ofPattern("yyyy-MM-dd");
											String nextDate  = dtf.format(localDate);
											//是跨天且当天法定假日
											if(holidaysMap.get(ymd)!=null && !"".equals(holidaysMap.get(ymd))){
												XdOvertimeSummary xdOvertimeSummary=new XdOvertimeSummary();
												xdOvertimeSummary.setEmpNum(scheduleSummary.getEmpNum());
												xdOvertimeSummary.setEmpName(scheduleSummary.getEmpName());
												xdOvertimeSummary.setEmpIdnum(emp==null?"":emp.getIdnum());
												xdOvertimeSummary.setProjectName(projectName);
												xdOvertimeSummary.setProjectId(projectValue);
												xdOvertimeSummary.setDeptName(scheduleSummary.getDeptName());
												xdOvertimeSummary.setDeptId(scheduleSummary.getDeptValue());
												xdOvertimeSummary.setApplyDate(ymr);
												xdOvertimeSummary.setApplyStart(xdShift.getBusitime());
												xdOvertimeSummary.setApplyEnd("24:00");
												xdOvertimeSummary.setApplyHours(xdShift.getCurdayHours());
												xdOvertimeSummary.setApplyType("0");
												xdOvertimeSummary.setCreateUser(ShiroKit.getUserId());
												xdOvertimeSummary.setCreateDate(DateUtil.getCurrentTime());
												overTimeList.add(xdOvertimeSummary);

											}
											//是跨天且第二天是法定假日
											if(holidaysMap.get(nextDate)!=null && !"".equals(holidaysMap.get(nextDate))){
												XdOvertimeSummary xdOvertimeSummary=new XdOvertimeSummary();
												xdOvertimeSummary.setEmpNum(scheduleSummary.getEmpNum());
												xdOvertimeSummary.setEmpName(scheduleSummary.getEmpName());
												xdOvertimeSummary.setEmpIdnum(emp==null?"":emp.getIdnum());
												xdOvertimeSummary.setProjectName(projectName);
												xdOvertimeSummary.setProjectId(projectValue);
												xdOvertimeSummary.setDeptName(scheduleSummary.getDeptName());
												xdOvertimeSummary.setDeptId(scheduleSummary.getDeptValue());
												xdOvertimeSummary.setApplyDate(dtf1.format(localDate));
												xdOvertimeSummary.setApplyStart("00:00");
												xdOvertimeSummary.setApplyEnd(xdShift.getUnbusitime());
												xdOvertimeSummary.setApplyHours(xdShift.getSpanHours());
												xdOvertimeSummary.setApplyType("0");
												xdOvertimeSummary.setCreateUser(ShiroKit.getUserId());
												xdOvertimeSummary.setCreateDate(DateUtil.getCurrentTime());
												overTimeList.add(xdOvertimeSummary);
											}
										}else{
											if(holidaysMap.get(ymd)!=null && !"".equals(holidaysMap.get(ymd)) && !cellValue.equals("")){
												XdOvertimeSummary xdOvertimeSummary=new XdOvertimeSummary();
												xdOvertimeSummary.setEmpNum(scheduleSummary.getEmpNum());
												xdOvertimeSummary.setEmpName(scheduleSummary.getEmpName());
												xdOvertimeSummary.setEmpIdnum(emp==null?"":emp.getIdnum());
												xdOvertimeSummary.setProjectName(projectName);
												xdOvertimeSummary.setProjectId(projectValue);
												xdOvertimeSummary.setDeptName(scheduleSummary.getDeptName());
												xdOvertimeSummary.setDeptId(scheduleSummary.getDeptValue());
												xdOvertimeSummary.setApplyDate(ymr);
												xdOvertimeSummary.setApplyStart(xdShift.getBusitime());
												xdOvertimeSummary.setApplyEnd(xdShift.getUnbusitime());
												xdOvertimeSummary.setApplyHours(xdShift.getHours());
												xdOvertimeSummary.setApplyType("0");
												xdOvertimeSummary.setCreateUser(ShiroKit.getUserId());
												xdOvertimeSummary.setCreateDate(DateUtil.getCurrentTime());
												overTimeList.add(xdOvertimeSummary);
											}

										}
									}
								}



								/*if(holidaysMap.get(ymd)!=null && !"".equals(holidaysMap.get(ymd)) && !cellValue.equals("")){
									XdOvertimeSummary xdOvertimeSummary=new XdOvertimeSummary();
									xdOvertimeSummary.setEmpNum(scheduleSummary.getEmpNum());
									xdOvertimeSummary.setEmpName(scheduleSummary.getEmpName());
									xdOvertimeSummary.setEmpIdnum(emp==null?"":emp.getIdnum());
									xdOvertimeSummary.setProjectName(projectName);
									xdOvertimeSummary.setProjectId(projectValue);
									xdOvertimeSummary.setDeptName(scheduleSummary.getDeptName());
									xdOvertimeSummary.setDeptId(scheduleSummary.getDeptValue());
									xdOvertimeSummary.setApplyDate(ymr);

									//XdShift shif = XdShift.dao.findFirst("select * from  xd_shift where shiftname='" + cellValue + "'");

									xdOvertimeSummary.setApplyStart(xdShift==null?"":xdShift.getBusitime());
									xdOvertimeSummary.setApplyEnd(xdShift==null?"":xdShift.getUnbusitime());
									xdOvertimeSummary.setApplyHours(xdShift==null?"":xdShift.getHours());
									xdOvertimeSummary.setApplyType("0");
									xdOvertimeSummary.setCreateUser(ShiroKit.getUserId());
									xdOvertimeSummary.setCreateDate(DateUtil.getCurrentTime());
//									xdOvertimeSummary.save();
									overTimeList.add(xdOvertimeSummary);
								}*/


							}



							for (int j = 1; j <=daysNum ; j++) {
								XdScheduleDetail schDetail =new XdScheduleDetail();
								schDetail.setScheduleId(summaryId);
								String cellValue = summaryList.get(6 + j);
								if(cellValue==null ||cellValue.equals("")){
									schDetail.setShiftName("");
									schDetail.setWorkHours("");
									schDetail.setMiddleNightShift("");
								}else{
									/*XdShift shif = XdShift.dao.findFirst("select * from  xd_shift where shiftname='" + cellValue + "'");*/
									XdShift shif = nameShiftObjMap.get(cellValue);
									schDetail.setShiftName(cellValue);
									if(shif==null){
										schDetail.setWorkHours("0");
										schDetail.setMiddleNightShift("");
									}else{

										schDetail.setWorkHours(shif.getHours());

										if(shif.getMiddleshift()==null && shif.getNigthshift()==null){
											schDetail.setMiddleNightShift("");
										}else{
											String middelShift = shif.getMiddleshift() == null ? "" : shif.getMiddleshift();
											String nightShift = shif.getNigthshift() == null ? "" : shif.getNigthshift();
											schDetail.setMiddleNightShift(middelShift+nightShift);
										}
									}
								}

								schDetail.setScheduleYear(year);
								schDetail.setScheduleMonth(month);
								schDetail.setScheduleDay(String.valueOf(j));
								String day="";
								if(j<10){
									day="0"+j;
								} else{
									day=j+"";
								}
								schDetail.setScheduleYmd(year+"-"+month+"-"+day);
								schDetail.setWeekDay(xdDayModels.get(j - 1).getWeeks());
								schDetail.setHolidays(xdDayModels.get(j - 1).getHolidays());
								schDetail.setCreateDate(DateUtil.getCurrentTime());
								schDetail.setCreateUser(ShiroKit.getUserId());
								//schDetail.save();
								scheduleDetailList.add(schDetail);
							}
						}


						Db.batchSave(overTimeList,overTimeList.size());
						Db.batchSave(scheduleSummaryList,scheduleSummaryList.size());
						Db.batchSave(scheduleDetailList,scheduleDetailList.size());

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
		File file = ExcelUtil.scheduletFile(path,rows);
		return file;
	}
	
}