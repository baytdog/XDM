package com.pointlion.mvc.admin.xdm.xddict;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.pointlion.mvc.common.base.BaseController;
import com.pointlion.mvc.admin.oa.workflow.WorkFlowService;
import com.pointlion.mvc.common.dto.ZtreeNode;
import com.pointlion.mvc.common.model.*;
import com.pointlion.mvc.common.utils.*;
import com.pointlion.mvc.admin.oa.common.OAConstants;
import com.pointlion.plugin.shiro.ShiroKit;



public class XdDictController extends BaseController {
	public static final XdDictService service = XdDictService.me;
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
		String groupId = getPara("groupId");
		String groupKey = getPara("groupKey");
		if(StrKit.notBlank(groupId)){//用id去查
			Page<Record> page = service.getPage(Integer.valueOf(curr),Integer.valueOf(pageSize),groupId);
			renderPage(page.getList(),"",page.getTotalRow());
		}else if(StrKit.notBlank(groupKey)){//用key去查
			SysDctGroup group = SysDctGroup.dao.getByKey(groupKey);
			if(group!=null){
				groupId = group.getId();
				Page<Record> page = service.getPage(Integer.valueOf(curr),Integer.valueOf(pageSize),groupId);
				renderPage(page.getList(),"",page.getTotalRow());
			}else{
				renderPage(null,"",0);
			}
		}else{
			renderPage(null,"",0);
		}
	}
	/***
	 * save data
	 */
	public void save(){
		XdDict o = getModel(XdDict.class);
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
		if(StrKit.notBlank(id)){//修改
			XdDict o = XdDict.dao.getById(id);
			setAttr("o", o);
			XdDictGroup group = XdDictGroup.dao.getById(o.getGroupId());
			if(group!=null){
				setAttr("group",group);
			}
		}else{
			String groupId = getPara("groupId");
			XdDictGroup group = XdDictGroup.dao.getById(groupId);
			if(group==null){
				group = new XdDictGroup();
				group.setId("");
				group.setName("");
			}
			setAttr("group",group);
		}
		setAttr("formModelName",StringUtil.toLowerCaseFirstOne(XdDict.class.getSimpleName()));//模型名称
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
	 * 查询树
	 */
	public void getDctGroupTree(){
		List<XdDictGroup> groupList = service.getChildrenAllTree(Constants.SYS_MENU_ROOT);
		List<ZtreeNode> nodelist = service.toZTreeNode(groupList,false,false);//数据库中的菜单
		List<ZtreeNode> rootList = new ArrayList<ZtreeNode>();//页面展示的,带根节点
		SysDctGroup org = SysDctGroup.dao.getById(Constants.SYS_MENU_ROOT);
		//声明根节点
		ZtreeNode root = new ZtreeNode();
		if(org!=null){
			root.setId(org.getId());
			root.setName(org.getName());
		}
		root.setChildren(nodelist);
		root.setOpen(true);
		root.setIcon(ContextUtil.getCtx()+"/common/plugins/zTree_v3/css/zTreeStyle/img/diy/1_open.png");
		rootList.add(root);
		renderJson(rootList);
	}








	/***
	 * 分组
	 * 获取编辑页面
	 */
	public void getEditGroupPage(){

		String id = getPara("id");
		String view = getPara("view");
		setAttr("view", view);
		if(StrKit.notBlank(id)){//修改
			XdDictGroup o = XdDictGroup.dao.getById(id);
			setAttr("o", o);
			XdDictGroup group = XdDictGroup.dao.getById(o.getParentId());
			if(group!=null){
				setAttr("group",group);
			}
		}else{
			String groupId = getPara("groupId");
			XdDictGroup group = XdDictGroup.dao.getById(groupId);
			if(group==null){
				group = new XdDictGroup();
				group.setId("");
				group.setName("");
			}
			setAttr("group",group);
		}
		setAttr("formModelName",StringUtil.toLowerCaseFirstOne(XdDictGroup.class.getSimpleName()));//模型名称
		renderIframe("editGroup.html");
	}

	/***
	 * 保存分组
	 */
	public void saveGroup(){
		XdDictGroup o = getModel(XdDictGroup.class);
		if(StrKit.notBlank(o.getId())){
			o.update();
		}else{
			o.setId(UuidUtil.getUUID());
			o.save();
		}
		renderSuccess();
	}


	/***
	 * 删除分组
	 */
	public void deleteGroup(){
		String groupId = getPara("groupId");
		List<XdDict> list = XdDict.dao.getDctByGroupId(groupId);
		List<XdDictGroup> groupList = XdDict.dao.getGroupListByParentId(groupId);
		if(groupList!=null&&groupList.size()>0){
			renderError("该分组下有子分组，清先清除子分组，再进行删除分组操作！");
		}else if(list!=null&&list.size()>0){
			renderError("该分组下有字典数据，清先清空字典数据，再进行删除分组操作！");
		}else{
			XdDictGroup group = XdDictGroup.dao.getById(groupId);
			group.delete();
			renderSuccess();
		}
	}


	/***
	 * 打开一个选择分组的弹层
	 */
	public void getSelectOneDctGroupPage(){
		renderIframeOpen("selectOneDctGroup.html");
	}
	
}