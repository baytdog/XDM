package com.pointlion.mvc.admin.oa.departmentsfiles;

import java.io.UnsupportedEncodingException;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.pointlion.mvc.admin.oa.workflow.WorkFlowService;
import com.pointlion.mvc.common.base.BaseController;
import com.pointlion.mvc.common.model.OaDepartmentsFiles;
import com.pointlion.mvc.common.utils.DateUtil;
import com.pointlion.mvc.common.utils.StringUtil;
import com.pointlion.mvc.common.utils.UuidUtil;
import com.pointlion.plugin.shiro.ShiroKit;



public class OaDepartmentsFilesController extends BaseController {
	public static final OaDepartmentsFilesService service = OaDepartmentsFilesService.me;
	public static WorkFlowService wfservice = WorkFlowService.me;
	/***
	 * get list page
	 */
	public void getListPage(){
		
		
		setAttr("way", getPara("way", ""));
		keepPara("way");
		
		renderIframe("list.html");
    }
	
	
	public void getMoreListPage(){
		
		
		setAttr("way", getPara("way", ""));
		keepPara("way");
		
		renderIframe("moreList.html");
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
		String  fileName= java.net.URLDecoder.decode(getPara("fileName",""),"UTF-8");
		
    	Page<Record> page = service.getFilePageList(Integer.valueOf(curr),Integer.valueOf(pageSize),startTime,endTime,fileName);
    	renderPage(page.getList(),"",page.getTotalRow());
    }
    public void listMoreData() throws UnsupportedEncodingException{
    	String curr = getPara("pageNumber");
    	String pageSize = getPara("pageSize");
    	String endTime = getPara("endTime","");
    	String startTime = getPara("startTime","");
    	String  fileName= java.net.URLDecoder.decode(getPara("fileName",""),"UTF-8");
    	
    	Page<Record> page = service.getMoreFilePageList(Integer.valueOf(curr),Integer.valueOf(pageSize),startTime,endTime,fileName);
    	renderPage(page.getList(),"",page.getTotalRow());
    }
    /***
     * save data
     */
    public void save(){
    	OaDepartmentsFiles o = getModel(OaDepartmentsFiles.class);
    	if(StrKit.notBlank(o.getId())){
    		o.update();
    	}else{
    		o.setId(UuidUtil.getUUID());
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
		OaDepartmentsFiles o = new OaDepartmentsFiles();
		if(StrKit.notBlank(id)){
    		o = OaDepartmentsFiles.dao.findById(id);
    	}else{
    		
    		//SysUser user = SysUser.dao.findById(ShiroKit.getUserId());
    		//SysOrg org = SysOrg.dao.findById(user.getOrgid());
    	 o.setId(UuidUtil.getUUID());
    	 o.setCtime(DateUtil.getCurrentTime());
     
    	 setAttr("currentUserId", ShiroKit.getUserId());
    		
		/*	o.setOrgId(org.getId());
			o.setOrgName(org.getName());
			o.setUserid(user.getId());
			o.setApplyerName(user.getName());*/
    	}
		setAttr("o", o);
    	setAttr("formModelName",StringUtil.toLowerCaseFirstOne(OaDepartmentsFiles.class.getSimpleName()));
		renderIframe("departfile.html");}
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
    	OaDepartmentsFiles o = OaDepartmentsFiles.dao.getById(id);
		String insId = wfservice.startProcess(id, o,null,null);
    	o.update();
    	renderSuccess("submit success");
    }
    /***
     * callBack
     */
    public void callBack(){
    	String id = getPara("id");
    	try{
    		OaDepartmentsFiles o = OaDepartmentsFiles.dao.getById(id);
        	o.update();
    		renderSuccess("callback success");
    	}catch(Exception e){
    		e.printStackTrace();
    		renderError("callback fail");
    	}
    }

	
}