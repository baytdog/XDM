package com.pointlion.mvc.admin.oa.hotline;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.upload.UploadFile;
import com.pointlion.mvc.admin.oa.workflow.WorkFlowService;
import com.pointlion.mvc.common.base.BaseController;
import com.pointlion.mvc.common.model.OaHotline;
import com.pointlion.mvc.common.model.OaHotlineUser;
import com.pointlion.mvc.common.model.OaStepHistory;
import com.pointlion.mvc.common.model.OaSteps;
import com.pointlion.mvc.common.model.SysAttachment;
import com.pointlion.mvc.common.model.SysOrg;
import com.pointlion.mvc.common.model.SysUser;
import com.pointlion.mvc.common.utils.Constants;
import com.pointlion.mvc.common.utils.DateUtil;
import com.pointlion.mvc.common.utils.StepUtil;
import com.pointlion.mvc.common.utils.StringUtil;
import com.pointlion.mvc.common.utils.UuidUtil;
import com.pointlion.mvc.common.utils.office.excel.ExcelUtil;
import com.pointlion.plugin.shiro.ShiroKit;



public class OaHotlineController extends BaseController {
	public static final OaHotlineService service = OaHotlineService.me;
	public static WorkFlowService wfservice = WorkFlowService.me;
	
	public  static final String  stepType="3";
	/***
	 * get list page
	 */
	public void getListPage(){
		//String type = getPara("type");
	    keepPara("type");
		
		renderIframe("list.html");
    }
	/***
	 * get list page
	 */
	public void getVListPage(){
		
		renderIframe("hotlineLetterlist.html");
	}
	public void getListPage1(){
		//String type = getPara("type");
		keepPara("type");
		
		renderIframe("list2.html");
	}
	public void getPHotLineListPage(){
		keepPara("type");
		
		renderIframe("pHotLineList.html");
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
		String hnum = getPara("hnum","");
		String hfromnum = getPara("hfromnum","");
		String hfromer = java.net.URLDecoder.decode(getPara("hfromer",""),"UTF-8");
		
		String hstate = getPara("hstate","");
    	Page<Record> page = service.getPageBySearch(Integer.valueOf(curr),Integer.valueOf(pageSize),startTime,endTime,hnum,hfromer,hfromnum,hstate);
    	renderPage(page.getList(),"",page.getTotalRow());
    }
    /**
     * 
    * @Title: listVData 
    * @Description: 信访热线查询
    * @date 2020年12月1日下午5:19:41
     */
    public void listVData(){
    	
    	String curr = getPara("pageNumber");
    	String pageSize = getPara("pageSize");
    	String endTime = getPara("endTime","");
    	String startTime = getPara("startTime","");
    	String applyUser = getPara("applyUser","");
    	String num=getPara("num","");
    	String selectType=getPara("selectType","");
    	Page<Record> page = service.getPage(Integer.valueOf(curr),Integer.valueOf(pageSize),startTime,endTime,applyUser,num,selectType);
    	renderPage(page.getList(),"",page.getTotalRow());
    }
    public void listData1(){
    	String curr = getPara("pageNumber");
    	String pageSize = getPara("pageSize");
    	Page<Record> page = service.getPage(Integer.valueOf(curr),Integer.valueOf(pageSize));
    	renderPage(page.getList(),"",page.getTotalRow());
    }
    public void pHotLineListData(){
    	String curr = getPara("pageNumber");
    	String pageSize = getPara("pageSize");
    	Page<Record> page = service.getPHotLineListPage(Integer.valueOf(curr),Integer.valueOf(pageSize));
    	renderPage(page.getList(),"",page.getTotalRow());
    }
    /***
     * save data
     */
    public void save(){
    	OaHotline o = getModel(OaHotline.class);
    	if(StrKit.notBlank(o.getId())){
    		o.update();
    	}else{
    		o.setId(UuidUtil.getUUID());
    		//o.setCreateTime(DateUtil.getCurrentTime());
    		o.setCuserid(ShiroKit.getUserId());
    		o.setCtime(DateUtil.getCurrentTime());
    		o.setCusername(ShiroKit.getUsername());
    		o.setStatus("0");
    		o.save();
    		saveJoinUsers(o,o.getLeadersid(),o.getDepartsid());
    	}
    	renderSuccess(o, "");
    }
    
    /**
     * 
    * @Title: saveJoinUsers 
    * @Description: TODO  void
    * @date 2020年9月21日下午3:44:39
     */
    
    public  void saveJoinUsers(OaHotline o,String leaderids,String departmentsid) {
    	int sortnum=1;
    	OaHotlineUser hotUser =new OaHotlineUser();
    	hotUser.setId(UuidUtil.getUUID());
    	hotUser.setHotlinid(o.getId());
    	hotUser.setUserid(ShiroKit.getUserId());
    	hotUser.setUsername(ShiroKit.getUsername());
    	hotUser.setOrgid(ShiroKit.getUserOrgId());
    	hotUser.setOrgname(ShiroKit.getUserOrgName()); 	
    	hotUser.setLookornot("1");
    	hotUser.setSortnum(String.valueOf(sortnum));
    	hotUser.setLooked("1");
    	hotUser.setIfshow("1");
    	hotUser.setIfcomplete("1");
    	hotUser.setShowqytextarea("0");
    
    	hotUser.setBackup1(ShiroKit.getUserName());
    	if(o.getType().equals("1")) {
    		hotUser.setButtondesc("热线登记");
    		hotUser.setStepdesc("热线登记");
    	}else {
    		hotUser.setButtondesc("信访登记");
    		hotUser.setStepdesc("信访登记");
    	}
    	hotUser.save();
    	sortnum++;
    	
    	String[] leadersid = leaderids==null?null:leaderids.split(",");

    	if(leadersid!=null) {
    		
	    	for (String leaderid : leadersid) {
	    		SysUser leader = SysUser.dao.findById(leaderid);
	    		OaHotlineUser leaderUser =new OaHotlineUser();
	    		leaderUser.setId(UuidUtil.getUUID());
	    		leaderUser.setHotlinid(o.getId());
	        	leaderUser.setUserid(leader.getId());
	        	leaderUser.setUsername(leader.getUsername());
	        	leaderUser.setOrgid(leader.getOrgid());
	        	//leaderUser.setOrgname(leader.ge); 	
	        	leaderUser.setLookornot("0");
	        	leaderUser.setSortnum(String.valueOf(sortnum));
	        	leaderUser.setLooked("0");
	        	leaderUser.setIfshow("1");
	        	leaderUser.setIfcomplete("0");
	        	leaderUser.setShowqytextarea("1");
	        	leaderUser.setButtondesc("阅示");
	        	leaderUser.setButtontype("1");
	        	leaderUser.setBackup1(leader.getName());
	        	leaderUser.setStepdesc(leader.getName()+"阅示");
	        	leaderUser.save();
			}
	    	sortnum++;
    	}
    	
    	String[] departmentsids = departmentsid==null?null:departmentsid.split(",");
    	if(departmentsids!=null) {
	    	for (String departmentid : departmentsids) {
	    		SysUser findFirst = SysUser.dao.findFirst("select  * from sys_user where orgid='"+departmentid+"' and position='7'");
	    		OaHotlineUser leaderUser =new OaHotlineUser();
	    		leaderUser.setId(UuidUtil.getUUID());
	    		leaderUser.setHotlinid(o.getId());
	        	leaderUser.setUserid(findFirst.getId());
	        	leaderUser.setUsername(findFirst.getUsername());
	        	leaderUser.setOrgid(findFirst.getOrgid());
	        	//leaderUser.setOrgname(leader.ge); 	
	        	leaderUser.setLookornot("0");
	        	leaderUser.setSortnum(String.valueOf(sortnum));
	        	leaderUser.setLooked("0");
	        	leaderUser.setIfshow(leadersid==null?"1":"0");
	        	leaderUser.setIfcomplete("0");
	        	leaderUser.setShowqytextarea("0");
	        	leaderUser.setButtontype("2");
	        	leaderUser.setStepdesc(findFirst.getName()+"分派任务");
	        	leaderUser.setButtondesc("分派任务");
	        	leaderUser.setBackup1(findFirst.getName());
	        	leaderUser.save();
			}
    	}	
    }
    
    
    
    
    
    
    
    
    
    /***
     * edit page
     */
    public void getEditPage(){
    	
    	
    	setAttr("currentUserId", ShiroKit.getUserId());
    	
    	String id = getPara("id");
    	String view = getPara("view");
    	String type = getPara("type");
		setAttr("view", view);
		setAttr("type", type);
		
		List<SysOrg> orgList = SysOrg.dao.find("select id  from  sys_org where  name='中心领导'");
		List<SysUser> userList = SysUser.dao.find("select * from  sys_user where orgid='"+orgList.get(0).getId()+"'");
		setAttr("userList",userList);
		List<SysOrg> orgLists = SysOrg.dao.find("select *  from  sys_org where parent_id='root'  and name !='中心领导' order by sort ");
		setAttr("orgLists",orgLists);
	   // keepPara("type");
		List<SysAttachment> attachmentsList = SysAttachment.dao.find("select  * from sys_attachment  where business_id='"+id+"'");
		System.out.println(attachmentsList.size());
		setAttr("attachments",attachmentsList.size());
		
		
		List<OaHotlineUser> syLlist = 
				OaHotlineUser.dao.find("select  * from  oa_hotline_user  where hotlinid='"+id+"' and showqytextarea='1' order by sortnum,readtime");
		setAttr("syLlist",syLlist);
		List<OaHotlineUser> stepList = 
				OaHotlineUser.dao.find("select  * from  oa_hotline_user  where hotlinid='"+id+"' order by sortnum,case when readtime is null then 1 else 0 end, readtime");
		setAttr("stepList",stepList);
		
	    List<OaHotline> completeLists = OaHotline.dao.find("select  * from  oa_hotline_user where  hotlinid='"+id+"' and  ifcomplete='1'"); 
	    List<OaHotline> findiscomplete = OaHotline.dao.find("select  * from oa_hotline_user  where hotlinid='"+id+"' and  ifcomplete='0'"); 
		
	    setAttr("completeCount", findiscomplete.size()==0?completeLists.size():completeLists.size()-1);
		
		
		
		OaHotline o = new OaHotline();
		if(StrKit.notBlank(id)){
    		o = service.getById(id);
    		if("detail".equals(view)){
    			/*if(StrKit.notBlank(o.getProcInsId())){
    				setAttr("procInsId", o.getProcInsId());
    				setAttr("defId", wfservice.getDefIdByInsId(o.getProcInsId()));
    			}*/
    		}
    	}else{
    		/*SysUser user = SysUser.dao.findById(ShiroKit.getUserId());
    		SysOrg org = SysOrg.dao.findById(user.getOrgid());*/
		/*	o.setOrgId(org.getId());
			o.setOrgName(org.getName());
			o.setUserid(user.getId());
			o.setApplyerName(user.getName());*/
    		
    		o.setStatus("0");
    		o.setId(UuidUtil.getUUID());
    		setAttr("currentUserId", ShiroKit.getUserId());
    	}
		setAttr("o", o);
    	setAttr("formModelName",StringUtil.toLowerCaseFirstOne(OaHotline.class.getSimpleName()));
		renderIframe("edit.html");
    }
    /***
     * edit page
     */
    public void getEditPage1(){
    	String id = getPara("id");
    	String view = getPara("view");
    	String type = getPara("type");
    	setAttr("view", "detail");
    	setAttr("type", type);
    	
    	
    	List<SysUser> commonusers = SysUser.dao.find("select  * from  sys_user  where  orgid='"+ShiroKit.getUserOrgId()+"' and  POSITION='9' ");
		
		setAttr("commonusers", commonusers);

		SysUser nowUser = SysUser.dao.findById(ShiroKit.getUserId());
		
		List<SysAttachment> attachmentsList = SysAttachment.dao.find("select  * from sys_attachment  where business_id='"+id+"'");
		System.out.println(attachmentsList.size());
		setAttr("attachments",attachmentsList.size());
		
		/*if(nowUser.getPosition().equals("9")) {
			setAttr("ifneedadd", false);
		}else {
			setAttr("ifneedadd", true);
		}*/
		if(nowUser.getPosition().equals("7")) {
			setAttr("ifneedadd", true);
		}else {
			setAttr("ifneedadd", false);
		}
/*		if(nowUser.getPosition().equals("7")) {
			setAttr("ifneedadd", true);
		}else {
			setAttr("ifneedadd", false);
		}
*/		
		
	     OaHotlineUser hotUser = OaHotlineUser.dao.findFirst("select  * from  oa_hotline_user where hotlinid='"+id+"' and userid='"+ShiroKit.getUserId()+"'  and  ifcomplete='0'");
	     setAttr("hotUser", hotUser);
    	
/*    	List<SysOrg> orgList = SysOrg.dao.find("select id  from  sys_org where  name='中心领导'");
    	List<SysUser> userList = SysUser.dao.find("select * from  sys_user where orgid='"+orgList.get(0).getId()+"'");
    	setAttr("userList",userList);
    	List<SysOrg> orgLists = SysOrg.dao.find("select *  from  sys_org where parent_id='root'  and name !='中心领导' order by sort ");
    	setAttr("orgLists",orgLists);*/
    	// keepPara("type");
    	OaHotline o = new OaHotline();
    	if(StrKit.notBlank(id)){
    		o = service.getById(id);
    		
    		 
			setAttr("o", o);
			
			
			List<OaHotlineUser> syLlist = 
					OaHotlineUser.dao.find("select  * from  oa_hotline_user  where hotlinid='"+id+"' and showqytextarea='1' order by sortnum,readtime");
			setAttr("syLlist",syLlist);
			List<OaHotlineUser> stepList = 
					OaHotlineUser.dao.find("select  * from  oa_hotline_user  where hotlinid='"+id+"' order by sortnum,case when readtime is null then 1 else 0 end, readtime");
			setAttr("stepList",stepList);
			
 	    List<OaHotline> completeLists = OaHotline.dao.find("select  * from  oa_hotline_user where  hotlinid='"+id+"' and  ifcomplete='1'"); 
 	    List<OaHotline> findiscomplete = OaHotline.dao.find("select  * from oa_hotline_user  where hotlinid='"+id+"' and  ifcomplete='0'"); 
    		
		    setAttr("completeCount", findiscomplete.size()==0?completeLists.size():completeLists.size()-1);
    		if("detail".equals(view)){
    			/*if(StrKit.notBlank(o.getProcInsId())){
    				setAttr("procInsId", o.getProcInsId());
    				setAttr("defId", wfservice.getDefIdByInsId(o.getProcInsId()));
    			}*/
    		}
    	}else{
    		/*SysUser user = SysUser.dao.findById(ShiroKit.getUserId());
    		SysOrg org = SysOrg.dao.findById(user.getOrgid());*/
    		/*	o.setOrgId(org.getId());
			o.setOrgName(org.getName());
			o.setUserid(user.getId());
			o.setApplyerName(user.getName());*/
    	}
    	setAttr("o", o);
    	setAttr("formModelName",StringUtil.toLowerCaseFirstOne(OaHotline.class.getSimpleName()));
    	renderIframe("edit2.html");
    }
    /**
     * 
    * @Title: getPHotLineForm 
    * @Description: 热线个人任务页面跳转
    * @date 2020年11月6日下午2:02:29
     */
    
    public void getPHotLineForm(){
    	String id = getPara("id");
    	String view = getPara("view");
    	String type = getPara("type");
    	setAttr("view", view);
    	setAttr("type", type);
    	keepPara("sid");
    	keepPara("sstep");
     
    	String sqlUser ="select  * from  sys_user  where  orgid='"+ShiroKit.getUserOrgId()+"'";
    	String adminUser = Constants.ADMIN_USER;
    	String[] adminUsers = adminUser.split(",");
    	for (String adminuser : adminUsers) {
			
    	sqlUser=sqlUser+"and username!='"+adminuser+"'";
    		
		}
    	
    	
    	sqlUser=sqlUser+"order by sort";
    	List<SysUser> commonusers = SysUser.dao.find(sqlUser);
    	
    	
    	setAttr("commonusers", commonusers);
    	
    	setAttr("currentUserId", ShiroKit.getUserId());
    	List<SysAttachment> attachmentsList = SysAttachment.dao.find("select  * from sys_attachment  where business_id='"+id+"'");
    	setAttr("attachments",attachmentsList.size());
    	
    	setAttr("usersname", ShiroKit.getUsername());
    
    	
    	
    	/*OaHotlineUser hotUser = OaHotlineUser.dao.findFirst("select  * from  oa_hotline_user where hotlinid='"+id+"' and userid='"+ShiroKit.getUserId()+"'  and  ifcomplete='0'");
    	setAttr("hotUser", hotUser);
    	*/
    	/*    	List<SysOrg> orgList = SysOrg.dao.find("select id  from  sys_org where  name='中心领导'");
    	List<SysUser> userList = SysUser.dao.find("select * from  sys_user where orgid='"+orgList.get(0).getId()+"'");
    	setAttr("userList",userList);
    	List<SysOrg> orgLists = SysOrg.dao.find("select *  from  sys_org where parent_id='root'  and name !='中心领导' order by sort ");
    	setAttr("orgLists",orgLists);*/
    	// keepPara("type");
    	OaHotline o = new OaHotline();
		o = service.getById(id);
	 
		
		if(!o.getCuserid().equals(ShiroKit.getUserId())) {
			setAttr("showCommont", true);
		}else {
			setAttr("showCommont", false);
		}
		
		if(o.getStatus().equals("3")||o.getStatus().equals("4")) {
    		setAttr("ifneedadd", true);
    	}else {
    		setAttr("ifneedadd", false);
    	}
		
		
		
		setAttr("o", o);
		
		
		
		SysUser user = SysUser.dao.findById(ShiroKit.getUserId());
		boolean isLeader=false;
		if(user.getPosition().equals("2")||user.getPosition().equals("6")) {
			isLeader=true;
		}
		
		setAttr("isLeader", isLeader);
		
		
		
		
		
		
		/*List<OaHotlineUser> syLlist = 
				OaHotlineUser.dao.find("select  * from  oa_hotline_user  where hotlinid='"+id+"' and showqytextarea='1' order by sortnum,readtime");
		setAttr("syLlist",syLlist);
		List<OaHotlineUser> stepList = 
				OaHotlineUser.dao.find("select  * from  oa_hotline_user  where hotlinid='"+id+"' order by sortnum,case when readtime is null then 1 else 0 end, readtime");
		setAttr("stepList",stepList);
		
		List<OaHotline> completeLists = OaHotline.dao.find("select  * from  oa_hotline_user where  hotlinid='"+id+"' and  ifcomplete='1'"); 
		List<OaHotline> findiscomplete = OaHotline.dao.find("select  * from oa_hotline_user  where hotlinid='"+id+"' and  ifcomplete='0'"); 
		
		setAttr("completeCount", findiscomplete.size()==0?completeLists.size():completeLists.size()-1);*/
    	setAttr("o", o);
    	setAttr("formModelName",StringUtil.toLowerCaseFirstOne(OaHotline.class.getSimpleName()));
    	renderIframe("pHotLine.html");
    }
    /**
     * 
    * @Title: doys 
    * @Description: 阅示操作
    * @date 2020年9月22日上午11:40:45
     */
    public void  doys() {
    	String hotlineid =getPara("hotlineid");
    	String comment = getPara("comment").replaceAll("'", "");
    	
    	Db.update("update oa_hotline_user set ifcomplete='1',leaderremark='"+comment+"',readtime='"+DateUtil.getCurrentTime()+"' where hotlinid='"+hotlineid+"' and userid='"+ShiroKit.getUserId()+"'");
    	
        OaHotlineUser findFirst = OaHotlineUser.dao.findFirst("select *  from oa_hotline_user  where  hotlinid='"+hotlineid+"' and userid='"+ShiroKit.getUserId()+"' and ifcomplete='1'");
        
        List<OaHotlineUser> find = OaHotlineUser.dao.find("select  * from  oa_hotline_user  where  hotlinid='"+hotlineid+"' and sortnum='"+findFirst.getSortnum()+"' and ifcomplete='0'");
        
    	if(find.size()==0) {
    		Db.update("update oa_hotline_user set ifshow='1' where hotlinid='"+hotlineid+"' and  sortnum='"+(Integer.valueOf(findFirst.getSortnum())+1)+"'");
    	}
    	renderSuccess();
    }
    
    
    public void  addOpPerson() {
    	String hotlineid = getPara("hotlineid");
    	String commonuserid = getPara("commonuserid");
    	Db.update("update oa_hotline_user set ifcomplete='1',readtime='"+DateUtil.getCurrentTime()+"' where hotlinid='"+hotlineid+"' and userid='"+ShiroKit.getUserId()+"'");
    	OaHotlineUser findFirst = OaHotlineUser.dao.findFirst("select * from  oa_hotline_user where ifcomplete='1'  and hotlinid='"+hotlineid+"' and userid='"+ShiroKit.getUserId()+"'");
    	SysUser addUser = SysUser.dao.findById(commonuserid);
    	OaHotlineUser hotUser=new OaHotlineUser();
    	hotUser.setId(UuidUtil.getUUID());
    	hotUser.setHotlinid(hotlineid);
    	hotUser.setUserid(commonuserid);
    	hotUser.setUsername(addUser.getUsername());
    	hotUser.setOrgid(addUser.getOrgid());
    	//hotUser.setOrgname(addUser.geto);
    	int sortnum=Integer.valueOf(findFirst.getSortnum())+1;
    	hotUser.setSortnum(String.valueOf(sortnum));
    	hotUser.setIfshow("1");
    	hotUser.setIfcomplete("0");
    	hotUser.setShowqytextarea("0");
    	hotUser.setButtondesc("提交领导确认");
    	hotUser.setButtontype("3");
    	hotUser.setStepdesc(addUser.getName()+"填写处理情况");
    	hotUser.setBackup1(addUser.getName());
    	hotUser.save();
    	renderSuccess();
    }
    /**
     * 
    * @Title: toUpperSure 
    * @Description: 给部门领导确认
    * @date 2020年9月22日下午5:23:07
     */
    
    public void toUpperSure() {
    	String hotlineid = getPara("hotlineid");
    	String doDeal = getPara("doDeal");
    	
    	Db.update("update oa_hotline set recontents='"+doDeal+"' where  id='"+hotlineid+"'");
    	
    	Db.update("update oa_hotline_user set readtime='"+DateUtil.getCurrentTime()+"' where hotlinid='"+hotlineid+"' and userid='"+ShiroKit.getUserId()+"'");
    	
    	OaHotlineUser findFirst = OaHotlineUser.dao.findFirst("select * from  oa_hotline_user where   hotlinid='"+hotlineid+"' and userid='"+ShiroKit.getUserId()+"'");
    	
    	int sortnum=Integer.valueOf(findFirst.getSortnum())+1;
    	
    	OaHotlineUser hotUser=new OaHotlineUser();
    	hotUser.setId(UuidUtil.getUUID());
    	hotUser.setHotlinid(hotlineid);
    	
    	SysUser upperLeader = SysUser.dao.findFirst("select  * from sys_user where  orgid='"+ShiroKit.getUserOrgId()+"' and position='7'");
    	
    	
    	hotUser.setUserid(upperLeader.getId());
    	hotUser.setUsername(upperLeader.getUsername());
    	hotUser.setOrgid(upperLeader.getOrgid());
    	hotUser.setSortnum(String.valueOf(sortnum));
    	hotUser.setIfshow("1");
    	hotUser.setIfcomplete("0");
    	hotUser.setShowqytextarea("0");
    	hotUser.setButtondesc("确认处理意见");
    	hotUser.setButtontype("4");
    	hotUser.setStepdesc(upperLeader.getName()+"确认处理意见");
    	hotUser.setBackup1(upperLeader.getName());
    	hotUser.save();
    	renderSuccess();
    }
    
    /**
     * 
    * @Title: departLeaderSure 
    * @Description: 部门领导确认
    * @date 2020年9月22日下午5:45:27
     */
    
    public void departLeaderSure() {
    	
    	
    	String hotlineid = getPara("hotlineid");
    	
    	Db.update("update oa_hotline_user set ifcomplete='1' ,readtime='"+DateUtil.getCurrentTime()+"'  where  hotlinid='"+hotlineid+"' and userid='"+ShiroKit.getUserId()+"' and ifcomplete='0'");
    	
    	Db.update("update oa_hotline_user set ifcomplete='1'   where  hotlinid='"+hotlineid+"' and orgid='"+ShiroKit.getUserOrgId()+"' and ifcomplete='0'");

    	renderSuccess();
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
    	OaHotline o = OaHotline.dao.getById(id);
    //	o.setIfSubmit(Constants.IF_SUBMIT_YES);
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
    		OaHotline o = OaHotline.dao.getById(id);
        /*	wfservice.callBack(o.getProcInsId());
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
    	
    	OaHotline hotline = OaHotline.dao.findById(id);
    	setAttr("wh","1233445");
    	setAttr("o",hotline);
    	render("hotlineprint.html");
    }
    
    
	/** 
	* @Title: insertStepHistory 
	* @Description: 插入步骤历史 
	* @param o void
	* @date 2020年11月6日上午11:16:39
	*/ 
	public void insertStepHistory(OaHotline o) {
		
		Db.delete("delete  from oa_step_history where actorid='"+ShiroKit.getUserId()+"' and oid='"+o.getId()+"'");
		
		
		OaStepHistory cos=new OaStepHistory();
		cos.setId(UuidUtil.getUUID());
		cos.setOid(o.getId());
		cos.setTitle(o.getFromnum());
		cos.setActorid(ShiroKit.getUserId());
		cos.setActorname(ShiroKit.getUserName());
		cos.setActtime(DateUtil.getCurrentTime());
		cos.setType("3");
		cos.setCtime(DateUtil.getCurrentTime());
		cos.setCuser(ShiroKit.getUserName());
		cos.save();
	}
    
    
    /**
    * @Title: stepOne 
    * @Description: 送拟办审核
    * @date 2020年11月6日上午10:23:22
    */
    
	public void stepOne() {
		
		
		
		OaHotline o = getModel(OaHotline.class);
		
		OaHotline dob = OaHotline.dao.findById(o.getId());
		if(dob==null) {
			o.setCuserid(ShiroKit.getUserId());
			o.setCtime(DateUtil.getCurrentTime());
			o.setCusername(ShiroKit.getUsername());
			o.setStatus("1");
			o.setSyts(o.getClqx());
			o.setBackup1("0");
			o.save();
			StepUtil.insertStepHistory(o.getId(),o.getBackup3(), stepType);
			OaSteps cou = new OaSteps();
			cou.setId(UuidUtil.getUUID());
			cou.setOid(o.getId());
			cou.setStep("1");
			cou.setType(stepType);
			cou.setTitle(o.getBackup3());
			cou.setSortnum("100");
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
		}else {
			Db.update("update  oa_steps  set ifcomplete ='1' where oid='"+o.getId()+"'  and  userid='"+ShiroKit.getUserId()+"' and  ifcomplete='0'");
			o.setStatus("1");
			StepUtil.insertStepHistory(o.getId(),o.getBackup3(), stepType);
			o.update();
		}
		
		//o.setId(UuidUtil.getUUID());

		

		//insertStepHistory(o);
		

		String[] nbshrids = o.getNbshrids().split(",");
		int i = 0;
		for (String nbshrid : nbshrids) {
			
			
			OaSteps nbrStep = new OaSteps();
			nbrStep.setId(UuidUtil.getUUID());
			nbrStep.setOid(o.getId());
			nbrStep.setStep("2");
			nbrStep.setType(stepType);
			nbrStep.setTitle(o.getBackup3());
			nbrStep.setSortnum(String.valueOf(200 + i));
			SysUser user = SysUser.dao.findById(nbshrid);
			SysOrg org = SysOrg.dao.findByIds(user.getOrgid());
			nbrStep.setUserid(user.getId());
			//cou.setUsername(user.getName());
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
			i++;

		}
    	renderSuccess(o, "");
	}
	/**
	 * 
	* @Title: sendReg 
	* @Description:拟办审核送登记人
	* @date 2020年11月6日下午2:40:03
	 */
    public  void  sendReg() {
    	OaHotline o = getModel(OaHotline.class);
    	o=OaHotline.dao.findById(o.getId());
    	String comment = getPara("comment");
    	String sstep = getPara("sstep");
    	
      	String sid = getPara("sid");
    	OaSteps uHotLineUser = OaSteps.dao.findById(sid);
    	uHotLineUser.setIfcomplete("1");
    	uHotLineUser.setRemarks(comment);
    	uHotLineUser.setCompletetime(DateUtil.getCurrentTime());
    	uHotLineUser.update();
    	
    	StepUtil.insertStepHistory(o.getId(),o.getBackup3(),stepType);
    	
    	List<OaSteps> uCompleteList = OaSteps.dao.find("select  * from  oa_steps where oid='"+o.getId()+"' and  step like '"+sstep+"%' and ifcomplete='0'");
    	if(uCompleteList.size()==0) {
    		o.setStatus("2");
    		OaSteps cHotlineUser=new OaSteps();
    		cHotlineUser.setId(UuidUtil.getUUID());
    		cHotlineUser.setOid(o.getId());
    		cHotlineUser.setStep("3");
    		cHotlineUser.setType(stepType);
    		cHotlineUser.setTitle(o.getBackup3());
    		SysUser user = SysUser.dao.findById(o.getCuserid());
    		SysOrg org = SysOrg.dao.findById(user.getOrgid());
    		cHotlineUser.setUserid(user.getId());
    		cHotlineUser.setUsername(user.getName());
    		cHotlineUser.setOrgid(org.getId());
    		cHotlineUser.setOrgname(org.getName());
    		cHotlineUser.setSortnum("300");
    		cHotlineUser.setIfshow("1");
    		cHotlineUser.setIfcomplete("0");
    		cHotlineUser.setCtime(DateUtil.getCurrentTime());
    		cHotlineUser.setCuserid(ShiroKit.getUserId());
    		cHotlineUser.setCusername(ShiroKit.getUserName());
    		cHotlineUser.save();
    	}
    	SimpleDateFormat  sdf =new SimpleDateFormat("yyyy-MM-dd");
    	 
    
    	String remark=(o.getNbshrremark()==null?"":o.getNbshrremark())+"\r\n"+comment+"\r\n\t\t\t\t\t\t\t\t\t\t"+ShiroKit.getUserName()+" "+sdf.format(new Date());
    	o.setNbshrremark(remark);
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
    	OaHotline o = getModel(OaHotline.class);
    	o.setCbrremark("");
    	o.setStatus("3");
    	o.update();
    	//o=OaHotline.dao.findById(o.getId());
    	//String hstep = getPara("hstep");
     	String sid = getPara("sid");
     	OaSteps uOHotLineUser = OaSteps.dao.findById(sid);
     	uOHotLineUser.setIfcomplete("1");
     	uOHotLineUser.setCompletetime(DateUtil.getCurrentTime());
     	uOHotLineUser.update();
     	StepUtil.insertStepHistory(o.getId(),o.getBackup3(), stepType);
     	
     	String[] cbrids = o.getCbrids().split(",");
     	for (String cbrid : cbrids) {
     		OaSteps cHotlineUser =new OaSteps();
     		cHotlineUser.setId(UuidUtil.getUUID());
     		cHotlineUser.setOid(o.getId());
     		cHotlineUser.setStep("4");
     		cHotlineUser.setType(stepType);
     		cHotlineUser.setTitle(o.getBackup3());
     		cHotlineUser.setSortnum("400");
     		SysUser user = SysUser.dao.findById(cbrid);
     		SysOrg org = SysOrg.dao.findById(user.getOrgid());
     		cHotlineUser.setUserid(user.getId());
     		cHotlineUser.setUsername(user.getName());
     		cHotlineUser.setOrgid(org.getId());
     		cHotlineUser.setOrgname(org.getName());
     		cHotlineUser.setIfshow("1");
     		cHotlineUser.setIfcomplete("0");
     		cHotlineUser.setCtime(DateUtil.getCurrentTime());
     		cHotlineUser.setCuserid(ShiroKit.getUserId());
     		cHotlineUser.setCusername(ShiroKit.getUserName());
     		cHotlineUser.save();
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

    	OaHotline o = getModel(OaHotline.class);
    	//o=OaHotline.dao.findById(o.getId());
    	String comment = getPara("comment");
    	String sstep = getPara("sstep");
    	
    	String commonuserid = getPara("commonuserid");
    	
      	String hotlineId = getPara("sid");
    	OaSteps uHotLineUser = OaSteps.dao.findById(hotlineId);
    	uHotLineUser.setIfcomplete("1");
    	uHotLineUser.setRemarks(comment);
    	uHotLineUser.setCompletetime(DateUtil.getCurrentTime());
    	uHotLineUser.update();
    	StepUtil.insertStepHistory(o.getId(),o.getBackup3(),stepType);
    	
    	List<OaSteps> uCompleteList = OaSteps.dao.find("select  * from  oa_steps where oid='"+o.getId()+"' and  step like '"+sstep+"%' and ifcomplete='0'");
    	if(uCompleteList.size()==0) {
    		o.setStatus("4");
    		OaSteps cHotlineUser=new OaSteps();
    		cHotlineUser.setId(UuidUtil.getUUID());
    		cHotlineUser.setOid(o.getId());
    		cHotlineUser.setStep("5");
    		cHotlineUser.setType(stepType);
    		cHotlineUser.setTitle(o.getBackup3());
    		SysUser user = SysUser.dao.findById(commonuserid);
    		SysOrg org = SysOrg.dao.findById(user.getOrgid());
    		cHotlineUser.setUserid(user.getId());
    		cHotlineUser.setUsername(user.getName());
    		cHotlineUser.setOrgid(org.getId());
    		cHotlineUser.setOrgname(org.getName());
    		cHotlineUser.setSortnum("500");
    		cHotlineUser.setIfshow("1");
    		cHotlineUser.setIfcomplete("0");
    		cHotlineUser.setCtime(DateUtil.getCurrentTime());
    		cHotlineUser.setCuserid(ShiroKit.getUserId());
    		cHotlineUser.setCusername(ShiroKit.getUserName());
    		cHotlineUser.save();
    	}
    	//String remark=(o.getCbrremark()==null?"":o.getCbrremark())+"\r\n"+comment+"("+ShiroKit.getUserName()+")";
    	OaHotline findById = OaHotline.dao.findById(o.getId());
		String remarks=findById.getCbrremark()==null?"":findById.getCbrremark()+"\r\n"+comment+"\r\n\t\t\t\t\t\t\t\t"+ShiroKit.getUserName()+" "+DateUtil.getCurrentYMD();
    	o.setCbrremark(remarks);
    	o.update();
    	
    	renderSuccess();
    
    	
    }
    
    //部门领导审核办结
    
   public  void dleaderOver() {
   	OaHotline o = getModel(OaHotline.class);
 	String comment = getPara("comment");
   	OaHotline findById = OaHotline.dao.findById(o.getId());
	String remarks=findById.getCbrremark()==null?"":findById.getCbrremark()+"\r\n"+comment+"\r\n\t\t\t\t\t\t\t\t"+ShiroKit.getUserName()+" "+DateUtil.getCurrentYMD();
	o.setCbrremark(remarks);
   	o.setStatus("5");
   	o.update();
   	o=OaHotline.dao.findById(o.getId());
  
 	
  	String sid = getPara("sid");
	OaSteps uHotLineUser = OaSteps.dao.findById(sid);
	uHotLineUser.setIfcomplete("1");
	uHotLineUser.setRemarks(comment);
	uHotLineUser.setCompletetime(DateUtil.getCurrentTime());
	uHotLineUser.update();
	
	StepUtil.insertStepHistory(o.getId(),o.getBackup3(), stepType);
	
	OaSteps cHotlineUser=new OaSteps();
	
	cHotlineUser.setId(UuidUtil.getUUID());
	cHotlineUser.setOid(o.getId());
	cHotlineUser.setStep("6");
	cHotlineUser.setTitle(o.getBackup3());
	cHotlineUser.setType(stepType);
	SysUser user = SysUser.dao.findById(o.getCuserid());
	SysOrg org = SysOrg.dao.findById(user.getOrgid());
	cHotlineUser.setUserid(user.getId());
	cHotlineUser.setUsername(user.getName());
	cHotlineUser.setOrgid(org.getId());
	cHotlineUser.setOrgname(org.getName());
	cHotlineUser.setSortnum("600");
	cHotlineUser.setIfshow("1");
	cHotlineUser.setIfcomplete("0");
	cHotlineUser.setCtime(DateUtil.getCurrentTime());
	cHotlineUser.setCuserid(ShiroKit.getUserId());
	cHotlineUser.setCusername(ShiroKit.getUserName());
	cHotlineUser.save();
	
	renderSuccess();
	   
   }
   
   //承办人办结
   
   public  void cbOver() {
	   OaHotline o = getModel(OaHotline.class);
	  // o.setStatus("5");
	   o.update();
	   o=OaHotline.dao.findById(o.getId());
	   String comment = getPara("comment");
	   
	   String sid = getPara("sid");
	   OaSteps uHotLineUser = OaSteps.dao.findById(sid);
	   uHotLineUser.setIfcomplete("1");
	   uHotLineUser.setRemarks(comment);
	   uHotLineUser.setCompletetime(DateUtil.getCurrentTime());
	   uHotLineUser.update();
	   
	   StepUtil.insertStepHistory(o.getId(),o.getBackup3(), stepType);
	   
	   
	  if(uHotLineUser.getParentid()!=null) {
		  
		  OaSteps paSteps = OaSteps.dao.findById(uHotLineUser.getParentid());
		  
		  OaSteps cSteps=new OaSteps();
		  cSteps.setId(UuidUtil.getUUID());
		  cSteps.setOid(paSteps.getOid());
		  cSteps.setType(paSteps.getType());
		  cSteps.setStep("54");
		  cSteps.setTitle(o.getBackup3());
		  cSteps.setType(stepType);
		  cSteps.setUserid(paSteps.getUserid());
		  cSteps.setUsername(paSteps.getUsername());
		  cSteps.setOrgid(paSteps.getOrgid());
		  cSteps.setOrgname(paSteps.getOrgname());
		  cSteps.setIfshow("1");
		  cSteps.setIfcomplete("0");
		  cSteps.setParentid(paSteps.getParentid());
		  cSteps.setCtime(DateUtil.getCurrentTime());
		  cSteps.setCuserid(ShiroKit.getUserId());
		  cSteps.setUsername(ShiroKit.getUserName());
		  
		  
		  cSteps.save();
		  
	  }else {
		  o.setStatus("5");
		   OaSteps cHotlineUser=new OaSteps();
		   cHotlineUser.setId(UuidUtil.getUUID());
		   cHotlineUser.setOid(o.getId());
		   cHotlineUser.setStep("6");
		   cHotlineUser.setType(stepType);
		   cHotlineUser.setTitle(o.getBackup3());
		   SysUser user = SysUser.dao.findById(o.getCuserid());
		   SysOrg org = SysOrg.dao.findById(user.getOrgid());
		   cHotlineUser.setUserid(user.getId());
		   cHotlineUser.setUsername(user.getName());
		   cHotlineUser.setOrgid(org.getId());
		   cHotlineUser.setOrgname(org.getName());
		   cHotlineUser.setSortnum("600");
		   cHotlineUser.setIfshow("1");
		   cHotlineUser.setIfcomplete("0");
		   cHotlineUser.setCtime(DateUtil.getCurrentTime());
		   cHotlineUser.setCuserid(ShiroKit.getUserId());
		   cHotlineUser.setCusername(ShiroKit.getUserName());
		   cHotlineUser.save();
	  }
	   
	  String remark=(o.getCbrremark()==null?"":o.getCbrremark())+"\r\n"+comment+"("+ShiroKit.getUserName()+")";
	  
	  o.setCbrremark(remark);
  	  o.update();
	   
	   renderSuccess();
	   
   }
    /**
     * 
    * @Title: hotlineOver 
    * @Description: 归档
    * @date 2020年11月6日下午5:24:20
     */
  public void  hotlineOver() {
	   	OaHotline o = getModel(OaHotline.class);
	   	o.setStatus("6");
	   	o.update();
	   	o=OaHotline.dao.findById(o.getId());
	   	String comment = getPara("comment");
	  
	 	String sid = getPara("sid");
		OaSteps uHotLineUser = OaSteps.dao.findById(sid);
		uHotLineUser.setIfcomplete("1");
		uHotLineUser.setRemarks(comment);
		uHotLineUser.setCompletetime(DateUtil.getCurrentTime());
		uHotLineUser.update();
		
		StepUtil.insertStepHistory(o.getId(), o.getBackup3(),stepType);
		renderSuccess();
  }
   
  
  /**
   * 
  * @Title: leaderSendCb 
  * @Description: 部门领导审批不通过送承办
  * @date 2020年11月6日下午5:33:36
   */
  public void leaderSendCb(){
	/*	OaHotline o = getModel(OaHotline.class);
    	o=OaHotline.dao.findById(o.getId());
    	String sid = getPara("sid");
    	String comment = getPara("comment");
    	
    	String commonuserid = getPara("commonuserid");
      	SysUser user = SysUser.dao.findById(commonuserid);
      	o.setCbrids(user.getId());
      	o.setCbrnames(user.getName());;
      	o.setCbrremark("");
      	o.setStatus("3");
      	o.update();
      	OaSteps uHotlineUser=OaSteps.dao.findById(sid);
      	uHotlineUser.setCompletetime(DateUtil.getCurrentTime());
      	uHotlineUser.setIfcomplete("1");
      	uHotlineUser.setRemarks(comment);
      	uHotlineUser.update();
      	StepUtil.insertStepHistory(o.getId(), o.getBackup3(),stepType);
      	
      	OaSteps cHotlineUser=new OaSteps();
      	cHotlineUser.setId(UuidUtil.getUUID());
      	cHotlineUser.setOid(o.getId());
      	cHotlineUser.setStep("54");
      	cHotlineUser.setType(stepType);
      	cHotlineUser.setTitle(o.getBackup3());
      	cHotlineUser.setUserid(user.getId());
      	cHotlineUser.setUsername(user.getName());
      	SysOrg org = SysOrg.dao.findById(user.getOrgid());
      	cHotlineUser.setOrgid(org.getId());
      	cHotlineUser.setOrgname(org.getName());
      	cHotlineUser.setIfshow("1");
      	cHotlineUser.setIfcomplete("0");
      	cHotlineUser.setCuserid(ShiroKit.getUserId());
      	cHotlineUser.setCusername(ShiroKit.getUserName());
      	cHotlineUser.setCtime(DateUtil.getCurrentTime());
      	cHotlineUser.save();
      	renderSuccess();*/
	  
	  
	  
		OaHotline o = getModel(OaHotline.class);
    	o=OaHotline.dao.findById(o.getId());
    	
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
      		   	cbrIds=cbrIds+","+userid;
      		   	cbrNames=cbrNames+","+user.getName();
      			SysOrg org = SysOrg.dao.findById(user.getOrgid());
      			OaSteps cHotlineUser=new OaSteps();
      			cHotlineUser.setId(UuidUtil.getUUID());
      			cHotlineUser.setOid(o.getId());
      			cHotlineUser.setStep("54");
      			cHotlineUser.setType(stepType);
      			cHotlineUser.setTitle(o.getBackup3());
      			cHotlineUser.setUserid(user.getId());
      			cHotlineUser.setUsername(user.getName());
      	
      			cHotlineUser.setOrgid(org.getId());
      			cHotlineUser.setOrgname(org.getName());
      			cHotlineUser.setIfshow("1");
      			cHotlineUser.setIfcomplete("0");
      			cHotlineUser.setCuserid(ShiroKit.getUserId());
      			cHotlineUser.setCusername(ShiroKit.getUserName());
      			cHotlineUser.setCtime(DateUtil.getCurrentTime());
      			cHotlineUser.save();
			}
      	   	o.setCbrids(o.getCbrids()+cbrIds);
    	    o.setCbrnames(o.getCbrnames()+cbrNames);;
      	}
      	
      	
      	
     
		String remarks=o.getCbrremark()==null?"":o.getCbrremark()+"\r\n"+comment+"\r\n\t\t\t\t\t\t\t\t"+ShiroKit.getUserName()+" "+DateUtil.getCurrentYMD();
    	o.setCbrremark(remarks);
      	
      	
  
     	o.setStatus("3");
      	
    	o.update();
      	renderSuccess();
  }
  
  /**
   * 
   * @Title: sendCb 
   * @Description: 承办人送承办
   * @date 2020年11月6日下午5:33:36
   */
  public void sendCb(){
	  OaHotline o = getModel(OaHotline.class);
	  o=OaHotline.dao.findById(o.getId());
	  String sid = getPara("sid");
	  String comment = getPara("comment");
	  
	  String commonuserid = getPara("commonuserid");
	  SysUser user = SysUser.dao.findById(commonuserid);
	  o.setCbrids(user.getId());
	  o.setCbrnames(user.getName());
	  o.setCbrremark("");
	  //o.setStatus("3");
	  o.update();
	  OaSteps uHotlineUser=OaSteps.dao.findById(sid);
	  uHotlineUser.setCompletetime(DateUtil.getCurrentTime());
	  uHotlineUser.setIfcomplete("1");
	  uHotlineUser.setRemarks(comment);
	  uHotlineUser.update();
	  StepUtil.insertStepHistory(o.getId(), o.getBackup3(),stepType);
	  
	  OaSteps cHotlineUser=new OaSteps();
	  cHotlineUser.setId(UuidUtil.getUUID());
	  cHotlineUser.setOid(o.getId());
	  cHotlineUser.setStep("54");
	  cHotlineUser.setType(stepType);
	  cHotlineUser.setTitle(o.getBackup3());
	  cHotlineUser.setUserid(user.getId());
	  cHotlineUser.setUsername(user.getName());
	  SysOrg org = SysOrg.dao.findById(user.getOrgid());
	  cHotlineUser.setOrgid(org.getId());
	  cHotlineUser.setOrgname(org.getName());
	  //	cHotlineUser.setSortnum("5400");
	  cHotlineUser.setIfshow("1");
	  cHotlineUser.setIfcomplete("0");
	  
	  cHotlineUser.setParentid(sid);
	  cHotlineUser.setCuserid(ShiroKit.getUserId());
	  cHotlineUser.setCusername(ShiroKit.getUserName());
	  cHotlineUser.setCtime(DateUtil.getCurrentTime());
	  cHotlineUser.save();
	  renderSuccess();
  }
    
  
  
	/***
	 * 导入
	 */
	public void importExcel() throws IOException, SQLException {
		UploadFile file = getFile("file","/content");
		List<List<String>> list = ExcelUtil.excelToList(file.getFile().getAbsolutePath());
		Map<String,Object> result = service.importExcel(list);
		if((Boolean)result.get("success")){
			renderSuccess((String)result.get("message"));
		}else{
			renderError((String)result.get("message"));
		}
	}
  
  /**
   * 
  * @Title: saveEdit 
  * @Description: 保存修改
  * @date 2020年12月3日下午4:34:58
   */
  public void saveEdit() {
	  
	  OaHotline o = getModel(OaHotline.class);
	  o.update();
	  renderSuccess();
  }
  
  
  public void  printFromSearh() {
  	String id= getPara("id");
  	OaHotline hotline = OaHotline.dao.findById(id);
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
  
}