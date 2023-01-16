package com.pointlion.mvc.admin.xdm.xdanleave;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.pointlion.mvc.common.base.BaseController;
import com.pointlion.mvc.common.model.*;
import com.pointlion.mvc.common.utils.DateUtil;
import com.pointlion.mvc.common.utils.StringUtil;
import com.pointlion.mvc.common.utils.UuidUtil;
import com.pointlion.plugin.shiro.ShiroKit;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.time.LocalDate;
import java.util.List;


public class XdAnleaveController extends BaseController {
	public static final XdAnleaveService service = XdAnleaveService.me;
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
		String name = URLDecoder.decode(getPara("name", ""), "UTF-8");
		String dept = getPara("dept","");
    	Page<Record> page = service.getPage(Integer.valueOf(curr),Integer.valueOf(pageSize),name,dept);
    	renderPage(page.getList(),"",page.getTotalRow());
    }
    /***
     * save data
     */
    public void save(){
    	XdAnleave o = getModel(XdAnleave.class);
    	if(o.getId()!=null){
    		o.update();
    	}else{
    		o.setCreateDate(DateUtil.getCurrentTime());
    		o.setCreateUser(ShiroKit.getUserId());
    		o.save();
			XdAnleaveSummary summary =new XdAnleaveSummary();
			summary.setEmpId(o.getEmpId());
			summary.setEmpName(o.getEmpName());
			summary.setDeptId(o.getDeptId());
			summary.setDeptName(o.getDeptName());
			summary.setLeaveDays(o.getLeaveDays());
			summary.setSurplusDays(o.getLeaveDays());
			summary.setYear(String.valueOf(LocalDate.now().getYear()));
			summary.setCreateDate(DateUtil.getCurrentTime());
			summary.setCreateUser(ShiroKit.getUserId());
			summary.save();
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
		XdAnleave o = new XdAnleave();
		if(StrKit.notBlank(id)){
    		o = service.getById(id);
    	}

		List<SysOrg> sysOrgs = SysOrg.dao.find("select * from sys_org where id<>'root' order by sort");
		setAttr("sysOrgs",sysOrgs);
		List<XdEmployee> xdEmployees = XdEmployee.dao.find("select * from xd_employee where name is not null and name <>''");

		setAttr("emps",xdEmployees);

		setAttr("o", o);
    	setAttr("formModelName",StringUtil.toLowerCaseFirstOne(XdAnleave.class.getSimpleName()));
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

	
}