package com.pointlion.mvc.admin.xdm.xdempcert;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.upload.UploadFile;
import com.pointlion.enums.XdOperEnum;
import com.pointlion.mvc.common.base.BaseController;
import com.pointlion.mvc.common.model.*;
import com.pointlion.mvc.common.utils.DateUtil;
import com.pointlion.mvc.common.utils.StringUtil;
import com.pointlion.mvc.common.utils.UuidUtil;
import com.pointlion.mvc.common.utils.XdOperUtil;
import com.pointlion.mvc.common.utils.office.excel.ExcelUtil;
import com.pointlion.plugin.shiro.ShiroKit;
import com.pointlion.util.DictMapping;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class XdEmpCertController extends BaseController {
	public static final XdEmpCertService service = XdEmpCertService.me;
	private  static final Map<String, List<XdDict>> dictListByType = DictMapping.getDictListByType();
	/***
	 * get list page
	 */
	public void getListPage(){

		List<XdEmpCert> snyList = XdEmpCert.dao.find("select  DISTINCT sny from xd_emp_cert order by sny");
		setAttr("snyList",snyList);

		List<SysOrg> orgList = SysOrg.dao.find("select * from  sys_org where id <>'root' order by sort");
		setAttr("orgList",orgList);
		List<XdDict> certLevelList = dictListByType.get("certLevel");
//				XdDict.dao.find("select * from xd_dict where  type ='certLevel' order by sortnum");
		setAttr("certLevelList",certLevelList);

		List<XdCertificate> certList = XdCertificate.dao.find("select * from xd_certificate where cert_type is not null order by cert_type");

		setAttr("certList",certList);

		List<XdDict> licenseAuthList = dictListByType.get("licenseauth");
		setAttr("licenseAuths",licenseAuthList);


		String sertStr="";
		Map<String,String> map =new HashMap<>();
		for (XdDict dict : certLevelList) {
			sertStr=sertStr+dict.getValue()+"="+dict.getName()+",";
		}
		setAttr("sertStr",sertStr);
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
		String certTitle = getPara("certTitle","");
		String certAuth = getPara("certAuth","");
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
		XdCertLog log =new XdCertLog();
		SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd");
		String[] ymd = sdf.format(new Date()).split("-");
		log.setYear(Integer.valueOf(ymd[0]));
		log.setMonth(Integer.valueOf(ymd[1]));
		log.setEid(o.getEid());
		log.setEname(o.getEname());
		log.setCertTitle(o.getCertTile());
		log.setDeptId(o.getDepartment());
		SysOrg org = SysOrg.dao.findById(o.getDepartment());
		log.setDeptName(org==null?"":org.getName());
		log.setNum(1);
		log.setCreateDate(DateUtil.getCurrentTime());
		log.setCreateUser(ShiroKit.getUserId());
		if(cert!=null){
			String closeDate = o.getCloseDate();
			if("长期".equals(closeDate)){
				o.setSny("长期");
				o.setSn("长期");
			}else if(closeDate!=null &&!"".equals(closeDate.trim())){
				String ny = closeDate.replaceFirst("-", "年").replaceFirst("-", "月").substring(0, 8);
				String n = ny.substring(0,5);
				o.setSny(ny);
				o.setSn(n);
			}

			if(o.getValidateDate()!=null && !o.getValidateDate().equals("")){
				o.setCloseDate(o.getValidateDate());
			}
			o.update();
			XdOplogSummary summary = XdOperUtil.logSummary(UuidUtil.getUUID(), cert.getId(), o, cert, XdOperEnum.U.name(), XdOperEnum.UNAPPRO.name());
			summary.save();

			if(cert.getValidateDate()!=null && !cert.getValidateDate().equals("") && !cert.getValidateDate().equals(o.getValidateDate()) ){
				log.setId(null);
				log.setNum(0);
				log.setLogType("审证复证");
				log.save();
			}

			if(o.getCertLevel()!=null && cert.getCertLevel()!=null && !o.getCertLevel().equals(cert.getCertLevel())){
				List<XdDict> dicts = XdDict.dao.find("select * from xd_dict where  type ='certLevel'  order by sortnum");
				Map<String,String> levelMap =new HashMap<>();
				dicts.forEach(dict->levelMap.put(dict.getValue(),dict.getName()));
				log.setId(null);
				String[] levelArr = o.getCertLevel().split(",");
				String chiLevel="";
				for (String level : levelArr) {
					chiLevel=chiLevel+levelMap.get(level);
				}

				log.setLogType(chiLevel+"替换");
				log.setNum(-1);
				log.save();
			}

		}else{
			String closeDate = o.getCloseDate();
			if("长期".equals(closeDate)){
				o.setSny("长期");
				o.setSn("长期");
			}else if(!"".equals(closeDate.trim())){
				String ny = closeDate.replaceFirst("-", "年").replaceFirst("-", "月").substring(0, 8);
				String n = ny.substring(0,5);
				o.setSny(ny);
				o.setSn(n);
			}
			o.setStatus("1");
			o.save();
			XdOperUtil.logSummary(o.getId(),o, XdOperEnum.C.name(),XdOperEnum.UNAPPRO.name(),0);

			log.setLogType("新增");
			log.save();

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

//		List<XdDict> certLevels =XdDict.dao.find("select * from xd_dict where  type='certLevel' order by sortnum");

		setAttr("certLevels",dictListByType.get("certLevel"));

//		List<XdDict> licenseAuths = XdDict.dao.find("select * from xd_dict where  type='licenseauth' order by sortnum");
		setAttr("licenseAuths",dictListByType.get("licenseauth"));

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
    	renderSuccess("操作成功");
    }

	/**
	 * @Method importExcel
	 * @param :
	 * @Date 2022/12/18 10:57
	 * @Exception
	 * @Description 导入功能
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


	public void exportExcel() {


		/*String name = java.net.URLDecoder.decode(getPara("name",""),"UTF-8");
		String serviceUnit = java.net.URLDecoder.decode(getPara("serviceUnit",""),"UTF-8");
		String job = java.net.URLDecoder.decode(getPara("job",""),"UTF-8");
		String addr = java.net.URLDecoder.decode(getPara("addr",""),"UTF-8");
		String entryDate =  getPara("entryDate","");
		String departDate =  getPara("departDate","");*/
		String path = this.getSession().getServletContext().getRealPath("")+"/upload/export/"+ DateUtil.format(new Date(),21)+".xlsx";
		File file = service.exportExcel(path);
		renderFile(file);
	}
	
	public void exportExcel2() throws UnsupportedEncodingException {

//		String certTitle = java.net.URLDecoder.decode(getPara("certTitle",""),"utf-8");
		String certTitle = getPara("certTitle","");
		String path = this.getSession().getServletContext().getRealPath("")+"/upload/export/"+ DateUtil.format(new Date(),21)+".xlsx";
		File file = service.exportExcel(path,certTitle);
		renderFile(file);
	}



	/**
	 * @Method getWarningCertList
	 * @Date 2023/1/6 11:20
	 * @Description 证书到期提醒列表
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
	  * @Description  证书到期列表数据
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
	 * @Description 打开证件到期提醒详情页面
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


	public void getUserinfo(){


		XdEmployee emp = XdEmployee.dao.findById(getPara("id"));

		SysOrg org = SysOrg.dao.findById(emp.getDepartment());
		//XdProjects project = XdProjects.dao.findById(emp.getCostitem());

		cn.hutool.json.JSONObject jsonObject=new cn.hutool.json.JSONObject();
		//jsonObject.put("empNum",emp.getEmpnum());

		jsonObject.put("idnum",emp.getIdnum());
		jsonObject.put("orgId",emp.getDepartment());
		/*jsonObject.put("projectId",project.getId()+"");
		jsonObject.put("projectName",project.getProjectName());*/
		renderJson(jsonObject);


	}


}