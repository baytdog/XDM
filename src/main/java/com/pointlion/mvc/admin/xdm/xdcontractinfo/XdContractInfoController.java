package com.pointlion.mvc.admin.xdm.xdcontractinfo;

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

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;



public class XdContractInfoController extends BaseController {
	public static final XdContractInfoService service = XdContractInfoService.me;
	/***
	 * get list page
	 */
	public void getListPage(){

		List<SysOrg> orgList = SysOrg.dao.find("select * from  sys_org where id !='root' order by sort");

		setAttr("orgs",orgList);
		renderIframe("list.html");
    }
	/***
     * list page data
     **/
    public void listData() throws UnsupportedEncodingException {
    	String curr = getPara("pageNumber");
    	String pageSize = getPara("pageSize");
		String name = java.net.URLDecoder.decode(getPara("name",""),"UTF-8");
		String empNum = getPara("empNum","");
		String startTime = getPara("startTime","");
		String department =getPara("department","");
    	Page<Record> page = service.getPage(Integer.valueOf(curr),Integer.valueOf(pageSize),name,empNum,startTime,department);
    	renderPage(page.getList(),"",page.getTotalRow());
    }
    /***
     * save data
     */
    public void save(){
    	XdContractInfo o = getModel(XdContractInfo.class);
		XdContractInfo contractInfo = XdContractInfo.dao.findById(o.getId());

		if(contractInfo==null){
//			o.setEid(employee.getId());
			if(o.getContractenddate()==null || "".equals(o.getContractenddate())){
				o.setContractenddate("无固定期限");
			}
			List<XdContractInfo> contractInfoList = XdContractInfo.dao.find("select * from  xd_contract_info where eid='" + o.getEid() + "' order by contractenddate desc");
			List <String>tipList=new ArrayList();
			boolean canInsert=true;
			if(contractInfoList.size()>0){
				for (XdContractInfo contract : contractInfoList) {
					if(o.getContractenddate().equals("无固定期限")){
						LocalDate start = LocalDate.parse(o.getContractstartdate());
						if(contract.getContractenddate()==null || contract.getContractenddate().equals("")||contract.getContractenddate().equals("无固定期限")){
							canInsert=false;
							tipList.add(contract.getContractstartdate()+"-无固定期限");
						}else {
								LocalDate oldStart = LocalDate.parse(contract.getContractstartdate());
								LocalDate oldEnd = LocalDate.parse(contract.getContractenddate());

								if (start.isBefore(oldEnd) && start.isAfter(oldStart)) {
									canInsert=false;
									tipList.add(contract.getContractstartdate() + "-" + contract.getContractenddate());
								}


						}
					}else{
						LocalDate newStart = LocalDate.parse(o.getContractstartdate());
						LocalDate newEnd = LocalDate.parse(o.getContractenddate());
						LocalDate oldStart = LocalDate.parse(contract.getContractstartdate());

						if(contract.getContractenddate()==null || contract.getContractenddate().equals("")||contract.getContractenddate().equals("无固定期限")){

							if(newStart.isAfter(oldStart) ||newEnd.isAfter(oldStart)){
								canInsert=false;
								tipList.add(contract.getContractstartdate()+"-至今");
							}

						}else {

							LocalDate oldEnd = LocalDate.parse(contract.getContractenddate());
								if (newStart.isBefore(oldStart) && newEnd.isAfter(oldEnd)) {
									canInsert=false;
									tipList.add(contract.getContractstartdate() + "-" + contract.getContractenddate());
								}


						}
					}

				}
			}


			if(canInsert){
				o.setCtime(DateUtil.getCurrentTime());
				o.setCuser(ShiroKit.getUserId());
				o.save();
				XdOperUtil.logSummary(o,null, XdOperEnum.C.name(),o.getClass());
				service.updateInfos(o.getEmpName());
				renderSuccess();
			}else{
				String tips="冲突时间[";
				for (String s : tipList) {
					tips=tips+s+",";
				}
				renderError(tips.replaceAll(",$",""));
			}


		}else{
			String eduChanges = XdOperUtil.getChangedMetheds(o, contractInfo);
			eduChanges = eduChanges.replaceAll("--$","");
			List<XdOplogSummary> summaryList =new ArrayList<>();
			List<XdOplogDetail> list =new ArrayList<>();
			if(!"".equals(eduChanges)){
				String lid=UuidUtil.getUUID();
				String[] eduCArray = eduChanges.split("--");
				for (String change : eduCArray) {
					change="{"+change+"}";
					XdOplogDetail logDetail = JSONUtil.jsonToBean(change, XdOplogDetail.class);
					logDetail.setRsid(lid);
					list.add(logDetail);
				}
				summaryList.add(XdOperUtil.logSummary(lid,o.getId(),o,contractInfo, XdOperEnum.U.name(),XdOperEnum.UNAPPRO.name()));
			}

			if (summaryList.size() > 0) {
				XdOperUtil.queryLastVersion(o.getId());
			}
			for (XdOplogSummary xdOplogSummary : summaryList) {
				xdOplogSummary.save();
			}
			for (XdOplogDetail detail : list) {
				detail.setId(UuidUtil.getUUID());
				detail.save();
			}
//			o.setEid(employee.getId());
			if(o.getContractenddate()==null || "".equals(o.getContractenddate())){
				o.setContractenddate("无固定期限");
			}
			o.update();

			XdOperUtil.logSummary(o,contractInfo, XdOperEnum.U.name(),o.getClass());
			service.updateInfos(o.getEmpName());
			renderSuccess();
		}

    /*	if(StrKit.notBlank(o.getId())){
    		o.update();
    	}else{
    		o.setId(UuidUtil.getUUID());
    		o.save();
    	}*/
    	renderSuccess();
    }
    /***
     * edit page
     */
    public void getEditPage(){
    	String id = getPara("id");
    	String view = getPara("view");
		setAttr("view", view);
		XdContractInfo o = new XdContractInfo();
		if(StrKit.notBlank(id)){
    		o = service.getById(id);
			List<XdContractInfo> xdContractInfoList = XdContractInfo.dao.find("select * from xd_contract_info where eid='" + o.getEid() + "' and contractenddate='无固定期限'");
			String needEndDate="N";
			if(xdContractInfoList.size()>0 && o.getContractenddate()!=null && !o.getContractenddate().equals("无固定期限")&& !o.getContractenddate().equals("")){
				needEndDate="Y";
			}
			setAttr("needEndD",needEndDate);
    	}else{
    		SysUser user = SysUser.dao.findById(ShiroKit.getUserId());
    		SysOrg org = SysOrg.dao.findById(user.getOrgid());
    		o.setId(UuidUtil.getUUID());
    	}
		List<XdEmployee> empList = XdEmployee.dao.find("select * from  xd_employee order by empnum");
		setAttr("emps",empList);
		List<XdDict> empRelationsList = XdDict.dao.find("select * from xd_dict where type= 'empRelation' order by sortnum");
		setAttr("empRelations",empRelationsList);
		setAttr("o", o);
    	setAttr("formModelName",StringUtil.toLowerCaseFirstOne(XdContractInfo.class.getSimpleName()));
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

	public void importExcel() throws IOException, SQLException {
		UploadFile file = getFile("file","/content");
		List<List<String>> list = ExcelUtil.excelToStringList(file.getFile().getAbsolutePath(),2);
		Map<String,Object> result = service.importExcel(list);
		if((Boolean)result.get("success")){
			renderSuccess((String)result.get("message"));
		}else{
			renderError((String)result.get("message"));
		}
	}

	public void getRelationInfo(){
		String eid = getPara("eid");
		XdContractInfo lastContract = XdContractInfo.dao.findFirst("select * from xd_contract_info where eid='" + eid + "' order by contractclauses desc");
	/*	String needEndDate="N";
		if(workExperList.size()>0){
			needEndDate="Y";
		}*/
		int contractNum=1;
		if(lastContract!=null){
			contractNum=lastContract.getContractclauses()+1;
		}

		cn.hutool.json.JSONObject jsonObject=new cn.hutool.json.JSONObject();
		//jsonObject.put("empNum",emp.getEmpnum());



		List<XdContractInfo> xdContractInfoList = XdContractInfo.dao.find("select * from xd_contract_info where eid='" + eid + "' and contractenddate='无固定期限' ");
		String needEndDate="N";
		if(xdContractInfoList.size()>0){
			needEndDate="Y";
		}

		jsonObject.put("contractNum",contractNum);
		jsonObject.put("needEndDate",needEndDate);
		renderJson(jsonObject);
	}


	public void validateDate(){

		String empId = getPara("empId");
		String chooseDate=getPara("chooseDate");
		String id = getPara("id");
		List<XdContractInfo> contractInfoList= XdContractInfo.dao.find("select * from  xd_contract_info where eid='" + empId + "' order by contractstartdate desc");
		String canUse="Y";
		String tips="";
		List <String>tipList=new ArrayList();
		LocalDate choose = LocalDate.parse(chooseDate);
		if(contractInfoList.size()>0){
			for (XdContractInfo contract : contractInfoList) {
				if(!contract.getId().equals(id)){
					if(contract.getContractenddate()==null || contract.getContractenddate().equals("")||contract.getContractenddate().equals("无固定期限")){
						boolean after = choose.isAfter(LocalDate.parse(contract.getContractstartdate()));
						if(after || chooseDate.equals(contract.getContractstartdate())){
							canUse="N";
							tipList.add(contract.getContractstartdate()+"-无固定期限");
						}
					}else{
								LocalDate start = LocalDate.parse(contract.getContractstartdate()).minusDays(1);
								LocalDate end = LocalDate.parse(contract.getContractenddate()).plusDays(1);

								if(choose.isBefore(end) && choose.isAfter(start)) {
									canUse = "N";
									tipList.add(contract.getContractstartdate() + "-" + contract.getContractenddate());
								}
					}


				}
			}
		}
		if(tipList.size()>0){
			tips="冲突时间[";
			for (String str : tipList) {
				tips=tips+str+",";
			}
			tips=tips.replaceAll(",$","");
			tips=tips+"]";
		}

		cn.hutool.json.JSONObject jsonObject=new cn.hutool.json.JSONObject();
		jsonObject.put("canUse",canUse);
		jsonObject.put("tips",tips);
		renderJson(jsonObject);


	}



	public void exportContractExcel() throws UnsupportedEncodingException {

		String name = java.net.URLDecoder.decode(getPara("name",""),"UTF-8");
		String empNum = java.net.URLDecoder.decode(getPara("empNum",""),"UTF-8");
		String startTime = java.net.URLDecoder.decode(getPara("startTime",""),"UTF-8");
		String department=getPara("department","");
		String path = this.getSession().getServletContext().getRealPath("")+"/upload/export/"+DateUtil.format(new Date(),21)+".xlsx";
		File file = service.exportContractExcel(path,"",empNum,startTime,department);
		renderFile(file);
	}
}