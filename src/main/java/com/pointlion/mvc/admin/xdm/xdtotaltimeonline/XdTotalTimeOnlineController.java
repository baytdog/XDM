package com.pointlion.mvc.admin.xdm.xdtotaltimeonline;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.pointlion.mvc.common.base.BaseController;
import com.pointlion.mvc.admin.oa.workflow.WorkFlowService;
import com.pointlion.mvc.common.utils.StringUtil;
import com.pointlion.mvc.common.model.XdTotalTimeOnline;
import com.pointlion.mvc.common.model.SysUser;
import com.pointlion.mvc.common.model.SysOrg;
import com.pointlion.mvc.common.utils.UuidUtil;
import com.pointlion.mvc.common.utils.Constants;
import com.pointlion.mvc.admin.oa.common.OAConstants;
import com.pointlion.mvc.common.utils.DateUtil;
import com.pointlion.plugin.shiro.ShiroKit;



public class XdTotalTimeOnlineController extends BaseController {
	public static final XdTotalTimeOnlineService service = XdTotalTimeOnlineService.me;
	/***
	 * get list page
	 */
	public void getListPage(){
		String days = getPara("days", "");
		if(StrKit.notBlank(days)){
			setAttr("now", days);
		}else{

			setAttr("now", DateTimeFormatter.ofPattern("yyyy-MM-dd").format(LocalDate.now()));
		}
		renderIframe("list.html");
    }
	/***
     * list page data
     **/
    public void listData(){
    	String curr = getPara("pageNumber");
    	String pageSize = getPara("pageSize");
		String work_date = getPara("work_date","");
		String hours = getPara("hours","");
		String min = getPara("min","00");
		String endhours = getPara("endhours","");
		String endmin = getPara("endmin","00");

		keepPara("work_date");
    	if(StrKit.notBlank(hours)){
			Page<Record> page = service.getPage(Integer.valueOf(curr),Integer.valueOf(pageSize),work_date,hours,min,endhours,endmin);
			renderPage(page.getList(),"",page.getTotalRow());
		}else{
			Page<Record> page = service.getPage(Integer.valueOf(curr),Integer.valueOf(pageSize),work_date);
			renderPage(page.getList(),"",page.getTotalRow());
		}

    }
    /***
     * save data
     */
    public void save(){
    	XdTotalTimeOnline o = getModel(XdTotalTimeOnline.class);
    	if(StrKit.notBlank(o.getId())){
    		o.update();
    	}else{
    		o.setId(UuidUtil.getUUID());
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
		XdTotalTimeOnline o = new XdTotalTimeOnline();
		if(StrKit.notBlank(id)){
    		o = service.getById(id);
    		if("detail".equals(view)){
    		}
    	}else{
    		SysUser user = SysUser.dao.findById(ShiroKit.getUserId());
    		SysOrg org = SysOrg.dao.findById(user.getOrgid());
    	}
		setAttr("o", o);
    	setAttr("formModelName",StringUtil.toLowerCaseFirstOne(XdTotalTimeOnline.class.getSimpleName()));
		renderIframe("edit.html");
    }


	public void getOnlineListPage(){

		setAttr("now", DateTimeFormatter.ofPattern("yyyy-MM-dd").format(LocalDate.now()));
		setAttr("id",getPara("id"));
		setAttr("field",getPara("field"));
		setAttr("days",getPara("days"));
//		keepPara("id","field","days");
		renderIframe("listWork.html");
	}

	public void listOnlineData(){
		String curr = getPara("pageNumber");
		String pageSize = getPara("pageSize");
		String id = getPara("id","");
		String field = getPara("field","");
		String days = getPara("days","");
		Page<Record> page = service.getOnlinePage(Integer.valueOf(curr),Integer.valueOf(pageSize), days,id,field);
		renderPage(page.getList(),"",page.getTotalRow());
	}

	public void exportExcel() throws UnsupportedEncodingException {

		String workDate=getPara("workDate","");

		String path = this.getSession().getServletContext().getRealPath("")+"/upload/export/"+ DateUtil.format(new Date(),21)+".xlsx";
		File file = service.exportExcel(path,workDate);
		renderFile(file);
	}
	public void exportEmpExcel() throws UnsupportedEncodingException {

		String workDate=getPara("workDate","");

		String path = this.getSession().getServletContext().getRealPath("")+"/upload/export/"+ DateUtil.format(new Date(),21)+".xlsx";
		File file = service.exportEmpExcel(path,workDate);
		renderFile(file);
	}
}