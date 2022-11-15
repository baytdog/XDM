package com.pointlion.mvc.admin.oa.stephistory;

import java.io.UnsupportedEncodingException;
import java.util.List;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.pointlion.mvc.admin.oa.workflow.WorkFlowService;
import com.pointlion.mvc.common.base.BaseController;
import com.pointlion.mvc.common.model.OaBumph;
import com.pointlion.mvc.common.model.OaHotline;
import com.pointlion.mvc.common.model.OaLetter;
import com.pointlion.mvc.common.model.OaSenddoc;
import com.pointlion.mvc.common.model.OaStepHistory;
import com.pointlion.mvc.common.model.SysAttachment;
import com.pointlion.mvc.common.model.SysOrg;
import com.pointlion.mvc.common.model.SysUser;
import com.pointlion.mvc.common.utils.StringUtil;
import com.pointlion.mvc.common.utils.UuidUtil;
import com.pointlion.plugin.shiro.ShiroKit;



public class OaStepHistoryController extends BaseController {
	public static final OaStepHistoryService service = OaStepHistoryService.me;
	public static WorkFlowService wfservice = WorkFlowService.me;
	/***
	 * get list page
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
		String ftitle = java.net.URLDecoder.decode(getPara("ftitle",""),"UTF-8");
		String type = getPara("type","");
    	Page<Record> page = service.getPage(Integer.valueOf(curr),Integer.valueOf(pageSize),startTime,endTime,type,ftitle);
    	renderPage(page.getList(),"",page.getTotalRow());
    }
    /***
     * save data
     */
    public void save(){
    	OaStepHistory o = getModel(OaStepHistory.class);
    	if(StrKit.notBlank(o.getId())){
    		o.update();
    	}else{
    		o.setId(UuidUtil.getUUID());
    		//o.setCreateTime(DateUtil.getCurrentTime());
    		o.save();
    	}
    	renderSuccess();
    }
    
    
    public void modifyNbyj() {
    	
    	
    	OaBumph o = getModel(OaBumph.class);
    	
    	System.out.println(o.getId());
    	
    	OaBumph findById = OaBumph.dao.findById(o.getId());
    	findById.setNbyj(o.getNbyj());
    	findById.update();
    	
       	System.out.println(o.getNbyj());
       
    	renderSuccess();
    	
    }
    
    
    /***
     * edit page
     */
    public void getEditPage(){
    	String id = getPara("id");
    	String view = getPara("view");
		setAttr("view", view);
		OaStepHistory o = new OaStepHistory();
		if(StrKit.notBlank(id)){
    		o = service.getById(id);
    		if("detail".equals(view)){
    		 
    		}
    	}else{
    	 
    	}
		
		String type=getPara("type");
		String oid=getPara("oid");
		if(type.equals("1")) {
			
			List<SysOrg> orgList = SysOrg.dao.find("select id  from  sys_org where  name='中心领导'");
			List<SysUser> userList = SysUser.dao.find("select * from  sys_user where orgid='"+orgList.get(0).getId()+"' order by sort");
			setAttr("userList",userList);
			
			
			List<SysUser> nbr = SysUser.dao.find("select * from  sys_user where orgid='"+ShiroKit.getUserOrgId()+"' and position in ('2','6') order by sort");
			
			setAttr("nbr",nbr);
			
			
			
			OaBumph  obump = OaBumph.dao.findById(oid);
			
			
				
		
				if(obump.getNbrids()!=null && obump.getNbrids().indexOf(ShiroKit.getUserId())!=-1) {
					
					setAttr("canmodify",true);
				}else {
					
					setAttr("canmodify",false);	
				}
				
				
			
			setAttr("o", obump);
	    	setAttr("formModelName",StringUtil.toLowerCaseFirstOne(OaBumph.class.getSimpleName()));
			renderIframe("hpreceive.html");
			
		}else if(type.equals("2")) {
			OaSenddoc sendDoc=OaSenddoc.dao.findById(oid);
			setAttr("o", sendDoc);
			SysAttachment sa = SysAttachment.dao.findFirst("select  * from sys_attachment  where business_id='"+oid+"' and  des='1'");
			if(sa!=null) {
				setAttr("fid", sa.getId());
				setAttr("uploadperson",sa.getCreateUserName());
				setAttr("filedName",sa.getFileName());
			}
	    	setAttr("formModelName",StringUtil.toLowerCaseFirstOne(OaSenddoc.class.getSimpleName()));
			renderIframe("hpSend.html");
		}else if(type.equals("3")) {
			OaHotline hotLine=OaHotline.dao.findById(oid);
			
			setAttr("o", hotLine);
	    	setAttr("formModelName",StringUtil.toLowerCaseFirstOne(OaHotline.class.getSimpleName()));
			renderIframe("hpHotLine.html");
		}else if(type.equals("4")) {
			OaLetter letter=OaLetter.dao.findById(oid);
			setAttr("o", letter);
	    	setAttr("formModelName",StringUtil.toLowerCaseFirstOne(OaLetter.class.getSimpleName()));
			renderIframe("hpLetter.html");
			
		}else {
			
			setAttr("o", o);
			setAttr("formModelName",StringUtil.toLowerCaseFirstOne(OaStepHistory.class.getSimpleName()));
			renderIframe("edit.html");
		}
		
		
		
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

	
}