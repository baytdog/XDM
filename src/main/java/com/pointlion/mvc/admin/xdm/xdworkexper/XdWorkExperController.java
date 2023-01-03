package com.pointlion.mvc.admin.xdm.xdworkexper;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.upload.UploadFile;
import com.pointlion.enums.XdOperEnum;
import com.pointlion.mvc.admin.oa.workflow.WorkFlowService;
import com.pointlion.mvc.common.base.BaseController;
import com.pointlion.mvc.common.model.*;
import com.pointlion.mvc.common.utils.*;
import com.pointlion.mvc.common.utils.office.excel.ExcelUtil;
import com.pointlion.plugin.shiro.ShiroKit;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


public class XdWorkExperController extends BaseController {
	public static final XdWorkExperService service = XdWorkExperService.me;
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
		String serviceUnit = java.net.URLDecoder.decode(getPara("serviceUnit",""),"UTF-8");
		String job = java.net.URLDecoder.decode(getPara("job",""),"UTF-8");
		String addr = java.net.URLDecoder.decode(getPara("addr",""),"UTF-8");
		String entryDate =  getPara("entryDate","");
		String departDate =  getPara("departDate","");
        Page<Record> page = service.getPage(Integer.valueOf(curr),Integer.valueOf(pageSize),name,serviceUnit,job,addr,entryDate,departDate);
        renderPage(page.getList(),"",page.getTotalRow());
    }
    /***
     * save data
     */
    public void save(){
    	XdWorkExper o = getModel(XdWorkExper.class);
		XdWorkExper workExper = XdWorkExper.dao.findById(o.getId());
		String ename = o.getEname();
		XdEmployee employee = XdEmployee.dao.findFirst("select * from xd_employee where name ='" + ename + "'");
		if(workExper==null){
			o.setEid(employee.getId());
			o.save();
			XdOperUtil.logSummary(o.getId(),o, XdOperEnum.C.name(),XdOperEnum.UNAPPRO.name(),0);
		}else{
			String eduChanges = XdOperUtil.getChangedMetheds(o, workExper);
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
				summaryList.add(XdOperUtil.logSummary(lid,o.getId(),o,workExper, XdOperEnum.U.name(),XdOperEnum.UNAPPRO.name()));
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
		renderSuccess();
    }
    /***
     * edit page
     */
    public void getEditPage(){
    	String id = getPara("id");
    	String view = getPara("view");
		setAttr("view", view);
		XdWorkExper o = new XdWorkExper();
		if(StrKit.notBlank(id)){
			o = service.getById(id);
		}else{
			o.setId(UuidUtil.getUUID());
		}
		setAttr("o", o);
    	setAttr("formModelName",StringUtil.toLowerCaseFirstOne(XdWorkExper.class.getSimpleName()));
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
	public void getWorkExperList(){
		String employeeId = getPara("employeeId");
		List<XdWorkExper> list = service.getWorkExperList(employeeId);
		renderJson(list);
	}



	/**
	 * @Method exportExcel
	 * @param :
	 * @Date 2022/12/17 9:28
	 * @Exception
	 * @Description
	 * @Author king
	 * @Version  1.0
	 * @Return void
	 */
	public void exportExcel() throws UnsupportedEncodingException {


		String name = java.net.URLDecoder.decode(getPara("name",""),"UTF-8");
		String serviceUnit = java.net.URLDecoder.decode(getPara("serviceUnit",""),"UTF-8");
		String job = java.net.URLDecoder.decode(getPara("job",""),"UTF-8");
		String addr = java.net.URLDecoder.decode(getPara("addr",""),"UTF-8");
		String entryDate =  getPara("entryDate","");
		String departDate =  getPara("departDate","");
		String path = this.getSession().getServletContext().getRealPath("")+"/upload/export/"+ DateUtil.format(new Date(),21)+".xlsx";
		File file = service.exportExcel(path,"","","","","","");
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