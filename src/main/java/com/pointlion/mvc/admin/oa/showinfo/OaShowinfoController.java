package com.pointlion.mvc.admin.oa.showinfo;

import java.io.UnsupportedEncodingException;
import java.util.List;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.pointlion.mvc.admin.oa.workflow.WorkFlowService;
import com.pointlion.mvc.common.base.BaseController;
import com.pointlion.mvc.common.model.OaMenuDict;
import com.pointlion.mvc.common.model.OaShowinfo;
import com.pointlion.mvc.common.model.SysAttachment;
import com.pointlion.mvc.common.utils.DateUtil;
import com.pointlion.mvc.common.utils.StringUtil;
import com.pointlion.mvc.common.utils.UuidUtil;
import com.pointlion.plugin.shiro.ShiroKit;



public class OaShowinfoController extends BaseController {
	public static final OaShowinfoService service = OaShowinfoService.me;
	public static WorkFlowService wfservice = WorkFlowService.me;
	/***
	 * get list page
	 */
	public void getListPage(){
		
		List<OaMenuDict> firsetm = OaMenuDict.dao.find("select  * from  oa_menu_dict  where  parentid='root' order by  sortnum");
		setAttr("firsetm", firsetm);
		renderIframe("list.html");
    }
	
	
	public void homeMorListPage(){
		
		List<OaMenuDict> firsetm = OaMenuDict.dao.find("select  * from  oa_menu_dict  where  parentid='root' order by  sortnum");
		setAttr("firsetm", firsetm);
		System.out.println("-------------------"+getPara("menuid"));
		setAttr("menuid", getPara("menuid"));
		renderIframe("showinfolist.html");
    }
	
	
	
	public void getSecondMenu() {
		
		System.out.println(getPara("parentid"));
		
		List<OaMenuDict> oaMenuDictList = OaMenuDict.dao.find("select  * from  oa_menu_dict  where  parentid='"+getPara("parentid")+"' order by  sortnum");
	
		renderSuccess(oaMenuDictList, "");
	
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
		String publisher = java.net.URLDecoder.decode(getPara("publisher",""),"UTF-8");
		String title =java.net.URLDecoder.decode(getPara("title",""),"UTF-8");
		String secondmenu = getPara("secondmenu","");
    	Page<Record> page = service.getPage(Integer.valueOf(curr),Integer.valueOf(pageSize),startTime,endTime,publisher,title,secondmenu);
    	renderPage(page.getList(),"",page.getTotalRow());
    }
    /***
     * list page data
     * @throws UnsupportedEncodingException 
     **/
    public void listDataBymenuId() throws UnsupportedEncodingException{
    	String menuid=getPara("menuid");
    	String curr = getPara("pageNumber");
    	String pageSize = getPara("pageSize");
    
    	Page<Record> page = service.getPageByMenuId(Integer.valueOf(curr),Integer.valueOf(pageSize),menuid);
    	renderPage(page.getList(),"",page.getTotalRow());
    }
    /***
     * save data
     */
    public void save(){
    	OaShowinfo o = getModel(OaShowinfo.class);
    	if(StrKit.notBlank(o.getId())){
    		o.update();
    	}else{
    		o.setId(UuidUtil.getUUID());
   /* 		o.setCreateTime(DateUtil.getCurrentTime());*/
    		o.setOpuser(ShiroKit.getUserName());
    		o.setPublisher(ShiroKit.getUserName());
    		o.setPublisherid(ShiroKit.getUserId());
    		o.setChangetime(DateUtil.getCurrentTime());
    		o.setPublishdatetime(DateUtil.getCurrentTime());
    		o.setSfpublish("1");
    		o.save();
    	}
    	
    	renderSuccess(o,"");
    	//renderSuccess();
    }
    /***
     * edit page
     */
	public void getEditPage() {
		String id = getPara("id");
		String view = getPara("view");
		setAttr("view", view);
		int fileSize = SysAttachment.dao.find("select  * from sys_attachment  where  business_id='"+id+"'").size();
		setAttr("fileSize", fileSize);
		OaShowinfo o = new OaShowinfo();
		if (StrKit.notBlank(id)) {
			o = service.getById(id);
			OaMenuDict menudictById = OaMenuDict.dao.findById(o.getMenuid());
			setAttr("firstMenuId", menudictById.getParentid());
			// OaMenuDict.dao.find("select * from oa_menu_dict where
			// parentid='"+o.getMenuid()+"'");

			if ("detail".equals(view)) {
				/*
				 * if(StrKit.notBlank(o.getProcInsId())){ setAttr("procInsId",
				 * o.getProcInsId()); setAttr("defId",
				 * wfservice.getDefIdByInsId(o.getProcInsId())); }
				 */
			}
		} else {
			/*
			 * SysUser user = SysUser.dao.findById(ShiroKit.getUserId()); SysOrg org =
			 * SysOrg.dao.findById(user.getOrgid());
			 */
			/*
			 * o.setOrgId(org.getId()); o.setOrgName(org.getName());
			 * o.setUserid(user.getId()); o.setApplyerName(user.getName());
			 */
		}
		setAttr("o", o);
		setAttr("formModelName", StringUtil.toLowerCaseFirstOne(OaShowinfo.class.getSimpleName()));

		List<OaMenuDict> firsetm = OaMenuDict.dao
				.find("select  * from  oa_menu_dict  where  parentid='root' order by  sortnum");
		setAttr("firsetm", firsetm);
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
    	OaShowinfo o = OaShowinfo.dao.getById(id);
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
    		OaShowinfo o = OaShowinfo.dao.getById(id);
   /*     	wfservice.callBack(o.getProcInsId());
        	o.setIfSubmit(Constants.IF_SUBMIT_NO);
        	o.setProcInsId("");*/
        	o.update();
    		renderSuccess("callback success");
    	}catch(Exception e){
    		e.printStackTrace();
    		renderError("callback fail");
    	}
    }

    
	/**
	 * 
	* @Title: homeViewNotice 
	* @Description: TODO  首页查看通知公告
	* @author bkkco
	* @date 2020年10月22日下午12:56:33
	 */
	public void homeViewNotice(){
		OaShowinfo oaShowinfo = OaShowinfo.dao.findById(getPara("id"));
		setAttr("showinfo", oaShowinfo);
		List<SysAttachment> sysAttachments = SysAttachment.dao.find("select * from sys_attachment where business_id='"+getPara("id")+"'");
		setAttr("sysAttachments", sysAttachments);
		renderIframe("homeShowNotice.html");
	}
    
    
	
	public void homeWorkKeySearch() {
		
		System.out.println(getPara("keyWord"));
		
		
	}
    
	
}