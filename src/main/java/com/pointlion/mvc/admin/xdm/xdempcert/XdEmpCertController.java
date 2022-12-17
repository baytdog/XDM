package com.pointlion.mvc.admin.xdm.xdempcert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.pointlion.mvc.common.base.BaseController;
import com.pointlion.mvc.admin.oa.workflow.WorkFlowService;
import com.pointlion.mvc.common.model.*;
import com.pointlion.mvc.common.utils.StringUtil;
import com.pointlion.mvc.common.utils.UuidUtil;
import com.pointlion.mvc.common.utils.Constants;
import com.pointlion.mvc.admin.oa.common.OAConstants;
import com.pointlion.mvc.common.utils.DateUtil;
import com.pointlion.plugin.shiro.ShiroKit;



public class XdEmpCertController extends BaseController {
	public static final XdEmpCertService service = XdEmpCertService.me;
	/***
	 * get list page
	 */
	public void getListPage(){
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
    	XdEmpCert o = getModel(XdEmpCert.class);
		XdEmpCert cert = XdEmpCert.dao.findById(o.getId());
		if(cert!=null){
    		o.update();
    	}else{
    		o.save();
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
		XdEmpCert o = new XdEmpCert();
		if(StrKit.notBlank(id)){
    		o = service.getById(id);
    	}else{
			o.setId(UuidUtil.getUUID());
    	}

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
    	renderSuccess("操作成功");
    }
    /***
     * submit
     */
    public void startProcess(){
    	String id = getPara("id");
    	XdEmpCert o = XdEmpCert.dao.getById(id);
    	o.update();
    	renderSuccess("submit success");
    }
    /***
     * callBack
     */
    public void callBack(){
    	String id = getPara("id");
    	try{
    		XdEmpCert o = XdEmpCert.dao.getById(id);
        	o.update();
    		renderSuccess("callback success");
    	}catch(Exception e){
    		e.printStackTrace();
    		renderError("callback fail");
    	}
    }

	
}