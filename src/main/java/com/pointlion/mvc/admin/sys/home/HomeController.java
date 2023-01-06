/**

 * @author Lion
 * @date 2017年1月24日 下午12:02:35
 * @qq 439635374
 */
package com.pointlion.mvc.admin.sys.home;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.pointlion.mvc.admin.oa.workflow.flowtask.FlowTaskService;
import com.pointlion.mvc.admin.sys.login.SessionUtil;
import com.pointlion.mvc.common.base.BaseController;
import com.pointlion.mvc.common.model.*;
import com.pointlion.mvc.common.utils.Constants;
import com.pointlion.mvc.common.utils.RzUtils;
import com.pointlion.mvc.common.utils.UuidUtil;
import com.pointlion.plugin.shiro.ShiroKit;
import com.pointlion.plugin.shiro.ext.SimpleUser;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;


/***
 * 首页控制器
 */
public class HomeController extends BaseController {
//	static WorkFlowService workflowService = WorkFlowService.me;
//	static NoticeService noticeService = new NoticeService();
	static FlowTaskService commonFlowService = FlowTaskService.me;
    /***
     * 首页
     */
    public void getHomePage(){
//    	SimpleUser user = ShiroKit.getLoginUser();
    	//获取首页通知公告
    	
    	SimpleUser user = ShiroKit.getLoginUser();
    	String username = user.getUsername();
    	setAttrToHomePage(username);
    	
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    	setAttr("today", sdf.format(new Date()));
    	
    	
    	renderIframe("/WEB-INF/admin/home/homePage.html");
    	//renderIframe("/WEB-INF/admin/home/personalhome.html");
    }
    
    /**
     * 
    * @Title: getPersonalHomePage 
    * @Description: 个人工作主页
    * @date 2020年11月9日上午10:58:16
     */
    public void getPersonalHomePage(){
    	SimpleUser user = ShiroKit.getLoginUser();
    	String username = user.getUsername();
    	setAttrToHomePage(username);
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    	setAttr("today", sdf.format(new Date()));
    	OaTypes findFirst = OaTypes.dao.findFirst("select  * from oa_types where  status='1'");
		if(findFirst.getType().equals("1")||findFirst.getType().equals("2")) {
			renderIframe("/WEB-INF/admin/home/personalhome.html");
		}else {
//			人员修改审批
			String empSql = " select * from   xd_steps s where  s.orgid ='"+ShiroKit.getUserOrgId()+"' and  s.finished='N'";
			int empSize = Db.find(empSql).size();
			setAttr("empSize",empSize);
			String employeeSql="select * from xd_employee";
			List<XdEmployee> xdEmployees = XdEmployee.dao.find(employeeSql);
//			合同到期
			DateTimeFormatter dtf=DateTimeFormatter.ofPattern("yyyy-MM-dd");
			Stream<XdEmployee> contractEndStream = xdEmployees.stream().filter( employee-> {
					String contractenddate = employee.getContractenddate();
					if(contractenddate==null||"".equals(contractenddate)||"无固定期限".equals(contractenddate)){
						return false;
					}else{

						LocalDate parse = LocalDate.parse(contractenddate, dtf).minusDays(30);
						LocalDate now = LocalDate.now();
						 return  now.isAfter(parse);
					}
			});
			setAttr("contractEndSize",contractEndStream.count());

//			适用到期
			Stream<XdEmployee> tryEmpStream = xdEmployees.stream().filter( employee->{
					String contractenddate = employee.getContractenddate();
					if (contractenddate != null&&!"".equals(contractenddate)) {
						return false;
					}else{
						String positivedate = employee.getPositivedate();
						if (positivedate == null||"".equals(positivedate)) {
							return false;
						}
						LocalDate bsposiDate = LocalDate.parse(positivedate, dtf).minusDays(30);
						LocalDate now = LocalDate.now();
						return  now.isAfter(bsposiDate);
					}
			});
			setAttr("tryEmpEndSize",tryEmpStream.count());

			//退休到期
			Stream<XdEmployee> retireStream = xdEmployees.stream().filter( employee->{
					String retiretime = employee.getRetiretime();
					if (retiretime == null||"".equals(retiretime)) {
						return false;
					}
					LocalDate bsRetireDate = LocalDate.parse(retiretime, dtf).minusDays(60);
					LocalDate now = LocalDate.now();
					return  now.isAfter(bsRetireDate);
			});
			setAttr("retireEmpEndSize",retireStream.count());

			String certSql="select * from  xd_emp_cert where status='1'";
			List<XdEmpCert> xdEmpCerts = XdEmpCert.dao.find(certSql);
			Stream<XdEmpCert> xdEmpCertStream = xdEmpCerts.stream().filter(cert -> {
				String closeDate = cert.getCloseDate();
				if (closeDate == null || "".equals(closeDate)||"长期".equals(closeDate)) {
					return false;
				}
				LocalDate bsRetireDate = LocalDate.parse(closeDate, dtf).minusDays(180);
				LocalDate now = LocalDate.now();
				return now.isAfter(bsRetireDate);
			});


			setAttr("certExpiredSize",xdEmpCertStream.count());

			renderIframe("/common/include/content.html");
		}
    }
	
	/** 
	* @Title: setAttrToHomePage 
	* @Description: 获取首页需要显示得信息
	* @date 2020年8月27日上午10:44:03
	*/ 
	private void setAttrToHomePage(String username) {
		

    	Map<String,List<Record>> todoMap = new HashMap<String,List<Record>>();
    	List<OaPicNews> picNewsList =null;
//    	List<OaNotice> xxjbList = OaNotice.dao.find("select * from oa_showinfo  where menuid='9b576e6581204cda90ae04722d8640c9' and sfpublish='1' order by changetime desc  limit 6");
    	List<OaNotice> xxjbList =null;
    	setAttr("picNewsList", picNewsList);
    	setAttr("xxjb", xxjbList);
    	
    	//事务公开查询
    	List<OaShowinfo> swgkList = null;
    	setAttr("swgk", swgkList);
    	
     	
    	//学习交流
    	List<OaShowinfo> xxjlList = null;
    	setAttr("xxjl", xxjlList);
    	//党务公开
    	List<OaShowinfo> dwgk =null;
    	setAttr("dwgk", dwgk);
    	
    	
    	
    	//待办
    	String  waitDoSql="select  * from  oa_steps   where  userid='"+ShiroKit.getUserId()+"' and ifcomplete='0'";
    	
//    	  List<OaSteps> find2 = OaSteps.dao.find(waitDoSql);
    	  List<OaSteps> find2 = null;

    	  setAttr("waitDoSql", waitDoSql);
    	  
    	  //已办理
    	   String overSql="select  * from  oa_step_history  where actorid='"+ShiroKit.getUserId()+"'";
//    	   List<OaStepHistory> over = OaStepHistory.dao.find(overSql);
    	   List<OaStepHistory> over = null;
    	   setAttr("over", over);
    	  
    	
	}

	/***
	 * 登录成功获取首页
	 */
    public void index(){
    	SimpleUser user = ShiroKit.getLoginUser();
    	String username = user.getUsername();
    	SysUser u = SysUser.dao.getByUsername(username);
    	SessionUtil.setUsernameToSession(this.getRequest(), username);
    	//加载个性化设置
	    String settingType = "tab";
    	SysCustomSetting setting = SysCustomSetting.dao.getCstmSettingByUsername(username);
    	if(setting==null){
		    setting = SysCustomSetting.dao.getDefaultCstmSetting();
    		setAttr("setting", setting);
    	}else{
		    settingType = setting.getIndexPageType();
    		setAttr("setting", setting);
    	}
    	this.getSession().setAttribute("setting",setting);//个性化设置放到sessiong里
    	List<SysUser> friends = SysFriend.dao.getUserFriend(u.getId());
    	setAttr("friends", friends);//我的好友
    	setAttr("user", u);//当前用户
    	setAttr("userName", user.getName());//我的姓名
    	setAttr("userEmail", user.getEmail());//我的邮箱
    	setAttr("uorgid", u.getOrgid());
    	
    	List<SysMenu> mlist;
    	if(user.getUsername().equals(Constants.SUUUUUUUUUUUUUPER_USER_NAME)||user.getUsername().equals("moa")){//特殊入口
    		mlist=SysMenu.dao.getAllMenu();
    	}else{
    		//查询所有有权限的菜单
        	mlist = SysRole.dao.getRoleAuthByUserid(u.getId(), "1",Constants.SYS_MENU_ROOT);//规定只有四级菜单 在这里暂定为A,B,C,D
        	for(SysMenu a : mlist){
        		List<SysMenu> blist = SysRole.dao.getRoleAuthByUserid(u.getId(), "1",a.getId());//A下面的菜单
        		a.setChildren(blist);
        		for(SysMenu b : blist){
        			List<SysMenu> clist = SysRole.dao.getRoleAuthByUserid(u.getId(), "1",b.getId());//B下面的菜单
        			b.setChildren(clist);
        			for(SysMenu c : clist){
            			List<SysMenu> dlist = SysRole.dao.getRoleAuthByUserid(u.getId(), "1",c.getId());//B下面的菜单
            			c.setChildren(dlist);
            		}
        		}
        	}
    	}
    	this.getSession().setAttribute("mlist", mlist);
    	setAttr("mlist", mlist);
    	
    	SimpleDateFormat sdf  =new SimpleDateFormat("yyyy-MM-dd");
    	OaNotices oanotices = OaNotices.dao.findFirst("select  * from  oa_notices 	where  	sfpublish = '1' and publishdatetime !='' 	and showtime >= '"+sdf.format(new Date())+"' and departments LIKE '%"+ShiroKit.getUserOrgId()+"%' ORDER BY 	publishdatetime DESC");
    	this.getSession().setAttribute("oanotices", oanotices);
    	setAttr("oanotices", oanotices);
    	if("single".equals(settingType)){
			render("/WEB-INF/admin/home/index_singlepage.html");
		}else{
			
		 
			/*if(username.equals("moa")||username.equals("tdog")) {
				render("/WEB-INF/admin/home/index.html");
			}else {

				render("/WEB-INF/admin/home/index1.html");
				
			}*/
			
			
			OaTypes findFirst = OaTypes.dao.findFirst("select  * from oa_types where  status='1'");
			if(findFirst.getType().equals("1")||findFirst.getType().equals("2")) {
				render("/WEB-INF/admin/home/index1.html");
			}else {
				render("/WEB-INF/admin/home/index.html");
			}
			
			
			
		}
    }


    public void getSingleCustomSettingPage(){
		render("/common/include/setting-single.html");
	}

    /***
     * 设定已办数据
     */
    public void setAttrHavedoneList(String username){
    	List<String> havedoneKeyList = commonFlowService.getHavedoneDefkeyList(ShiroKit.getUsername());
    	setAttr("havedoneKeyList", havedoneKeyList);
    }
    
    /***
     * 首页内容页
     */
    public void getMyHome(){
    	renderIframe("/WEB-INF/admin/home/myHome.html");
    }
    /**
     * 内容页
     * */
    public void content(){
    	renderIframe("/WEB-INF/admin/home/content.html");
    }
    /***
     * 获取消息中心最新消息
     */
    public void getSiteMessageTipPage(){
    	renderIframe("/WEB-INF/admin/home/siteMessageTip.html");
    }
    
    
    
    public void getWholeSearchList() throws UnsupportedEncodingException {
    	
    	
    	  String keyword = java.net.URLDecoder.decode(getPara("keyword",""),"UTF-8");
    	  
    	  System.out.println("keyword="+keyword);
    	  
    	  
    	  if(keyword!=null && !keyword.trim().equals("")) {
    		  insertSearch(keyword);
    		  RzUtils.insertOaRz(keyword, "4");
    	  }
    	
    	  
    	 
    	  
    	renderIframe("/WEB-INF/admin/home/wholeSearchList.html");
    	
    	
    }
    
    
    
    
    
    public  void insertSearch(String keyWords) {
    	  
    	
    	String userid=ShiroKit.getUserId();
  	  //收文 
  	  
    	insert("1",keyWords,userid);
  	  
  	  //发文
     	insert("2",keyWords,userid);
  	  
     	
     	//热线
     	insert("3",keyWords,userid);
  	  //信访
     	insert("4",keyWords,userid);
  	  
  	  //图片新闻
     	
     	List<OaPicNews> picNews = OaPicNews.dao.find("select  * from  oa_pic_news  where title like '%"+keyWords+"%'");
     	
     	for (OaPicNews oaPicNews : picNews) {
     		
     		  Db.delete("delete  from  oa_search   where oid='"+oaPicNews.getId()+"' and  userid='"+userid+"' and keywords='"+keyWords+"'");
      	  	  
      	  	  OaSearch  addSearch=new OaSearch();
      	  	  addSearch.setId(UuidUtil.getUUID());
      	  	  addSearch.setOid(oaPicNews.getId().toString());
      	  	  addSearch.setTitle(oaPicNews.getTitle());
      	  	  addSearch.setContent(oaPicNews.getTitle());
      	  	  addSearch.setType("5");
      	  	  addSearch.setUserid(userid);
      	  	  addSearch.setKeywords(keyWords);
      	  	  addSearch.setOtime(oaPicNews.getCdate());
      	  	  addSearch.save();
		}
  	  
  	  //通知
     	List<OaNotices> notice = OaNotices.dao.find("select  * from  oa_notices  where   sfpublish='1' and   departments like '%"+ShiroKit.getUserOrgId()+"%'  and (noticename like '%"+keyWords+"%' or noticeinfo like '%"+keyWords+"%' )");
     	
     	
     	
     	
     	for (OaNotices oaNotices : notice) {
     		
     		
     		  Db.delete("delete  from  oa_search   where oid='"+oaNotices.getId()+"' and  userid='"+userid+"' and keywords='"+keyWords+"'");
			System.out.println(oaNotices.getNoticeinfo());
     		
     		int indexOf = oaNotices.getNoticeinfo().indexOf(keyWords);
     		System.out.println(indexOf);
     		String substring ="";
     		
     		if(indexOf!=-1) {
	     		int length = oaNotices.getNoticeinfo().length();
	     		
	     		int endindex=length-indexOf>10?indexOf+10:length;
	     		
	     		int len2 = oaNotices.getNoticeinfo().substring(0, indexOf).length();
	     		
	     		 int startindex=len2>10 ?indexOf-10:0;
	     		  substring = oaNotices.getNoticeinfo().substring(startindex, endindex);
     		}
     		
     		  OaSearch  addSearch=new OaSearch();
     	  	  addSearch.setId(UuidUtil.getUUID());
     	  	  addSearch.setOid(oaNotices.getId().toString());
     	  	  addSearch.setTitle(oaNotices.getNoticename());
     	  	  addSearch.setContent(substring);
     	  	  addSearch.setType("6");
     	  	  addSearch.setUserid(userid);
     	  	  addSearch.setKeywords(keyWords);
     	  	  addSearch.setOtime(oaNotices.getChangetime());
     	  	  
     	  	  
     	  	  addSearch.save();
		}
     	
  	  
     	List<OaShowinfo> showinfo = OaShowinfo.dao.find("select  * from  oa_showinfo   where   sfpublish='1'  and   (infotitle like '%"+keyWords+"%'  or  infocontent like '%"+keyWords+"%')");
  	  
  	  
     	for (OaShowinfo oaShowinfo : showinfo) {

     		
     		
   		  Db.delete("delete  from  oa_search   where oid='"+oaShowinfo.getId()+"' and  userid='"+userid+"' and keywords='"+keyWords+"'");
   		
   		int indexOf = oaShowinfo.getInfocontent().indexOf(keyWords);
   		System.out.println(indexOf);
   		String substring ="";
   		
   		if(indexOf!=-1) {
	     		int length = oaShowinfo.getInfocontent().length();
	     		
	     		int endindex=length-indexOf>10?indexOf+10:length;
	     		
	     		int len2 = oaShowinfo.getInfocontent().substring(0, indexOf).length();
	     		
	     		 int startindex=len2>10 ?indexOf-10:0;
	     		  substring = oaShowinfo.getInfocontent().substring(startindex, endindex);
   		}
   		
   		  OaSearch  addSearch=new OaSearch();
   	  	  addSearch.setId(UuidUtil.getUUID());
   	  	  addSearch.setOid(oaShowinfo.getId().toString());
   	  	  addSearch.setTitle(oaShowinfo.getInfotitle());
   	  	  addSearch.setContent(substring);
   	  	  addSearch.setType("7");
   	  	  addSearch.setUserid(userid);
   	  	  addSearch.setKeywords(keyWords);
   	  	  addSearch.setOtime(oaShowinfo.getChangetime());
   	  	  addSearch.save();
		}
    	
    }
    
    
    public void  insert(String type,String keyWords,String userid) {
    	
	List<OaSteps> receiveList = OaSteps.dao.find("select  * from  oa_steps  where   type='"+type+"'  and  userid='"+userid+"'  and title like '%"+keyWords+"%'");
  	  	
  	  	for (OaSteps oaSteps : receiveList) {
  	  	  Db.delete("delete  from  oa_search   where oid='"+oaSteps.getOid()+"' and  userid='"+userid+"' and keywords='"+keyWords+"'");
  	  	  
  	  	  OaSearch  addSearch=new OaSearch();
  	  	  addSearch.setId(UuidUtil.getUUID());
  	  	  addSearch.setOid(oaSteps.getOid());
  	  	  addSearch.setTitle(oaSteps.getTitle());
  	  	  addSearch.setContent(oaSteps.getTitle());
  	  	  addSearch.setType(type);
  	  	  addSearch.setUserid(userid);
  	  	  addSearch.setKeywords(keyWords);
  	  	  addSearch.setOtime(oaSteps.getCtime());
  	  	  addSearch.save();
  	  	  
		}
  	  	
  	  	
  	  	List<OaStepHistory> hSteps = OaStepHistory.dao.find("select  * from  oa_step_history  where   type ='"+type+"' and   actorid='"+userid+"'   and  title like '%"+keyWords+"%' ");
  	  	for (OaStepHistory oash : hSteps) {
  	  		
  	  	 Db.delete("delete  from  oa_search   where oid='"+oash.getOid()+"' and  userid='"+userid+"' and keywords='"+keyWords+"'");
 	  	  
 	  	  OaSearch  addSearch=new OaSearch();
 	  	  addSearch.setId(UuidUtil.getUUID());
 	  	  addSearch.setOid(oash.getOid());
 	  	  addSearch.setTitle(oash.getTitle());
 	  	  addSearch.setContent(oash.getTitle());
 	  	  addSearch.setType(type);
 	  	  addSearch.setUserid(userid);
 	  	  addSearch.setKeywords(keyWords);
 	  	  addSearch.setOtime(oash.getCtime());
 	  	  addSearch.save();
			
		}
    }
    
    public  void listSearchData() throws UnsupportedEncodingException {
    	
    	System.out.println(java.net.URLDecoder.decode(getPara("keyWords",""),"UTF-8"));
    	
    	String curr = getPara("pageNumber");
    	String pageSize = getPara("pageSize");
    	String keyWords = java.net.URLDecoder.decode(getPara("keyWords",""),"UTF-8");
    	
    	Page<OaSearch> page = OaSearch.dao.getPage(Integer.valueOf(curr),Integer.valueOf(pageSize),keyWords);
    	renderPage(page.getList(),"" ,page.getTotalRow());
    	
    }
    
    
    
    public void  showDetail() {
    	
    	System.out.println(getPara("id"));
    	System.out.println(getPara("type"));
    	
    	String  id=getPara("id");
    	String  type=getPara("type");
   
    	if(type.equals("1")) {
    		this.redirect("/admin/oa/bumph/getDraftEditPage?view=detail&sstep=0&id="+id);     
    		
    	}else if(type.equals("2")) {
    		
    		this.redirect("/admin/oa/senddoc/getEditPage?view=detail&sstep=0&id="+id);     	
    	}else if(type.equals("3")) {
    		
    		///admin/oa/hotline/getEditPage?id='+row.id+'&type='+row.type+'&view=detail
    				this.redirect("/admin/oa/hotline/getEditPage?view=detail&id="+id);     	
    		
    	}else if(type.equals("4")) {
    		
    		this.redirect("/admin/oa/letter/getEditPage?view=detail&id="+id);     	
    		
    	}else if(type.equals("5")) {
    		//ok
    		this.redirect("/admin/oa/picnews/showAllPic?busid="+id);
    		
    	}else if(type.equals("6")) {
    		this.redirect("/admin/oa/notices/getEditPage?view=detail&id="+id);
    		
    	}else if(type.equals("7")) {
    		
    		this.redirect("/admin/oa/showinfo/homeViewNotice?view=detail&id="+id);     
    	} 
    	
    	
    	
    }
    
    
    
}
