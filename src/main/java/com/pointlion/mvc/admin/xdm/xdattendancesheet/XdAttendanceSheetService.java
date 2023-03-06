package com.pointlion.mvc.admin.xdm.xdattendancesheet;

import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.*;
import com.pointlion.mvc.common.utils.office.excel.ExcelUtil;
import com.pointlion.plugin.shiro.ShiroKit;
import com.pointlion.mvc.common.utils.DateUtil;
import com.pointlion.util.DictMapping;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class XdAttendanceSheetService{
	public static final XdAttendanceSheetService me = new XdAttendanceSheetService();
	public static final String TABLE_NAME = XdAttendanceSheet.tableName;
	
	/***
	 * query by id
	 */
	public XdAttendanceSheet getById(String id){
		return XdAttendanceSheet.dao.findById(id);
	}
	
	/***
	 * get page
	 */
	public Page<Record> getPage(int pnum,int psize,String deptId,String year,String month,String empName){
		String userId = ShiroKit.getUserId();
		String userOrgId = ShiroKit.getUserOrgId();


		String sql  = " from "+TABLE_NAME+" o  where 1=1";

		if(!"1".equals(userOrgId)){

			//sql = sql + " and o.create_user='"+ userId+"'";
			sql = sql + " and o.dept_id='"+ userOrgId+"'";
		}
		if(StrKit.notBlank(deptId)){
			sql = sql + " and o.dept_id='"+ deptId+"'";
		}
		if(StrKit.notBlank(empName)){
			sql = sql + " and o.emp_name like '%"+ empName+"%'";
		}
		if(StrKit.notBlank(year)){
			sql = sql + " and o.year='"+year+"'";
		}
		if(StrKit.notBlank(month)){
			sql = sql + " and o.month ='"+Integer.parseInt(month)+"'";
		}
		sql = sql + " order by o.dept_id, o.unit_id";
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
    		XdAttendanceSheet o = me.getById(id);
    		o.delete();
    	}
	}


	public File exportCheckInExcel(String path, String dept, String year, String month){
		String sql  = " from "+TABLE_NAME+" o   where  1=1";
		if(StrKit.notBlank(dept)){
			sql = sql + " and o.dept_id='"+ dept+"'";
		}
		if(StrKit.notBlank(year)){
			sql = sql + " and o.year='"+ year+"'";
		}
		if(StrKit.notBlank(month)){
			sql = sql + " and o.month='"+ month+"'";
		}

		sql = sql + " order by o.dept_id ,emp_num";


		List<XdAttendanceSheet> list = XdAttendanceSheet.dao.find(" select * "+sql);//查询全部


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
			XdAttendanceSheet summary = list.get(j);
			row.add(String.valueOf(j+1));
			row.add(summary.getDeptName());
			row.add(summary.getUnitName());
			row.add(summary.getProjectName());
			row.add(summary.getEmpNum());
			row.add(summary.getEmpName());
			emp=XdEmployee.dao.findFirst("select * from xd_employee where name ='"+summary.getEmpName()+"'");
			allowance=XdSeniorityAllowance.dao.findFirst("select * from  xd_seniority_allowance where year='"+year+"' and emp_name='"+summary.getEmpName()+"'");
			row.add(emp==null?"":emp.getIdnum());//职员代码
			//XdAttendanceDays days = XdAttendanceDays.dao.findFirst("select * from  xd_attendance_days where attendid_id='" + summary.getId() + "'");
			row.add(summary.getOrdinaryOt());
			row.add("");//双休日加班
			row.add(summary.getNationalOt());
			row.add(summary.getDutyCharge());
			row.add(summary.getMidShifts());
			row.add(summary.getNightShifts());
			row.add(summary.getHightempAllowance());
			row.add(allowance==null?"":allowance.getSeniorityAllowance()+"");//工龄津贴
			row.add("");//兼项津贴
			row.add("");//其他津贴
			row.add("");//其他调整
			row.add(summary.getShouldWorkdays());
			row.add(summary.getSickLeave());
			row.add(summary.getCasualLeave());
			row.add(summary.getAbsenceDuty());
			row.add(summary.getAbsentWork());
			row.add("");//试用期扣款
			row.add(summary.getRewardPunish());
			row.add(summary.getRemarks());
			row.add(summary.getRestAnleave());
			row.add(summary.getHireDate());
			row.add(summary.getLeaveDate());
			rows.add(row);
		}

		File file = ExcelUtil.exportCheckInExcelFile(path,rows);
		return file;
	}


	public Page<Record> getHomePageData(int pnum,int psize){
		String userId = ShiroKit.getUserId();
		SysUser sysUser = SysUser.dao.findById(userId);
		String sheetSql="";
		if(sysUser.getOrgid().equals("1")){
			sheetSql = "  from   xd_steps s left join  xd_attendance_sheet x on s.oid=x.id" +
					" where  s.stype='7' and s.orgid ='"+ShiroKit.getUserOrgId()+"' and  s.finished='N'";
		}else{
			sheetSql = "  from   xd_steps s left join  xd_attendance_sheet x on s.oid=x.id" +
					" where  s.userid ='"+ShiroKit.getUserId()+"' and  s.finished='N'";

		}

		/*{field: 'year_month', title: '年月', align: 'center'},
		{field: 'dept_name', title: '部门', align: 'center'},
		{field: 'unit_name', title: '单元', align: 'center'},
		{field: 'project_name', title: '项目', align: 'center'},
		{field: 'emp_num', title: '工号', align: 'center'},
		{field: 'emp_name', title: '姓名', align: 'center'},*/
		return Db.paginate(pnum, psize, " select s.*,x.year_month ,x.dept_name,x.year_month ,x.unit_name,x.project_name,x.emp_num,x.emp_name", sheetSql);
	}
}