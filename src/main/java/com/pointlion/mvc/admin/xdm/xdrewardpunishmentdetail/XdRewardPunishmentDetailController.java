package com.pointlion.mvc.admin.xdm.xdrewardpunishmentdetail;

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
import com.pointlion.mvc.common.model.XdRewardPunishmentSummary;
import com.pointlion.mvc.common.utils.StringUtil;
import com.pointlion.mvc.common.model.XdRewardPunishmentDetail;
import com.pointlion.mvc.common.model.SysUser;
import com.pointlion.mvc.common.model.SysOrg;
import com.pointlion.mvc.common.utils.UuidUtil;
import com.pointlion.mvc.common.utils.Constants;
import com.pointlion.mvc.admin.oa.common.OAConstants;
import com.pointlion.mvc.common.utils.DateUtil;
import com.pointlion.mvc.common.utils.office.excel.ExcelUtil;
import com.pointlion.plugin.shiro.ShiroKit;



public class XdRewardPunishmentDetailController extends BaseController {
	public static final XdRewardPunishmentDetailService service = XdRewardPunishmentDetailService.me;
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
		String year = getPara("year","");
		String empName = java.net.URLDecoder.decode(getPara("emp_name",""),"UTF-8");
    	Page<Record> page = service.getPage(Integer.valueOf(curr),Integer.valueOf(pageSize),year,empName);
    	renderPage(page.getList(),"",page.getTotalRow());
    }
    /***
     * save data
     */
    public void save(){
    	XdRewardPunishmentDetail o = getModel(XdRewardPunishmentDetail.class);
    	if(StrKit.notBlank( )){
    		o.update();
    	}else{
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
		XdRewardPunishmentDetail o = new XdRewardPunishmentDetail();
		if(StrKit.notBlank(id)){
    		o = service.getById(id);
    		if("detail".equals(view)){
    		}
    	}else{
    		SysUser user = SysUser.dao.findById(ShiroKit.getUserId());
    		SysOrg org = SysOrg.dao.findById(user.getOrgid());
    	}
		setAttr("o", o);
    	setAttr("formModelName",StringUtil.toLowerCaseFirstOne(XdRewardPunishmentDetail.class.getSimpleName()));
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
		List<List<String>> list = ExcelUtil.excelToStringList(file.getFile().getAbsolutePath(),9);
		Map<String,Object> result = service.importExcel(list);
		if((Boolean)result.get("success")){
			renderSuccess((String)result.get("message"));
		}else{
			renderError((String)result.get("message"));
		}
	}

	public void updateCanUse() throws Exception{
		String id = getPara("id");
		String val = getPara("val");
		XdRewardPunishmentDetail rpDetail = service.getById(id);

		rpDetail.setCanDistribution(val);
		rpDetail.update();

		XdRewardPunishmentSummary rpSummary = XdRewardPunishmentSummary.dao.findFirst("select * from  xd_reward_punishment_summary where year='"+rpDetail.getYear()+"' and  dept_id='" + rpDetail.getDeptId() + "'");
		Double rewardPunishment = rpDetail.getRewardPunishment();
		//rpSummary.setQuota(rpSummary.getQuota()+rewardPunishment);
		if(val.equals("是")){
			rpSummary.setAbleQuota(rpSummary.getAbleQuota()+rewardPunishment);
			rpSummary.setDisableQuota(rpSummary.getDisableQuota()-rewardPunishment);
		}else{
			rpSummary.setDisableQuota(rpSummary.getDisableQuota()+rewardPunishment);
			rpSummary.setAbleQuota(rpSummary.getAbleQuota()-rewardPunishment);
		}
		rpSummary.update();
		//service.deleteByIds(ids);
		renderSuccess("操作成功!");
	}

	
}