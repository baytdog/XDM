package com.pointlion.mvc.admin.xdm.xdemployee;

import com.itextpdf.text.log.SysoCounter;
import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.enums.XdOperEnum;
import com.pointlion.mvc.common.model.*;
import com.pointlion.mvc.common.utils.*;
import com.pointlion.mvc.common.utils.office.excel.ExcelUtil;
import com.pointlion.plugin.shiro.ShiroKit;
import com.pointlion.plugin.shiro.ext.SimpleUser;
import org.apache.commons.lang3.StringUtils;

import javax.jnlp.ServiceManagerStub;
import java.io.File;
import java.sql.SQLException;
import java.util.*;

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
	public Page<Record> getPage(int pnum,int psize,String startTime,String endTime,String applyUser){
		String userId = ShiroKit.getUserId();
		String sql  = " from "+TABLE_NAME+" o where cuser='"+ShiroKit.getUserId()+"'";
		if(StrKit.notBlank(startTime)){
			sql = sql + " and o.create_time>='"+ DateUtil.formatSearchTime(startTime,"0")+"'";
		}
		if(StrKit.notBlank(endTime)){
			sql = sql + " and o.create_time<='"+DateUtil.formatSearchTime(endTime,"1")+"'";
		}
		sql = sql + " order by o.ctime desc";
		return Db.paginate(pnum, psize, " select * ", sql);
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
			o.setBackup1("D");
			o.update();
			if("1".equals(userOrgId)){
				o.delete();
			}else{
				XdOperUtil.insertEmpoloyeeSteps(o,"","1","","","D");
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
				list.add(logDetail);
			}
			flag=true;
			summaryList.add(XdOperUtil.logSummary(lid,oldEmp.getId(),newEmp,oldEmp,XdOperEnum.U.name(),summaryStatus));
		}
		if(rs){
			newEmp.update();
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
			XdOperUtil.insertEmpoloyeeSteps(newEmp,"","1","","","U");
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
	public File exportExcel(String path, String name, String empnum, String emprelation, String unitname, String costitem){

		String userId = ShiroKit.getUserId();

		String sql  = " from "+TABLE_NAME+" o   where 1=1";

//		String adminUser = Constants.ADMIN_USER;
		SysUser user = SysUser.dao.findById(userId);

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
			sql = sql + " and o.unitname like '%"+unitname+"%'";
		}

		if (StrKit.notBlank(costitem)) {
			sql = sql + " and o.status='"+costitem+"'";
		}
		sql = sql + " order by o.ctime desc";


		List<XdEmployee> list = XdEmployee.dao.find(" select * "+sql);//查询全部
		List<List<String>> rows = new ArrayList<List<String>>();
		List<String> first = new ArrayList<String>();
		first.add("员工编号");
		first.add("姓名");//0
		first.add("身份证号");
		first.add("性别");//1
		first.add("所属部门");//2
		first.add("单元");//3
		first.add("成本项目");//3
		first.add("入职日期");//3
		first.add("转正日期");//3
		first.add("离职日期");//3
		first.add("就职状态");//3
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


		rows.add(first);
		for(XdEmployee emp:list){
			List<String> row = new ArrayList<String>();
			row.add(emp.getEmpnum());//0
			row.add(emp.getName());//0
			row.add(emp.getIdnum());//0
			row.add(emp.getGender().toString());//0
			row.add(emp.getDepartment()==null?"":emp.getDepartment().toString());//0
			row.add(emp.getUnitname()==null?"":emp.getUnitname().toString());//0
			row.add(emp.getCostitem()==null?"":emp.getCostitem().toString());//0
			row.add(emp.getEntrytime());//0
			row.add(emp.getPositivedate());//转正日期
			row.add(emp.getDepartime());//0
			row.add(emp.getInductionstatus()==null?"":emp.getInductionstatus().toString());//0
			row.add(emp.getBirthday());//出生日期
			row.add(emp.getSeniority());//0
			row.add(emp.getAge().toString());//0
			row.add(emp.getRetiretime());//0
			row.add("");//退休状态
			row.add(emp.getEmprelation());
			row.add(emp.getPosition()==null?"":emp.getPosition().toString());//0
			row.add(emp.getWorkstation());//0
			row.add(emp.getTel());//0
			row.add(emp.getNational());//0
			row.add(emp.getPoliticsstatus());//0
			row.add(emp.getMarried()==null?"":emp.getMarried().toString());//0
			row.add(emp.getTopedu());//0
			row.add(emp.getEdubg1());//0
			row.add(emp.getSchool1());//0
			row.add(emp.getMajor1());//0
			row.add(emp.getEdubg2());//0
			row.add(emp.getSchool2());//0
			row.add(emp.getMajor2());//0
			row.add(emp.getTopdegree());//0
			row.add(emp.getCensusregister());//0
			row.add(emp.getBirthplace());//0
			row.add(emp.getNativeplace());//0
			row.add(emp.getPresentaddr());//0
			row.add(emp.getCensusregisteraddr());//0
			row.add(emp.getIssoldier()==null?"":emp.getIssoldier().toString());//0
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
			row.add(emp.getEmcontact());
			row.add(emp.getBanaccount());//银行账号
			row.add(emp.getFundaccount());//公积金账号
			row.add(emp.getRecruitsource());
			row.add(emp.getSalary()==null?"":emp.getSalary().toString());
			row.add("");//薪资变动
			row.add("");//调职记录
			rows.add(row);
		}
		File file = ExcelUtil.listToFile(path,rows);
		return file;
	}

	public String formatFromchannel(String fromchannel) {
		String fFromchannel="";
		switch (fromchannel) {
			case "1":
				fFromchannel="国家局转信";
				break;
			case "2":
				fFromchannel="国家局转访";
				break;
			case "3":
				fFromchannel="市转来信";
				break;
			case "4":
				fFromchannel="市转来访";
				break;
			case "5":
				fFromchannel="市委领导信箱";
				break;
			case "6":
				fFromchannel="市长信箱";
				break;
			case "7":
				fFromchannel="委转来信";
				break;
			case "8":
				fFromchannel="委转来访";
				break;
			case "9":
				fFromchannel="委转来电";
				break;
			case "10":
				fFromchannel="中心来访";
				break;
			case "11":
				fFromchannel="中心来信";
				break;
			case "12":
				fFromchannel="交通网";
				break;
			case "13":
				fFromchannel="主任信箱";
				break;
			case "14":
				fFromchannel="投诉受理信箱";
				break;
			case "15":
				fFromchannel="市交通委信箱";
				break;

			default:
				break;
		}
		return fFromchannel;

	}

	public String formatLetterresult(String letterresult) {
		String fletterresult="";
		if(letterresult==null) {

		}else {

			switch (letterresult) {
				case "1":
					fletterresult="解决";
					break;
				case "2":
					fletterresult="部分解决";
					break;
				case "3":
					fletterresult="视为解决";
					break;
				case "4":
					fletterresult="未解决";
					break;
				case "5":
					fletterresult="留作参考";
					break;

				default:
					break;
			}
		}

		return fletterresult;

	}


	public String formatLetterreason(String letterreason) {
		String fLetterreason="";

		if(letterreason==null) {

		}else {

			switch (letterreason) {
				case "1":
					fLetterreason="无理/失实";
					break;
				case "2":
					fLetterreason="政策所限";
					break;
				case "3":
					fLetterreason="客观所限";
					break;
				case "4":
					fLetterreason="要求过高";
					break;

				default:
					break;
			}
		}
		return fLetterreason;

	}

	public String formatFromersug(String fromersug) {
		String fFromersug="";
		if(fromersug==null) {

		}else {

			switch (fromersug) {
				case "1":
					fFromersug="同意";
					break;
				case "2":
					fFromersug="不同意";
					break;
				case "3":
					fFromersug="未有明确意见";
					break;
				default:
					break;
			}
		}

		return fFromersug;

	}
	public String formatLettertype(String lettertype) {
		String flettertype="";
		switch (lettertype) {
			case "1":
				flettertype="申述";
				break;
			case "2":
				flettertype="求决";
				break;
			case "3":
				flettertype="举报";
				break;
			case "4":
				flettertype="意见建议";
				break;
			case "5":
				flettertype="其他";
				break;

			default:
				break;
		}
		return flettertype;

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
		Db.tx(new IAtom() {
			@Override
			public boolean run() throws SQLException {
				try{
					if(list.size()>1){
						SimpleUser user = ShiroKit.getLoginUser();
						/*String orgid = ShiroKit.getUserOrgId();
						String orgName = ShiroKit.getUserOrgName();*/
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
							emp.setIdnum(empStr.get(3));
							emp.setGender(empStr.get(4).equals("女")?0:1);
							String org = empStr.get(5);
							SysOrg orgInfo = SysOrg.dao.findFirst("select * from sys_org where name ='" + org + "'");
							if(orgInfo==null){
								emp.setDepartment("0");//empStr.get(5) 所属部门
							}else{
								emp.setDepartment(orgInfo.getId());
							}

							XdDict unit = XdDict.dao.findFirst("select * from xd_dict where type='unit' and name ='" + empStr.get(6) + "'");
							if(unit==null){
								emp.setUnitname(0);//所属单元empStr.get(6)
							}else{
								emp.setUnitname(Integer.valueOf(unit.getValue()));
							}
							//emp.setUnitname(0);//所属单元empStr.get(6)
							XdDict project = XdDict.dao.findFirst("select * from xd_dict where type='projects' and name ='" + empStr.get(7) + "'");
							if(project==null){
								emp.setCostitem(0);//empStr.get(7)成本项目
							}else{
								emp.setCostitem(Integer.valueOf(project.getValue()));
							}
							emp.setEntrytime(empStr.get(8));
							emp.setPositivedate(empStr.get(9));
							emp.setDepartime(empStr.get(10));
							XdDict officestatus = XdDict.dao.findFirst("select * from xd_dict where type='officestatus' and name ='" + empStr.get(11) + "'");
							if(officestatus==null){
								emp.setInductionstatus(0);//就职状态empStr.get(11)
							}else{
								emp.setInductionstatus(Integer.valueOf(officestatus.getValue()));
							}
							//emp.setInductionstatus(0);//就职状态empStr.get(11)
							emp.setBirthday(empStr.get(12));
							emp.setSeniority(empStr.get(13));
							emp.setAge(Integer.valueOf(empStr.get(14)));
							emp.setRetiretime(empStr.get(15));
							emp.setRetirestatus(empStr.get(16));
							emp.setEmprelation(empStr.get(17));
							XdDict position = XdDict.dao.findFirst("select * from xd_dict where type='position' and name ='" + empStr.get(18) + "'");
							if(position==null){
								emp.setPosition(0);//职位empStr.get(18)
							}else{
								emp.setPosition(Integer.valueOf(position.getValue()));
							}

							emp.setWorkstation(empStr.get(19));
							emp.setTel(empStr.get(20));
							emp.setNational(empStr.get(21));
							emp.setPoliticsstatus(empStr.get(22));
							XdDict ismarry = XdDict.dao.findFirst("select * from xd_dict where type='ismarry' and name ='" + empStr.get(23) + "'");
							if(ismarry==null){
								emp.setMarried(0);//婚姻：empStr.get(23)
							}else{
								emp.setMarried(Integer.valueOf(ismarry.getValue()));
							}
							//emp.setMarried(0);//婚姻：empStr.get(23)
							//emp.setTopedu(empStr.get(24));
							XdDict edu = XdDict.dao.findFirst("select * from xd_dict where type='edu' and name ='" + empStr.get(24) + "'");
							if(edu==null){
								emp.setTopedu(empStr.get(24));;//婚姻：empStr.get(23)
							}else{
								emp.setTopedu(edu.getValue());
							}
							//emp.setEdubg1(empStr.get(25));//0
							if(edu==null){
								emp.setEdubg1(empStr.get(25));//婚姻：empStr.get(23)
							}else{
								emp.setEdubg1(edu.getValue());
							}
							emp.setSchool1(empStr.get(26));//0
							emp.setMajor1(empStr.get(27));//0
							//emp.setEdubg2(empStr.get(28));//0
							if(edu==null){
								emp.setEdubg2(empStr.get(28));//婚姻：empStr.get(23)
							}else{
								emp.setEdubg2(edu.getValue());
							}
							emp.setSchool2(empStr.get(29));//0
							emp.setMajor2(empStr.get(30));//0
							emp.setTopdegree(empStr.get(31));//0
							emp.setCensusregister(empStr.get(32));//0
							emp.setBirthplace(empStr.get(33));//0
							emp.setNativeplace(empStr.get(34));//0
							emp.setPresentaddr(empStr.get(35));//0
							emp.setCensusregisteraddr(empStr.get(36));//0
							emp.setIssoldier("是".equals(empStr.get(37))?1:0);//会是否军人empStr.get(37)

							emp.setWorktime(empStr.get(38));//0
							emp.setContractstartdate(empStr.get(39));//0
							emp.setContractenddate(empStr.get(40));//0
							emp.setContractclauses(0);// 合同期数empStr.get(41)
							emp.setEmprelation(empStr.get(42));
							emp.setProtechgrade(empStr.get(43));//0
							emp.setProtechposts(empStr.get(44));//0
							emp.setVocaqualifilevel(empStr.get(45));//0
							emp.setVocaQualifilevel1(empStr.get(46));//0
							emp.setCertificates(empStr.get(47));
							emp.setSpecialty(empStr.get(48));
							emp.setEmcontact(empStr.get(49));
							emp.setBanaccount(empStr.get(50));//银行账号
							emp.setFundaccount(empStr.get(51));//公积金账号
							emp.setRecruitsource(empStr.get(52));
							emp.setSalary(Integer.valueOf((empStr.get(53)==null||empStr.get(53).equals(""))?"0":empStr.get(53)));
							emp.setSaladjrecord(empStr.get(54));
							emp.setChrecord(empStr.get(55));
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


	public List<XdEmployee> getPrintInfos(String name,String empnum,String department
	,String emprelation,String unitname,String costitem){
		String userId = ShiroKit.getUserId();
		String sql  = "select * from "+TABLE_NAME+" o where 1=1";
		if(StrKit.notBlank(name)){
			sql = sql + " and o.name like '%"+ name+"%'";
		}
		if(StrKit.notBlank(empnum)){
			sql = sql + " and o.empnum like '%"+ empnum+"%'";
		}
		if(StrKit.notBlank(department)){
			sql = sql + " and o.department = '"+ department+"'";
		}
		if(StrKit.notBlank(emprelation)){
			sql = sql + " and o.emprelation = '"+ emprelation+"'";
		}
		if(StrKit.notBlank(unitname)){
			sql = sql + " and o.unitname = '"+ unitname+"'";
		}
		if(StrKit.notBlank(costitem)){
			sql = sql + " and o.costitem = '"+ costitem+"'";
		}
		sql = sql + " order by o.ctime desc";

		return  XdEmployee.dao.find(sql);
	}
}