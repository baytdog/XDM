/**
 * @author Lion
 * @date 2017年1月24日 下午12:02:35
 * @qq 439635374
 */
package com.pointlion.mvc.admin.oa.bumph;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.pointlion.mvc.admin.oa.common.OAConstants;
import com.pointlion.mvc.admin.oa.workflow.WorkFlowService;
import com.pointlion.mvc.common.base.BaseController;
import com.pointlion.mvc.common.model.D3Edge;
import com.pointlion.mvc.common.model.OaBumph;
import com.pointlion.mvc.common.model.OaBumphUser;
import com.pointlion.mvc.common.model.OaNodes;
import com.pointlion.mvc.common.model.OaSteps;
import com.pointlion.mvc.common.model.SysOrg;
import com.pointlion.mvc.common.model.SysUser;
import com.pointlion.mvc.common.utils.Constants;
import com.pointlion.mvc.common.utils.DateUtil;
import com.pointlion.mvc.common.utils.NodesUtils;
import com.pointlion.mvc.common.utils.StepUtil;
import com.pointlion.mvc.common.utils.StringUtil;
import com.pointlion.mvc.common.utils.UuidUtil;
import com.pointlion.plugin.shiro.ShiroKit;

/***
 * 用户管理控制器
 * @author Administrator
 *
 */
public class BumphController extends BaseController {
	static BumphService service = BumphService.me;
	static WorkFlowService wfservice = WorkFlowService.me;
	public static  final String  typeStep="1";
	
	/***************************内部发文---开始***********************/
	/***
	 * 获取公文起草页面
	 */
	public void getDraftListPage(){
		String type = getPara("type");

		setAttr("type", type);

		renderIframe("list.html");
    }
	/***
	 * 公文起草列表数据
	 * @throws UnsupportedEncodingException 
	 */
    public void draftListData() throws UnsupportedEncodingException{
    	String curr = getPara("pageNumber");
    	String pageSize = getPara("pageSize");
    	String type = getPara("type");
    	
    	
		String endTime = getPara("endTime","");
		String startTime = getPara("startTime","");
		String rdnum = java.net.URLDecoder.decode(getPara("rdnum",""),"UTF-8");
		String rtitle = java.net.URLDecoder.decode(getPara("rtitle",""),"UTF-8");
		String rFileNo = java.net.URLDecoder.decode(getPara("rFileNo",""),"UTF-8");
		
		String rstate = getPara("rstate","");
    	
    	
    	Page<OaBumph> page = OaBumph.dao.getPage(Integer.valueOf(curr),Integer.valueOf(pageSize),type, rdnum,  rtitle,  rFileNo,  rstate,  startTime,  endTime);
    	renderPage(page.getList(),"" ,page.getTotalRow());
    }
    
    
    
	public void getDraftListPage2(){
		String type = getPara("type");
	/*	String name = "";
		if("1".equals(type)){
			name = "公司发文";
		}else if("2".equals(type)){
			name = "公司收文";
		}*/
		setAttr("type", type);

		renderIframe("list2.html");
    }
	/**
	 * 
	* @Title: getDraftListPage2 
	* @Description: 收文待办结列表
	* @date 2020年11月7日下午5:07:55
	 */
	public void getPReceivePage(){
		String type = getPara("type");
	 
		setAttr("type", type);
		
		renderIframe("pReceiveList.html");
	}
	/**
	 * 
	* @Title: getPReceivesPage 
	* @Description: TODO  收文页面（没有归档的）
	* @date 2021年1月23日上午8:41:38
	 */
	public void getPReceivesPage(){
		String type = getPara("type");
		
		setAttr("type", type);
		
		renderIframe("pReceivesList.html");
	}
	
	
	
	/*
	 * 菜单待办收文跳转
	 */
	public void getReceiveWaitDoPage(){
		String type = getPara("type");
	 
		setAttr("type", type);
		
		
		String serverIp = getRequest().getServerName();
		System.out.println(serverIp);
		String requestURL=getRequest().getRequestURL().toString();
		System.out.println(requestURL);
		String string = requestURL.split(serverIp)[0];
		
 
		System.out.println(string+serverIp);
		
		String startStr= string+serverIp;
		
		
		
		setAttr("startStr", startStr);
		setAttr("requestURL", requestURL);
		
		renderIframe("pReceiveWaitDoList.html");
	}
	
	/*
	 * 菜单已办收文跳转
	 */
	public void getReceiveOverPage(){
		String type = getPara("type");
	 
		setAttr("type", type);
		

		String serverIp = getRequest().getServerName();
		System.out.println(serverIp);
		String requestURL=getRequest().getRequestURL().toString();
		System.out.println(requestURL);
		String string = requestURL.split(serverIp)[0];
		
 
		System.out.println(string+serverIp);
		
		String startStr= string+serverIp;
		
		
		
		setAttr("startStr", startStr);
		setAttr("requestURL", requestURL);
		
		
		
		String userId = ShiroKit.getUserId();
		SysUser user = SysUser.dao.findById(userId);
		
		
		boolean isLeader=false;
		if(user.getPosition().equals("2")||user.getPosition().equals("6")) {
			isLeader=true;
		}
		
		setAttr("isLeader", isLeader);
		
		renderIframe("pReceiveOverDoList.html");
	}
	
	
	
	
	
	public void getBsznListPage(){
		String type = getPara("type");
		/*	String name = "";
		if("1".equals(type)){
			name = "公司发文";
		}else if("2".equals(type)){
			name = "公司收文";
		}*/
		setAttr("type", type);
		
		renderIframe("bsznlist.html");
	}
	
	public void getBsznListFromHomtPage(){
		String type = getPara("type");
		/*	String name = "";
		if("1".equals(type)){
			name = "公司发文";
		}else if("2".equals(type)){
			name = "公司收文";
		}*/
		setAttr("type", type);
		
		renderIframe("bsznlistFromHomePage.html");
	}
	
	
	/***
	 * 公文起草列表数据
	 */
    public void draftListData2(){
    	String curr = getPara("pageNumber");
    	String pageSize = getPara("pageSize");
    	Page<OaBumph> page = OaBumph.dao.getPageData(Integer.valueOf(curr),Integer.valueOf(pageSize));
    	renderPage(page.getList(),"" ,page.getTotalRow());
    }
    /**
     * 
    * @Title: pReceiveListData 
    * @Description: 公文个人任务列表数据
    * @date 2020年11月7日下午5:13:53
     */
    public void pReceiveListData(){
    	String curr = getPara("pageNumber");
    	String pageSize = getPara("pageSize");
    	Page<OaBumph> page = OaBumph.dao.getPReceivePageData(Integer.valueOf(curr),Integer.valueOf(pageSize));
    	renderPage(page.getList(),"" ,page.getTotalRow());
    }
    
    /**
     * 
    * @Title: pReceiveListWaitData 
    * @Description: TODO  待办菜单数据
    * @date 2021年1月24日上午8:42:45
     */
    
    public void pReceiveListWaitData(){
    	String curr = getPara("pageNumber");
    	String pageSize = getPara("pageSize");
    	Page<OaBumph> page = OaBumph.dao.getPReceivePageWaitData(Integer.valueOf(curr),Integer.valueOf(pageSize));
    	renderPage(page.getList(),"" ,page.getTotalRow());
    }
    
    /**
     * 
    * @Title: pReceivesListData 
    * @Description: TODO 获取归档之前的收文
    * @date 2021年1月23日上午8:43:57
     */
    public void pReceivesListData(){
    	String curr = getPara("pageNumber");
    	String pageSize = getPara("pageSize");
    	Page<OaBumph> page = OaBumph.dao.getPReceivesPageData(Integer.valueOf(curr),Integer.valueOf(pageSize));
    	renderPage(page.getList(),"" ,page.getTotalRow());
    }
    
    
    
    
    /**
     * 
    * @throws UnsupportedEncodingException 
     * @Title: pReceiveListData 
    * @Description: 收文已办列表
    * @date 2021年1月18日上午11:19:25
     */
    public void pReceiveOverListData() throws UnsupportedEncodingException{
    	String curr = getPara("pageNumber");
    	String pageSize = getPara("pageSize");
    	
    	
		String endTime = getPara("endTime","");
		String startTime = getPara("startTime","");
		String rdnum = java.net.URLDecoder.decode(getPara("rdnum",""),"UTF-8");
		String rtitle = java.net.URLDecoder.decode(getPara("rtitle",""),"UTF-8");
		String rFileNo = java.net.URLDecoder.decode(getPara("rFileNo",""),"UTF-8");
		
    	
    	
    	Page<OaBumph> page = OaBumph.dao.getPReceiveOverPageData(Integer.valueOf(curr),Integer.valueOf(pageSize), startTime, endTime, rdnum, rtitle, rFileNo);
    	renderPage(page.getList(),"" ,page.getTotalRow());
    }
    
    
    
    
    public void bsznListData(){
    	String curr = getPara("pageNumber");
    	String pageSize = getPara("pageSize");
    	Page<OaBumph> page = OaBumph.dao.getPage(Integer.valueOf(curr),Integer.valueOf(pageSize));
    	renderPage(page.getList(),"" ,page.getTotalRow());
    }
    
    
    
    public void  dobumphqy() {
    	
 
    
    Db.update("update  oa_bumph_user set looked='1',ifcomplete='1',readtime='"+DateUtil.getCurrentTime()+"',leaderremark='"+getPara("comment")+"' where bumphid='"+getPara("oabid")+"' and username='"+ShiroKit.getUsername()+"'");
	List<OaBumphUser> findOaBumphUser = OaBumphUser.dao.find("select  * from oa_bumph_user  where bumphid='"+getPara("oabid")+"' and  username='"+ShiroKit.getUsername()+"'");
	List<OaBumphUser> find = OaBumphUser.dao.find("select  * from oa_bumph_user  where bumphid='"+getPara("oabid")+"' and  looked='0' and  parentid='"+findOaBumphUser.get(0).getParentid()+"'");
    
	//if(findOaBumphUser.get(0).getBumphmodel().equals("1")) {
		if(find.size()<1) {
			Db.update("update  oa_bumph_user set ifcomplete='1'  where id='"+findOaBumphUser.get(0).getParentid()+"'");
			Db.update("update  oa_bumph_user set lookornot='1'  where bumphid='"+getPara("oabid")+"' and parentid!=''");
		}
//	}
	
	
	
	
 
	if(getPara("ifneed").equals("1")) {
	List<OaBumphUser> findOaBUser = OaBumphUser.dao.find("select  * from  oa_bumph_user  where bumphid='"+getPara("oabid")+"' and username='"+ShiroKit.getUsername()+"'");
	OaBumphUser oaBumphUser = findOaBUser.get(0);
	
	SysUser adduser = SysUser.dao.findById(getPara("commonuserid"));
	OaBumphUser buser =new OaBumphUser();
	buser.setId(UuidUtil.getUUID());
	buser.setBumphid(getPara("oabid"));
	buser.setBumphmodel(oaBumphUser.getBumphmodel());
	buser.setParentid(oaBumphUser.getParentid());
	buser.setIfcomplete("0");
	buser.setOrgid(oaBumphUser.getOrgid());
	buser.setOrgname(oaBumphUser.getOrgname());
	buser.setLookornot("1");
	buser.setLooked("0");
	buser.setIfshow("1");
	buser.setIfcomplete("0");
	buser.setUsername(adduser.getUsername());
	buser.setUserid(adduser.getId());
	List<OaBumphUser> buserL = OaBumphUser.dao.find("select  * from  oa_bumph_user where  bumphid='"+getPara("oabid")+"'");
	//buser.setSortnum(String.valueOf(buserL.size()+1));
	buser.setSortnum(buserL.size()+1);
	buser.setBackup1(adduser.getName());
	buser.setBackup2(DateUtil.getCurrentTime());
	buser.setReadtime(DateUtil.getAfterDayDate("30"));
	buser.save();
	}	
	
	
	
	renderSuccess();
    	
    	
    }
    public void  dobumphqyV1() {
    	
    	
    	Db.update("update  oa_bumph_user set looked='1',ifcomplete='1',readtime='"+DateUtil.getCurrentTime()+"',leaderremark='"+getPara("comment")+"' where bumphid='"+getPara("oabid")+"' and userid='"+ShiroKit.getUserId()+"'");
    	OaBumphUser findFirst = OaBumphUser.dao.findFirst("select  * from  oa_bumph_user  where  bumphid='"+getPara("oabid")+"'  and userid='"+ShiroKit.getUserId()+"'");
    	
    	List<OaBumphUser> findNoCompletes = OaBumphUser.dao.find("select  * from  oa_bumph_user  where  bumphid='"+getPara("oabid")+"' and  sortnum='"+findFirst.getSortnum()+"' and  ifcomplete='0'");
    	
    	if(findNoCompletes.size()==0) {
    		Db.update("update  oa_bumph_user set backup3='1' where bumphid='"+getPara("oabid")+"' and  sortnum='"+(Integer.valueOf(findFirst.getSortnum())+1)+"'");
    	}
    	
    	
    	List<OaBumphUser> findOaBumphUser = OaBumphUser.dao.find("select  * from oa_bumph_user  where bumphid='"+getPara("oabid")+"' and  username='"+ShiroKit.getUsername()+"'");
    	List<OaBumphUser> find = OaBumphUser.dao.find("select  * from oa_bumph_user  where bumphid='"+getPara("oabid")+"' and  looked='0' and  parentid='"+findOaBumphUser.get(0).getParentid()+"'");
    	
    	//if(findOaBumphUser.get(0).getBumphmodel().equals("1")) {
    	if(find.size()<1) {
    		Db.update("update  oa_bumph_user set ifcomplete='1'  where id='"+findOaBumphUser.get(0).getParentid()+"'");
    		Db.update("update  oa_bumph_user set lookornot='1'  where bumphid='"+getPara("oabid")+"' and parentid!=''");
    	}
//	}
    	
    	
    	
    	
    	
    	
    	
    	
    	if(getPara("ifneed").equals("1")) {
    		List<OaBumphUser> findOaBUser = OaBumphUser.dao.find("select  * from  oa_bumph_user  where bumphid='"+getPara("oabid")+"' and username='"+ShiroKit.getUsername()+"'");
    		OaBumphUser oaBumphUser = findOaBUser.get(0);
    		
    		SysUser adduser = SysUser.dao.findById(getPara("commonuserid"));
    		OaBumphUser buser =new OaBumphUser();
    		buser.setId(UuidUtil.getUUID());
    		buser.setBumphid(getPara("oabid"));
    		buser.setBumphmodel(oaBumphUser.getBumphmodel());
    		buser.setParentid(oaBumphUser.getParentid());
    		buser.setIfcomplete("0");
    		buser.setOrgid(oaBumphUser.getOrgid());
    		buser.setOrgname(oaBumphUser.getOrgname());
    		buser.setLookornot("1");
    		buser.setLooked("0");
    		buser.setIfshow("1");
    		buser.setIfcomplete("0");
    		buser.setUsername(adduser.getUsername());
    		buser.setUserid(adduser.getId());
    		List<OaBumphUser> buserL = OaBumphUser.dao.find("select  * from  oa_bumph_user where  bumphid='"+getPara("oabid")+"'");
    		//buser.setSortnum(String.valueOf(buserL.size()+1));
    		buser.setSortnum(buserL.size()+1);
    		buser.setBackup1(adduser.getName());
    		buser.setBackup2(DateUtil.getCurrentTime());
    		buser.setBackup3("1");
    		buser.setReadtime(DateUtil.getAfterDayDate("30"));
    		buser.save();
    	}	
    	
    	
    	
    	renderSuccess();
    	
    	
    }
    
    public void  bumphqy() {
    	

		
		List<SysOrg> orgList = SysOrg.dao.find("select id  from  sys_org where  name='中心领导'");
		List<SysUser> userList = SysUser.dao.find("select * from  sys_user where status='1' and  orgid='"+orgList.get(0).getId()+"' order by sort");
		setAttr("userList",userList);
		
		
		List<SysOrg> orgLists = SysOrg.dao.find("select *  from  sys_org where parent_id='root'  and name !='中心领导' order by sort ");
		
		setAttr("orgLists",orgLists);
		
		List<SysUser> commonusers = SysUser.dao.find("select  * from  sys_user  where  orgid='"+ShiroKit.getUserOrgId()+"'");
		
		setAttr("commonusers", commonusers);

		SysUser nowUser = SysUser.dao.findById(ShiroKit.getUserId());
		
		if(nowUser.getPosition().equals("科员")) {
			setAttr("ifneedadd", false);
		}else {
			setAttr("ifneedadd", true);
		}
		
		
		String type = getPara("type");
	 
		setAttr("type", type);

		//添加和修改
    	String id = getPara("id");//修改
		String view = getPara("view");//查看
		setAttr("view", view);
		if(StrKit.notBlank(id)){//修改
			OaBumph o = OaBumph.dao.findById(id);
			setAttr("o", o);
			
			
			List<OaBumphUser> syLlist = OaBumphUser.dao.find("select  * from  oa_bumph_user  where bumphid='"+id+"' and parentid !='' order by readtime,sortnum");
			setAttr("syLlist",syLlist);
			
		    List<OaBumph> completeLists = OaBumph.dao.find("select  * from  oa_bumph_user where  bumphid='"+id+"' and parentid !='' and  ifcomplete='1'"); 
		    List<OaBumph> findiscomplete = OaBumph.dao.find("select  * from oa_bumph_user  where bumphid='"+id+"' and  ifcomplete='0'"); 
		    setAttr("completeCount", findiscomplete.size()==0?completeLists.size()+1:completeLists.size());
			
			
		
		  
		  //获取主送和抄送单位
			service.setAttrFirstSecond(this,o.getId());
			//是否是查看详情页面
			if("detail".equals(view)){
    			if(StrKit.notBlank(o.getProcInsId())){
    				setAttr("procInsId", o.getProcInsId());
    				setAttr("defId", wfservice.getDefIdByInsId(o.getProcInsId()));
    			}
    		}
    	}else{//新增
    		OaBumph o = new OaBumph();
    		String userId = ShiroKit.getUserId();//用户主键
    		SysUser user = SysUser.dao.getById(userId);//用户对象
    		SysOrg org = SysOrg.dao.getById(user.getOrgid());//单位对象
    		o.setDocNumYear(DateUtil.getCurrentYear());
    		o.setSenderId(userId);
    		o.setSenderName(user.getName());
    		o.setSenderOrgid(org.getId());
    		o.setSenderOrgname(org.getName());
    		setAttr("o",o);
    		
  /*  		List<SysOrg> orgList = SysOrg.dao.find("select id  from  sys_org where  name='中心领导'");
    		List<SysUser> userList = SysUser.dao.find("select * from  sys_user where orgid='"+orgList.get(0).getId()+"'");*/
    		/*setAttr("userList",userList);*/
    	}
    	setAttr("formModelName",StringUtil.toLowerCaseFirstOne(OaBumph.class.getSimpleName()));//模型名称
    	renderIframe("edit2.html");
    	
    }
    public void  bumphqyV1() {
    	
    	
    	
    	/*List<SysOrg> orgList = SysOrg.dao.find("select id  from  sys_org where  name='中心领导'");
    	List<SysUser> userList = SysUser.dao.find("select * from  sys_user where orgid='"+orgList.get(0).getId()+"'");
    	setAttr("userList",userList);
    	*/
    	
    	List<SysOrg> orgLists = SysOrg.dao.find("select *  from  sys_org where parent_id='root'  and name !='中心领导' order by sort ");
    	
    	setAttr("orgLists",orgLists);
    	
    	List<SysUser> commonusers = SysUser.dao.find("select  * from  sys_user  where  orgid='"+ShiroKit.getUserOrgId()+"' and  POSITION='9' ");
    	
    	setAttr("commonusers", commonusers);
    	
    	SysUser nowUser = SysUser.dao.findById(ShiroKit.getUserId());
    	
    	if(nowUser.getPosition().equals("9")) {
    		setAttr("ifneedadd", false);
    	}else {
    		setAttr("ifneedadd", true);
    	}
    	
    	
    	String type = getPara("type");
    	
    	setAttr("type", type);
    	
    	//添加和修改
    	String id = getPara("id");//修改
    	String view = getPara("view");//查看
    	setAttr("view", view);
    	if(StrKit.notBlank(id)){//修改
    		OaBumph o = OaBumph.dao.findById(id);
    		setAttr("o", o);
    		
    		
    		List<OaBumphUser> syLlist = OaBumphUser.dao.find("select  * from  oa_bumph_user  where bumphid='"+id+"' order by readtime,sortnum");
    		setAttr("syLlist",syLlist);
    		
    		List<OaBumph> completeLists = OaBumph.dao.find("select  * from  oa_bumph_user where  bumphid='"+id+"'  and  ifcomplete='1'"); 
    		List<OaBumph> findiscomplete = OaBumph.dao.find("select  * from oa_bumph_user  where bumphid='"+id+"' and  ifcomplete='0'"); 
    		setAttr("completeCount", findiscomplete.size()==0?completeLists.size()+1:completeLists.size());
    		
    		OaBumphUser oabu = OaBumphUser.dao.findFirst("select  * from  oa_bumph_user  where bumphid='"+id+"' and userid='"+ShiroKit.getUserId()+"'");
    		
    		setAttr("oabu", oabu);
    		
    		
    		//获取主送和抄送单位
    		service.setAttrFirstSecond(this,o.getId());
    		//是否是查看详情页面
    		if("detail".equals(view)){
    			if(StrKit.notBlank(o.getProcInsId())){
    				setAttr("procInsId", o.getProcInsId());
    				setAttr("defId", wfservice.getDefIdByInsId(o.getProcInsId()));
    			}
    		}
    	}else{//新增
    		OaBumph o = new OaBumph();
    		String userId = ShiroKit.getUserId();//用户主键
    		SysUser user = SysUser.dao.getById(userId);//用户对象
    		SysOrg org = SysOrg.dao.getById(user.getOrgid());//单位对象
    		o.setDocNumYear(DateUtil.getCurrentYear());
    		o.setSenderId(userId);
    		o.setSenderName(user.getName());
    		o.setSenderOrgid(org.getId());
    		o.setSenderOrgname(org.getName());
    		setAttr("o",o);
    		
    		/*  		List<SysOrg> orgList = SysOrg.dao.find("select id  from  sys_org where  name='中心领导'");
    		List<SysUser> userList = SysUser.dao.find("select * from  sys_user where orgid='"+orgList.get(0).getId()+"'");*/
    		/*setAttr("userList",userList);*/
    	}
    	setAttr("formModelName",StringUtil.toLowerCaseFirstOne(OaBumph.class.getSimpleName()));//模型名称
    	renderIframe("edit2.html");
    	
    }
    
    
    
    public void  bumphLz() {
    	
    	
    	List<SysOrg> orgLists = SysOrg.dao.find("select *  from  sys_org where parent_id='root'  and name !='中心领导' order by sort ");
    	
    	setAttr("orgLists",orgLists);
    	
    	List<SysUser> commonusers = SysUser.dao.find("select  * from  sys_user  where  orgid='"+ShiroKit.getUserOrgId()+"' and  POSITION='9' ");
    	
    	setAttr("commonusers", commonusers);
    	
    	List<SysOrg> orgList = SysOrg.dao.find("select id  from  sys_org where  name='中心领导'");
		List<SysUser> userList = SysUser.dao.find("select * from  sys_user where status='1' and  orgid='"+orgList.get(0).getId()+"' order by sort");
		setAttr("userList",userList);
    	
		
    	
    /*	SysUser nowUser = SysUser.dao.findById(ShiroKit.getUserId());
    	
    	
    	
    	
    	if(nowUser.getPosition().equals("9")) {
    		setAttr("ifneedadd", false);
    	}else {
    		setAttr("ifneedadd", true);
    	}*/
    	
		if( Integer.valueOf(getPara("step"))>=4) {
			setAttr("ifneedadd", true);
		}else {
			setAttr("ifneedadd", false);
		}
		
		
		
    	
    	String type = getPara("type");
    	
    	setAttr("type", type);
    	
    	//添加和修改
    	String id = getPara("id");//修改
    	String view = getPara("view");//查看
    	
    	//String step = getPara("step");
    	keepPara("step");
    	keepPara("uid");
    	
    	setAttr("view", view);
    		OaBumph o = OaBumph.dao.findById(id);
    		setAttr("o", o);
    		
    		
    		List<OaBumphUser> syLlist = OaBumphUser.dao.find("select  * from  oa_bumph_user  where bumphid='"+id+"' order by readtime,sortnum");
    		setAttr("syLlist",syLlist);
    		
    		List<OaBumph> completeLists = OaBumph.dao.find("select  * from  oa_bumph_user where  bumphid='"+id+"'  and  ifcomplete='1'"); 
    		List<OaBumph> findiscomplete = OaBumph.dao.find("select  * from oa_bumph_user  where bumphid='"+id+"' and  ifcomplete='0'"); 
    		setAttr("completeCount", findiscomplete.size()==0?completeLists.size()+1:completeLists.size());
    		
    		OaBumphUser oabu = OaBumphUser.dao.findFirst("select  * from  oa_bumph_user  where bumphid='"+id+"' and userid='"+ShiroKit.getUserId()+"'");
    		
    		setAttr("oabu", oabu);
    		
    		
    		//获取主送和抄送单位
    		service.setAttrFirstSecond(this,o.getId());
    /*		//是否是查看详情页面
    		if("detail".equals(view)){
    			if(StrKit.notBlank(o.getProcInsId())){
    				setAttr("procInsId", o.getProcInsId());
    				setAttr("defId", wfservice.getDefIdByInsId(o.getProcInsId()));
    			}
    		}*/
    		
    	List<OaBumphUser> find = OaBumphUser.dao.find("select  * from oa_bumph_user  where ifcomplete ='0' and  step not in ('1','6','7') and bumphid='"+o.getId()+"'");
    	
    	if(find.size()==0) {
    		setAttr("kfgd", true);
    	}else {
    		setAttr("kfgd", false);
    	}
    	
    	setAttr("formModelName",StringUtil.toLowerCaseFirstOne(OaBumph.class.getSimpleName()));//模型名称
    	renderIframe("edit2.html");
    	
    
    }
    
/**
 * 
* @Title: openReceivePage 
* @Description: 打开收文个人任务页面
* @date 2020年11月7日下午5:21:14
 */
  public void  openReceivePage() {
    	List<SysOrg> orgLists = SysOrg.dao.find("select *  from  sys_org where parent_id='root'  and name !='中心领导' order by sort ");
    	setAttr("orgLists",orgLists);
    	
    	
    	String sqlUser ="select  * from  sys_user  where  orgid='"+ShiroKit.getUserOrgId()+"'";
    	String adminUser = Constants.ADMIN_USER;
    	String[] adminUsers = adminUser.split(",");
    	for (String adminuser : adminUsers) {
			
    	sqlUser=sqlUser+"and username!='"+adminuser+"'";
    		
		}
    	
    	
    	sqlUser=sqlUser+"order by sort";
    	
    	
    	List<SysUser> commonusers = SysUser.dao.find(sqlUser);
    	setAttr("commonusers", commonusers);
    	
    	List<SysOrg> orgList = SysOrg.dao.find("select id  from  sys_org where  name='中心领导'");
		List<SysUser> userList = SysUser.dao.find("select * from  sys_user where status='1' and orgid='"+orgList.get(0).getId()+"' order by sort");
		setAttr("userList",userList);
    	setAttr("usersname", "show");
		
    	
    /*	SysUser nowUser = SysUser.dao.findById(ShiroKit.getUserId());
    	
    	
    	
    	
    	if(nowUser.getPosition().equals("9")) {
    		setAttr("ifneedadd", false);
    	}else {
    		setAttr("ifneedadd", true);
    	}*/
    	
		if( Integer.valueOf(getPara("sstep"))>=4) {
			setAttr("ifneedadd", true);
		}else {
			setAttr("ifneedadd", false);
		}
		
		setAttr("currentUserId", ShiroKit.getUserId());
		
		
    	
    	String type = getPara("type");
    	
    	setAttr("type", type);
    	
    	//添加和修改
    	String id = getPara("id");//修改
    	String view = getPara("view");//查看
    	
    	//String step = getPara("step");
    	keepPara("sstep");
    	keepPara("sid");
    	
    	setAttr("view", view);
		OaBumph o = OaBumph.dao.findById(id);
		setAttr("o", o);
		
		OaSteps osteps = OaSteps.dao.findById(getPara("sid"));
		
		setAttr("buttontype", osteps.getButtontype());
		
		setAttr("completed", osteps.getIfcomplete());
		
		if(!o.getCreateUserId().equals(ShiroKit.getUserId())) {
			setAttr("showCommont", true);
		}else {
			setAttr("showCommont", false);
		}
		
		
		
		List<OaSteps> syLlist = OaSteps.dao.find("select  * from  oa_steps  where oid='"+id+"' order by sortnum");
		setAttr("syLlist",syLlist);
		
		/*List<OaBumph> completeLists = OaBumph.dao.find("select  * from  oa_bumph_user where  bumphid='"+id+"'  and  ifcomplete='1'"); 
		List<OaBumph> findiscomplete = OaBumph.dao.find("select  * from oa_bumph_user  where bumphid='"+id+"' and  ifcomplete='0'"); 
		setAttr("completeCount", findiscomplete.size()==0?completeLists.size()+1:completeLists.size());*/
		
	/*	OaSteps oabu = OaSteps.dao.findFirst("select  * from  oa_steps  where oid='"+id+"' and userid='"+ShiroKit.getUserId()+"'");
		
		setAttr("oabu", oabu);*/
    		
    		
    		//获取主送和抄送单位
    		service.setAttrFirstSecond(this,o.getId());
    /*		//是否是查看详情页面
    		if("detail".equals(view)){
    			if(StrKit.notBlank(o.getProcInsId())){
    				setAttr("procInsId", o.getProcInsId());
    				setAttr("defId", wfservice.getDefIdByInsId(o.getProcInsId()));
    			}
    		}*/
    		
   /* 	List<OaSteps> find = OaSteps.dao.find("select  * from oa_steps  where ifcomplete ='0' and  step not in ('1','6','7') and oid='"+o.getId()+"'");
    	
    	if(find.size()==0) {
    		setAttr("kfgd", true);
    	}else {
    		setAttr("kfgd", false);
    	}*/
    	
    	setAttr("formModelName",StringUtil.toLowerCaseFirstOne(OaBumph.class.getSimpleName()));//模型名称
    	renderIframe("preceive.html");
    	
    
    }
    
    
  /**
   *   
  * @Title: openReceivePageFromList 
  * @Description: TODO  待办收文列表跳转
  * @date 2021年1月24日上午9:06:40
   */
  public void  openReceivePageFromList() {
	  List<SysOrg> orgLists = SysOrg.dao.find("select *  from  sys_org where parent_id='root'  and name !='中心领导' order by sort ");
	  setAttr("orgLists",orgLists);
	  
	  
	  String sqlUser ="select  * from  sys_user  where  orgid='"+ShiroKit.getUserOrgId()+"'";
	  String adminUser = Constants.ADMIN_USER;
	  String[] adminUsers = adminUser.split(",");
	  for (String adminuser : adminUsers) {
		  
		  sqlUser=sqlUser+"and username!='"+adminuser+"'";
		  
	  }
	  
	  
	  sqlUser=sqlUser+"order by sort";
	  
	  
	  List<SysUser> commonusers = SysUser.dao.find(sqlUser);
	  setAttr("commonusers", commonusers);
	  
	  List<SysOrg> orgList = SysOrg.dao.find("select id  from  sys_org where  name='中心领导'");
	  List<SysUser> userList = SysUser.dao.find("select * from  sys_user where status='1' and orgid='"+orgList.get(0).getId()+"' order by sort");
	  setAttr("userList",userList);
	  setAttr("usersname", "show");
	  
	  
	 
	  
	  if( Integer.valueOf(getPara("sstep"))>=4) {
		  setAttr("ifneedadd", true);
	  }else {
		  setAttr("ifneedadd", false);
	  }
	  
	  setAttr("currentUserId", ShiroKit.getUserId());
	  
	  
	  
	  String type = getPara("type");
	  
	  setAttr("type", type);
	  
	  //添加和修改
	  String id = getPara("id");//修改
	  String view = getPara("view");//查看
	  
	  //String step = getPara("step");
	  keepPara("sstep");
	  keepPara("sid");
	  
	  setAttr("view", view);
	  OaBumph o = OaBumph.dao.findById(id);
	  setAttr("o", o);
	  
	  OaSteps osteps = OaSteps.dao.findById(getPara("sid"));
	  
	  setAttr("buttontype", osteps.getButtontype());
	  
	  setAttr("completed", osteps.getIfcomplete());
	  
	  if(!o.getCreateUserId().equals(ShiroKit.getUserId())) {
		  setAttr("showCommont", true);
	  }else {
		  setAttr("showCommont", false);
	  }
	  
	  
	  
	  List<OaSteps> syLlist = OaSteps.dao.find("select  * from  oa_steps  where oid='"+id+"' order by sortnum");
	  setAttr("syLlist",syLlist);
	  
	 
	  
	  //获取主送和抄送单位
	  service.setAttrFirstSecond(this,o.getId());
	 
	  
	  setAttr("formModelName",StringUtil.toLowerCaseFirstOne(OaBumph.class.getSimpleName()));//模型名称
	  renderIframe("preceiveFromList.html");
	  
	  
  }
  
  
  
    public void  print() {
    	String id= getPara("id");
    	OaBumph bump = OaBumph.dao.findById(id);
    	String nbrids = bump.getNbrids();
    	String nbrname="";
    	if(nbrids!=null&& !nbrids.equals("")) {
    		String[] nbrsid = nbrids.split(",");
    		
    		for (String nbrid : nbrsid) {
				SysUser user = SysUser.dao.findById(nbrid);
				if(nbrsid.length==1) {
					nbrname=user.getName();
				}else {
					nbrname=user.getName()+","+nbrname;
				}
			}
    	}
    	
    	
    	List<OaSteps> nbSteps = OaSteps.dao.find("select * from  oa_steps where  oid='"+id+"' and  step='2' and ifcomplete='1'");
    
    	String nbyj =bump.getNbyj();
    	
    	for (OaSteps oaSteps : nbSteps) {
			nbyj=nbyj.replace(oaSteps.getUsername(), "").replace(oaSteps.getCompletetime().substring(0, 10).trim(), "");
		}
    	
    	//String nbyj =bump.getNbyj().replace(nbrname, "").replace(DateUtil.getCurrentYMD(), "");
    	nbrname=nbrname +" "+DateUtil.getCurrentYMD();
    	
    	
    	
    	String[] contentArry = null;
    	if(bump.getContent()!=null) {
    		contentArry=bump.getContent().split("\r\n");
    	}
    	
    	
    	
    			
    	String content="";
    	
    	if(contentArry!=null && !contentArry.equals("")) {
    		for (int i = 0; i < contentArry.length; i=i+2) {
    			content=content+contentArry[i]+contentArry[i+1]+"\r\n";
    		}
    	}
    	
    	
    	
    	String[] clqkArr = null;
    	if(bump.getClqk()!=null) {
    		
    		
    		clqkArr=bump.getClqk().split("\r\n");
    		
    	}
    	String clqk="";
    	if(clqkArr!=null) {
    		for (int i = 0; i < clqkArr.length; i=i+2) {
    			clqk=clqk+clqkArr[i]+clqkArr[i+1]+"\r\n";
    		}
    		
    	}
    	
    	setAttr("content", content);
    	setAttr("clqk", clqk);
    	setAttr("o",bump);
    	setAttr("nbrname",nbrname);
    	setAttr("nbyj",nbyj);
    	
    	if(content.equals("")||content.equals("")) {
    		
    		render("printM2.html");
    	}else {
    		
    		render("print.html");
    		
    	}
    	
    }
    
    
    
	/***
	 * 编辑公文起草页面
	 */
	public void getDraftEditPage(){
		
		List<SysOrg> orgList = SysOrg.dao.find("select id  from  sys_org where  name='中心领导'");
		List<SysUser> userList = SysUser.dao.find("select * from  sys_user where  status='1' and orgid='"+orgList.get(0).getId()+"' order by sort");
		setAttr("userList",userList);
		
		
		List<SysOrg> orgLists = SysOrg.dao.find("select *  from  sys_org where parent_id='root'  and name !='中心领导' order by sort ");
		
		setAttr("orgLists",orgLists);
		
		
		List<SysUser> nbr = SysUser.dao.find("select * from  sys_user where orgid='"+ShiroKit.getUserOrgId()+"' and position in ('2','6') order by sort");
		
		setAttr("nbr",nbr);
		
		String type = getPara("type");
	 
		setAttr("type", type);
		//String parentPath = this.getRequest().getServletPath().substring(0,this.getRequest().getServletPath().lastIndexOf("/")+1); 
		setAttr("currentUserId",ShiroKit.getUserId());
		//添加和修改
    	String id = getPara("id");//修改
		String view = getPara("view");//查看
		setAttr("view", view);
		if(StrKit.notBlank(id)){//修改
			OaBumph o = OaBumph.dao.findById(id);
			setAttr("o", o);
			//获取主送和抄送单位
			service.setAttrFirstSecond(this,o.getId());
			//是否是查看详情页面
			if("detail".equals(view)){
    			if(StrKit.notBlank(o.getProcInsId())){
    				setAttr("procInsId", o.getProcInsId());
    				setAttr("defId", wfservice.getDefIdByInsId(o.getProcInsId()));
    			}
    		}
    	}else{//新增
    		OaBumph o = new OaBumph();
    		String userId = ShiroKit.getUserId();//用户主键
    		SysUser user = SysUser.dao.getById(userId);//用户对象
    		SysOrg org = SysOrg.dao.getById(user.getOrgid());//单位对象
    		o.setDocNumYear(DateUtil.getCurrentYear());
    		o.setSenderId(userId);
    		o.setSenderName(user.getName());
    		o.setSenderOrgid(org.getId());
    		o.setSenderOrgname(org.getName());
    		o.setStatus("0");
    		o.setId(UuidUtil.getUUID());
    		
    		
    		setAttr("o",o);
    		
  /*  		List<SysOrg> orgList = SysOrg.dao.find("select id  from  sys_org where  name='中心领导'");
    		List<SysUser> userList = SysUser.dao.find("select * from  sys_user where orgid='"+orgList.get(0).getId()+"'");*/
    		/*setAttr("userList",userList);*/
    	}
    	setAttr("formModelName",StringUtil.toLowerCaseFirstOne(OaBumph.class.getSimpleName()));//模型名称
    	renderIframe("edit.html");
	}
	public void getBsznEditPage(){
		
		List<SysOrg> orgList = SysOrg.dao.find("select id  from  sys_org where  name='中心领导'");
		List<SysUser> userList = SysUser.dao.find("select * from  sys_user where status='1' and orgid='"+orgList.get(0).getId()+"'");
		setAttr("userList",userList);
		
		
		List<SysOrg> orgLists = SysOrg.dao.find("select *  from  sys_org where parent_id='root'  and name !='中心领导' order by sort ");
		
		setAttr("orgLists",orgLists);
		
		
		String type = getPara("type");
		
		setAttr("type", type);
		//String parentPath = this.getRequest().getServletPath().substring(0,this.getRequest().getServletPath().lastIndexOf("/")+1); 
		
		//添加和修改
		String id = getPara("id");//修改
		String view = getPara("view");//查看
		setAttr("view", view);
		if(StrKit.notBlank(id)){//修改
			OaBumph o = OaBumph.dao.findById(id);
			setAttr("o", o);
			//获取主送和抄送单位
			service.setAttrFirstSecond(this,o.getId());
			//是否是查看详情页面
			if("detail".equals(view)){
				if(StrKit.notBlank(o.getProcInsId())){
					setAttr("procInsId", o.getProcInsId());
					setAttr("defId", wfservice.getDefIdByInsId(o.getProcInsId()));
				}
			}
		}else{//新增
			OaBumph o = new OaBumph();
			String userId = ShiroKit.getUserId();//用户主键
			SysUser user = SysUser.dao.getById(userId);//用户对象
			SysOrg org = SysOrg.dao.getById(user.getOrgid());//单位对象
			o.setDocNumYear(DateUtil.getCurrentYear());
			o.setSenderId(userId);
			o.setSenderName(user.getName());
			o.setSenderOrgid(org.getId());
			o.setSenderOrgname(org.getName());
			setAttr("o",o);
			
			/*  		List<SysOrg> orgList = SysOrg.dao.find("select id  from  sys_org where  name='中心领导'");
    		List<SysUser> userList = SysUser.dao.find("select * from  sys_user where orgid='"+orgList.get(0).getId()+"'");*/
			/*setAttr("userList",userList);*/
		}
		setAttr("formModelName",StringUtil.toLowerCaseFirstOne(OaBumph.class.getSimpleName()));//模型名称
		renderIframe("bsznedit.html");
	}
	
	
	
	
	/***
	 * 导出
	 */
	public void export(){
		String id = getPara("id");
		File file = null;
		try {
			file = service.bumphExport(id, this.getRequest());
		} catch (Exception e) {
			e.printStackTrace();
		}
		renderFile(file);
	}

	
    
    /***
     * 公文起草保存
     */
    public void draftSave(){
    	OaBumph o = getModel(OaBumph.class);
		o.setCreateOrgId(ShiroKit.getUserOrgId());
		o.setCreateUserId(ShiroKit.getUserId());
		o.setCreateOrgName(ShiroKit.getUserOrgName());
		o.setCreateUserName(ShiroKit.getUserName());
		o.setCreateTime(DateUtil.getCurrentTime());
    	String firstOrgId = getPara("firstOrgId");//主送单位
    	String secondOrgId = getPara("secondOrgId");//抄送单位
    	service.save(o, firstOrgId, secondOrgId);
    	renderSuccess();
    }
    public void draftSaveV1(){
    	OaBumph o = getModel(OaBumph.class);
    	o.setCreateOrgId(ShiroKit.getUserOrgId());
    	o.setCreateUserId(ShiroKit.getUserId());
    	o.setCreateOrgName(ShiroKit.getUserOrgName());
    	o.setCreateUserName(ShiroKit.getUserName());
    	o.setCreateTime(DateUtil.getCurrentTime());
    	String firstOrgId = getPara("firstOrgId");//主送单位
    	String secondOrgId = getPara("secondOrgId");//抄送单位
    	//String bszn=getPara("bszn");//抄送单位
    	service.saveV1(o, firstOrgId, secondOrgId);
    	//renderSuccess();
    	renderSuccess(o,"");
    }
    public void draftSaveV2(){
    	OaBumph o = getModel(OaBumph.class);
    	o.setCreateOrgId(ShiroKit.getUserOrgId());
    	o.setCreateUserId(ShiroKit.getUserId());
    	o.setCreateOrgName(ShiroKit.getUserOrgName());
    	o.setCreateUserName(ShiroKit.getUserName());
    	o.setCreateTime(DateUtil.getCurrentTime());
 
    	service.saveV2(o);
    	renderSuccess(o,"");
    }
    /***
     * 删除公文
     */
    public void delete(){
    	String id = getPara("ids");
    	service.delete(id);
    	renderSuccess("删除成功!");
    }
    
    /***
     * 提交
     */
    public void startProcess(){
    	String id = getPara("id");
    	service.startProcess(id);
    	renderSuccess("提交成功");
    }
    /***
     * 撤回
     */
    public void callBack(){
    	String id = getPara("id");
    	try{
    		service.callBack(id);
    		renderSuccess("撤回成功");
    	}catch(Exception e){
    		e.printStackTrace();
    		renderError("撤回失败");
    	}
    }
    
    /***
     * 获取内部发文待办列表页面
     */
    public void getToDoPage(){

    	renderIframe("todoList.html");
    }



    /***
     * 获取历史数据内部发文
     */
    public void getHistoryBumphPage(){

    	renderIframe("historyList.html");
    }
    
	/***
	 * 获取所有收文历史数据-----获取所有经办数据
	 */
    public void partListData(){
    	String curr = getPara("pageNumber");
    	String pageSize = getPara("pageSize");
    	Page<Record> page = service.getPartList(Integer.valueOf(curr),Integer.valueOf(pageSize), null, ShiroKit.getUsername());
    	renderPage(page.getList(),"" ,page.getTotalRow());
    }
    /************************内部发文---结束*************************************************/


	/***
	 * 公文待办列表数据
	 */
	public void bumphToDoListData(){
		String curr = getPara("pageNumber");
		String pageSize = getPara("pageSize");
		Page<Record> page = wfservice.getToDoPageByKey(Integer.valueOf(curr),Integer.valueOf(pageSize), OAConstants.DEFKEY_BUMPH, ShiroKit.getUsername(),null);
		renderPage(page.getList(),"",page.getTotalRow());
	}

	/***
	 * 取到办理页面
	 */
	public void getDoTaskPage(){
//    	String parentPath = this.getRequest().getServletPath().substring(0,this.getRequest().getServletPath().lastIndexOf("/")+1);
		String taskid = getPara("taskid");
		String id = getPara("id");
		OaBumph bumph = OaBumph.dao.findById(id);
		Record task = wfservice.getTaskRecord(taskid);
		//获取主送和抄送单位
		service.setAttrFirstSecond(this,bumph.getId());
		setAttr("o", bumph);
		if(StrKit.notBlank(bumph.getProcInsId())){
			setAttr("procInsId", bumph.getProcInsId());
			setAttr("defId", wfservice.getDefIdByInsId(bumph.getProcInsId()));
		}
		setAttr("task", task);
		render("dotask.html");
	}
	
	/**
	 * 
	* @Title: nbSendReg 
	* @Description: 拟办完成送登记人员
	* @author bkkco
	* @date 2020年10月29日下午5:08:25
	 */
	
	public  void nbSendReg() {
		OaBumph o = getModel(OaBumph.class);
		o=OaBumph.dao.findById(o.getId());
		//o.setIfSubmit("1");
		//o.update();
		String sid = getPara("sid");
		String sstep = getPara("sstep");
		String comment = getPara("comment");
		
		OaSteps steps=OaSteps.dao.findById(sid);
		steps.setRemarks(comment);
		steps.setIfcomplete("1");
		steps.setCompletetime(DateUtil.getCurrentTime());
		steps.update();
		
		NodesUtils.completeNode(steps);
		
		StepUtil.insertStepHistory(o.getId(),o.getTitle(), typeStep);
		
		String remark=(o.getNbyj()==null?"":o.getNbyj()+"\r\n")+comment+"\r\n\t\t\t\t\t\t\t\t"+ShiroKit.getUserName()+" "+DateUtil.getCurrentYMD();
		o.setNbyj(remark);
		
		List<OaSteps> uCompleteList = OaSteps.dao.find("select  * from  oa_steps where oid='"+o.getId()+"' and step='"+sstep+"' and ifcomplete='0'");
		
		//nodestart 
			
		List<OaSteps> stepsList =new ArrayList<OaSteps>();
		//nodeend
		
		
		
		
		
		if(uCompleteList.size()==0) {
			o.setStatus("2");
			OaSteps buser =new OaSteps();
			buser.setId(UuidUtil.getUUID());
			SysUser user = SysUser.dao.findById(o.getCreateUserId());
			SysOrg org = SysOrg.dao.findById(user.getOrgid());
			buser.setOid(o.getId());
			buser.setType(typeStep);
			buser.setTitle(o.getTitle());
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
			buser.setButtontype("1");
			buser.setStep("3");
			buser.save();
			//nodestart 
			stepsList.add(buser);
			
			
			
			
			List<OaSteps> nbrList = OaSteps.dao.find("select  * from  oa_steps where oid='"+o.getId()+"' and step='"+sstep+"' ");
			
			
			NodesUtils.inserNode(stepsList, o.getId(),"1");
			
			
			
			for (OaSteps oaSteps : nbrList) {
				
				NodesUtils.bundRelation(oaSteps, stepsList);
			}
			
			//nodeend
			
		}
		
		o.update();
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

		
		OaBumph o = getModel(OaBumph.class);
		OaBumph dob = OaBumph.dao.findById(o.getId());
		String newLeaderids = o.getLeadersid();
	
		
		String sid= getPara("sid");
		OaSteps cBUser = OaSteps.dao.findById(sid);
		cBUser.setIfcomplete("1");
		cBUser.setCompletetime(DateUtil.getCurrentTime());
		cBUser.update();
		StepUtil.insertStepHistory(o.getId(),o.getTitle(),typeStep);
 
		NodesUtils.completeNode(cBUser);
		 
	
		 //处理领导start
		
		if(o.getLeadersid()==null) {
			
			 List<OaSteps> dobLeaderSteps = OaSteps.dao.find("select  * from oa_steps  where oid='"+o.getId()+"' and cuserid='"+ShiroKit.getUserId()+"' and ifcomplete='0' and step='41' ");
		 		for (OaSteps oaSteps : dobLeaderSteps) {
		 			OaSteps.dao.deleteById(oaSteps.getId());
		 			NodesUtils.deleteNodes(oaSteps.getId());
				}
			
		}else  if(o.getLeadersid()!=null && !o.getLeadersid().equals(dob.getLeadersid())) {
			// String buttonType="";
			 String[] leadersArry = newLeaderids.split(",");
			 Map <String,String>leadersMap  =new HashMap<String,String>();
			 
			 for (String leadersid : leadersArry) {
				
				 leadersMap.put(leadersid, leadersid);
			}
			  List<OaSteps> dobSteps = OaSteps.dao.find("select  * from oa_steps  where oid='"+o.getId()+"' and cuserid='"+ShiroKit.getUserId()+"' and ifcomplete='0' and step='41' ");
			 
			  for (OaSteps oaSteps : dobSteps) {
				//if(newLeaderidsoaSteps.getUserid())
				  
				  if(leadersMap.get(oaSteps.getUserid())==null) {
					  OaSteps.dao.deleteById(oaSteps.getId());
						NodesUtils.deleteNodes(oaSteps.getId());
				  }
			}
				//int overLeaderStep = OaSteps.dao.find("select  * from oa_steps where  oid='"+o.getId()+"' and userid in ('4','147') and  ifcomplete='0'  and step='41'").size();
				int overLeaderStep = OaSteps.dao.find("select  * from oa_steps where  oid='"+o.getId()+"' and userid in ('5','147') and  ifcomplete='0'  and step='41'").size();
			  
			  if(overLeaderStep==0) {
					String updateSql="update oa_steps set  buttontype='1'  where  oid='"+o.getId()+"' and   step='41'";
					Db.update(updateSql);
			  }
			  
			  	int overLeaderStep1 = OaSteps.dao.find("select  * from oa_steps where  oid='"+o.getId()+"'  and ifcomplete='0'  and step='41'").size();
			  
			 /* if(overLeaderStep1==0) {
					String updateSql="update oa_steps set ifcomplete='0',buttontype='0'  where  oid='"+o.getId()+"' and step in ('42','43')";
					Db.update(updateSql);
			  }*/
			  if(overLeaderStep1!=0) {
				  String updateSql="update oa_steps set ifcomplete='0',buttontype='0'  where  oid='"+o.getId()+"' and step in ('42','43')";
				  Db.update(updateSql);
			  }
		 }
		//处理领导end
		 
		 //处理主办科室人 start
		 
		 	if(o.getHostsid()==null) {
		 		 List<OaSteps> dobHostSteps = OaSteps.dao.find("select  * from oa_steps  where oid='"+o.getId()+"' and cuserid='"+ShiroKit.getUserId()+"' and ifcomplete='0' and step='42' ");
		 		for (OaSteps oaSteps : dobHostSteps) {
		 			OaSteps.dao.deleteById(oaSteps.getId());
		 			NodesUtils.deleteNodes(oaSteps.getId());
				}
		 		
		 	}else if(o.getHostsid()!=null && !o.getHostsid().equals(dob.getHostsid())) {
				
				String newHostsid = o.getHostsid();
				 
				 String[] hostArry = newHostsid.split(",");
				 Map <String,String>hostsMap  =new HashMap<String,String>();
				 
				 for (String hostsid : hostArry) {
					
					 hostsMap.put(hostsid, hostsid);
				}
				  List<OaSteps> dobSteps = OaSteps.dao.find("select  * from oa_steps  where oid='"+o.getId()+"' and cuserid='"+ShiroKit.getUserId()+"' and ifcomplete='0' and step='42' ");
				 
				  for (OaSteps oaSteps : dobSteps) {
					//if(newLeaderidsoaSteps.getUserid())
					  
					  if(hostsMap.get(oaSteps.getUserid())==null) {
						  OaSteps.dao.deleteById(oaSteps.getId());
						  NodesUtils.deleteNodes(oaSteps.getId());
					  }
				}
				/*  int overLeaderStep = OaSteps.dao.find("select  * from oa_steps where  oid='"+o.getId()+"' and userid in ('4','147') and  ifcomplete='0'  and step='41'").size();
				  
				  if(overLeaderStep==0) {
						String updateSql="update oa_steps set  buttontype='1'  where  oid='"+o.getId()+"' and   step='41'";
						Db.update(updateSql);
				  }*/
				
			}
		 
		 //处理主办科室人 end
		 	
		 	
		 //会办处理 start
		 	
		 	
		 	if(o.getCustomersid()==null) {
		 		 List<OaSteps> dobHostSteps = OaSteps.dao.find("select  * from oa_steps  where oid='"+o.getId()+"' and cuserid='"+ShiroKit.getUserId()+"' and ifcomplete='0' and step='43' ");
		 		for (OaSteps oaSteps : dobHostSteps) {
		 			OaSteps.dao.deleteById(oaSteps.getId());
		 			NodesUtils.deleteNodes(oaSteps.getId());
				}
		 		
		 	}else if(o.getCustomersid()!=null && !o.getCustomersid().equals(dob.getCustomersid())) {
				
				String newCustomersid = o.getCustomersid();
				 
				 String[] customerArry = newCustomersid.split(",");
				 Map <String,String>customerMap  =new HashMap<String,String>();
				 
				 for (String customerid : customerArry) {
					
					 customerMap.put(customerid, customerid);
				}
				  List<OaSteps> dobSteps = OaSteps.dao.find("select  * from oa_steps  where oid='"+o.getId()+"' and cuserid='"+ShiroKit.getUserId()+"' and ifcomplete='0' and step='43' ");
				 
				  for (OaSteps oaSteps : dobSteps) {
					//if(newLeaderidsoaSteps.getUserid())
					  
					  if(customerMap.get(oaSteps.getUserid())==null) {
						  OaSteps.dao.deleteById(oaSteps.getId());
						  NodesUtils.deleteNodes(oaSteps.getId());
					  }
				}
				/*  int overLeaderStep = OaSteps.dao.find("select  * from oa_steps where  oid='"+o.getId()+"' and userid in ('4','147') and  ifcomplete='0'  and step='41'").size();
				  
				  if(overLeaderStep==0) {
						String updateSql="update oa_steps set  buttontype='1'  where  oid='"+o.getId()+"' and   step='41'";
						Db.update(updateSql);
				  }*/
				
			}
		 
		 	
		 //会办处理end	
		 	
		 	
		
		
		
		
		
		
		
		
		 List<OaSteps> stepsList =new ArrayList<OaSteps>();
		
		   
		 	
		 	
		
		
		if(o.getLeadersid()!=null && !o.getLeadersid().equals("")) {
			
			String[] leadersids = o.getLeadersid().split(",");
			
			for (String leadersid : leadersids) {
				 int size = OaSteps.dao.find("select  * from oa_steps where  oid='"+o.getId()+"' and userid='"+leadersid+"' and step='41'").size();
				if(size==0) {
					OaSteps aBUser =new OaSteps();
					SysUser user = SysUser.dao.findById(leadersid);
					SysOrg org = SysOrg.dao.findById(user.getOrgid());
					aBUser.setId(UuidUtil.getUUID());
					aBUser.setOid(o.getId());
					aBUser.setType(typeStep);
					aBUser.setTitle(o.getTitle());
					aBUser.setUserid(user.getId());
					aBUser.setUsername(user.getName());
					aBUser.setOrgid(org.getId());
					aBUser.setOrgname(org.getName());
					aBUser.setIfshow("1");//显示
					aBUser.setIfcomplete("0");
					aBUser.setCtime(DateUtil.getCurrentTime());
					aBUser.setCusername(ShiroKit.getUserName());
					aBUser.setCuserid(ShiroKit.getUserId());
					aBUser.setStepdesc(user.getSort().toString());
					aBUser.setStep("41");
					/*if(leadersid.equals("4")||leadersid.equals("147")) {
						String updateSql="update oa_steps set ifcomplete='0',buttontype='0'  where  oid='"+o.getId()+"' and userid  not in ('4','147') and step in('41','42','43') ";
						Db.update(updateSql);
						aBUser.setButtontype("1");
						
					}*/
					if(leadersid.equals("5")||leadersid.equals("147")) {
						String updateSql="update oa_steps set ifcomplete='0',buttontype='0'  where  oid='"+o.getId()+"' and userid  not in ('5','147') and step in('41','42','43') ";
						Db.update(updateSql);
						aBUser.setButtontype("1");
						
					}else {
						int size2 = OaSteps.dao.find("select  * from oa_steps where  oid='"+o.getId()+"' and userid in ('5','147') and  ifcomplete='0'  and step='41'").size();
						if(size2==0) {
							aBUser.setButtontype("1");
						}else {
							aBUser.setButtontype("0");
						}
						String updateSql="update oa_steps set ifcomplete='0',buttontype='0'  where  oid='"+o.getId()+"' and step in ('42','43')";
						Db.update(updateSql);
					}
					aBUser.save();
					
					stepsList.add(aBUser);
				}
				 
			}
		} 
		
		if(o.getHostsid()!=null && !o.getHostsid().equals("")) {
			//int leaderFinishSize = OaSteps.dao.find("select  * from oa_steps where  oid='"+o.getId()+"' and step='41' and  userid!='4' and ifcomplete='0'").size();
			int leaderFinishSize = OaSteps.dao.find("select  * from oa_steps where  oid='"+o.getId()+"' and step='41' and ifcomplete='0'").size();
			
			if(leaderFinishSize==0) {
				buttonType="1";
			}else {
				buttonType="0";
			}
			
		
		String[] hostids = o.getHostsid().split(",");
		
			for (String hostid : hostids) {
				 int size = OaSteps.dao.find("select  * from oa_steps where  oid='"+o.getId()+"' and userid='"+hostid+"' and step='42' and ifcomplete='0'").size();
				 if(size==0) {
					 OaSteps buser =new OaSteps();
					 SysUser user = SysUser.dao.findById(hostid);
					 SysOrg org = SysOrg.dao.findById(user.getOrgid());
					 buser.setId(UuidUtil.getUUID());
					 buser.setOid(o.getId());
					 buser.setType(typeStep);
					 buser.setTitle(o.getTitle());
					 buser.setUserid(user.getId());
					 buser.setUsername(user.getName());
					 buser.setOrgid(user.getOrgid());
					 buser.setOrgname(org.getName());
					 buser.setIfshow("1");//显示
					 buser.setIfcomplete("0");
					 buser.setCtime(DateUtil.getCurrentTime());
					 buser.setCusername(ShiroKit.getUserName());
					 buser.setCuserid(ShiroKit.getUserId());
					 buser.setStep("42");//主办
					 buser.setStepdesc(user.getSort().toString());
					 buser.setButtontype(buttonType);
					 
					 buser.save();
					 
					 stepsList.add(buser);
				 }
			}
		}
		
		
		if(o.getCustomersid()!=null && !o.getCustomersid().equals("")) {
			
			//int leaderFinishSize = OaSteps.dao.find("select  * from oa_steps where  oid='"+o.getId()+"' and step='41'  and  userid!='4' and ifcomplete='0'").size();
			int leaderFinishSize = OaSteps.dao.find("select  * from oa_steps where  oid='"+o.getId()+"' and step='41'  and ifcomplete='0'").size();
			
			if(leaderFinishSize==0) {
				buttonType="1";
			}else {
				buttonType="0";
			}
			
			
			
			String[] customerids = o.getCustomersid().split(",");
			
			for (String customerid : customerids) {
				//int size = OaSteps.dao.find("select  * from oa_steps where  oid='"+o.getId()+"' and userid='"+customerid+"' and step='43' and  ifcomplete='0'").size();
				int size = OaSteps.dao.find("select  * from oa_steps where  oid='"+o.getId()+"' and userid='"+customerid+"' and step='43' and ifcomplete='0'").size();
				 if(size==0) {
					 OaSteps buser =new OaSteps();
					 SysUser user = SysUser.dao.findById(customerid);
					 buser.setId(UuidUtil.getUUID());
					 buser.setOid(o.getId());
					 buser.setType(typeStep);
					 buser.setTitle(o.getTitle());
					 buser.setUserid(user.getId());
					 buser.setUsername(user.getName());
					 buser.setOrgid(user.getOrgid());
					 SysOrg org = SysOrg.dao.findById(user.getOrgid());
					 buser.setOrgname(org.getName());
					// buser.setSortnum(4);
					 buser.setSortnum("4");
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
					 stepsList.add(buser);
				 }
				
			}
		}
		
		
		//nodestart
		
		NodesUtils.inserNode(stepsList, o.getId(),cBUser.getId(),"1");
		NodesUtils.bundRelation(cBUser, stepsList);
		//nodeend
		o.setStatus("3");
		o.update();
		
	  	renderSuccess();
		
	}
	
	
public  void  specialSendCb() {
		
		String buttonType="0"; // 0不可操作，1可操作
		OaBumph o = getModel(OaBumph.class);
		String sid= getPara("sid");
		OaSteps cBUser = OaSteps.dao.findById(sid);
		cBUser.setIfcomplete("1");
		cBUser.setCompletetime(DateUtil.getCurrentTime());
		cBUser.update();
		StepUtil.insertStepHistory(o.getId(),o.getTitle(),typeStep);
 
		NodesUtils.completeNode(cBUser);
		 List<OaSteps> stepsList =new ArrayList<OaSteps>();
		
		if(o.getFirstLeaderAudit()!=null && !o.getFirstLeaderAudit().equals("")) {
			int leaderFinishSize = OaSteps.dao.find("select  * from oa_steps where  oid='"+o.getId()+"' and step='41' and ifcomplete='0'").size();
			
			if(leaderFinishSize==0) {
				buttonType="1";
			}else {
				buttonType="0";
			}
			
		
		String[] hostids = o.getFirstLeaderAudit().split(",");
		
			for (String hostid : hostids) {
				
				
				
				List<OaSteps> find = OaSteps.dao.find("select  * from oa_steps where  oid='"+o.getId()+"' and userid='"+hostid+"'");
				if(find.size()==0) {
					 OaSteps buser =new OaSteps();
					 SysUser user = SysUser.dao.findById(hostid);
					 SysOrg org = SysOrg.dao.findById(user.getOrgid());
					 buser.setId(UuidUtil.getUUID());
					 buser.setOid(o.getId());
					 buser.setType(typeStep);
					 buser.setTitle(o.getTitle());
					 buser.setUserid(user.getId());
					 buser.setUsername(user.getName());
					 buser.setOrgid(user.getOrgid());
					 buser.setOrgname(org.getName());
					 buser.setIfshow("1");//显示
					 buser.setIfcomplete("0");
					 buser.setCtime(DateUtil.getCurrentTime());
					 buser.setCusername(ShiroKit.getUserName());
					 buser.setCuserid(ShiroKit.getUserId());
					 buser.setStep("42");//主办
					 buser.setStepdesc(user.getSort().toString());
					 buser.setButtontype(buttonType);
					 
					 buser.save();
					 
					 stepsList.add(buser);
					
				}else {
					 int size = OaSteps.dao.find("select  * from oa_steps where  oid='"+o.getId()+"' and userid='"+hostid+"' and step='42' and ifcomplete='0'").size();
					 if(size==0) {
						 OaSteps buser =new OaSteps();
						 SysUser user = SysUser.dao.findById(hostid);
						 SysOrg org = SysOrg.dao.findById(user.getOrgid());
						 buser.setId(UuidUtil.getUUID());
						 buser.setOid(o.getId());
						 buser.setType(typeStep);
						 buser.setTitle(o.getTitle());
						 buser.setUserid(user.getId());
						 buser.setUsername(user.getName());
						 buser.setOrgid(user.getOrgid());
						 buser.setOrgname(org.getName());
						 buser.setIfshow("1");//显示
						 buser.setIfcomplete("0");
						 buser.setCtime(DateUtil.getCurrentTime());
						 buser.setCusername(ShiroKit.getUserName());
						 buser.setCuserid(ShiroKit.getUserId());
						 buser.setStep(find.get(0).getStep());
						 buser.setStepdesc(user.getSort().toString());
						 buser.setButtontype(buttonType);
						 
						 buser.save();
						 
						 stepsList.add(buser);
					 }
					
					
				}
				
				
		
			}
		}
		
		
		
		
		
		NodesUtils.inserNode(stepsList, o.getId(),cBUser.getId(),"1");
		NodesUtils.bundRelation(cBUser, stepsList);
		
		o.setStatus("3");
		o.update();
		
	  	renderSuccess();
	}
	
	/**
	 * 
	* @Title: ASendCb 
	* @Description: 送承办
	* @date 2020年11月7日下午6:06:20
	 */
	public  void  ASendCb() {
		
		OaBumph o = getModel(OaBumph.class);
		String flag="0";
		 OaBumph dob = OaBumph.dao.findById(o.getId());
		 String newLeaderids = o.getLeadersid();
	
		
		Db.update("update oa_steps  set ifcomplete='1' where userid='"+ShiroKit.getUserId()+"' and oid ='"+o.getId()+"' and ifcomplete='0'");
		

		 //处理领导start
		
		if(o.getLeadersid()==null) {
	 		 List<OaSteps> dobLeaderSteps = OaSteps.dao.find("select  * from oa_steps  where oid='"+o.getId()+"' and cuserid='"+ShiroKit.getUserId()+"' and ifcomplete='0' and step='41' ");
	 		for (OaSteps oaSteps : dobLeaderSteps) {
	 			OaSteps.dao.deleteById(oaSteps.getId());
	 			 NodesUtils.deleteNodes(oaSteps.getId());
			}
	 		
	 	}else if(!o.getLeadersid().equals(dob.getLeadersid())) {
			// String buttonType="";
			 String[] leadersArry = newLeaderids.split(",");
			 Map <String,String>leadersMap  =new HashMap<String,String>();
			 
			 for (String leadersid : leadersArry) {
				
				 leadersMap.put(leadersid, leadersid);
			}
			  List<OaSteps> dobSteps = OaSteps.dao.find("select  * from oa_steps  where oid='"+o.getId()+"' and cuserid='"+ShiroKit.getUserId()+"' and ifcomplete='0' and step='41' ");
			 
			  for (OaSteps oaSteps : dobSteps) {
				//if(newLeaderidsoaSteps.getUserid())
				  
				  if(leadersMap.get(oaSteps.getUserid())==null) {
					  OaSteps.dao.deleteById(oaSteps.getId());
					  NodesUtils.deleteNodes(oaSteps.getId());
				  }
			}
				int overLeaderStep = OaSteps.dao.find("select  * from oa_steps where  oid='"+o.getId()+"' and userid in ('5','147') and  ifcomplete='0'  and step='41'").size();
			  
			  if(overLeaderStep==0) {
					String updateSql="update oa_steps set  buttontype='1'  where  oid='"+o.getId()+"' and   step='41'";
					Db.update(updateSql);
			  }
			  
			  	int overLeaderStep1 = OaSteps.dao.find("select  * from oa_steps where  oid='"+o.getId()+"'  and ifcomplete='0'  and step='41'").size();
			  
			  if(overLeaderStep1==0) {
					String updateSql="update oa_steps set ifcomplete='0',buttontype='0'  where  oid='"+o.getId()+"' and step in ('42','43')";
					Db.update(updateSql);
			  }
		 }
		//处理领导end
		 
		 //处理主办科室人 start
		 
		 	if(o.getHostsid()==null) {
		 		 List<OaSteps> dobHostSteps = OaSteps.dao.find("select  * from oa_steps  where oid='"+o.getId()+"' and cuserid='"+ShiroKit.getUserId()+"' and ifcomplete='0' and step='42' ");
		 		for (OaSteps oaSteps : dobHostSteps) {
		 			OaSteps.dao.deleteById(oaSteps.getId());
		 			 NodesUtils.deleteNodes(oaSteps.getId());
				}
		 		
		 	}else if(o.getHostsid()!=null && !o.getHostsid().equals(dob.getHostsid())) {
				
				String newHostsid = o.getHostsid();
				 
				 String[] hostArry = newHostsid.split(",");
				 Map <String,String>hostsMap  =new HashMap<String,String>();
				 
				 for (String hostsid : hostArry) {
					
					 hostsMap.put(hostsid, hostsid);
				}
				  List<OaSteps> dobSteps = OaSteps.dao.find("select  * from oa_steps  where oid='"+o.getId()+"' and cuserid='"+ShiroKit.getUserId()+"' and ifcomplete='0' and step='42' ");
				 
				  for (OaSteps oaSteps : dobSteps) {
					//if(newLeaderidsoaSteps.getUserid())
					  
					  if(hostsMap.get(oaSteps.getUserid())==null) {
						  OaSteps.dao.deleteById(oaSteps.getId());
						  NodesUtils.deleteNodes(oaSteps.getId());
					  }
				}
				/*  int overLeaderStep = OaSteps.dao.find("select  * from oa_steps where  oid='"+o.getId()+"' and userid in ('4','147') and  ifcomplete='0'  and step='41'").size();
				  
				  if(overLeaderStep==0) {
						String updateSql="update oa_steps set  buttontype='1'  where  oid='"+o.getId()+"' and   step='41'";
						Db.update(updateSql);
				  }*/
				
			}
		 
		 //处理主办科室人 end
		 	
		 	
		 //会办处理 start
		 	
		 	
		 	if(o.getCustomersid()==null) {
		 		 List<OaSteps> dobHostSteps = OaSteps.dao.find("select  * from oa_steps  where oid='"+o.getId()+"' and cuserid='"+ShiroKit.getUserId()+"' and ifcomplete='0' and step='43' ");
		 		for (OaSteps oaSteps : dobHostSteps) {
		 			OaSteps.dao.deleteById(oaSteps.getId());
		 			 NodesUtils.deleteNodes(oaSteps.getId());
				}
		 		
		 	}else if(o.getCustomersid()!=null && !o.getCustomersid().equals(dob.getCustomersid())) {
				
				String newCustomersid = o.getCustomersid();
				 
				 String[] customerArry = newCustomersid.split(",");
				 Map <String,String>customerMap  =new HashMap<String,String>();
				 
				 for (String customerid : customerArry) {
					
					 customerMap.put(customerid, customerid);
				}
				  List<OaSteps> dobSteps = OaSteps.dao.find("select  * from oa_steps  where oid='"+o.getId()+"' and cuserid='"+ShiroKit.getUserId()+"' and ifcomplete='0' and step='43' ");
				 
				  for (OaSteps oaSteps : dobSteps) {
					//if(newLeaderidsoaSteps.getUserid())
					  
					  if(customerMap.get(oaSteps.getUserid())==null) {
						  OaSteps.dao.deleteById(oaSteps.getId());
						  NodesUtils.deleteNodes(oaSteps.getId());
					  }
				}
				/*  int overLeaderStep = OaSteps.dao.find("select  * from oa_steps where  oid='"+o.getId()+"' and userid in ('4','147') and  ifcomplete='0'  and step='41'").size();
				  
				  if(overLeaderStep==0) {
						String updateSql="update oa_steps set  buttontype='1'  where  oid='"+o.getId()+"' and   step='41'";
						Db.update(updateSql);
				  }*/
				
			}
		 
		 	
		 //会办处理end	
		 	
		 	List<OaSteps> stepsList =new ArrayList<>();
		
		
		
	 
		
		if(o.getLeadersid()!=null && !o.getLeadersid().equals(""))	{
 
		String[] leadersids = o.getLeadersid().split(",");
		
			for (String leadersid : leadersids) {
				 int size = OaSteps.dao.find("select  * from oa_steps where  oid='"+o.getId()+"' and userid='"+leadersid+"' and step='41' ").size();
				if(size==0) {
					
					flag="1";
					OaSteps aBUser =new OaSteps();
					SysUser user = SysUser.dao.findById(leadersid);
					SysOrg org = SysOrg.dao.findById(user.getOrgid());
					aBUser.setId(UuidUtil.getUUID());
					aBUser.setOid(o.getId());
					aBUser.setTitle(o.getTitle());
					aBUser.setType(typeStep);
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
						String updateSql="update oa_steps set ifcomplete='0',buttontype='0'  where  oid='"+o.getId()+"' and userid  not in ('5','147') and step in('41','42','43') ";
						Db.update(updateSql);
						aBUser.setButtontype("1");
						
					}else {
						int size2 = OaSteps.dao.find("select  * from oa_steps where  oid='"+o.getId()+"' and userid in ('5','147') and  ifcomplete='0'  and step='41'").size();
						if(size2==0) {
							aBUser.setButtontype("1");
						}else {
							aBUser.setButtontype("0");
						}
						String updateSql="update oa_steps set ifcomplete='0',buttontype='0'  where  oid='"+o.getId()+"' and step in ('42','43')";
						Db.update(updateSql);
					}
					
					aBUser.save();
					stepsList.add(aBUser);
				}
				 
			}
		}	
		
		
		if(o.getHostsid()!=null && !o.getHostsid().equals("")) {
			String   buttonType="";
			int leaderFinishSize = OaSteps.dao.find("select  * from oa_steps where  oid='"+o.getId()+"' and step='41' and ifcomplete='0'").size();
			
			if(leaderFinishSize==0) {
				buttonType="1";
			}else {
				buttonType="0";
			}
			
			
			String[] hostids = o.getHostsid().split(",");
			
			for (String hostid : hostids) {
				 int size = OaSteps.dao.find("select  * from oa_steps where  oid='"+o.getId()+"' and userid='"+hostid+"' and step='42'  ").size();
				 if(size==0) {
						flag="1";
					 OaSteps buser =new OaSteps();
					 SysUser user = SysUser.dao.findById(hostid);
					 SysOrg org = SysOrg.dao.findById(user.getOrgid());
					 buser.setId(UuidUtil.getUUID());
					 buser.setOid(o.getId());
					 buser.setUserid(user.getId());
					 buser.setUsername(user.getName());
					 buser.setOrgid(user.getOrgid());
					 buser.setTitle(o.getTitle());
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
					 stepsList.add(buser);
				 }
			}
		}
		
		if( o.getCustomersid()!=null &&  !o.getCustomersid().equals("")) {
			
			String   buttonType="";
			int leaderFinishSize = OaSteps.dao.find("select  * from oa_steps where  oid='"+o.getId()+"' and step='41' and ifcomplete='0'").size();
			
			if(leaderFinishSize==0) {
				buttonType="1";
			}else {
				buttonType="0";
			}
		
			String[] customerids = o.getCustomersid().split(",");
			
			for (String customerid : customerids) {
				int size = OaSteps.dao.find("select  * from oa_steps where  oid='"+o.getId()+"' and userid='"+customerid+"' and step='43'").size();
				 if(size==0) {
						flag="1";
					 OaSteps buser =new OaSteps();
					 SysUser user = SysUser.dao.findById(customerid);
					 buser.setId(UuidUtil.getUUID());
					 buser.setOid(o.getId());
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
					 buser.setTitle(o.getTitle());
					 
					 buser.setStepdesc(user.getSort().toString());
					 buser.setButtontype(buttonType);
					 buser.save();
					 
					 stepsList.add(buser);
				 }
				
			}
		}
		
		//NodesUtils.addNodes(o.getId(), stepsList);
		
		//nodestart
		
				  OaSteps findFirst = OaSteps.dao.findFirst("select * from oa_steps where step='3'  and  oid='"+o.getId()+"'");
		
		
		
				NodesUtils.inserNode(stepsList, o.getId(),findFirst.getId(),"1");
				
				
				
				List<OaNodes> nodesList = OaNodes.dao.find("select  * from oa_nodes where  oid='"+o.getId()+"' and  parentid='"+findFirst.getId()+"'");
				
				List nodesLists =new ArrayList<OaSteps>();
				
				for (OaNodes oaNodes : nodesList) {
					
					
					OaSteps step = OaSteps.dao.findById(oaNodes.getId());
					
					if(step!=null) {
						
						nodesLists.add(step);
					}
				}
				
				
				
				NodesUtils.bundRelation(findFirst, nodesLists);
				//nodeend

		if(flag.equals("1")) {
			
			
			Db.update("update oa_steps  set ifcomplete='1' where userid='"+ShiroKit.getUserId()+"' and oid ='"+o.getId()+"' and ifcomplete='0'");
		}
		
		
		
		o.setStatus("3");
		o.update();
	  	renderSuccess();
		
		
	}
	
	
	
	
	
	/**
	 * 
	* @Title: leaderPs 
	* @Description: 领导批示
	* @date 2020年10月30日上午10:13:30
	 */
	
	public void leaderPs() {
		OaBumph o = getModel(OaBumph.class);
		String comment=getPara("comment");
		String  sid=getPara("sid");
		OaSteps obu = OaSteps.dao.findById(sid);
		obu.setIfcomplete("1");
		obu.setCompletetime(DateUtil.getCurrentTime());
		obu.setRemarks(comment);
		obu.update();
		
		
		NodesUtils.completeNode(obu);
		StepUtil.insertStepHistory(o.getId(),o.getTitle(),typeStep);
		
	
	//	o.setContent((o.getContent()==null?"":o.getContent()+"\r\n")+comment+"\r\n\t\t\t\t\t\t\t\t\t\t"+ShiroKit.getUserName()+" "+DateUtil.getCurrentYMD());
		
		
		List<OaSteps> oss = OaSteps.dao.find("select  * from oa_steps  where oid='"+o.getId()+"' and step='41' and  ifcomplete='1' order by stepdesc ");
		String remarks="";
		
		for (OaSteps oaStep : oss) {
			remarks=remarks+"\r\n"+oaStep.getRemarks()+"\r\n\t\t\t\t\t"+oaStep.getUsername()+" "+oaStep.getCompletetime().substring(0, 10);
		}
		
		
		o.setContent(remarks);
		
	
//		List<OaSteps> leadersPS = OaSteps.dao.find("select  * from  oa_steps   where oid='"+o.getId()+"' and   step ='41'  and userid in ('4','147')") ;
		List<OaSteps> leadersPS = OaSteps.dao.find("select  * from  oa_steps   where oid='"+o.getId()+"' and   step ='41'  and userid in ('5','147')") ;
		
		if(leadersPS.size()==0) {
			//int size = OaSteps.dao.find("select  * from  oa_steps   where oid='"+o.getId()+"' and   step ='41'  and userid !='4'  and ifcomplete='0'").size();
			int size = OaSteps.dao.find("select  * from  oa_steps   where oid='"+o.getId()+"' and   step ='41'   and ifcomplete='0'").size();
			if(size==0) {
				Db.update("update oa_steps set buttontype='1' where oid='"+o.getId()+"'");
			}
			
		}else {
			//int size = OaSteps.dao.find("select  * from  oa_steps   where oid='"+o.getId()+"' and   step ='41'  and userid in ('4','147')  and ifcomplete='0'").size() ;
			int size = OaSteps.dao.find("select  * from  oa_steps   where oid='"+o.getId()+"' and   step ='41'  and userid in ('5','147')  and ifcomplete='0'").size() ;
			
			if(size==0) {
				Db.update("update oa_steps set buttontype='1' where oid='"+o.getId()+"' and   step ='41'");
			}
			
			
			//int size2 = OaSteps.dao.find("select  * from  oa_steps   where oid='"+o.getId()+"' and   step ='41'  and userid !='4'  and ifcomplete='0'").size() ;
			int size2 = OaSteps.dao.find("select  * from  oa_steps   where oid='"+o.getId()+"' and   step ='41'   and ifcomplete='0'").size() ;
			
			if(size2==0) {
				Db.update("update oa_steps set buttontype='1' where oid='"+o.getId()+"'");
			}
		}
		
		
		
		
		
		
		//int size = OaSteps.dao.find("select  * from  oa_steps   where oid='"+o.getId()+"' and ifcomplete='0'  and step like '4%'").size();
		 List<OaSteps> find = OaSteps.dao.find("select  * from  oa_steps   where oid='"+o.getId()+"' and ifcomplete='0'  and step like '4%'");
		
		 int size =find.size();
		 List<OaSteps> stepsList=new ArrayList<>();
		
		if(size==0) {
			o.setStatus("4");
			OaBumph os = OaBumph.dao.findById(o.getId());
			SysUser user = SysUser.dao.findById(os.getCreateUserId());
			SysOrg org = SysOrg.dao.findById(user.getOrgid());
			OaSteps buser =new OaSteps();
			buser.setId(UuidUtil.getUUID());
			buser.setOid(o.getId());
			buser.setType(typeStep);
			buser.setTitle(o.getTitle());
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
			buser.setStep("5");
			buser.save();
			
			//nodestart
			
			
			
			 List<OaSteps> startNodes = OaSteps.dao.find("select  * from  oa_steps   where oid='"+o.getId()+"'  and step like '4%'");
			stepsList.add(buser);
			
			NodesUtils.inserNode(stepsList, o.getId(),"1");
			
			for (OaSteps oaSteps : startNodes) {
				
				NodesUtils.bundRelation(oaSteps, stepsList);
				
			}
			
			
			//nodeend
			
			
			
			
			
		}
		
	
		
		
		o.update();
		
		renderSuccess();
		/*String comment=getPara("comment");
		
		OaBumphUser obu = OaBumphUser.dao.findById(getPara("uid"));
		obu.setIfcomplete("1");
		obu.setCompletetime(DateUtil.getCurrentTime());
		obu.setLooktime(DateUtil.getCurrentTime());
		obu.setIflooked("1");
		obu.setLeaderremark(comment);
		obu.update();
		
		
		
		OaBumph o = getModel(OaBumph.class);
		o.setContent((o.getContent()==null?"":o.getContent()+"\r\n")+comment+"("+ShiroKit.getUserName()+")");
		o.update();
		
 
		 OaBumph os = OaBumph.dao.findById(o.getId());
		 OaBumphUser buser =new OaBumphUser();
		 buser.setId(UuidUtil.getUUID());
		 buser.setBumphid(o.getId());
		 buser.setUserid(os.getSenderId());
		 buser.setUsername(os.getSenderName());
		 buser.setOrgid(os.getSenderOrgid());
		 buser.setOrgname(os.getSenderOrgname());
		 buser.setLookornot("1");
		 buser.setSortnum(5);
		 buser.setLooked("0");
		 buser.setIfshow("1");//显示
		 buser.setIfcomplete("1");
		 //buser.setCompletetime(DateUtil.getCurrentTime());
		 buser.setCreatetime(DateUtil.getCurrentTime());
		 buser.setCreateuser(ShiroKit.getUserName());
		 buser.setCreateuserid(ShiroKit.getUserId());
		 buser.setStep("31");
		 buser.save();
	  	renderSuccess();*/
	}
	
	
	public  void findSons(String  bumphId,String  parentId ,List<OaBumphUser> list){
		
		List<OaBumphUser> lists = OaBumphUser.dao.find("select * from  oa_bumph_user where  bumphid='"+bumphId+"' and  parentid='"+parentId+"'");
		
		for (OaBumphUser oaBumphUser : lists) {
			list.add(oaBumphUser);
			findSons(bumphId,oaBumphUser.getId(),list);
		}
		
		 
	}
	
	/**
	 * 
	* @Title: overStep 
	* @Description: 主会办完结 
	* @author bkkco
	* @date 2020年10月30日上午11:16:51
	 */
	
	public void overStep() {
		
		OaBumph o = getModel(OaBumph.class);
		String sid =getPara("sid");
		String sstep=getPara("sstep");
		OaSteps obu = OaSteps.dao.findById(sid);
		String comment=getPara("comment");
		obu = OaSteps.dao.findById(sid);
		obu.setIfcomplete("1");
		obu.setCompletetime(DateUtil.getCurrentTime());
		obu.setRemarks(comment);
		obu.update();
		
		NodesUtils.completeNode(obu);
		
		StepUtil.insertStepHistory(o.getId(),o.getTitle(),typeStep);
		
		String remark="";
		
		//List<OaSteps> zbList = OaSteps.dao.find("select  * from  oa_steps  where oid='"+o.getId()+"' and ifcomplete='1'  and step ='42' order by  stepdesc  ");
		List<OaSteps> zbList = OaSteps.dao.find("select  * from  oa_steps  where oid='"+o.getId()+"' and completetime is not null and step ='42' order by  completetime  ");
		if(zbList.size()>0) {
			for (OaSteps zb : zbList) {
				
				//if(zb.getRemarks()!=null && !zb.getRemarks().trim().equals("")) {
					remark+=zb.getRemarks()+"\r\n\t\t\t\t\t"+zb.getUsername()+" "+zb.getCompletetime().substring(0,10)+"\r\n";
				//}
				
			}
		}
		//List<OaSteps> cbList = OaSteps.dao.find("select  * from  oa_steps  where oid='"+o.getId()+"' and ifcomplete='1'  and step ='43' order by  stepdesc  ");
		List<OaSteps> cbList = OaSteps.dao.find("select  * from  oa_steps  where oid='"+o.getId()+"' and completetime is not null and step ='43' order by  completetime  ");
		if(cbList.size()>0) {
			for (OaSteps cb : cbList) {
				
				//if(cb.getRemarks()!=null && !cb.getRemarks().trim().equals("")) {
					remark+=cb.getRemarks()+"\r\n\t\t\t\t\t"+cb.getUsername()+" "+cb.getCompletetime().substring(0,10)+"\r\n";
				//}
				
			}
		}
		
		o.setClqk(remark);
		
		
		
		if(obu.getParentid()==null ||obu.getParentid().equals("")) {
 
			
			//int size = OaSteps.dao.find("select  * from  oa_steps   where oid='"+o.getId()+"' and ifcomplete='0'  and step like '4%'").size();
			
			List<OaSteps> stepsList=new ArrayList<>();
			 List<OaSteps> find = OaSteps.dao.find("select  * from  oa_steps   where oid='"+o.getId()+"' and ifcomplete='0'  and step like '4%'");
			 int size =find.size();
			if(size==0) {
				o.setStatus("4");
				OaBumph os = OaBumph.dao.findById(o.getId());
				SysUser user = SysUser.dao.findById(os.getCreateUserId());
				SysOrg org = SysOrg.dao.findById(user.getOrgid());
				OaSteps buser =new OaSteps();
				buser.setId(UuidUtil.getUUID());
				buser.setOid(o.getId());
				buser.setType(typeStep);
				buser.setTitle(o.getTitle());
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
				buser.setStep("5");
				buser.setButtontype("1");
				buser.save();
				
				//nodestart
				// List<OaSteps> startNodes = OaSteps.dao.find("select  * from  oa_steps   where oid='"+o.getId()+"'  and step like '4%'");
				 List<OaSteps> startNodes = OaSteps.dao.find("select  * from  oa_steps where  step like '4%'  and  id in (select  id from  oa_nodes where oid='"+o.getId()+"'  and endnode='')");
				
				stepsList.add(buser);
				NodesUtils.inserNode(stepsList, o.getId(),"1");
					
				for (OaSteps oaSteps : startNodes) {
					NodesUtils.bundRelation(oaSteps, stepsList);
				}
				//nodeend
				
				
			}
			
		//	o.update();
		}else {
			
		
			
			
			OaSteps nowBu = OaSteps.dao.findById(sid);
			List<OaSteps> sonList = OaSteps.dao.find("select  * from oa_steps where oid='"+nowBu.getOid()+"' and parentid='"+nowBu.getParentid()+"'  and ifcomplete='0' ");
			List<OaSteps> endNodes=new ArrayList<>();
			
			int size = sonList.size();
			if(size==0) {
				
				OaSteps pBu = OaSteps.dao.findById(nowBu.getParentid());
				
				pBu.setButtontype("1");
				pBu.setIfcomplete("1");
				pBu.update();
				
				
				
				
				OaSteps buser =new OaSteps();
				buser.setId(UuidUtil.getUUID());
				buser.setOid(o.getId());
				buser.setType(typeStep);
				buser.setTitle(o.getTitle());
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
				buser.setButtontype("1");
				buser.save();
				endNodes.add(buser);
				//nodestart
				
			   OaSteps findById = OaSteps.dao.findById(nowBu.getParentid());
				
				NodesUtils.completeNode(findById);
				List<OaSteps> find = OaSteps.dao.find("select  * from oa_steps where oid='"+nowBu.getOid()+"' and parentid='"+nowBu.getParentid()+"'");
				for (OaSteps oaSteps : find) {
					NodesUtils.bundRelation(oaSteps,endNodes);
				}
				
				
				NodesUtils.updateBundRelation(o.getId(),nowBu.getParentid(), buser);
				
				//nodeend
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
		OaBumph o = getModel(OaBumph.class);
		
		String [] addPIds=getPara("commonuserid").split(",");
		String sid=getPara("sid");
		String sstep = getPara("sstep");
		String comment=getPara("comment");
		
		OaSteps obu = OaSteps.dao.findById(sid);
		//obu.setIfcomplete("1");
		obu.setButtontype("0");
		obu.setCompletetime(DateUtil.getCurrentTime());
		obu.setRemarks(comment);
		obu.update();
	//	StepUtil.insertStepHistory(o.getId(),o.getTitle(),typeStep);
		
		
		NodesUtils.completeNode(obu);
		
	//	int step=getPara("step").length()>1? Integer.valueOf(getPara("step"))+1:Integer.valueOf(getPara("step"))*10+1;
		
		String remark="";
		
		//List<OaSteps> zbList = OaSteps.dao.find("select  * from  oa_steps  where oid='"+o.getId()+"' and ifcomplete='1'  and step ='42' order by  ctime desc ");
		List<OaSteps> zbList = OaSteps.dao.find("select  * from  oa_steps  where oid='"+o.getId()+"' and completetime is not null  and step ='42' order by  completetime  ");
		if(zbList.size()>0) {
			for (OaSteps zb : zbList) {
				
				//if(zb.getRemarks()!=null && !zb.getRemarks().trim().equals("")) {
					remark+=zb.getRemarks()+"\r\n\t\t\t\t\t\t\t\t"+zb.getUsername()+" "+DateUtil.getCurrentYMD()+"\r\n";
				//}
				
			}
		}
		//List<OaSteps> cbList = OaSteps.dao.find("select  * from  oa_steps  where oid='"+o.getId()+"' and ifcomplete='1'  and step ='43' order by  completetime desc ");
		 List<OaSteps> cbList = OaSteps.dao.find("select  * from  oa_steps  where oid='"+o.getId()+"' and  completetime is not null  and step ='43' order by  completetime  ");
		if(zbList.size()>0) {
			for (OaSteps cb : cbList) {
				
				//if(cb.getRemarks()!=null && !cb.getRemarks().trim().equals("")) {
					remark+=cb.getRemarks()+"\r\n\t\t\t\t\t\t\t\t"+cb.getUsername()+" "+DateUtil.getCurrentYMD()+"\r\n";
				//}
				
			}
		}
		
		
		
		//String clqk=(o.getClqk()==null?"":o.getClqk()+"\r\n")+comment+"("+ShiroKit.getUserName()+")";
		o.setClqk(remark);
		
		o.update();
		List<OaSteps> stepsList =new ArrayList<>();
	
	//	List<OaSteps> stepsList =new ArrayList<>();
		for (String addid : addPIds) {
			OaSteps buser =new OaSteps();
			SysUser user = SysUser.dao.findById(addid);
			SysOrg org = SysOrg.dao.findById(user.getOrgid());
			buser.setId(UuidUtil.getUUID());
			buser.setOid(o.getId());
			buser.setType(typeStep);
			buser.setTitle(o.getTitle());
			buser.setUserid(user.getId());
			buser.setUsername(user.getName());
			buser.setOrgid(org.getId());
			buser.setOrgname(org.getName());
			buser.setParentid(sid);
			buser.setIfshow("1");//显示
			buser.setIfcomplete("0");
			//buser.setCompletetime(DateUtil.getCurrentTime());
			buser.setCtime(DateUtil.getCurrentTime());
			buser.setCusername(ShiroKit.getUserName());
			buser.setCuserid(ShiroKit.getUserId());
			buser.setStep(sstep);
			buser.setStepdesc(user.getSort().toString());
			buser.setButtontype("1");
			buser.save();
			stepsList.add(buser);
			//nodestart
			//stepsList.add(buser);
			//NodesUtils.inserNode(stepsList, o.getId());
			//NodesUtils.bundRelation(obu, stepsList);
			
			//nodeend
			
			
		}
		
		
		//nodestart
		
		NodesUtils.inserNode(stepsList, o.getId(),sid,"2");
		NodesUtils.bundRelation(obu, stepsList);
		//nodeend
		renderSuccess();
	
	}
	/**
	 * 
	* @Title: FormToFile 
	* @Description: 表单归档
	* @date 2020年11月2日上午10:27:58
	 */
	
	public void FormToFile() {
		OaBumph o=getModel(OaBumph.class);
		//o.setIfComplete("2");//已归档
		o.setStatus("5");
		o.update();
		//String uid = getPara("uid");
		//OaBumphUser ouser = OaBumphUser.dao.findById(uid);
		//		ouser.setIfcomplete("1");
	//	ouser.setCompletetime(DateUtil.getCurrentTime());
		String sid = getPara("sid");
		OaSteps ouser = OaSteps.dao.findById(sid);
		ouser.setIfcomplete("1");
		ouser.setCompletetime(DateUtil.getCurrentTime());
		ouser.update();
		StepUtil.insertStepHistory(o.getId(),o.getTitle(),typeStep);
		NodesUtils.completeNode(ouser);
		
	 	renderSuccess("归档成功!");
	}
	
	/**
	 * 
	* @Title: listToFile 
	* @Description: 列表归档
	* @date 2020年11月2日上午10:28:18
	 */
	
	
	public  void listToFile() {
		OaBumph o = OaBumph.dao.findById(getPara("id"));
		
		//o.setIfComplete("2");//已归档
		o.setStatus("5");
		o.update();
		
	//	Db.update("update  oa_bumph_user  set ifcomplete ='1',completetime='"+DateUtil.getCurrentTime()+"'  where bumphid='"+o.getId()+"' and ifcomplete ='0'");
		StepUtil.insertStepHistory(o.getId(),o.getTitle(),typeStep);
    	renderSuccess("归档成功!");
	}
	
	
	/**
	 * 
	* @Title: draftSaveV2 
	* @Description: 送拟办
	* @date 2020年11月7日下午4:51:24
	 */
	 public void toNb(){
	    	OaBumph o = getModel(OaBumph.class);
	    	o.setCreateOrgId(ShiroKit.getUserOrgId());
	    	o.setCreateUserId(ShiroKit.getUserId());
	    	o.setCreateOrgName(ShiroKit.getUserOrgName());
	    	o.setCreateUserName(ShiroKit.getUserName());
	    	o.setCreateTime(DateUtil.getCurrentTime());
	 
	    	service.saveNb(o);
	    	renderSuccess(o,"");
	    }
	
	public void  listSelectDatas() {
		
		String curr = getPara("pageNumber");
    	String pageSize = getPara("pageSize");
    	Page<OaBumph> page = OaBumph.dao.getSelectPage(Integer.valueOf(curr),Integer.valueOf(pageSize));
    	renderPage(page.getList(),"" ,page.getTotalRow());
		
		
	} 

	 
	/*
	 * 收文历史列表
	 */
	public void listHistory() {
		
		renderIframe("listHistory.html");
		
	}
	
	/*
	 *收文历史数据查询 
	 */

	public void  listHistoryData() throws UnsupportedEncodingException {

    	String curr = getPara("pageNumber");
    	String pageSize = getPara("pageSize");
    	String type = getPara("type");
    	
		String endTime = getPara("endTime","");
		String startTime = getPara("startTime","");
		/*String rdnum = getPara("rdnum","");
		String rtitle = getPara("rtitle","");
		String rFileNo = getPara("rFileNo","");*/
		
		String rdnum = java.net.URLDecoder.decode(getPara("rdnum",""),"UTF-8");
		String rtitle = java.net.URLDecoder.decode(getPara("rtitle",""),"UTF-8");
		String rFileNo = java.net.URLDecoder.decode(getPara("rFileNo",""),"UTF-8");
    	
    	Page<OaBumph> page = OaBumph.dao.getHistoryPage(Integer.valueOf(curr),Integer.valueOf(pageSize),type, rdnum,  rtitle,  rFileNo, startTime,  endTime);
    	renderPage(page.getList(),"" ,page.getTotalRow());
    
	}
	
	
	
	
	
	/***
	 * 收文历史编辑界面
	 */
	public void getEditPageFromRhistory(){
		
		List<SysOrg> orgList = SysOrg.dao.find("select id  from  sys_org where  name='中心领导'");
		List<SysUser> userList = SysUser.dao.find("select * from  sys_user where status='1'  and  orgid='"+orgList.get(0).getId()+"' order by sort");
		setAttr("userList",userList);
		
		
		List<SysOrg> orgLists = SysOrg.dao.find("select *  from  sys_org where parent_id='root'  and name !='中心领导' order by sort ");
		
		setAttr("orgLists",orgLists);
		
		
	
		keepPara("sid");
		OaSteps ostep = OaSteps.dao.findById(getPara("sid"));
		setAttr("remark", ostep.getRemarks());
	 
		//String parentPath = this.getRequest().getServletPath().substring(0,this.getRequest().getServletPath().lastIndexOf("/")+1); 

		//添加和修改
    	String id = getPara("id");//修改
		String view = getPara("view");//查看
		setAttr("view", view);
		if(StrKit.notBlank(id)){//修改
			OaBumph o = OaBumph.dao.findById(id);
			setAttr("o", o);
			
			List<SysUser> nbr = SysUser.dao.find("select * from  sys_user where id='"+o.getNbrids()+"' and position in ('2','6') order by sort");
			
			setAttr("nbr",nbr);
    	}
		

		
		

    	setAttr("formModelName",StringUtil.toLowerCaseFirstOne(OaBumph.class.getSimpleName()));//模型名称
    	renderIframe("rHistoryEdit.html");
	}
	 /*
	  * 领导保存意见
	  */
	 
	
	public void leaderRsaveSuggerstion() {
	
		
	 OaBumph o = getModel(OaBumph.class);
	 o=OaBumph.dao.findById(o.getId());
	 String sid = getPara("sid");
	 String comment=getPara("comment");
	 
	 OaSteps ostep = OaSteps.dao.findById(sid);
	 
	 ostep.setRemarks(comment);
	 ostep.update();
	 
		List<OaSteps> oss = OaSteps.dao.find("select  * from oa_steps  where oid='"+o.getId()+"' and step='41' and  ifcomplete='1' order by stepdesc ");
		String remarks="";
		
		for (OaSteps oaStep : oss) {
			remarks=remarks+"\r\n"+oaStep.getRemarks()+"\r\n\t\t\t\t\t"+oaStep.getUsername()+" "+oaStep.getCompletetime().substring(0, 10);
		}
		
		o.setContent(remarks);
		o.update();
		
		renderSuccess();
	}
	/*
	 * 保存修改
	 */
	
	public  void eidtSave() {
		String flag="0";
		 OaBumph o = getModel(OaBumph.class);
		 
		 OaBumph dob = OaBumph.dao.findById(o.getId());
		 String newLeaderids = o.getLeadersid();
		 
		 String buttonType="";
		 //处理领导start
		 
		 
		 

		 	if(o.getLeadersid()==null) {
		 		 List<OaSteps> dobLeaderSteps = OaSteps.dao.find("select  * from oa_steps  where oid='"+o.getId()+"' and cuserid='"+ShiroKit.getUserId()+"' and ifcomplete='0' and step='41' ");
		 		for (OaSteps oaSteps : dobLeaderSteps) {
		 			OaSteps.dao.deleteById(oaSteps.getId());
		 			NodesUtils.deleteNodes(oaSteps.getId());
				}
		 		
		 	}else if(o.getLeadersid()!=null && !o.getLeadersid().equals(dob.getLeadersid())) {
			 
			 String[] leadersArry = newLeaderids.split(",");
			 Map <String,String>leadersMap  =new HashMap<String,String>();
			 
			 for (String leadersid : leadersArry) {
				
				 leadersMap.put(leadersid, leadersid);
			}
			  List<OaSteps> dobSteps = OaSteps.dao.find("select  * from oa_steps  where oid='"+o.getId()+"' and cuserid='"+ShiroKit.getUserId()+"' and ifcomplete='0' and step='41' ");
			 
			  for (OaSteps oaSteps : dobSteps) {
				//if(newLeaderidsoaSteps.getUserid())
				  
				  if(leadersMap.get(oaSteps.getUserid())==null) {
					  OaSteps.dao.deleteById(oaSteps.getId());
					  NodesUtils.deleteNodes(oaSteps.getId());
				  }
			}
				int overLeaderStep = OaSteps.dao.find("select  * from oa_steps where  oid='"+o.getId()+"' and userid in ('4','147') and  ifcomplete='0'  and step='41'").size();
			  
			  if(overLeaderStep==0) {
					String updateSql="update oa_steps set  buttontype='1'  where  oid='"+o.getId()+"' and   step='41'";
					Db.update(updateSql);
			  }
			  
			  	int overLeaderStep1 = OaSteps.dao.find("select  * from oa_steps where  oid='"+o.getId()+"'  and ifcomplete='0'  and step='41'").size();
			  
			  if(overLeaderStep1==0) {
					String updateSql="update oa_steps set ifcomplete='0',buttontype='0'  where  oid='"+o.getId()+"' and step in ('42','43')";
					Db.update(updateSql);
			  }
		 }
		//处理领导end
		 
		 //处理主办科室人 start
		 
		 	if(o.getHostsid()==null) {
		 		 List<OaSteps> dobHostSteps = OaSteps.dao.find("select  * from oa_steps  where oid='"+o.getId()+"' and cuserid='"+ShiroKit.getUserId()+"' and ifcomplete='0' and step='42' ");
		 		for (OaSteps oaSteps : dobHostSteps) {
		 			OaSteps.dao.deleteById(oaSteps.getId());
		 			NodesUtils.deleteNodes(oaSteps.getId());
				}
		 		
		 	}else if(o.getHostsid()!=null && !o.getHostsid().equals(dob.getHostsid())) {
				
				String newHostsid = o.getHostsid();
				 
				 String[] hostArry = newHostsid.split(",");
				 Map <String,String>hostsMap  =new HashMap<String,String>();
				 
				 for (String hostsid : hostArry) {
					
					 hostsMap.put(hostsid, hostsid);
				}
				  List<OaSteps> dobSteps = OaSteps.dao.find("select  * from oa_steps  where oid='"+o.getId()+"' and cuserid='"+ShiroKit.getUserId()+"' and ifcomplete='0' and step='42' ");
				 
				  for (OaSteps oaSteps : dobSteps) {
					//if(newLeaderidsoaSteps.getUserid())
					  
					  if(hostsMap.get(oaSteps.getUserid())==null) {
						  OaSteps.dao.deleteById(oaSteps.getId());
						  NodesUtils.deleteNodes(oaSteps.getId());
					  }
				}
				/*  int overLeaderStep = OaSteps.dao.find("select  * from oa_steps where  oid='"+o.getId()+"' and userid in ('4','147') and  ifcomplete='0'  and step='41'").size();
				  
				  if(overLeaderStep==0) {
						String updateSql="update oa_steps set  buttontype='1'  where  oid='"+o.getId()+"' and   step='41'";
						Db.update(updateSql);
				  }*/
				
			}
		 
		 //处理主办科室人 end
		 	
		 	
		 //会办处理 start
		 	
		 	
		 	if(o.getCustomersid()==null) {
		 		 List<OaSteps> dobHostSteps = OaSteps.dao.find("select  * from oa_steps  where oid='"+o.getId()+"' and cuserid='"+ShiroKit.getUserId()+"' and ifcomplete='0' and step='43' ");
		 		for (OaSteps oaSteps : dobHostSteps) {
		 			OaSteps.dao.deleteById(oaSteps.getId());
		 			NodesUtils.deleteNodes(oaSteps.getId());
				}
		 		
		 	}else if(o.getCustomersid()!=null && !o.getCustomersid().equals(dob.getCustomersid())) {
				
				String newCustomersid = o.getCustomersid();
				 
				 String[] customerArry = newCustomersid.split(",");
				 Map <String,String>customerMap  =new HashMap<String,String>();
				 
				 for (String customerid : customerArry) {
					
					 customerMap.put(customerid, customerid);
				}
				  List<OaSteps> dobSteps = OaSteps.dao.find("select  * from oa_steps  where oid='"+o.getId()+"' and cuserid='"+ShiroKit.getUserId()+"' and ifcomplete='0' and step='43' ");
				 
				  for (OaSteps oaSteps : dobSteps) {
					//if(newLeaderidsoaSteps.getUserid())
					  
					  if(customerMap.get(oaSteps.getUserid())==null) {
						  OaSteps.dao.deleteById(oaSteps.getId());
						  NodesUtils.deleteNodes(oaSteps.getId());
					  }
				}
				/*  int overLeaderStep = OaSteps.dao.find("select  * from oa_steps where  oid='"+o.getId()+"' and userid in ('4','147') and  ifcomplete='0'  and step='41'").size();
				  
				  if(overLeaderStep==0) {
						String updateSql="update oa_steps set  buttontype='1'  where  oid='"+o.getId()+"' and   step='41'";
						Db.update(updateSql);
				  }*/
				
			}
		 
		 	
		 //会办处理end	
		 	
		 	
		 	
		 	
		 	
		 	
		 	
		 
		 //=================参与人员处理 start===================
		 	
		 	
		 	List<OaSteps> stepsList=new ArrayList<>();	
		 	
		 	
		 if(o.getLeadersid()!=null && !o.getLeadersid().equals("")) {
				
				String[] leadersids = o.getLeadersid().split(",");
				
				for (String leadersid : leadersids) {
					 int size = OaSteps.dao.find("select  * from oa_steps where  oid='"+o.getId()+"' and userid='"+leadersid+"' and step='41'").size();
					if(size==0) {
						flag="1";
						OaSteps aBUser =new OaSteps();
						SysUser user = SysUser.dao.findById(leadersid);
						SysOrg org = SysOrg.dao.findById(user.getOrgid());
						aBUser.setId(UuidUtil.getUUID());
						aBUser.setOid(o.getId());
						aBUser.setType(typeStep);
						aBUser.setTitle(o.getTitle());
						aBUser.setUserid(user.getId());
						aBUser.setUsername(user.getName());
						aBUser.setOrgid(org.getId());
						aBUser.setOrgname(org.getName());
						aBUser.setIfshow("1");//显示
						aBUser.setIfcomplete("0");
						aBUser.setCtime(DateUtil.getCurrentTime());
						aBUser.setCusername(ShiroKit.getUserName());
						aBUser.setCuserid(ShiroKit.getUserId());
						aBUser.setStepdesc(user.getSort().toString());
						aBUser.setStep("41");
						if(leadersid.equals("4")||leadersid.equals("147")) {
							String updateSql="update oa_steps set ifcomplete='0',buttontype='0'  where  oid='"+o.getId()+"' and userid  not in ('4','147') and step in('41','42','43')  ";
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
						
						
						stepsList.add(aBUser);
					}
					 
				}
			} 
			
			if(o.getHostsid()!=null && !o.getHostsid().equals("")) {
				int leaderFinishSize = OaSteps.dao.find("select  * from oa_steps where  oid='"+o.getId()+"' and step='41' and ifcomplete='0'").size();
				
				if(leaderFinishSize==0) {
					buttonType="1";
				}else {
					buttonType="0";
				}
				
			
			String[] hostids = o.getHostsid().split(",");
			
				for (String hostid : hostids) {
					 int size = OaSteps.dao.find("select  * from oa_steps where  oid='"+o.getId()+"' and userid='"+hostid+"' and step='42'").size();
					 if(size==0) {
						 flag="1";
						 OaSteps buser =new OaSteps();
						 SysUser user = SysUser.dao.findById(hostid);
						 SysOrg org = SysOrg.dao.findById(user.getOrgid());
						 buser.setId(UuidUtil.getUUID());
						 buser.setOid(o.getId());
						 buser.setType(typeStep);
						 buser.setTitle(o.getTitle());
						 buser.setUserid(user.getId());
						 buser.setUsername(user.getName());
						 buser.setOrgid(user.getOrgid());
						 buser.setOrgname(org.getName());
						 buser.setIfshow("1");//显示
						 buser.setIfcomplete("0");
						 buser.setCtime(DateUtil.getCurrentTime());
						 buser.setCusername(ShiroKit.getUserName());
						 buser.setCuserid(ShiroKit.getUserId());
						 buser.setStep("42");//主办
						 buser.setStepdesc(user.getSort().toString());
						 buser.setButtontype(buttonType);
						 
						 buser.save();
						 stepsList.add(buser);
					 }
				}
			}
			
			
			if(o.getCustomersid()!=null && !o.getCustomersid().equals("")) {
				
				int leaderFinishSize = OaSteps.dao.find("select  * from oa_steps where  oid='"+o.getId()+"' and step='41' and ifcomplete='0'").size();
				
				if(leaderFinishSize==0) {
					buttonType="1";
				}else {
					buttonType="0";
				}
				
				
				
				String[] customerids = o.getCustomersid().split(",");
				
				for (String customerid : customerids) {
					int size = OaSteps.dao.find("select  * from oa_steps where  oid='"+o.getId()+"' and userid='"+customerid+"' and step='43'").size();
					 if(size==0) {
						 flag="1";
						 OaSteps buser =new OaSteps();
						 SysUser user = SysUser.dao.findById(customerid);
						 buser.setId(UuidUtil.getUUID());
						 buser.setOid(o.getId());
						 buser.setType(typeStep);
						 buser.setTitle(o.getTitle());
						 buser.setUserid(user.getId());
						 buser.setUsername(user.getName());
						 buser.setOrgid(user.getOrgid());
						 SysOrg org = SysOrg.dao.findById(user.getOrgid());
						 buser.setOrgname(org.getName());
						// buser.setSortnum(4);
						 buser.setSortnum("4");
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
						 stepsList.add(buser);
					 }
					
				}
			}
			
			
			//NodesUtils.addNodes(o.getId(), stepsList);
			
			
			//nodestart
			
			  OaSteps findFirst = OaSteps.dao.findFirst("select * from oa_steps where step='3'  and  oid='"+o.getId()+"'");
	
	
	
			NodesUtils.inserNode(stepsList, o.getId(),findFirst.getId(),"1");
			
			
			
			List<OaNodes> nodesList = OaNodes.dao.find("select  * from oa_nodes where  oid='"+o.getId()+"' and  parentid='"+findFirst.getId()+"'");
			
			List nodesLists =new ArrayList<OaSteps>();
			
			for (OaNodes oaNodes : nodesList) {
				
				
				OaSteps step = OaSteps.dao.findById(oaNodes.getId());
				
				if(step!=null) {
					nodesLists.add(step);
				}
			}
			
			
			
			NodesUtils.bundRelation(findFirst, nodesLists);
			//nodeend

			
			 //=================参与人员处理 start===================
			
			
			
			

			if(flag.equals("1")) {
				
				
				Db.update("update oa_steps  set ifcomplete='1' where userid='"+ShiroKit.getUserId()+"' and oid ='"+o.getId()+"' and ifcomplete='0'");
				o.setStatus("3");
			}
			
			o.update();
			
			renderSuccess();
			
			
			
	}
 

	
	
	
	public void showNoede() {
		
		

		
		
		String id = getPara("id");
		
		
		List<OaNodes> nodes = OaNodes.dao.find("select  * from  oa_nodes where oid='"+id+"'  and status='1' and backup1='1' order by  sortnum");
		//List<OaNodes> nodes = OaNodes.dao.find("select  * from  oa_nodes where oid='"+id+"'   order by  sortnum");
		
	/*	for (OaNodes oaNodes : nodes) {
			
			
			
		}*/	
		
		
		
		
		setAttr("nodes", nodes);
		List<D3Edge> d3Llist=new  ArrayList<>();
		
		
		
		for (OaNodes oaNodes : nodes) {
			String start =oaNodes.getStartnode();
			
			
			String end=oaNodes.getEndnode();
			List<OaNodes> sonList = OaNodes.dao.find("select  * from  oa_nodes where oid='"+id+"' and parentid='"+oaNodes.getId()+"'  and status='1' and backup1='2' ");
			if(sonList.size()==0) {
			if(end !=null && !end.equals("")) {
				String[] split = end.split(",");
				
				for (String str : split) {
					if(!str.trim().equals("")) {
					 D3Edge de=new D3Edge();
						de.setStart(start);
						
						/*startList.add(start);
						endList.add(str);*/
						
						de.setEnd(str);
						d3Llist.add(de);
					}
				}
			}
			
			
		}
			
		}
		
		
		/*setAttr("startList", startList);
		setAttr("endList", endList);*/
		
		setAttr("d3Llist", d3Llist);
		

		renderIframe("showNodes.html");
		
	
		
	}
	
	
	
	
	
	/*
	 * 流程显示
	 */
	public void   showNodes() {
		
		
		String id = getPara("id");
		
		
		List<OaNodes> nodes = OaNodes.dao.find("select  * from  oa_nodes where oid='"+id+"'  and status='1' and backup1='1' order by  sortnum");
		//List<OaNodes> nodes = OaNodes.dao.find("select  * from  oa_nodes where oid='"+id+"'   order by  sortnum");
		
		setAttr("nodes", nodes);
		List<D3Edge> d3Llist=new  ArrayList<>();
		
		
		
		for (OaNodes oaNodes : nodes) {
			String start =oaNodes.getStartnode();
			
			
			String end=oaNodes.getEndnode();
			List<OaNodes> sonList = OaNodes.dao.find("select  * from  oa_nodes where oid='"+id+"' and parentid='"+oaNodes.getId()+"'  and status='1' and backup1='2' ");
			if(sonList.size()==0) {
			if(end !=null && !end.equals("")) {
				String[] split = end.split(",");
				
				for (String str : split) {
					if(!str.trim().equals("")) {
					 D3Edge de=new D3Edge();
						de.setStart(start);
						
						/*startList.add(start);
						endList.add(str);*/
						
						de.setEnd(str);
						d3Llist.add(de);
					}
				}
			}
			
			
		}
			
		}
		
		
		/*setAttr("startList", startList);
		setAttr("endList", endList);*/
		
		setAttr("d3Llist", d3Llist);
		

		renderIframe("showNodes.html");
		
	}
	
	
	
	
	/*
	 * 科长流程显示
	 */
	public void   showNodes2() {
		
		
	String id = getPara("id");
		
		
		List<OaNodes> nodes = OaNodes.dao.find("select  * from  oa_nodes where oid='"+id+"' and status<>'3'   order by  sortnum");
		
		for (OaNodes oaNodes : nodes) {
			if(oaNodes.getNodecomplete().equals("0")) {
				
				if(oaNodes.getCompletetime()!=null && !oaNodes.getCompletetime().equals("")) {
					
					oaNodes.setNodecomplete("1");
				}
			}
		}
		
		
		
		setAttr("nodes", nodes);
		List<D3Edge> d3Llist=new  ArrayList<>();
		
		
		
		for (OaNodes oaNodes : nodes) {
			String start =oaNodes.getStartnode();
			
			
			String end=oaNodes.getBackup2();
			if(end !=null && !end.equals("")) {
				String[] split = end.split(",");
				
				for (String str : split) {
					if(!str.trim().equals("")) {
					 D3Edge de=new D3Edge();
						de.setStart(start);
						de.setEnd(str);
						d3Llist.add(de);
					}
				}
			}
			
			
		}
		
		
		/*setAttr("startList", startList);
		setAttr("endList", endList);*/
		
		setAttr("d3Llist", d3Llist);
		

		
		
		renderIframe("showNodes2.html");
/*		
		String id = getPara("id");
		
		
		List<OaNodes> nodes = OaNodes.dao.find("select  * from  oa_nodes where oid='"+id+"' and cuserid='"+ShiroKit.getUserId()+"' and backup1='2' order by  sortnum");
		//List<OaNodes> nodes = OaNodes.dao.find("select  * from  oa_nodes where oid='"+id+"'   order by  sortnum");
		
		
		
		
		List<OaNodes> showNodes = getShowNodes(nodes);
		
		OaNodes lastShow = showNodes.get(showNodes.size()-1);
		
		showNodes.remove(showNodes.size()-1);
		
		lastShow.setEndnode("");
		showNodes.add(lastShow);
		
		
		
		
		
		setAttr("nodes", showNodes);
		List<D3Edge> d3Llist=new  ArrayList<>();
		
		
		
		for (OaNodes oaNodes : showNodes) {
			String start =oaNodes.getStartnode();
			
			
			String end=oaNodes.getEndnode();
			
			if(end !=null && !end.equals("")) {
				String[] split = end.split(",");
				
				for (String str : split) {
					if(!str.trim().equals("")) {
						D3Edge de=new D3Edge();
						de.setStart(start);
						
						startList.add(start);
						endList.add(str);
						
						de.setEnd(str);
						d3Llist.add(de);
					}
				}
			}
			
			
			
		}
		
		
		setAttr("d3Llist", d3Llist);
		
		
		renderIframe("showNodes.html");
*/		
	}

	
	public List<OaNodes>  getShowNodes(List<OaNodes> nodes) {
		List<String> ids =new ArrayList<>();
		
		
		for (OaNodes node : nodes) {
			
			ids.add(node.getId());
			
			ids.add(node.getParentid());
			
			String endnode = node.getEndnode();
			
			if(endnode!=null && !endnode.equals("")) {
				
				String[] nodeids = endnode.split(",");
				
				for (String id : nodeids) {
					if(!id.equals("")) {
						
						ids.add(id);
					}
				}
			}
			
			
			
		}
		
		String strids="";
		
		for (String strid : ids) {
			strids=strids+",'"+strid+"'";
		}
		
		strids=strids.substring(1);
		System.out.println(strids);
		
		String sql="select  * from oa_nodes where id  in (" +strids+ ") order by sortnum ";
		
		System.out.print(sql);
		
		List<OaNodes> find = OaNodes.dao.find(sql);
		System.out.println(find.size());
		
		return find;
	}
	
}
