package com.pointlion.mvc.admin.xdm.xdemployee;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.upload.UploadFile;
import com.pointlion.enums.XdOperEnum;
import com.pointlion.mvc.common.base.BaseController;
import com.pointlion.mvc.common.model.*;
import com.pointlion.mvc.common.utils.*;
import com.pointlion.mvc.common.utils.office.excel.ExcelUtil;
import com.pointlion.plugin.shiro.ShiroKit;
import com.pointlion.util.DictMapping;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;


public class XdEmployeeController extends BaseController {
	public static final XdEmployeeService service = XdEmployeeService.me;
	/***
	 * get list page
	 */
	public void getListPage(){
		String userOrgId = ShiroKit.getUserOrgId();
		if("1".equals(userOrgId)){
			setAttr("personnel","Y");
		}else{
			setAttr("personnel","N");

		}

		Map<String, List<XdDict>> dictListByType = DictMapping.getDictListByType();
		//	List<XdDict> units = XdDict.dao.find("select *from xd_dict where type='unit' order by  sortnum");
		setAttr("units",	dictListByType.get("unit"));

		if(!"1".equals(userOrgId)){

			SysUser otherUser = SysUser.dao.findById(ShiroKit.getUserId());
			String instr="";
			if(otherUser.getOperProject()!=null && !otherUser.getOperProject().equals("")){
				String[] split = otherUser.getOperProject().split(",");
				for (String s : split) {
					instr=instr+",'"+s+"'";
				}
				instr=instr.replaceAll("^,","");
				if(!instr.equals("")){
					List<XdProjects> projects = XdProjects.dao.find("select * from xd_projects where status='1' and  id in ("+instr+")");
					setAttr("projects",projects);
				}else{

					List<XdProjects> projects = XdProjects.dao.find("select * from xd_projects where status='1' ");
					setAttr("projects",projects);
				}
			}

		}else{
			List<XdProjects> projects = XdProjects.dao.find("select * from xd_projects where status='1' ");
			setAttr("projects",projects);
		}


		List<SysOrg> orgList = SysOrg.dao.find("select * from  sys_org where id !='root' order by sort");

		setAttr("orgs",orgList);
		String orgStr="";
		Map<String,String> map =new HashMap<>();
		for (SysOrg org : orgList) {
			orgStr=orgStr+org.getId()+"="+org.getName()+",";
		}
		List<XdDict> dutyList = dictListByType.get("duty");
		String dutyStr="";
		for (XdDict duty : dutyList) {
			dutyStr=dutyStr+duty.getValue()+"="+duty.getName()+",";
		}
		List<XdDict> eduList = dictListByType.get("edu");
		String edu = JSONUtil.listToJson(eduList);
		List<XdDict> positionList = dictListByType.get("position");
		List<XdDict> empRelationList = dictListByType.get("empRelation");


		String positions = JSONUtil.listToJson(positionList);
		String empRelations = JSONUtil.listToJson(empRelationList);

		setAttr("eduStr",edu);
		setAttr("orgStr",orgStr);
		setAttr("dutyStr",dutyStr);
		setAttr("positions",positions);
		setAttr("positionList",positionList);
		setAttr("empRelations",empRelations);
		setAttr("empRelationList",empRelationList);
		setAttr("dutyList",dutyList);
		renderIframe("list.html");
    }
	public void getLeaveListPage(){
		String userOrgId = ShiroKit.getUserOrgId();
		if("1".equals(userOrgId)){
			setAttr("personnel","Y");
		}else{
			setAttr("personnel","N");

		}

		Map<String, List<XdDict>> dictListByType = DictMapping.getDictListByType();
		//	List<XdDict> units = XdDict.dao.find("select *from xd_dict where type='unit' order by  sortnum");
		setAttr("units",	dictListByType.get("unit"));

		if(!"1".equals(userOrgId)){

			SysUser otherUser = SysUser.dao.findById(ShiroKit.getUserId());
			String instr="";
			if(otherUser.getOperProject()!=null && !otherUser.getOperProject().equals("")){
				String[] split = otherUser.getOperProject().split(",");
				for (String s : split) {
					instr=instr+",'"+s+"'";
				}
				instr=instr.replaceAll("^,","");
				if(!instr.equals("")){
					List<XdProjects> projects = XdProjects.dao.find("select * from xd_projects where status='1' and  id in ("+instr+")");
					setAttr("projects",projects);
				}else{

					List<XdProjects> projects = XdProjects.dao.find("select * from xd_projects where status='1' ");
					setAttr("projects",projects);
				}
			}

		}else{
			List<XdProjects> projects = XdProjects.dao.find("select * from xd_projects where status='1' ");
			setAttr("projects",projects);
		}


		List<SysOrg> orgList = SysOrg.dao.find("select * from  sys_org where id !='root' order by sort");

		setAttr("orgs",orgList);
		String orgStr="";
		Map<String,String> map =new HashMap<>();
		for (SysOrg org : orgList) {
			orgStr=orgStr+org.getId()+"="+org.getName()+",";
		}
		List<XdDict> dutyList = dictListByType.get("duty");
		String dutyStr="";
		for (XdDict duty : dutyList) {
			dutyStr=dutyStr+duty.getValue()+"="+duty.getName()+",";
		}
		List<XdDict> eduList = dictListByType.get("edu");
		String edu = JSONUtil.listToJson(eduList);
		List<XdDict> positionList = dictListByType.get("position");
		List<XdDict> empRelationList = dictListByType.get("empRelation");


		String positions = JSONUtil.listToJson(positionList);
		String empRelations = JSONUtil.listToJson(empRelationList);

		setAttr("eduStr",edu);
		setAttr("orgStr",orgStr);
		setAttr("dutyStr",dutyStr);
		setAttr("positions",positions);
		setAttr("positionList",positionList);
		setAttr("empRelations",empRelations);
		setAttr("empRelationList",empRelationList);
		setAttr("dutyList",dutyList);
		renderIframe("leaveList.html");
    }


	public void getCompareListPage(){
		String userOrgId = ShiroKit.getUserOrgId();
		if("1".equals(userOrgId)){
			setAttr("personnel","Y");
		}else{
			setAttr("personnel","N");

		}

		Map<String, List<XdDict>> dictListByType = DictMapping.getDictListByType();
		//	List<XdDict> units = XdDict.dao.find("select *from xd_dict where type='unit' order by  sortnum");
		setAttr("units",	dictListByType.get("unit"));

		if(!"1".equals(userOrgId)){

			SysUser otherUser = SysUser.dao.findById(ShiroKit.getUserId());
			String instr="";
			if(otherUser.getOperProject()!=null && !otherUser.getOperProject().equals("")){
				String[] split = otherUser.getOperProject().split(",");
				for (String s : split) {
					instr=instr+",'"+s+"'";
				}
				instr=instr.replaceAll("^,","");
				if(!instr.equals("")){
					List<XdProjects> projects = XdProjects.dao.find("select * from xd_projects where status='1' and  id in ("+instr+")");
					setAttr("projects",projects);
				}else{

					List<XdProjects> projects = XdProjects.dao.find("select * from xd_projects where status='1' ");
					setAttr("projects",projects);
				}
			}

		}else{
			List<XdProjects> projects = XdProjects.dao.find("select * from xd_projects where status='1' ");
			setAttr("projects",projects);
		}


		List<SysOrg> orgList = SysOrg.dao.find("select * from  sys_org where id !='root' order by sort");

		setAttr("orgs",orgList);
		String orgStr="";
		Map<String,String> map =new HashMap<>();
		for (SysOrg org : orgList) {
			orgStr=orgStr+org.getId()+"="+org.getName()+",";
		}
		List<XdDict> dutyList = dictListByType.get("duty");
		String dutyStr="";
		for (XdDict duty : dutyList) {
			dutyStr=dutyStr+duty.getValue()+"="+duty.getName()+",";
		}
		List<XdDict> eduList = dictListByType.get("edu");
		String edu = JSONUtil.listToJson(eduList);


		List<XdDict> positionList = dictListByType.get("position");
		String positions = JSONUtil.listToJson(positionList);

		List<XdDict> empRelationList = dictListByType.get("empRelation");
		String empRelations = JSONUtil.listToJson(empRelationList);
		setAttr("eduStr",edu);
		setAttr("orgStr",orgStr);
		setAttr("dutyStr",dutyStr);
		setAttr("positions",positions);
		setAttr("positionList",positionList);

		setAttr("empRelations",empRelations);
		setAttr("empRelationList",empRelationList);
		setAttr("dutyList",dutyList);
		renderIframe("compareList.html");
	}

	public void getComparePage() throws UnsupportedEncodingException {
//		String ids = getPara("ids");

		String selectedName = new String(getPara("selectedName","").getBytes("ISO-8859-1"), "utf-8");
		System.out.println("比较人员：="+selectedName);

		String nameArr[] = selectedName.split(",");
		String inSql="";
		for (String name : nameArr) {
			inSql=inSql+"'"+name+"'"+",";
		}
		inSql=inSql.replaceAll(",$","");
		List<XdEmployee> empLists = XdEmployee.dao.find("select * from  xd_employee where name in (" + inSql + ") order by empnum");

		System.out.println("选择人员个数："+empLists.size());


		Map<String, Map<String, String>> stringMapMap = DictMapping.dictMappingValueToName();
		List<SysOrg> orgList = SysOrg.dao.find("select * from  sys_org");
		Map <String,String>orgMap =new HashMap();
		for (SysOrg org : orgList) {
			orgMap.put(org.getId(),org.getName());
		}

		List<XdProjects> projects = XdProjects.dao.find("select * from  xd_projects");
		Map <String,String>projectMap =new HashMap();
		projects.stream().forEach( project->projectMap.put(project.getId().toString(),project.getProjectName()));
		empLists.stream().forEach(emp-> {
			emp.setGender(emp.getGender().equals("1")?"男":"女");
			emp.setMarried(stringMapMap.get("ismarry").get(emp.getMarried()));
			emp.setPoliticsstatus(stringMapMap.get("polity").get(emp.getPoliticsstatus()));
			emp.setTopedu(stringMapMap.get("edu").get(emp.getTopedu()));
			emp.setTopedu(stringMapMap.get("edu").get(emp.getTopedu()));
			emp.setInductionstatus(stringMapMap.get("officestatus").get(emp.getInductionstatus()));
			emp.setDepartment(orgMap.get(emp.getDepartment()));
			emp.setPosition(stringMapMap.get("position").get(emp.getPosition()));
			emp.setEdubg1(stringMapMap.get("edu").get(emp.getEdubg1()));
			emp.setEdubg2(stringMapMap.get("edu").get(emp.getEdubg2()));
			emp.setCostitem(projectMap.get(emp.getCostitem()));
			emp.setWorkstation(stringMapMap.get("duty").get(emp.getWorkstation()));
			List<XdWorkExper> workExperList = XdWorkExper.dao.find("select * from  xd_work_e" +
					"xper where eid='" + emp.getId() + "' order by entrydate  desc");
			String workExpr="";
			int i =1;
			for (XdWorkExper workExper : workExperList) {
				workExpr=workExpr+i+"、"+workExper.getServiceunit()+"/"+workExper.getJob()+"("+workExper.getEntrydate()+"/"+workExper.getDepartdate()+")\t";
				i++;
			}
			emp.setBackup1(workExpr);

		});
		setAttr("empLists",empLists);
		renderIframe("compare.html");
	}
	/***
     * list page data
     **/
    public void listData() throws UnsupportedEncodingException {
		String curr = getPara("pageNumber");
    	String pageSize = getPara("pageSize");
    	String sort = getPara("sort");
    	String sortOrder = getPara("sortOrder");

		String name = java.net.URLDecoder.decode(getPara("name",""),"UTF-8");
		String empnum = java.net.URLDecoder.decode(getPara("empnum",""),"UTF-8");
		String emprelation = java.net.URLDecoder.decode(getPara("emprelation",""),"UTF-8");
		String department =getPara("department","");
		String unitname = getPara("unitname","");
		String costitem = getPara("costitem","");
		String inductionstatus = getPara("inductionstatus","");
		String departime = getPara("departime","");
		String position = getPara("position","");
		String workstation = getPara("workstation","");
		String checked = getPara("checked","");
		String selectedName = java.net.URLDecoder.decode(getPara("selectedName",""),"UTF-8");

    	Page<Record> page = service.getPage(Integer.valueOf(curr),Integer.valueOf(pageSize),sort,sortOrder,name,empnum,emprelation,department,unitname,costitem,inductionstatus,departime,checked,selectedName,position,workstation);
    	renderPage(page.getList(),"",page.getTotalRow());
    }
    /***
     * save data
     */
    public void save(){
		String gridData1 = getPara("gridData1");
		String gridData2 = getPara("gridData2");
		XdEmployee o = getModel(XdEmployee.class);
		List<XdEdutrain> gridList1 = JSONUtil.jsonToBeanList(gridData1, XdEdutrain.class);
		List<XdWorkExper> gridList2 = JSONUtil.jsonToBeanList(gridData2, XdWorkExper.class);
		String id = o.getId();
		XdEmployee employee = XdEmployee.dao.findById(id);
		if(employee != null){
			o.setCtime(employee.getCtime());
			o.setCuser(employee.getCuser());
			service.modifyObj(o,employee,gridList1,gridList2);

//			员工信息修改更新教育、工作经历、证书表相关字段
			XdOperUtil.updateEmpRelationInfos(o);

		}else{
			String operName = XdOperEnum.C.name();
			String operStatus = XdOperEnum.WAITAPPRO.name();
			String oid=UuidUtil.getUUID();
			//o.setId(oid);
    		o.setCtime(DateUtil.getCurrentTime());
    		o.setCuser(ShiroKit.getUserId());
			if(!"1".equals(ShiroKit.getUserOrgId())){
				o.setBackup1("C");
				XdOperUtil.insertEmpoloyeeSteps(o,"","1","","","C","WA");
				//XdOperUtil.logSummary(oid,o,operName,operStatus,0);
				XdOperUtil.logSummary(id,o,operName,operStatus,0);
			}else{
				String idnum = o.getIdnum();
				if(idnum.length()==15){
					String year ="19"+ idnum.substring(6,8);
					String month = idnum.substring(8,10);
					String days = idnum.substring(10,12);
					int lastIndex = Integer.valueOf(idnum.substring(14));
					o.setGender(String.valueOf(lastIndex%2));
					o.setBirthday(year+"-"+month+"-"+days);
					LocalDate birthday = LocalDate.of(Integer.valueOf(year), Integer.valueOf(month), Integer.valueOf(days));
					long minusDays = LocalDate.now().toEpochDay() - birthday.toEpochDay();
					int age = (int) (minusDays / 365);
					o.setAge(age);

				}else{
					String year = idnum.substring(6,10);
					String month = idnum.substring(10,12);
					String days = idnum.substring(12,14);
					int lastIndex = Integer.valueOf(idnum.substring(17));
					o.setGender(String.valueOf(lastIndex%2));
					o.setBirthday(year+"-"+month+"-"+days);

					LocalDate birthday = LocalDate.of(Integer.valueOf(year), Integer.valueOf(month), Integer.valueOf(days));
					long minusDays = LocalDate.now().toEpochDay() - birthday.toEpochDay();
					int age = (int) (minusDays / 365);
					o.setAge(age);
				}


				o.save();
				//XdOperUtil.logSummary(oid,o,operName,XdOperEnum.UNAPPRO.name(),0);
				XdOperUtil.logSummary(id,o,operName,XdOperEnum.UNAPPRO.name(),0);
			}
    		if(gridList1.size()!=0){
				for (int i = 0; i < gridList1.size(); i++) {
					XdEdutrain xdEdutrain = gridList1.get(i);
					xdEdutrain.setEid(o.getId());
					xdEdutrain.setEname(o.getName());
					if("1".equals(ShiroKit.getUserOrgId())){

						xdEdutrain.save(xdEdutrain);
						XdOperUtil.logSummary(oid,xdEdutrain,operName,XdOperEnum.UNAPPRO.name(),0);
						XdOperUtil.updateEdu(o.getId());
					}else{
						xdEdutrain.loadObj(xdEdutrain);
						XdOperUtil.logSummary(oid,xdEdutrain,operName,operStatus,0);
					}
				}
			}
    		if(gridList2.size()!=0){
				for (XdWorkExper workExper : gridList2) {
					workExper.setEid(o.getId());
					workExper.setEname(o.getName());
					if("1".equals(ShiroKit.getUserOrgId())){
						workExper.save(workExper);
						XdOperUtil.logSummary(oid,workExper,operName,XdOperEnum.UNAPPRO.name(),0);
					}else{
						workExper.loadObj(workExper);
						XdOperUtil.logSummary(oid,workExper,operName,operStatus,0);
					}

				}
			}
		}
		renderSuccess();
		}
    /***
     * edit page
	 */
    public void getEditPage(){
    	String id = getPara("id");
    	String view = getPara("view");
		setAttr("view", view);
		XdEmployee o = new XdEmployee();
		if(StrKit.notBlank(id)){
    		o = service.getById(id);
    		/*if("detail".equals(view)){
    		}*/
			if("1".equals(ShiroKit.getUserOrgId())){
				setAttr("oper","1");
			}else{
				setAttr("oper","0");
			}
			if(o.getWorktime().contains("-")){
				o.setWorktime(o.getWorktime().substring(0,7).replaceAll("-","年")+"月");
			}
    	}else{
			String uuid = UuidUtil.getUUID();
			System.out.println(uuid);
			o.setId(uuid);
			if("1".equals(ShiroKit.getUserOrgId())){
				setAttr("oper","1");
			}else{
				setAttr("oper","0");
			}
		}
		Map<String, List<XdDict>> dictListByType = DictMapping.getDictListByType();
		List<XdDict> ismarry = XdDict.dao.find("select * from xd_dict where type ='ismarry'");
		setAttr("ismarry",ismarry);
		List<XdDict> nations = XdDict.dao.find("select * from xd_dict where type ='nation' order by sortnum");
		setAttr("nations",nations);
		List<XdDict> polities = XdDict.dao.find("select * from xd_dict where type ='polity' order by sortnum");
		setAttr("polities",polities);
		List<XdDict> edus = XdDict.dao.find("select * from xd_dict where type ='edu' order by sortnum");
		setAttr("edus",edus);
		List<XdDict> officestatus = XdDict.dao.find("select * from xd_dict where type ='officestatus' order by sortnum");
		setAttr("officestatus",officestatus);

		List<SysOrg> sysOrgs = SysOrg.dao.find("select * from sys_org where id<>'root' order by sort");
		setAttr("sysOrgs",sysOrgs);
		List<XdDict> units = XdDict.dao.find("select * from xd_dict where type ='unit' order by sortnum");
		setAttr("units",units);
//		List<XdDict> projects = XdDict.dao.find("select * from xd_dict where type ='projects' order by sortnum");
		List<XdProjects> projects = XdProjects.dao.find("select * from xd_projects where status='1'");
		setAttr("projects",projects);
		List<XdDict> position = XdDict.dao.find("select * from xd_dict where type ='position' order by sortnum");
		setAttr("position",position);
		List<XdDict> hardstuff = XdDict.dao.find("select * from xd_dict where type ='hardstuff' order by sortnum");
		setAttr("hardstuff",hardstuff);

		List<XdDict> dutyList = dictListByType.get("duty");
		setAttr("duties",dutyList);
		List<XdDict> empRelations = dictListByType.get("empRelation");
		setAttr("empRelations",empRelations);
		//String edu = JSONUtil.listToJson(eduList);
		/*List education=new ArrayList();
		Map <String,String>eduMaps=new HashMap();
		eduList.stream().forEach(dict-> education.add("{id:"+dict.getValue()+",text:'"+dict.getName()+"'}") );
		setAttr("edu",education);*/
		setAttr("o", o);
    	setAttr("formModelName",StringUtil.toLowerCaseFirstOne(XdEmployee.class.getSimpleName()));
		renderIframe("edit.html");
    }
    /***
     * del
     * @throws Exception
     */
    public void delete() throws Exception{

		String ids = getPara("ids");
		service.deleteByIds(ids);
		renderSuccess("操作成功!");
    }


	public void getListEmployee(){
			renderIframe("pEmployeeList.html");
	}
	 /**
	  * @Method openEmployeePage
	  * @Date 2022/12/8 14:18
	  * @Description  打开人员审批表单界面
	  * @Author king
	  * @Version  1.0
	  * @Return void
	  */
	public void openEmployeePage(){
		boolean rs = ShiroKit.getUserOrgId().equals("1");
		String sid = getPara("id");
		keepPara("id");
		XdSteps step = XdSteps.dao.findById(sid);
		String stepType = step.getSoptye();
		String s = (step.getRemarks() == null ? "" : step.getRemarks());
		if(stepType.equals("UP")){
			XdSteps pStep = XdSteps.dao.findById(step.getParentid());
			s=pStep.getRemarks();
		}
		setAttr("comments",s);
		String oid = step.getOid();
		List<XdOplogSummary> summaries =
				XdOplogSummary.dao.find("select * from xd_oplog_summary where oid='" + oid + "' and lastversion='0'");
		List<String> listEdu=new ArrayList<>();
		List<String> listWExper=new ArrayList<>();
		XdEmployee xdEmployee=null;
		if("C".equals(stepType)){
			for (XdOplogSummary summary : summaries) {
					if("XdEmployee".equals(summary.getTname())){
						String changea = summary.getChangea();
						xdEmployee = JSONUtil.jsonToBean(changea, XdEmployee.class);
					}else if("XdEdutrain".equals(summary.getTname())){
						listEdu.add(summary.getChangea());
					}else{
						listWExper.add(summary.getChangea());
					}
			}
		}else if("D".equals(stepType)){
			for (XdOplogSummary summary : summaries) {
				if("XdEmployee".equals(summary.getTname())){
					String changeb = summary.getChangeb();
					xdEmployee = JSONUtil.jsonToBean(changeb, XdEmployee.class);
				}else if("XdEdutrain".equals(summary.getTname())){
					listEdu.add(summary.getChangeb());
				}else{
					listWExper.add(summary.getChangeb());
				}
			}
		}else if("U".equals(stepType)){
			xdEmployee = XdEmployee.dao.findById(oid);
			List<XdEdutrain> edutrainList = XdEdutrain.dao.find("select * from  xd_edutrain where eid='" + oid + "'");
			for (XdEdutrain edutrain : edutrainList) {
				listEdu.add(JSONUtil.beanToJsonString(edutrain));
			}
			List<XdWorkExper> xdEdutrainList = XdWorkExper.dao.find("select * from  xd_work_exper where eid='" + oid + "'");
			for (XdWorkExper workExper : xdEdutrainList) {
				listWExper.add(JSONUtil.beanToJsonString(workExper));
			}
//			List<XdOplogDetail> oplogEmployee  =new ArrayList<>();
			List<String> oplogEmployee  =new ArrayList<>();
			List<String> oplogEdu  =new ArrayList<>();
			List<String> oplogWork  =new ArrayList<>();
			for (XdOplogSummary summary : summaries) {
				if(summary.getTname().equals("XdEmployee")){
//					oplogEmployee = XdOplogDetail.dao.find("select * from xd_oplog_detail where rsid='" + summary.getId() + "'");
					List<XdOplogDetail> xdOplogDetails =null;
					if(rs){
						xdOplogDetails=XdOplogDetail.dao.find("select * from xd_oplog_detail where status='0' and rsid='" + summary.getId() + "'");
					}else{
						xdOplogDetails=XdOplogDetail.dao.find("select * from xd_oplog_detail where rsid='" + summary.getId() + "'");
					}
					DictMapping.opLogsMapping(xdOplogDetails);
					for (XdOplogDetail xdOplogDetail : xdOplogDetails) {
						oplogEmployee.add(JSONUtil.beanToJsonString(xdOplogDetail));
					}
				}
				if(summary.getTname().equals("XdEdutrain")) {
					if(summary.getStatus().equals("WAITAPPRO") || !rs){
					if (summary.getOtype().equals("C")) {
						XdEdutrain xdEdutrain = JSONUtil.jsonToBean(summary.getChangea(), XdEdutrain.class);
						xdEdutrain.setBakcup2("新增");
						xdEdutrain.setBackup1(summary.getStatus());
						xdEdutrain.setBackup4(summary.getReason());
						xdEdutrain.setBackup5(summary.getId());
						oplogEdu.add(JSONUtil.beanToJsonString(xdEdutrain));
					} else if (summary.getOtype().equals("D")) {
						XdEdutrain xdEdutrain = JSONUtil.jsonToBean(summary.getChangeb(), XdEdutrain.class);
						xdEdutrain.setBakcup2("删除");
						xdEdutrain.setBackup1(summary.getStatus());
						xdEdutrain.setBackup4(summary.getReason());
						xdEdutrain.setBackup5(summary.getId());
						oplogEdu.add(JSONUtil.beanToJsonString(xdEdutrain));
					} else {
						XdEdutrain xdEdutrain = JSONUtil.jsonToBean(summary.getChangeb(), XdEdutrain.class);
						List<XdOplogDetail> xdOplogDetails = null;
						if (rs) {
							xdOplogDetails = XdOplogDetail.dao.find("select * from xd_oplog_detail where status='0' and  rsid='" + summary.getId() + "'");
						} else {
							xdOplogDetails = XdOplogDetail.dao.find("select * from xd_oplog_detail where rsid='" + summary.getId() + "'");
						}

						StringBuilder sb = new StringBuilder("[ ");
						for (XdOplogDetail xdOplogDetail : xdOplogDetails) {
							String fieldName = xdOplogDetail.getFieldName();
							sb.append(xdOplogDetail.getFieldDesc() + " ").append("原值 ： ");

									if(fieldName.equals("edubg")||fieldName.equals("grade")){
											sb.append((xdOplogDetail.getOldValue() == null || "".equals(xdOplogDetail.getOldValue())) ? "空" : XdOperUtil.apprListToValue(fieldName,xdOplogDetail.getOldValue()));
									}else{
										sb.append((xdOplogDetail.getOldValue() == null || "".equals(xdOplogDetail.getOldValue())) ? "空" : xdOplogDetail.getOldValue());
									}
									sb.append(", 现值 ： ");
									if(xdOplogDetail.getFieldName().equals("edubg")||xdOplogDetail.getFieldName().equals("grade")){
											sb.append((xdOplogDetail.getNewValue() == null || "".equals(xdOplogDetail.getNewValue())) ? "空" : XdOperUtil.apprListToValue(fieldName,xdOplogDetail.getNewValue()));
									}else{
											sb.append((xdOplogDetail.getNewValue() == null || "".equals(xdOplogDetail.getNewValue())) ? "空" : xdOplogDetail.getNewValue());
									}
									sb.append("  --  ");
						}
						String s1 = sb.toString().replaceAll("--  $", "") + " ]";
						xdEdutrain.setBakcup2("修改");
						xdEdutrain.setBackup3(s1);
						xdEdutrain.setBackup1(summary.getStatus());
						xdEdutrain.setBackup4(summary.getReason());
						xdEdutrain.setBackup5(summary.getId());
						oplogEdu.add(JSONUtil.beanToJsonString(xdEdutrain));
					}
				}
				}
				if(summary.getTname().equals("XdWorkExper")) {
					if(summary.getStatus().equals("WAITAPPRO")|| !rs){
					if (summary.getOtype().equals("C")) {
						XdWorkExper workExper = JSONUtil.jsonToBean(summary.getChangea(), XdWorkExper.class);
						workExper.setBackup2("新增");
						workExper.setBackup1(summary.getStatus());
						workExper.setBackup4(summary.getReason());
						workExper.setBackup5(summary.getId());
						oplogWork.add(JSONUtil.beanToJsonString(workExper));
					} else if (summary.getOtype().equals("D")) {
						XdWorkExper workExper = JSONUtil.jsonToBean(summary.getChangeb(), XdWorkExper.class);
						workExper.setBackup2("删除");
						workExper.setBackup1(summary.getStatus());
						workExper.setBackup4(summary.getReason());
						workExper.setBackup5(summary.getId());
						oplogWork.add(JSONUtil.beanToJsonString(workExper));
					} else {

						XdWorkExper workExper = JSONUtil.jsonToBean(summary.getChangeb(), XdWorkExper.class);
						List<XdOplogDetail> xdOplogDetails = null;
						if (rs) {
							xdOplogDetails = XdOplogDetail.dao.find("select * from xd_oplog_detail where status='0' and  rsid='" + summary.getId() + "'");
						} else {
							xdOplogDetails = XdOplogDetail.dao.find("select * from xd_oplog_detail where  rsid='" + summary.getId() + "'");
						}


						StringBuilder sb = new StringBuilder("[ ");
						for (XdOplogDetail xdOplogDetail : xdOplogDetails) {
							sb.append(xdOplogDetail.getFieldDesc() + " ").append("原值 ： ")
									.append((xdOplogDetail.getOldValue() == null || "".equals(xdOplogDetail.getOldValue())) ? "空" : xdOplogDetail.getOldValue())
									.append(", 现值 ： ").append((xdOplogDetail.getNewValue() == null || "".equals(xdOplogDetail.getNewValue())) ? "空" : xdOplogDetail.getNewValue())
									.append("  --  ");
						}
						String s1 = sb.toString().replaceAll("--  $", "") + " ]";
						workExper.setBackup2("修改");
						workExper.setBackup3(s1);
						workExper.setBackup1(summary.getStatus());
						workExper.setBackup4(summary.getReason());
						workExper.setBackup5(summary.getId());
						oplogWork.add(JSONUtil.beanToJsonString(workExper));
					}
				}
				}
			}
			setAttr("oplogEmployee",oplogEmployee);
			setAttr("oplogEdu",oplogEdu);
			setAttr("oplogWork",oplogWork);
		}

		if(step.getAuditresult().equals("OA")){
			/*else if("P".equals(stepType)||"UP".equals(stepType)){
				for (XdOplogSummary summary : summaries) {
					if("XdEmployee".equals(summary.getTname())){
						String empStr = summary.getChangea();
						xdEmployee = JSONUtil.jsonToBean(empStr, XdEmployee.class);
					}else if("XdEdutrain".equals(summary.getTname())){
						listEdu.add(summary.getChangea());
					}else{
						listWExper.add(summary.getChangea());
					}
				}
				if (xdEmployee == null) {
					xdEmployee=XdEmployee.dao.findById(step.getOid());
				}*/
				step.setFinished("Y");
				step.setFinishtime(DateUtil.getCurrentTime());
				step.update();
		}




		List<XdDict> ismarry = XdDict.dao.find("select * from xd_dict where type ='ismarry'");
		setAttr("ismarry",ismarry);
		List<XdDict> nations = XdDict.dao.find("select * from xd_dict where type ='nation' order by sortnum");
		setAttr("nations",nations);
		List<XdDict> polities = XdDict.dao.find("select * from xd_dict where type ='polity' order by sortnum");
		setAttr("polities",polities);
		List<XdDict> edus = XdDict.dao.find("select * from xd_dict where type ='edu' order by sortnum");
		setAttr("edus",edus);
		List<XdDict> officestatus = XdDict.dao.find("select * from xd_dict where type ='officestatus' order by sortnum");
		setAttr("officestatus",officestatus);

		List<SysOrg> sysOrgs = SysOrg.dao.find("select * from sys_org where id<>'root' order by sort");
		setAttr("sysOrgs",sysOrgs);
		List<XdDict> units = XdDict.dao.find("select * from xd_dict where type ='unit' order by sortnum");
		setAttr("units",units);
		List<XdProjects> projects = XdProjects.dao.find("select * from xd_projects where status='1'");
		setAttr("projects",projects);
		List<XdDict> position = XdDict.dao.find("select * from xd_dict where type ='position' order by sortnum");
		setAttr("position",position);
		List<XdDict> hardstuff = XdDict.dao.find("select * from xd_dict where type ='hardstuff' order by sortnum");
		setAttr("hardstuff",hardstuff);
		setAttr("o",xdEmployee);
		setAttr("listEdu",listEdu);
		setAttr("listWExper",listWExper);
		setAttr("otype",stepType);
		if(ShiroKit.getUserOrgId().equals("1")){
			setAttr("oper","approver");
		}
		setAttr("formModelName",StringUtil.toLowerCaseFirstOne(XdEmployee.class.getSimpleName()));//模型名称
		renderIframe("pEmployee.html");

	}
	/**
	 * @Method pass
	 * @Date 2022/12/8 15:04
	 * @Description 人员审批通过
	 * @Author king
	 * @Version  1.0
	 * @Return void
	 */
	public void pass(){
		String stepsId = getPara("stepsId");
		XdSteps steps = XdSteps.dao.findById(stepsId);
		String oid = steps.getOid();

		String g5ids = getPara("g5ids");
		String g4ids = getPara("g4ids");
		String g3ids = getPara("g3ids");
		String comment = getPara("comment");
		if("".equals(g5ids)&&"".equals(g4ids)&&"".equals(g3ids)) {
			List<XdOplogSummary> summaries = XdOplogSummary.dao.find("select * from xd_oplog_summary where oid='" + oid + "' and lastversion='0'");
			for (XdOplogSummary summary : summaries) {
				String tName = summary.getTname();
				String oType = summary.getOtype();
				String values = "";
				try {
					Class clazz = Class.forName("com.pointlion.mvc.common.model." + tName);
					Method method = null;
					if ("C".equals(oType)) {
						values = summary.getChangea();
						method = clazz.getMethod("save");
					} else if ("D".equals(oType)) {
						values = summary.getChangeb();
						method = clazz.getMethod("delete");
					} else if ("U".equals(oType)) {
						method = clazz.getMethod("update");
						values = summary.getChangea();
						if ("XdEmployee".equals(tName)) {
							values = summary.getChangea();
							XdEmployee emp = (XdEmployee) JSONUtil.jsonToBean(values, clazz);
							List<XdOplogDetail> xdOplogDetails = XdOplogDetail.dao.find("select * from xd_oplog_detail where rsid='" + summary.getId() + "'");
							for (XdOplogDetail logDetail : xdOplogDetails) {
								if ("salary".equals(logDetail.getFieldName())) {
									String salaryRecord = (emp.getSaladjrecord() == null ? "" : emp.getSaladjrecord() + "\t") + "原工资: " + logDetail.getOldValue() + " - " + "最新工资: " + logDetail.getNewValue();

									emp.setSaladjrecord(salaryRecord);
								}
								if ("contractclauses".equals(logDetail.getFieldName())) {
									XdContractInfo contract = new XdContractInfo();
									contract.setId(UuidUtil.getUUID());
									contract.setEid(emp.getId());
									contract.setContractstartdate(emp.getContractstartdate());
									contract.setContractenddate(emp.getContractenddate());
									contract.setContractclauses(emp.getContractclauses());
									contract.setContractnature(emp.getContractnature());
									contract.setCtime(summary.getCtime());
									contract.setCuser(summary.getCuser());
									contract.save();
								}
								if ("workstation".equals(logDetail.getFieldName())) {
									String workRecord = (emp.getChrecord() == null ? "" : emp.getChrecord() + "\t") + "原岗位: " + logDetail.getOldValue() + "-" + "最新岗位: " + logDetail.getNewValue();
									emp.setChrecord(workRecord);
								}
								if("Idnum".equals(logDetail.getFieldName())) {
									String newIdNum = logDetail.getNewValue();
									if (newIdNum.length() == 15) {
										String year = "19" + newIdNum.substring(6, 8);
										String month = newIdNum.substring(8, 10);
										String days = newIdNum.substring(10, 12);
										int lastIndex = Integer.valueOf(newIdNum.substring(14));
										emp.setGender(String.valueOf(lastIndex % 2));
										emp.setBirthday(year + "-" + month + "-" + days);
										LocalDate birthday = LocalDate.of(Integer.valueOf(year), Integer.valueOf(month), Integer.valueOf(days));
										long minusDays = LocalDate.now().toEpochDay() - birthday.toEpochDay();
										int age = (int) (minusDays / 365);
										emp.setAge(age);

									} else {
										String year = newIdNum.substring(6, 10);
										String month = newIdNum.substring(10, 12);
										String days = newIdNum.substring(12, 14);
										int lastIndex = Integer.valueOf(newIdNum.substring(17));
										emp.setGender(String.valueOf(lastIndex % 2));
										emp.setBirthday(year + "-" + month + "-" + days);

										LocalDate birthday = LocalDate.of(Integer.valueOf(year), Integer.valueOf(month), Integer.valueOf(days));
										long minusDays = LocalDate.now().toEpochDay() - birthday.toEpochDay();
										int age = (int) (minusDays / 365);
										emp.setAge(age);
									}
								}

								if("inductionstatus".equals(logDetail.getFieldName())){
									XdOperUtil.updateEmpCert(emp);
								}
								logDetail.setStatus(1);
								logDetail.setReason(comment);
								logDetail.update();
							}
							values = JSONUtil.beanToJsonString(emp);
						}
					}
					method.setAccessible(true);
					method.invoke(JSONUtil.jsonToBean(values, clazz));
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
				summary.setStatus(XdOperEnum.PASS.name());
				summary.setReason(comment);
				summary.update();
			}








			steps.setFinished("Y");
			steps.setUserid(ShiroKit.getUserId());
			steps.setUsername(ShiroKit.getUsername());
			steps.setFinishtime(DateUtil.getCurrentTime());
			steps.setRemarks(comment);
			steps.update();
			XdEmployee employee = new XdEmployee();
			employee.setId(steps.getOid());
			employee.setName(steps.getStep());
			employee.setEmpnum(steps.getStepdesc());
			SysUser user = SysUser.dao.findById(steps.getCuserid());
			XdOperUtil.insertEmpoloyeeSteps(employee, stepsId, user.getOrgid(), user.getId(), user.getName(), steps.getSoptye(), "OA");

			renderSuccess("over");
		}else {
			if(!"".equals(g5ids)){
				String[] logsArr = g5ids.split(",");
				String rsid="";
				XdEmployee emp=null;
				for (String logid : logsArr) {
					XdOplogDetail logDetail = XdOplogDetail.dao.findById(logid);
					rsid=logDetail.getRsid();
					logDetail.setStatus(1);
					logDetail.setReason(comment);
					logDetail.update();
					XdOplogSummary summay = XdOplogSummary.dao.findById(rsid);
					if(emp==null){
						//String changeb = summay.getChangeb();
						emp = XdEmployee.dao.findById(summay.getTid());
						//emp= JSONUtil.jsonToBean(changeb, XdEmployee.class);
					}
					XdOperUtil.setChangeValue(emp,logDetail.getFieldName(),logDetail.getNewValue());

					if ("salary".equals(logDetail.getFieldName())) {
						String salaryRecord = (emp.getSaladjrecord() == null ? "" : emp.getSaladjrecord() + "\t") + "原工资: " + logDetail.getOldValue() + " - " + "最新工资: " + logDetail.getNewValue();

						emp.setSaladjrecord(salaryRecord);
					}
					if ("contractclauses".equals(logDetail.getFieldName())) {
						XdContractInfo contract = new XdContractInfo();
						contract.setId(UuidUtil.getUUID());
						contract.setEid(emp.getId());
						contract.setContractstartdate(emp.getContractstartdate());
						contract.setContractenddate(emp.getContractenddate());
						contract.setContractclauses(emp.getContractclauses());
						contract.setContractnature(emp.getContractnature());
						contract.setCtime(summay.getCtime());
						contract.setCuser(summay.getCuser());
						contract.save();
					}
					if ("workstation".equals(logDetail.getFieldName())) {
						String workRecord = (emp.getChrecord() == null ? "" : emp.getChrecord() + "\t") + "原岗位: " + logDetail.getOldValue() + "-" + "最新岗位: " + logDetail.getNewValue();
						emp.setChrecord(workRecord);
					}
					if("Idnum".equals(logDetail.getFieldName())) {
						String newIdNum = logDetail.getNewValue();
						if (newIdNum.length() == 15) {
							String year = "19" + newIdNum.substring(6, 8);
							String month = newIdNum.substring(8, 10);
							String days = newIdNum.substring(10, 12);
							int lastIndex = Integer.valueOf(newIdNum.substring(14));
							emp.setGender(String.valueOf(lastIndex % 2));
							emp.setBirthday(year + "-" + month + "-" + days);
							LocalDate birthday = LocalDate.of(Integer.valueOf(year), Integer.valueOf(month), Integer.valueOf(days));
							long minusDays = LocalDate.now().toEpochDay() - birthday.toEpochDay();
							int age = (int) (minusDays / 365);
							emp.setAge(age);

						} else {
							String year = newIdNum.substring(6, 10);
							String month = newIdNum.substring(10, 12);
							String days = newIdNum.substring(12, 14);
							int lastIndex = Integer.valueOf(newIdNum.substring(17));
							emp.setGender(String.valueOf(lastIndex % 2));
							emp.setBirthday(year + "-" + month + "-" + days);

							LocalDate birthday = LocalDate.of(Integer.valueOf(year), Integer.valueOf(month), Integer.valueOf(days));
							long minusDays = LocalDate.now().toEpochDay() - birthday.toEpochDay();
							int age = (int) (minusDays / 365);
							emp.setAge(age);
						}
					}

					if("inductionstatus".equals(logDetail.getFieldName())){
						XdOperUtil.updateEmpCert(emp);
					}
				}
				emp.update();
				List<XdOplogDetail> xdOplogDetails = XdOplogDetail.dao.find("select * from  xd_oplog_detail where status='0' and rsid='" + rsid + "'");
				if(xdOplogDetails.size()==0){
					XdOplogSummary summary = XdOplogSummary.dao.findById(rsid);
					summary.setReason(comment);
					summary.setStatus("FINISH");
					summary.update();
				}
			}
			if(!"".equals(g4ids)){
				String[] summaryArr = g4ids.split(",");
				for (String sumId : summaryArr) {
					XdOplogSummary summary = XdOplogSummary.dao.findById(sumId);
					summary.setReason(comment);
					summary.setStatus(XdOperEnum.PASS.name());
					summary.update();
					String otype = summary.getOtype();
					if("D".equals(otype)){
						String changeb = summary.getChangeb();
						XdWorkExper workExper = JSONUtil.jsonToBean(changeb, XdWorkExper.class);
						workExper.delete();
					}else if("C".equals(otype)){
						String changea = summary.getChangea();
						XdWorkExper workExper = JSONUtil.jsonToBean(changea, XdWorkExper.class);
						workExper.save();
					}else if("U".equals(otype)){
						String changea = summary.getChangea();
						XdWorkExper workExper = JSONUtil.jsonToBean(changea, XdWorkExper.class);
						workExper.update();
					}

					List<XdOplogDetail> xdOplogDetails = XdOplogDetail.dao.find("select * from  xd_oplog_detail where status='0' and rsid='" + sumId + "'");
					for (XdOplogDetail xdOplogDetail : xdOplogDetails) {
						xdOplogDetail.setStatus(1);
						xdOplogDetail.setReason(comment);
						xdOplogDetail.update();
					}

				}
			}

			if(!"".equals(g3ids)){
				String[] summaryArr = g3ids.split(",");
				for (String sumId : summaryArr) {
					XdOplogSummary summary = XdOplogSummary.dao.findById(sumId);
					summary.setReason(comment);
					summary.setStatus(XdOperEnum.PASS.name());
					summary.update();

					String otype = summary.getOtype();
					if("D".equals(otype)){
						String changeb = summary.getChangeb();
						XdEdutrain edutrain = JSONUtil.jsonToBean(changeb, XdEdutrain.class);
						edutrain.delete();
					}else if("C".equals(otype)){
						String changea = summary.getChangea();
						XdEdutrain edutrain  = JSONUtil.jsonToBean(changea, XdEdutrain.class);
						edutrain.save();
					}else if("U".equals(otype)){
						String changea = summary.getChangea();
						XdEdutrain edutrain = JSONUtil.jsonToBean(changea, XdEdutrain.class);
						edutrain.update();
					}



					List<XdOplogDetail> xdOplogDetails = XdOplogDetail.dao.find("select * from  xd_oplog_detail where status='0' and rsid='" + sumId + "'");
					for (XdOplogDetail xdOplogDetail : xdOplogDetails) {
						xdOplogDetail.setStatus(1);
						xdOplogDetail.setReason(comment);
						xdOplogDetail.update();
					}

				}
			}

			List<XdOplogSummary> summaries = XdOplogSummary.dao.find("select * from xd_oplog_summary where oid='" + oid + "' and status='WAITAPPRO' and lastversion='0'");

			if(summaries.size()==0){
				steps.setFinished("Y");
				steps.setUserid(ShiroKit.getUserId());
				steps.setUsername(ShiroKit.getUsername());
				steps.setFinishtime(DateUtil.getCurrentTime());
				steps.setRemarks(comment);
				steps.update();
				XdEmployee employee = new XdEmployee();
				employee.setId(steps.getOid());
				employee.setName(steps.getStep());
				employee.setEmpnum(steps.getStepdesc());
				SysUser user = SysUser.dao.findById(steps.getCuserid());
				XdOperUtil.insertEmpoloyeeSteps(employee,stepsId,user.getOrgid(),user.getId(),user.getName(),steps.getSoptye(),"OA");
				renderSuccess("over");
			}else{
				renderSuccess("noover");
			}

		}

		XdOperUtil.updateEdu(oid);


	}


	public void noPass(){
		String stepsId = getPara("stepsId");
		XdSteps steps = XdSteps.dao.findById(stepsId);
		String oid = steps.getOid();
		String g5ids = getPara("g5ids");
		String g4ids = getPara("g4ids");
		String g3ids = getPara("g3ids");
		String comment = getPara("comment");
		if("".equals(g5ids)&&"".equals(g4ids)&&"".equals(g3ids)){

			List<XdOplogSummary> summaries = XdOplogSummary.dao.find("select * from xd_oplog_summary where oid='" + oid + "' and lastversion='0'");
			for (XdOplogSummary summary : summaries) {
				summary.setStatus(XdOperEnum.FAIL.name());
				summary.setReason(comment);
				summary.update();
				List<XdOplogDetail> xdOplogDetails = XdOplogDetail.dao.find("select * from  xd_oplog_detail where status='0' and rsid='" + summary.getId() + "'");
				for (XdOplogDetail xdOplogDetail : xdOplogDetails) {
					xdOplogDetail.setStatus(2);
					xdOplogDetail.setReason(comment);
					xdOplogDetail.update();
				}

			}
			steps.setFinished("Y");
			steps.setUserid(ShiroKit.getUserId());
			steps.setUsername(ShiroKit.getUsername());
			steps.setFinishtime(DateUtil.getCurrentTime());
			steps.setRemarks(comment);
			steps.update();
			XdEmployee employee = new XdEmployee();
			employee.setId(steps.getOid());
			employee.setName(steps.getStep());
			employee.setEmpnum(steps.getStepdesc());
			SysUser user = SysUser.dao.findById(steps.getCuserid());
			XdOperUtil.insertEmpoloyeeSteps(employee,stepsId,user.getOrgid(),user.getId(),user.getName(),steps.getSoptye(),"OA");
			renderSuccess("over");
		}else{
			if(!"".equals(g5ids)){
				String[] logsArr = g5ids.split(",");
				String rsid="";
				for (String logid : logsArr) {
					XdOplogDetail logDetail = XdOplogDetail.dao.findById(logid);
					rsid=logDetail.getRsid();
					logDetail.setStatus(2);
					logDetail.setReason(comment);
					logDetail.update();
				}
				List<XdOplogDetail> xdOplogDetails = XdOplogDetail.dao.find("select * from  xd_oplog_detail where status='0' and rsid='" + rsid + "'");
				if(xdOplogDetails.size()==0){
					XdOplogSummary summary = XdOplogSummary.dao.findById(rsid);
					summary.setReason(comment);
					summary.setStatus("FINISH");
					summary.update();
				}
			}
			if(!"".equals(g4ids)){
				String[] summaryArr = g4ids.split(",");
				for (String sumId : summaryArr) {
					XdOplogSummary summary = XdOplogSummary.dao.findById(sumId);
					summary.setReason(comment);
					summary.setStatus(XdOperEnum.FAIL.name());
					summary.update();
					List<XdOplogDetail> xdOplogDetails = XdOplogDetail.dao.find("select * from  xd_oplog_detail where status='0' and rsid='" + sumId + "'");
					for (XdOplogDetail xdOplogDetail : xdOplogDetails) {
						xdOplogDetail.setStatus(2);
						xdOplogDetail.setReason(comment);
						xdOplogDetail.update();
					}

				}
			}

			if(!"".equals(g3ids)){
				String[] summaryArr = g3ids.split(",");
				for (String sumId : summaryArr) {
					XdOplogSummary summary = XdOplogSummary.dao.findById(sumId);
					summary.setReason(comment);
					summary.setStatus(XdOperEnum.FAIL.name());
					summary.update();
					List<XdOplogDetail> xdOplogDetails = XdOplogDetail.dao.find("select * from  xd_oplog_detail where status='0' and rsid='" + sumId + "'");
					for (XdOplogDetail xdOplogDetail : xdOplogDetails) {
						xdOplogDetail.setStatus(2);
						xdOplogDetail.setReason(comment);
						xdOplogDetail.update();
					}

				}
			}

			List<XdOplogSummary> summaries = XdOplogSummary.dao.find("select * from xd_oplog_summary where oid='" + oid + "' and status='WAITAPPRO' and lastversion='0'");

			if(summaries.size()==0){
				steps.setFinished("Y");
				steps.setUserid(ShiroKit.getUserId());
				steps.setUsername(ShiroKit.getUsername());
				steps.setFinishtime(DateUtil.getCurrentTime());
				steps.setRemarks(comment);
				steps.update();
				XdEmployee employee = new XdEmployee();
				employee.setId(steps.getOid());
				employee.setName(steps.getStep());
				employee.setEmpnum(steps.getStepdesc());
				SysUser user = SysUser.dao.findById(steps.getCuserid());
				XdOperUtil.insertEmpoloyeeSteps(employee,stepsId,user.getOrgid(),user.getId(),user.getName(),steps.getSoptye(),"OA");
				renderSuccess("over");
			}else{
				renderSuccess("noover");
			}

		}

		XdOperUtil.updateEdu(oid);
	}

	/**
	 * @Method getWarningEmpList
	 * @Date 2022/12/21 16:19
	 * @Description 获取首页员工即将合同到期、试用到期页面
	 * @Author king
	 * @Version  1.0
	 * @Return void
	 */
	public void getWarningEmpList(){
		keepPara("warnType");
		renderIframe("pWarningList.html");
	}
	/**
	 * @Method listWaringData
	 * @Date 2022/12/21 16:20
	 * @Description  获取员工到期提醒数据
	 * @Author king
	 * @Version  1.0
	 * @Return void
	 */
	public void listWaringData(){
		String curr = getPara("pageNumber");
		String pageSize = getPara("pageSize");
		String warnType = getPara("warnType","");
		Page<Record> page = service.getPage(Integer.valueOf(curr),Integer.valueOf(pageSize),warnType);
		renderPage(page.getList(),"",page.getTotalRow());
	}


	/**
	 * @Method openWarningEmployeePage
	 * @Date 2022/12/21 17:23
	 * @Description 提醒信息详情
	 * @Author king
	 * @Version  1.0
	 * @Return void
	 */
	public void openWarningEmployeePage(){
		String id = getPara("id");
		String view = getPara("view");
		setAttr("view", view);
		XdEmployee o = new XdEmployee();
		if(StrKit.notBlank(id)){
			o = service.getById(id);
    		/*if("detail".equals(view)){
    		}*/
		}else{
			String uuid = UuidUtil.getUUID();
			System.out.println(uuid);
			o.setId(uuid);
		}

		List<XdDict> ismarry = XdDict.dao.find("select * from xd_dict where type ='ismarry'");
		setAttr("ismarry",ismarry);
		List<XdDict> nations = XdDict.dao.find("select * from xd_dict where type ='nation' order by sortnum");
		setAttr("nations",nations);
		List<XdDict> polities = XdDict.dao.find("select * from xd_dict where type ='polity' order by sortnum");
		setAttr("polities",polities);
		List<XdDict> edus = XdDict.dao.find("select * from xd_dict where type ='edu' order by sortnum");
		setAttr("edus",edus);
		List<XdDict> officestatus = XdDict.dao.find("select * from xd_dict where type ='officestatus' order by sortnum");
		setAttr("officestatus",officestatus);

		List<SysOrg> sysOrgs = SysOrg.dao.find("select * from sys_org where id<>'root' order by sort");
		setAttr("sysOrgs",sysOrgs);
		List<XdDict> units = XdDict.dao.find("select * from xd_dict where type ='unit' order by sortnum");
		setAttr("units",units);
		List<XdProjects> projects = XdProjects.dao.find("select * from xd_projects where status='1'");
		setAttr("projects",projects);
		List<XdDict> position = XdDict.dao.find("select * from xd_dict where type ='position' order by sortnum");
		setAttr("position",position);
		List<XdDict> hardstuff = XdDict.dao.find("select * from xd_dict where type ='hardstuff' order by sortnum");
		setAttr("hardstuff",hardstuff);

		setAttr("o", o);
		setAttr("formModelName",StringUtil.toLowerCaseFirstOne(XdEmployee.class.getSimpleName()));
		renderIframe("editWarning.html");
	}



	/**
	 * @Method exportExcel
	 * @Date 2022/12/15 14:31
	 * @Description  员工信息导出excle
	 * @Author king
	 * @Version  1.0
	 * @Return void
	 */
	public void exportExcel() throws UnsupportedEncodingException {

		String name = java.net.URLDecoder.decode(getPara("name",""),"UTF-8");
		String empnum = java.net.URLDecoder.decode(getPara("empnum",""),"UTF-8");
		String emprelation = java.net.URLDecoder.decode(getPara("emprelation",""),"UTF-8");
		String department=getPara("department","");
		String unitname=getPara("unitname","");
		String costitem=getPara("costitem","");
		String inductionstatus = getPara("inductionstatus","");
		String departime = getPara("departime","");
		String checked = getPara("checked","");
		String selectedName = new String(getPara("selectedName","").getBytes("ISO-8859-1"), "utf-8");
		String path = this.getSession().getServletContext().getRealPath("")+"/upload/export/"+DateUtil.format(new Date(),21)+".xlsx";
		File file = service.exportExcel(path,name,empnum,emprelation,department,unitname,costitem,inductionstatus,departime,checked,selectedName);
		renderFile(file);
	}
	/**
	 * @Method exportContractExcel
	 * @Date 2022/12/22 9:38
	 * @Exception 转码异常
	 * @Description 导出合同信息
	 * @Author king
	 * @Version  1.0
	 * @Return void
	 */
	public void exportContractExcel() throws UnsupportedEncodingException {

		String name = java.net.URLDecoder.decode(getPara("name",""),"UTF-8");
		String empnum = java.net.URLDecoder.decode(getPara("empnum",""),"UTF-8");
		String emprelation = java.net.URLDecoder.decode(getPara("emprelation",""),"UTF-8");
		String department=getPara("department","");
		String unitname=getPara("unitname","");
		String costitem=getPara("costitem","");
		String inductionstatus = getPara("inductionstatus","");
		String departime = getPara("departime","");
		String checked = getPara("checked","");
		String selectedName = new String(getPara("selectedName","").getBytes("ISO-8859-1"), "utf-8");
		String path = this.getSession().getServletContext().getRealPath("")+"/upload/export/"+DateUtil.format(new Date(),21)+".xlsx";
		File file = service.exportContractExcel(path,"","","","","","",inductionstatus,departime,checked,selectedName);
		renderFile(file);
	}


	 /**
	  * @Method importExcel
	  * @Date 2022/12/15 14:31
	  * @Description 员工信息excel导入
	  * @Author king
	  * @Version  1.0
	  * @Return void
	  */
	public void importExcel() throws IOException, SQLException {
		UploadFile file = getFile("file","/content");
		//List<List<String>> list = ExcelUtil.excelToList(file.getFile().getAbsolutePath());
		List<List<String>> list = ExcelUtil.excelToStringList(file.getFile().getAbsolutePath(),0);
		Map<String,Object> result = service.importExcel(list);
		if((Boolean)result.get("success")){
			renderSuccess((String)result.get("message"));
		}else{
			renderError((String)result.get("message"));
		}
	}



	/**
	 * @Method getPrintInfo
	 * @Date 2022/12/21 16:13
	 * @Description 获取打印信息
	 * @Author king
	 * @Version  1.0
	 * @Return void
	 */
	public void getPrintInfo() throws UnsupportedEncodingException {
		String name = java.net.URLDecoder.decode(getPara("name",""),"UTF-8");
		String empnum = java.net.URLDecoder.decode(getPara("empnum",""),"UTF-8");
		String department = java.net.URLDecoder.decode(getPara("department",""),"UTF-8");
		String emprelation = java.net.URLDecoder.decode(getPara("emprelation",""),"UTF-8");
		String unitname=java.net.URLDecoder.decode(getPara("unitname",""),"UTF-8");
		String costitem=java.net.URLDecoder.decode(getPara("costitem",""),"UTF-8");
		String checked=java.net.URLDecoder.decode(getPara("checked",""),"UTF-8");
		String selectedName=java.net.URLDecoder.decode(getPara("selectedName",""),"UTF-8");
		String inductionstatus=java.net.URLDecoder.decode(getPara("inductionstatus",""),"UTF-8");
		String departime=java.net.URLDecoder.decode(getPara("departime",""),"UTF-8");

		List<XdEmployee> employees = service.getPrintInfos(name, empnum, department, emprelation, unitname, costitem,inductionstatus,departime,checked,selectedName);
		Map<String, String> orgMap = DictMapping.orgMapping("0");
		Map<String, String> projectMap = DictMapping.projectsMappingValueToName();
		Map<String, Map<String, String>> dictMap = DictMapping.dictMappingValueToName();
		employees.stream().forEach( employee->{
				DictMapping.fieldValueToName(employee,orgMap,projectMap,dictMap);
				if(!ShiroKit.getUserOrgId().equals("1")){
					employee.setSalary(0);
					employee.setSalaryLevel("");
					employee.setSaladjrecord("");
				}
			}
		);


		List<PrintInfoVo> list=new ArrayList<>();
		for (XdEmployee emp : employees) {
			PrintInfoVo printInfoVo = new PrintInfoVo();


			String id = emp.getCertPicId();
			SysAttachment attachment = SysAttachment.dao.getById(id);
			if(attachment!=null){
				String fileUrl = attachment.getUrl();

				String baseUrl ="D:\\apache-tomcat-7.0.100";
				//String baseUrl ="D:\\apache-tomcat-7.0.100";
				System.out.println(baseUrl+"/upload"+fileUrl);
				File f = new File(baseUrl+"/upload"+fileUrl);
				String fileName = f.getName();
				String newFileName=emp.getId()+"."+fileName.substring(fileName.lastIndexOf(".") + 1);
				FileInputStream fis=null;
				FileOutputStream fos=null;
				try {
					 fis =new FileInputStream(baseUrl+"/upload"+fileUrl);
//					 fos =new FileOutputStream("D:\\apache-tomcat-7.0.100\\webapps\\XDM\\common\\"+newFileName);
					 fos =new FileOutputStream("D:\\apache-tomcat-7.0.100\\webapps\\XDM\\common\\"+newFileName);
					int len=0;
					byte[] bytes=new byte[1024];
						while ((len=fis.read(bytes))!=-1){

							fos.write(bytes,0,len);
						}


				} catch (Exception e) {
					e.printStackTrace();
				}finally {
					if(fos!=null){
						try {
							fos.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}

					if(fis!=null){
						try {
							fis.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
				System.out.println("http://localhost:8080/XDM/common/img/"+newFileName);
				emp.setCertPicId("http://localhost:8080/XDM/common/"+newFileName);
			}else{

				emp.setCertPicId("http://localhost:8080/XDM/common/img/profile-photos/1.png");
			}

			printInfoVo.setEmp(emp);
			List<XdEdutrain> xdEdutrainList = XdEdutrain.dao.find("select * from xd_edutrain where eid='"+emp.getId()+"'");
			xdEdutrainList.stream().forEach( edutrain-> edutrain.setEdubg(dictMap.get("edu").get(edutrain.getEdubg())));
			printInfoVo.setEdutrainList(xdEdutrainList);
			List<XdWorkExper> workExperList = XdWorkExper.dao.find("select * from xd_work_exper where eid='"+emp.getId()+"'");
			printInfoVo.setWorkExperList(workExperList);
			List<XdEmpCert> xdEmpCerts = XdEmpCert.dao.find("select * from  xd_emp_cert where eid='" + emp.getId() + "'");
			printInfoVo.setCertList(xdEmpCerts);
			list.add(printInfoVo);
		}
		renderJson(list);

	}



	public void getManagerListPage(){
		String userOrgId = ShiroKit.getUserOrgId();
		if("1".equals(userOrgId)){
			setAttr("personnel","Y");
		}else{
			setAttr("personnel","N");

		}
		Map<String, List<XdDict>> dictListByType = DictMapping.getDictListByType();
//		List<XdDict> units = XdDict.dao.find("select *from xd_dict where type='unit' order by  sortnum");
		setAttr("units",dictListByType.get("unit"));
		List<XdProjects> projects = XdProjects.dao.find("select * from xd_projects where status='1' ");
		setAttr("projects",projects);
		List<SysOrg> orgList = SysOrg.dao.find("select * from sys_org where id <>'root' order by sort");
		setAttr("depts",orgList);
		List<XdDict> empRelationList = dictListByType.get("empRelation");
		String empRelations = JSONUtil.listToJson(empRelationList);
		setAttr("empRelations",empRelations);
		renderIframe("managerList.html");
	}


	public void getEditManagerPage(){
		String id = getPara("id");
		String view = getPara("view");
		setAttr("view", view);
		XdEmployee o = new XdEmployee();
		o = service.getById(id);
			/*List<XdEffict> xdEfficts = XdEffict.dao.find("select * from  xd_effict where status='0' and eid='" + id + "'");

	if(xdEfficts!=null){
			for (XdEffict xdEffict : xdEfficts) {
				if(xdEffict.getFieldtype()==1){
					o.setWorkstation(xdEffict.getValues());
					setAttr("workEffectDate",xdEffict.getEffectDate());
					setAttr("workRemarks",xdEffict.getRemarks());
				}else if(xdEffict.getFieldtype()==2){
					o.setSalary(Integer.valueOf(xdEffict.getValues()));
					setAttr("salaryEffectDate",xdEffict.getEffectDate());
					setAttr("salaryRemarks",xdEffict.getRemarks());
				}
			}
		}*/

		XdEffict effict = XdEffict.dao.findFirst("select * from  xd_effict where  eid='" + id + "' order by ctime desc");
		if (effict != null) {

			setAttr("effectDate",effict.getEffectDate());
			setAttr("adjustReason",effict.getAdjustReason());
			setAttr("otherRemarks",effict.getOtherRemarks());

		}


		Map<String, List<XdDict>> dictListByType = DictMapping.getDictListByType();

		List<XdDict> ismarry = dictListByType.get("ismarry");
//				XdDict.dao.find("select * from xd_dict where type ='ismarry'");
		setAttr("ismarry",ismarry);
		List<XdDict> nations = dictListByType.get("nation");
//				XdDict.dao.find("select * from xd_dict where type ='nation' order by sortnum");
		setAttr("nations",nations);
		List<XdDict> polities = dictListByType.get("polity");
//				XdDict.dao.find("select * from xd_dict where type ='polity' order by sortnum");
		setAttr("polities",polities);
//		List<XdDict> edus = dictListByType.get("edu");
//				XdDict.dao.find("select * from xd_dict where type ='edu' order by sortnum");
		setAttr("edus",dictListByType.get("edu"));
//		List<XdDict> officestatus = dictListByType.get("officestatus");
//				XdDict.dao.find("select * from xd_dict where type ='officestatus' order by sortnum");
		setAttr("officestatus",dictListByType.get("officestatus"));

		List<SysOrg> sysOrgs = SysOrg.dao.find("select * from sys_org where id<>'root' order by sort");
		setAttr("sysOrgs",sysOrgs);
//		List<XdDict> units = dictListByType.get("unit");
//				XdDict.dao.find("select * from xd_dict where type ='unit' order by sortnum");
		setAttr("units",dictListByType.get("unit"));
//		List<XdDict> projects = XdDict.dao.find("select * from xd_dict where type ='projects' order by sortnum");
		List<XdProjects> projects = XdProjects.dao.find("select * from xd_projects where status='1'");
		setAttr("projects",projects);
		List<XdDict> position = dictListByType.get("position");
//				XdDict.dao.find("select * from xd_dict where type ='position' order by sortnum");
		setAttr("position",position);
//		List<XdDict> hardstuff = dictListByType.get("hardstuff");
//				XdDict.dao.find("select * from xd_dict where type ='hardstuff' order by sortnum");
		setAttr("hardstuff",dictListByType.get("hardstuff"));
//		List<XdDict> dutyList = dictListByType.get("duty");
		setAttr("duties",dictListByType.get("duty"));
		List<XdDict> empRelations = dictListByType.get("empRelation");
		setAttr("empRelations",empRelations);
		setAttr("o", o);
		setAttr("formModelName",StringUtil.toLowerCaseFirstOne(XdEmployee.class.getSimpleName()));
		renderIframe("managerEdit.html");
	}



	public void saveManagerBak(){
		XdEmployee o = getModel(XdEmployee.class);
		String id = o.getId();
		XdEmployee employee = XdEmployee.dao.findById(id);
		String salaryEffectDate = getPara("salaryEffectDate");
		String workEffectDate = getPara("workEffectDate");
		String workRemarks = getPara("workRemarks");
		String salaryRemarks = getPara("salaryRemarks");
		String gridData1 = getPara("gridData1");
		String gridData2 = getPara("gridData2");
		List<XdRecords> gridList1 = JSONUtil.jsonToBeanList(gridData1, XdRecords.class);
		List<XdRecords> gridList2 = JSONUtil.jsonToBeanList(gridData2, XdRecords.class);

		/*List<XdEffict> xdEfficts = XdEffict.dao.find("select * from  xd_effict where status='0' and eid='" + id + "'");
		xdEfficts.stream().forEach(new Consumer<XdEffict>() {
			@Override
			public void accept(XdEffict xdEffict) {
				xdEffict.setOvertime(DateUtil.getCurrentTime());
				xdEffict.setStatus("2");
				xdEffict.setOveruser(ShiroKit.getUserId());
				xdEffict.update();
			}
		});*/

		if(workEffectDate!=null&&!workEffectDate.trim().equals("")) {
			List<XdEffict> xdEfficts = XdEffict.dao.find("select * from  xd_effict where fieldtype='1' and status='0' and eid='" + id + "'");
			xdEfficts.stream().forEach( xdEffict-> {
				xdEffict.setOvertime(DateUtil.getCurrentTime());
				xdEffict.setStatus("2");
				xdEffict.setOveruser(ShiroKit.getUserId());
				xdEffict.update();
			});
			XdEffict xde = new XdEffict();
			xde.setEid(id);
			xde.setValues(o.getWorkstation());
			xde.setFieldtype(1);
			xde.setEffectDate(workEffectDate);
			xde.setRemarks(workRemarks);
			xde.setCtime(DateUtil.getCurrentTime());
			xde.setCuser(ShiroKit.getUserId());
			xde.setStatus("0");
			xde.save();
		}

		if(salaryEffectDate!=null&&!salaryEffectDate.trim().equals("")) {
			List<XdEffict> xdEfficts = XdEffict.dao.find("select * from  xd_effict where fieldtype='2' and status='0' and eid='" + id + "'");
			xdEfficts.stream().forEach( xdEffict-> {
				xdEffict.setOvertime(DateUtil.getCurrentTime());
				xdEffict.setStatus("2");
				xdEffict.setOveruser(ShiroKit.getUserId());
				xdEffict.update();
			});
			XdEffict xdsalary = new XdEffict();
			xdsalary.setEid(id);
			xdsalary.setValues(String.valueOf(o.getSalary()));
			xdsalary.setFieldtype(2);
			xdsalary.setEffectDate(salaryEffectDate);
			xdsalary.setRemarks(salaryRemarks);
			xdsalary.setCtime(DateUtil.getCurrentTime());
			xdsalary.setCuser(ShiroKit.getUserId());
			xdsalary.setStatus("0");
			xdsalary.save();
		}

		if(gridList1.size()!=0){
			for (int i = 0; i < gridList1.size(); i++) {
				XdRecords records = gridList1.get(i);
				records.update();
			}
		}
		if(gridList2.size()!=0){
			for (int i = 0; i < gridList2.size(); i++) {
				XdRecords records = gridList2.get(i);
				records.update();
			}
		}
		renderSuccess();
	}
	public void saveManager(){
		XdEmployee o = getModel(XdEmployee.class);
		String id = o.getId();
		XdEmployee employee = XdEmployee.dao.findById(id);
		String effectDate = getPara("effectDate");
		String adjustReason = getPara("adjustReason");
		String otherRemarks = getPara("otherRemarks");
		List<XdEffict> effictsList = XdEffict.dao.find("select * from  xd_effict where eid='" + o.getId() + "' and  status ='0'");
		for (XdEffict xdEffict : effictsList) {
			xdEffict.setStatus("2");
			xdEffict.setOveruser(ShiroKit.getUserId());
			xdEffict.setOvertime(DateUtil.getCurrentTime());
			xdEffict.update();
		}
		Map<String, Map<String, String>> stringMapMap = DictMapping.dictMappingValueToName();
		Map<String, String> duty = stringMapMap.get("duty");
		XdEffict effict=new XdEffict();
		effict.setEid(o.getId());
		effict.setEmpNum(o.getEmpnum());
		effict.setEmpName(o.getName());
		effict.setHireDate(o.getEntrytime());
		effict.setAdjustReason(adjustReason);
		effict.setOldDeptId(employee.getDepartment());
		SysOrg org = SysOrg.dao.findById(employee.getDepartment());
		effict.setOldDeptName(org.getName());
		effict.setOldPdvalue(employee.getWorkstation());
		effict.setOldPdname(duty.get(employee.getWorkstation()));
		effict.setOldSalaryLevel(employee.getSalaryLevel());
		effict.setOldSalary(Double.valueOf(employee.getSalary()));
		effict.setNewDeptId(o.getDepartment());
		org = SysOrg.dao.findById(o.getDepartment());
		effict.setNewDeptName(org.getName());
		effict.setNewPdvalue(o.getWorkstation());
		effict.setNewPdname(duty.get(o.getWorkstation()));
		effict.setNewSalaryLevel(o.getSalaryLevel());
		effict.setNewSalary(o.getSalary().doubleValue());
		effict.setEffectDate(effectDate);
		effict.setOtherRemarks(otherRemarks);
		effict.setStatus("0");
		effict.setCuser(ShiroKit.getUserId());
		effict.setCtime(DateUtil.getCurrentTime());
		effict.save();

		/*XdEmployee o = getModel(XdEmployee.class);
		String id = o.getId();
		XdEmployee employee = XdEmployee.dao.findById(id);
		String today = DateTimeFormatter.ofPattern("yyyy-MM-dd").format(LocalDate.now());
		String effectDate = getPara("effectDate");
		String adjustReason = getPara("adjustReason");
		String otherRemarks = getPara("otherRemarks");
		List<XdEffict> effictsList = XdEffict.dao.find("select * from  xd_effict where eid='" + o.getId() + "' and  status ='0'");
		for (XdEffict xdEffict : effictsList) {
			xdEffict.setStatus("2");
			xdEffict.setOveruser(ShiroKit.getUserId());
			xdEffict.setOvertime(DateUtil.getCurrentTime());
			xdEffict.update();
		}


		XdEffict xdEffict=new XdEffict();
		xdEffict.setEid(o.getId());
		xdEffict.setEmpNum(o.getEmpnum());
		xdEffict.setEmpName(o.getName());
		xdEffict.setHireDate(o.getEntrytime());
		xdEffict.setOldDeptId(employee.getDepartment());
		SysOrg org = SysOrg.dao.findById(employee.getDepartment());
		xdEffict.setOldDeptName(org==null?"":org.getName());
		xdEffict.setOldPdvalue(employee.getWorkstation());
		Map<String, Map<String, String>> stringMapMap = DictMapping.dictMappingValueToName();
		Map<String, String> duty = stringMapMap.get("duty");
		xdEffict.setOldPdname(duty.get(employee.getWorkstation()));
		xdEffict.setOldSalaryLevel(employee.getSalaryLevel());
		xdEffict.setOldSalary(Double.valueOf(employee.getSalary()));
		xdEffict.setNewDeptId(o.getDepartment());
		org = SysOrg.dao.findById(employee.getDepartment());
		xdEffict.setNewDeptName(org.getName());
		xdEffict.setNewPdvalue(o.getWorkstation());
		xdEffict.setNewPdname(duty.get(o.getWorkstation()));
		xdEffict.setNewSalaryLevel(o.getSalaryLevel());
		xdEffict.setNewSalary(Double.valueOf(o.getSalary()));
		if(today.equals(effectDate)){
			xdEffict.setStatus("1");
			o.update();
		}else{
			xdEffict.setStatus("0");
		}
		xdEffict.setAdjustReason(adjustReason);
		xdEffict.setOtherRemarks(otherRemarks);
		xdEffict.setEffectDate(effectDate);
		xdEffict.setCtime(DateUtil.getCurrentTime());
		xdEffict.setCuser(ShiroKit.getUserId());
		xdEffict.save();*/
		renderSuccess();
	}


	/**
	 * @Method immediateEffect
	 * @Date 2023/1/6 16:00
	 * @Description 员工薪资岗位立即生效
	 * @Author king
	 * @Version  1.0
	 * @Return void
	 */
	public void immediateEffect(){
		XdEmployee o = getModel(XdEmployee.class);
		String id = o.getId();
		XdEmployee employee = XdEmployee.dao.findById(id);
		String effectDate = getPara("effectDate");
		String adjustReason = getPara("adjustReason");
		String otherRemarks = getPara("otherRemarks");
		List<XdEffict> effictsList = XdEffict.dao.find("select * from  xd_effict where eid='" + o.getId() + "' and  status ='0'");
		for (XdEffict xdEffict : effictsList) {
			xdEffict.setStatus("2");
			xdEffict.setOveruser(ShiroKit.getUserId());
			xdEffict.setOvertime(DateUtil.getCurrentTime());
			xdEffict.update();
		}


		XdEffict xdEffict=new XdEffict();
		xdEffict.setEid(o.getId());
		xdEffict.setEmpNum(o.getEmpnum());
		xdEffict.setEmpName(o.getName());
		xdEffict.setHireDate(o.getEntrytime());
		xdEffict.setOldDeptId(employee.getDepartment());
		SysOrg org = SysOrg.dao.findById(employee.getDepartment());
		xdEffict.setOldDeptName(org==null?"":org.getName());
		xdEffict.setOldPdvalue(employee.getWorkstation());
		Map<String, Map<String, String>> stringMapMap = DictMapping.dictMappingValueToName();
		Map<String, String> duty = stringMapMap.get("duty");
		xdEffict.setOldPdname(duty.get(employee.getWorkstation()));
		xdEffict.setOldSalaryLevel(employee.getSalaryLevel());
		xdEffict.setOldSalary(Double.valueOf(employee.getSalary()));
		xdEffict.setNewDeptId(o.getDepartment());
		org = SysOrg.dao.findById(employee.getDepartment());
		xdEffict.setNewDeptName(org.getName());
		xdEffict.setNewPdvalue(o.getWorkstation());
		xdEffict.setNewPdname(duty.get(o.getWorkstation()));
		xdEffict.setNewSalaryLevel(o.getSalaryLevel());
		xdEffict.setNewSalary(Double.valueOf(o.getSalary()));
		xdEffict.setStatus("1");
		xdEffict.setAdjustReason(adjustReason);
		xdEffict.setOtherRemarks(otherRemarks);
		xdEffict.setEffectDate(effectDate);
		xdEffict.setCtime(DateUtil.getCurrentTime());
		xdEffict.setCuser(ShiroKit.getUserId());
		xdEffict.save();

		if(!employee.getWorkstation().equals(o.getWorkstation())){
			String chrecord = o.getChrecord();
		/*	if(chrecord!=null && !"".equals(chrecord) && !chrecord.endsWith(";")){
				chrecord=chrecord+";";
			}*/
			//int size = XdEffict.dao.find("select * from  xd_effict where eid='" + o.getId() + "' and  status ='1'").size();
//			String[] dateArr = effectDate.split("-");
//			chrecord=chrecord+size+"、"+dateArr[0]+"年"+dateArr[1]+"月"+xdEffict.getNewDeptName()+xdEffict.getNewPdname();
			int size=0;
			if(chrecord==null || "".equals(chrecord)){
				size=1;
				chrecord="";
			}else{
				if(!chrecord.endsWith(";")){
					chrecord=chrecord+";";
				}
				size=chrecord.split("、").length;
			}


			chrecord=chrecord+" "+size+"、"+xdEffict.getNewDeptName()+xdEffict.getNewPdname()+effectDate;
			if(xdEffict.getOtherRemarks()!=null && !"".equals(xdEffict.getOtherRemarks())){
				chrecord=chrecord+"("+xdEffict.getOtherRemarks()+")";
			}
			chrecord=chrecord+";";
			o.setChrecord(chrecord);
		}
		if((int)employee.getSalary()!=(int)o.getSalary()){
			String salaryRecord = o.getSaladjrecord();
			/*if(salaryRecord!=null && !"".equals(salaryRecord) && !salaryRecord.endsWith(";")){
				salaryRecord=salaryRecord+";";
			}*/
			//int size = XdEffict.dao.find("select * from  xd_effict where eid='" + o.getId() + "' and  status ='1'").size();
			int size=0;
			if(salaryRecord==null || salaryRecord.equals("")){
				size=1;
				salaryRecord="";
			}else{
				if(!salaryRecord.endsWith(";")){
					salaryRecord=salaryRecord+";";
				}
				size=salaryRecord.split("、").length;
			}


			salaryRecord=salaryRecord+" "+size+"、"+xdEffict.getNewSalaryLevel()+xdEffict.getNewSalary()+"\t"+effectDate;
			if(xdEffict.getOtherRemarks()!=null && !"".equals(xdEffict.getOtherRemarks())){
				salaryRecord=salaryRecord+"("+xdEffict.getOtherRemarks()+")";
			}
			salaryRecord=salaryRecord+";";
//			o.setChrecord(chrecord);
			o.setSaladjrecord(salaryRecord);
		}
		o.update();

	/*		List<XdEffict> xdEfficts = XdEffict.dao.find("select * from  xd_effict where fieldtype='1' and status='0' and eid='" + id + "'");
			xdEfficts.stream().forEach( xdEffict-> {
					xdEffict.setOvertime(DateUtil.getCurrentTime());
					xdEffict.setStatus("2");
					xdEffict.setOveruser(ShiroKit.getUserId());
					xdEffict.update();
			});*/
			/*XdEffict xde = new XdEffict();
			xde.setEid(id);
			xde.setValues(o.getWorkstation());
			xde.setFieldtype(1);
			xde.setEffectDate(workEffectDate);
			xde.setCtime(DateUtil.getCurrentTime());
			xde.setCuser(ShiroKit.getUserId());
			xde.setStatus("1");
			xde.setRemarks(workRemarks);
*/

		/*	XdRecords records=new XdRecords();
			records.setEffictDate(workEffectDate);
			records.setEid(id);
			records.setFieldType(2);
			records.setOldValue(employee.getWorkstation()==null?"":employee.getWorkstation());
			records.setNewValue(o.getWorkstation());
			records.setRemarks(workRemarks);
			records.setCtime(DateUtil.getCurrentTime());
			records.setCuser(ShiroKit.getUserId());
			records.save();*/

		/*	employee.setWorkstation(o.getWorkstation());
			employee.update();
			xde.save();*/

		/*if(salaryEffectDate!=null&&!salaryEffectDate.trim().equals("")) {
			List<XdEffict> xdEfficts = XdEffict.dao.find("select * from  xd_effict where fieldtype='2' and status='0' and eid='" + id + "'");
			xdEfficts.stream().forEach( xdEffict-> {
				xdEffict.setOvertime(DateUtil.getCurrentTime());
				xdEffict.setStatus("2");
				xdEffict.setOveruser(ShiroKit.getUserId());
				xdEffict.update();
			});
			XdEffict xdsalary = new XdEffict();
			xdsalary.setEid(id);
			xdsalary.setValues(String.valueOf(o.getSalary()));
			xdsalary.setFieldtype(2);
			xdsalary.setEffectDate(salaryEffectDate);
			xdsalary.setCtime(DateUtil.getCurrentTime());
			xdsalary.setCuser(ShiroKit.getUserId());
			xdsalary.setStatus("1");
			xdsalary.setRemarks(salaryRemarks);

			XdRecords records=new XdRecords();
			records.setEid(id);
			records.setEffictDate(salaryEffectDate);
			records.setFieldType(1);
			records.setOldValue(String.valueOf(employee.getSalary()));
			records.setNewValue(String.valueOf(o.getSalary()));
			records.setOldsallevel(employee.getSalaryLevel()==null?"":employee.getSalaryLevel());
			records.setNewsallevel(o.getSalaryLevel()==null?"":o.getSalaryLevel());
			records.setRemarks(salaryRemarks);
			records.setCtime(DateUtil.getCurrentTime());
			records.setCuser(ShiroKit.getUserId());
			records.save();

			employee.setSalary(o.getSalary());
			employee.setSalaryLevel(o.getSalaryLevel());
			employee.update();
			xdsalary.save();
		}*/


		/*if(gridList1.size()!=0){
			for (int i = 0; i < gridList1.size(); i++) {
				XdRecords records = gridList1.get(i);
				records.update();
			}
		}
		if(gridList2.size()!=0){
			for (int i = 0; i < gridList2.size(); i++) {
				XdRecords records = gridList2.get(i);
				records.update();
			}
		}*/
		renderSuccess();

	}
	public void immediateEffectBak(){
		XdEmployee o = getModel(XdEmployee.class);
		String id = o.getId();
		XdEmployee employee = XdEmployee.dao.findById(id);
		String salaryEffectDate = getPara("salaryEffectDate");
		String workEffectDate = getPara("workEffectDate");
		String workRemarks = getPara("workRemarks");
		String salaryRemarks = getPara("salaryRemarks");
		String gridData1 = getPara("gridData1");
		String gridData2 = getPara("gridData2");
		List<XdRecords> gridList1 = JSONUtil.jsonToBeanList(gridData1, XdRecords.class);
		List<XdRecords> gridList2 = JSONUtil.jsonToBeanList(gridData2, XdRecords.class);

		if(workEffectDate!=null&&!workEffectDate.trim().equals("")) {

			List<XdEffict> xdEfficts = XdEffict.dao.find("select * from  xd_effict where fieldtype='1' and status='0' and eid='" + id + "'");
			xdEfficts.stream().forEach( xdEffict-> {
					xdEffict.setOvertime(DateUtil.getCurrentTime());
					xdEffict.setStatus("2");
					xdEffict.setOveruser(ShiroKit.getUserId());
					xdEffict.update();
			});
			XdEffict xde = new XdEffict();
			xde.setEid(id);
			xde.setValues(o.getWorkstation());
			xde.setFieldtype(1);
			xde.setEffectDate(workEffectDate);
			xde.setCtime(DateUtil.getCurrentTime());
			xde.setCuser(ShiroKit.getUserId());
			xde.setStatus("1");
			xde.setRemarks(workRemarks);


			XdRecords records=new XdRecords();
			records.setEffictDate(workEffectDate);
			records.setEid(id);
			records.setFieldType(2);
			records.setOldValue(employee.getWorkstation()==null?"":employee.getWorkstation());
			records.setNewValue(o.getWorkstation());
			records.setRemarks(workRemarks);
			records.setCtime(DateUtil.getCurrentTime());
			records.setCuser(ShiroKit.getUserId());
			records.save();

			employee.setWorkstation(o.getWorkstation());
			employee.update();
			xde.save();
		}

		if(salaryEffectDate!=null&&!salaryEffectDate.trim().equals("")) {
			List<XdEffict> xdEfficts = XdEffict.dao.find("select * from  xd_effict where fieldtype='2' and status='0' and eid='" + id + "'");
			xdEfficts.stream().forEach( xdEffict-> {
				xdEffict.setOvertime(DateUtil.getCurrentTime());
				xdEffict.setStatus("2");
				xdEffict.setOveruser(ShiroKit.getUserId());
				xdEffict.update();
			});
			XdEffict xdsalary = new XdEffict();
			xdsalary.setEid(id);
			xdsalary.setValues(String.valueOf(o.getSalary()));
			xdsalary.setFieldtype(2);
			xdsalary.setEffectDate(salaryEffectDate);
			xdsalary.setCtime(DateUtil.getCurrentTime());
			xdsalary.setCuser(ShiroKit.getUserId());
			xdsalary.setStatus("1");
			xdsalary.setRemarks(salaryRemarks);

			XdRecords records=new XdRecords();
			records.setEid(id);
			records.setEffictDate(salaryEffectDate);
			records.setFieldType(1);
			records.setOldValue(String.valueOf(employee.getSalary()));
			records.setNewValue(String.valueOf(o.getSalary()));
			records.setOldsallevel(employee.getSalaryLevel()==null?"":employee.getSalaryLevel());
			records.setNewsallevel(o.getSalaryLevel()==null?"":o.getSalaryLevel());
			records.setRemarks(salaryRemarks);
			records.setCtime(DateUtil.getCurrentTime());
			records.setCuser(ShiroKit.getUserId());
			records.save();

			employee.setSalary(o.getSalary());
			employee.setSalaryLevel(o.getSalaryLevel());
			employee.update();
			xdsalary.save();
		}


		if(gridList1.size()!=0){
			for (int i = 0; i < gridList1.size(); i++) {
				XdRecords records = gridList1.get(i);
				records.update();
			}
		}
		if(gridList2.size()!=0){
			for (int i = 0; i < gridList2.size(); i++) {
				XdRecords records = gridList2.get(i);
				records.update();
			}
		}
		renderSuccess();

	}


	public void getEffectList(){
		String employeeId = getPara("employeeId");
//		List<XdRecords> list = service.getRecordsList(employeeId,filedType);
		List<XdEffict> xdEffictList = XdEffict.dao.find("select * from  xd_effict where eid='" + employeeId + "' order by ctime desc");
		renderJson(xdEffictList);
	}
}