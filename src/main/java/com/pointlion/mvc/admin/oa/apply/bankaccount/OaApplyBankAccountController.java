package com.pointlion.mvc.admin.oa.apply.bankaccount;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.pointlion.mvc.admin.oa.common.BusinessUtil;
import com.pointlion.mvc.admin.oa.common.FlowCCService;
import com.pointlion.mvc.admin.oa.workflow.WorkFlowService;
import com.pointlion.mvc.common.base.BaseController;
import com.pointlion.mvc.common.model.OaApplyBankAccount;
import com.pointlion.mvc.common.model.OaProject;
import com.pointlion.mvc.common.model.SysOrg;
import com.pointlion.mvc.common.model.SysUser;
import com.pointlion.mvc.common.utils.Constants;
import com.pointlion.mvc.common.utils.DateUtil;
import com.pointlion.mvc.common.utils.StringUtil;
import com.pointlion.mvc.common.utils.UuidUtil;
import com.pointlion.plugin.shiro.ShiroKit;

import java.io.UnsupportedEncodingException;
import java.util.Date;




public class OaApplyBankAccountController extends BaseController {
	public static final OaApplyBankAccountService service = OaApplyBankAccountService.me;
	public static WorkFlowService wfservice = WorkFlowService.me;
	public static FlowCCService ccService = FlowCCService.me;
	/***
	 * 列表页面
	 */
	public void getListPage(){
		String type = getPara("type");
		String name = service.getTypeName(type);
		setAttr("type",type);

    	renderIframe("list.html");
    }
	/***
     * 获取分页数据
	 * @throws UnsupportedEncodingException 
     **/
    public void listData() throws UnsupportedEncodingException{
    	String type = getPara("type","");
    	String curr = getPara("pageNumber");
    	String pageSize = getPara("pageSize");
    	String endTime = getPara("endTime","");
    	String startTime = getPara("startTime","");
    	String name = java.net.URLDecoder.decode(getPara("name",""),"UTF-8");
    	String c = getPara("c","");
    	if(StrKit.notBlank(c)){
    		if(c.equals("myCC")){
    			Page<Record> page = wfservice.getFlowCCPage(Integer.valueOf(curr),Integer.valueOf(pageSize), service.getDefKeyByType(type), ShiroKit.getUserId(), service.getQuerySql(type, name, startTime, endTime));
            	renderPage(page.getList(),"",page.getTotalRow());
    		}else if(c.equals("myTask")){
    			Page<Record> page = wfservice.getToDoPageByKey(Integer.valueOf(curr),Integer.valueOf(pageSize), service.getDefKeyByType(type), ShiroKit.getUsername(), service.getQuerySql(type, name, startTime, endTime));
            	renderPage(page.getList(),"",page.getTotalRow());
    		}else if(c.equals("myHaveDone")){
    			Page<Record> page = wfservice.getHavedonePage(Integer.valueOf(curr),Integer.valueOf(pageSize), service.getDefKeyByType(type), ShiroKit.getUsername(), service.getQuerySql(type, name, startTime, endTime));
            	renderPage(page.getList(),"",page.getTotalRow());
    		}else{
    			Page<Record> page = service.getPage(Integer.valueOf(curr),Integer.valueOf(pageSize),type,name,startTime,endTime);
            	renderPage(page.getList(),"",page.getTotalRow());	
    		}
    	}else{
    		Page<Record> page = service.getPage(Integer.valueOf(curr),Integer.valueOf(pageSize),type,name,startTime,endTime);
        	renderPage(page.getList(),"",page.getTotalRow());	
    	}
    	
    }
    /***
     * 保存
     */
    public void save(){
    	OaApplyBankAccount o = getModel(OaApplyBankAccount.class);
    	if(StrKit.notBlank(o.getProjectId())){
    		o.setProjectName(OaProject.dao.getById(o.getProjectId()).getProjectName());
    	}
    	if(StrKit.notBlank(o.getId())){
    		o.update();
    		ccService.addFlowCC(this, o.getId(), service.getDefKeyByType(o.getType()),OaApplyBankAccount.tableName);
    	}else{
    		String username = ShiroKit.getUsername();
    		String id = UuidUtil.getUUID();
    		o.setId(id);
    		o.setCreateTime(DateUtil.getCurrentTime());
    		
    		Integer num = BusinessUtil.getMaxNum(o); 
    		o.setBankaccountNumNum(num);//自增编号
    		o.setBankaccountNum(BusinessUtil.getAddNum(num,username));
    		o.setBankaccountNumYear(DateUtil.format(new Date(), 23));
    		
    		o.save();
    		ccService.addFlowCC(this, id, service.getDefKeyByType(o.getType()),OaApplyBankAccount.tableName);
    	}
    	renderSuccess();
    }
    /***
     * 获取编辑页面
     */
    public void getEditPage(){
    	String type = getPara("type");
    	setAttr("type", type);
    	String name = service.getTypeName(type);

    	String id = getPara("id");
    	String view = getPara("view");//查看
    	setAttr("view", view);
    	if(StrKit.notBlank(id)){//修改
    		OaApplyBankAccount o = service.getById(id);
    		setAttr("o", o);
    		//是否是查看详情页面
    		if("detail".equals(view)){
    			if(StrKit.notBlank(o.getProcInsId())){
    				setAttr("procInsId", o.getProcInsId());
    				setAttr("defId", wfservice.getDefIdByInsId(o.getProcInsId()));
    			}
    		}
    		if(StrKit.notBlank(o.getProjectId())){
    			setAttr("projectName", OaProject.dao.getById(o.getProjectId()).getProjectName());
        		o.setProjectName(OaProject.dao.getById(o.getProjectId()).getProjectName());
        	}
    		ccService.setAttrFlowCC(this, o.getId(), service.getDefKeyByType(o.getType()));
    	}else{
    		SysUser user = SysUser.dao.findById(ShiroKit.getUserId());
    		SysOrg org = SysOrg.dao.findById(user.getOrgid());
    		setAttr("user", user);
    		setAttr("org", org);
    	}
    	setAttr("formModelName",StringUtil.toLowerCaseFirstOne(OaApplyBankAccount.class.getSimpleName()));//模型名称
    	
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
    	OaApplyBankAccount o = OaApplyBankAccount.dao.getById(id);
    	o.setIfSubmit(Constants.IF_SUBMIT_YES);
    	String defkey = service.getDefKeyByType(o.getType());
    	String insId = wfservice.startProcess(id,o,defkey,o.getTitle(),null);
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
    		OaApplyBankAccount o = OaApplyBankAccount.dao.getById(id);
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