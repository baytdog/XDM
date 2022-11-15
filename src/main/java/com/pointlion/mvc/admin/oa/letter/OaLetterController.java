package com.pointlion.mvc.admin.oa.letter;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.pointlion.mvc.admin.oa.workflow.WorkFlowService;
import com.pointlion.mvc.common.base.BaseController;
import com.pointlion.mvc.common.model.OaHotlineUser;
import com.pointlion.mvc.common.model.OaLetter;
import com.pointlion.mvc.common.model.OaSteps;
import com.pointlion.mvc.common.model.SysAttachment;
import com.pointlion.mvc.common.model.SysOrg;
import com.pointlion.mvc.common.model.SysUser;
import com.pointlion.mvc.common.utils.DateUtil;
import com.pointlion.mvc.common.utils.StepUtil;
import com.pointlion.mvc.common.utils.StringUtil;
import com.pointlion.mvc.common.utils.UuidUtil;
import com.pointlion.plugin.shiro.ShiroKit;



public class OaLetterController extends BaseController {
	public static final OaLetterService service = OaLetterService.me;
	public static WorkFlowService wfservice = WorkFlowService.me;
	public static final  String stepType="4";
	
	
	/**
	 * 
	* @Title: getListPage 
	* @Description: 获取列表页面
	* @date 2020年11月7日上午9:49:12
	 */
	public void getListPage(){
		renderIframe("list.html");
    }
	/***
     * list page data
	 * @throws UnsupportedEncodingException 
     **/
    public void listData() throws UnsupportedEncodingException{
    	String curr = getPara("pageNumber");
    	String pageSize = getPara("pageSize");
    	String endTime = getPara("endTime","");
		String startTime = getPara("startTime","");
		String lnum = java.net.URLDecoder.decode(getPara("lnum",""),"UTF-8");
		String lfromnum = java.net.URLDecoder.decode(getPara("lfromnum",""),"UTF-8");
		String lfromer = java.net.URLDecoder.decode(getPara("lfromer",""),"UTF-8");
		String lstate = getPara("lstate","");
    	Page<Record> page = service.getListPage(Integer.valueOf(curr),Integer.valueOf(pageSize),  startTime,  endTime,  lnum,  lfromer,  lfromnum,  lstate);
    	renderPage(page.getList(),"",page.getTotalRow());
    }
    /***
     * save data
     */
    public void save(){
    	OaLetter o = getModel(OaLetter.class);
    	if(StrKit.notBlank(o.getId())){
    		o.update();
    	}else{
    		o.setId(UuidUtil.getUUID());
    	//	o.setCreateTime(DateUtil.getCurrentTime());
    		o.save();
    	}
    	renderSuccess();
    }
    /***
     * edit page
     */
    public void getEditPage(){
    	
    	
    	
    	setAttr("currentUserId", ShiroKit.getUserId());
    	String id = getPara("id");
    	String view = getPara("view");
		setAttr("view", view);
		OaLetter o = new OaLetter();
		if(StrKit.notBlank(id)){
    		o = service.getById(id);
    		if("detail".equals(view)){
    	 
    		}
    	}else{
    		
    		//SysUser user = SysUser.dao.findById(ShiroKit.getUserId());
    		//SysOrg org = SysOrg.dao.findById(user.getOrgid());
    	 o.setId(UuidUtil.getUUID());
    	 o.setCtime(DateUtil.getCurrentTime());
    	 o.setStatus("0");
    	 o.setType("1");
    	 setAttr("currentUserId", ShiroKit.getUserId());
    		
		/*	o.setOrgId(org.getId());
			o.setOrgName(org.getName());
			o.setUserid(user.getId());
			o.setApplyerName(user.getName());*/
    	}
		setAttr("o", o);
    	setAttr("formModelName",StringUtil.toLowerCaseFirstOne(OaLetter.class.getSimpleName()));
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
    	OaLetter o = OaLetter.dao.getById(id);
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
    		OaLetter o = OaLetter.dao.getById(id);
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

    
    
    public  void letterSave() {
    	

		OaLetter o = getModel(OaLetter.class);
		
		OaLetter dob = OaLetter.dao.findById(o.getId());
		
		
		if(dob==null) {
			
			//o.setId(UuidUtil.getUUID());
			o.setCuserid(ShiroKit.getUserId());
			o.setCtime(DateUtil.getCurrentTime());
			o.setCusername(ShiroKit.getUsername());
			o.setStatus("1");
			o.setSyts(o.getClqx());
			o.save();
			
			OaSteps cou = new OaSteps();
			cou.setId(UuidUtil.getUUID());
			cou.setOid(o.getId());
			cou.setStep("1");
			cou.setType(stepType);
			cou.setTitle(o.getBackup3());
			//cou.setSortnum("100");
			cou.setUserid(ShiroKit.getUserId());
			cou.setUsername(ShiroKit.getUserName());
			SysOrg org = SysOrg.dao.findById(ShiroKit.getUserOrgId());
			cou.setOrgid(org.getId());
			cou.setOrgname(org.getName());
			cou.setIfshow("1");
			cou.setIfcomplete("1");
			cou.setCompletetime(DateUtil.getCurrentTime());
			cou.setCtime(DateUtil.getCurrentTime());
			cou.setCuserid(ShiroKit.getUserId());
			cou.setCusername(ShiroKit.getUserName());
			cou.save();
			//insertStepHistory(o);
		}else {
			
			Db.update("update  oa_steps  set ifcomplete ='1' where oid='"+o.getId()+"'  and  userid='"+ShiroKit.getUserId()+"' and  ifcomplete='0'");
			o.setStatus("1");
			//StepUtil.insertStepHistory(o.getId(),o.getBackup3(), stepType);
			o.update();
		}
		
		
		
		StepUtil.insertStepHistory(o.getId(),o.getBackup3(), stepType);
		

		String[] nbshrids = o.getNbshrids().split(",");
		//int i = 0;
		for (String nbshrid : nbshrids) {
			
			OaSteps nbrStep = new OaSteps();
			nbrStep.setId(UuidUtil.getUUID());
			nbrStep.setOid(o.getId());
			nbrStep.setStep("2");
			nbrStep.setType(stepType);
			nbrStep.setTitle(o.getBackup3());
			//cou.setSortnum(String.valueOf(200 + i));
			SysUser user = SysUser.dao.findById(nbshrid);
			SysOrg org = SysOrg.dao.findByIds(user.getOrgid());
			nbrStep.setUserid(user.getId());
			//cou.setUsername(user.getName());
		//	System.out.println(user.getName());
			nbrStep.setUsername(user.getName());
			nbrStep.setOrgid(org.getId());
			nbrStep.setOrgname(org.getName());
			nbrStep.setIfshow("1");
			nbrStep.setIfcomplete("0");
			nbrStep.setCompletetime("");
			nbrStep.setCtime(DateUtil.getCurrentTime());
			nbrStep.setCuserid(ShiroKit.getUserId());
			nbrStep.setCusername(ShiroKit.getUserName());
			nbrStep.save();
			//i++;

		}
    	renderSuccess(o, "");
	
    }
    /**
     * 
    * @Title: getPHotLineListPage 
    * @Description: 个人信访列表跳转
    * @date 2020年11月7日下午1:07:20
     */
    
	public void getPLetterListPage(){
		//keepPara("type");
		renderIframe("pLetterList.html");
	}
	/**
	 * 
	* @Title: pHotLineListData 
	* @Description: 个人信访列表数据
	* @date 2020年11月7日下午1:08:48
	 */
   public void pLetterListData(){
    	String curr = getPara("pageNumber");
    	String pageSize = getPara("pageSize");
    	Page<Record> page = service.getPLetterListPage(Integer.valueOf(curr),Integer.valueOf(pageSize));
    	renderPage(page.getList(),"",page.getTotalRow());
    }
    
   
   /**
    * 
   * @Title: getPLetterForm 
   * @Description: 信访个人任务表单页面
   * @date 2020年11月7日下午1:15:31
    */
   
   public void getPLetterForm(){
   	String id = getPara("id");
   	String view = getPara("view");
   	String type = getPara("type");
   	setAttr("view", view);
   	setAttr("type", type);
   	keepPara("sid");
   	keepPara("sstep");
    
   	List<SysUser> commonusers = SysUser.dao.find("select  * from  sys_user  where  orgid='"+ShiroKit.getUserOrgId()+"'");
   	setAttr("commonusers", commonusers);
   	
   	List<SysAttachment> attachmentsList = SysAttachment.dao.find("select  * from sys_attachment  where business_id='"+id+"'");
   	System.out.println(attachmentsList.size());
   	setAttr("attachments",attachmentsList.size());
   	
   	OaHotlineUser hotUser = OaHotlineUser.dao.findFirst("select  * from  oa_hotline_user where hotlinid='"+id+"' and userid='"+ShiroKit.getUserId()+"'  and  ifcomplete='0'");
   	setAttr("hotUser", hotUser);
   	setAttr("usersname",ShiroKit.getUsername());
   	
 
   	OaLetter o = new OaLetter();
		o = service.getById(id);
	 
		
		
		if(!o.getCuserid().equals(ShiroKit.getUserId())) {
			setAttr("showCommont", true);
		}else {
			setAttr("showCommont", false);
		}
		
		if(!getPara("sstep").equals("41")&&(o.getStatus().equals("3")||o.getStatus().equals("4"))) {
   		setAttr("ifneedadd", true);
   	}else {
   		setAttr("ifneedadd", false);
   	}
		
		
		if(o.getCuserid().equals(ShiroKit.getUserId())) {
			
			setAttr("ifc", true);
		}else {
			setAttr("ifc",false);
		}
		
		
		
		SysUser user = SysUser.dao.findById(ShiroKit.getUserId());
		boolean isLeader=false;
		if(user.getPosition().equals("2")||user.getPosition().equals("6")) {
			isLeader=true;
		}
		
		setAttr("isLeader", isLeader);
		OaSteps osteps = OaSteps.dao.findById(getPara("sid"));
		
		setAttr("buttontype", osteps.getButtontype());
		
		
		setAttr("o", o);
		
   	setAttr("o", o);
   	setAttr("formModelName",StringUtil.toLowerCaseFirstOne(OaLetter.class.getSimpleName()));
   	renderIframe("pLetter.html");
   }
    
   
   /**
	 * 
	* @Title: sendReg 
	* @Description:拟办审核送登记人
	* @date 2020年11月6日下午2:40:03
	 */
   public  void  sendReg() {
	OaLetter o = getModel(OaLetter.class);
   	o=OaLetter.dao.findById(o.getId());
   	String comment = getPara("comment");
   	String sstep = getPara("sstep");
   	
    String sid = getPara("sid");
   	OaSteps uLetterUser = OaSteps.dao.findById(sid);
   	uLetterUser.setIfcomplete("1");
   	uLetterUser.setRemarks(comment);
   	uLetterUser.setCompletetime(DateUtil.getCurrentTime());
   	uLetterUser.update();
   	
   	
   	StepUtil.insertStepHistory(o.getId(),o.getBackup3(), stepType);
   	
   	List<OaSteps> uCompleteList = OaSteps.dao.find("select  * from  oa_steps where oid='"+o.getId()+"' and  step like '"+sstep+"%' and ifcomplete='0'");
   	if(uCompleteList.size()==0) {
   		o.setStatus("2");
   		OaSteps cLetterUser=new OaSteps();
   		cLetterUser.setId(UuidUtil.getUUID());
   		cLetterUser.setOid(o.getId());
   		cLetterUser.setStep("3");
   		cLetterUser.setType(stepType);
   		cLetterUser.setTitle(o.getBackup3());
   		SysUser user = SysUser.dao.findById(o.getCuserid());
   		SysOrg org = SysOrg.dao.findById(user.getOrgid());
   		cLetterUser.setUserid(user.getId());
   		cLetterUser.setUsername(user.getName());
   		cLetterUser.setOrgid(org.getId());
   		cLetterUser.setOrgname(org.getName());
   		//cLetterUser.setSortnum("300");
   		cLetterUser.setIfshow("1");
   		cLetterUser.setIfcomplete("0");
   		cLetterUser.setCtime(DateUtil.getCurrentTime());
   		cLetterUser.setCuserid(ShiroKit.getUserId());
   		cLetterUser.setCusername(ShiroKit.getUserName());
   		cLetterUser.save();
   	}
   	
	SimpleDateFormat  sdf =new SimpleDateFormat("yyyy-MM-dd");
   //	String remark=(o.getNbshrremark()==null?"":o.getNbshrremark())+"\r\n"+comment+"("+ShiroKit.getUserName()+")";
   	String remark=(o.getNbshrremark()==null?"":o.getNbshrremark())+"\r\n"+comment+"\r\n\t\t\t\t\t\t\t\t\t\t"+ShiroKit.getUserName()+" "+sdf.format(new Date());
   	o.setNbshrremark(remark);
   	o.update();
   	renderSuccess();
   }
	
   /**
    * 
   * @Title: sendLeaderPs 
   * @Description: 拟办送领导批示
   * @date 2020年11月7日下午1:45:59
    */
   public  void sendLeaderPs() {
	   

	   	OaLetter o = getModel(OaLetter.class);
	  //o=OaLetter.dao.findById(o.getId());
	   	String comment = getPara("comment");
	   	String sstep = getPara("sstep");
	   	
	    String sid = getPara("sid");
	   	OaSteps uLetterUser = OaSteps.dao.findById(sid);
	   	uLetterUser.setIfcomplete("1");
	   	uLetterUser.setRemarks(comment);
	   	uLetterUser.setCompletetime(DateUtil.getCurrentTime());
	   	uLetterUser.update();
	   	
	   	StepUtil.insertStepHistory(o.getId(),o.getBackup3(),stepType);
	   	List<OaSteps> uCompleteList = OaSteps.dao.find("select  * from  oa_steps where oid='"+o.getId()+"' and  step like '"+sstep+"%' and ifcomplete='0'");
	   	if(uCompleteList.size()==0) {
	   		o.setStatus("7");
	   		String[] leadersids = o.getLeadersid().split(",");
	   		for (String leadersid : leadersids) {
				
	   			OaSteps cLetterUser=new OaSteps();
	   			cLetterUser.setId(UuidUtil.getUUID());
	   			cLetterUser.setOid(o.getId());
	   			cLetterUser.setStep("31");
	   			cLetterUser.setType(stepType);
	   			cLetterUser.setTitle(o.getBackup3());
	   			SysUser user = SysUser.dao.findById(leadersid);
	   			SysOrg org = SysOrg.dao.findById(user.getOrgid());
	   			cLetterUser.setUserid(user.getId());
	   			cLetterUser.setUsername(user.getName());
	   			cLetterUser.setOrgid(org.getId());
	   			cLetterUser.setOrgname(org.getName());
	   			cLetterUser.setIfshow("1");
	   			cLetterUser.setIfcomplete("0");
	   			cLetterUser.setCtime(DateUtil.getCurrentTime());
	   			cLetterUser.setCuserid(ShiroKit.getUserId());
	   			cLetterUser.setCusername(ShiroKit.getUserName());
	   			cLetterUser.save();
			}
	   		
	   		
	   	}
	   	String remark=(o.getNbshrremark()==null?"":o.getNbshrremark())+"\r\n"+comment+"("+ShiroKit.getUserName()+")";
	   	o.setNbshrremark(remark);
	   	o.update();
	   	renderSuccess();
	   
   }
   
   /**'
    * 
   * @Title: leaderPs 
   * @Description: 领导批示
   * @date 2020年11月7日下午1:53:47
    */
   public void leaderPs() {

	   	OaLetter o = getModel(OaLetter.class);
	   	o=OaLetter.dao.findById(o.getId());
	   	String comment = getPara("comment");
	   	String sstep = getPara("sstep");
	   	
	    String sid = getPara("sid");
	   	OaSteps uLetterUser = OaSteps.dao.findById(sid);
	   	uLetterUser.setIfcomplete("1");
	   	uLetterUser.setRemarks(comment);
	   	uLetterUser.setCompletetime(DateUtil.getCurrentTime());
	   	uLetterUser.update();
	   	
	   	
	   	List<OaSteps> uCompleteList = OaSteps.dao.find("select  * from  oa_steps where oid='"+o.getId()+"' and  step like '"+sstep+"%' and ifcomplete='0'");
	   	if(uCompleteList.size()==0) {
	   		o.setStatus("8");
	   		OaSteps cLetterUser=new OaSteps();
	   		cLetterUser.setId(UuidUtil.getUUID());
	   		cLetterUser.setOid(o.getId());
	   		cLetterUser.setStep("32");
	   		cLetterUser.setType(stepType);
	   		cLetterUser.setTitle(o.getBackup3());
	   		SysUser user = SysUser.dao.findById(o.getCuserid());
	   		SysOrg org = SysOrg.dao.findById(user.getOrgid());
	   		cLetterUser.setUserid(user.getId());
	   		cLetterUser.setUsername(user.getName());
	   		cLetterUser.setOrgid(org.getId());
	   		cLetterUser.setOrgname(org.getName());
	   		//cLetterUser.setSortnum("300");
	   		cLetterUser.setIfshow("1");
	   		cLetterUser.setIfcomplete("0");
	   		cLetterUser.setCtime(DateUtil.getCurrentTime());
	   		cLetterUser.setCuserid(ShiroKit.getUserId());
	   		cLetterUser.setCusername(ShiroKit.getUserName());
	   		cLetterUser.save();
	   	}
	   	String remark=(o.getLeadersremark()==null?"":o.getLeadersremark())+"\r\n"+comment+"("+ShiroKit.getUserName()+")";
	   	o.setLeadersremark(remark);
	   //	o.setNbshrremark(remark);
	   	o.update();
	   	renderSuccess();
	   
  
   }
   /**
    * 
   * @Title: sendDo 
   * @Description: 送承办
   * @date 2020年11月6日下午4:18:07
    */
   public void sendDo() {
   	OaLetter o = getModel(OaLetter.class);
   	o.setStatus("3");
   	o.update();
   	//o=OaHotline.dao.findById(o.getId());
   	//String hstep = getPara("hstep");
    	String sid = getPara("sid");
    	OaSteps uLetterUser = OaSteps.dao.findById(sid);
    	uLetterUser.setIfcomplete("1");
    	uLetterUser.setCompletetime(DateUtil.getCurrentTime());
    	uLetterUser.update();
    	StepUtil.insertStepHistory(o.getId(),o.getBackup3(),stepType);
    	
    	String[] cbrids = o.getCbrids().split(",");
    	for (String cbrid : cbrids) {
    		OaSteps cLetterUser =new OaSteps();
    		cLetterUser.setId(UuidUtil.getUUID());
    		cLetterUser.setOid(o.getId());
    		cLetterUser.setStep("4");
    		cLetterUser.setType(stepType);
    		cLetterUser.setTitle(o.getBackup3());
    		cLetterUser.setSortnum("400");
    		SysUser user = SysUser.dao.findById(cbrid);
    		SysOrg org = SysOrg.dao.findById(user.getOrgid());
    		cLetterUser.setUserid(user.getId());
    		cLetterUser.setUsername(user.getName());
    		cLetterUser.setOrgid(org.getId());
    		cLetterUser.setOrgname(org.getName());
    		cLetterUser.setIfshow("1");
    		cLetterUser.setIfcomplete("0");
    		cLetterUser.setCtime(DateUtil.getCurrentTime());
    		cLetterUser.setCuserid(ShiroKit.getUserId());
    		cLetterUser.setCusername(ShiroKit.getUserName());
    		cLetterUser.save();
		}
    	renderSuccess();
   }
   
   /**
    * 
   * @Title: sendDleader 
   * @Description: 送部门领导审批
   * @date 2020年11月6日下午4:38:28
    */
   public void sendDleader() {

   	OaLetter o = getModel(OaLetter.class);
   	//o.update();
   	//o=OaLetter.dao.findById(o.getId());
   	String comment = getPara("comment");
   	String hstep = getPara("sstep");
   	
   	String commonuserid = getPara("commonuserid");
   	
    String hotlineId = getPara("sid");
    OaSteps uLetterUser = OaSteps.dao.findById(hotlineId);
    uLetterUser.setIfcomplete("1");
    uLetterUser.setRemarks(comment);
    uLetterUser.setCompletetime(DateUtil.getCurrentTime());
    uLetterUser.update();
   	StepUtil.insertStepHistory(o.getId(),o.getBackup3(),stepType);
   	
   	List<OaSteps> uCompleteList = OaSteps.dao.find("select  * from  oa_steps where oid='"+o.getId()+"' and  step like '"+hstep+"%' and ifcomplete='0'");
   	if(uCompleteList.size()==0) {
   		o.setStatus("4");
   		OaSteps cLetterUser=new OaSteps();
   		cLetterUser.setId(UuidUtil.getUUID());
   		cLetterUser.setOid(o.getId());
   		cLetterUser.setStep("5");
   		cLetterUser.setTitle(o.getBackup3());
   		cLetterUser.setType(stepType);
   		SysUser user = SysUser.dao.findById(commonuserid);
   		SysOrg org = SysOrg.dao.findById(user.getOrgid());
   		cLetterUser.setUserid(user.getId());
   		cLetterUser.setUsername(user.getName());
   		cLetterUser.setOrgid(org.getId());
   		cLetterUser.setOrgname(org.getName());
   		cLetterUser.setSortnum("500");
   		cLetterUser.setIfshow("1");
   		cLetterUser.setIfcomplete("0");
   		cLetterUser.setCtime(DateUtil.getCurrentTime());
   		cLetterUser.setCuserid(ShiroKit.getUserId());
   		cLetterUser.setCusername(ShiroKit.getUserName());
   		cLetterUser.setButtontype("1");
   		cLetterUser.save();
   	}
   	//String remark=(o.getCbrremark()==null?"":o.getCbrremark())+"\r\n"+comment+"("+ShiroKit.getUserName()+")";
   	OaLetter findById = OaLetter.dao.findById(o.getId());
	String remarks= (findById.getCbrremark()==null? "":findById.getCbrremark())+"\r\n"+comment+"\r\n\t\t\t\t\t\t\t\t"+ShiroKit.getUserName()+" "+DateUtil.getCurrentYMD();
	System.out.println("==============="+remarks);
	o.setCbrremark(remarks);
	o.update();
   	renderSuccess();
   }
   
   
   /**
    * 
   * @Title: leaderSendCb 
   * @Description: 部门领导审批不通过送承办
   * @date 2020年11月6日下午5:33:36
    */
   public void leaderSendCb(){ 
 		 
	   
		OaLetter o = getModel(OaLetter.class);
    	o=OaLetter.dao.findById(o.getId());
    	
    	String sid = getPara("sid");
    	String comment = getPara("comment");
    	String commonuserid = getPara("commonuserid");
    	if(comment==null||comment.trim().equals("")) {
    		comment="转送";
    	}
    	
    	
      	OaSteps uHotlineUser=OaSteps.dao.findById(sid);
      	uHotlineUser.setCompletetime(DateUtil.getCurrentTime());
      	uHotlineUser.setIfcomplete("1");
      	uHotlineUser.setRemarks(comment);
      	uHotlineUser.update();
      	StepUtil.insertStepHistory(o.getId(), o.getBackup3(),stepType);
    	
    	
   
      	//o.setCbrremark("");
     
      
      	if(commonuserid!=null &&!commonuserid.equals("")) {
      		String[] userids = commonuserid.split(",");
      		String cbrIds="";
      		String cbrNames="";
      		
      		for (String userid : userids) {
      		   	SysUser user = SysUser.dao.findById(userid);
      		   	
      		   if(o.getType().equals("1")) {

	      		   	if(o.getCbrids().indexOf(userid)==-1) {
	      		   		
	      		   		cbrIds=cbrIds+","+userid;
	      		   		cbrNames=cbrNames+","+user.getName();
	      		   	}
      		   } 
      			SysOrg org = SysOrg.dao.findById(user.getOrgid());
      			OaSteps cLetterUser=new OaSteps();
      			cLetterUser.setId(UuidUtil.getUUID());
      			cLetterUser.setOid(o.getId());
      			cLetterUser.setStep("54");
      			cLetterUser.setType(stepType);
      			cLetterUser.setTitle(o.getBackup3());
      			cLetterUser.setUserid(user.getId());
      			cLetterUser.setUsername(user.getName());
      	
      			cLetterUser.setOrgid(org.getId());
      			cLetterUser.setOrgname(org.getName());
      			cLetterUser.setIfshow("1");
      			cLetterUser.setIfcomplete("0");
      			cLetterUser.setCuserid(ShiroKit.getUserId());
      			cLetterUser.setCusername(ShiroKit.getUserName());
      			cLetterUser.setCtime(DateUtil.getCurrentTime());
      			cLetterUser.setButtontype("1");
      			cLetterUser.save();
			}
      	  if(o.getType().equals("1")) {
      	   	o.setCbrids(o.getCbrids()+cbrIds);
    	    o.setCbrnames(o.getCbrnames()+cbrNames);;
      	  }
      	}
      	
  
      	String remarks=(o.getCbrremark()==null?"":o.getCbrremark()+"\r\n")+comment+"\r\n\t\t\t\t\t\t\t\t"+ShiroKit.getUserName()+" "+DateUtil.getCurrentYMD();
    	o.setCbrremark(remarks);
      	
      	
     	o.setStatus("3");
      	
    	o.update();
      	renderSuccess();
   
   
   
   }
   
   /**
    * 
   * @Title: dleaderOver 
   * @Description: 部门领导办结
   * @date 2020年11月7日下午2:50:20
    */
   public  void dleaderOver() {
	   	OaLetter o = getModel(OaLetter.class);
	   	o=OaLetter.dao.findById(o.getId());
	 	String comment = getPara("comment");
	  	String hotlineId = getPara("sid");
	   	o.setStatus("5");
	   	//String remark =o.getCbrremark()+"\r\n"+"("+ShiroKit.getUserName()+")";
	  	
		String remark=(o.getCbrremark()==null?"":o.getCbrremark())+"\r\n"+comment+"\r\n\t\t\t\t\t\t\t"+ShiroKit.getUserName()+" "+DateUtil.getCurrentYMD();
	   	
	   	o.setCbrremark(remark);
	   	o.update();
	   	o=OaLetter.dao.findById(o.getId());
	  
	 	
	
		OaSteps uLetterUser = OaSteps.dao.findById(hotlineId);
		uLetterUser.setIfcomplete("1");
		uLetterUser.setRemarks(comment);
		uLetterUser.setCompletetime(DateUtil.getCurrentTime());
		uLetterUser.update();
		StepUtil.insertStepHistory(o.getId(),o.getBackup3(),stepType);
		
		OaSteps cLetterUser=new OaSteps();
		
		cLetterUser.setId(UuidUtil.getUUID());
		cLetterUser.setOid(o.getId());
		cLetterUser.setStep("6");
		cLetterUser.setTitle(o.getBackup3());
		cLetterUser.setType(stepType);
		SysUser user = SysUser.dao.findById(o.getCuserid());
		SysOrg org = SysOrg.dao.findById(user.getOrgid());
		cLetterUser.setUserid(user.getId());
		cLetterUser.setUsername(user.getName());
		cLetterUser.setOrgid(org.getId());
		cLetterUser.setOrgname(org.getName());
		//cLetterUser.setSortnum("600");
		cLetterUser.setIfshow("1");
		cLetterUser.setIfcomplete("0");
		cLetterUser.setCtime(DateUtil.getCurrentTime());
		cLetterUser.setCuserid(ShiroKit.getUserId());
		cLetterUser.setCusername(ShiroKit.getUserName());
		cLetterUser.save();
		
		renderSuccess();
		   
	   }
   /**
    * 
    * @Title: cbOver 
    * @Description: 承办办结
    * @date 2020年11月7日下午2:50:20
    */
   public  void cbOver() {
	   OaLetter o = getModel(OaLetter.class);
	   o.update();
	   o=OaLetter.dao.findById(o.getId());
	   String comment = getPara("comment");
	   String sid = getPara("sid");
	   if(comment!=null&&!comment.trim().equals("")) {
		   
		   String remark =o.getCbrremark()+"\r\n"+comment+"\r\n\t\t\t\t\t\t\t"+ShiroKit.getUserName()+" "+DateUtil.getCurrentYMD();
		   o.setCbrremark(remark);
		  
	   }
	   o.update();
	   o=OaLetter.dao.findById(o.getId());
	   
	   
	   
	   OaSteps uLetterUser = OaSteps.dao.findById(sid);
	   uLetterUser.setIfcomplete("1");
	   uLetterUser.setRemarks(comment);
	   uLetterUser.setCompletetime(DateUtil.getCurrentTime());
	   uLetterUser.update();
	   StepUtil.insertStepHistory(o.getId(),o.getBackup3(),stepType);
	   
	   if(uLetterUser.getParentid()!=null) {
		   OaSteps steps=new OaSteps();
		   OaSteps paSteps=new OaSteps().dao().findById(uLetterUser.getParentid());
		   steps.setId(UuidUtil.getUUID());
		   steps.setType(stepType);
		   steps.setTitle(o.getBackup3());
		   steps.setOid(o.getId());
		   steps.setStep("545");
		   steps.setUserid(paSteps.getUserid());
		   steps.setUsername(paSteps.getUsername());
		   steps.setOrgid(paSteps.getOrgid());
		   steps.setOrgname(paSteps.getOrgname());
		   steps.setIfshow("1");
		   steps.setIfcomplete("0");
		   steps.setParentid(paSteps.getParentid());
		   steps.setCtime(DateUtil.getCurrentTime());
		   steps.setCuserid(ShiroKit.getUserId());
		   steps.setCusername(ShiroKit.getUserName());
		   steps.save();
		   
		   
		   
		   
		   
	   }else {
		   o.setStatus("5");
		   o.update();
		   OaSteps cLetterUser=new OaSteps();
		   
		   cLetterUser.setId(UuidUtil.getUUID());
		   cLetterUser.setOid(o.getId());
		   cLetterUser.setStep("6");
		   cLetterUser.setType(stepType);
		   cLetterUser.setTitle(o.getBackup3());
		   SysUser user = SysUser.dao.findById(o.getCuserid());
		   SysOrg org = SysOrg.dao.findById(user.getOrgid());
		   cLetterUser.setUserid(user.getId());
		   cLetterUser.setUsername(user.getName());
		   cLetterUser.setOrgid(org.getId());
		   cLetterUser.setOrgname(org.getName());
		   cLetterUser.setSortnum("600");
		   cLetterUser.setIfshow("1");
		   cLetterUser.setIfcomplete("0");
		   cLetterUser.setCtime(DateUtil.getCurrentTime());
		   cLetterUser.setCuserid(ShiroKit.getUserId());
		   cLetterUser.setCusername(ShiroKit.getUserName());
		   cLetterUser.save();
	   }
	   
	   
	   
	   renderSuccess();
	   
   }
   
   /**
    * 
   * @Title: hotlineOver 
   * @Description: 归档
   * @date 2020年11月6日下午5:24:20
    */
 public void  letterOver() {
	   	OaLetter o = getModel(OaLetter.class);
	   	o.setStatus("6");
	   	o.update();
	   	o=OaLetter.dao.findById(o.getId());
	   	String comment = getPara("comment");
	  
	 	String sid = getPara("sid");
		OaSteps uLetterUser = OaSteps.dao.findById(sid);
		uLetterUser.setIfcomplete("1");
		uLetterUser.setRemarks(comment);
		uLetterUser.setCompletetime(DateUtil.getCurrentTime());
		uLetterUser.update();
		
		StepUtil.insertStepHistory(o.getId(),o.getBackup3(),stepType);
		renderSuccess();
 }
  
   
 
 
 /**
	 * 
	* @Title: SendCb 
	* @Description: 送承办
	* @date 2020年11月7日下午6:06:20
	 */
	public  void  SendCb() {
		

		String buttonType="0"; // 0不可操作，1可操作
	 
		OaLetter o = getModel(OaLetter.class);
		//o.setStatus("3");
		//o.update();
		
		String sid= getPara("sid");
		OaSteps cBUser = OaSteps.dao.findById(sid);
		cBUser.setIfcomplete("1");
		cBUser.setCompletetime(DateUtil.getCurrentTime());
		cBUser.update();
		StepUtil.insertStepHistory(o.getId(),o.getBackup3(), stepType);

		
		if( o.getLeadersid()!=null && !o.getLeadersid().equals("")) {
			
		
		String[] leadersids = o.getLeadersid().split(",");
		
		for (String leadersid : leadersids) {
			 int size = OaSteps.dao.find("select  * from oa_steps where  oid='"+o.getId()+"' and userid='"+leadersid+"' and step='41'").size();
			if(size==0) {
				OaSteps aBUser =new OaSteps();
				SysUser user = SysUser.dao.findById(leadersid);
				SysOrg org = SysOrg.dao.findById(user.getOrgid());
				aBUser.setId(UuidUtil.getUUID());
				aBUser.setOid(o.getId());
				aBUser.setType(stepType);
				aBUser.setTitle(o.getBackup3());
				aBUser.setUserid(user.getId());
				aBUser.setUsername(user.getName());
				aBUser.setOrgid(org.getId());
				aBUser.setOrgname(org.getName());
				aBUser.setIfshow("1");//显示
				aBUser.setIfcomplete("0");
				aBUser.setCtime(DateUtil.getCurrentTime());
				aBUser.setCusername(ShiroKit.getUserName());
				aBUser.setCuserid(ShiroKit.getUserId());
				aBUser.setStep("41");
				aBUser.setStepdesc(user.getSort().toString());
				
				if(leadersid.equals("4")||leadersid.equals("147")) {
					String updateSql="update oa_steps set ifcomplete='0',buttontype='0'  where  oid='"+o.getId()+"' and userid  not in ('4','147') and step in('41','42','43') ";
					Db.update(updateSql);
					aBUser.setButtontype("1");
					
				}else {
					int size2 = OaSteps.dao.find("select  * from oa_steps where  oid='"+o.getId()+"' and userid in ('4','147') and  ifcomplete='0'  and step='41'").size();
					if(size2==0) {
						aBUser.setButtontype("1");
					}else {
						aBUser.setButtontype("0");
					}
					String updateSql="update oa_steps set ifcomplete='0',buttontype='0'  where  oid='"+o.getId()+"' and step in ('42','43')";
					Db.update(updateSql);
				}
				aBUser.save();
			}
			 
		}
		
		}
		
		if( o.getHostsid()!=null && !o.getHostsid().equals("")) {
			
			
			
				int leaderFinishSize = OaSteps.dao.find("select  * from oa_steps where  oid='"+o.getId()+"' and step='41' and ifcomplete='0'").size();
				
				if(leaderFinishSize==0) {
					buttonType="1";
				}else {
					buttonType="0";
				}
			
		
				String[] hostids = o.getHostsid().split(",");
				
				for (String hostid : hostids) {
					// int size = OaSteps.dao.find("select  * from oa_steps where  oid='"+o.getId()+"' and userid='"+hostid+"' and step='42'").size();
					 //if(size==0) {
						 OaSteps buser =new OaSteps();
						 SysUser user = SysUser.dao.findById(hostid);
						 SysOrg org = SysOrg.dao.findById(user.getOrgid());
						 buser.setId(UuidUtil.getUUID());
						 buser.setOid(o.getId());
						 buser.setType(stepType);
						 buser.setTitle(o.getBackup3());
						 buser.setUserid(user.getId());
						 buser.setUsername(user.getName());
						 buser.setOrgid(user.getOrgid());
						 buser.setOrgname(org.getName());
						 buser.setIfshow("1");//显示
						 buser.setIfcomplete("0");
						 //buser.setCompletetime(DateUtil.getCurrentTime());
						 buser.setCtime(DateUtil.getCurrentTime());
						 buser.setCusername(ShiroKit.getUserName());
						 buser.setCuserid(ShiroKit.getUserId());
						 buser.setStep("42");//主办
						 buser.setStepdesc(user.getSort().toString());
						 buser.setButtontype(buttonType);
						 buser.save();
					 //}
				}
		}
		
		
		if( o.getCustomersid()!=null && !o.getCustomersid().equals("")) {
			
			
			int leaderFinishSize = OaSteps.dao.find("select  * from oa_steps where  oid='"+o.getId()+"' and step='41' and ifcomplete='0'").size();
			
			if(leaderFinishSize==0) {
				buttonType="1";
			}else {
				buttonType="0";
			}
		String[] customerids = o.getCustomersid().split(",");
		
		for (String customerid : customerids) {
			//int size = OaSteps.dao.find("select  * from oa_steps where  oid='"+o.getId()+"' and userid='"+customerid+"' and step='43'").size();
			 //if(size==0) {
				 OaSteps buser =new OaSteps();
				 SysUser user = SysUser.dao.findById(customerid);
				 buser.setId(UuidUtil.getUUID());
				 buser.setOid(o.getId());
				 buser.setType(stepType);
				 buser.setTitle(o.getBackup3());
				 buser.setUserid(user.getId());
				 buser.setUsername(user.getName());
				 buser.setOrgid(user.getOrgid());
				 SysOrg org = SysOrg.dao.findById(user.getOrgid());
				 buser.setOrgname(org.getName());
				 buser.setIfshow("1");//显示
				 buser.setIfcomplete("0");
				 //buser.setCompletetime(DateUtil.getCurrentTime());
				 buser.setCtime(DateUtil.getCurrentTime());
				 buser.setCusername(ShiroKit.getUserName());
				 buser.setCuserid(ShiroKit.getUserId());
				 buser.setStep("43");//会办
				 buser.setStepdesc(user.getSort().toString());
				 buser.setButtontype(buttonType);
				 buser.save();
			// }
			
		}
		
		}
		
		
		 o.setStatus("3");
		 o.update();
		
	  	renderSuccess();
		
		
	}
 
	/**
	 * 
	* @Title: leadersPs 
	* @Description: 领导批示
	* @date 2020年10月30日上午10:13:30
	 */
	
	public void leadersPs() {

		String comment=getPara("comment");
		OaLetter o = getModel(OaLetter.class);
		
		OaSteps obu = OaSteps.dao.findById(getPara("sid"));
		obu.setIfcomplete("1");
		obu.setCompletetime(DateUtil.getCurrentTime());
		obu.setRemarks(comment);
		obu.update();
		StepUtil.insertStepHistory(o.getId(),o.getBackup3(), stepType);
		
		
		
		//o.setLeadersremark((o.getLeadersremark()==null?"":o.getLeadersremark()+"\r\n")+"\r\n"+comment+"\r\n\t\t\t\t\t\t\t\t\t\t"+ShiroKit.getUserName()+" "+DateUtil.getCurrentYMD());
		
		List<OaSteps> oss = OaSteps.dao.find("select  * from oa_steps  where oid='"+o.getId()+"' and step='41' and  ifcomplete='1' order by stepdesc ");
		String remarks="";
		
		for (OaSteps oaStep : oss) {
			remarks=remarks+"\r\n"+oaStep.getRemarks()+"\r\n\t\t\t\t\t"+oaStep.getUsername()+" "+oaStep.getCompletetime().substring(0, 10);
		}
		
		
		o.setLeadersremark(remarks);
		
		

		List<OaSteps> leadersPS = OaSteps.dao.find("select  * from  oa_steps   where oid='"+o.getId()+"' and   step ='41'  and userid in ('4','147')") ;
		
		if(leadersPS.size()==0) {
			int size = OaSteps.dao.find("select  * from  oa_steps   where oid='"+o.getId()+"' and   step ='41' and ifcomplete='0'").size();
			if(size==0) {
				Db.update("update oa_steps set buttontype='1' where oid='"+o.getId()+"'");
			}
			
		}else {
			int size = OaSteps.dao.find("select  * from  oa_steps   where oid='"+o.getId()+"' and   step ='41'  and userid in ('4','147')  and ifcomplete='0'").size() ;
			
			if(size==0) {
				Db.update("update oa_steps set buttontype='1' where oid='"+o.getId()+"' and   step ='41'");
			}
			
			
			int size2 = OaSteps.dao.find("select  * from  oa_steps   where oid='"+o.getId()+"' and   step ='41'    and ifcomplete='0'").size() ;
			
			if(size2==0) {
				Db.update("update oa_steps set buttontype='1' where oid='"+o.getId()+"'");
			}
		}
		
		
		
		
		
		int size = OaSteps.dao.find("select  * from  oa_steps   where oid='"+o.getId()+"' and ifcomplete='0'  and step like '4%'").size();
		if(size==0) {
			o.setStatus("5");
			OaLetter os = OaLetter.dao.findById(o.getId());
			SysUser user = SysUser.dao.findById(os.getCuserid());
			SysOrg org = SysOrg.dao.findById(user.getOrgid());
			OaSteps buser =new OaSteps();
			buser.setId(UuidUtil.getUUID());
			buser.setOid(o.getId());
			buser.setType(stepType);
			buser.setTitle(o.getBackup3());
			buser.setUserid(user.getId());
			buser.setUsername(user.getName());
			buser.setOrgid(org.getId());
			buser.setOrgname(org.getName());
			buser.setIfshow("1");//显示
			buser.setIfcomplete("0");
			//buser.setCompletetime(DateUtil.getCurrentTime());
			buser.setCtime(DateUtil.getCurrentTime());
			buser.setCusername(ShiroKit.getUserName());
			buser.setCuserid(ShiroKit.getUserId());
			buser.setStep("7");
			buser.save();
			
		}
		o.update();
		
		renderSuccess();
		 
	}
	
	/**
	 * 
	* @Title: overStep 
	* @Description: 主会办完结 
	* @author bkkco
	* @date 2020年10月30日上午11:16:51
	 */
	
	public void overStep() {
		
		OaLetter o = getModel(OaLetter.class);
		String sid =getPara("sid");
		String sstep=getPara("sstep");
		OaSteps obu = OaSteps.dao.findById(sid);
		String comment=getPara("comment");
		obu = OaSteps.dao.findById(sid);
		obu.setIfcomplete("1");
		obu.setCompletetime(DateUtil.getCurrentTime());
		obu.setRemarks(comment);
		obu.update();
		
		StepUtil.insertStepHistory(o.getId(),o.getBackup3(),stepType);
		
		String remark="";
		
		List<OaSteps> zbList = OaSteps.dao.find("select  * from  oa_steps where oid='"+o.getId()+"' and ifcomplete='1'  and step ='42' order by  ctime  ");
		if(zbList.size()>0) {
			for (OaSteps zb : zbList) {
				
				//if(zb.getRemarks()!=null && !zb.getRemarks().trim().equals("")) {
					//remark+=zb.getRemarks()+"("+zb.getUsername()+")"+"\r\n";
					remark=remark+"\r\n"+zb.getRemarks()+"\r\n\t\t\t\t\t\t\t\t\t\t"+zb.getUsername()+" "+DateUtil.getCurrentYMD();
				//}
				
			}
		}
		List<OaSteps> cbList = OaSteps.dao.find("select  * from  oa_steps WHERE oid='"+o.getId()+"' and ifcomplete='1'  and step ='43' order by  ctime  ");
		if(zbList.size()>0) {
			for (OaSteps cb : cbList) {
				
				//if(cb.getRemarks()!=null && !cb.getRemarks().trim().equals("")) {
					//remark+=cb.getRemarks()+"("+cb.getUsername()+")"+"\r\n";
				remark=remark+"\r\n"+cb.getRemarks()+"\r\n\t\t\t\t\t\t\t\t\t\t"+cb.getUsername()+" "+DateUtil.getCurrentYMD();
				//}
				
			}
		}
		
		o.setCbrremark(remark);
		
		
		
		if(obu.getParentid()==null) {
			
			
			int size = OaSteps.dao.find("select  * from  oa_steps WHERE oid='"+o.getId()+"' and ifcomplete='0'  and step like '4%'").size();
			if(size==0) {
				o.setStatus("5");
				OaLetter os = OaLetter.dao.findById(o.getId());
				SysUser user = SysUser.dao.findById(os.getCuserid());
				SysOrg org = SysOrg.dao.findById(user.getOrgid());
				OaSteps buser =new OaSteps();
				buser.setId(UuidUtil.getUUID());
				buser.setOid(o.getId());
				buser.setType(stepType);
				buser.setTitle(o.getBackup3());
				buser.setUserid(user.getId());
				buser.setUsername(user.getName());
				buser.setOrgid(org.getId());
				buser.setOrgname(org.getName());
				buser.setIfshow("1");//显示
				buser.setIfcomplete("0");
				buser.setCtime(DateUtil.getCurrentTime());
				buser.setCusername(ShiroKit.getUserName());
				buser.setCuserid(ShiroKit.getUserId());
				buser.setStep("7");
				buser.save();
			}
			
		}else {
			
		
	 
			
			
			OaSteps nowBu = OaSteps.dao.findById(sid);
			int size = OaSteps.dao.find("select  * from oa_steps where oid='"+nowBu.getOid()+"' and parentid='"+nowBu.getParentid()+"'  and ifcomplete='0' ").size();
			if(size==0) {
				
				OaSteps pBu = OaSteps.dao.findById(nowBu.getParentid());
				
				OaSteps buser =new OaSteps();
				buser.setId(UuidUtil.getUUID());
				buser.setOid(o.getId());
				buser.setType(stepType);
				buser.setTitle(o.getBackup3());
				buser.setUserid(pBu.getUserid());
				buser.setUsername(pBu.getUsername());
				buser.setOrgid(pBu.getOrgid());
				buser.setOrgname(pBu.getOrgname());
				buser.setParentid(pBu.getParentid());
				buser.setIfshow("1");//显示
				buser.setIfcomplete("0");
				buser.setCtime(DateUtil.getCurrentTime());
				buser.setCusername(ShiroKit.getUserName());
				buser.setCuserid(ShiroKit.getUserId());
				buser.setStep(sstep);
				buser.save();
			
			}
		}
		
		
		o.update();
		
		renderSuccess();
		 
	}
	/**
	 * 
	* @Title: sendSon 
	* @Description: TODO  添加承办
	* @date 2020年10月30日下午1:39:02
	 */
	
	public void sendSon() {
		
		String [] addPIds=getPara("commonuserid").split(",");
		OaLetter o = getModel(OaLetter.class);
		String sid=getPara("sid");
		String sstep = getPara("sstep");
		String comment=getPara("comment");
		
		OaSteps obu = OaSteps.dao.findById(sid);
		obu.setIfcomplete("1");
		obu.setCompletetime(DateUtil.getCurrentTime());
		obu.setRemarks(comment);
		obu.update();
		
		StepUtil.insertStepHistory(o.getId(),o.getBackup3(), stepType);
		
		
		String remark="";
		
		List<OaSteps> zbList = OaSteps.dao.find("select  * from  oa_steps WHERE oid='"+o.getId()+"' and ifcomplete='1'  and step ='42' order by  ctime   ");
		if(zbList.size()>0) {
			for (OaSteps zb : zbList) {
				
				//if(zb.getRemarks()!=null && !zb.getRemarks().trim().equals("")) {
					//remark+=zb.getRemarks()+"("+zb.getUsername()+")"+"\r\n";
				remark=remark+"\r\n"+zb.getRemarks()+"\r\n\t\t\t\t\t\t\t\t\t\t"+zb.getUsername()+" "+DateUtil.getCurrentYMD();
				//}
				
			}
		}
		List<OaSteps> cbList = OaSteps.dao.find("select  * from  oa_steps WHERE oid='"+o.getId()+"' and ifcomplete='1'  and step ='43' order by  ctime   ");
		if(zbList.size()>0) {
			for (OaSteps cb : cbList) {
				
				//if(cb.getRemarks()!=null && !cb.getRemarks().trim().equals("")) {
				remark=remark+"\r\n"+cb.getRemarks()+"\r\n\t\t\t\t\t\t\t\t\t\t"+cb.getUsername()+" "+DateUtil.getCurrentYMD();
				//}
				
			}
		}
		
		o.setCbrremark(remark);
		
		o.update();
		
	
		
		for (String addid : addPIds) {
			OaSteps buser =new OaSteps();
			SysUser user = SysUser.dao.findById(addid);
			SysOrg org = SysOrg.dao.findById(user.getOrgid());
			buser.setId(UuidUtil.getUUID());
			buser.setOid(o.getId());
			buser.setType(stepType);
			buser.setTitle(o.getBackup3());
			buser.setUserid(user.getId());
			buser.setUsername(user.getName());
			buser.setOrgid(org.getId());
			buser.setOrgname(org.getName());
			buser.setParentid(sid);
			buser.setIfshow("1");//显示
			buser.setIfcomplete("0");
			buser.setCtime(DateUtil.getCurrentTime());
			buser.setCusername(ShiroKit.getUserName());
			buser.setCuserid(ShiroKit.getUserId());
			buser.setStep(sstep);
			buser.save();
		}
		
		renderSuccess();
	 
	}
   
	/**
	 * 
	 * @Title: cbSendCb 
	 * @Description: TODO  承办送承办
	 * @date 2020年10月30日下午1:39:02
	 */
	
	public void cbSendCb() {
		
		String [] addPIds=getPara("commonuserid").split(",");
		OaLetter o = getModel(OaLetter.class);
		String sid=getPara("sid");
		String comment=getPara("comment");
		
		OaSteps obu = OaSteps.dao.findById(sid);
		obu.setIfcomplete("1");
		obu.setCompletetime(DateUtil.getCurrentTime());
		obu.setRemarks(comment);
		obu.update();
		
		StepUtil.insertStepHistory(o.getId(),o.getBackup3(), stepType);
		
		
		String remark="";
		
		List<OaSteps> zbList = OaSteps.dao.find("select  * from  oa_steps WHERE oid='"+o.getId()+"' and ifcomplete='1'  and step ='42' order by  ctime   ");
		if(zbList.size()>0) {
			for (OaSteps zb : zbList) {
				
				//if(zb.getRemarks()!=null && !zb.getRemarks().trim().equals("")) {
					//remark+=zb.getRemarks()+"("+zb.getUsername()+")"+"\r\n";
				remark=remark+"\r\n"+zb.getRemarks()+"\r\n\t\t\t\t\t\t\t\t\t\t"+zb.getUsername()+" "+DateUtil.getCurrentYMD();
				//}
				
			}
		}
		List<OaSteps> cbList = OaSteps.dao.find("select  * from  oa_steps WHERE oid='"+o.getId()+"' and ifcomplete='1'  and step ='43' order by  ctime   ");
		if(zbList.size()>0) {
			for (OaSteps cb : cbList) {
				
				//if(cb.getRemarks()!=null && !cb.getRemarks().trim().equals("")) {
					//remark+=cb.getRemarks()+"("+cb.getUsername()+")"+"\r\n";
				remark=remark+"\r\n"+cb.getRemarks()+"\r\n\t\t\t\t\t\t\t\t\t\t"+cb.getUsername()+" "+DateUtil.getCurrentYMD();
				//}
				
			}
		}
		
		o.setCbrremark(remark);
		
		o.update();
		
		for (String addid : addPIds) {
			OaSteps buser =new OaSteps();
			SysUser user = SysUser.dao.findById(addid);
			SysOrg org = SysOrg.dao.findById(user.getOrgid());
			buser.setId(UuidUtil.getUUID());
			buser.setOid(o.getId());
			buser.setType(stepType);
			buser.setTitle(o.getBackup3());
			buser.setUserid(user.getId());
			buser.setUsername(user.getName());
			buser.setOrgid(org.getId());
			buser.setOrgname(org.getName());
			buser.setParentid(sid);
			buser.setIfshow("1");//显示
			buser.setIfcomplete("0");
			buser.setCtime(DateUtil.getCurrentTime());
			buser.setCusername(ShiroKit.getUserName());
			buser.setCuserid(ShiroKit.getUserId());
			buser.setStep("54");
			buser.save();
		}
		
		renderSuccess();
		
	}
	
	
	public void editSave() {
		OaLetter o = getModel(OaLetter.class);
		o.update();
		
		renderSuccess();
		
	}
	
	  public void  print() {
	    	
	    	String id= getPara("id");
	    	
	    	OaLetter hotline = OaLetter.dao.findById(id);
	    	setAttr("o",hotline);
	    	render("hotlineprint.html");
	    }
	  
	  public void  printFromSearh() {
		  	String id= getPara("id");
		  	OaLetter hotline = OaLetter.dao.findById(id);
		  	setAttr("o",hotline);
		  	List<SysAttachment> find = SysAttachment.dao.find("select  * from sys_attachment where  business_id='"+id+"' order by create_time desc");
		  	
		  	if(find!=null && find.size()!=0) {
		  		
		  		setAttr("fileName", find.get(0).getFileName());
		  	}
		  	setAttr("fileName", "");
		  	 String cuserid = hotline.getCuserid();
		  	 SysUser user = SysUser.dao.findById(cuserid);
		   
		  	 List<OaSteps> find2 = OaSteps.dao.find( "select  * from oa_steps  where oid='"+hotline.getId()+"' and  completetime  is not null and userid <>'"+user.getId()+"' and orgid<>'"+user.getOrgid()+"' order by  completetime desc ");
		  	
		  	 String doDepart="";
		  	 String doTime="";
		  	 
		  	if(find2!=null&& find2.size()!=0) {
		  		OaSteps step = find2.get(0);
		  		SysOrg org = SysOrg.dao.findById(step.getOrgid());
		  		doDepart=org.getName();
		  		doTime= step.getCompletetime().substring(0, 10);
		  	}	
		  	
		  	setAttr("doDepart", doDepart);
		  	setAttr("doTime", doTime);
		  	
		  	render("printFromSearch.html");
		  }
	  
	  
	  
	  
	  /**
	   * 
	  * @Title: saveEdit 
	  * @Description: 保存修改
	  * @date 2020年12月3日下午4:34:58
	   */
	  public void saveEdit() {
		  
		  OaLetter o = getModel(OaLetter.class);
		  o.update();
		  renderSuccess();
	  }
	
	  
	  
	  /*
	     * 导出
	     */
	    
		public void exportExcel() throws UnsupportedEncodingException{
	 
			String endTime = getPara("endTime","");
			String startTime = getPara("startTime","");
			String lnum = java.net.URLDecoder.decode(getPara("lnum",""),"UTF-8");
			String lfromer = java.net.URLDecoder.decode(getPara("lfromer",""),"UTF-8");
			String lstate = getPara("lstate","");
			String lfromnum=java.net.URLDecoder.decode(getPara("lfromnum",""),"UTF-8");
			String path = this.getSession().getServletContext().getRealPath("")+"/upload/export/"+DateUtil.format(new Date(),21)+".xlsx";
			File file = service.exportExcel(path,lnum,lfromer,lfromnum,startTime,endTime,lstate);
			renderFile(file);
		}
	    
	  
}