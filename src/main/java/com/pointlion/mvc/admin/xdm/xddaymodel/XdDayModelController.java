package com.pointlion.mvc.admin.xdm.xddaymodel;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.pointlion.mvc.common.base.BaseController;
import com.pointlion.mvc.common.model.SysOrg;
import com.pointlion.mvc.common.model.SysUser;
import com.pointlion.mvc.common.model.XdDayModel;
import com.pointlion.mvc.common.utils.StringUtil;
import com.pointlion.plugin.shiro.ShiroKit;



public class XdDayModelController extends BaseController {
	public static final XdDayModelService service = XdDayModelService.me;
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
		String days = getPara("days","");
    	Page<Record> page = service.getPage(Integer.valueOf(curr),Integer.valueOf(pageSize),year,month,days);
    	renderPage(page.getList(),"",page.getTotalRow());
    }
    /***
     * save data
     */
    public void save(){
    	XdDayModel o = getModel(XdDayModel.class);
    	if(o.getHolidays()!=null && !"".equals(o.getHolidays())){
    		o.setIsnatHoliday("1");
		}else{
			o.setIsnatHoliday("0");
		}
    	o.update();
    	renderSuccess();
    }
    /***
     * edit page
     */
    public void getEditPage(){
    	String id = getPara("id");
    	String view = getPara("view");
    	keepPara(view);
		setAttr("view", view);
		XdDayModel o = new XdDayModel();
		if(StrKit.notBlank(id)){
    		o = service.getById(id);
    		if("detail".equals(view)){
//    			if(StrKit.notBlank(o.getProcInsId())){
//    				setAttr("procInsId", o.getProcInsId());
//    				setAttr("defId", wfservice.getDefIdByInsId(o.getProcInsId()));
//    			}
    		}
    	}else{
    		SysUser user = SysUser.dao.findById(ShiroKit.getUserId());
    		SysOrg org = SysOrg.dao.findById(user.getOrgid());
//			o.setOrgId(org.getId());
//			o.setOrgName(org.getName());
//			o.setUserid(user.getId());
//			o.setApplyerName(user.getName());
    	}
		setAttr("o", o);
    	setAttr("formModelName",StringUtil.toLowerCaseFirstOne(XdDayModel.class.getSimpleName()));
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

	
}