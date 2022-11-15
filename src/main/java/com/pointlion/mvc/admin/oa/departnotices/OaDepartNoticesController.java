package com.pointlion.mvc.admin.oa.departnotices;

import java.util.ArrayList;
import java.util.List;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.pointlion.mvc.admin.oa.notice.NoticeService;
import com.pointlion.mvc.admin.oa.workflow.WorkFlowService;
import com.pointlion.mvc.common.base.BaseController;
import com.pointlion.mvc.common.model.OaDepartNotices;
import com.pointlion.mvc.common.model.SysAttachment;
import com.pointlion.mvc.common.utils.Constants;
import com.pointlion.mvc.common.utils.DateUtil;
import com.pointlion.mvc.common.utils.StringUtil;
import com.pointlion.mvc.common.utils.UuidUtil;
import com.pointlion.plugin.shiro.ShiroKit;



public class OaDepartNoticesController extends BaseController {
	public static final OaDepartNoticesService service = OaDepartNoticesService.me;
	public static WorkFlowService wfservice = WorkFlowService.me;
	static NoticeService noticeService = new NoticeService();
	/***
	 * get list page
	 */
	public void getListPage(){
		

		setAttr("way", getPara("way", ""));
		keepPara("way");
		
		renderIframe("list.html");
    }
	
	/**
	 * 
	* @Title: getMoreListPage 
	* @Description: 部门主页更多获取
	* @date 2021年1月27日上午9:39:27
	 */
	public void getMoreListPage(){
		
		
		setAttr("way", getPara("way", ""));
		keepPara("way");
		
		renderIframe("moreList.html");
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
     * list page data
     **/
    public void listMoreData(){
    	String curr = getPara("pageNumber");
    	String pageSize = getPara("pageSize");
    	String endTime = getPara("endTime","");
    	String startTime = getPara("startTime","");
    	String applyUser = getPara("applyUser","");
    	Page<Record> page = service.getMorePage(Integer.valueOf(curr),Integer.valueOf(pageSize),startTime,endTime,applyUser);
    	renderPage(page.getList(),"",page.getTotalRow());
    }
    
    
	public void getDepartPage(){


    	//setAttrHavedoneList(username);//获取已办
		
		String username = ShiroKit.getUsername();
		List<SysAttachment> files = new ArrayList<SysAttachment>();
				
				
		
		
		if(Constants.DEPARTHOME.indexOf(username)!=-1) {
			files=SysAttachment.dao.find("select  * from  sys_attachment  where business_id in (select  id from oa_departments_files) order by  create_time desc");
		}else {
			files=SysAttachment.dao.find("select    *   from sys_attachment  where moduel_from='"+ShiroKit.getUserOrgId()+"' order by  create_time desc");
		}
		
		setAttr("files", files);
		
		List<OaDepartNotices> NoticeList =new ArrayList<>();
		if(Constants.DEPARTHOME.indexOf(username)!=-1) {
			NoticeList=OaDepartNotices.dao.find("select * from oa_depart_notices where   sfpublish='1'     order by publishdatetime desc");
		}else {
			NoticeList=OaDepartNotices.dao.find("select * from oa_depart_notices  where departments='"+ShiroKit.getUserOrgId()+"' and  sfpublish='1'   order by publishdatetime desc");
		}
				
		
		setAttr("NoticeList",NoticeList);//获取首页通知公告
		renderIframe("havedoneList.html");
	}
    
    /***
     * save data
     */
    public void save(){
    	OaDepartNotices model = getModel(OaDepartNotices.class);
    	
    	
    	
		if(model.getSfpublish().equals("1")){
			model.setPublisher(ShiroKit.getUserName());
			model.setChangetime(model.getShowtime());
			model.setPublishdatetime(DateUtil.getCurrentTime());
			
			model.setPublisherid(ShiroKit.getUserId());
			
			model.setOpuserid(ShiroKit.getUserId());
			
			
		}
    	if(StrKit.notBlank(model.getId())){
    		model.update();
    	}else{
    		model.setId(UuidUtil.getUUID());
    		
    		model.setChangetime(DateUtil.getCurrentTime());
    		model.setDepartments(ShiroKit.getUserOrgId());
    		
    		model.save();
    	}
    	renderSuccess(model,"");
    }
    
    
    
	public void homeViewNotice(){
		OaDepartNotices findById = OaDepartNotices.dao.findById(getPara("id"));
		setAttr("notices", findById);
		
		setAttr("showHeader", "N");
		renderIframeOpen("homeShowNotices.html");
	}
    
    /***
     * edit page
     */
    public void getEditPage(){
    	String id = getPara("id");
    	String view = getPara("view");
		setAttr("view", view);
		OaDepartNotices o = new OaDepartNotices();
		if(StrKit.notBlank(id)){
    		o = service.getById(id);
    		if("detail".equals(view)){
    		}
    	}else{
    		//SysUser user = SysUser.dao.findById(ShiroKit.getUserId());
    		//SysOrg org = SysOrg.dao.findById(user.getOrgid());
    	}
		setAttr("o", o);
    	setAttr("formModelName",StringUtil.toLowerCaseFirstOne(OaDepartNotices.class.getSimpleName()));
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
    /***
     * submit
     */
    public void startProcess(){
    	String id = getPara("id");
    	OaDepartNotices o = OaDepartNotices.dao.getById(id);
    	o.update();
    	renderSuccess("submit success");
    }
    /***
     * callBack
     */
    public void callBack(){
    	String id = getPara("id");
    	try{
    		OaDepartNotices o = OaDepartNotices.dao.getById(id);
        	o.update();
    		renderSuccess("callback success");
    	}catch(Exception e){
    		e.printStackTrace();
    		renderError("callback fail");
    	}
    }

	
}