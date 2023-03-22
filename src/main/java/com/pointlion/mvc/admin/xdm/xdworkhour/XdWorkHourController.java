package com.pointlion.mvc.admin.xdm.xdworkhour;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.pointlion.mvc.common.base.BaseController;
import com.pointlion.mvc.admin.oa.workflow.WorkFlowService;
import com.pointlion.mvc.common.utils.StringUtil;
import com.pointlion.mvc.common.model.XdWorkHour;
import com.pointlion.mvc.common.model.SysUser;
import com.pointlion.mvc.common.model.SysOrg;
import com.pointlion.mvc.common.utils.UuidUtil;
import com.pointlion.mvc.common.utils.Constants;
import com.pointlion.mvc.admin.oa.common.OAConstants;
import com.pointlion.mvc.common.utils.DateUtil;
import com.pointlion.plugin.shiro.ShiroKit;



public class XdWorkHourController extends BaseController {
	public static final XdWorkHourService service = XdWorkHourService.me;
	public static WorkFlowService wfservice = WorkFlowService.me;
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
		String year = getPara("year","");
		String month = getPara("month","");
    	Page<Record> page = service.getPage(Integer.valueOf(curr),Integer.valueOf(pageSize),year,month);
    	renderPage(page.getList(),"",page.getTotalRow());
    }
    /***
     * save data
     */
    public void save(){
    	XdWorkHour o = getModel(XdWorkHour.class);
		List<XdWorkHour> xdWorkHours = XdWorkHour.dao.find("select * from  xd_work_hour where year='" + o.getYear() + "' and month='" + o.getMonth() + "'");
		if(xdWorkHours.size()==0){
			o.setYearMonth(o.getYear()+"-"+o.getMonth());
			o.setCreateTime(DateUtil.getCurrentTime());
			o.setCreateUser(ShiroKit.getUserId());
			o.save();
		}else{
			/*for (XdWorkHour workhour :
					xdWorkHours) {
				workhour.delete();
			}*/
			o.setYearMonth(o.getYear()+"-"+o.getMonth());
			o.update();
			Db.update("update xd_attendance_summary set curmon_workhours='"+o.getWorkHour()+"' where schedule_year='"+o.getYear()+"' and schedule_month='"+o.getMonth()+"'");
			Db.update("update xd_schedule_summary set cur_mon_hours='"+o.getWorkHour()+"' where schedule_year='"+o.getYear()+"' and schedule_month='"+o.getMonth()+"'");

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
		XdWorkHour o = new XdWorkHour();
		if(StrKit.notBlank(id)){
    		o = service.getById(id);
    		if("detail".equals(view)){
    		}
    	}else{
    		SysUser user = SysUser.dao.findById(ShiroKit.getUserId());
    		SysOrg org = SysOrg.dao.findById(user.getOrgid());
    	}
		setAttr("o", o);
    	setAttr("formModelName",StringUtil.toLowerCaseFirstOne(XdWorkHour.class.getSimpleName()));
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