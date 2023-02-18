package com.pointlion.mvc.admin.xdm.xdsettleovertimesummary;

import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.*;
import com.pointlion.mvc.common.utils.office.excel.ExcelUtil;
import com.pointlion.plugin.shiro.ShiroKit;
import com.pointlion.mvc.common.utils.DateUtil;
import com.pointlion.plugin.shiro.ext.SimpleUser;
import com.pointlion.util.DictMapping;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.*;

public class XdSettleOvertimeSummaryService{
	public static final XdSettleOvertimeSummaryService me = new XdSettleOvertimeSummaryService();
	public static final String TABLE_NAME = XdSettleOvertimeSummary.tableName;
	
	/***
	 * query by id
	 */
	public XdSettleOvertimeSummary getById(String id){
		return XdSettleOvertimeSummary.dao.findById(id);
	}
	
	/***
	 * get page
	 */
	public Page<Record> getPage(int pnum,int psize,String startTime,String endTime,String applyUser){
		String userId = ShiroKit.getUserId();
		String sql  = " from "+TABLE_NAME+" o  where 1=1";
		if(StrKit.notBlank(startTime)){
			sql = sql + " and o.create_time>='"+ DateUtil.formatSearchTime(startTime,"0")+"'";
		}
		if(StrKit.notBlank(endTime)){
			sql = sql + " and o.create_time<='"+DateUtil.formatSearchTime(endTime,"1")+"'";
		}
		if(StrKit.notBlank(applyUser)){
			sql = sql + " and o.applyer_name like '%"+applyUser+"%'";
		}
		sql = sql + " order by o.create_date desc";
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
    		XdSettleOvertimeSummary o = me.getById(id);

			String[] appDateArr = o.getApplyDate().split("-");

			int oldIndex = Integer.parseInt(appDateArr[2]);

			XdAttendanceSummary attendanceSummary =
					XdAttendanceSummary.dao.findFirst("select * from  xd_attendance_summary where emp_name='"+o.getEmpName()
							+"' and schedule_year='" + appDateArr[0]+ "' and schedule_month='" + appDateArr[1] + "'");
			if(attendanceSummary!=null){
				String[] oldTips = attendanceSummary.getTips().split(",");

				String oldTip = oldTips[oldIndex];
				oldTip=oldTip.replaceAll(o.getActStart()+"-"+o.getActEnd(),"");
				if("".equals(oldTip)){
					oldTip="0";
				}
				oldTips[oldIndex]=oldTip;

				oldTip="";
				for (String tip : oldTips) {
					oldTip=oldTip+tip+",";
				}
				attendanceSummary.setTips(oldTip.replaceAll(",$",""));

				if("0".equals(o.getApplyType())){
					attendanceSummary.setNatOthours(attendanceSummary.getNatOthours()- o.getActHours());
				}else{
					attendanceSummary.setOthours(attendanceSummary.getOthours()-o.getActHours());
				}

				attendanceSummary.update();
			}

    		o.delete();
    	}
	}

	/**
	 * @Method importExcel
	 * @param list:
	 * @Date 2023/1/15 15:12
	 * @Description 导入excle 数据
	 * @Author king
	 * @Version  1.0
	 * @Return java.util.Map<java.lang.String,java.lang.Object>
	 */
	public Map<String,Object> importExcel(List<List<String>> list) throws SQLException {
		final List<String> message = new ArrayList<String>();
		final Map<String,Object> result = new HashMap<String,Object>();
		Map<String, String> orgMapping = DictMapping.orgMapping("1");
		Map<String, String> projectsMap = DictMapping.projectsMapping();

		Db.tx(new IAtom() {
			@Override
			public boolean run() throws SQLException {
				try{
					if(list.size()>5){
						SimpleUser user = ShiroKit.getLoginUser();
						String time = DateUtil.getCurrentTime();
						List<String> deptRow = list.get(3);
						String  deptName = deptRow.get(11);
						String dept_value = orgMapping.get(deptName);
						for(int i = 6;i<list.size();i++){
							List<String> overtimeList = list.get(i);

							if(overtimeList.get(0)==null ||overtimeList.get(0).equals("")){
								continue;
							}
							XdSettleOvertimeSummary overtimeSummary=new XdSettleOvertimeSummary();
							overtimeSummary.setDeptId(dept_value);
							overtimeSummary.setDeptName(deptName);
							overtimeSummary.setEmpNum(overtimeList.get(0));
							overtimeSummary.setEmpName(overtimeList.get(1));
							overtimeSummary.setEmpIdnum(overtimeList.get(2));
							String project_name = overtimeList.get(3);
							String project_id = projectsMap.get(project_name);
							overtimeSummary.setProjectId(project_id);
							overtimeSummary.setProjectName(project_name);
							overtimeSummary.setApplyDate(overtimeList.get(4));
							overtimeSummary.setApplyStart(overtimeList.get(5));
							overtimeSummary.setApplyEnd(overtimeList.get(6));
							overtimeSummary.setApplyHours(Double.valueOf(overtimeList.get(7)));
							String overtype = overtimeList.get(8);
							if(overtype==null||overtype.equals("")){
								overtimeSummary.setApplyType("");
							}else{
								if("非国定节假日加班".equals(overtype)){
									overtimeSummary.setApplyType("1");
								}else if("国定节假日加班".equals(overtype)){
									overtimeSummary.setApplyType("0");
								}else{
									overtimeSummary.setApplyType(overtype);
								}
							}
							overtimeSummary.setActStart(overtimeList.get(9));
							overtimeSummary.setActEnd(overtimeList.get(10));
							overtimeSummary.setActHours(Double.valueOf(overtimeList.get(11)));
							overtimeSummary.setRemarks(overtimeList.get(12));
							overtimeSummary.setCreateDate(DateUtil.getCurrentTime());
							overtimeSummary.setCreateUser(ShiroKit.getUserId());
							overtimeSummary.save();

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
	 * @Method exportExcel
	 * @param path:
	 * @param dept:
	 * @param unitname:
	 * @param year:
	 * @param month:
	 * @param emp_name:
	 * @Date 2023/1/15 15:13
	 * @Description  导出excle 数据
	 * @Author king
	 * @Version  1.0
	 * @Return java.io.File
	 */
	public File exportExcel(String path, String dept, String unitname, String year, String month, String emp_name){
		String sql  = " from "+TABLE_NAME+" o   where 1=1";
		if(StrKit.notBlank(dept)){
			sql = sql + " and o.dept_id='"+ dept+"'";
		}

		sql = sql + " order by o.create_date desc,emp_num";


		List<XdSettleOvertimeSummary> list = XdSettleOvertimeSummary.dao.find(" select * "+sql);//查询全部


		Map<String, Map<String, String>> dictMap = DictMapping.dictMappingValueToName();
		Map<String, String> projectMap = DictMapping.projectsMappingValueToName();
		Map<String, String> orgMap = DictMapping.orgMapping("0");
		String departName = orgMap.get(dept)==null?"":orgMap.get(dept);
		Map<String, String> unit = dictMap.get("unit");
		List<List<String>> rows = new ArrayList<List<String>>();
		List<String> titleFirstRow=new ArrayList<String>();
		List<String> titleSecondRow=new ArrayList<String>();
		List<String> titleThirdRow=new ArrayList<String>();
		List<String> titleFourRow=new ArrayList<String>();

		for (int i =1; i <=13; i++) {
			if(i==13){
				titleFirstRow.add("MF/WI-HR-03-01(B/0)");
				titleSecondRow.add("上海东方欣迪商务服务有限公司");
				titleThirdRow.add("加班申请表");
				if(StrKit.notBlank(dept)) {
					SysOrg org = SysOrg.dao.findById(dept);
					titleFourRow.add(org.getName());
				}else{
					titleFourRow.add("");
				}

			}else if(i==11){
				titleFirstRow.add("");
				titleSecondRow.add("");
				titleThirdRow.add("");
				titleFourRow.add("部门:");
			}else{
				titleFirstRow.add("");
				titleSecondRow.add("");
				titleThirdRow.add("");
				titleFourRow.add("");

			}

		}
		rows.add(titleFirstRow);
		rows.add(titleSecondRow);
		rows.add(titleThirdRow);
		rows.add(titleFourRow);
		/*List<String> titleRow=new ArrayList<String>();

		titleRow.add(title);
		rows.add(titleRow);*/

		List<String> first = new ArrayList<String>();
		List<String> second = new ArrayList<String>();
		first.add("工号");
		first.add("姓名");//0
		first.add("身份证");
		first.add("加班所在项目");//1
		first.add("加班申请");//2
		first.add("");//2
		first.add("");//2
		first.add("");//2
		first.add("");//2
		first.add("实际加班");//2
		first.add("");//2
		first.add("");//2
		first.add("备注");//3

		second.add("");
		second.add("");
		second.add("");
		second.add("");
		second.add("日期");
		second.add("开始时间");
		second.add("结束时间");
		second.add("加班时长");
		second.add("类别");
		second.add("开始时间");
		second.add("结束时间");
		second.add("加班时长");
		second.add("");

		rows.add(first);
		rows.add(second);
		double applyOvertime=0;

		DecimalFormat df = new DecimalFormat("0.00");

		double actOvertime=0;
		for(int j = 0; j < list.size(); j++){
			List<String> row = new ArrayList<String>();
			XdSettleOvertimeSummary summary = list.get(j);
			row.add(summary.getEmpNum());//0
			row.add(summary.getEmpName());//0
			row.add(summary.getEmpIdnum());//0
			row.add(summary.getProjectName());
			row.add(summary.getApplyDate());
			row.add(summary.getApplyStart());
			row.add(summary.getApplyEnd());
			row.add(summary.getApplyHours()+"");
			String applyType = summary.getApplyType();
			if(applyType.equals("1")){
				row.add("非国定节假日加班");
			}else if(applyType.equals("0")){
				row.add("国定节假日加班");
			}else{
				row.add(applyType);
			}
			row.add(summary.getActStart());
			row.add(summary.getActEnd());
			row.add(summary.getActHours()+"");
			row.add(summary.getRemarks());

			if(summary.getApplyHours()==null ||summary.getApplyHours().equals("")){
				applyOvertime=applyOvertime+Double.valueOf("0");
			}else{
				applyOvertime=applyOvertime+Double.valueOf(summary.getApplyHours());
			}

			if(summary.getActHours()==null ||summary.getActHours().equals("")){
				actOvertime=actOvertime+Double.valueOf("0");
			}else{
				actOvertime=actOvertime+Double.valueOf(summary.getActHours());
			}

			rows.add(row);
		}

		List<String> footFirstRow = new ArrayList<String>();
		footFirstRow.add("合计");
		footFirstRow.add("0");
		footFirstRow.add("人次");
		footFirstRow.add(String.valueOf(list.size()));
		footFirstRow.add("");
		footFirstRow.add("");
		footFirstRow.add("");
		footFirstRow.add(df.format(applyOvertime));
		footFirstRow.add("");
		footFirstRow.add("");
		footFirstRow.add("");
		footFirstRow.add(df.format(actOvertime));
		footFirstRow.add("");
		List<String> footSecondRow = new ArrayList<String>();
		footSecondRow.add("部门签字");
		footSecondRow.add("");
		footSecondRow.add("");
		footSecondRow.add("");
		footSecondRow.add("");
		footSecondRow.add("");
		footSecondRow.add("");
		footSecondRow.add("");
		footSecondRow.add("");
		footSecondRow.add("日期：           年       月      日\t\t\t\t\n");
		footSecondRow.add("");
		footSecondRow.add("");
		footSecondRow.add("");

		List<String> footThirdRow = new ArrayList<String>();
		footThirdRow.add("人力资源部签字");
		footThirdRow.add("");
		footThirdRow.add("");
		footThirdRow.add("");
		footThirdRow.add("日期：           年       月      日\t\t\t\t\n");
		footThirdRow.add("");
		footThirdRow.add("");
		footThirdRow.add("");
		footThirdRow.add("");
		footThirdRow.add("日期：           年       月      日\t\t\t\t\n");
		footThirdRow.add("");
		footThirdRow.add("");
		footThirdRow.add("");
		List<String> footFourRow = new ArrayList<String>();
		footFourRow.add("分管领导签字");
		footFourRow.add("");
		footFourRow.add("");
		footFourRow.add("");
		footFourRow.add("日期：           年       月      日\t\t\t\t\n");
		footFourRow.add("");
		footFourRow.add("");
		footFourRow.add("");
		footFourRow.add("");
		footFourRow.add("日期：           年       月      日\t\t\t\t\n");
		footFourRow.add("");
		footFourRow.add("");
		footFourRow.add("");
		List<String> footFiveRow = new ArrayList<String>();
		footFiveRow.add("总经理签字");
		footFiveRow.add("");
		footFiveRow.add("");
		footFiveRow.add("");
		footFiveRow.add("日期：           年       月      日\t\t\t\t\n");
		footFiveRow.add("");
		footFiveRow.add("");
		footFiveRow.add("");
		footFiveRow.add("");
		footFiveRow.add("日期：           年       月      日\t\t\t\t\n");
		footFiveRow.add("");
		footFiveRow.add("");
		footFiveRow.add("");
		rows.add(footFirstRow);
		rows.add(footSecondRow);
		rows.add(footThirdRow);
		rows.add(footFourRow);
		rows.add(footFiveRow);


		File file = ExcelUtil.overtimeFile(path,rows);
		return file;
	}


	public File exportApportionExcel(String path, String dept, String unitname, String year, String month, String emp_name){
		String sql  = " from "+TABLE_NAME+" o   where 1=1";
	/*	if(StrKit.notBlank(dept)){
			sql = sql + " and o.dept_id='"+ dept+"'";
		}*/

		sql = sql + " order by o.create_date desc,emp_num";


		List<XdSettleOvertimeSummary> list = XdSettleOvertimeSummary.dao.find(" select * "+sql);//查询全部


		List<List<String>> rows = new ArrayList<List<String>>();


		Map<String, Map<String, String>> dictMap = DictMapping.dictMappingValueToName();
		Map<String, String> unit = dictMap.get("unit");

		List<String> first = new ArrayList<String>();
		first.add("部门");
		first.add("单元");
		first.add("加班项目");
		first.add("姓名");
		first.add("身份证号");
		first.add("平时加班");
		first.add("国定加班");



		rows.add(first);
		double applyOvertime=0;

		DecimalFormat df = new DecimalFormat("0.00");
		Map<String,Double> comOTmap=new HashMap<>();
		Map<String,Double> natOTmap=new HashMap<>();
		Map<String,XdSettleOvertimeSummary> map =new HashMap<>();
		double actOvertime=0;
		for(int j = 0; j < list.size(); j++){
			XdSettleOvertimeSummary summary = list.get(j);
			//if(map.get(summary.getEmpName())==null){
				String empName = summary.getEmpName();
				map.put(empName,summary);
				if(summary.getApplyType().equals("1")){
					double comOT = comOTmap.get(empName) == null ? 0 : comOTmap.get(empName);
					comOT+=summary.getActHours()==null?0:Double.valueOf(summary.getActHours());
					comOTmap.put(empName,comOT);
				}else{

					double natOT = natOTmap.get(empName) == null ? 0 : natOTmap.get(empName);
					natOT+=summary.getActHours()==null?0:Double.valueOf(summary.getActHours());
					natOTmap.put(empName,natOT);
					//natOTmap.put(empName,(summary.getActHours()==null?0:Double.valueOf(summary.getActHours())));
				}
		}

		Set<String> empNameSet = map.keySet();
		double sumComOTHours=0;
		double sumNaOTHours=0;

		for (String empName : empNameSet) {
			List<String> row = new ArrayList<String>();
			XdSettleOvertimeSummary settleOvertimeSummary = map.get(empName);

			Double comHours = (comOTmap.get(empName)==null? 0.0 :comOTmap.get(empName));
			Double natHours = (natOTmap.get(empName)==null? 0.0 :natOTmap.get(empName));
			sumComOTHours+=comHours;
			sumNaOTHours+=natHours;
			row.add(settleOvertimeSummary.getDeptName());
			XdEmployee emp = XdEmployee.dao.findFirst("select * from  xd_employee where name='" + empName + "'");
			row.add(emp==null?"":emp.getUnitname());
			row.add(settleOvertimeSummary.getProjectName());
			row.add(empName);
			row.add(emp==null?"":emp.getIdnum());
			row.add(String.valueOf(comHours));
			row.add(String.valueOf(natHours));
			rows.add(row);

		}


		List<String> footFirstRow = new ArrayList<String>();
		footFirstRow.add("合计");
		footFirstRow.add("");
		footFirstRow.add("");
		footFirstRow.add("");
		footFirstRow.add("");
		footFirstRow.add(String.valueOf(sumComOTHours));
		footFirstRow.add(String.valueOf(sumNaOTHours));
		rows.add(footFirstRow);
		File file = ExcelUtil.listToFile(path,rows);
		return file;
	}


}