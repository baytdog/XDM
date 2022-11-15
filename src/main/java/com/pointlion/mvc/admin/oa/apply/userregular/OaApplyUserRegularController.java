package com.pointlion.mvc.admin.oa.apply.userregular;

import java.util.HashMap;
import java.util.Map;

import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import com.pointlion.mvc.admin.oa.workflow.WorkFlowService;
import com.pointlion.mvc.common.base.BaseController;
import com.pointlion.mvc.common.model.OaApplyUserRegular;
import com.pointlion.mvc.common.model.SysOrg;
import com.pointlion.mvc.common.model.SysUser;
import com.pointlion.mvc.common.utils.Constants;
import com.pointlion.mvc.common.utils.DateUtil;
import com.pointlion.mvc.common.utils.StringUtil;
import com.pointlion.mvc.common.utils.UuidUtil;
import com.pointlion.plugin.shiro.ShiroKit;


public class OaApplyUserRegularController extends BaseController {
	public static final OaApplyUserRegularService service = OaApplyUserRegularService.me;
	static WorkFlowService wfservice = WorkFlowService.me;
	/***
	 * 列表页面
	 */
	public void getApplyListPage(){

    	renderIframe("list.html");
    }
	/***
     * 获取分页数据
     **/
    public void listData(){
    	String curr = getPara("pageNumber");
    	String pageSize = getPara("pageSize");
    	Page<Record> page = service.getPage(Integer.valueOf(curr),Integer.valueOf(pageSize));
    	renderPage(page.getList(),"",page.getTotalRow());
    }
    /***
     * 保存
     */
    public void save(){
    	OaApplyUserRegular o = getModel(OaApplyUserRegular.class);
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
     * 获取编辑页面
     */
    public void getEditPage(){

    	//添加和修改
    	String id = getPara("id");
    	if(StrKit.notBlank(id)){//修改
    		OaApplyUserRegular o = service.getById(id);
    		setAttr("o", o);
    		SysUser user = SysUser.dao.findById(o.getUserid());
    		setAttr("user", user);
    		//是否是查看详情页面
    		String view = getPara("view");//查看
    		if("detail".equals(view)){
    			setAttr("view", view);
    			if(StrKit.notBlank(o.getProcInsId())){
    				setAttr("procInsId", o.getProcInsId());
    				setAttr("defId", wfservice.getDefIdByInsId(o.getProcInsId()));
    			}
    		}
    	}else{
    		SysUser user = SysUser.dao.findById(ShiroKit.getUserId());
    		SysOrg org = SysOrg.dao.findById(user.getOrgid());
    		setAttr("user", user);
    		setAttr("org", org);
    	}
    	setAttr("formModelName",StringUtil.toLowerCaseFirstOne(OaApplyUserRegular.class.getSimpleName()));//模型名称
    	renderIframe("edit.html");
    }
    /***
     * 删除
     * @throws Exception
     */
    public void delete() throws Exception{
		String ids = getPara("ids");
    	//执行删除
		service.deleteByIds(ids);
    	renderSuccess("删除成功!");
    }
    /***
     * 校验转正状态
     */
    public void checkRegular(){
    	SysUser user = SysUser.dao.getById(ShiroKit.getUserId());
//    	类型.0：试用期。1：正式员工。2：离职。3：实习
    	String type = user.getType();
    	if("0".equals(type)||"3".equals(type)){
    		renderSuccess();
    	}else if("1".equals(type)){
    		renderError("您已是正式员工！");
    	}else if("2".equals(type)){
    		renderError("您已离职！请联系管理员检查员工状态！");
    	}else{
    		renderError("请联系管理员检查员工状态！");
    	}
    }
    
    /***
     * 校验离职状态
     */
//    public void checkDimission(){
//    	SysUser user = SysUser.dao.getById(ShiroKit.getUserId());
////    	类型.0：试用期。1：正式员工。2：离职。3：实习
//    	String type = user.getType();
//    	if("0".equals(type)||"3".equals(type)){
//    		renderSuccess();
//    	}else if("1".equals(type)){
//    		renderError("您已是正式员工！");
//    	}else if("2".equals(type)){
//    		renderError("您已离职！请联系管理员检查员工状态！");
//    	}else{
//    		renderError("请联系管理员检查员工状态！");
//    	}
//    }
    
    /***
     * 提交
     */
    public void startProcess(){
    	String id = getPara("id");
    	OaApplyUserRegular o = OaApplyUserRegular.dao.getById(id);
    	o.setIfSubmit(Constants.IF_SUBMIT_YES);
    	String insId = wfservice.startProcess(id,o,o.getTitle(),null);
    	o.setProcInsId(insId);
    	o.update();
    	renderSuccess("提交成功");
    }
    /***
     * 撤回
     */
    public void callBack(){
    	String id = getPara("id");
    	try{
        	OaApplyUserRegular o = OaApplyUserRegular.dao.getById(id);
        	wfservice.callBack(o.getProcInsId());
        	o.setIfSubmit(Constants.IF_SUBMIT_NO);
        	o.setProcInsId("");
        	o.update();
    		renderSuccess("撤回成功");
    	}catch(Exception e){
    		e.printStackTrace();
    		renderError("撤回失败");
    	}
    }
}