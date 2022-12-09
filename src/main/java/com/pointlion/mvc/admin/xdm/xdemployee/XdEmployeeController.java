package com.pointlion.mvc.admin.xdm.xdemployee;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.pointlion.enums.XdOperEnum;
import com.pointlion.mvc.common.base.BaseController;
import com.pointlion.mvc.common.model.*;
import com.pointlion.mvc.common.utils.*;
import com.pointlion.plugin.shiro.ShiroKit;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


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
			o.update();
			XdOperUtil.logSummary(UuidUtil.getUUID(),o.getId(),o,employee,XdOperEnum.U.name(),XdOperEnum.WAITAPPRO.name());

			if(gridList1.size() == 0) {
				List<XdEdutrain> edutrainList = XdEdutrain.dao.find("select * from xd_edutrain where eid='" + id + "'");
				for (XdEdutrain xdEdutrain : edutrainList) {
					xdEdutrain.delete();
					XdOperUtil.logSummary(UuidUtil.getUUID(),id,null,xdEdutrain,XdOperEnum.D.name(),XdOperEnum.WAITAPPRO.name());
				}
			}else{
				for (XdEdutrain xdEdutrain : gridList1) {
					if("".equals(xdEdutrain.getId())){
						xdEdutrain.setEid(o.getId());
						xdEdutrain.save(xdEdutrain);
						XdOperUtil.logSummary(UuidUtil.getUUID(),o.getId(),null,xdEdutrain,XdOperEnum.C.name(),XdOperEnum.WAITAPPRO.name());
					}else{
						xdEdutrain.setEnrolldate(xdEdutrain.getEnrolldate().length()>9?xdEdutrain.getEnrolldate().substring(0,10):"");
						xdEdutrain.setGraduatdate(xdEdutrain.getGraduatdate().length()>9?xdEdutrain.getGraduatdate().substring(0,10):"");
						xdEdutrain.update();
					}
				}
			}
			if(gridList2.size() == 0) {
				Db.delete("delete from  xd_work_exper where eid='"+o.getId()+"'");
			}else{
				for (XdWorkExper workExper : gridList2) {
					if("".equals(workExper.getId())){
						workExper.setEid(o.getId());
						workExper.save(workExper);
					}else{
						XdWorkExper.dao.deleteByIds(workExper.getId());
						workExper.save();
					}
				}
			}

		}else{
			String operName = XdOperEnum.C.name();
			String operStatus = XdOperEnum.WAITAPPRO.name();
			String oid=UuidUtil.getUUID();
			o.setId(oid);
    		o.setCtime(DateUtil.getCurrentTime());
    		o.setCuser(ShiroKit.getUserId());
			ShiroKit.getUserOrgId();
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
    		if("detail".equals(view)){
    		}
    	}else{
//    		SysUser user = SysUser.dao.findById(ShiroKit.getUserId());
//    		SysOrg org = SysOrg.dao.findById(user.getOrgid());
//			o.setOrgId(org.getId());
//			o.setOrgName(org.getName());
//			o.setUserid(user.getId());
//			o.setApplyerName(user.getName());
//			 UUIDUtil.uuid().toString();
			String uuid = UuidUtil.getUUID();
			System.out.println(uuid);
			o.setId(uuid);
			if("1".equals(ShiroKit.getUserOrgId())){
				setAttr("oper","1");
			}else{
				setAttr("oper","0");
			}
		}
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
		if(step.getBackup1().equals("P")||step.getBackup1().equals("UP")){
			step.setFinished("Y");
			step.setFinishtime(DateUtil.getCurrentTime());
			step.update();
		}
		String s = (step.getRemarks() == null ? "" : step.getRemarks());
		setAttr("comments",s);
		String oid = step.getOid();
		List<XdOplogSummary> summaries =
				XdOplogSummary.dao.find("select * from xd_oplog_summary where oid='" + oid + "' and lastversion='0'");
		List<String> listEdu=new ArrayList<>();
		List<String> listWExper=new ArrayList<>();
		XdEmployee xdEmployee=null;
		for (XdOplogSummary summary : summaries) {
			if("C".equals(summary.getOtype())){
				if("XdEmployee".equals(summary.getTname())){
					String changea = summary.getChangea();
					xdEmployee = JSONUtil.jsonToBean(changea, XdEmployee.class);

				}else if("XdEdutrain".equals(summary.getTname())){
					//XdEdutrain xdEdutrain = JSONUtil.jsonToBean(summary.getChangea(), XdEdutrain.class);
					listEdu.add(summary.getChangea());
				}else{
					//XdWorkExper workExper = JSONUtil.jsonToBean(summary.getChangea(), XdWorkExper.class);
					listWExper.add(summary.getChangea());
				}
			}else if("D".equals(summary.getOtype())){

				if("XdEmployee".equals(summary.getTname())){
					String changeb = summary.getChangeb();
					xdEmployee = JSONUtil.jsonToBean(changeb, XdEmployee.class);

				}else if("XdEdutrain".equals(summary.getTname())){
					//XdEdutrain xdEdutrain = JSONUtil.jsonToBean(summary.getChangea(), XdEdutrain.class);
					listEdu.add(summary.getChangeb());
				}else{
					//XdWorkExper workExper = JSONUtil.jsonToBean(summary.getChangea(), XdWorkExper.class);
					listWExper.add(summary.getChangeb());
				}


			}else{

			}
		}
		setAttr("o",xdEmployee);
		setAttr("listEdu",listEdu);
		setAttr("listWExper",listWExper);
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
			//if("C".equals(summary.getOtype())){

			//String values = summary.getChangea();
				/*	if("XdEmployee".equals(tname)){
					XdEmployee xdEmployee = JSONUtil.jsonToBean(values, XdEmployee.class);
					xdEmployee.save();
				}else if("XdEdutrain".equals(tname)){
					XdEdutrain xdEdutrain = JSONUtil.jsonToBean(values, XdEdutrain.class);
					xdEdutrain.save();
				}else{
					XdWorkExper workExper = JSONUtil.jsonToBean(values, XdWorkExper.class);
					workExper.save();
				}*/

				try {
					Class clazz = Class.forName("com.pointlion.mvc.common.model." + tName);
					Method method =null;
					if("C".equals(oType)){
						values = summary.getChangea();
						method= clazz.getMethod("save");
					}else if("D".equals(oType)){
						values = summary.getChangeb();
						method = clazz.getMethod("delete");
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
			//}else if("D".equals(summary.getOtype())){
//				try {
//					Class clazz = Class.forName("com.pointlion.mvc.common.model." + tName);
//					//Method delete = clazz.getDeclaredMethod("delete");
//					Method delete = clazz.getMethod("delete");
//					delete.setAccessible(true);
//					delete.invoke(JSONUtil.jsonToBean(values,clazz));
//				} catch (ClassNotFoundException e) {
//					e.printStackTrace();
//				} catch (NoSuchMethodException e) {
//					e.printStackTrace();
//				} catch (IllegalAccessException e) {
//					e.printStackTrace();
//				} catch (InvocationTargetException e) {
//					e.printStackTrace();
//				}
//
//				summary.setStatus(XdOperEnum.PASS.name());
//				summary.update();
//			}
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

	}

}