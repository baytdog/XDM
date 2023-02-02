package com.pointlion.mvc.admin.xdm.xdrcpsummary;

import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.common.model.SysOrg;
import com.pointlion.mvc.common.model.XdRcpSummary;
import com.pointlion.mvc.common.model.XdSettleOvertimeSummary;
import com.pointlion.mvc.common.utils.office.excel.ExcelUtil;
import com.pointlion.plugin.shiro.ShiroKit;
import com.pointlion.mvc.common.model.SysRoleOrg;
import com.pointlion.mvc.common.utils.DateUtil;
import com.pointlion.plugin.shiro.ext.SimpleUser;
import com.pointlion.util.DictMapping;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XdRcpSummaryService{
	public static final XdRcpSummaryService me = new XdRcpSummaryService();
	public static final String TABLE_NAME = XdRcpSummary.tableName;
	
	/***
	 * query by id
	 */
	public XdRcpSummary getById(String id){
		return XdRcpSummary.dao.findById(id);
	}
	
	/***
	 * get page
	 */
	public Page<Record> getPage(int pnum,int psize,String dept,String unit,String project,String empName,String workstation){
		String sql  = " from "+TABLE_NAME+" o    where 1=1";
		if(StrKit.notBlank(dept)){
			sql = sql + " and o.dept_value='"+dept+"'";
		}
		if(StrKit.notBlank(unit)){
			sql = sql + " and o.unit_value='"+unit+"'";
		}
		if(StrKit.notBlank(project)){
			sql = sql + " and o.project_value='"+project+"'";
		}
		if(StrKit.notBlank(empName)){
			sql = sql + " and o.emp_name like '%"+empName+"%'";
		}
		if(StrKit.notBlank(workstation)){
			sql = sql + " and o.wor_station like '%"+workstation.trim()+"%'";
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
    		XdRcpSummary o = me.getById(id);
    		o.delete();
    	}
	}
	/**
	 * @Method importExcel
	 * @param list:
	 * @Date 2023/1/15 16:44
	 * @Description  导入excle数据
	 * @Author king
	 * @Version  1.0
	 * @Return java.util.Map<java.lang.String,java.lang.Object>
	 */

	public Map<String,Object> importExcel(List<List<String>> list) throws SQLException {
		final List<String> message = new ArrayList<String>();
		final Map<String,Object> result = new HashMap<String,Object>();
		Map<String, String> orgMapping = DictMapping.orgMapping("1");
		Map<String, String> projectsMap = DictMapping.projectsMapping();
		Map<String, Map<String, String>> stringMapMap = DictMapping.dictMapping();
		Map<String, String> unitMap = stringMapMap.get("unit");

		Db.tx(new IAtom() {
			@Override
			public boolean run() throws SQLException {
				try{
					if(list.size()>4){
						String time = DateUtil.getCurrentTime();
						List<String> ny = list.get(2);
						String  nyDate = ny.get(10);
						String[] nyArr = nyDate.replaceAll("年","-").replaceAll("月","").split("-");
						String n=nyArr[0];
						String y=nyArr[1];
						if(y.length()<2){
							y="0"+y;
						}
						nyDate=n+"-"+y;
						//String dept_value = orgMapping.get(deptName);
						for(int i = 4;i<list.size();i++){
							List<String> rcpList = list.get(i);

							if(rcpList.get(0)==null ||rcpList.get(0).equals("")){
								continue;
							}
							XdRcpSummary rcpSummary=new XdRcpSummary();
							String deptName = rcpList.get(0);
							String deptValue = orgMapping.get(deptName);
							rcpSummary.setDeptName(deptName);
							if(deptValue==null||deptValue.equals("")){
								rcpSummary.setDeptValue("");
							}else{
								rcpSummary.setDeptValue(deptValue);
							}
							String unitName = rcpList.get(1);
							String unitValue = unitMap.get(unitName);
							rcpSummary.setUnitName(unitName);
							rcpSummary.setUnitValue(unitValue);

							String projectName = rcpList.get(2);
							String projectValue = projectsMap.get(projectName);
							rcpSummary.setProjectName(projectName);
							rcpSummary.setProjectValue(projectValue);
							rcpSummary.setEmpName(rcpList.get(3));
							rcpSummary.setWorStation(rcpList.get(4));
							rcpSummary.setShfitName(rcpList.get(5));
							rcpSummary.setWorkHours(rcpList.get(6));
							rcpSummary.setRcpDays(rcpList.get(7));
							rcpSummary.setWorkDays(rcpList.get(8));
							rcpSummary.setRcpStandard(rcpList.get(9));
							rcpSummary.setRental(rcpList.get(10));
							rcpSummary.setRemarks(rcpList.get(11));
							rcpSummary.setRcpYear(n);
							rcpSummary.setRcpMonth(y);
							rcpSummary.setRcpMonthYear(nyDate);
							rcpSummary.setCreateDate(DateUtil.getCurrentTime());
							rcpSummary.setCreateUser(ShiroKit.getUserId());
							rcpSummary.save();
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
	 * @Date 2023/1/15 16:44
	 * @Description  导出excle
	 * @Author king
	 * @Version  1.0
	 * @Return java.io.File
	 */
	public File exportExcel(String path, String dept, String unitname, String year, String month, String emp_name){
		String sql  = " from "+TABLE_NAME+" o   where 1=1";
		if(StrKit.notBlank(dept)){
			sql = sql + " and o.dept_id='"+ dept+"'";
		}

		sql = sql + " order by o.create_date desc";


		List<XdRcpSummary> list = XdRcpSummary.dao.find(" select * "+sql);//查询全部


		Map<String, Map<String, String>> dictMap = DictMapping.dictMappingValueToName();
	/*	Map<String, String> projectMap = DictMapping.projectsMappingValueToName();
		Map<String, String> orgMap = DictMapping.orgMapping("0");
		String departName = orgMap.get(dept)==null?"":orgMap.get(dept);
		Map<String, String> unit = dictMap.get("unit");*/
		List<List<String>> rows = new ArrayList<List<String>>();
		List<String> titleFirstRow=new ArrayList<String>();
		List<String> titleSecondRow=new ArrayList<String>();
		List<String> titleThirdRow=new ArrayList<String>();
		List<String> titleFourRow=new ArrayList<String>();
		String ny="";
		if(StrKit.notBlank(year)){
			ny=year+"年";
		}
		if(StrKit.notBlank(month)){
			ny=ny+month+"月";
		}
		for (int i =1; i <=12; i++) {
			if(i==12){
				titleFirstRow.add("上海东方欣迪商务服务有限公司");
				titleSecondRow.add("RCP项目津贴申请表");
				titleThirdRow.add("");

			}else if(i==11){
				titleFirstRow.add("");
				titleSecondRow.add("");
				titleThirdRow.add(ny);
			}else{
				titleFirstRow.add("");
				titleSecondRow.add("");
				titleThirdRow.add("");
			}

		}
		rows.add(titleFirstRow);
		rows.add(titleSecondRow);
		rows.add(titleThirdRow);

		List<String> first = new ArrayList<String>();
		first.add("部门");
		first.add("单元");//0
		first.add("项目");
		first.add("姓名");//1
		first.add("岗位");//2
		first.add("班次");//2
		first.add("时间");//2
		first.add("津贴\n" +
				"天数");//2
		first.add("上班\n" +
				"天数");//2
		first.add("津贴\n" +
				"标准");//2
		first.add("总额");//3
		first.add("备注");//3

		rows.add(first);
		double rcpdays=0;
		DecimalFormat df = new DecimalFormat("0.00");
		double rental=0;
		for(int j = 0; j < list.size(); j++){
			List<String> row = new ArrayList<String>();
			XdRcpSummary summary = list.get(j);
			row.add(summary.getDeptName());//0
			row.add(summary.getUnitName());//0
			row.add(summary.getProjectName());
			row.add(summary.getEmpName());
			row.add(summary.getWorStation());
			row.add(summary.getShfitName());
			row.add(summary.getWorkHours());
			row.add(summary.getRcpDays());
			row.add(summary.getWorkDays());
			row.add(summary.getRcpStandard());
			row.add(summary.getRental());
			row.add(summary.getRemarks());

			if(summary.getRcpDays()==null ||summary.getRcpDays().equals("")){
				rcpdays=rcpdays+Double.valueOf("0");
			}else{
				rcpdays=rcpdays+Double.valueOf(summary.getRcpDays());
			}

			if(summary.getRental()==null ||summary.getRental().equals("")){
				rental=rental+Double.valueOf("0");
			}else{
				rental=rental+Double.valueOf(summary.getRental());
			}
			rows.add(row);
		}

		List<String> footFirstRow = new ArrayList<String>();
		footFirstRow.add("合计");
		footFirstRow.add(String.valueOf(list.size()));
		footFirstRow.add("人次");
		footFirstRow.add("");
		footFirstRow.add("");
		footFirstRow.add("");
		footFirstRow.add("");
		footFirstRow.add(df.format(rcpdays));
		footFirstRow.add("");
		footFirstRow.add("");
		footFirstRow.add(df.format(rental));
		footFirstRow.add("");
		List<String> footSecondRow = new ArrayList<String>();
		footSecondRow.add("制表人签字");
		footSecondRow.add("");
		footSecondRow.add("");
		footSecondRow.add("日期：           年       月      日\t\t\t\t\n");
		footSecondRow.add("");
		footSecondRow.add("");
		footSecondRow.add("");
		footSecondRow.add("");
		footSecondRow.add("");
		footSecondRow.add("");
		footSecondRow.add("");
		footSecondRow.add("");

		List<String> footThirdRow = new ArrayList<String>();
		footThirdRow.add("部门经理签字");
		footThirdRow.add("");
		footThirdRow.add("");
		footThirdRow.add("日期：           年       月      日\t\t\t\t\n");
		footThirdRow.add("");
		footThirdRow.add("");
		footThirdRow.add("");
		footThirdRow.add("");
		footThirdRow.add("");
		footThirdRow.add("");
		footThirdRow.add("");
		footThirdRow.add("");
		List<String> footFourRow = new ArrayList<String>();
		footFourRow.add("人力资源部签字");
		footFourRow.add("");
		footFourRow.add("");
		footFourRow.add("日期：           年       月      日\t\t\t\t\n");
		footFourRow.add("");
		footFourRow.add("");
		footFourRow.add("");
		footFourRow.add("");
		footFourRow.add("");
		footFourRow.add("");
		footFourRow.add("");
		footFourRow.add("");
		List<String> footFiveRow = new ArrayList<String>();
		footFiveRow.add("分管领导签字");
		footFiveRow.add("");
		footFiveRow.add("");
		footFiveRow.add("日期：           年       月      日\t\t\t\t\n");
		footFiveRow.add("");
		footFiveRow.add("");
		footFiveRow.add("");
		footFiveRow.add("");
		footFiveRow.add("");
		footFiveRow.add("");
		footFiveRow.add("");
		footFiveRow.add("");
		List<String> footSixRow = new ArrayList<String>();
		footSixRow.add("总经理签字");
		footSixRow.add("");
		footSixRow.add("");
		footSixRow.add("日期：           年       月      日\t\t\t\t\n");
		footSixRow.add("");
		footSixRow.add("");
		footSixRow.add("");
		footSixRow.add("");
		footSixRow.add("");
		footSixRow.add("");
		footSixRow.add("");
		footSixRow.add("");
		rows.add(footFirstRow);
		rows.add(footSecondRow);
		rows.add(footThirdRow);
		rows.add(footFourRow);
		rows.add(footFiveRow);
		rows.add(footSixRow);


		File file = ExcelUtil.rcpFile(path,rows);
		return file;
	}

}