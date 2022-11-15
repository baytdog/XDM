package com.pointlion.mvc.admin.oa.outm;

import java.util.HashMap;
import java.util.Map;

import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.pointlion.mvc.common.base.BaseController;
import com.pointlion.mvc.admin.oa.workflow.WorkFlowService;
import com.pointlion.mvc.common.utils.StringUtil;
import com.pointlion.mvc.common.model.OaBumph;
import com.pointlion.mvc.common.model.OaOutm;
import com.pointlion.mvc.common.model.SysUser;
import com.pointlion.mvc.common.model.SysOrg;
import com.pointlion.mvc.common.utils.UuidUtil;
import com.pointlion.mvc.common.utils.Constants;
import com.pointlion.mvc.admin.oa.common.OAConstants;
import com.pointlion.mvc.common.utils.DateUtil;
import com.pointlion.plugin.shiro.ShiroKit;



public class OaOutmController extends BaseController {
	public static final OaOutmService service = OaOutmService.me;
	public static WorkFlowService wfservice = WorkFlowService.me;
	/***
	 * get list page
	 */
	public void getListPage(){
		renderIframe("list.html");
    }
	/***
     * list page data
     **/
    public void listData(){
    	String curr = getPara("pageNumber");
    	String pageSize = getPara("pageSize");
		String endTime = getPara("endTime","");
		String startTime = getPara("startTime","");
		String applyUser = getPara("applyUser","");
    	Page<Record> page = service.getPage(Integer.valueOf(curr),Integer.valueOf(pageSize),startTime,endTime,applyUser);
    	renderPage(page.getList(),"",page.getTotalRow());
    }
    /***
     * save data
     */
    public void save(){
    	OaOutm o = getModel(OaOutm.class);
    	if(StrKit.notBlank(o.getId())){
    		o.update();
    	}else{
    		o.setId(UuidUtil.getUUID());
    		o.setCreateTime(DateUtil.getCurrentTime());
    		o.save();
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
		OaOutm o = new OaOutm();
		if(StrKit.notBlank(id)){
    		o = service.getById(id);
    		if("detail".equals(view)){
    			if(StrKit.notBlank(o.getProcInsId())){
    				setAttr("procInsId", o.getProcInsId());
    				setAttr("defId", wfservice.getDefIdByInsId(o.getProcInsId()));
    			}
    		}
    	}else{
    		SysUser user = SysUser.dao.findById(ShiroKit.getUserId());
    	/*	SysOrg org = SysOrg.dao.findById(user.getOrgid());
			o.setOrgId(org.getId());
			o.setOrgName(org.getName());
			o.setUserid(user.getId());
			o.setApplyerName(user.getName());*/
    	}
		setAttr("o", o);
    	setAttr("formModelName",StringUtil.toLowerCaseFirstOne(OaOutm.class.getSimpleName()));
		renderIframe("edit.html");
    }
    /***
     * del
     * @throws Exception
     */
/*    public void delete() throws Exception{
		String ids = getPara("ids");
		service.deleteByIds(ids);
    	renderSuccess("删除成功!");
    }*/
    /***
     * submit
     */
  /*  public void startProcess(){
    	String id = getPara("id");
    	OaOutm o = OaOutm.dao.getById(id);
    	o.setIfSubmit(Constants.IF_SUBMIT_YES);
		String insId = wfservice.startProcess(id, o,null,null);
    	o.setProcInsId(insId);
    	o.update();
    	renderSuccess("submit success");
    }*/

    
    
    public void getDraftEditPage(){
		String parentPath = this.getRequest().getServletPath().substring(0,this.getRequest().getServletPath().lastIndexOf("/")+1); 

		//添加和修改
    	String id = getPara("id");//修改
		String view = getPara("view");//查看
		setAttr("view", view);
		if(StrKit.notBlank(id)){//修改
			OaOutm o = OaOutm.dao.findById(id);
			setAttr("o", o);
			//获取主送和抄送单位
			//service.setAttrFirstSecond(this,o.getId());
			//是否是查看详情页面
			if("detail".equals(view)){
    			if(StrKit.notBlank(o.getProcInsId())){
    				setAttr("procInsId", o.getProcInsId());
    				setAttr("defId", wfservice.getDefIdByInsId(o.getProcInsId()));
    			}
    		}
    	}else{//新增
    		OaOutm o = new OaOutm();
    		String userId = ShiroKit.getUserId();//用户主键
    		SysUser user = SysUser.dao.getById(userId);//用户对象
    		SysOrg org = SysOrg.dao.getById(user.getOrgid());//单位对象
    		o.setDocNumYear(DateUtil.getCurrentYear());
    		o.setSenderId(userId);
    		o.setSenderName(user.getName());
    		o.setSenderOrgid(org.getId());
    		o.setSenderOrgname(org.getName());
    		setAttr("o",o);
    	}
    	setAttr("formModelName",StringUtil.toLowerCaseFirstOne(OaOutm.class.getSimpleName()));//模型名称
    	renderIframe("edit.html");
	}
	
    
    
    
    /***
     * 公文起草保存
     */
    public void draftSave(){
    	OaOutm o = getModel(OaOutm.class);
		o.setCreateOrgId(ShiroKit.getUserOrgId());
		o.setCreateUserId(ShiroKit.getUserId());
		o.setCreateOrgName(ShiroKit.getUserOrgName());
		o.setCreateUserName(ShiroKit.getUserName());
		o.setCreateTime(DateUtil.getCurrentTime());
    /*	String firstOrgId = getPara("firstOrgId");//主送单位
    	String secondOrgId = getPara("secondOrgId");//抄送单位
*/    	service.save(o);
    	renderSuccess();
    }
    
    
    
    /***
	 * 获取公文起草页面
	 */
	public void getDraftListPage(){

		renderIframe("list.html");
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
	 * 公文起草列表数据
	 */
    public void draftListData(){
    	String curr = getPara("pageNumber");
    	String pageSize = getPara("pageSize");
    	Page<OaOutm> page = OaOutm.dao.getPage(Integer.valueOf(curr),Integer.valueOf(pageSize),"");
    	renderPage(page.getList(),"" ,page.getTotalRow());
    }
    
    
    
    /***
     * 删除公文
     */
    public void delete(){
    	String id = getPara("ids");
    	service.delete(id);
    	renderSuccess("删除成功!");
    }
    
}