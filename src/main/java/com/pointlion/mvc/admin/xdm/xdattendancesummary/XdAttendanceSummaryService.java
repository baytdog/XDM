package com.pointlion.mvc.admin.xdm.xdattendancesummary;

import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.*;
import com.pointlion.mvc.common.utils.UuidUtil;
import com.pointlion.mvc.common.utils.office.excel.ExcelUtil;
import com.pointlion.plugin.shiro.ShiroKit;
import com.pointlion.mvc.common.utils.DateUtil;
import com.pointlion.plugin.shiro.ext.SimpleUser;
import com.pointlion.util.DictMapping;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

			XdAttendanceRcp rcp= XdAttendanceRcp.dao.findFirst("select * from  xd_attendance_rcp  where  attendid_id='"+summary.getId()+"'");
			row.add(rcp.getMidnightDays());
			row.add(rcp.getMonworkDays());
			row.add(rcp.getAllowanceAmount());
			row.add(rcp.getShiftName());
			row.add(rcp.getRemarks());

			rows.add(row);
		}
		File file = ExcelUtil.attendanceFile(path,rows,xdDayModels.size());
		return file;
	}




	
}