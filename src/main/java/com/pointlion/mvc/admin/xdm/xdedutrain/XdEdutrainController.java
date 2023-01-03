package com.pointlion.mvc.admin.xdm.xdedutrain;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

import com.fasterxml.uuid.impl.UUIDUtil;
import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.upload.UploadFile;
import com.pointlion.enums.XdOperEnum;
import com.pointlion.mvc.common.base.BaseController;
import com.pointlion.mvc.admin.oa.workflow.WorkFlowService;
import com.pointlion.mvc.common.model.*;
import com.pointlion.mvc.common.utils.*;
import com.pointlion.mvc.admin.oa.common.OAConstants;
import com.pointlion.mvc.common.utils.office.excel.ExcelUtil;
import com.pointlion.plugin.shiro.ShiroKit;
import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


public class XdEdutrainController extends BaseController {
	public static final XdEdutrainService service = XdEdutrainService.me;
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
		String trainOrgname = java.net.URLDecoder.decode(getPara("trainOrgname",""),"UTF-8");
		String major = java.net.URLDecoder.decode(getPara("major",""),"UTF-8");
		String edubg = java.net.URLDecoder.decode(getPara("edubg",""),"UTF-8");
		String enrolldate =  getPara("enrolldate","");
		String graduatdate =  getPara("graduatdate","");
    	Page<Record> page = service.getPage(Integer.valueOf(curr),Integer.valueOf(pageSize),"","","","","","");
    	renderPage(page.getList(),"",page.getTotalRow());
    }
    /***
     * save data
     */
    public void save(){
    	XdEdutrain o = getModel(XdEdutrain.class);
		XdEdutrain xdEdutrain = XdEdutrain.dao.findById(o.getId());
		String ename = o.getEname();
		XdEmployee employee = XdEmployee.dao.findFirst("select * from xd_employee where name ='" + ename + "'");
		if(xdEdutrain==null){
			o.setEid(employee.getId());
			o.save();
			XdOperUtil.logSummary(o.getId(),o, XdOperEnum.C.name(),XdOperEnum.UNAPPRO.name(),0);
		}else{
			String eduChanges = XdOperUtil.getChangedMetheds(o, xdEdutrain);
			eduChanges = eduChanges.replaceAll("--$","");
			List<XdOplogSummary> summaryList =new ArrayList<>();
			List<XdOplogDetail> list =new ArrayList<>();
			if(!"".equals(eduChanges)){
				String lid=UuidUtil.getUUID();
				String[] eduCArray = eduChanges.split("--");
				for (String change : eduCArray) {
					change="{"+change+"}";
					XdOplogDetail logDetail = JSONUtil.jsonToBean(change, XdOplogDetail.class);
					logDetail.setRsid(lid);
					list.add(logDetail);
				}
				summaryList.add(XdOperUtil.logSummary(lid,o.getId(),o,xdEdutrain,XdOperEnum.U.name(),XdOperEnum.UNAPPRO.name()));
			}

			if (summaryList.size() > 0) {
				XdOperUtil.queryLastVersion(o.getId());
			}
			for (XdOplogSummary xdOplogSummary : summaryList) {
				xdOplogSummary.save();
			}
			for (XdOplogDetail detail : list) {
				detail.setId(UuidUtil.getUUID());
				detail.save();
			}
			o.setEid(employee.getId());
			o.update();
		}
		XdOperUtil.updateEdu(employee.getId());
    	renderSuccess();
    }
    /***
     * edit page
     */
    public void getEditPage(){
    	String id = getPara("id");
    	String view = getPara("view");
		setAttr("view", view);
		XdEdutrain o = new XdEdutrain();
		if(StrKit.notBlank(id)){
    		o = service.getById(id);
    	}else{
			o.setId(UuidUtil.getUUID());
    	}

		List<XdDict> edus = XdDict.dao.find("select * from xd_dict where type ='edu' order by sortnum");
		setAttr("edus",edus);

		List<XdEmployee> emps = XdEmployee.dao.find("select * from  xd_employee");
		setAttr("emps",emps);

		setAttr("o", o);
    	setAttr("formModelName",StringUtil.toLowerCaseFirstOne(XdEdutrain.class.getSimpleName()));
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

	/**
	 * 查询员工对应的教育培训信息
	 * 2022年11月29日16:25:42
	 */
	public void getEduTrainList(){
		String employeeId = getPara("employeeId");
		List<XdEdutrain> list = service.getEduTrainList(employeeId);
		renderJson(list);
	}


	public void exportExcel() throws UnsupportedEncodingException {


		String name = java.net.URLDecoder.decode(getPara("name",""),"UTF-8");
		String serviceUnit = java.net.URLDecoder.decode(getPara("serviceUnit",""),"UTF-8");
		String job = java.net.URLDecoder.decode(getPara("job",""),"UTF-8");
		String addr = java.net.URLDecoder.decode(getPara("addr",""),"UTF-8");
		String entryDate =  getPara("entryDate","");
		String departDate =  getPara("departDate","");
		String path = this.getSession().getServletContext().getRealPath("")+"/upload/export/"+ DateUtil.format(new Date(),21)+".xlsx";
		File file = service.exportExcel(path,name,serviceUnit,job,addr,entryDate,departDate);
		renderFile(file);
	}



	/**
	 * @Method importExcel
	 * @param :
	 * @Date 2023/1/3 14:05
	 * @Exception
	 * @Description 导入数据
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