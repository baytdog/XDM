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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
							scheduleSummary.setEmpId(emp.getId());
							String workStation = summaryList.get(6);
							scheduleSummary.setWorkStation(workStation);
							scheduleSummary.setRemarks(summaryList.get(38));
							scheduleSummary.setScheduleMonth(month);
							scheduleSummary.setScheduleYear(year);
							scheduleSummary.setScheduleYearMonth(yearMonth);
							scheduleSummary.save();
							for (int j = 1; j <=daysNum ; j++) {
								XdScheduleDetail schDetail =new XdScheduleDetail();
								schDetail.setScheduleId(summaryId);
								String cellValue = summaryList.get(6 + j);
								if(cellValue==null ||cellValue.equals("")){
									schDetail.setShiftName("");
									schDetail.setWorkHours("");
									schDetail.setMiddleNightShift("");
								}else{
									XdShift shif = XdShift.dao.findFirst("select * from  xd_shift where shiftname='" + cellValue + "'");
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
								schDetail.save();
							}
/*
							String orgName = empCertStr.get(1);
							SysOrg sysOrg = SysOrg.dao.findFirst("select * from sys_org where name='" + orgName + "'");
							empCert.setDepartment(sysOrg.getId());
							String name = empCertStr.get(2);
							XdEmployee emp = XdEmployee.dao.findFirst("select * from  xd_employee where name='" + name + "'");
							if(emp==null){
								empCert.setEname(name);
							}else{
								empCert.setEid(emp.getId());
								empCert.setEname(name);
							}
							String certName = empCertStr.get(3);
							XdCertificate certificate = XdCertificate.dao.findFirst("select * from xd_certificate where certificateTitle='" + certName + "'");
							if(certificate!=null){
								empCert.setCertTile(certName);
								empCert.setCertId(certificate.getId());
							}else{
								empCert.setCertTile(certName);
							}
							String level = empCertStr.get(4);
							String auth = empCertStr.get(5);
							XdDict licenseAuth = XdDict.dao.findFirst("select * from xd_dict where  type  = 'licenseauth' and name ='" + auth + "'");

							empCert.setCertAuthValue(Integer.valueOf(licenseAuth.getValue()));
							empCert.setCertAuthName(auth);
							String opendate = empCertStr.get(6);
							empCert.setOpenDate(opendate);
							String validity = empCertStr.get(7);
							empCert.setValidity(validity);
							if(empCertStr.get(8).contains("-")){
								empCert.setCloseDate(empCertStr.get(8));
							}else{
								try {
									if("长期".equals(validity)){
										empCert.setCloseDate("长期");
									}else{
										LocalDate parse = LocalDate.parse(opendate);
										LocalDate localDate = parse.plusMonths(Integer.valueOf(validity) * 12);
										LocalDate localDate1 = localDate.minusDays(1);
										empCert.setCloseDate(localDate1.toString());

									}
								} catch (NumberFormatException e) {
									e.printStackTrace();
									empCert.setCloseDate(empCertStr.get(8));
								}
							}

							String idnum = empCertStr.get(9);
							empCert.setIdnum(idnum);
							String certnum = empCertStr.get(10);
							empCert.setCertnum(certnum);
							String remark = empCertStr.get(11);
							empCert.setRemak(remark);
							String certstatus = empCertStr.get(12);
							if(certstatus!=null && !"".equals(certstatus)){
								if(certstatus.contains("复印件")){
									empCert.setCertstatus("0");
								}else{
									empCert.setCertstatus("1");
								}
							}
							String sny = empCertStr.get(16);
							empCert.setSny(sny);
							String sn = empCertStr.get(17);
							empCert.setSn(sn);
							empCert.save();*/
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