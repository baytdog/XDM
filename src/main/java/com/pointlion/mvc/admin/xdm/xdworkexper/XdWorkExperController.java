package com.pointlion.mvc.admin.xdm.xdworkexper;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.pointlion.mvc.admin.oa.workflow.WorkFlowService;
import com.pointlion.mvc.common.base.BaseController;
import com.pointlion.mvc.common.model.SysOrg;
import com.pointlion.mvc.common.model.SysUser;
import com.pointlion.mvc.common.model.XdEdutrain;
import com.pointlion.mvc.common.model.XdWorkExper;
import com.pointlion.mvc.common.utils.DateUtil;
import com.pointlion.mvc.common.utils.StringUtil;
import com.pointlion.plugin.shiro.ShiroKit;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;


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
    	renderSuccess();
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
    	}else{
    		SysUser user = SysUser.dao.findById(ShiroKit.getUserId());
    		SysOrg org = SysOrg.dao.findById(user.getOrgid());
    	}
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



}