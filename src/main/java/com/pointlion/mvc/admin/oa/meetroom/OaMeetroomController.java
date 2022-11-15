package com.pointlion.mvc.admin.oa.meetroom;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.pointlion.mvc.admin.oa.workflow.WorkFlowService;
import com.pointlion.mvc.common.base.BaseController;
import com.pointlion.mvc.common.model.OaMeetroom;
import com.pointlion.mvc.common.utils.StringUtil;
import com.pointlion.mvc.common.utils.UuidUtil;



public class OaMeetroomController extends BaseController {
	public static final OaMeetroomService service = OaMeetroomService.me;
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
    	OaMeetroom o = getModel(OaMeetroom.class);
    	if(StrKit.notBlank(o.getId())){
    		o.update();
    	}else{
    		o.setId(UuidUtil.getUUID());
    		//o.setCreateTime(DateUtil.getCurrentTime());
    		o.save();
    	}
    	//renderSuccess();
    	renderSuccess(o, "");
    }
    /***
     * edit page
     */
    public void getEditPage(){
    	String id = getPara("id");
    	String view = getPara("view");
		setAttr("view", view);
		OaMeetroom o = new OaMeetroom();
		if(StrKit.notBlank(id)){
    		o = service.getById(id);
    		if("detail".equals(view)){
    			/*if(StrKit.notBlank(o.getProcInsId())){
    				setAttr("procInsId", o.getProcInsId());
    				setAttr("defId", wfservice.getDefIdByInsId(o.getProcInsId()));
    			}*/
    		}
    	}else{
    		//SysUser user = SysUser.dao.findById(ShiroKit.getUserId());
    		//SysOrg org = SysOrg.dao.findById(user.getOrgid());
			/*o.setOrgId(org.getId());
			o.setOrgName(org.getName());
			o.setUserid(user.getId());
			o.setApplyerName(user.getName());*/
    	}
		setAttr("o", o);
    	setAttr("formModelName",StringUtil.toLowerCaseFirstOne(OaMeetroom.class.getSimpleName()));
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
    	OaMeetroom o = OaMeetroom.dao.getById(id);
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
    		OaMeetroom o = OaMeetroom.dao.getById(id);
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

	
}