package com.pointlion.mvc.admin.xdm.xdeffict;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.upload.UploadFile;
import com.pointlion.mvc.common.base.BaseController;
import com.pointlion.mvc.common.model.SysOrg;
import com.pointlion.mvc.common.model.SysUser;
import com.pointlion.mvc.common.model.XdEffict;
import com.pointlion.mvc.common.model.XdEmployee;
import com.pointlion.mvc.common.utils.StringUtil;
import com.pointlion.mvc.common.utils.office.excel.ExcelUtil;
import com.pointlion.plugin.shiro.ShiroKit;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;


public class XdEffictController extends BaseController {
	public static final XdEffictService service = XdEffictService.me;
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
		String empName = java.net.URLDecoder.decode(getPara("empName",""),"UTF-8");
    	Page<Record> page = service.getPage(Integer.valueOf(curr),Integer.valueOf(pageSize),empName);
    	renderPage(page.getList(),"",page.getTotalRow());
    }
    /***
     * save data
     */
    public void save(){
    	XdEffict o = getModel(XdEffict.class);
    	renderSuccess();
    }
    /***
     * edit page
     */
    public void getEditPage(){
    	String id = getPara("id");
    	String view = getPara("view");
		setAttr("view", view);
		XdEffict o = new XdEffict();
		if(StrKit.notBlank(id)){
    		o = service.getById(id);
    	}else{
    		SysUser user = SysUser.dao.findById(ShiroKit.getUserId());
    		SysOrg org = SysOrg.dao.findById(user.getOrgid());
    	}
		setAttr("o", o);
    	setAttr("formModelName",StringUtil.toLowerCaseFirstOne(XdEffict.class.getSimpleName()));
		renderIframe("edit.html");
    }
    /***
     * del
     * @throws Exception
     */
    public void delete() throws Exception{
		String ids = getPara("ids");
		service.deleteByIds(ids);
    	renderSuccess("操作成功!");
    }


	public void importExcel() throws IOException, SQLException {
		UploadFile file = getFile("file","/content");
		List<List<String>> list = ExcelUtil.excelToStringList(file.getFile().getAbsolutePath());
		Map<String,Object> result = service.importExcel(list);
		List<XdEffict> xdEfficts = XdEffict.dao.find("select * from  xd_effict order by  emp_name ,ctime");
		String empName="";
		String linkChrecord="";
		XdEmployee emp =null;
		int num=1;
		for (XdEffict xdEffict : xdEfficts) {
			if(!xdEffict.getEmpName().equals(empName)){
				if (emp != null) {
					emp.setChrecord(linkChrecord);
					emp.update();
					linkChrecord="";
					num=1;
				}
				empName=xdEffict.getEmpName();
				emp=XdEmployee.dao.findFirst("select * from  xd_employee where name='"+empName+"'");
			}
			linkChrecord=num+"、"+xdEffict.getNewDeptName()+xdEffict.getNewPdname();
			if (xdEffict.getOtherRemarks() != null) {
				linkChrecord=linkChrecord+"("+xdEffict.getOtherRemarks()+")";

			}
			num++;

		}


		if((Boolean)result.get("success")){
			renderSuccess((String)result.get("message"));
		}else{
			renderError((String)result.get("message"));
		}
	}
	
}