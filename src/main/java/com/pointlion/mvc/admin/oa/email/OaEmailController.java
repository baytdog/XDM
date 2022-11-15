package com.pointlion.mvc.admin.oa.email;

import java.util.List;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.pointlion.mvc.admin.oa.workflow.WorkFlowService;
import com.pointlion.mvc.common.base.BaseController;
import com.pointlion.mvc.common.model.OaEmail;
import com.pointlion.mvc.common.model.OaEmailSon;
import com.pointlion.mvc.common.model.SysAttachment;
import com.pointlion.mvc.common.model.SysUser;
import com.pointlion.mvc.common.utils.DateUtil;
import com.pointlion.mvc.common.utils.StringUtil;
import com.pointlion.mvc.common.utils.UuidUtil;
import com.pointlion.plugin.shiro.ShiroKit;



public class OaEmailController extends BaseController {
	public static final OaEmailService service = OaEmailService.me;
	public static WorkFlowService wfservice = WorkFlowService.me;
	/***
	 * get list page
	 */
	public void getListPage(){
		//renderIframe("list.html");
		String fromway = getPara("fromway","");
		
		setAttr("fromway", fromway);
		setAttr("eid",UuidUtil.getUUID());
		setAttr("currentUserId", ShiroKit.getUserId());
		renderIframe("email.html");
		//renderIframe("edit.html");
	}
	/***
     * list page data
     **/
    public void listData(){
    	String curr = getPara("pageNumber");
    	String pageSize = getPara("pageSize");
		String endTime = getPara("endTime","");
		String applyUser = getPara("applyUser","");
		System.out.println(getPara("vs"));
		
		
    	Page<Record> page = service.getPage(Integer.valueOf(curr),Integer.valueOf(pageSize),getPara("vs"),endTime,applyUser);
    	renderPage(page.getList(),"",page.getTotalRow());
    }
    public void listData1(){
    	String curr = getPara("pageNumber");
    	String pageSize = getPara("pageSize");
    	String endTime = getPara("endTime","");
    	String startTime = getPara("startTime","");
    	String applyUser = getPara("applyUser","");
    	System.out.println(getPara("vs"));
    	
    	Page<Record> page = service.getPage(Integer.valueOf(curr),Integer.valueOf(pageSize),startTime,endTime,applyUser);
    	renderPage(page.getList(),"",page.getTotalRow());
    }
    /***
     * save data
     */
    public void save(){
    	OaEmail o = getModel(OaEmail.class);
  /*  	if(StrKit.notBlank(o.getId())){
    		o.update();
    	}else{
    	 
    		//o.setCreateTime(DateUtil.getCurrentTime());
    		o.setTimeflage(DateUtil.getCurrentTime());
    		o.setFuserid(ShiroKit.getUserId());
    		o.setOpstatis("0");
    		o.save();
    	}*/
    	o.setTimeflage(DateUtil.getCurrentTime());
		o.setFuserid(ShiroKit.getUserId());
		o.setOpstatis("0");
		o.save();
		
		String suserid = o.getSuserid();
		if(suserid!=null && !suserid.equals("")) {
			
			List<SysAttachment> attachList = SysAttachment.dao.find("select  * from sys_attachment  where business_id='"+o.getId()+"'");
			
			
			String[] userids = suserid.split(",");
			
			for (String userid : userids) {
				
				SysUser user = SysUser.dao.findById(userid);
				OaEmailSon oms=new OaEmailSon();
				oms.setId(UuidUtil.getUUID());
				oms.setOid(o.getId());
				oms.setSuserid(user.getId());
				oms.setSusername(user.getName());
				oms.setIsreaded("0");
				oms.setFilenum(String.valueOf(attachList.size()));
				oms.setFuserid(ShiroKit.getUserId());
				oms.setFusername(ShiroKit.getUserName());
				oms.setFtime(DateUtil.getCurrentTime());
				oms.setCtime(DateUtil.getCurrentTime());
				oms.setCuserid(ShiroKit.getUserId());
				oms.setCusername(ShiroKit.getUserName());
				oms.save();
			}
			
			
			
			
		}
			
			
		
		
		
		
		
    	//renderSuccess(o,"");
    	renderSuccess(UuidUtil.getUUID(),"");
    }
    /***
     * edit page
     */
    public void getEditPage(){
    	String id = getPara("id");
    	String view = getPara("view");
		setAttr("view", view);
		OaEmail o = new OaEmail();
		if(StrKit.notBlank(id)){
    		o = service.getById(id);
    		if("detail".equals(view)){
    			/*if(StrKit.notBlank(o.getProcInsId())){
    				setAttr("procInsId", o.getProcInsId());
    				setAttr("defId", wfservice.getDefIdByInsId(o.getProcInsId()));
    			}*/
    			
    			 
    			
    			o.setOpstatis("1");
    			o.update();
    			
    			
    			Db.update("update oa_email_son set   isreaded='1' ,stime='"+DateUtil.getCurrentTime()+"'  where oid='"+o.getId()+"' and suserid='"+ShiroKit.getUserId()+"'");
    		}
    	}else{
    	/*	SysUser user = SysUser.dao.findById(ShiroKit.getUserId());
    		SysOrg org = SysOrg.dao.findById(user.getOrgid());*/
	/*		o.setOrgId(org.getId());
			o.setOrgName(org.getName());
			o.setUserid(user.getId());
			o.setApplyerName(user.getName());*/
    	}
		
		setAttr("eid", o.getId());
		setAttr("o", o);
		setAttr("currentUserId", ShiroKit.getUserId());
    	setAttr("formModelName",StringUtil.toLowerCaseFirstOne(OaEmail.class.getSimpleName()));
		renderIframe("edit.html");
    }
    /***
     * del
     * @throws Exception
     */
    public void delete() throws Exception{
		String ids = getPara("ids");
		String fromtype = getPara("fromtype");
		
		
		String idarr[] = ids.split(",");
    	for(String id : idarr){
    		
    		if(fromtype.equals("1")) {
    			OaEmail o = OaEmail.dao.findById(id);
    			o.setStatus("0");
    			o.setDtime(DateUtil.getCurrentTime());
    			o.update();
    		}else {
    			
    			OaEmailSon o =OaEmailSon.dao.findFirst("select  * from oa_email_son  where oid='"+id+"' and suserid='"+ShiroKit.getUserId()+"'");
    			o.setStatus("0");
    			o.setDtime(DateUtil.getCurrentTime());
    			o.update();
    		}
    	}
		
		
		//service.deleteByIds(ids);
    	renderSuccess("删除成功!");
    }
    /***
     * submit
     */
    public void startProcess(){
    	String id = getPara("id");
    	OaEmail o = OaEmail.dao.getById(id);
    	//o.setIfSubmit(Constants.IF_SUBMIT_YES);
    	//o.setProcInsId(insId);
    	o.update();
    	renderSuccess("submit success");
    }
    /***
     * callBack
     */
    public void callBack(){
    	String id = getPara("id");
    	try{
    		OaEmail o = OaEmail.dao.getById(id);
        	/*wfservice.callBack(o.getProcInsId());
        	o.setIfSubmit(Constants.IF_SUBMIT_NO);
        	o.setProcInsId("");*/
        	o.update();
    		renderSuccess("callback success");
    	}catch(Exception e){
    		e.printStackTrace();
    		renderError("callback fail");
    	}
    }

	
}