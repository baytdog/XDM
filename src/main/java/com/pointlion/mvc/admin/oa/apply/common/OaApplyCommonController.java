package com.pointlion.mvc.admin.oa.apply.common;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.pointlion.mvc.common.base.BaseController;
import com.pointlion.mvc.admin.oa.workflow.WorkFlowService;
import com.pointlion.mvc.common.utils.StringUtil;
import com.pointlion.mvc.common.model.OaApplyCommon;
import com.pointlion.mvc.common.model.SysUser;
import com.pointlion.mvc.common.model.SysOrg;
import com.pointlion.mvc.common.utils.UuidUtil;
import com.pointlion.mvc.common.utils.Constants;
import com.pointlion.mvc.admin.oa.common.OAConstants;
import com.pointlion.mvc.common.utils.DateUtil;
import com.pointlion.plugin.shiro.ShiroKit;



public class OaApplyCommonController extends BaseController {
    public static final OaApplyCommonService service = OaApplyCommonService.me;
    public static WorkFlowService wfservice = WorkFlowService.me;
    /***
     * get list page
     */
    public void getListPage(){
        setAttr("type",getPara("type","1"));
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
        String type = getPara("type","");
        Page<Record> page = service.getPage(Integer.valueOf(curr),Integer.valueOf(pageSize),startTime,endTime,applyUser,type);
        renderPage(page.getList(),"",page.getTotalRow());
    }
    /***
     * save
     */
    public void save(){
        OaApplyCommon o = getModel(OaApplyCommon.class);
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
     * 修改
     */
    public void getEditPage(){
        String id = getPara("id");
        String view = getPara("view");
        setAttr("view", view);
        if(StrKit.notBlank(id)){
            OaApplyCommon o = service.getById(id);
            setAttr("o", o);
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
        setAttr("formModelName",StringUtil.toLowerCaseFirstOne(OaApplyCommon.class.getSimpleName()));
        renderIframe("edit.html");
    }
    /***
     * delete
     * @throws Exception
     */
    public void delete() throws Exception{
        String ids = getPara("ids");
        service.deleteByIds(ids);
        renderSuccess("删除成功!");
    }
    /***
     * startProcess
     */
    public void startProcess(){
        String id = getPara("id");
        OaApplyCommon o = OaApplyCommon.dao.getById(id);
        o.setIfSubmit(Constants.IF_SUBMIT_YES);
    	String insId = wfservice.startProcess(id,o ,o.getTitle(),null);
    	o.setProcInsId(insId);
        o.update();
        renderSuccess("提交成功");
    }
    /***
     * callBack
     */
    public void callBack(){
        String id = getPara("id");
        try{
            OaApplyCommon o = OaApplyCommon.dao.getById(id);
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