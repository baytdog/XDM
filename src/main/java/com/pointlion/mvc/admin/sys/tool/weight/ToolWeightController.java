package com.pointlion.mvc.admin.sys.tool.weight;

import com.jfinal.aop.Clear;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.pointlion.mvc.common.base.BaseController;
import com.pointlion.mvc.common.model.SysCustomSetting;
import com.pointlion.mvc.common.model.ToolWeight;
import com.pointlion.mvc.common.utils.StringUtil;
import com.pointlion.mvc.common.utils.UuidUtil;

import java.io.File;


@Clear
public class ToolWeightController extends BaseController {
	public static final ToolWeightService service = ToolWeightService.me;
	/***
	 * 列表页面
	 */
	public void getListPage(){
		render("list.html");
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
		ToolWeight o = getModel(ToolWeight.class);
		if(StrKit.notBlank(o.getId())){
			o.update();
		}else{
			o.setId(UuidUtil.getUUID());
			o.save();
		}
		renderSuccess(o,"");
	}
	/***
	 * 获取编辑页面
	 */
	public void getEditPage(){
		String id = getPara("id");
		String view = getPara("view");
		setAttr("view", view);
		setAttr("setting", SysCustomSetting.dao.getDefaultCstmSetting());
		if(StrKit.notBlank(id)){//修改
			ToolWeight o = service.getById(id);
			setAttr("o", o);
		}else{
		}
		setAttr("formModelName",StringUtil.toLowerCaseFirstOne(ToolWeight.class.getSimpleName()));//模型名称
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
	 * 导出
	 * @throws Exception
	 */
	public void export() throws Exception {
		String id = getPara("id","");
		File file = service.export(id,this.getRequest());
		renderFile(file);
	}


}