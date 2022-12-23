package com.pointlion.mvc.admin.xdm.xdedutrain;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.pointlion.mvc.common.base.BaseController;
import com.pointlion.mvc.admin.oa.workflow.WorkFlowService;
import com.pointlion.mvc.common.utils.StringUtil;
import com.pointlion.mvc.common.model.XdEdutrain;
import com.pointlion.mvc.common.model.SysUser;
import com.pointlion.mvc.common.model.SysOrg;
import com.pointlion.mvc.common.utils.UuidUtil;
import com.pointlion.mvc.common.utils.Constants;
import com.pointlion.mvc.admin.oa.common.OAConstants;
import com.pointlion.mvc.common.utils.DateUtil;
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
//    	if(StrKit.notBlank(o.getId())){
//    		o.update();
//    	}else{
//    		o.setId(UuidUtil.getUUID());
//    		o.setCreateTime(DateUtil.getCurrentTime());
//    		o.save();
//    	}
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
    		if("detail".equals(view)){
//    			if(StrKit.notBlank(o.getProcInsId())){
//    				setAttr("procInsId", o.getProcInsId());
//    				setAttr("defId", wfservice.getDefIdByInsId(o.getProcInsId()));
//    			}
    		}
    	}else{
    		SysUser user = SysUser.dao.findById(ShiroKit.getUserId());
    		SysOrg org = SysOrg.dao.findById(user.getOrgid());
//			o.setOrgId(org.getId());
//			o.setOrgName(org.getName());
//			o.setUserid(user.getId());
//			o.setApplyerName(user.getName());
    	}
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
}