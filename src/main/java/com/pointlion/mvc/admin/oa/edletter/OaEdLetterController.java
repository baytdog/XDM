package com.pointlion.mvc.admin.oa.edletter;


import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.pointlion.mvc.admin.oa.workflow.WorkFlowService;
import com.pointlion.mvc.common.base.BaseController;
import com.pointlion.mvc.common.model.OaDict;
import com.pointlion.mvc.common.model.OaEdLetter;
import com.pointlion.mvc.common.utils.DateUtil;
import com.pointlion.mvc.common.utils.StringUtil;
import com.pointlion.mvc.common.utils.UuidUtil;
import com.pointlion.plugin.shiro.ShiroKit;



public class OaEdLetterController extends BaseController {
	public static final OaEdLetterService service = OaEdLetterService.me;
	public static WorkFlowService wfservice = WorkFlowService.me;
	/***
	 * get list page
	 */
	public void getListPage(){
		
    	List<OaDict> lettertype = OaDict.dao.find("select  * from oa_dict where type ='edlettertype' order by sortnum");
    	setAttr("lettertype",lettertype);
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
		String lfromer = java.net.URLDecoder.decode(getPara("lfromer",""),"UTF-8");
		String lstate = getPara("lstate","");
		
    	Page<Record> page = service.getPage(Integer.valueOf(curr),Integer.valueOf(pageSize),startTime,endTime,lnum,lfromer,lstate);
    	renderPage(page.getList(),"",page.getTotalRow());
    }
    /***
     * save data
     */
    public void save(){
    	OaEdLetter o = getModel(OaEdLetter.class);
  
    	//	o.setId(UuidUtil.getUUID());
    		//o.setCreateTime(DateUtil.getCurrentTime());
    	OaEdLetter dob = OaEdLetter.dao.findById(o.getId());
    	if(dob==null) {
    		
    	 	o.setLettercbrid(ShiroKit.getUserId());
        	o.setLettercbrname(ShiroKit.getUserName());
        	o.setLetterbctime(DateUtil.getCurrentTime());
        	o.setCtime(DateUtil.getCurrentTime());
        	o.setCuserid(ShiroKit.getUserId());
        	o.setCusername(ShiroKit.getUserName());
        	o.save();
    	}else {
    		o.update();
    	}
    	
   
     
    	renderSuccess();
    }
    /***
     * edit page
     */
    public void getEditPage(){
    	
    	
    	List<OaDict> lettertype = OaDict.dao.find("select  * from oa_dict where type ='edlettertype' order by sortnum");
    	
    	setAttr("lettertype",lettertype);
    	
    	
    	setAttr("currentUserId", ShiroKit.getUserId());
    	String id = getPara("id");
    	String view = getPara("view");
		setAttr("view", view);
		OaEdLetter o = new OaEdLetter();
		if(StrKit.notBlank(id)){
    		o = OaEdLetter.dao.findById(id);
    	}else{
    	 o.setId(UuidUtil.getUUID());
    	 o.setCtime(DateUtil.getCurrentTime());
    	 setAttr("currentUserId", ShiroKit.getUserId());
    	}
		setAttr("o", o);
    	setAttr("formModelName",StringUtil.toLowerCaseFirstOne(OaEdLetter.class.getSimpleName()));
		renderIframe("edletter.html");
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
    	OaEdLetter o = OaEdLetter.dao.getById(id);
    	o.update();
    	renderSuccess("submit success");
    }
    /***
     * callBack
     */
    public void callBack(){
    	String id = getPara("id");
    	try{
    		OaEdLetter o = OaEdLetter.dao.getById(id);
        	o.update();
    		renderSuccess("callback success");
    	}catch(Exception e){
    		e.printStackTrace();
    		renderError("callback fail");
    	}
    }

    
    
    
    public void exportTotal() {
    	
   	 try {
   		 
   		String endTime = getPara("endTime","");
		String startTime = getPara("startTime","");
		String lnum = java.net.URLDecoder.decode(getPara("lnum",""),"UTF-8");
		String lfromer = java.net.URLDecoder.decode(getPara("lfromer",""),"UTF-8");
		String lstate = getPara("lstate","");
		String path = this.getSession().getServletContext().getRealPath("")+"/upload/export/"+DateUtil.format(new Date(),21)+".xlsx";
		File file = service.exportTotalExcel(path,lnum,lfromer,startTime,endTime,lstate,this.getRequest(),this.getResponse());
   		 
   		 
		//File exporttest = Exports.exportCount(this.getRequest(), this.getResponse(),path);
		renderFile(file);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    }
    
    
    /*
     * 导出
     */
    
	public void exportExcel() throws UnsupportedEncodingException {
		
	
 
		String endTime = getPara("endTime","");
		String startTime = getPara("startTime","");
		String lnum = java.net.URLDecoder.decode(getPara("lnum",""),"UTF-8");
		String lfromer = java.net.URLDecoder.decode(getPara("lfromer",""),"UTF-8");
		String lstate = getPara("lstate","");
		String path = this.getSession().getServletContext().getRealPath("")+"/upload/export/"+DateUtil.format(new Date(),21)+".xlsx";
		File file = service.exportExcel(path,lnum,lfromer,startTime,endTime,lstate);
		renderFile(file);
	}
    
   
}