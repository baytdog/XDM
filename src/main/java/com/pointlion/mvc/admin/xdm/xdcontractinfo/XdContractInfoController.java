package com.pointlion.mvc.admin.xdm.xdcontractinfo;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.upload.UploadFile;
import com.pointlion.mvc.common.base.BaseController;
import com.pointlion.mvc.admin.oa.workflow.WorkFlowService;
import com.pointlion.mvc.common.model.*;
import com.pointlion.mvc.common.utils.StringUtil;
import com.pointlion.mvc.common.utils.UuidUtil;
import com.pointlion.mvc.common.utils.Constants;
import com.pointlion.mvc.admin.oa.common.OAConstants;
import com.pointlion.mvc.common.utils.DateUtil;
import com.pointlion.mvc.common.utils.office.excel.ExcelUtil;
import com.pointlion.plugin.shiro.ShiroKit;



public class XdContractInfoController extends BaseController {
	public static final XdContractInfoService service = XdContractInfoService.me;
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
		String name = java.net.URLDecoder.decode(getPara("name",""),"UTF-8");
		String empNum = getPara("empNum","");
		String startTime = getPara("startTime","");
    	Page<Record> page = service.getPage(Integer.valueOf(curr),Integer.valueOf(pageSize),name,empNum,startTime);
    	renderPage(page.getList(),"",page.getTotalRow());
    }
    /***
     * save data
     */
    public void save(){
    	XdContractInfo o = getModel(XdContractInfo.class);
    	if(StrKit.notBlank(o.getId())){
    		o.update();
    	}else{
    		o.setId(UuidUtil.getUUID());
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
		XdContractInfo o = new XdContractInfo();
		if(StrKit.notBlank(id)){
    		o = service.getById(id);
			List<XdContractInfo> xdContractInfoList = XdContractInfo.dao.find("select * from xd_contract_info where eid='" + o.getEid() + "' and contractenddate='无固定期限'");
			String needEndDate="N";
			if(xdContractInfoList.size()>0 && o.getContractenddate()!=null && !o.getContractenddate().equals("无固定期限")&& !o.getContractenddate().equals("")){
				needEndDate="Y";
			}
			setAttr("needEndD",needEndDate);
    	}else{
    		SysUser user = SysUser.dao.findById(ShiroKit.getUserId());
    		SysOrg org = SysOrg.dao.findById(user.getOrgid());
    	}
		List<XdEmployee> empList = XdEmployee.dao.find("select * from  xd_employee order by empnum");
		setAttr("emps",empList);
		List<XdDict> empRelationsList = XdDict.dao.find("select * from xd_dict where type= 'empRelation' order by sortnum");
		setAttr("empRelations",empRelationsList);
		setAttr("o", o);
    	setAttr("formModelName",StringUtil.toLowerCaseFirstOne(XdContractInfo.class.getSimpleName()));
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
		List<List<String>> list = ExcelUtil.excelToStringList(file.getFile().getAbsolutePath(),2);
		Map<String,Object> result = service.importExcel(list);
		if((Boolean)result.get("success")){
			renderSuccess((String)result.get("message"));
		}else{
			renderError((String)result.get("message"));
		}
	}

	public void getRelationInfo(){
		String eid = getPara("eid");
		XdContractInfo lastContract = XdContractInfo.dao.findFirst("select * from xd_contract_info where eid='" + eid + "' order by contractclauses desc");
	/*	String needEndDate="N";
		if(workExperList.size()>0){
			needEndDate="Y";
		}*/
		int contractNum=1;
		if(lastContract!=null){
			contractNum=lastContract.getContractclauses()+1;
		}

		cn.hutool.json.JSONObject jsonObject=new cn.hutool.json.JSONObject();
		//jsonObject.put("empNum",emp.getEmpnum());



		List<XdContractInfo> xdContractInfoList = XdContractInfo.dao.find("select * from xd_contract_info where eid='" + eid + "' and contractenddate='无固定期限' ");
		String needEndDate="N";
		if(xdContractInfoList.size()>0){
			needEndDate="Y";
		}

		jsonObject.put("contractNum",contractNum);
		jsonObject.put("needEndDate",needEndDate);
		renderJson(jsonObject);
	}
	
}