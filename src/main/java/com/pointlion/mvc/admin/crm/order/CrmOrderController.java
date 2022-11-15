package com.pointlion.mvc.admin.crm.order;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.jfinal.kit.PathKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.upload.UploadFile;
import com.pointlion.mvc.admin.oa.workflow.WorkFlowService;
import com.pointlion.mvc.common.base.BaseController;
import com.pointlion.mvc.common.model.CrmOrder;
import com.pointlion.mvc.common.model.SysOrg;
import com.pointlion.mvc.common.model.SysUser;
import com.pointlion.mvc.common.utils.Constants;
import com.pointlion.mvc.common.utils.DateUtil;
import com.pointlion.mvc.common.utils.StringUtil;
import com.pointlion.mvc.common.utils.UuidUtil;
import com.pointlion.mvc.common.utils.office.excel.ExcelUtil;
import com.pointlion.plugin.shiro.ShiroKit;


public class CrmOrderController extends BaseController {
	public static CrmOrderService service = CrmOrderService.me;
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
		String typeName = getPara("type_name","");
		String orderCode = getPara("order_code","");
		String orderNum = getPara("order_num","");
		Page<Record> page = service.getPage(Integer.valueOf(curr),Integer.valueOf(pageSize),typeName,orderCode,orderNum);
		renderPage(page.getList(),"",page.getTotalRow());
	}
	/***
	 * save data
	 */
	public void save(){
		CrmOrder o = getModel(CrmOrder.class);
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
		if(StrKit.notBlank(id)){
			CrmOrder o = service.getById(id);
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
		setAttr("formModelName",StringUtil.toLowerCaseFirstOne(CrmOrder.class.getSimpleName()));
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
		CrmOrder o = CrmOrder.dao.getById(id);
		o.setIfSubmit(Constants.IF_SUBMIT_YES);
		//String insId = wfservice.startProcess(id, "your process defkey",o.getTitle(),null);
		//o.setProcInsId(insId);
		o.update();
		renderSuccess("提交成功");
	}
	/***
	 * callBack
	 */
	public void callBack(){
		String id = getPara("id");
		try{
			CrmOrder o = CrmOrder.dao.getById(id);
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

	/***
	 * 导入
	 */
	public void importExcel() throws IOException, SQLException {
//		E:/work/workspacePointLion/JPointLion-flowable/JPointLion-flowable/target/JPointLion/upload/content
		UploadFile file = getFile("file","/content");
		List<List<String>> list = ExcelUtil.excelToList(file.getFile().getAbsolutePath());
		Map<String,Object> result = service.importExcel(list);
		if((Boolean)result.get("success")){
			renderSuccess((String)result.get("message"));
		}else{
			renderError((String)result.get("message"));
		}
	}

	/***
	 * 导出
	 */
	public void exportExcel(){
		String typeName = getPara("type_name","");
		String orderCode = getPara("order_code","");
		String orderNum = getPara("order_num","");
		System.out.println("===================================");
		System.out.println(this.getSession().getServletContext().getRealPath(""));
		System.out.println("===================================");
		String path = this.getSession().getServletContext().getRealPath("")+"/upload/export/"+DateUtil.format(new Date(),21)+".xlsx";
		File file = service.exportExcel(path,typeName,orderCode,orderNum);
		renderFile(file);
	}

	/***
	 * 下载
	 */
	public void download(){
		String path = PathKit.getRootClassPath();
		File file = new File(path+"/../admin/crm/order/订单导入模版.xlsx");
		renderFile(file);
	}
}