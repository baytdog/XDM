package com.pointlion.mvc.admin.xdm.xdemployee;

import cn.hutool.core.lang.Dict;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.upload.UploadFile;
import com.pointlion.enums.XdOperEnum;
import com.pointlion.mvc.common.base.BaseController;
import com.pointlion.mvc.common.model.*;
import com.pointlion.mvc.common.utils.*;
import com.pointlion.mvc.common.utils.office.excel.ExcelUtil;
import com.pointlion.plugin.shiro.ShiroKit;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
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
		renderIframe("list.html");
    }
	/***
     * list page data
     **/
    public void listData(){
		String curr = getPara("pageNumber");
    	String pageSize = getPara("pageSize");
		String endTime = getPara("endTime","");
		String startTime = getPara("startTime","");
		String applyUser = getPara("applyUser","");
    	Page<Record> page = service.getPage(Integer.valueOf(curr),Integer.valueOf(pageSize),startTime,endTime,applyUser);
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
		}else{
			String operName = XdOperEnum.C.name();
			String operStatus = XdOperEnum.WAITAPPRO.name();
			String oid=UuidUtil.getUUID();
			o.setId(oid);
    		o.setCtime(DateUtil.getCurrentTime());
    		o.setCuser(ShiroKit.getUserId());
			if(!"1".equals(ShiroKit.getUserOrgId())){
				o.setBackup1("C");
				XdOperUtil.insertEmpoloyeeSteps(o,"","1","","","C");
				XdOperUtil.logSummary(oid,o,operName,operStatus,0);
			}else{
				o.save();
				XdOperUtil.logSummary(oid,o,operName,XdOperEnum.UNAPPRO.name(),0);
			}
    		if(gridList1.size()!=0){
				for (int i = 0; i < gridList1.size(); i++) {
					XdEdutrain xdEdutrain = gridList1.get(i);
					xdEdutrain.setEid(o.getId());
					xdEdutrain.setEname(o.getName());
					if("1".equals(ShiroKit.getUserOrgId())){
						xdEdutrain.save(xdEdutrain);
						XdOperUtil.logSummary(oid,xdEdutrain,operName,XdOperEnum.UNAPPRO.name(),0);
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
		List<XdDict> projects = XdDict.dao.find("select * from xd_dict where type ='projects' order by sortnum");
		setAttr("projects",projects);
		List<XdDict> position = XdDict.dao.find("select * from xd_dict where type ='position' order by sortnum");
		setAttr("position",position);
		List<XdDict> hardstuff = XdDict.dao.find("select * from xd_dict where type ='hardstuff' order by sortnum");
		setAttr("hardstuff",hardstuff);

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
		String sid = getPara("id");
		keepPara("id");
		XdSteps step = XdSteps.dao.findById(sid);
		String resultType = step.getBackup1();
	/*	if(resultType.equals("P")||resultType.equals("UP")){
			step.setFinished("Y");
			step.setFinishtime(DateUtil.getCurrentTime());
			step.update();
		}*/
		String s = (step.getRemarks() == null ? "" : step.getRemarks());
		if(resultType.equals("UP")){
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
		if("C".equals(resultType)){
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
		}else if("D".equals(resultType)){
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
		}else if("U".equals(resultType)){
			xdEmployee = XdEmployee.dao.findById(oid);
			List<XdEdutrain> edutrainList = XdEdutrain.dao.find("select * from  xd_edutrain where eid='" + oid + "'");
			for (XdEdutrain edutrain : edutrainList) {
				listEdu.add(JSONUtil.beanToJsonString(edutrain));
			}
			List<XdWorkExper> xdEdutrainList = XdWorkExper.dao.find("select * from  xd_work_exper where eid='" + oid + "'");
			for (XdWorkExper workExper : xdEdutrainList) {
				listWExper.add(JSONUtil.beanToJsonString(workExper));
			}
			List<XdOplogDetail> oplogEmployee  =new ArrayList<>();
			List<String> oplogEdu  =new ArrayList<>();
			List<String> oplogWork  =new ArrayList<>();
			for (XdOplogSummary summary : summaries) {
				if(summary.getTname().equals("XdEmployee")){
					oplogEmployee = XdOplogDetail.dao.find("select * from xd_oplog_detail where rsid='" + summary.getId() + "'");
				}
				if(summary.getTname().equals("XdEdutrain")){
					if(summary.getOtype().equals("C")){
						XdEdutrain xdEdutrain = JSONUtil.jsonToBean(summary.getChangea(), XdEdutrain.class);
						xdEdutrain.setBakcup2("新增");
						oplogEdu.add(JSONUtil.beanToJsonString(xdEdutrain));
					}else if(summary.getOtype().equals("D")){
						XdEdutrain xdEdutrain = JSONUtil.jsonToBean(summary.getChangeb(), XdEdutrain.class);
						xdEdutrain.setBakcup2("删除");
						oplogEdu.add(JSONUtil.beanToJsonString(xdEdutrain));
					}else {
						XdEdutrain xdEdutrain = JSONUtil.jsonToBean(summary.getChangeb(), XdEdutrain.class);
						List<XdOplogDetail> xdOplogDetails = XdOplogDetail.dao.find("select * from xd_oplog_detail where rsid='" + summary.getId() + "'");
						StringBuilder  sb = new StringBuilder("[ ");
						for (XdOplogDetail xdOplogDetail : xdOplogDetails) {
							sb.append(xdOplogDetail.getFieldDesc()+" ").append("原值 ： ")
									.append((xdOplogDetail.getOldValue()==null|| "".equals(xdOplogDetail.getOldValue()))?"空":xdOplogDetail.getOldValue())
									.append(", 现值 ： ").append((xdOplogDetail.getNewValue()==null||"".equals(xdOplogDetail.getNewValue()))?"空":xdOplogDetail.getNewValue())
									.append("  --  ");
						}
						String s1 = sb.toString().replaceAll("--  $", "")+" ]";
						xdEdutrain.setBakcup2("修改");
						xdEdutrain.setBackup3(s1);
						oplogEdu.add(JSONUtil.beanToJsonString(xdEdutrain));
					}

				}
				if(summary.getTname().equals("XdWorkExper")){
					if(summary.getOtype().equals("C")){
						XdWorkExper workExper = JSONUtil.jsonToBean(summary.getChangea(), XdWorkExper.class);
						workExper.setBackup2("新增");
						oplogWork.add(JSONUtil.beanToJsonString(workExper));
					}else if(summary.getOtype().equals("D")){
						XdWorkExper workExper = JSONUtil.jsonToBean(summary.getChangeb(), XdWorkExper.class);
						workExper.setBackup2("删除");
						oplogWork.add(JSONUtil.beanToJsonString(workExper));
					}else {

						XdWorkExper workExper = JSONUtil.jsonToBean(summary.getChangeb(), XdWorkExper.class);
						List<XdOplogDetail> xdOplogDetails = XdOplogDetail.dao.find("select * from xd_oplog_detail where rsid='" + summary.getId() + "'");
						StringBuilder  sb = new StringBuilder("[ ");
						for (XdOplogDetail xdOplogDetail : xdOplogDetails) {
							sb.append(xdOplogDetail.getFieldDesc()+" ").append("原值 ： ")
									.append((xdOplogDetail.getOldValue()==null|| "".equals(xdOplogDetail.getOldValue()))?"空":xdOplogDetail.getOldValue())
									.append(", 现值 ： ").append((xdOplogDetail.getNewValue()==null||"".equals(xdOplogDetail.getNewValue()))?"空":xdOplogDetail.getNewValue())
									.append("  --  ");
						}
						String s1 = sb.toString().replaceAll("--  $", "")+" ]";
						workExper.setBackup2("修改");
						workExper.setBackup3(s1);
						oplogWork.add(JSONUtil.beanToJsonString(workExper));
					}

				}
			}
			setAttr("oplogEmployee",oplogEmployee);
			setAttr("oplogEdu",oplogEdu);
			setAttr("oplogWork",oplogWork);
		}else if("P".equals(resultType)||"UP".equals(resultType)){
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
			}
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
		List<XdDict> projects = XdDict.dao.find("select * from xd_dict where type ='projects' order by sortnum");
		setAttr("projects",projects);
		List<XdDict> position = XdDict.dao.find("select * from xd_dict where type ='position' order by sortnum");
		setAttr("position",position);
		List<XdDict> hardstuff = XdDict.dao.find("select * from xd_dict where type ='hardstuff' order by sortnum");
		setAttr("hardstuff",hardstuff);
		setAttr("o",xdEmployee);
		setAttr("listEdu",listEdu);
		setAttr("listWExper",listWExper);
		setAttr("otype",resultType);
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

		List<XdOplogSummary> summaries = XdOplogSummary.dao.find("select * from xd_oplog_summary where oid='" + oid + "' and lastversion='0'");
		for (XdOplogSummary summary : summaries) {
			String tName = summary.getTname();
			String oType = summary.getOtype();
			String values="";
				try {
					Class clazz = Class.forName("com.pointlion.mvc.common.model." + tName);
					Method method =null;
					if("C".equals(oType)){
						values = summary.getChangea();
						method= clazz.getMethod("save");
					}else if("D".equals(oType)){
						values = summary.getChangeb();
						method = clazz.getMethod("delete");
					}else if("U".equals(oType)){
						method = clazz.getMethod("update");
						values = summary.getChangea();
						if("XdEmployee".equals(tName)){
							values = summary.getChangea();
							XdEmployee emp = (XdEmployee)JSONUtil.jsonToBean(values, clazz);
							List<XdOplogDetail> xdOplogDetails = XdOplogDetail.dao.find("select * from xd_oplog_detail where rsid='" + summary.getId() + "'");
							for (XdOplogDetail logDetail : xdOplogDetails) {
								if("salary".equals(logDetail.getFieldName())){
									String salaryRecord = (emp.getSaladjrecord()==null?"":emp.getSaladjrecord()+"\t" )+ "原工资: " + logDetail.getOldValue() + " - " + "最新工资: " + logDetail.getNewValue();

									emp.setSaladjrecord(salaryRecord);
								}
								if("contractclauses".equals(logDetail.getFieldName())){
									XdContractInfo contract =new XdContractInfo();
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
								if("workstation".equals(logDetail.getFieldName())){
									String workRecord = (emp.getChrecord()==null?"":emp.getChrecord()+"\t") + "原岗位: " + logDetail.getOldValue() + "-" + "最新岗位: " + logDetail.getNewValue();
									emp.setChrecord(workRecord);
								}
							}
							values=JSONUtil.beanToJsonString(emp);
						}
					}
					method.setAccessible(true);
					method.invoke(JSONUtil.jsonToBean(values,clazz));
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
				summary.update();
		}
		steps.setFinished("Y");
		steps.setUserid(ShiroKit.getUserId());
		steps.setUsername(ShiroKit.getUsername());
		steps.setFinishtime(DateUtil.getCurrentTime());
		steps.setRemarks(getPara("comment"));
		steps.update();
		XdEmployee employee = new XdEmployee();
		employee.setId(steps.getOid());
		employee.setName(steps.getStep());
		employee.setEmpnum(steps.getStepdesc());
		SysUser user = SysUser.dao.findById(steps.getCuserid());
		XdOperUtil.insertEmpoloyeeSteps(employee,stepsId,user.getOrgid(),user.getId(),user.getName(),"P");

		renderSuccess("操作成功!");
	}

	public void noPass(){
		String stepsId = getPara("stepsId");
		XdSteps steps = XdSteps.dao.findById(stepsId);
		String oid = steps.getOid();

		List<XdOplogSummary> summaries = XdOplogSummary.dao.find("select * from xd_oplog_summary where oid='" + oid + "' and lastversion='0'");
		for (XdOplogSummary summary : summaries) {
			summary.setStatus(XdOperEnum.FAIL.name());
			summary.update();
		}
		steps.setFinished("Y");
		steps.setUserid(ShiroKit.getUserId());
		steps.setUsername(ShiroKit.getUsername());
		steps.setFinishtime(DateUtil.getCurrentTime());
		steps.setRemarks(getPara("comment"));
		steps.update();
		XdEmployee employee = new XdEmployee();
		employee.setId(steps.getOid());
		employee.setName(steps.getStep());
		employee.setEmpnum(steps.getStepdesc());
		SysUser user = SysUser.dao.findById(steps.getCuserid());
		XdOperUtil.insertEmpoloyeeSteps(employee,stepsId,user.getOrgid(),user.getId(),user.getName(),"UP");

		renderSuccess("操作成功!");
	}




	/**
	 * @Method exportExcel
	 * @param :
	 * @Date 2022/12/15 14:31
	 * @Exception
	 * @Description  员工信息导出excle
	 * @Author king
	 * @Version  1.0
	 * @Return void
	 */
	public void exportExcel() throws UnsupportedEncodingException {

		/*String endTime = getPara("endTime","");
		String startTime = getPara("startTime","");*/
		String name = java.net.URLDecoder.decode(getPara("name",""),"UTF-8");
		String empnum = java.net.URLDecoder.decode(getPara("empnum",""),"UTF-8");
		String emprelation = getPara("emprelation","");
		String unitname=getPara("unitname","");
		String costitem=getPara("costitem","");
		String path = this.getSession().getServletContext().getRealPath("")+"/upload/export/"+DateUtil.format(new Date(),21)+".xlsx";
		File file = service.exportExcel(path,name,empnum,emprelation,unitname,costitem);
		renderFile(file);
	}
	public void exportContractExcel() throws UnsupportedEncodingException {

		String name = java.net.URLDecoder.decode(getPara("name",""),"UTF-8");
		String empnum = java.net.URLDecoder.decode(getPara("empnum",""),"UTF-8");
		String emprelation = getPara("emprelation","");
		String unitname=getPara("unitname","");
		String costitem=getPara("costitem","");
		String path = this.getSession().getServletContext().getRealPath("")+"/upload/export/"+DateUtil.format(new Date(),21)+".xlsx";
		File file = service.exportContractExcel(path,name,empnum,emprelation,unitname,costitem);
		renderFile(file);
	}


	 /**
	  * @Method importExcel
	  * @param :
	  * @Date 2022/12/15 14:31
	  * @Exception 员工信息excel导入
	  * @Description
	  * @Author king
	  * @Version  1.0
	  * @Return void
	  */
	public void importExcel() throws IOException, SQLException {
		UploadFile file = getFile("file","/content");
		List<List<String>> list = ExcelUtil.excelToList(file.getFile().getAbsolutePath());
		Map<String,Object> result = service.importExcel(list);
		if((Boolean)result.get("success")){
			renderSuccess((String)result.get("message"));
		}else{
			renderError((String)result.get("message"));
		}
	}


	public void printTest(){
		renderIframe("printTest.html");
	}


	public void getPrintInfo() throws UnsupportedEncodingException {
		String name = java.net.URLDecoder.decode(getPara("name",""),"UTF-8");
		String empnum = java.net.URLDecoder.decode(getPara("empnum",""),"UTF-8");
		String department = java.net.URLDecoder.decode(getPara("department",""),"UTF-8");
		String emprelation = java.net.URLDecoder.decode(getPara("emprelation",""),"UTF-8");
		String unitname=java.net.URLDecoder.decode(getPara("unitname",""),"UTF-8");
		String costitem=java.net.URLDecoder.decode(getPara("costitem",""),"UTF-8");
		List<XdEmployee> employees = service.getPrintInfos(name, empnum, department, emprelation, unitname, costitem);
		List<PrintInfoVo> list=new ArrayList<>();
		for (XdEmployee emp : employees) {
			PrintInfoVo printInfoVo = new PrintInfoVo();
			printInfoVo.setEmp(emp);
			List<XdEdutrain> xdEdutrainList = XdEdutrain.dao.find("select * from xd_edutrain where eid='"+emp.getId()+"'");
			printInfoVo.setEdutrainList(xdEdutrainList);
			List<XdWorkExper> workExperList = XdWorkExper.dao.find("select * from xd_work_exper where eid='"+emp.getId()+"'");
			printInfoVo.setWorkExperList(workExperList);
			list.add(printInfoVo);
		}
		renderJson(list);

	}

}