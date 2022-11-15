package com.pointlion.mvc.admin.oa.senddoc;

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
import com.pointlion.mvc.common.model.OaEmail;
import com.pointlion.mvc.common.model.OaEmailSon;
import com.pointlion.mvc.common.model.OaSenddoc;
import com.pointlion.mvc.common.model.OaSenddocStep;
import com.pointlion.mvc.common.model.OaSteps;
import com.pointlion.mvc.common.model.SysAttachment;
import com.pointlion.mvc.common.model.SysOrg;
import com.pointlion.mvc.common.model.SysUser;
import com.pointlion.mvc.common.utils.DateUtil;
import com.pointlion.mvc.common.utils.StepUtil;
import com.pointlion.mvc.common.utils.StringUtil;
import com.pointlion.mvc.common.utils.UuidUtil;
import com.pointlion.plugin.shiro.ShiroKit;



public class OaSenddocController extends BaseController {
	public static final OaSenddocService service = OaSenddocService.me;
	public static WorkFlowService wfservice = WorkFlowService.me;
	
	public static final String stepType="2";
	/***
	 * get list page
	 */
	public void getListPage(){
		renderIframe("list.html");
    }
	public void getListPage1(){
		renderIframe("list1.html");
	}
	/**
	* @Title: getListPersonal 
	* @Description: 发文个人任务列表
	* @date 2020年11月3日下午5:06:37
	*/
	public void getListPersonal(){
		renderIframe("pSendList.html");
	}
	
	/**
	* @Title: listPersonal 
	* @Description: 发文个人列表数据
	* @date 2020年11月3日下午5:18:27
	*/
    public void listPersonal(){
    	String curr = getPara("pageNumber");
    	String pageSize = getPara("pageSize");
    	Page<Record> page = service.getPersonalPage(Integer.valueOf(curr),Integer.valueOf(pageSize));
    	renderPage(page.getList(),"",page.getTotalRow());
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
		String stype = getPara("stype","");
		String snum = java.net.URLDecoder.decode(getPara("snum",""),"UTF-8");
		String stitle = java.net.URLDecoder.decode(getPara("stitle",""),"UTF-8");
		String sstate = getPara("sstate","");
    	//Page<Record> page = service.getPage(Integer.valueOf(curr),Integer.valueOf(pageSize),startTime,endTime,applyUser);
		Page<Record> page = service.getPage(Integer.valueOf(curr),Integer.valueOf(pageSize),startTime,endTime,stype, snum, stitle, sstate);
    	renderPage(page.getList(),"",page.getTotalRow());
    }
    public void listData1(){
    	String curr = getPara("pageNumber");
    	String pageSize = getPara("pageSize");
    	Page<Record> page = service.getPage(Integer.valueOf(curr),Integer.valueOf(pageSize));
    	renderPage(page.getList(),"",page.getTotalRow());
    }
   /**
    * 
   * @Title: save 
   * @Description: 送分管领导
   * @date 2020年11月9日下午4:19:55
    */
    public void save(){
    	OaSenddoc o = getModel(OaSenddoc.class);
    /*	if(StrKit.notBlank(o.getId())){
    		o.update();
    	}else{*/
    		//o.setId(UuidUtil.getUUID());
    		//o.setCreateTime(DateUtil.getCurrentTime());
    		o.setCtime(DateUtil.getCurrentTime());
    		o.setCuserid(ShiroKit.getUserId());
    		o.setCuser(ShiroKit.getUsername());
    	    //SysOrg sysOrg = SysOrg.dao.findFirst("select  * from sys_org where  name='办公室'");
    		//o.setDofficesure(sysOrg.getId());
    		o.setStatus("1");
    		
    		String dtype = o.getDtype();   
    	/*	String dnum="";
    		if(dtype.equals("1")||dtype.equals("2")) {
    			dnum="沪交行"+o.getDsource()+"("+o.getDyear()+")"+o.getDnumber()+"号";
    		}else {
    			dnum=o.getDsource()+"("+o.getDyear()+")"+o.getDnumber();
    		}
    		o.setDnum(dnum);*/
    		o.save();
    		StepUtil.insertStepHistory(o.getId(),o.getDtitle(), stepType);
    		
    	/*}*/
    	
    	
    	sendDocStep1(o,ShiroKit.getUserId(),"1",100,"1","1");
		String[] fgldids = o.getDfgldids().split(",");
		int i=0;
		for (String fgldid : fgldids) {
			
			sendDocStep1(o,fgldid,"2",200+i,"1","0");
			i++;
		}
		
    	renderSuccess();
    }
	/** 
	* @Title: sendDocStep1 
	* @Description: 分管领导 
	* @param o void
	* @date 2020年11月3日下午4:21:41
	*/ 
	public void sendDocStep1(OaSenddoc o,String actorid ,String  step,int sortnum,String ifshow,String ifcomplete) {
		
   int size = OaSenddocStep.dao.find("select * from oa_steps  where oid='"+o.getId()+"' and  userid='"+actorid+"'  and ifcomplete='0'").size();
		if(size==0) {
			
			OaSteps os=new OaSteps();
			os.setId(UuidUtil.getUUID());
			os.setOid(o.getId());
			os.setStep(step);
			os.setType(stepType);
			os.setTitle(o.getDtitle());
			os.setSortnum(String.valueOf(sortnum));
			SysUser user = SysUser.dao.findById(actorid);
			os.setUserid(user.getId());
			os.setUsername(user.getName());
			os.setOrgid(user.getOrgid());
			SysOrg org = SysOrg.dao.findById(user.getOrgid());
			os.setOrgname(org.getName());
			os.setIfshow(ifshow);
			os.setIfcomplete(ifcomplete);
			os.setCuserid(ShiroKit.getUserId());
			os.setCusername(ShiroKit.getUserName());
			os.setCtime(DateUtil.getCurrentTime());
			os.setStepdesc(user.getSort().toString());
			os.save();
		}	
	}
    
	
	/***
	 * 发文个人任务页面
	 */
	public void getPersonalForm(){
		String id = getPara("id");
		String view = getPara("view");
		setAttr("view", view);
	/*	List<SysOrg> orgLists = SysOrg.dao.find("select *  from  sys_org where parent_id='root'  and name !='中心领导' order by sort ");
		
		setAttr("orgLists",orgLists);*/
		
		SysAttachment sa = SysAttachment.dao.findFirst("select  * from sys_attachment  where business_id='"+id+"' and  des='1'");
		if(sa!=null) {
			setAttr("fid", sa.getId());
			setAttr("uploadperson",sa.getCreateUserName());
			setAttr("filedName",sa.getFileName());
		}
 
		List<SysOrg> orgList = SysOrg.dao.find("select id  from  sys_org where  name='中心领导'");
		List<SysUser> userList = SysUser.dao.find("select * from  sys_user where orgid='"+orgList.get(0).getId()+"' order by sort");
		setAttr("userList",userList);
		
		keepPara("sid");
		keepPara("sstep");
		OaSenddoc o = new OaSenddoc();
			o = service.getById(id);
		if(o.getCuserid().equals(ShiroKit.getUserId())) {
			
			setAttr("cUser", true);
		}else {
			setAttr("cUser", false);
		}
			
			
		setAttr("o", o);
		setAttr("formModelName",StringUtil.toLowerCaseFirstOne(OaSenddoc.class.getSimpleName()));
		renderIframe("pSend.html");
	}
    
	/**
	 * 
	* @Title: osFgldps 
	* @Description: 分管领导批示
	* @date 2020年11月4日上午9:12:22
	 */
	
	public void osFgldps() {
		
		OaSenddoc o = getModel(OaSenddoc.class);
		o=OaSenddoc.dao.findById(o.getId());
		String sid = getPara("sid");
		String sstep = getPara("sstep");
		String comment = getPara("comment");
		
		OaSteps os = OaSteps.dao.findById(sid);
		os.setCompletetime(DateUtil.getCurrentTime());
		os.setIfcomplete("1");
		os.setRemarks(comment);
		os.update();
		
		StepUtil.insertStepHistory(o.getId(),o.getDtitle(),stepType);
		
		List<OaSteps> noComplete = OaSteps.dao.find("select  * from oa_steps  where  oid='"+o.getId()+"' and step='"+sstep+"' and  ifcomplete='0'");
		if(noComplete.size()==0) {
			OaSteps cos=new OaSteps();
			SysUser user = SysUser.dao.findById(o.getCuserid());
			SysOrg org = SysOrg.dao.findById(user.getOrgid());
			cos.setId(UuidUtil.getUUID());
			cos.setOid(o.getId());
			cos.setStep("21");
			cos.setType(stepType);
			cos.setTitle(o.getDtitle());
			cos.setSortnum("210");
			cos.setUserid(user.getId());
			cos.setUsername(user.getName());
			cos.setOrgid(org.getId());
			cos.setOrgname(org.getName());
			cos.setIfshow("1");
			cos.setIfcomplete("0");
			cos.setCuserid(ShiroKit.getUserId());
			cos.setCusername(ShiroKit.getUserName());
			cos.setCtime(DateUtil.getCurrentTime());
			cos.save();
			o.setStatus("2");
		}
		
		List<OaSteps> oss = OaSteps.dao.find("select  * from oa_steps  where oid='"+o.getId()+"' and step='"+sstep+"' and  ifcomplete='1' order by stepdesc ");
		String remarks="";
		
		for (OaSteps oaStep : oss) {
			remarks=remarks+"\r\n"+oaStep.getRemarks()+"\r\n\t\t\t\t\t\t\t\t\t\t"+oaStep.getUsername()+" "+oaStep.getCompletetime().substring(0, 10);
		}
		
		o.setDfgldyj(remarks);
		o.update();
		renderSuccess("批示成功!");
	}
	
	
	
	/**
	 * 
	* @Title: osSendHq 
	* @Description: 送会签
	* @date 2020年11月4日上午11:19:20
	 */
	public void  osSendHq() {
		OaSenddoc o = getModel(OaSenddoc.class);
		
		String sid = getPara("sid");
		o.setStatus("3");
		o.update();
		
		OaSteps uos = OaSteps.dao.findById(sid);
		uos.setIfcomplete("1");
		uos.setCompletetime(DateUtil.getCurrentTime());
		uos.update();
		
		StepUtil.insertStepHistory(o.getId(),o.getDtitle(),stepType);
		
		String[] hqrids = o.getDhqrids().split(",");
		int i=0;
		for (String hqrid : hqrids) {
			OaSteps os =new OaSteps();
			os.setId(UuidUtil.getUUID());
			os.setOid(o.getId());
			os.setStep("3");
			os.setType(stepType);
			os.setTitle(o.getDtitle());
    		os.setSortnum(String.valueOf(300+i));
    		SysUser user = SysUser.dao.findById(hqrid);
    		SysOrg  org = SysOrg.dao.findById(user.getOrgid());
    		os.setUserid(user.getId());
    		os.setUsername(user.getName());
    		os.setOrgid(org.getId());
    		os.setOrgname(org.getName());
    		os.setIfshow("1");
    		os.setIfcomplete("0");
    		os.setCtime(DateUtil.getCurrentTime());
    		os.setCuserid(ShiroKit.getUserId());
    		os.setCusername(ShiroKit.getUserName());
    		os.setStepdesc(user.getSort().toString());
    		os.save();
			i++;
		}
		
		renderSuccess();
	}
	/**
	 * 
	* @Title: osHq 
	* @Description: 会签
	* @date 2020年11月4日下午1:50:05
	 */
	public  void osHq() {
		
		OaSenddoc o = getModel(OaSenddoc.class);
		//o=OaSenddoc.dao.findById(o.getId());
		String sid = getPara("sid");
		String sstep = getPara("sstep");
		String comment = getPara("comment");
		
		OaSteps uos = OaSteps.dao.findById(sid);
		uos.setCompletetime(DateUtil.getCurrentTime());
		uos.setIfcomplete("1");
		uos.setRemarks(comment);
		uos.update();
		StepUtil.insertStepHistory(o.getId(),o.getDtitle(), stepType);
		
		//String hqyj= o.getDhqryj()==null?"":o.getDhqryj();
				
		SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd");
		//hqyj=hqyj+"\r\n"+ comment+"\r\n\t\t\t\t\t\t\t\t\t\t"+ShiroKit.getUserName()+" "+sdf.format(new Date());
	 
		
		
		
		
		List<OaSteps> oss = OaSteps.dao.find("select  * from oa_steps  where  oid='"+o.getId()+"' and  step='"+sstep+"' and  ifcomplete='1' order by stepdesc ");
		String hqyj="";
		for (OaSteps oaStep : oss) {
			hqyj=hqyj+oaStep.getRemarks()+"\r\n\t\t\t\t\t\t\t\t\t\t"+oaStep.getUsername()+" "+sdf.format(new Date())+"\r\n";
		}
		String dnum = dealDnum(o);
		
		//o.setDnum(dnum);
 
		
		o.setDhqryj(hqyj);
		//o.update();
		
		
		
		
		List<OaSteps> noComplete = OaSteps.dao.find("select  * from oa_steps  where oid='"+o.getId()+"' and step='"+sstep+"' and  ifcomplete='0'");
		if(noComplete.size()==0) {
			OaSteps cos=new OaSteps();
			cos.setId(UuidUtil.getUUID());
			cos.setOid(o.getId());
			cos.setStep("31");
			cos.setSortnum("310");
			cos.setType(stepType);
			cos.setTitle(o.getDtitle());
			SysUser user = SysUser.dao.findById(OaSenddoc.dao.findById(o.getId()).getCuserid());
			SysOrg org = SysOrg.dao.findById(user.getOrgid());
			cos.setUserid(user.getId());
			cos.setUsername(user.getName());
			cos.setOrgid(org.getId());
			cos.setOrgname(org.getName());
			cos.setIfshow("1");
			cos.setIfcomplete("0");
			cos.setCuserid(ShiroKit.getUserId());
			cos.setCusername(ShiroKit.getUserName());
			cos.setCtime(DateUtil.getCurrentTime());
			cos.save();
			
			
		/*	List<OaSteps> oss = OaSteps.dao.find("select  * from oa_steps  where  oid='"+o.getId()+"' and  step='"+sstep+"' and  ifcomplete='1' order by stepdesc ");
			String remarks="";
			for (OaSteps oaStep : oss) {
				remarks=remarks+oaStep.getRemarks()+"\r\n\t\t\t\t\t\t\t\t\t\t"+oaStep.getUsername()+" "+sdf.format(new Date());
			}
			
			o.setDhqryj(remarks);*/
			o.setStatus("4");
			
			
			
		}
		o.update();
		renderSuccess("会签成功!");
	
	}
	/** 
	* @Title: dealDnum 
	* @Description: TODO 
	* @param o
	* @return String
	* @date 2020年12月29日下午12:28:47
	*/ 
	public String dealDnum(OaSenddoc o) {
		String dnumber = o.getDnumber();
		String dsource = o.getDsource();
		
		String dtype = o.getDtype();
		String dnum="";
	 
		if(dtype.equals("1")||dtype.equals("2")) {
			dnum="沪交行"+dsource+o.getDyear()+dnumber+"号";
		}else {
			
			dnum=dsource+o.getDyear()+dnumber;
		}
		return dnum;
	}
	
	/**
	 * 
	* @Title: osSendHg 
	* @Description: 送核稿
	* @date 2020年11月4日下午2:24:03
	 */
	
	public void osSendHg() {

		OaSenddoc o = getModel(OaSenddoc.class);
		String sid = getPara("sid");
		o.setStatus("5");
		o.update();
		
		OaSteps uos = OaSteps.dao.findById(sid);
		uos.setIfcomplete("1");
		uos.setCompletetime(DateUtil.getCurrentTime());
		uos.update();
		
		StepUtil.insertStepHistory(o.getId(), o.getDtitle(), stepType);
		
		String[] hgrids = o.getDhgrids().split(",");
		int i=0;
		for (String hgrid : hgrids) {
			OaSteps os =new OaSteps();
			os.setId(UuidUtil.getUUID());
			os.setOid(o.getId());
			os.setStep("4");
			os.setType(stepType);
			os.setTitle(o.getDtitle());
			os.setTitle(o.getDtitle());
    		os.setSortnum(String.valueOf(400+i));
    		SysUser user = SysUser.dao.findById(hgrid);
    		SysOrg  org = SysOrg.dao.findById(user.getOrgid());
    		os.setUserid(user.getId());
    		os.setUsername(user.getName());
    		os.setOrgid(org.getId());
    		os.setOrgname(org.getName());
    		os.setIfshow("1");
    		os.setIfcomplete("0");
    		os.setCtime(DateUtil.getCurrentTime());
    		os.setCuserid(ShiroKit.getUserId());
    		os.setCusername(ShiroKit.getUserName());
    		os.setStepdesc(user.getSort().toString());
    		os.save();
			i++;
		}
		
		renderSuccess();
	}
	
	/**
	 * 
	* @Title: osHg 
	* @Description: 核稿
	* @date 2020年11月4日下午2:35:39
	 */
	public void osHg() {
		OaSenddoc o = getModel(OaSenddoc.class);
		//o=OaSenddoc.dao.findById(o.getId());
		String sid = getPara("sid");
		String sstep = getPara("sstep");
		String comment = getPara("comment");
		
		OaSteps uos = OaSteps.dao.findById(sid);
		uos.setCompletetime(DateUtil.getCurrentTime());
		uos.setIfcomplete("1");
		uos.setRemarks(comment);
		uos.update();
		
		StepUtil.insertStepHistory(o.getId(),o.getDtitle(),stepType);
		
		
		
		
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
	/*	String hgremark= o.getDhgremarks()==null?"":o.getDhgremarks();
		hgremark=hgremark+"\r\n"+"\r\n\t\t\t\t\t\t\t\t\t\t"+ShiroKit.getUserName()+" "+sdf.format(new Date());
		o.setDhgremarks(hgremark);
		o.update();*/
		
		List<OaSteps> oss = OaSteps.dao.find("select  * from oa_steps  where   oid='"+o.getId()+"' and  step='"+sstep+"' and  ifcomplete='1' order by stepdesc ");
		String remarks="";
		for (OaSteps oaStep : oss) {
			remarks=remarks+"\r\n"+oaStep.getRemarks()+"\r\n\t\t\t\t\t\t"+oaStep.getUsername()+" "+sdf.format(new Date());
		}
		
		o.setDhgremarks(remarks);
		
		
		String dnum = dealDnum(o);
		
		//o.setDnum(dnum);
		
		List<OaSteps> noComplete = OaSteps.dao.find("select  * from oa_steps  where  oid='"+o.getId()+"' and  step='"+sstep+"' and  ifcomplete='0' order by stepdesc ");
		if(noComplete.size()==0) {
			OaSteps cos=new OaSteps();
			cos.setId(UuidUtil.getUUID());
			cos.setOid(o.getId());
			cos.setStep("41");
			cos.setSortnum("410");
			
			cos.setType(stepType);
			cos.setTitle(o.getDtitle());
			SysUser user = SysUser.dao.findById(OaSenddoc.dao.findById(o.getId()).getCuserid());
			SysOrg org = SysOrg.dao.findById(user.getOrgid());
			cos.setUserid(user.getId());
			cos.setUsername(user.getName());
			cos.setOrgid(org.getId());
			cos.setOrgname(org.getName());
			cos.setIfshow("1");
			cos.setIfcomplete("0");
			cos.setCuserid(ShiroKit.getUserId());
			cos.setCusername(ShiroKit.getUserName());
			cos.setCtime(DateUtil.getCurrentTime());
			cos.save();
			
			
		/*	List<OaSteps> oss = OaSteps.dao.find("select  * from oa_steps  where   oid='"+o.getId()+"' and  step='"+sstep+"' and  ifcomplete='1' order by stepdesc ");
			String remarks="";
			for (OaSteps oaStep : oss) {
				remarks=remarks+"\r\n"+oaStep.getRemarks()+"\r\n\t\t\t\t\t\t\t\t\t\t"+oaStep.getUsername()+" "+sdf.format(new Date());
			}
			
			o.setDhgremarks(remarks);*/
			o.setStatus("6");
			//o.update();
		}
		o.update();
		renderSuccess("完成!");
		
	}
	
	/**
	 * 
	* @Title: osSendQf 
	* @Description: 送签发
	* @date 2020年11月4日下午2:51:48
	 */
	public void osSendQf() {
		OaSenddoc o = getModel(OaSenddoc.class);
		String sid = getPara("sid");
		o.setStatus("7");
		o.update();
		
		OaSteps uos = OaSteps.dao.findById(sid);
		uos.setIfcomplete("1");
		uos.setCompletetime(DateUtil.getCurrentTime());
		uos.update();
		StepUtil.insertStepHistory(o.getId(), o.getDtitle(), stepType);
		
		String[] qfldids = o.getDqfldids().split(",");
		int i=0;
		for (String qfldid : qfldids) {
			OaSteps os =new OaSteps();
			os.setId(UuidUtil.getUUID());
			os.setOid(o.getId());
			os.setStep("5");
			os.setType(stepType);
			os.setTitle(o.getDtitle());
    		os.setSortnum(String.valueOf(500+i));
    		SysUser user = SysUser.dao.findById(qfldid);
    		SysOrg  org = SysOrg.dao.findById(user.getOrgid());
    		os.setUserid(user.getId());
    		os.setUsername(user.getName());
    		os.setOrgid(org.getId());
    		os.setOrgname(org.getName());
    		os.setIfshow("1");
    		os.setIfcomplete("0");
    		os.setCtime(DateUtil.getCurrentTime());
    		os.setCuserid(ShiroKit.getUserId());
    		os.setCusername(ShiroKit.getUserName());
    		os.setStepdesc(user.getSort().toString());
    		os.save();
			i++;
		}
		renderSuccess();
	}
	
	/**
	 * 
	* @Title: osQf 
	* @Description:签发
	* @date 2020年11月4日下午3:13:37
	 */
	public  void osQf() {
		SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd");
		OaSenddoc o = getModel(OaSenddoc.class);
		//o=OaSenddoc.dao.findById(o.getId());
		String sid = getPara("sid");
		String sstep = getPara("sstep");
		String comment = getPara("comment");
		
		OaSteps uos = OaSteps.dao.findById(sid);
		uos.setCompletetime(DateUtil.getCurrentTime());
		uos.setIfcomplete("1");
		uos.setRemarks(comment);
		uos.update();
		StepUtil.insertStepHistory(o.getId(),o.getDtitle(), stepType);
		
		/*String qfldremarks= o.getDqfldremarks()==null?"":o.getDqfldremarks();
		qfldremarks=qfldremarks+"\r\n"+ comment+"\r\n\t\t\t\t\t\t\t\t\t\t"+ShiroKit.getUserName()+" "+sdf.format(new Date());
		o.setDqfldremarks(qfldremarks);*/
		//o.update();
		
		
		
		List<OaSteps> oss = OaSteps.dao.find("select  * from oa_steps  where oid='"+o.getId()+"' and  step='"+sstep+"' and  ifcomplete='1' order by stepdesc ");
		String remarks="";
		for (OaSteps oaStep : oss) {
			remarks=remarks+"\r\n"+oaStep.getRemarks()+"\r\n\t\t\t\t\t"+oaStep.getUsername()+" "+sdf.format(new Date());
		}
		
		o.setDqfldremarks(remarks);
		String dnum = dealDnum(o);
		
		//o.setDnum(dnum);
		
		
		List<OaSteps> noComplete = OaSteps.dao.find("select  * from oa_steps  where   oid='"+o.getId()+"' and step='"+sstep+"' and  ifcomplete='0' order by stepdesc ");
		if(noComplete.size()==0) {
			OaSteps cos=new OaSteps();
			cos.setId(UuidUtil.getUUID());
			cos.setOid(o.getId());
			cos.setStep("51");
			cos.setSortnum("510");
			cos.setType(stepType);
			cos.setTitle(o.getDtitle());
			SysUser user = SysUser.dao.findById(OaSenddoc.dao.findById(o.getId()).getCuserid());
			SysOrg org = SysOrg.dao.findById(user.getOrgid());
			cos.setUserid(user.getId());
			cos.setUsername(user.getName());
			cos.setOrgid(org.getId());
			cos.setOrgname(org.getName());
			cos.setIfshow("1");
			cos.setIfcomplete("0");
			cos.setCuserid(ShiroKit.getUserId());
			cos.setCusername(ShiroKit.getUserName());
			cos.setCtime(DateUtil.getCurrentTime());
			cos.save();
			
			
		/*	List<OaSteps> oss = OaSteps.dao.find("select  * from oa_steps  where oid='"+o.getId()+"' and  step='"+sstep+"' and  ifcomplete='1' order by stepdesc ");
			String remarks="";
			for (OaSteps oaStep : oss) {
				remarks=remarks+oaStep.getRemarks()+"\r\n\t\t\t\t\t\t\t\t\t\t"+oaStep.getUsername()+" "+sdf.format(new Date());
			}
			
			o.setDhgremarks(remarks);*/
			o.setStatus("8");
			o.update();
		}
		
		renderSuccess("完成!");
	}
	
	/**
	* @Title: osSendfw 
	* @Description: 送发文
	* @date 2020年11月4日下午3:30:07
	*/
	
	public void osSendfw() {

		OaSenddoc o = getModel(OaSenddoc.class);
		String sid = getPara("sid");
		o.setStatus("9");
		o.update();
		
		OaSteps uos = OaSteps.dao.findById(sid);
		uos.setIfcomplete("1");
		uos.setCompletetime(DateUtil.getCurrentTime());
		uos.update();
		
		StepUtil.insertStepHistory(o.getId(),o.getDtitle(),stepType);
		
		String[] fwids = o.getDfwids().split(",");
		int i=0;
		for (String fwid : fwids) {
			OaSteps os =new OaSteps();
			os.setId(UuidUtil.getUUID());
			os.setOid(o.getId());
			os.setStep("6");
			os.setType(stepType);
			os.setTitle(o.getDtitle());
    		os.setSortnum(String.valueOf(600+i));
    		SysUser user = SysUser.dao.findById(fwid);
    		SysOrg  org = SysOrg.dao.findById(user.getOrgid());
    		os.setUserid(user.getId());
    		os.setUsername(user.getName());
    		os.setOrgid(org.getId());
    		os.setOrgname(org.getName());
    		os.setIfshow("1");
    		os.setIfcomplete("0");
    		os.setCtime(DateUtil.getCurrentTime());
    		os.setCuserid(ShiroKit.getUserId());
    		os.setCusername(ShiroKit.getUserName());
    		os.save();
			i++;
		}
		renderSuccess();
	
	}
	
	
	/**
	 * 
	* @Title: osSendSave 
	* @Description: 发文保存
	* @date 2020年11月4日下午3:55:36
	 */
	public void osSendSave() {


		OaSenddoc o = getModel(OaSenddoc.class);
		//o=OaSenddoc.dao.findById(o.getId());
		String sid = getPara("sid");
		String sstep = getPara("sstep");
		String comment = getPara("comment");
		
		
		OaSteps uos = OaSteps.dao.findById(sid);
		uos.setCompletetime(DateUtil.getCurrentTime());
		uos.setIfcomplete("1");
		uos.setRemarks(comment);
		uos.update();
		
		
		String dnumber = o.getDnumber();
		String dsource = o.getDsource();
		
		String dtype = o.getDtype();
		String dnum="";
		SimpleDateFormat sdf =new SimpleDateFormat("yyyy");
		if(dtype.equals("1")||dtype.equals("2")) {
			dnum="沪交行"+dsource+sdf.format(new Date())+dnumber+"号";
		}else {
			
			dnum=dsource+sdf.format(new Date())+dnumber;
		}
		
		//o.setDnum(dnum);
		
		StepUtil.insertStepHistory(o.getId(),o.getDtitle(), stepType);
		
/*		String qfldremarks= o.getDqfldremarks()==null?"":o.getDqfldremarks();
		qfldremarks=qfldremarks+"\r\n"+ comment+"("+ShiroKit.getUserName()+")";
		o.setDqfldremarks(qfldremarks);
		o.update();
		*/
		
		
		
		List<OaSteps> noComplete = OaSteps.dao.find("select  * from oa_steps  where  oid='"+o.getId()+"' and step='"+sstep+"' and  ifcomplete='0'");
		if(noComplete.size()==0) {
			OaSteps cos=new OaSteps();
			cos.setId(UuidUtil.getUUID());
			cos.setOid(o.getId());
			cos.setStep("61");
			cos.setSortnum("610");
			cos.setTitle(o.getDtitle());
			cos.setType(stepType);
			SysUser user = SysUser.dao.findById(OaSenddoc.dao.findById(o.getId()).getCuserid());
			SysOrg org = SysOrg.dao.findById(user.getOrgid());
			cos.setUserid(user.getId());
			cos.setUsername(user.getName());
			cos.setOrgid(org.getId());
			cos.setOrgname(org.getName());
			cos.setIfshow("1");
			cos.setIfcomplete("0");
			cos.setCuserid(ShiroKit.getUserId());
			cos.setCusername(ShiroKit.getUserName());
			cos.setCtime(DateUtil.getCurrentTime());
			
			
			OaSenddoc findById = OaSenddoc.dao.findById(o.getId());
			if(findById.getStatus().equals("9")) {
			 
				cos.save();
				
				
				/*	List<OaSenddocStep> oss = OaSenddocStep.dao.find("select  * from oa_senddoc_step  where  steps='"+osstep+"' and  ifcomplete='1'");
					String remarks="";
					for (OaSenddocStep oaStep : oss) {
						remarks=remarks+oaStep.getRemarks()+"("+oaStep.getActorsname()+")"+"\r\n";
					}
					
					o.setDhgremarks(remarks);*/
					o.setStatus("10");
			}
			//cos.save();
			
			
		/*	List<OaSenddocStep> oss = OaSenddocStep.dao.find("select  * from oa_senddoc_step  where  steps='"+osstep+"' and  ifcomplete='1'");
			String remarks="";
			for (OaSenddocStep oaStep : oss) {
				remarks=remarks+oaStep.getRemarks()+"("+oaStep.getActorsname()+")"+"\r\n";
			}
			
			o.setDhgremarks(remarks);*/
			//o.setStatus("10");
		
		}
		o.update();
		renderSuccess("完成!");
	
		
	}
	
	/**
	 * 
	* @Title: osSendWy 
	* @Description: 送文印 
	* @date 2020年11月4日下午4:33:41
	 */
	public void osSendWy() {

		OaSenddoc o = getModel(OaSenddoc.class);
		String sid = getPara("sid");
		o.setStatus("11");
		o.update();
		
		OaSteps uos = OaSteps.dao.findById(sid);
		uos.setIfcomplete("1");
		uos.setCompletetime(DateUtil.getCurrentTime());
		uos.update();
		
		StepUtil.insertStepHistory(o.getId(),o.getDtitle(), stepType);
		
		String[] wyids = o.getDwyids().split(",");
		int i=0;
		for (String wyid : wyids) {
			OaSteps os =new OaSteps();
			os.setId(UuidUtil.getUUID());
			os.setOid(o.getId());
			os.setStep("7");
			os.setTitle(o.getDtitle());
			os.setType(stepType);
    		os.setSortnum(String.valueOf(700+i));
    		SysUser user = SysUser.dao.findById(wyid);
    		SysOrg  org = SysOrg.dao.findById(user.getOrgid());
    		os.setUserid(user.getId());
    		os.setUsername(user.getName());
    		os.setOrgid(org.getId());
    		os.setOrgname(org.getName());
    		os.setIfshow("1");
    		os.setIfcomplete("0");
    		os.setCtime(DateUtil.getCurrentTime());
    		os.setCuserid(ShiroKit.getUserId());
    		os.setCusername(ShiroKit.getUserName());
    		os.setSortnum(user.getSort().toString());
    		os.save();
			i++;
		}
		renderSuccess();
	
	
		
	}
	
	/**
	 * 
	* @Title: osWyOver 
	* @Description:文印完成
	* @date 2020年11月4日下午4:44:03
	 */
	public void osWyOver() {

		OaSenddoc o = getModel(OaSenddoc.class);
		String sid = getPara("sid");
		String comment = getPara("comment");
		
		OaSteps uos = OaSteps.dao.findById(sid);
		uos.setCompletetime(DateUtil.getCurrentTime());
		uos.setIfcomplete("1");
		uos.setRemarks(comment);
		uos.update();
		
		
		
		OaSteps cos=new OaSteps();
		cos.setId(UuidUtil.getUUID());
		cos.setOid(o.getId());
		cos.setStep("71");
		cos.setTitle(o.getDtitle());
		cos.setType(stepType);
		cos.setSortnum("710");
		SysUser user = SysUser.dao.findById(OaSenddoc.dao.findById(o.getId()).getCuserid());
		SysOrg  org = SysOrg.dao.findById(user.getOrgid());
		cos.setUserid(user.getId());
		cos.setUsername(user.getName());
		cos.setOrgid(org.getId());
		cos.setOrgname(org.getName());
		cos.setIfshow("1");
		cos.setIfcomplete("0");
		cos.setCuserid(ShiroKit.getUserId());
		cos.setCusername(ShiroKit.getUserName());
		cos.setCtime(DateUtil.getCurrentTime());
		cos.save();
		o.setStatus("12");
		o.update();
		renderSuccess();
	}
	
	/**
	 * 
	* @Title: osOver 
	* @Description: 归档
	* @date 2020年11月4日下午4:58:29
	 */
	public void osOver() {

		OaSenddoc o = getModel(OaSenddoc.class);
		String sid = getPara("sid");
		
		if(sid==null ||sid.equals("")) {

			Db.update("update oa_steps set ifcomplete='1' ,completetime='"+DateUtil.getCurrentTime()+"' where oid ='"+o.getId()+"' and userid='"+ShiroKit.getUserId()+"' and ifcomplete='0'");
		}else {
			String comment = getPara("comment");
			
			OaSteps uos = OaSteps.dao.findById(sid);
			uos.setCompletetime(DateUtil.getCurrentTime());
			uos.setIfcomplete("1");
			uos.setRemarks(comment);
			uos.update();
			
			
		}
		StepUtil.insertStepHistory(o.getId(),o.getDtitle(), stepType);
		
		
		 
		o.setStatus("13");
		o.update();
		renderSuccess();
	
	}
	
	/**
	 * 
	* @Title: osSendInPerson 
	* @Description: 内部发送
	* @date 2020年11月4日下午5:00:09
	 */
	
	public  void osSendInPerson() {

		OaSenddoc o = getModel(OaSenddoc.class);
		String sid = getPara("sid");
		String comment = getPara("comment");
	if(sid==null ||sid.equals("")) {
			
		
		Db.update("update oa_steps set ifcomplete='1' ,completetime='"+DateUtil.getCurrentTime()+"' where oid ='"+o.getId()+"' and userid='"+ShiroKit.getUserId()+"' and ifcomplete='0'");
		
		
		}else {
		OaSteps uos = OaSteps.dao.findById(sid);
		uos.setCompletetime(DateUtil.getCurrentTime());
		uos.setIfcomplete("1");
		uos.setRemarks(comment);
		uos.update();
		}
		StepUtil.insertStepHistory(o.getId(), o.getDtitle(), stepType);
		
		o.setStatus("13");
		o.update();
		String[] nbcyids = o.getDnbcyids().split(",");
		SysAttachment sa = SysAttachment.dao.findFirst("select  * from sys_attachment  where business_id='"+o.getId()+"' and  des='1'");
		List<SysAttachment> attachList = SysAttachment.dao.find("select  * from sys_attachment  where business_id='"+o.getId()+"'");
		for (String nbcyid : nbcyids) {
			
			OaEmail email=new OaEmail();
			email.setId(UuidUtil.getUUID());
			email.setSubject(o.getDtitle());
			email.setTimeflage(DateUtil.getCurrentTime());
			SysUser user = SysUser.dao.findById(nbcyid);
			email.setBcc(user.getName());
			email.setFuserid(ShiroKit.getUserId());
			email.setSuserid(nbcyid);
			email.setOpstatis("0");
			email.save();
			if(sa!=null) {
				
				
				SysAttachment nsa=sa;
				nsa.setId(UuidUtil.getUUID());
				nsa.setBusinessId(email.getId());
				nsa.save();
			}
			  
			  
			 // SysUser user = SysUser.dao.findById(nbcyid);
				OaEmailSon oms=new OaEmailSon();
				oms.setId(UuidUtil.getUUID());
				oms.setOid(email.getId());
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
		renderSuccess();
	}
	
    /***
     * edit page
     */
    public void getEditPage(){
    	String id = getPara("id");
    	String view = getPara("view");
		setAttr("view", view);
		List<SysOrg> orgLists = SysOrg.dao.find("select *  from  sys_org where parent_id='root'  and name !='中心领导' order by sort ");
		
		setAttr("orgLists",orgLists);
		
		List<SysOrg> orgList = SysOrg.dao.find("select id  from  sys_org where  name='中心领导'");
		List<SysUser> userList = SysUser.dao.find("select * from  sys_user where orgid='"+orgList.get(0).getId()+"' order by sort");
		setAttr("userList",userList);
		
		
		
		
		OaSenddoc o = new OaSenddoc();
		if(StrKit.notBlank(id)){
    		o = service.getById(id);
    		if("detail".equals(view)){
    			/*if(StrKit.notBlank(o.getProcInsId())){
    				setAttr("procInsId", o.getProcInsId());
    				setAttr("defId", wfservice.getDefIdByInsId(o.getProcInsId()));
    			}*/
    			
    			
    			SysAttachment sa = SysAttachment.dao.findFirst("select  * from sys_attachment  where business_id='"+id+"' and  des='1'");
    			if(sa!=null) {
    				setAttr("fid", sa.getId());
    				setAttr("uploadperson",sa.getCreateUserName());
    				setAttr("filedName",sa.getFileName());
    			}
    		}
    	}else{
    		/*SysUser user = SysUser.dao.findById(ShiroKit.getUserId());
    		SysOrg org = SysOrg.dao.findById(user.getOrgid());*/
			/*o.setOrgId(org.getId());
			o.setOrgName(org.getName());
			o.setUserid(user.getId());
			o.setApplyerName(user.getName());*/
    		o.setStatus("0");
    		o.setId(UuidUtil.getUUID());
    		SimpleDateFormat sdf=new SimpleDateFormat("yyyy");
    		o.setDyear(sdf.format(new Date()));
    		
    	}
		setAttr("o", o);
    	setAttr("formModelName",StringUtil.toLowerCaseFirstOne(OaSenddoc.class.getSimpleName()));
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
    	OaSenddoc o = OaSenddoc.dao.getById(id);
    	//o.setIfSubmit(Constants.IF_SUBMIT_YES);
		//String insId = wfservice.startProcess(id, o,null,null);
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
    		OaSenddoc o = OaSenddoc.dao.getById(id);
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
  public void  print() {
    	
    	String id= getPara("id");
    	
    	OaSenddoc senddoc = OaSenddoc.dao.findById(id);
    	//setAttr("wh","1233445");
    	setAttr("o",senddoc);
    	//senddoc.getDqfldids();
    	
    	//查询签发
    	List<OaSteps> qfStep = OaSteps.dao.find("select  * from oa_steps where oid='"+senddoc.getId()+"'  and  step='5'  and  ifcomplete='1'");
    	
    	String qfyj=senddoc.getDqfldremarks();
    	
    	String qfY="";
    	String qfM="";
    	String qfD="";
    	String qfsj="";
    	
    	
    	for (OaSteps oaSteps : qfStep) {
    		qfyj=qfyj.replaceAll(oaSteps.getCompletetime().substring(0, 10), "");
    		qfsj=oaSteps.getCompletetime().substring(0, 10);		
		}
    	
    	
    	if(!qfsj.equals("")) {
    		
    		String[] qfArray= qfsj.split("-");
    		qfY=qfArray[0];
    		qfM=qfArray[1];
    		qfD=qfArray[2];
    		
    		
    	}
    	
    	setAttr("qfyj",qfyj);
    	setAttr("qfY",qfY);
    	setAttr("qfM",qfM);
    	setAttr("qfD",qfD);
    	
    	
    	//查询核稿
    	List<OaSteps> hgStep = OaSteps.dao.find("select  * from oa_steps where oid='"+senddoc.getId()+"'  and  step='4'  and  ifcomplete='1'");
    	
     
    	
    	String hgY="";
    	String hgM="";
    	String hgD="";
    	String hgsj="";
    	String hgR="";
    	
    	for (OaSteps oaSteps : hgStep) {
     
    		hgsj=oaSteps.getCompletetime().substring(0, 10);		
    		hgR= oaSteps.getUsername()+","+hgR;
		}
    	
    	if(!hgsj.equals("")) {
    		
    		String[] hgArray= hgsj.split("-");
    		hgY=hgArray[0];
    		hgM=hgArray[1];
    		hgD=hgArray[2];
    	}
    	
    	
    	setAttr("hgR",hgR);
    	setAttr("hgY",hgY);
    	setAttr("hgM",hgM);
    	setAttr("hgD",hgD);
    	
    	
    	
    	//查询会稿
    	List<OaSteps> hqStep = OaSteps.dao.find("select  * from oa_steps where oid='"+senddoc.getId()+"'  and  step='3'  and  ifcomplete='1'");
    	
     
    	
    	String hqY="";
    	String hqM="";
    	String hqD="";
    	String hqsj="";
    	String hqR="";
    	
    	for (OaSteps oaSteps : hqStep) {
     
    		hqsj=oaSteps.getCompletetime().substring(0, 10);	
    		
    		
    		if(hqStep.size()==1) {
    			hqR= oaSteps.getUsername();
    		}else {
    			
    			hqR= oaSteps.getUsername()+","+hqR;
    		}
		}
    	
    	if(!hqsj.equals("")) {
    		
    		String[] hqArray= hqsj.split("-");
    		hqY=hqArray[0];
    		hqM=hqArray[1];
    		hqD=hqArray[2];
    		
    	}
    	
    	setAttr("hqR",hqR);
    	setAttr("hqY",hqY);
    	setAttr("hqM",hqM);
    	setAttr("hqD",hqD);
    	
    	
    	
    	String cuserId = senddoc.getCuserid();
    	SysUser user = SysUser.dao.findById(cuserId);
    	setAttr("cname", user.getName());
    	String ctime = senddoc.getCtime().substring(0, 10);
    	
    	String cY="";
    	String cM="";
    	String cD="";
    	if(ctime!=null&& !ctime.equals("")) {
    		String[] cTimeArray = ctime.split("-");
    		cY=cTimeArray[0];
    		cM=cTimeArray[1];
    		cD=cTimeArray[2];
    		
    	}
    
    	setAttr("cY",cY);
    	setAttr("cM",cM);
    	setAttr("cD",cD);
    	
    	
    	render("senddocprint.html");
    }
	
  
  
  public  void  savedit() {
	  OaSenddoc o = getModel(OaSenddoc.class);
	  
		String dtype = o.getDtype();   
		String dnum="";
		if(dtype.equals("1")||dtype.equals("2")) {
			dnum="沪交行"+o.getDsource()+"("+o.getDyear()+")"+o.getDnumber()+"号";
		}else {
			dnum=o.getDsource()+"("+o.getDyear()+")"+o.getDnumber();
		}
		//o.setDnum(dnum);
	  o.update();
	  renderSuccess();
	  
  }
  
  
  public void openSelectRdoc(){
  	
    	String orgid = getPara("orgid");
  	setAttr("orgid", orgid);
  	renderIframe("selectRdoc.html");
  } 
}