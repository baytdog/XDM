package com.pointlion.mvc.admin.xdm.xdempcert;

import java.io.File;
import java.io.IOException;
import java.io.SyncFailedException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Consumer;

import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.upload.UploadFile;
import com.pointlion.enums.XdOperEnum;
import com.pointlion.mvc.common.base.BaseController;
import com.pointlion.mvc.admin.oa.workflow.WorkFlowService;
import com.pointlion.mvc.common.model.*;
import com.pointlion.mvc.common.utils.*;
import com.pointlion.mvc.admin.oa.common.OAConstants;
import com.pointlion.mvc.common.utils.office.excel.ExcelUtil;
import com.pointlion.plugin.shiro.ShiroKit;



public class XdEmpCertController extends BaseController {
	public static final XdEmpCertService service = XdEmpCertService.me;
	/***
	 * get list page
	 */
	public void getListPage(){

		List<XdEmpCert> snyList = XdEmpCert.dao.find("select  DISTINCT sny from xd_emp_cert order by sny");
		setAttr("snyList",snyList);

		renderIframe("list.html");
    }
	/***
     * list page data
     **/
    public void listData() throws UnsupportedEncodingException {
    	String curr = getPara("pageNumber");
    	String pageSize = getPara("pageSize");

		String dept =  getPara("dept","");
		String name = java.net.URLDecoder.decode(getPara("name",""),"UTF-8");
		String certTitle = java.net.URLDecoder.decode(getPara("certTitle",""),"UTF-8");
		String certAuth = java.net.URLDecoder.decode(getPara("certAuth",""),"UTF-8");
		String sny = java.net.URLDecoder.decode(getPara("sny",""),"UTF-8");
		String ctime = java.net.URLDecoder.decode(getPara("ctime",""),"UTF-8");
    	Page<Record> page = service.getPage(Integer.valueOf(curr),Integer.valueOf(pageSize),dept,name,certTitle,certAuth,sny,ctime);
    	renderPage(page.getList(),"",page.getTotalRow());
    }
    /***
     * save data
     */
    public void save(){
    	XdEmpCert o = getModel(XdEmpCert.class);
		XdEmpCert cert = XdEmpCert.dao.findById(o.getId());
		if(cert!=null){
			String closeDate = o.getCloseDate();
			if("??????".equals(closeDate)){
				o.setSny("??????");
				o.setSn("??????");
			}else if(!"".equals(closeDate.trim())){
				String ny = closeDate.replaceFirst("-", "???").replaceFirst("-", "???").substring(0, 8);
				System.out.println(ny);
				String n = ny.substring(0,5);
				System.out.println(n);
				o.setSny(ny);
				o.setSn(n);
			}
			o.update();
			XdOplogSummary summary = XdOperUtil.logSummary(UuidUtil.getUUID(), cert.getId(), o, cert, XdOperEnum.U.name(), XdOperEnum.UNAPPRO.name());
			summary.save();
		}else{
			String closeDate = o.getCloseDate();
			if("??????".equals(closeDate)){
				o.setSny("??????");
				o.setSn("??????");
			}else if(!"".equals(closeDate.trim())){
				String ny = closeDate.replaceFirst("-", "???").replaceFirst("-", "???").substring(0, 8);
				System.out.println(ny);
				String n = ny.substring(0,5);
				System.out.println(n);
				o.setSny(ny);
				o.setSn(n);
			}
			o.setStatus("1");
			o.save();
			XdOperUtil.logSummary(o.getId(),o, XdOperEnum.C.name(),XdOperEnum.UNAPPRO.name(),0);
    	}

		XdOperUtil.updateEmpCert(o);
    	renderSuccess();
    }




    /***
     * edit page
     */
    public void getEditPage(){
    	String id = getPara("id");
    	String view = getPara("view");
		setAttr("view", view);
		XdEmpCert o = new XdEmpCert();
		if(StrKit.notBlank(id)){
    		o = service.getById(id);
    	}else{
			o.setId(UuidUtil.getUUID());
    	}

		List<XdEmployee> emps = XdEmployee.dao.find("select * from  xd_employee");
		setAttr("emps",emps);
		List<SysOrg> sysOrgs = SysOrg.dao.find("select * from sys_org where id <> 'root'");
		setAttr("departs",sysOrgs);

		List<XdCertificate> xdCertificates = XdCertificate.dao.find("select * from xd_certificate");
		setAttr("certs",xdCertificates);

		List<XdDict> certLevels = XdDict.dao.find("select * from xd_dict where  type='certLevel' order by sortnum");
		setAttr("certLevels",certLevels);

		List<XdDict> licenseAuths = XdDict.dao.find("select * from xd_dict where  type='licenseauth' order by sortnum");
		setAttr("licenseAuths",licenseAuths);

		setAttr("o", o);
    	setAttr("formModelName",StringUtil.toLowerCaseFirstOne(XdEmpCert.class.getSimpleName()));
		renderIframe("edit.html");
    }
    /***
     * del
     * @throws Exception
     */
    public void delete() throws Exception{
		String ids = getPara("ids");
		service.deleteByIds(ids);
    	renderSuccess("????????????");
    }

	/**
	 * @Method importExcel
	 * @param :
	 * @Date 2022/12/18 10:57
	 * @Exception
	 * @Description ????????????
	 * @Author king
	 * @Version  1.0
	 * @Return void
	 */
	public void importExcel() throws IOException, SQLException {
		UploadFile file = getFile("file","/content");
		List<List<String>> list = ExcelUtil.excelToStringList(file.getFile().getAbsolutePath());
		Map<String,Object> result = service.importExcel(list);
		if((Boolean)result.get("success")){
			renderSuccess((String)result.get("message"));
		}else{
			renderError((String)result.get("message"));
		}
	}


	public void exportExcel() throws UnsupportedEncodingException {


		String name = java.net.URLDecoder.decode(getPara("name",""),"UTF-8");
		String serviceUnit = java.net.URLDecoder.decode(getPara("serviceUnit",""),"UTF-8");
		String job = java.net.URLDecoder.decode(getPara("job",""),"UTF-8");
		String addr = java.net.URLDecoder.decode(getPara("addr",""),"UTF-8");
		String entryDate =  getPara("entryDate","");
		String departDate =  getPara("departDate","");
		String path = this.getSession().getServletContext().getRealPath("")+"/upload/export/"+ DateUtil.format(new Date(),21)+".xlsx";
		File file = service.exportExcel(path,name,serviceUnit,job,addr,entryDate,departDate);
		renderFile(file);
	}
	
	public void exportExcel2() throws UnsupportedEncodingException {

//		String certTitle = java.net.URLDecoder.decode(getPara("certTitle",""),"utf-8");
		String certTitle = new String(getPara("certTitle","").getBytes("ISO-8859-1"), "utf-8");
		String path = this.getSession().getServletContext().getRealPath("")+"/upload/export/"+ DateUtil.format(new Date(),21)+".xlsx";
		File file = service.exportExcel(path,certTitle);
		renderFile(file);
	}



	/**
	 * @Method getWarningCertList
	 * @Date 2023/1/6 11:20
	 * @Description ????????????????????????
	 * @Author king
	 * @Version  1.0
	 * @Return void
	 */
	public void getWarningCertList(){

		List<XdEmpCert> snyList = XdEmpCert.dao.find("select  DISTINCT sny from xd_emp_cert where status='1' and closeDate  is not null and (TO_DAYS(str_to_date(closeDate, '%Y-%m-%d')) - TO_DAYS(now()))<180 order by sny");
		setAttr("snyList",snyList);
		renderIframe("pWarningList.html");
	}
	 /**
	  * @Method listWaringData
	  * @Date 2023/1/6 11:21
	  * @Description  ????????????????????????
	  * @Author king
	  * @Version  1.0
	  * @Return void
	  */
	public void listWaringData() throws UnsupportedEncodingException {
		String curr = getPara("pageNumber");
		String pageSize = getPara("pageSize");
		String dept =  getPara("dept","");
		String sny = java.net.URLDecoder.decode(getPara("sny",""),"UTF-8");
		Page<Record> page = service.getPage(Integer.valueOf(curr),Integer.valueOf(pageSize),dept, sny);
		renderPage(page.getList(),"",page.getTotalRow());
	}

	/**
	 * @Method openWarningCertPage
	 * @Date 2023/1/6 13:56
	 * @Description ????????????????????????????????????
	 * @Author king
	 * @Version  1.0
	 * @Return void
	 */
	public void openWarningCertPage(){
		String id = getPara("id");
		String view = getPara("view");
		setAttr("view", view);
		XdEmpCert o = new XdEmpCert();
		o = service.getById(id);

		List<XdEmployee> emps = XdEmployee.dao.find("select * from  xd_employee");
		setAttr("emps",emps);
		List<SysOrg> sysOrgs = SysOrg.dao.find("select * from sys_org where id <> 'root'");
		setAttr("departs",sysOrgs);

		List<XdCertificate> xdCertificates = XdCertificate.dao.find("select * from xd_certificate");
		setAttr("certs",xdCertificates);

		List<XdDict> certLevels = XdDict.dao.find("select * from xd_dict where  type='certLevel' order by sortnum");
		setAttr("certLevels",certLevels);

		List<XdDict> licenseAuths = XdDict.dao.find("select * from xd_dict where  type='licenseauth' order by sortnum");
		setAttr("licenseAuths",licenseAuths);
		setAttr("o", o);
		setAttr("formModelName",StringUtil.toLowerCaseFirstOne(XdEmpCert.class.getSimpleName()));
		renderIframe("editWarning.html");
	}

}