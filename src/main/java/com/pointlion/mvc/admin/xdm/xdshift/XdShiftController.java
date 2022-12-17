package com.pointlion.mvc.admin.xdm.xdshift;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.upload.UploadFile;
import com.pointlion.mvc.common.base.BaseController;
import com.pointlion.mvc.common.model.SysOrg;
import com.pointlion.mvc.common.model.SysUser;
import com.pointlion.mvc.common.model.XdShift;
import com.pointlion.mvc.common.utils.DateUtil;
import com.pointlion.mvc.common.utils.StringUtil;
import com.pointlion.mvc.common.utils.UuidUtil;
import com.pointlion.mvc.common.utils.office.excel.ExcelUtil;
import com.pointlion.plugin.shiro.ShiroKit;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;


public class XdShiftController extends BaseController {
	public static final XdShiftService service = XdShiftService.me;
	/***
	 * get list page
	 */
	public void getListPage(){
		renderIframe("list.html");
	}
	/***
	 * list page data
	 **/
	public void listData() throws UnsupportedEncodingException {
		String curr = getPara("pageNumber");
		String pageSize = getPara("pageSize");
		String shiftname = java.net.URLDecoder.decode(getPara("shiftname",""),"UTF-8");
		Page<Record> page = service.getPage(Integer.valueOf(curr),Integer.valueOf(pageSize),shiftname,"endTime","applyUser");
		renderPage(page.getList(),"",page.getTotalRow());
	}
	/***
	 * save data
	 */
	public void save(){
		XdShift o = getModel(XdShift.class);
		XdShift xdShift= XdShift.dao.findById(o.getId());
		if (xdShift == null) {
			o.setCtime(DateUtil.getCurrentTime());
			o.setCuser(ShiroKit.getUserId());
			o.setStatus("1");
			o.save();

		}else{
			o.update();
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
		XdShift o = new XdShift();
		if(StrKit.notBlank(id)){
			o = service.getById(id);
			if("detail".equals(view)){

			}
		}else{
			o.setId(UuidUtil.getUUID());
//			SysUser user = SysUser.dao.findById(ShiroKit.getUserId());
//			SysOrg org = SysOrg.dao.findById(user.getOrgid());
		}
		setAttr("o", o);
		setAttr("formModelName",StringUtil.toLowerCaseFirstOne(XdShift.class.getSimpleName()));
		renderIframe("edit.html");
	}
	/***
	 * del
	 * @throws Exception
	 */
	public void delete() throws Exception{
		String ids = getPara("ids");
		service.deleteByIds(ids);
		renderSuccess("操作成功");
	}

	/**
	 * @Method importExcel
	 * @param :
	 * @Date 2022/12/17 16:26
	 * @Exception
	 * @Description 导入
	 * @Author king
	 * @Version  1.0
	 * @Return void
	 */
	public void importExcel() throws IOException, SQLException {
		UploadFile file = getFile("file","/content");
		List<List<String>> list = ExcelUtil.excelToStringList(file.getFile().getAbsolutePath());
		Map<String,Object> result = service.importExcel(list);
		if((Boolean)result.get("success")){
			renderSuccess((String)result.get("message"));
		}else{
			renderError((String)result.get("message"));
		}
	}


}