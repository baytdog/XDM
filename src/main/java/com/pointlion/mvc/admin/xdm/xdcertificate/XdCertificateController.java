package com.pointlion.mvc.admin.xdm.xdcertificate;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.pointlion.mvc.common.base.BaseController;
import com.pointlion.mvc.admin.oa.workflow.WorkFlowService;
import com.pointlion.mvc.common.utils.StringUtil;
import com.pointlion.mvc.common.model.XdCertificate;
import com.pointlion.mvc.common.model.SysUser;
import com.pointlion.mvc.common.model.SysOrg;
import com.pointlion.mvc.common.utils.UuidUtil;
import com.pointlion.mvc.common.utils.Constants;
import com.pointlion.mvc.admin.oa.common.OAConstants;
import com.pointlion.mvc.common.utils.DateUtil;
import com.pointlion.plugin.shiro.ShiroKit;



public class XdCertificateController extends BaseController {
	public static final XdCertificateService service = XdCertificateService.me;
	/***
	 * get list page
	 */
	public void getListPage(){
		renderIframe("list.html");
    }
	public void getDetailListPage(){
		System.out.println(getPara("certType"));
		keepPara("certType");
		renderIframe("listDetail.html");
    }
	/***
     * list page data
     **/
    public void listData() throws UnsupportedEncodingException {
    	String curr = getPara("pageNumber");
    	String pageSize = getPara("pageSize");
    	Page<Record> page = service.getPage(Integer.valueOf(curr),Integer.valueOf(pageSize));
    	renderPage(page.getList(),"",page.getTotalRow());
    }
    public void detailListData() throws UnsupportedEncodingException {
    	String curr = getPara("pageNumber");
    	String pageSize = getPara("pageSize");
		String title = java.net.URLDecoder.decode(getPara("title",""),"UTF-8");
		String certType = java.net.URLDecoder.decode(getPara("certType",""),"UTF-8");
    	Page<Record> page = service.getPage(Integer.valueOf(curr),Integer.valueOf(pageSize),title,certType);
    	renderPage(page.getList(),"",page.getTotalRow());
    }
    /***
     * save data
     */
    public void save(){
    	XdCertificate o = getModel(XdCertificate.class);
		XdCertificate cert = XdCertificate.dao.findById(o.getId());
		if (cert == null) {
			o.save();
		}else{
			o.update();
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
		XdCertificate o = new XdCertificate();
		if(StrKit.notBlank(id)){
    		o = service.getById(id);
    	}else{
			o.setId(UuidUtil.getUUID());
    	}

		List<XdCertificate> xdCertificates = XdCertificate.dao.find("select  * from  xd_certificate");
		setAttr("xdCertificates",xdCertificates);


		setAttr("o", o);
    	setAttr("formModelName",StringUtil.toLowerCaseFirstOne(XdCertificate.class.getSimpleName()));
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

	
}