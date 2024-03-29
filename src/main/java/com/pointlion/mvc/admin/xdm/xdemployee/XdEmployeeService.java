package com.pointlion.mvc.admin.xdm.xdemployee;

import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.enums.XdOperEnum;
import com.pointlion.mvc.common.model.*;
import com.pointlion.mvc.common.utils.DateUtil;
import com.pointlion.mvc.common.utils.JSONUtil;
import com.pointlion.mvc.common.utils.UuidUtil;
import com.pointlion.mvc.common.utils.XdOperUtil;
import com.pointlion.mvc.common.utils.office.excel.ExcelUtil;
import com.pointlion.plugin.shiro.ShiroKit;
import com.pointlion.plugin.shiro.ext.SimpleUser;
import com.pointlion.util.DictMapping;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Stream;

public class XdEmployeeService{
	public static final XdEmployeeService me = new XdEmployeeService();
	public static final String TABLE_NAME = XdEmployee.tableName;
	
	/***
	 * query by id
	 */
	public XdEmployee getById(String id){
		return XdEmployee.dao.findById(id);
	}
	
	/***
	 * get page
	 */
	public Page<Record> getPage(int pNum,int pSize,String sort,String sortOrder,String name,String empNum,String empRelation
			,String department,String unitName,String costItem,String inductionStatus
			,String departTime,String checked,String selectedName,String position, String workstation){
		String userOrgId = ShiroKit.getUserOrgId();

		String sql  = " from "+TABLE_NAME+" o where 1=1";
		if(!"1".equals(userOrgId)){
			//sql=sql+" and cuser='"+ShiroKit.getUserId()+"'";

			SysUser otherUser = SysUser.dao.findById(ShiroKit.getUserId());
			String instr="";
			if(otherUser.getOperProject()!=null && !otherUser.getOperProject().equals("")){
				String[] split = otherUser.getOperProject().split(",");
				for (String s : split) {
					instr=instr+",'"+s+"'";
				}
				instr=instr.replaceAll("^,","");

				sql=sql+" and costitem in ("+instr+")";
			}
			sql = sql + " and o.department = '"+ userOrgId+"'";
		}else {
			if(StrKit.notBlank(department)){
				String[] deptSplit = department.split(",");

				String inSql="";
				for (String deptId : deptSplit) {
					inSql=inSql+"'"+deptId+"'"+",";

				}
				inSql=inSql.replaceAll(",$","");

				sql = sql + " and o.department in("+ inSql+")";
			}
		}

		if(StrKit.notBlank(costItem)){
			sql = sql + " and o.costitem = '"+ costItem+"'";
		}
		if(StrKit.notBlank(name)){
			sql = sql + " and o.name like '%"+ name+"%'";
		}
		if(StrKit.notBlank(empNum)){
			sql = sql + " and o.empnum like '%"+ empNum+"%'";
		}
		if(StrKit.notBlank(position)){
			sql = sql + " and o.position='"+ position+"'";
		}
		if(StrKit.notBlank(workstation)){
			sql = sql + " and o.workstation='"+ workstation+"'";
		}

		if(StrKit.notBlank(empRelation)){
			sql = sql + " and o.emprelation like '%"+ empRelation+"%'";
		}

		if(StrKit.notBlank(unitName)){
			sql = sql + " and o.unitname = '"+ unitName+"'";
		}

		if(StrKit.notBlank(inductionStatus)){
			sql=sql+ " and o.inductionstatus='"+inductionStatus+"'";
			/*if(inductionStatus.equals("1")){
				sql=sql+ " and o.status='0'";
			}else{
				sql=sql+ " and o.status='1'";
			}*/
		}
		if(StrKit.notBlank(departTime)){
			sql=sql+ " and o.departime like '"+departTime+"%'";
		}


		if("true".equals(checked) && !"".equals(selectedName)){
			String[] namesArr = selectedName.split(",");
			String insql="";
			for (String names : namesArr) {
				insql+="'"+names+"',";
			}
			sql  = " from "+TABLE_NAME+" o where 1=1 and name in ("+
					insql.replaceAll(",$","")
					+")";
		}
		/*if(StrKit.notBlank(inductionStatus)||StrKit.notBlank(departTime)){
//			sql = sql + " order by o.departime desc";
			sql = sql + " order by o.empnum ,o.departime desc";
		}else{
			sql = sql + " order by o.empnum ";
		}
*/
		sql=sql+" order by "+sort+" " +sortOrder;

		return Db.paginate(pNum, pSize, " select * ", sql);
	}
	public Page<Record> getPage(int pnum,int psize,String warnType){
		String sql  ="";
		if(warnType.equals("1")){
			sql  = " from "+TABLE_NAME+" o  where o.contractenddate  is not null and (TO_DAYS(str_to_date(o.contractenddate, '%Y-%m-%d')) - TO_DAYS(now()))<30";
			sql = sql + " order by o.ctime desc";
			return Db.paginate(pnum, psize, " select o.*, TO_DAYS(str_to_date(o.contractenddate, '%Y-%m-%d')) - TO_DAYS(now()) diffdate,o.contractenddate endtime", sql);
		}else if(warnType.equals("2")){
			sql  = " from "+TABLE_NAME+" o  where o.contractenddate  is  null and (TO_DAYS(str_to_date(o.positivedate, '%Y-%m-%d')) - TO_DAYS(now()))<30";
			sql = sql + " order by o.ctime desc";
			return Db.paginate(pnum, psize, " select o.*, TO_DAYS(str_to_date(o.positivedate, '%Y-%m-%d')) - TO_DAYS(now()) diffdate ,o.positivedate endtime", sql);

		}else{
			sql  = " from "+TABLE_NAME+" o  where o.retiretime  is not null and (TO_DAYS(str_to_date(o.retiretime, '%Y-%m-%d')) - TO_DAYS(now()))<60";
			sql = sql + " order by o.ctime desc";
			return Db.paginate(pnum, psize, " select o.*, TO_DAYS(str_to_date(o.retiretime, '%Y-%m-%d')) - TO_DAYS(now()) diffdate ,o.retiretime endtime", sql);
		}


	}
	/***
	 * del
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idArr[] = ids.split(",");
		String userOrgId = ShiroKit.getUserOrgId();

		for(String id : idArr){
			XdOperUtil.queryLastVersion(id);
			XdEmployee o = me.getById(id);

			Db.delete("delete from xd_contract_info where eid='"+id+"'");
			o.setBackup1("D");
			o.update();
			if("1".equals(userOrgId)){
				o.delete();
			}else{
				XdOperUtil.insertEmpoloyeeSteps(o,"","1","","","D","WA");
			}

			XdOperUtil.logSummary(id,o,XdOperEnum.D.name(),XdOperEnum.WAITAPPRO.name(),0);

			List<XdWorkExper> workExperList = XdWorkExper.dao.find("select * from xd_work_exper where eid='" + id + "'");
			for (XdWorkExper workExper : workExperList) {
				if("1".equals(userOrgId)){
					workExper.delete();
				}
				XdOperUtil.logSummary(id,workExper,XdOperEnum.D.name(),XdOperEnum.WAITAPPRO.name(),0);
			}
			List<XdEdutrain> edutrainList = XdEdutrain.dao.find("select * from xd_edutrain where eid='" + id + "'");
			for (XdEdutrain xdEdutrain : edutrainList) {
				if("1".equals(userOrgId)){
					xdEdutrain.delete();
				}
				XdOperUtil.logSummary(id,xdEdutrain,XdOperEnum.D.name(),XdOperEnum.WAITAPPRO.name(),0);
			}
		}
	}

	/*@Before(Tx.class)*/
	public void modifyObj(XdEmployee newEmp,XdEmployee oldEmp,List<XdEdutrain> gridList1,List<XdWorkExper> gridList2){
		boolean rs ="1".equals(ShiroKit.getUserOrgId());
		String summaryStatus=XdOperEnum.WAITAPPRO.name();
		if (rs) {
			summaryStatus=XdOperEnum.UNAPPRO.name();
		}

		boolean flag=false;
		String employeeChanges = XdOperUtil.getChangedMetheds(newEmp, oldEmp);
		employeeChanges = employeeChanges.replaceAll("--$","");
		List<XdOplogDetail> list =new ArrayList<>();
		List<XdOplogSummary> summaryList =new ArrayList<>();
		if(!"".equals(employeeChanges)){
			String lid=UuidUtil.getUUID();
			String[] empCArray = employeeChanges.split("--");
			for (String change : empCArray) {
				change="{"+change+"}";
				XdOplogDetail logDetail = JSONUtil.jsonToBean(change, XdOplogDetail.class);
				logDetail.setRsid(lid);
				if(!"salary".equals(logDetail.getFieldName()) && !"workstation".equals(logDetail.getFieldName())){
					list.add(logDetail);
				}


				if(rs){
					if("salary".equals(logDetail.getFieldName())){
						String salaryRecord = (newEmp.getSaladjrecord()==null?"":newEmp.getSaladjrecord()+"\t" )+ "原工资: " + logDetail.getOldValue() + " - " + "最新工资: " + logDetail.getNewValue();
						//salaryRecord.replaceAll("^\n","");
						newEmp.setSaladjrecord(salaryRecord);
					}
					if("contractclauses".equals(logDetail.getFieldName())){
						XdContractInfo contract =new XdContractInfo();
						contract.setId(UuidUtil.getUUID());
						contract.setEid(newEmp.getId());
						contract.setContractstartdate(newEmp.getContractstartdate());
						contract.setContractenddate(newEmp.getContractenddate());
						contract.setContractclauses(newEmp.getContractclauses());
						contract.setContractnature(newEmp.getContractnature());
						contract.setCtime(DateUtil.getCurrentTime());
						contract.setCuser(ShiroKit.getUserId());
						contract.save();
					}
					if("workstation".equals(logDetail.getFieldName())){
						String workRecord = (newEmp.getChrecord()==null?"":newEmp.getChrecord()+"\t") + "原岗位: " + logDetail.getOldValue() + "-" + "最新岗位: " + logDetail.getNewValue();
						//workRecord.replaceAll("^\n","");
						newEmp.setChrecord(workRecord);
					}
					if("inductionstatus".equals(logDetail.getFieldName())){
						XdOperUtil.updateEmpCert(newEmp);
					}
				}
			}
			flag=true;
			summaryList.add(XdOperUtil.logSummary(lid,oldEmp.getId(),newEmp,oldEmp,XdOperEnum.U.name(),summaryStatus));
			if(rs){


				String idnum = newEmp.getIdnum();
				if(idnum.length()==15){
					String year ="19"+ idnum.substring(6,8);
					String month = idnum.substring(8,10);
					String days = idnum.substring(10,12);
					int lastIndex = Integer.valueOf(idnum.substring(14));
					newEmp.setGender(String.valueOf(lastIndex%2));
					newEmp.setBirthday(year+"-"+month+"-"+days);
					LocalDate birthday = LocalDate.of(Integer.valueOf(year), Integer.valueOf(month), Integer.valueOf(days));
					long minusDays = LocalDate.now().toEpochDay() - birthday.toEpochDay();
					int age = (int) (minusDays / 365);
					newEmp.setAge(age);

				}else{
					String year = idnum.substring(6,10);
					String month = idnum.substring(10,12);
					String days = idnum.substring(12,14);
					int lastIndex = Integer.valueOf(idnum.substring(17));
					newEmp.setGender(String.valueOf(lastIndex%2));
					newEmp.setBirthday(year+"-"+month+"-"+days);

					LocalDate birthday = LocalDate.of(Integer.valueOf(year), Integer.valueOf(month), Integer.valueOf(days));
					long minusDays = LocalDate.now().toEpochDay() - birthday.toEpochDay();
					int age = (int) (minusDays / 365);
					newEmp.setAge(age);
				}

				newEmp.update();
			}
		}


		List<XdEdutrain> eduTrainList = XdEdutrain.dao.find("select * from xd_edutrain where eid='" + oldEmp.getId() + "'");
		Map<String,XdEdutrain> eduMap =new HashMap<>();
		for (XdEdutrain xdEdutrain : eduTrainList) {
			eduMap.put(xdEdutrain.getId(),xdEdutrain);
		}
		if(gridList1.size() == 0) {
			for (XdEdutrain xdEdutrain : eduTrainList){
				if(rs){
					xdEdutrain.delete();
				}
				summaryList.add(XdOperUtil.logSummary(UuidUtil.getUUID(),oldEmp.getId(),null,xdEdutrain,XdOperEnum.D.name(),summaryStatus));
				flag=true;
			}
		}else{
			for (XdEdutrain xdEdutrain : gridList1) {
				if("".equals(xdEdutrain.getId())){
					xdEdutrain.setEid(oldEmp.getId());
					xdEdutrain.setEname(oldEmp.getName());
					if (rs) {
						xdEdutrain.save(xdEdutrain);
					}else{
						xdEdutrain.loadObj(xdEdutrain);
					}
					summaryList.add(XdOperUtil.logSummary(UuidUtil.getUUID(),oldEmp.getId(),xdEdutrain,null,XdOperEnum.C.name(),summaryStatus));
				}else{
					XdEdutrain oldXdEduTrain = eduMap.get(xdEdutrain.getId());
					if(oldXdEduTrain!=null){
						xdEdutrain.setEnrolldate(xdEdutrain.getEnrolldate().length()>9?xdEdutrain.getEnrolldate().substring(0,10):"");
						xdEdutrain.setGraduatdate(xdEdutrain.getGraduatdate().length()>9?xdEdutrain.getGraduatdate().substring(0,10):"");

						String changedEduTrain = XdOperUtil.getChangedMetheds(xdEdutrain, oldXdEduTrain);
						changedEduTrain= changedEduTrain.replaceAll("--$", "");
						eduMap.remove(oldXdEduTrain.getId());
						if(!"".equals(changedEduTrain)){//有变动插入日志汇总和日志详情表
							if (rs) {
								xdEdutrain.update();
							}
							flag=true;
							String lid =UuidUtil.getUUID();
							summaryList.add(XdOperUtil.logSummary(lid,oldEmp.getId(),xdEdutrain,oldXdEduTrain,XdOperEnum.U.name(),summaryStatus));
							String[] eduCArry = changedEduTrain.split("--");
							for (String edu : eduCArry) {
								edu="{"+edu+"}";
								XdOplogDetail logDetail = JSONUtil.jsonToBean(edu, XdOplogDetail.class);
								logDetail.setRsid(lid);
								list.add(logDetail);
							}

						}
					}
				}

			}
			Collection<XdEdutrain> entries = eduMap.values();
			Iterator<XdEdutrain> iterator = entries.iterator();
			while (iterator.hasNext()){
				XdEdutrain next = iterator.next();
				if (rs) {
					next.delete();
				}
				flag=true;
				summaryList.add(XdOperUtil.logSummary(UuidUtil.getUUID(),oldEmp.getId(),null,next,XdOperEnum.D.name(),summaryStatus));
			}
		}

		List<XdWorkExper> workExperList = XdWorkExper.dao.find("select * from xd_work_exper where eid='" + oldEmp.getId() + "'");
		Map<String,XdWorkExper> workMap =new HashMap<>();
		for (XdWorkExper workExper : workExperList) {
			workMap.put(workExper.getId(),workExper);
		}
		if(gridList2.size() == 0) {
			for (XdWorkExper workExper : workExperList){
				if (rs) {
					workExper.delete();
				}
				summaryList.add(XdOperUtil.logSummary(UuidUtil.getUUID(),oldEmp.getId(),null,workExper,XdOperEnum.D.name(),summaryStatus));
				flag=true;
			}
		}else{
			for (XdWorkExper workExper : gridList2) {
				if("".equals(workExper.getId())){
					workExper.setEid(oldEmp.getId());
					workExper.setEname(oldEmp.getId());
					if (rs) {
						workExper.save(workExper);
					}else{
						workExper.loadObj(workExper);
					}
					summaryList.add(XdOperUtil.logSummary(UuidUtil.getUUID(),oldEmp.getId(),workExper,null,XdOperEnum.C.name(),summaryStatus));
					flag=true;
				}else{
					XdWorkExper oldWorkExper = workMap.get(workExper.getId());
					if(oldWorkExper!=null){
						workExper.setEntrydate(workExper.getEntrydate().length()>9?workExper.getEntrydate().substring(0,10):"");
						workExper.setDepartdate(workExper.getDepartdate().length()>9?workExper.getDepartdate().substring(0,10):"");
						String changedWorkExper = XdOperUtil.getChangedMetheds(workExper, oldWorkExper);
						changedWorkExper= changedWorkExper.replaceAll("--$", "");
						workMap.remove(oldWorkExper.getId());
						if(!"".equals(changedWorkExper)){//有变动插入日志汇总和日志详情表
							if (rs) {
								workExper.update();
							}
							flag=true;
							String lid =UuidUtil.getUUID();
							summaryList.add(XdOperUtil.logSummary(lid,oldEmp.getId(),workExper,oldWorkExper,XdOperEnum.U.name(),summaryStatus));
							String[] workCArry = changedWorkExper.split("--");
							for (String work : workCArry) {

								work="{"+work+"}";
								XdOplogDetail logDetail = JSONUtil.jsonToBean(work, XdOplogDetail.class);
								logDetail.setRsid(lid);
								list.add(logDetail);
							}
						}
					}
				}

			}

			Collection<XdWorkExper> entries = workMap.values();
			Iterator<XdWorkExper> iterator = entries.iterator();
			while (iterator.hasNext()){
				XdWorkExper next = iterator.next();
				if (rs) {
					next.delete();
				}
				flag=true;
				summaryList.add(XdOperUtil.logSummary(UuidUtil.getUUID(),oldEmp.getId(),null,next,XdOperEnum.D.name(),summaryStatus));
			}
		}

		if (summaryList.size() > 0) {
			XdOperUtil.queryLastVersion(oldEmp.getId());
		}
		for (XdOplogSummary xdOplogSummary : summaryList) {
			xdOplogSummary.save();
		}
		for (XdOplogDetail detail : list) {
			detail.setId(UuidUtil.getUUID());
			detail.save();
		}

		if(!rs && flag){
			//XdOperUtil.queryLastVersion(oldEmp.getId());
			XdOperUtil.insertEmpoloyeeSteps(newEmp,"","1","","","U","WA");
		}

		if(rs){

			XdOperUtil.updateEdu(newEmp.getId());
			/*XdEmployee employee = XdEmployee.dao.findById(newEmp.getId());
			boolean arz=true;
			boolean uarz=true;


			List<XdEdutrain> xdEdutrainList = XdEdutrain.dao.find("select * from xd_edutrain where eid='" + newEmp.getId() + "' order by grade, edubg desc");
			for (XdEdutrain edutrain : xdEdutrainList) {
				if(arz && edutrain.getGrade().equals("0")){//全日制
					employee.setEdubg2(edutrain.getEdubg());
					employee.setSchool2(edutrain.getTrainOrgname());
					employee.setMajor2(edutrain.getMajor());
					arz=false;
				}

				if(uarz && edutrain.getGrade().equals("1")){//非全日制
					employee.setEdubg1(edutrain.getEdubg());
					employee.setSchool1(edutrain.getTrainOrgname());
					employee.setMajor1(edutrain.getMajor());
					uarz=false;
				}
			}
			employee.update();
*/
		}
	}


	/**
	 * @Method exportExcel
	 * @param path:	 路径
	 * @param name:	 员工姓名
	 * @param empnum:员工号
	 * @param emprelation:	员工关系
	 * @param unitname:
	 * @param costitem:
	 * @Date 2022/12/15 14:33
	 * @Exception
	 * @Description
	 * @Author king
	 * @Version  1.0
	 * @Return java.io.File
	 */
	public File exportExcel(String path, String name, String empnum, String emprelation, String department, String unitname, String costitem
		,String inductionstatus,String departime,String checked,String selectedName){
		String sql  = " from "+TABLE_NAME+" o   where 1=1";
		String userOrgId = ShiroKit.getUserOrgId();

		if(!"1".equals(userOrgId)){
			//sql=sql+" and cuser='"+ShiroKit.getUserId()+"'";

			SysUser otherUser = SysUser.dao.findById(ShiroKit.getUserId());
			String instr="";
			if(otherUser.getOperProject()!=null && !otherUser.getOperProject().equals("")){
				String[] split = otherUser.getOperProject().split(",");
				for (String s : split) {
					instr=instr+",'"+s+"'";
				}
				instr=instr.replaceAll("^,","");

				sql=sql+" and costitem in ("+instr+")";
			}
			sql = sql + " and o.department = '"+ userOrgId+"'";
		}else {
			if(department!=null && !"null".equals(department) && StrKit.notBlank(department)){
				String[] deptSplit = department.split(",");

				String inSql="";
				for (String deptId : deptSplit) {
					inSql=inSql+"'"+deptId+"'"+",";

				}
				inSql=inSql.replaceAll(",$","");

				sql = sql + " and o.department in("+ inSql+")";
			}
			/*if (StrKit.notBlank(department)) {
				sql = sql + " and o.department = '"+department+"'";
			}*/
		}

		if (StrKit.notBlank(name)) {
			sql = sql + " and o.name like '%"+name +"%'";
		}

		if (StrKit.notBlank(empnum)) {
			sql = sql + " and o.empnum like '%"+empnum +"%'";
		}
		if (StrKit.notBlank(emprelation)) {
			sql = sql + " and o.emprelation like '%"+emprelation+"%'";
		}

		if (StrKit.notBlank(unitname)) {
			sql = sql + " and o.unitname = '"+unitname+"'";
		}

		if (StrKit.notBlank(costitem)) {
			sql = sql + " and o.costitem='"+costitem+"'";
		}

		if(StrKit.notBlank(inductionstatus)){
			sql=sql+ " and o.inductionstatus='"+inductionstatus+"'";
		}
		if(StrKit.notBlank(departime)){
			sql=sql+ " and o.departime like '"+departime+"%'";
		}


		if("true".equals(checked) && !"".equals(selectedName)){
			String[] namesArr = selectedName.split(",");
			String insql="";
			for (String names : namesArr) {
				insql+="'"+names+"',";
			}
			sql  = " from "+TABLE_NAME+" o where 1=1 and name in ("+
					insql.replaceAll(",$","")
					+")";
		}
		if(StrKit.notBlank(inductionstatus)||StrKit.notBlank(departime)){
			sql = sql + " order by o.departime desc";
		}else{
			sql = sql + " order by o.empnum desc";
		}



		List<XdEmployee> list = XdEmployee.dao.find(" select * "+sql);//查询全部
		Map<String, Map<String, String>> dictMap = DictMapping.dictMappingValueToName();
		Map<String, String> projectMap = DictMapping.projectsMappingValueToName();
		Map<String, String> orgMap = DictMapping.orgMapping("0");

		List<List<String>> rows = new ArrayList<List<String>>();
		List<String> first = new ArrayList<String>();
		first.add("序");
		first.add("员工编号");
		first.add("姓名");//0
		first.add("身份证号");
		first.add("所属部门");//2
		first.add("单元");//3
		first.add("成本项目");//3
		first.add("入职日期");//3
		first.add("转正日期");//3
		first.add("离职日期");//3
		first.add("离职原因");//3
		first.add("就职状态");//3
		first.add("性别");//1
		first.add("出生日期");//3
		first.add("在职工龄");//3
		first.add("年龄");//3
		first.add("退休日期");//3
		first.add("退休状态");//3
		first.add("员工性质");//3
		first.add("职位");//3
		first.add("工作岗位");//3
		first.add("联系方式");//3
		first.add("民族");//3
		first.add("政治面貌");//3
		first.add("婚姻");//3
		first.add("最高学历");//3
		first.add("学历非全日制");//3
		first.add("学校非全日制");//3
		first.add("专业非全日制");//3
		first.add("学历全日制");//3
		first.add("学校全日制");//3
		first.add("专业全日制");//3
		first.add("学位");//3
		first.add("户籍");//3
		first.add("出生地");//3
		first.add("籍贯");//3
		first.add("现住址");//3
		first.add("户口所在地");//3
		first.add("退伍军人");//3
		first.add("参加工作时间");//3
		first.add("合同起始日期");//3
		first.add("合同结束日期");//3
		first.add("合同期数");//3
		first.add("合同性质");//3
		first.add("专业技术等级");//3
		first.add("专业技术职务");//3
		first.add("职业资格等级");//3
		first.add("职业资格");//3
		first.add("证书");//3
		first.add("特长");//3
		first.add("紧急联系人");//3
		first.add("银行账号");//3
		first.add("公积金账号");//3
		first.add("招聘来源");//3
		first.add("薪资");//3
		first.add("薪资变动状况");//3
		first.add("调职记录");//3
		first.add("上家工作起日");
		first.add("上家工作止日");
		first.add("上家单位名称");
		first.add("上家职位");


		rows.add(first);
		int rowNum=1;
		Map<String, Map<String, String>> stringMapMap = DictMapping.dictMappingValueToName();
		Map<String, String> positionMap = stringMapMap.get("position");
		Map<String, String> dutyMap = stringMapMap.get("duty");
		Map<String, String> isMarryMap = stringMapMap.get("ismarry");
		Map<String, String> eduMap = stringMapMap.get("edu");
		Map<String, String> empRelationMap = stringMapMap.get("empRelation");
//		String userOrgId = ShiroKit.getUserOrgId();
		for(XdEmployee emp:list){
			List<String> row = new ArrayList<String>();
			DictMapping.fieldValueToName(emp,orgMap,projectMap,dictMap);
			row.add(rowNum+"");
			row.add(emp.getEmpnum());//0
			row.add(emp.getName());//0
			row.add(emp.getIdnum());//0
			row.add(emp.getDepartment()==null?"":emp.getDepartment().toString());//0
			row.add((emp.getUnitname()==null||emp.getUnitname().equals("0"))?"":emp.getUnitname());//0
			row.add(emp.getCostitem()==null?"":emp.getCostitem().toString());//0
			row.add(emp.getEntrytime());//0
			row.add(emp.getPositivedate());//转正日期
			row.add(emp.getDepartime());//0
			row.add(emp.getLeaveReason());
			row.add(emp.getInductionstatus()==null?"":emp.getInductionstatus().toString());//0
			row.add(emp.getGender().toString());//0
			row.add(emp.getBirthday());//出生日期
			row.add(emp.getSeniority());//0
			row.add(emp.getAge()==null?"0":String.valueOf(emp.getAge()));//0
			row.add(emp.getRetiretime());//0
			row.add(emp.getRetirestatus());//退休状态
			row.add(emp.getEmprelation()==null?"":empRelationMap.get(emp.getEmprelation()));
			row.add(emp.getPosition()==null?"":positionMap.get(emp.getPosition().toString()));//0
			row.add(emp.getWorkstation()==null?"":dutyMap.get(emp.getWorkstation()));//0
			row.add(emp.getTel());//0
			row.add(emp.getNational());//0
			row.add(emp.getPoliticsstatus());//0
			row.add(emp.getMarried()==null?"":isMarryMap.get(emp.getMarried().toString()));//0
			row.add(emp.getTopedu()==null?"":eduMap.get(emp.getTopedu()));//0
			row.add(emp.getEdubg1()==null?"":eduMap.get(emp.getEdubg1()));//0
			row.add(emp.getSchool1());//0
			row.add(emp.getMajor1());//0
			row.add(emp.getEdubg2()==null?"":eduMap.get(emp.getEdubg2()));//0
			row.add(emp.getSchool2());//0
			row.add(emp.getMajor2());//0
			row.add(emp.getTopdegree());//0
			row.add(emp.getCensusregister());//0
			row.add(emp.getBirthplace());//0
			row.add(emp.getNativeplace());//0
			row.add(emp.getPresentaddr());//0
			row.add(emp.getCensusregisteraddr());//0
			row.add(emp.getIssoldier()==null?"":(emp.getIssoldier().equals("1")?"是":"否"));//0
			row.add(emp.getWorktime());//0
			row.add(emp.getContractstartdate());//0
			row.add(emp.getContractenddate());//0
			row.add(emp.getContractclauses()==null?"":emp.getContractclauses().toString());//0
			row.add(emp.getEmprelation());//0
			row.add(emp.getProtechgrade());//0
			row.add(emp.getProtechposts());//0
			row.add(emp.getVocaqualifilevel());//0
			row.add(emp.getVocaQualifilevel1());//0
			row.add(emp.getCertificates());
			row.add(emp.getSpecialty());
			row.add(emp.getEmergencyTel());
			row.add(emp.getBanaccount());//银行账号
			row.add(emp.getFundaccount());//公积金账号
			row.add(emp.getRecruitsource());
			if("1".equals(userOrgId)){
				row.add(emp.getSalary()==null?"":emp.getSalary().toString());
				row.add(emp.getSaladjrecord()==null?"":emp.getSaladjrecord());//薪资变动
				row.add(emp.getChrecord()==null?"":emp.getChrecord());//调职记录
			}else{
				row.add("");
				row.add("");//薪资变动
				row.add("");//调职记录
			}

			XdWorkExper firstWork = XdWorkExper.dao.findFirst("select * from  xd_work_exper where eid='" + emp.getId() + "' order by entrydate desc,departdate desc ");
			if(firstWork==null){
				row.add("");
				row.add("");
				row.add("");
				row.add("");
			}else{
				String startDate = firstWork.getEntrydate() == null ? "" : firstWork.getEntrydate();
				String departDate = firstWork.getDepartdate() == null ? "" : firstWork.getDepartdate();
				row.add(startDate);
				row.add(departDate);
				row.add(firstWork.getServiceunit());
				row.add(firstWork.getJob());
			}

			rows.add(row);
		}
		File file = ExcelUtil.listToFile(path,rows);
		return file;
	}
	/**
	 * @Method exportContractExcel
	 * @param path:
	 * @param name:
	 * @param empnum:
	 * @param emprelation:
	 * @param unitname:
	 * @param costitem:
	 * @Date 2022/12/20 14:48
	 * @Exception
	 * @Description 导出合同信息
	 * @Author king
	 * @Version  1.0
	 * @Return java.io.File
	 */
	public File exportContractExcel(String path, String name, String empnum, String emprelation, String department, String unitname, String costitem
			,String inductionstatus,String departime,String checked,String selectedName
	){
		String sql  = " from "+TABLE_NAME+" o   where 1=1";

		if (StrKit.notBlank(name)) {
			sql = sql + " and o.name like '%"+name +"%'";
		}

		if (StrKit.notBlank(empnum)) {
			sql = sql + " and o.empnum like '%"+empnum +"%'";
		}
		if (StrKit.notBlank(emprelation)) {
			sql = sql + " and o.emprelation like '%"+emprelation+"%'";
		}
		if (StrKit.notBlank(department)) {
			sql = sql + " and o.department = '"+department+"'";
		}
		if (StrKit.notBlank(unitname)) {
			sql = sql + " and o.unitname = '"+unitname+"'";
		}
		if (StrKit.notBlank(costitem)) {
			sql = sql + " and o.costitem='"+costitem+"'";
		}
		if(StrKit.notBlank(inductionstatus)){
			sql=sql+ " and o.inductionstatus='"+inductionstatus+"'";
		}
		if(StrKit.notBlank(departime)){
			sql=sql+ " and o.departime like '"+departime+"%'";
		}


		if("true".equals(checked) && !"".equals(selectedName)){
			String[] namesArr = selectedName.split(",");
			String insql="";
			for (String names : namesArr) {
				insql+="'"+names+"',";
			}
			sql  = " from "+TABLE_NAME+" o where 1=1 and name in ("+
					insql.replaceAll(",$","")
					+")";
		}
		if(StrKit.notBlank(inductionstatus)||StrKit.notBlank(departime)){
			sql = sql + " order by o.departime desc";
		}else{
			sql = sql + " order by o.empnum desc";
		}



		List<XdEmployee> list = XdEmployee.dao.find(" select * "+sql);//查询全部
		List<XdContractInfo> listContract =new ArrayList<>();
		Map<String,XdEmployee> mapObj=new HashMap<>();
		if(!"true".equals(checked)&&"".equals(name)&&"".equals(empnum)
				&&"".equals(emprelation)&&"".equals(unitname)&&"".equals(costitem)
		&&"".equals(inductionstatus)&&"".equals(departime)){
		  listContract = XdContractInfo.dao.find("select * from xd_contract_info  order by eid,contractclauses ");
			for (XdEmployee xdEmployee : list) {
				mapObj.put(xdEmployee.getId(),xdEmployee);
			}
		}else{
			String inSql="'0',";
			for (XdEmployee xdEmployee : list) {
				inSql=inSql+"'"+xdEmployee.getId()+"'"+",";
				mapObj.put(xdEmployee.getId(),xdEmployee);
			}
			inSql=inSql.replaceAll(",$","");
			listContract = XdContractInfo.dao.find("select * from xd_contract_info where  eid in (" + inSql + ")  order by eid,contractclauses ");
		}

		Map<String,Integer> mapCount=new HashMap<>();
		Map<String,List<XdContractInfo>> mapObje=new HashMap<>();
		for (XdContractInfo contractInfo : listContract) {
			if(mapCount.get(contractInfo.getEid())==null){
				mapCount.put(contractInfo.getEid(),1);
				List<XdContractInfo> conInfoList=new ArrayList<>();
				conInfoList.add(contractInfo);
				mapObje.put(contractInfo.getEid(),conInfoList);
			}else{
				Integer integer = mapCount.get(contractInfo.getEid());
				mapCount.put(contractInfo.getEid(),integer+1);
				List<XdContractInfo> conList = mapObje.get(contractInfo.getEid());
				conList.add(contractInfo);
				mapObje.put(contractInfo.getEid(),conList );
			}
		}

		Collection<Integer> values = mapCount.values();
		Stream<Integer> sorted = values.stream().sorted((o1, o2) ->  -o1.compareTo(o2));
		int maxLen =sorted.findFirst().get();;
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
			}else{
				first.add(num[i-2]);
				second.add("合同起始日期"+(i-1));
				second.add("合同结束日期"+(i-1));
				second.add("期数"+(i-1));
				second.add("合同性质"+(i-1));
			}
			//first.add("教育/培训经历"+i);

		}

		rows.add(first);
		rows.add(second);
		Collection<List<XdContractInfo>> collContract = mapObje.values();
		Stream<List<XdContractInfo>> sorted1 = collContract.stream().sorted((o1, o2) -> -(o1.size() - o2.size()));
		Map<String, String> orgMap = DictMapping.orgMapping("0");
		sorted1.forEach( xdContractInfos->{
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

		});

		File file = ExcelUtil.conTractFile(path,rows);
		return file;
	}








	/**
	 * @Method importExcel
	 * @param list:
	 * @Date 2022/12/15 14:34
	 * @Exception
	 * @Description 员工信息导入
	 * @Author king
	 * @Version  1.0
	 * @Return java.util.Map<java.lang.String,java.lang.Object>
	 */
	public Map<String,Object> importExcel(List<List<String>> list) throws SQLException {
		final List<String> message = new ArrayList<String>();
		final Map<String,Object> result = new HashMap<String,Object>();
		Map<String,String> orgMap=DictMapping.orgMapping("1");
		Map<String,String> projectsMap=DictMapping.projectsMapping();
		Map<String, Map<String, String>> dictMapping= DictMapping.dictMapping();
		Db.tx(new IAtom() {
			@Override
			public boolean run() throws SQLException {
				try{
					if(list.size()>1){
						SimpleUser user = ShiroKit.getLoginUser();
						String time = DateUtil.getCurrentTime();
						for(int i = 1;i<list.size();i++){//从第二行开始取
							List<String> empStr = list.get(i);
							XdEmployee emp=new XdEmployee();
							emp.setId(UuidUtil.getUUID());
							emp.setCtime(time);
							emp.setCuser(user.getId());
							if(empStr.get(1)==null ||"".equals(empStr.get(1).trim())){
								continue;
							}
							emp.setEmpnum(empStr.get(1));
							emp.setName(empStr.get(2));
							System.out.println(empStr.get(2));
							emp.setIdnum(empStr.get(3));

							String org = empStr.get(4);

							String orgid = orgMap.get(org);
							//SysOrg orgInfo = SysOrg.dao.findFirst("select * from sys_org where name ='" + org + "'");
							if(orgid==null){
								emp.setDepartment("0");//empStr.get(5) 所属部门
							}else{
//								emp.setDepartment(orgInfo.getId());
								emp.setDepartment(orgid);
							}

//							XdDict unit = XdDict.dao.findFirst("select * from xd_dict where type='unit' and name ='" + empStr.get(6) + "'");
							Map<String, String> unit = dictMapping.get("unit");
							// unit.get(empStr.get(5));
							/*if(unit==null){
								emp.setUnitname(0);//所属单元empStr.get(6)
							}else{
								emp.setUnitname(Integer.valueOf(unit.getValue()));
							}*/
							if(unit.get(empStr.get(5))==null){
								emp.setUnitname("0");//所属单元empStr.get(6)
							}else{
								emp.setUnitname(unit.get(empStr.get(5)));
							}
							String projectValue = (projectsMap.get(empStr.get(6)))==null?"0":projectsMap.get(empStr.get(6));
							emp.setCostitem(projectValue);
							emp.setEntrytime(empStr.get(7));
							emp.setPositivedate(empStr.get(8));
							emp.setDepartime(empStr.get(9));
							emp.setLeaveReason(empStr.get(10));
							Map<String, String> officestatus = dictMapping.get("officestatus");
							if(officestatus.get(empStr.get(11))==null){
								emp.setInductionstatus("0");//就职状态empStr.get(11)
							}else{
								emp.setInductionstatus(officestatus.get(empStr.get(11)));
							}
							emp.setGender(empStr.get(12).equals("女")?"0":"1");
							emp.setBirthday("".equals(empStr.get(13))?"":XdOperUtil.numToDateFormat(empStr.get(13)));
							emp.setSeniority(empStr.get(14));
//							Double age =Double.valueOf(empStr.get(15));
							emp.setAge(Integer.valueOf(empStr.get(15)));
							emp.setRetiretime("".equals(empStr.get(16))?"":XdOperUtil.numToDateFormat(empStr.get(16)));
							emp.setRetirestatus(empStr.get(17));
							Map<String, String> empRelationMap = dictMapping.get("empRelation");
							emp.setEmprelation(empRelationMap.get(empStr.get(18)));
							/*XdDict position = XdDict.dao.findFirst("select * from xd_dict where type='position' and name ='" + empStr.get(18) + "'");
							if(position==null){
								emp.setPosition(0);//职位empStr.get(18)
							}else{
								emp.setPosition(Integer.valueOf(position.getValue()));
							}*/
							Map<String, String> position = dictMapping.get("position");
//							System.out.println("职位=============="+empStr.get(19));
//							System.out.println(position.get(empStr.get(19)));
							if(position.get(empStr.get(19))==null){
								emp.setPosition("0");//职位empStr.get(19)
							}else{
								emp.setPosition(position.get(empStr.get(19)));
							}
							Map<String, String> duty = dictMapping.get("duty");
							if(empStr.get(20).equals("")){
								emp.setWorkstation("");
							}else{
								emp.setWorkstation(duty.get(empStr.get(20)));
							}

							//emp.setWorkstation(empStr.get(20));
							emp.setTel(empStr.get(21));
//							emp.setNational(empStr.get(21));
							if(empStr.get(22)==null){
								emp.setNational("");
							}else{
								String nation = dictMapping.get("nation").get(empStr.get(22));
								if(nation==null){
									emp.setNational("");
								}else{
									emp.setNational(nation);
								}
							}
//							政治面貌
//							emp.setPoliticsstatus(empStr.get(22));
							String politicsstatus = empStr.get(23);
							if(politicsstatus==null){
								emp.setPoliticsstatus("");
							}else{
								String polity = dictMapping.get("polity").get(empStr.get(23));
								if (polity == null) {
									emp.setPoliticsstatus("");
								}else{
									emp.setPoliticsstatus(polity);
								}
							}



							Map<String, String> ismarry = dictMapping.get("ismarry");
							String marryCode=ismarry.get(empStr.get(24))==null?"0":ismarry.get(empStr.get(24));
							emp.setMarried(marryCode);


							//emp.setMarried(0);//婚姻：empStr.get(23)
							//emp.setTopedu(empStr.get(24));
							/*XdDict edu = XdDict.dao.findFirst("select * from xd_dict where type='edu' and name ='" + empStr.get(24) + "'");
							if(edu==null){
								emp.setTopedu(empStr.get(24));;//婚姻：empStr.get(23)
							}else{
								emp.setTopedu(edu.getValue());
							}*/
							Map<String, String> eduMap = dictMapping.get("edu");
							String  edu=eduMap.get(empStr.get(25))==null?empStr.get(25):eduMap.get(empStr.get(25)).toString();
							emp.setTopedu(edu);


							//emp.setEdubg1(empStr.get(25));//0
							/*if(edu==null){
								emp.setEdubg1(empStr.get(25));
							}else{
								emp.setEdubg1(edu.getValue());
							}*/
							String edubg1=eduMap.get(empStr.get(26))==null?empStr.get(26):eduMap.get(empStr.get(26)).toString();
							emp.setEdubg1(edubg1);
							emp.setSchool1(empStr.get(27));//0
							emp.setMajor1(empStr.get(28));//0
							//emp.setEdubg2(empStr.get(28));//0
							/*if(edu==null){
								emp.setEdubg2(empStr.get(28));
							}else{
								emp.setEdubg2(edu.getValue());
							}*/

							String edubg2=eduMap.get(empStr.get(29))==null?empStr.get(29):eduMap.get(empStr.get(29)).toString();
							emp.setEdubg2(edubg2);
//							emp.setEdubg1(edubg1);
							emp.setSchool2(empStr.get(30));//0
							emp.setMajor2(empStr.get(31));//0
							emp.setTopdegree(empStr.get(32));//0
							emp.setCensusregister(empStr.get(33));//0
							emp.setBirthplace(empStr.get(34));//0
							emp.setNativeplace(empStr.get(35));//0
							emp.setPresentaddr(empStr.get(36));//0
							emp.setCensusregisteraddr(empStr.get(37));//0
							emp.setIssoldier("是".equals(empStr.get(38))?"1":"0");//会是否军人empStr.get(37)

							emp.setWorktime(empStr.get(39));//0
							emp.setContractstartdate("".equals(empStr.get(40))?"":XdOperUtil.numToDateFormat(empStr.get(40)));//0
							if(empStr.get(41).equals("")||empStr.get(41).equals("无固定期限")){
								emp.setContractenddate(empStr.get(41));//0
							}else{

								emp.setContractenddate(XdOperUtil.numToDateFormat(empStr.get(41)));//0
							}
							emp.setContractclauses(Integer.valueOf(empStr.get(42).equals("")?"0":empStr.get(42)));// 合同期数empStr.get(41)
//							emp.setEmprelation(empStr.get(42));
							emp.setContractnature(empStr.get(43));
							emp.setProtechgrade(empStr.get(44));//0
							emp.setProtechposts(empStr.get(45));//0
							emp.setVocaqualifilevel(empStr.get(46));//0
							emp.setVocaQualifilevel1(empStr.get(47));//0
							emp.setCertificates(empStr.get(48));
							emp.setSpecialty(empStr.get(49));
							emp.setEmergencyTel(empStr.get(50));
							emp.setBanaccount(empStr.get(51));//银行账号
							emp.setFundaccount(empStr.get(52));//公积金账号
							emp.setRecruitsource(empStr.get(53));
							emp.setSalary(Integer.valueOf((empStr.get(54)==null||empStr.get(54).equals(""))?"0":empStr.get(54)));
							emp.setSaladjrecord(empStr.get(55));
							emp.setChrecord(empStr.get(56));
							emp.save();


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
	public static String getStr(List<String> l,int i){
		String re = "";
		try{
			if(i==1) {
				re = l.get(i).substring(0, 10);
			}else {
				re = l.get(i);
			}
		}catch(Exception e){
			re = "";
		}
		return re;
	}


	public List<XdEmployee> getPrintInfos(String name,String empNum,String department
	,String empRelation,String unitName,String costItem,String inductionStatus,String departTime,
										  String checked,String selectedName){
//		String userId = ShiroKit.getUserId();
		String sql  = "select * from "+TABLE_NAME+" o where 1=1";
		if(StrKit.notBlank(name)){
			sql = sql + " and o.name like '%"+ name+"%'";
		}
		if(StrKit.notBlank(empNum)){
			sql = sql + " and o.empnum like '%"+ empNum+"%'";
		}
		if(StrKit.notBlank(empRelation)){
			sql = sql + " and o.emprelation like '%"+ empRelation+"%'";
		}
	/*	if(StrKit.notBlank(department)){
			sql = sql + " and o.department = '"+ department+"'";
		}
*/
		String userOrgId = ShiroKit.getUserOrgId();

		if(!"1".equals(userOrgId)){
			//sql=sql+" and cuser='"+ShiroKit.getUserId()+"'";

			SysUser otherUser = SysUser.dao.findById(ShiroKit.getUserId());
			String instr="";
			if(otherUser.getOperProject()!=null && !otherUser.getOperProject().equals("")){
				String[] split = otherUser.getOperProject().split(",");
				for (String s : split) {
					instr=instr+",'"+s+"'";
				}
				instr=instr.replaceAll("^,","");

				sql=sql+" and costitem in ("+instr+")";
			}
			sql = sql + " and o.department = '"+ userOrgId+"'";
		}else {
			if(department!=null && !"null".equals(department) && StrKit.notBlank(department)){
				String[] deptSplit = department.split(",");

				String inSql="";
				for (String deptId : deptSplit) {
					inSql=inSql+"'"+deptId+"'"+",";

				}
				inSql=inSql.replaceAll(",$","");

				sql = sql + " and o.department in("+ inSql+")";
			}
		}

		if(StrKit.notBlank(unitName)){
			sql = sql + " and o.unitname = '"+ unitName+"'";
		}
		if(StrKit.notBlank(costItem)){
			sql = sql + " and o.costitem = '"+ costItem+"'";
		}
		if(StrKit.notBlank(inductionStatus)){
			sql=sql+ " and o.inductionstatus='"+inductionStatus+"'";
		}
		if(StrKit.notBlank(departTime)){
			sql=sql+ " and o.departime like '"+departTime+"%'";
		}


		if("true".equals(checked) && !"".equals(selectedName)){
			String[] namesArr = selectedName.split(",");
			String insql="";
			for (String names : namesArr) {
				insql+="'"+names+"',";
			}
			sql  = "select * from "+TABLE_NAME+" o where 1=1 and name in ("+
					insql.replaceAll(",$","")
					+")";
		}
		if(StrKit.notBlank(inductionStatus)||StrKit.notBlank(departTime)){
			sql = sql + " order by o.departime desc";
		}else{
			sql = sql + " order by o.empnum desc";
		}


		return  XdEmployee.dao.find(sql);
	}
}