package com.pointlion.mvc.admin.oa.apply.finance;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.pointlion.mvc.admin.oa.common.BusinessUtil;
import com.pointlion.mvc.admin.oa.common.FlowCCService;
import com.pointlion.mvc.admin.oa.workflow.WorkFlowService;
import com.pointlion.mvc.common.base.BaseController;
import com.pointlion.mvc.common.model.*;
import com.pointlion.mvc.common.utils.Constants;
import com.pointlion.mvc.common.utils.DateUtil;
import com.pointlion.mvc.common.utils.StringUtil;
import com.pointlion.mvc.common.utils.UuidUtil;
import com.pointlion.plugin.shiro.ShiroKit;

import java.io.UnsupportedEncodingException;
import java.util.Date;




public class OaApplyFinanceController extends BaseController {
	public static final OaApplyFinanceService service = OaApplyFinanceService.me;
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
    	OaApplyFinance o = getModel(OaApplyFinance.class);
    	if(StrKit.notBlank(o.getProjectId())){
    		o.setProjectName(OaProject.dao.getById(o.getProjectId()).getProjectName());
    	}
    	if(StrKit.notBlank(o.getId())){
    		OaProject p = OaProject.dao.getById(o.getProjectId());
    		String title = "申请人："+o.getApplyerName()+"   项目："+(p!=null?p.getProjectName():"");
    		o.setTitle(title);
    		o.update();
    		ccService.addFlowCC(this, o.getId(), service.getDefKeyByType(o.getType()),OaApplyFinance.tableName);
    	}else{
    		String username = ShiroKit.getUsername();
    		String id = UuidUtil.getUUID();
    		o.setId(id);
    		OaProject p = OaProject.dao.getById(o.getProjectId());
    		String title = "申请人："+o.getApplyerName()+"   项目："+(p!=null?p.getProjectName():"");
    		o.setTitle(title);
    		o.setCreateTime(DateUtil.getCurrentTime());
    		
    		Integer num = BusinessUtil.getMaxNum(o); 
    		o.setFinanceNumNum(num);//自增编号
    		o.setFinanceNum(BusinessUtil.getAddNum(num,username));
    		o.setFinanceNumYear(DateUtil.format(new Date(), 23));
    		
    		o.save();
    		ccService.addFlowCC(this, id, service.getDefKeyByType(o.getType()),OaApplyFinance.tableName);
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
		String view = getPara("view");
		setAttr("view", view);
		//是否是查看详情页面
    	if(StrKit.notBlank(id)){//修改
    		OaApplyFinance o = service.getById(id);
    		setAttr("o", o);
    		if("detail".equals(view)){
    			if(StrKit.notBlank(o.getProcInsId())){
    				setAttr("procInsId", o.getProcInsId());
    				setAttr("defId", wfservice.getDefIdByInsId(o.getProcInsId()));
    			}
    		}
    		OaProject p = OaProject.dao.getById(o.getProjectId());
    		setAttr("projectName", p!=null?p.getProjectName():"");
    		OaContract c = OaContract.dao.getById(o.getContractId());
    		setAttr("contractName", c!=null?c.getName():"");
    		String aboutOrgId = o.getAboutOrgid();
    		if(StrKit.notBlank(aboutOrgId)){
    			SysOrg aboutOrg = SysOrg.dao.getById(aboutOrgId);
    			if(aboutOrg!=null){
    				setAttr("aboutOrg", aboutOrg);
    			}
    		}
    		ccService.setAttrFlowCC(this, o.getId(), service.getDefKeyByType(o.getType()));
    	}else{
    		SysUser user = SysUser.dao.findById(ShiroKit.getUserId());
    		SysOrg org = SysOrg.dao.findById(user.getOrgid());
    		setAttr("user", user);
    		setAttr("org", org);
    	}
    	setAttr("formModelName",StringUtil.toLowerCaseFirstOne(OaApplyFinance.class.getSimpleName()));//模型名称
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
     * @throws IllegalAccessException 
     * @throws InstantiationException 
     * @throws ClassNotFoundException 
     */
    public void startProcess() throws ClassNotFoundException, InstantiationException, IllegalAccessException{
    	String id = getPara("id");
    	OaApplyFinance o = OaApplyFinance.dao.getById(id);
		o.setIfSubmit(Constants.IF_SUBMIT_YES);
    	String insId = wfservice.startProcess(id,o,null,null);
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
    		OaApplyFinance o = OaApplyFinance.dao.getById(id);
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