package com.pointlion.mvc.admin.xdm.xdovertimesummary;

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
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XdOvertimeSummaryService{
	public static final XdOvertimeSummaryService me = new XdOvertimeSummaryService();
	public static final String TABLE_NAME = XdOvertimeSummary.tableName;
	
	/***
	 * query by id
	 */
	public XdOvertimeSummary getById(String id){
		return XdOvertimeSummary.dao.findById(id);
	}
	
	/***
	 * get page
	 */
	public Page<Record> getPage(int pnum,int psize,String dept,String project,String emp_name,String emp_num,String apply_date,String overtimeType){
		String sql  = " from "+TABLE_NAME+" o   where 1=1";
		if(StrKit.notBlank(dept)){
			sql = sql + " and o.dept_id='"+dept+"'";
		}
		if(StrKit.notBlank(project)){
			sql = sql + " and o.project_id='"+project+"'";
		}
		if(StrKit.notBlank(emp_name)){
			sql = sql + " and o.emp_name like '%"+emp_name+"%'";
		}
		if(StrKit.notBlank(emp_num)){
			sql = sql + " and o.emp_num like '%"+emp_num+"%'";
		}

		if(StrKit.notBlank(overtimeType)){
			sql = sql + " and o.apply_type='"+overtimeType+"'";
		}
		if(StrKit.notBlank(apply_date)){
			String[] split = apply_date.split("-");
			String month=split[1];
			String day=split[2];
			if(month.startsWith("0")){
				month=month.replaceAll("0","");
			}
			if(day.startsWith("0")){
				day=day.replaceAll("0","");
			}
			String ymd=split[0]+"/"+month+"/"+day;
			sql = sql + " and o.apply_date='"+ymd+"'";
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
    		XdOvertimeSummary o = me.getById(id);
    		o.delete();
    	}
	}

	public Map<String,Object> importExcel(List<List<String>> list) throws SQLException {
		final List<String> message = new ArrayList<String>();
		final Map<String,Object> result = new HashMap<String,Object>();
		Map<String, String> orgMapping = DictMapping.orgMapping("1");
		/*Map<String, Map<String, String>> stringMapMap = DictMapping.dictMapping();
		Map<String, String> unitMap = stringMapMap.get("unit");*/
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
							XdOvertimeSummary overtimeSummary=new XdOvertimeSummary();
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
							overtimeSummary.setApplyHours(overtimeList.get(7));
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
							overtimeSummary.setActHours(overtimeList.get(11));
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


	public File exportExcel(String path, String dept, String unitname, String year, String month, String emp_name){
		String sql  = " from "+TABLE_NAME+" o   where 1=1";
		if(StrKit.notBlank(dept)){
			sql = sql + " and o.dept_id='"+ dept+"'";
		}
	/*	if(StrKit.notBlank(unitname)){
			sql = sql + " and o.unit_value='"+ unitname+"'";
		}*/
		/*if(StrKit.notBlank(year)){
			sql = sql + " and o.schedule_year='"+ year+"'";
		}
		if(StrKit.notBlank(month)){
			sql = sql + " and o.schedule_month='"+ month+"'";
		}
		if(StrKit.notBlank(emp_name)){
			sql = sql + " and o.emp_name like'%"+emp_name+"%'";
		}*/

		sql = sql + " order by o.create_date desc,emp_num";


		List<XdOvertimeSummary> list = XdOvertimeSummary.dao.find(" select * "+sql);//查询全部


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
			XdOvertimeSummary summary = list.get(j);
			row.add(summary.getEmpNum());//0
			row.add(summary.getEmpName());//0
			row.add(summary.getEmpIdnum());//0
			row.add(summary.getProjectName());
			row.add(summary.getApplyDate());
			row.add(summary.getApplyStart());
			row.add(summary.getApplyEnd());
			row.add(summary.getApplyHours());
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
			row.add(summary.getActHours());
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

}