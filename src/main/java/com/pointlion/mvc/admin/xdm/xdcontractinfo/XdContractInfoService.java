package com.pointlion.mvc.admin.xdm.xdcontractinfo;

import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.enums.XdOperEnum;
import com.pointlion.mvc.common.model.XdContractInfo;
import com.pointlion.mvc.common.model.XdDict;
import com.pointlion.mvc.common.model.XdEmployee;
import com.pointlion.mvc.common.model.XdWorkExper;
import com.pointlion.mvc.common.utils.DateUtil;
import com.pointlion.mvc.common.utils.UuidUtil;
import com.pointlion.mvc.common.utils.XdOperUtil;
import com.pointlion.mvc.common.utils.office.excel.ExcelUtil;
import com.pointlion.plugin.shiro.ShiroKit;
import com.pointlion.plugin.shiro.ext.SimpleUser;
import com.pointlion.util.DictMapping;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class XdContractInfoService{
	public static final XdContractInfoService me = new XdContractInfoService();
	public static final String TABLE_NAME = XdContractInfo.tableName;
	
	/***
	 * query by id
	 */
	public XdContractInfo getById(String id){
		return XdContractInfo.dao.findById(id);
	}
	
	/***
	 * get page
	 */
	public Page<Record> getPage(int pNum,int pSize,String name,String empNum,String startTime,String department){
//		String userId = ShiroKit.getUserId();
		String userOrgId = ShiroKit.getUserOrgId();

		String sql  = "  from  xd_employee  e LEFT JOIN xd_contract_info c on e.name=c.emp_name where 1=1";
		if(!userOrgId.equals("1")){
			sql = sql + " and e.department  ='"+userOrgId+"'";
		}else{
			if(StrKit.notBlank(department)){
				String[] deptSplit = department.split(",");

				String inSql="";
				for (String deptId : deptSplit) {
					inSql=inSql+"'"+deptId+"'"+",";

				}
				inSql=inSql.replaceAll(",$","");

				sql = sql + " and e.department in("+ inSql+")";
			}
		}

		if(StrKit.notBlank(name)){

			sql = sql + " and e.name  like '%"+name+"%'";
//			sql = sql + " and o.create_time>='"+ DateUtil.formatSearchTime(startTime,"0")+"'";
		}

		if(StrKit.notBlank(empNum)){
			sql = sql + " and e.empnum  like '%"+empNum+"%'";
//			sql = sql + " and o.create_time<='"+""+"'";
		}
		if(StrKit.notBlank(startTime)){
			sql = sql + " and c.contractstartdate >= '"+startTime+"'";
		}
		sql = sql + " order by e.empnum, c.contractclauses desc";
		return Db.paginate(pNum, pSize, "select c.id,e.empnum,e.idnum,e.department,e.name,e.age,e.emprelation,c.contractstartdate," +
				"c.contractenddate,c.contractclauses,c.contractnature", sql);
	}
	
	/***
	 * del
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idArr[] = ids.split(",");
    	String names="";
    	for(String id : idArr){
    		XdContractInfo o = me.getById(id);
    		o.delete();
    		XdOperUtil.logSummary(null,o, XdOperEnum.D.name(),o.getClass());
			names=names+o.getEmpName()+",";
    	}
		updateInfos(names.replaceAll(",$",""));
	}


	public  void updateInfos(String userNames){

		String[] nameArr = userNames.split(",");
		for (String name : nameArr) {
			//更新编号
			List<XdContractInfo> contractInfoList = XdContractInfo.dao.find("select * from xd_contract_info where emp_name='" + name + "' order by contractstartdate desc");
			for (int i = 0; i <contractInfoList.size(); i++) {
				XdContractInfo contractInfo = contractInfoList.get(i);
				contractInfo.setContractclauses(contractInfoList.size()-i);
				contractInfo.update();
			}
			//更新员工合同信息

			XdContractInfo contractInfo = contractInfoList.get(0);

			XdEmployee employee = XdEmployee.dao.findFirst("select * from  xd_employee where name='"+name+"'");
			XdEmployee oldEmp =new XdEmployee();
			try {
				BeanUtils.copyProperties(oldEmp,employee);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			employee.setContractstartdate(contractInfo.getContractstartdate());
			employee.setContractenddate(contractInfo.getContractenddate());
			employee.setContractclauses(contractInfo.getContractclauses());
			employee.setContractnature(contractInfo.getContractnature());
			employee.update();

			XdOperUtil.logSummary(employee,oldEmp, XdOperEnum.U.name(),employee.getClass());

		}






	}

	public Map<String,Object> importExcel(List<List<String>> list) throws SQLException {
		final List<String> message = new ArrayList<String>();
		final Map<String,Object> result = new HashMap<String,Object>();
		Map<String, Map<String, String>> dictMapping= DictMapping.dictMapping();
		Map<String, String> eudMap = dictMapping.get("edu");
		Map<String, String> relationMap = dictMapping.get("empRelation");
		Db.tx(new IAtom() {
			@Override
			public boolean run() throws SQLException {
				try{
					if(list.size()>2){
						SimpleUser user = ShiroKit.getLoginUser();
						String time = DateUtil.getCurrentTime();
						for(int i = 2;i<list.size();i++){//从第二行开始取
							List<String> eduStr = list.get(i);
							XdContractInfo contractInfo=new XdContractInfo();
							contractInfo.setId(UuidUtil.getUUID());
							contractInfo.setCtime(time);
							contractInfo.setCuser(user.getId());
							if(eduStr.get(3)==null ||"".equals(eduStr.get(3).trim())){
								continue;
							}
							XdEmployee employee = XdEmployee.dao.findFirst("select * from xd_employee where name ='" + eduStr.get(3) + "'");
							contractInfo.setEid(employee==null?"":employee.getId());
							contractInfo.setEmpName(eduStr.get(3));

							boolean flag=false;
							int j=11;
							while (true){
								int k = (j-10) % 4;
								if(j>=list.get(0).size() ||(k==1 && "".equals(eduStr.get(j)))){
									flag=true;
									break;
								}else{
									switch (k){
										case 1:
											contractInfo.setContractstartdate(eduStr.get(j)==null?"":eduStr.get(j));
											break;
										case 2:
											contractInfo.setContractenddate(eduStr.get(j)==null?"":eduStr.get(j));
											break;
										case 3:
											contractInfo.setContractclauses(Integer.valueOf(eduStr.get(j)==null?"0":eduStr.get(j)));
											break;
										case 0:
											contractInfo.setContractnature(eduStr.get(j)==null?"": eduStr.get(j));
											contractInfo.setId(UuidUtil.getUUID());
											contractInfo.save();
											break;
									}

								}
								j++;
							}

							if (flag) {
								continue;
							}


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


	public File exportContractExcel(String path, String name, String empNum, String startTime, String department){
		String sql  = " from "+TABLE_NAME+" o  left join  xd_employee  e on  o.emp_name=e.name where 1=1";

		if (StrKit.notBlank(name)) {
			sql = sql + " and o.name like '%"+name +"%'";
		}

		if (StrKit.notBlank(empNum)) {
			sql = sql + " and e.empnum like '%"+empNum +"%'";
		}
		if (StrKit.notBlank(startTime)) {
			sql = sql + " and o.contractstartdate like '%"+startTime+"%'";
		}
		String userOrgId = ShiroKit.getUserOrgId();
		if(!userOrgId.equals("1")){
			sql = sql + " and e.department = '"+userOrgId+"'";
		}else{
			if( department!=null && !department.equals("null") &&StrKit.notBlank(department)){
				String[] deptSplit = department.split(",");

				String inSql="";
				for (String deptId : deptSplit) {
					inSql=inSql+"'"+deptId+"'"+",";

				}
				inSql=inSql.replaceAll(",$","");

				sql = sql + " and e.department in("+ inSql+")";
			}
		}

		 sql = sql + " order by e.empnum ,o.contractclauses";



		List<XdContractInfo> listContract = XdContractInfo.dao.find(" select o.emp_name,o.contractstartdate,o.contractenddate,o.contractclauses,o.contractnature ,e.empnum as backup1   "+sql);//查询全部
//		List<XdContractInfo> listContract =new ArrayList<>();
		Map<String,XdEmployee> mapObj=new HashMap<>();

			/*String inSql="'0',";
			for (XdEmployee xdEmployee : list) {
				inSql=inSql+"'"+xdEmployee.getId()+"'"+",";
				mapObj.put(xdEmployee.getId(),xdEmployee);
			}
			inSql=inSql.replaceAll(",$","");
			listContract = XdContractInfo.dao.find("select * from xd_contract_info where  eid in (" + inSql + ")  order by eid,contractclauses ");
*/
		Map<String,Integer> mapCount=new HashMap<>();
		Map<String,List<XdContractInfo>> mapObjList=new HashMap<>();
		for (XdContractInfo contractInfo : listContract) {
			if(mapCount.get(contractInfo.getBackup1())==null){
				mapCount.put(contractInfo.getBackup1(),1);
				List<XdContractInfo> conInfoList=new ArrayList<>();
				conInfoList.add(contractInfo);
				mapObjList.put(contractInfo.getBackup1(),conInfoList);
			}else{
				Integer integer = mapCount.get(contractInfo.getBackup1());
				mapCount.put(contractInfo.getBackup1(),integer+1);
				List<XdContractInfo> conList = mapObjList.get(contractInfo.getBackup1());
				conList.add(contractInfo);
				mapObjList.put(contractInfo.getBackup1(),conList );
			}
		}
		int maxLen=0;
		if(listContract.size()>0){
			Collection<Integer> values = mapCount.values();
			Stream<Integer> sorted = values.stream().sorted((o1, o2) ->  -o1.compareTo(o2));
			maxLen =sorted.findFirst().get();
		}


		String[] num = {"一", "二", "三", "四", "五", "六", "七", "八", "九"};
		List<List<String>> rows = new ArrayList<List<String>>();
		List<String> first = new ArrayList<String>();
		List<String> second = new ArrayList<String>();
		first.add("工号");
		first.add("身份证号");
		first.add("部门/条线");
		first.add("姓名");
		first.add("年龄");
		first.add("员工性质");
		first.add("入职日期");
		second.add("");
		second.add("");
		second.add("");
		second.add("");
		second.add("");
		second.add("");
		second.add("");

		for (int i = 1; i <=maxLen ; i++) {
			if(i==1){
				first.add("最近一份合同");
				second.add("合同起始日期");
				second.add("合同结束日期");
				second.add("期数");
				second.add("合同性质");
			}
			first.add(num[i-1]);
			second.add("合同起始日期"+i);
			second.add("合同结束日期"+i);
			second.add("期数"+i);
			second.add("合同性质"+i);
		}

		rows.add(first);
		rows.add(second);

		if(listContract.size()>0) {
			Collection<List<XdContractInfo>> collContract = mapObjList.values();
//			Stream<List<XdContractInfo>> sorted1 = collContract.stream().sorted((o1, o2) -> -(o1.size() - o2.size()));
			Map<String, String> orgMap = DictMapping.orgMapping("0");
			Map<String, Map<String, String>> stringMapMap = DictMapping.dictMappingValueToName();
			Map<String, String> relation = stringMapMap.get("empRelation");

			Set<String> set = mapObjList.keySet();
			Stream<String> sortEmpNum = set.stream().sorted((String o1, String o2) -> {
				return o1.compareTo(o2);
			});

			int finalMaxLen = maxLen;
			sortEmpNum.forEach(s -> {
				List<XdContractInfo> contractInfoList = mapObjList.get(s);
				List<String> row = new ArrayList<String>();
				XdContractInfo contract = contractInfoList.get(contractInfoList.size() - 1);
				XdEmployee xdEmployee = XdEmployee.dao.findFirst("select * from xd_employee where name='" + contract.getEmpName() + "'");
				row.add(xdEmployee == null ? "" : xdEmployee.getEmpnum());
				row.add(xdEmployee == null ? "" : xdEmployee.getIdnum());
				row.add(xdEmployee.getDepartment() == null ? "" : orgMap.get(xdEmployee.getDepartment()));
				row.add(xdEmployee.getName());
				row.add(xdEmployee == null ? "" : String.valueOf(xdEmployee.getAge()));
				row.add(xdEmployee == null ? "" : relation.get(xdEmployee.getEmprelation()));
				row.add(xdEmployee == null ? "" : xdEmployee.getEntrytime());
				row.add(contract.getContractstartdate());
				row.add(contract.getContractenddate());
				row.add(String.valueOf(contract.getContractclauses()));
				row.add(contract.getContractnature());
				for (int i = 0; i < contractInfoList.size(); i++) {
					contract = contractInfoList.get(i);
					row.add(contract.getContractstartdate());
					row.add(contract.getContractenddate());
					row.add(String.valueOf(contract.getContractclauses()));
					row.add(contract.getContractnature());
				}
				for (int i = 0; i < (finalMaxLen - contractInfoList.size()); i++) {
					row.add("");
					row.add("");
					row.add("");
					row.add("");
				}

				rows.add(row);
			});
		}


/*		sorted1.forEach( xdContractInfos->{
			List<String> row = new ArrayList<String>();
			XdContractInfo contract = xdContractInfos.get(xdContractInfos.size() - 1);
			XdEmployee xdEmployee = mapObj.get(contract.getEid());
			row.add(xdEmployee.getEmpnum()
			);
			row.add(xdEmployee.getIdnum());
			row.add(xdEmployee.getDepartment()==null?"":orgMap.get(xdEmployee.getDepartment()));
			row.add(xdEmployee.getName());
			row.add(String.valueOf(xdEmployee.getAge()));
			row.add(xdEmployee.getEmprelation());
			row.add(xdEmployee.getEntrytime());
			row.add(contract.getContractstartdate());
			row.add(contract.getContractenddate());
			row.add(String.valueOf(contract.getContractclauses()));
			row.add(contract.getContractnature());
			for (int i = 0; i < xdContractInfos.size()-1; i++) {
				contract = xdContractInfos.get(i);
				row.add(contract.getContractstartdate());
				row.add(contract.getContractenddate());
				row.add(String.valueOf(contract.getContractclauses()));
				row.add(contract.getContractnature());
			}
			for (int i = 0; i < (maxLen - xdContractInfos.size()); i++) {
				row.add("");
				row.add("");
				row.add("");
				row.add("");
			}

			rows.add(row);

		});*/

		File file = ExcelUtil.conTractFile(path,rows);
		return file;
	}

}