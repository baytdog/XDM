package com.pointlion.mvc.admin.grap.dbsource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Config;
import com.jfinal.plugin.activerecord.DbKit;
import com.jfinal.plugin.activerecord.Page;

import com.pointlion.mvc.admin.oa.workflow.WorkFlowService;
import com.pointlion.mvc.common.base.BaseController;
import com.pointlion.mvc.common.model.GrapDbSource;
import com.pointlion.mvc.common.model.SysOrg;
import com.pointlion.mvc.common.model.SysUser;
import com.pointlion.mvc.common.utils.DateUtil;
import com.pointlion.mvc.common.utils.StringUtil;
import com.pointlion.mvc.common.utils.UuidUtil;
import com.pointlion.plugin.shiro.ShiroKit;




public class GrapDbSourceController extends BaseController {
	public static final GrapDbSourceService service = GrapDbSourceService.me;
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
    	Page<GrapDbSource> page = service.getPage(Integer.valueOf(curr),Integer.valueOf(pageSize));
    	List<GrapDbSource> list = page.getList(); 
    	for(GrapDbSource r : list){
    		Config cf = DbKit.getConfig(r.getDbConfigName());
    		if(cf!=null){
    			r.put("ifConnected", "1");
    		}else{
    			r.put("ifConnected", "0");
    		}
    	}
    	renderPage(list,"",page.getTotalRow());
    }
    /***
     * 保存
     */
    public void save(){
    	GrapDbSource o = getModel(GrapDbSource.class);
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

    	String id = getPara("id");
    	String view = getPara("view");
		setAttr("view", view);
    	if(StrKit.notBlank(id)){//修改
    		GrapDbSource o = service.getById(id);
    		setAttr("o", o);
    		//是否是查看详情页面
    		if("detail".equals(view)){
    			setAttr("view", view);
    		}
    	}else{
    		SysUser user = SysUser.dao.findById(ShiroKit.getUserId());
    		SysOrg org = SysOrg.dao.findById(user.getOrgid());
    		setAttr("user", user);
    		setAttr("org", org);
    	}
    	setAttr("formModelName",StringUtil.toLowerCaseFirstOne(GrapDbSource.class.getSimpleName()));//模型名称
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
     * 测试连接
     * @throws Exception 
     */
    public void testConnect() throws Exception{
    	String id = getPara("id","");
    	GrapDbSource db = GrapDbSource.dao.getById(id);
    	Boolean b = service.testConnect(db);
    	if(b){
    		renderSuccess("连接成功！");
    	}else{
    		renderError("连接失败！");
    	}
    }
    /***
     * 断开连接
     */
    public void disConnect(){
    	String id = getPara("id","");
    	GrapDbSource db = GrapDbSource.dao.getById(id);
    	service.disConnect(db);
    	renderSuccess();
    }
}