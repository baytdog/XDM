package com.pointlion.mvc.admin.oa.apply.buy;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.pointlion.mvc.admin.oa.workflow.WorkFlowService;
import com.pointlion.mvc.common.base.BaseController;
import com.pointlion.mvc.common.model.OaApplyBuy;
import com.pointlion.mvc.common.model.OaApplyBuyItem;
import com.pointlion.mvc.common.model.SysOrg;
import com.pointlion.mvc.common.model.SysUser;
import com.pointlion.mvc.common.utils.Constants;
import com.pointlion.mvc.common.utils.DateUtil;
import com.pointlion.mvc.common.utils.StringUtil;
import com.pointlion.mvc.common.utils.UuidUtil;
import com.pointlion.plugin.shiro.ShiroKit;

import java.util.List;


public class OaApplyBuyController extends BaseController {
	public static final OaApplyBuyService service = OaApplyBuyService.me;
	public static WorkFlowService wfservice = WorkFlowService.me;
	/***
	 * 列表页面
	 */
	public void getListPage(){

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
    	OaApplyBuy o = getModel(OaApplyBuy.class);
    	List<OaApplyBuyItem> newItemList = getModels(OaApplyBuyItem.class,"oaApplyBuyItemList", true);
    	if(StrKit.notBlank(o.getId())){
    		service.deleteByBuyId(o.getId());
    		o.update();//更新主表
    		for(OaApplyBuyItem i :newItemList){
    			if(StrKit.notBlank(i.getName())||StrKit.notBlank(i.getCount())||StrKit.notBlank(i.getModelNum())||StrKit.notBlank(i.getSumPrice())||StrKit.notBlank(i.getSupplyOrgName())||StrKit.notBlank(i.getUnitPrice())){
    				i.setBuyId(o.getId());
    				i.save();
    			}
        	}
    	}else{
    		String id = UuidUtil.getUUID();
    		for(OaApplyBuyItem i :newItemList){
    			if(StrKit.notBlank(i.getName())||StrKit.notBlank(i.getCount())||StrKit.notBlank(i.getModelNum())||StrKit.notBlank(i.getSumPrice())||StrKit.notBlank(i.getSupplyOrgName())||StrKit.notBlank(i.getUnitPrice())){
    				i.setBuyId(id);
    				i.save();
    			}
        	}
    		o.setId(id);
    		o.setCreateTime(DateUtil.getCurrentTime());
    		o.save();
    	}
    	renderSuccess();
    }
    /***
     * 获取编辑页面
     */
    public void getEditPage(){

    	String id = getPara("id");
    	if(StrKit.notBlank(id)){//修改
    		OaApplyBuy o = service.getById(id);
    		setAttr("o", o);
    		//是否是查看详情页面
    		String view = getPara("view");//查看
    		if("detail".equals(view)){
    			setAttr("view", view);
    			if(StrKit.notBlank(o.getProcInsId())){
    				setAttr("procInsId", o.getProcInsId());
    				setAttr("defId", wfservice.getDefIdByInsId(o.getProcInsId()));
    			}
    		}
    		List<OaApplyBuyItem> itemList = OaApplyBuyItem.dao.getAllItemByBuyId(o.getId());
    		setAttr("itemList", itemList);
    	}else{
    		SysUser user = SysUser.dao.findById(ShiroKit.getUserId());
    		SysOrg org = SysOrg.dao.findById(user.getOrgid());
    		setAttr("user", user);
    		setAttr("org", org);
    	}
    	setAttr("formModelName",StringUtil.toLowerCaseFirstOne(OaApplyBuy.class.getSimpleName()));//模型名称
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
    	OaApplyBuy o = OaApplyBuy.dao.getById(id);
    	o.setIfSubmit(Constants.IF_SUBMIT_YES);
    	String insId = wfservice.startProcess(id, o,o.getProjectName(),null);
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
    		OaApplyBuy o = OaApplyBuy.dao.getById(id);
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