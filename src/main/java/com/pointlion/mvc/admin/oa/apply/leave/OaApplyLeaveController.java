package com.pointlion.mvc.admin.oa.apply.leave;

import java.util.HashMap;
import java.util.Map;

import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import com.pointlion.mvc.admin.oa.workflow.WorkFlowService;
import com.pointlion.mvc.common.base.BaseController;
import com.pointlion.mvc.common.model.OaApplyLeave;
import com.pointlion.mvc.common.model.SysOrg;
import com.pointlion.mvc.common.model.SysUser;
import com.pointlion.mvc.common.utils.Constants;
import com.pointlion.mvc.common.utils.DateUtil;
import com.pointlion.mvc.common.utils.StringUtil;
import com.pointlion.mvc.common.utils.UuidUtil;
import com.pointlion.plugin.shiro.ShiroKit;


public class OaApplyLeaveController extends BaseController {
	public static final OaApplyLeaveService service = OaApplyLeaveService.me;
	static WorkFlowService wfservice = WorkFlowService.me;
	/***
	 * 列表页面
	 */
	public void getApplyListPage(){
		String type = getPara("type");
		String name = "";
		if("1".equals(type)){
			name = "请假申请";
		}else{
			name = "公出申请";
		}

		setAttr("type",getPara("type"));
    	renderIframe("list.html");
    }
	/***
     * 获取分页数据
     **/
    public void listData(){
    	String type = getPara("type");
    	String curr = getPara("pageNumber");
    	String pageSize = getPara("pageSize");
		String endTime = getPara("endTime","");
		String startTime = getPara("startTime","");
		String applyUser = getPara("applyUser","");
    	Page<Record> page = service.getPage(Integer.valueOf(curr),Integer.valueOf(pageSize),type,startTime,endTime,applyUser);
    	renderPage(page.getList(),"",page.getTotalRow());
    }
    /***
     * 保存
     */
    public void save(){
    	OaApplyLeave o = getModel(OaApplyLeave.class);
    	SysUser user = SysUser.dao.getById(o.getUserid());
    	if(StrKit.notBlank(o.getId())){//更新的时候，要重新计算天数
    		if(o.getType().equals("1")){
//    			OaApplyLeave old = OaApplyLeave.dao.getById(o.getId());//老的对象
//    			Integer day = Integer.parseInt(user.getYearHoliday())+Integer.parseInt(old.getDays())-Integer.parseInt(o.getDays());//加上老的减去新的
//				if(day>=0){//如果计算后的天数正数
//					user.setYearHoliday(day+"");//新增请假需要减去年假天数
//					user.update();//更新年假
//				}else{//修改之后的天数过多
//					renderError("年假天数不足，请重新填写！");
//					return;
//				}
    		}
    		o.update();
    	}else{
    		o.setId(UuidUtil.getUUID());
			o.setCreateTime(DateUtil.getCurrentTime());
//			if(o.getType().equals("1")){
//				user.setYearHoliday(Integer.parseInt(user.getYearHoliday())-Integer.parseInt(o.getDays())+"");//新增请假需要减去年假天数
//				user.update();
//			}
    		o.save();
    	}
    	renderSuccess();
    }
    /***
     * 获取编辑页面
     */
    public void getEditPage(){
    	String type = getPara("type");
    	String name = "";
    	if("1".equals(type)){
    		name = "请假申请";
    	}else{
    		name = "外出申请";
    	}
		String view = getPara("view");//查看
		setAttr("view", view);
    	//添加和修改
    	String id = getPara("id");
    	if(StrKit.notBlank(id)){//修改
    		OaApplyLeave o = service.getById(id);
    		SysUser user = SysUser.dao.findById(o.getUserid());
    		setAttr("o", o);
    		setAttr("type",o.getType());
    		setAttr("user", user);
    		//是否是查看详情页面
			if("detail".equals(view)){
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
    		setAttr("type",type);
    	}
    	setAttr("formModelName",StringUtil.toLowerCaseFirstOne(OaApplyLeave.class.getSimpleName()));//模型名称
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
     * 提交
     */
    public void startProcess(){
    	String id = getPara("id");
    	OaApplyLeave o = OaApplyLeave.dao.getById(id);
    	o.setIfSubmit(Constants.IF_SUBMIT_YES);
    	String insId = wfservice.startProcess(id, o,o.getTitle(),null);
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
    		OaApplyLeave o = OaApplyLeave.dao.getById(id);
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