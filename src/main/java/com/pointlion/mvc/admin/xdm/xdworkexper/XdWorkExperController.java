package com.pointlion.mvc.admin.xdm.xdworkexper;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.upload.UploadFile;
import com.pointlion.enums.XdOperEnum;
import com.pointlion.mvc.common.base.BaseController;
import com.pointlion.mvc.common.model.*;
import com.pointlion.mvc.common.utils.*;
import com.pointlion.mvc.common.utils.office.excel.ExcelUtil;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;


public class XdWorkExperController extends BaseController {
	public static final XdWorkExperService service = XdWorkExperService.me;
	/***
	 * get list page
	 */
	public void getListPage(){
		renderIframe("list.html");
    }
	/***
     * list page data
     **/
    public void listData() throws UnsupportedEncodingException {
        String curr = getPara("pageNumber");
        String pageSize = getPara("pageSize");

		String name = java.net.URLDecoder.decode(getPara("name",""),"UTF-8");
		String serviceUnit = java.net.URLDecoder.decode(getPara("serviceUnit",""),"UTF-8");
		String job = java.net.URLDecoder.decode(getPara("job",""),"UTF-8");
		String addr = java.net.URLDecoder.decode(getPara("addr",""),"UTF-8");
		String entryDate =  getPara("entryDate","");
		String departDate =  getPara("departDate","");
        Page<Record> page = service.getPage(Integer.valueOf(curr),Integer.valueOf(pageSize),name,serviceUnit,job,addr,entryDate,departDate);
        renderPage(page.getList(),"",page.getTotalRow());
    }
    /***
     * save data
     */
    public void save(){
    	XdWorkExper o = getModel(XdWorkExper.class);
		XdWorkExper workExper = XdWorkExper.dao.findById(o.getId());
		String ename = o.getEname();
		XdEmployee employee = XdEmployee.dao.findFirst("select * from xd_employee where name ='" + ename + "'");
		if(workExper==null){
			o.setEid(employee.getId());
			if(o.getDepartdate()==null || "".equals(o.getDepartdate())){
				o.setDepartdate("至今");
			}



			List<XdWorkExper> workExperList = XdWorkExper.dao.find("select * from  xd_work_exper where eid='" + o.getEid() + "' order by entrydate desc");
			List <String>tipList=new ArrayList();
			boolean canInsert=true;
			if(workExperList.size()>0){
				for (XdWorkExper expr : workExperList) {
						if(o.getDepartdate().equals("至今")){
							LocalDate entry = LocalDate.parse(o.getEntrydate());
							if(expr.getDepartdate()==null || expr.getDepartdate().equals("")||expr.getDepartdate().equals("至今")){
								canInsert=false;
								tipList.add(expr.getEntrydate()+"-至今");
							}else {
								if (expr.getDepartdate().contains("年") && expr.getDepartdate().contains("月")) {
									String departDate = expr.getDepartdate().replaceAll("年", "-").replaceAll("[^(0-9-)]", "") + "-01";
									LocalDate oldDepart = LocalDate.parse(departDate);


									if (entry.isBefore(oldDepart) && entry.isAfter(LocalDate.parse(expr.getEntrydate()).minusDays(1))) {
										canInsert=false;
										tipList.add(expr.getEntrydate() + "-" + expr.getDepartdate());
									}

								} else if (expr.getDepartdate().contains("年")) {
									LocalDate oldDepart = LocalDate.parse(expr.getDepartdate().replaceAll("年", "-") + "-01-01").plusYears(1);
									if (entry.isBefore(oldDepart) && entry.isAfter(LocalDate.parse(expr.getEntrydate()).minusDays(1))) {
										canInsert=false;
										tipList.add(expr.getEntrydate() + "-" + expr.getDepartdate());
									}
								} else {
									LocalDate oldEntry = LocalDate.parse(expr.getEntrydate());
									LocalDate oldDepart = LocalDate.parse(expr.getDepartdate());

									if (entry.isBefore(oldDepart) && entry.isAfter(oldEntry)) {
										canInsert=false;
										tipList.add(expr.getEntrydate() + "-" + expr.getDepartdate());
									}


								}
							}
						}else{
							LocalDate entry = LocalDate.parse(o.getEntrydate());
							LocalDate depart = LocalDate.parse(o.getDepartdate());

							if(expr.getDepartdate()==null || expr.getDepartdate().equals("")||expr.getDepartdate().equals("至今")){

								LocalDate oldEntry = LocalDate.parse(expr.getEntrydate());
								if(entry.isAfter(oldEntry) ||depart.isAfter(oldEntry)){
									canInsert=false;
									tipList.add(expr.getEntrydate()+"-至今");
								}

							}else {
								if (expr.getDepartdate().contains("年") && expr.getDepartdate().contains("月")) {
									String departDate = expr.getDepartdate().replaceAll("年", "-").replaceAll("[^(0-9-)]", "") + "-01";
									LocalDate oldDepart = LocalDate.parse(departDate);
									LocalDate oldEntry = LocalDate.parse(expr.getEntrydate());

									if (entry.isBefore(oldDepart) && depart.isAfter(oldEntry)) {
										canInsert=false;
										tipList.add(expr.getEntrydate() + "-" + expr.getDepartdate());
									}

								} else if (expr.getDepartdate().contains("年")) {
									LocalDate oldDepart = LocalDate.parse(expr.getDepartdate().replaceAll("年", "-") + "-01-01").plusYears(1);
									LocalDate oldEntry = LocalDate.parse(expr.getEntrydate());
									if (entry.isBefore(oldEntry) && depart.isAfter(oldDepart)) {
										canInsert=false;
										tipList.add(expr.getEntrydate() + "-" + expr.getDepartdate());
									}
								} else {
									LocalDate oldEntry = LocalDate.parse(expr.getEntrydate());
									LocalDate oldDepart = LocalDate.parse(expr.getDepartdate());

									if (entry.isBefore(oldEntry) && depart.isAfter(oldDepart)) {
										canInsert=false;
										tipList.add(expr.getEntrydate() + "-" + expr.getDepartdate());
									}


								}
							}
						}

					}
				}


			if(canInsert){
				o.save();
				XdOperUtil.logSummary(o.getId(),o, XdOperEnum.C.name(),XdOperEnum.UNAPPRO.name(),0);
				renderSuccess();
			}else{
				String tips="冲突时间[";
					for (String s : tipList) {
						tips=tips+s+",";
					}
				renderError(tips.replaceAll(",$",""));
			}

		}else{
			String eduChanges = XdOperUtil.getChangedMetheds(o, workExper);
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
				summaryList.add(XdOperUtil.logSummary(lid,o.getId(),o,workExper, XdOperEnum.U.name(),XdOperEnum.UNAPPRO.name()));
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
			o.setEid(employee.getId());
			if(o.getDepartdate()==null || "".equals(o.getDepartdate())){
				o.setDepartdate("至今");
			}
			o.update();
			renderSuccess();
		}

    }
    /***
     * edit page
     */
    public void getEditPage(){
    	String id = getPara("id");
    	String view = getPara("view");
		setAttr("view", view);
		XdWorkExper o = new XdWorkExper();
		if(StrKit.notBlank(id)){
			o = service.getById(id);
			setAttr("op","mod");
			List<XdWorkExper> workExperList = XdWorkExper.dao.find("select * from xd_work_exper where eid='" + o.getEid() + "' and departdate in('至今','')");
			String needEndDate="N";
			if(workExperList.size()>0&& o.getDepartdate()!=null && !o.getDepartdate().equals("至今")&& !o.getDepartdate().equals("")){
				needEndDate="Y";
			}
			setAttr("needEndD",needEndDate);
		}else{
			o.setId(UuidUtil.getUUID());
			setAttr("op","add");
		}


		List<XdEmployee> emps = XdEmployee.dao.find("select * from  xd_employee order by empnum");
		setAttr("emps",emps);
		setAttr("o", o);
    	setAttr("formModelName",StringUtil.toLowerCaseFirstOne(XdWorkExper.class.getSimpleName()));
		renderIframe("edit.html");
    }
    /***
     * del
     * @throws Exception
     */
    public void delete() throws Exception{
		String ids = getPara("ids");
		service.deleteByIds(ids);
    	renderSuccess("删除成功!");
    }
	public void getWorkExperList(){
		String employeeId = getPara("employeeId");
		List<XdWorkExper> list = service.getWorkExperList(employeeId);
		renderJson(list);
	}


	public void validateDate(){

		String empId = getPara("empId");

		String chooseDate=getPara("chooseDate");
		String id = getPara("id");
//		XdEmployee emp = XdEmployee.dao.findById(getPara("id"));
		List<XdWorkExper> workExperList = XdWorkExper.dao.find("select * from  xd_work_exper where eid='" + empId + "' order by entrydate desc");
		String canUse="Y";
		String tips="";
		List <String>tipList=new ArrayList();
		LocalDate choose = LocalDate.parse(chooseDate);
		if(workExperList.size()>0){
			for (XdWorkExper workExper : workExperList) {
				if(!workExper.getId().equals(id)){
					if(workExper.getDepartdate()==null || workExper.getDepartdate().equals("")||workExper.getDepartdate().equals("至今")){
						boolean after = choose.isAfter(LocalDate.parse(workExper.getEntrydate()));
						if(after || chooseDate.equals(workExper.getEntrydate())){
							System.out.println("最近一次合同时间冲突");
							canUse="N";
							tipList.add(workExper.getEntrydate()+"-至今");
						}
					}else{
						if(workExper.getDepartdate().contains("年")&&workExper.getDepartdate().contains("月")){
							/*String year = xdWorkExper.getDepartdate().substring(0, 4);
							String month = xdWorkExper.getDepartdate().substring(0, 4);*/
							String departDate = workExper.getDepartdate().replaceAll("年", "-").replaceAll("[^(0-9-)]", "") + "-01";
							LocalDate localDate = LocalDate.parse(departDate).plusDays(1);

							if(choose.isBefore(localDate) && choose.isAfter(LocalDate.parse(workExper.getEntrydate()).minusDays(1))){
								System.out.println("合同时间冲突");
								canUse="N";
								tipList.add(workExper.getEntrydate()+"-"+workExper.getDepartdate());
							}

						}else if(workExper.getDepartdate().contains("年")){
							LocalDate localDate = LocalDate.parse(workExper.getDepartdate().replaceAll("年", "-") + "-01-01").plusYears(1);
							if(choose.isBefore(localDate) && choose.isAfter(LocalDate.parse(workExper.getEntrydate()).minusDays(1))){
								System.out.println("合同时间冲突");
								canUse="N";
								tipList.add(workExper.getEntrydate()+"-"+workExper.getDepartdate());
							}
						}else{
							LocalDate entry = LocalDate.parse(workExper.getEntrydate()).minusDays(1);
							LocalDate depart = LocalDate.parse(workExper.getDepartdate()).plusDays(1);

							if(choose.isBefore(depart) && choose.isAfter(entry)){
								System.out.println("合同时间冲突");
								canUse="N";
								tipList.add(workExper.getEntrydate()+"-"+workExper.getDepartdate());
							}

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
		//jsonObject.put("empNum",emp.getEmpnum());

		jsonObject.put("canUse",canUse);
		jsonObject.put("tips",tips);
		renderJson(jsonObject);


	}


	public void needEndDate(){
		String eid = getPara("eid");
		List<XdWorkExper> workExperList = XdWorkExper.dao.find("select * from xd_work_exper where eid='" + eid + "' and departdate in('至今','')");
		String needEndDate="N";
		if(workExperList.size()>0){
			needEndDate="Y";
		}
		cn.hutool.json.JSONObject jsonObject=new cn.hutool.json.JSONObject();
		//jsonObject.put("empNum",emp.getEmpnum());

		jsonObject.put("needEndDate",needEndDate);
		renderJson(jsonObject);


	}



	/**
	 * @Method exportExcel
	 * @param :
	 * @Date 2022/12/17 9:28
	 * @Exception
	 * @Description
	 * @Author king
	 * @Version  1.0
	 * @Return void
	 */
	public void exportExcel() throws UnsupportedEncodingException {


		String name = java.net.URLDecoder.decode(getPara("name",""),"UTF-8");
		String serviceUnit = java.net.URLDecoder.decode(getPara("serviceUnit",""),"UTF-8");
		String job = java.net.URLDecoder.decode(getPara("job",""),"UTF-8");
		String addr = java.net.URLDecoder.decode(getPara("addr",""),"UTF-8");
		String entryDate =  getPara("entryDate","");
		String departDate =  getPara("departDate","");
		String path = this.getSession().getServletContext().getRealPath("")+"/upload/export/"+ DateUtil.format(new Date(),21)+".xlsx";
		File file = service.exportExcel(path,"","","","","","");
		renderFile(file);
	}

	/**
	 * @Method importExcel
	 * @param :
	 * @Date 2023/1/3 14:05
	 * @Exception
	 * @Description 导入数据
	 * @Author king
	 * @Version  1.0
	 * @Return void
	 */
	public void importExcel() throws IOException, SQLException {
		UploadFile file = getFile("file","/content");
		List<List<String>> list = ExcelUtil.excelToStringList(file.getFile().getAbsolutePath(),3);
		Map<String,Object> result = service.importExcel(list);
		if((Boolean)result.get("success")){
			renderSuccess((String)result.get("message"));
		}else{
			renderError((String)result.get("message"));
		}
	}





}