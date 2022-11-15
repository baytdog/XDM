package com.pointlion.mvc.admin.oa.notices;

import java.util.List;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.pointlion.mvc.admin.oa.workflow.WorkFlowService;
import com.pointlion.mvc.common.base.BaseController;
import com.pointlion.mvc.common.model.OaNotices;
import com.pointlion.mvc.common.model.SysAttachment;
import com.pointlion.mvc.common.model.SysOrg;
import com.pointlion.mvc.common.utils.DateUtil;
import com.pointlion.mvc.common.utils.RzUtils;
import com.pointlion.plugin.shiro.ShiroKit;



public class OaNoticesController extends BaseController {
	public static final OaNoticesService service = OaNoticesService.me;
	public static WorkFlowService wfservice = WorkFlowService.me;
	/***
	 * get list page
	 */
	public void getListPage(){
		renderIframe("list.html");
    }
	
	
	public void getListFromHomePage(){
		renderIframe("fromHomeList.html");
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
    
    
	public void getListData(){
		String curr = getPara("pageNumber");
    	String pageSize = getPara("pageSize");
    	Page<OaNotices> page = OaNotices.dao.getPage(Integer.valueOf(curr),Integer.valueOf(pageSize));
    	renderPage(page.getList(),"" ,page.getTotalRow());
	}
	
    
	public void getEditPage(){

		
		List<SysOrg> orglist = SysOrg.dao.find("select  * from  sys_org where  parent_id!='' order by sort " );
		setAttr("orglist", orglist);
		
	
		//是否是查看详情页面
		String view = getPara("view");
		if("detail".equals(view)){
			setAttr("view", view);
		}
		String id = getPara("id");
		int fileSize = SysAttachment.dao.find("select  * from sys_attachment  where  business_id='"+id+"'").size();
		setAttr("fileSize", fileSize);
		
		
		if(StrKit.notBlank(id)){
			
			OaNotices notice = OaNotices.dao.findById(id);
			
			setAttr("o", notice);
			
			if(notice.getDepartments()!=null) {
				String[] departes = notice.getDepartments().split(",");
				setAttr("departes",departes);
			}
			
			
			
			
			
		}else{
			OaNotices o = new OaNotices();
    		//String userId = ShiroKit.getUserId();//用户主键
    		//SysUser user = SysUser.dao.getById(userId);//用户对象
    		//o.setSenderId(userId);
    		//o.setCuser(user.getName());
    		//o.setCdate(DateUtil.getCurrentTime());
//    		o.setSenderName(user.getName());
//    		o.setSenderOrgId(org.getId());
//    		o.setSenderOrgName(org.getName());
    		
    	
    		setAttr("o",o);
		}
		renderIframe("edit.html");
	}
	 
    
	
	/***
	 * 保存
	 */
	public void save(){
		OaNotices model = getModel(OaNotices.class);
		//model.setChangetime(model.getShowtime());
		if(model.getSfpublish().equals("1")){
			model.setPublisher(ShiroKit.getUserName());
			if(model.getShowtime()==null) {
				model.setChangetime(DateUtil.getCurrentTime());
			}else {
				model.setChangetime(model.getShowtime());
			}
			
			model.setPublishdatetime(DateUtil.getCurrentTime());
			
			model.setPublisherid(ShiroKit.getUserId());
			
			model.setOpuserid(ShiroKit.getUserId());
			
		}
		
		service.save(model);
		//System.out.println("-----------------"+model.getId());
		//renderSuccess();
		renderSuccess(model,"");
	}
	
	
    
    /***
     * save data
     */
  /*  public void save(){
    	OaNotices o = getModel(OaNotices.class);
    	if(StrKit.notBlank(o.getId())){
    		o.update();
    	}else{
    		o.setId(UuidUtil.getUUID());
    		o.setCreateTime(DateUtil.getCurrentTime());
    		o.save();
    	}
    	renderSuccess();
    }*/
    /***
     * edit page
     */
   /* public void getEditPage(){
    	String id = getPara("id");
    	String view = getPara("view");
		setAttr("view", view);
		OaNotices o = new OaNotices();
		if(StrKit.notBlank(id)){
    		o = service.getById(id);
    		if("detail".equals(view)){
    		 
    		}
    	}else{
    		SysUser user = SysUser.dao.findById(ShiroKit.getUserId());
    		SysOrg org = SysOrg.dao.findById(user.getOrgid());
 
    	}
		setAttr("o", o);
    	setAttr("formModelName",StringUtil.toLowerCaseFirstOne(OaNotices.class.getSimpleName()));
		renderIframe("edit.html");
    }*/
    /***
     * del
     * @throws Exception
     */
    public void delete() throws Exception{
		String ids = getPara("ids");
		service.deleteByIds(ids);
    	renderSuccess("删除成功!");
    }
    
    
    public void  homeNotices() {
    	
    	String id = getPara("id");
		
		
		if(StrKit.notBlank(id)){
			
			OaNotices notice = OaNotices.dao.findById(id);
			
			setAttr("o", notice);
			
			if(notice.getDepartments()!=null) {
				String[] departes = notice.getDepartments().split(",");
				setAttr("departes",departes);
			}
		}	
		renderIframe("showhomeNotices.html");	
    }
    
    
    
    
    
    
    /***
     * submit
     */
    public void startProcess(){
    	String id = getPara("id");
    	OaNotices o = OaNotices.dao.getById(id);
    	//o.setIfSubmit(Constants.IF_SUBMIT_YES);
		//String insId = wfservice.startProcess(id, o,null,null);
   // 	o.setProcInsId(insId);
    	o.update();
    	renderSuccess("submit success");
    }
    /***
     * callBack
     */
    public void callBack(){
    	//String id = getPara("id");
    	try{
    		//OaNotices o = OaNotices.dao.getById(id);
        //	wfservice.callBack(o.getProcInsId());
        	//o.setIfSubmit(Constants.IF_SUBMIT_NO);
        	//o.setProcInsId("");
        	//o.update();
    		renderSuccess("callback success");
    	}catch(Exception e){
    		e.printStackTrace();
    		renderError("callback fail");
    	}
    }
    
    
    
	public void homeViewNotice(){
		setAttr("notices", OaNotices.dao.findById(getPara("id")));
		
		setAttr("showHeader", "N");
		
		RzUtils.insertOaRz(getPara("id"),"1");
		
		renderIframeOpen("homeShowNotices.html");
	}

	
}