package com.pointlion.mvc.admin.oa.apply.bankaccount;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.pointlion.mvc.common.model.SysRoleOrg;
import com.pointlion.plugin.shiro.ShiroKit;
import org.apache.commons.lang3.StringUtils;

import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.admin.oa.workflow.WorkFlowService;
import com.pointlion.mvc.common.model.OaApplyBankAccount;
import com.pointlion.mvc.common.model.OaFlowCarbonC;
import com.pointlion.mvc.common.utils.office.word.WordExportUtil;
import com.pointlion.mvc.common.utils.ModelToMapUtil;
import com.pointlion.mvc.common.utils.office.word.POITemplateUtil;

public class OaApplyBankAccountService{
	public static final OaApplyBankAccountService me = new OaApplyBankAccountService();
	public static final String TABLE_NAME = OaApplyBankAccount.tableName;
	public static final WorkFlowService workFlowService = new WorkFlowService();
	
	/***
	 * 根据主键查询
	 */
	public OaApplyBankAccount getById(String id){
		return OaApplyBankAccount.dao.findById(id);
	}
	
	/***
	 * 获取分页
	 */
	public Page<Record> getPage(int pnum,int psize,String type,String name,String startTime,String endTime){
		String sql  = " from "+TABLE_NAME+" o LEFT JOIN act_hi_procinst p ON o.proc_ins_id=p.ID_  where 1=1 ";
		if(StrKit.notBlank(type)){
			sql = sql + " and o.type='"+type+"' ";
		}
		String userId = ShiroKit.getUserId();
		//数据权限
		sql = sql + SysRoleOrg.dao.getRoleOrgSql(userId) ;
		sql = sql + getQuerySql(type,name,startTime,endTime);
		sql = sql + " order by o.create_time desc";
		return Db.paginate(pnum, psize, " select o.*,p.PROC_DEF_ID_ defid ", sql);
	}
	/***
	 * 导出
	 * @throws Exception 
	 */
	public File export(String id,HttpServletRequest request) throws Exception{
		OaApplyBankAccount bankaccount = OaApplyBankAccount.dao.findById(id);
		String path = request.getSession().getServletContext().getRealPath("")+"/WEB-INF/admin/oa/apply/bankaccount/template/";
		String templateUrl = path + "bankaccount_"+bankaccount.getType()+".docx";
		List<Record> list = workFlowService.getHisTaskList(bankaccount.getProcInsId());
		List<String> receivelist = new ArrayList<String>();
		for(Record r : list){
			receivelist.add(r.getStr("taskName")+":"+r.getStr("assignee")+":"+r.getStr("message"));
		}
		bankaccount.put("projectName", bankaccount.getFormProjectName());
		bankaccount.put("receiver", StringUtils.join(receivelist,POITemplateUtil.NewLine+"———————————————————————————"+POITemplateUtil.NewLine));
		//抄送
		List<OaFlowCarbonC> cclist = OaFlowCarbonC.dao.find("select * from oa_flow_carbon_c c ,sys_user u  where u.id=c.user_id and business_id = '"+id+"' ");
		List<String> nameList = new ArrayList<String>();
		 for(OaFlowCarbonC cc : cclist){
			 nameList.add(cc.getStr("name"));
		 }
		bankaccount.put("cc",StringUtils.join(nameList,","));
		
		bankaccount.put("beizhuName",bankaccount.getDes());
		String exportURL = path+bankaccount.getTitle()+"_"+bankaccount.getId()+".docx";
		Map<String , Object > data = ModelToMapUtil.ModelToMap2(bankaccount);
		WordExportUtil.export(data, templateUrl, exportURL);
		File file = new File(exportURL);
		if(file.exists()){
			return file;
		}else{
			return null;
		}
	}
	/****
	 * 获取查询sql
	 * @param type
	 * @param name
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public String  getQuerySql(String type,String name,String startTime,String endTime){
		String sql = " ";
		if(StrKit.notBlank(type)){
			sql = sql + " and o.type='"+type+"' ";
		}
		if(StrKit.notBlank(name)){
			sql = sql + " and (o.applyer_name like '%"+name+"%' or o.org_name like '%"+name+"%')";
		}
		if(StrKit.notBlank(startTime)){
			sql = sql + "  and o.create_time >= '"+startTime+" 00:00:00'";
		}
		if(StrKit.notBlank(endTime)){
			sql = sql + "  and o.create_time <= '"+endTime+" 23:59:59'";
		}
		return sql;
	}
	
	/***
	 * 删除
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		OaApplyBankAccount o = me.getById(id);
    		o.delete();
    	}
	}
	/***
	 * 获取申请类型对应的名称
	 * @param type
	 * @return
	 * 1：开户申请
	 * 2：销户申请
	 * 3：变更申请
	 * 4：开通网银
	 * 5：注销网银
	 * 6：开户+开通网银
	 */
	public String getTypeName(String type){
		String name = "";
		if("1".equals(type)){
			name = "开户申请";
		}else if("2".equals(type)){
			name = "销户申请";
		}else if("3".equals(type)){
			name = "变更申请";
		}else if("4".equals(type)){
			name = "开通网银";
		}else if("5".equals(type)){
			name = "注销网银";
		}else if("6".equals(type)){
			name = "开户+开通网银";
		}
		return name;
	}
	
	/***
	 * 获取申请类型对应的名称
	 * @param type
	 * @return
	 * 1：开户申请
	 * 2：销户申请
	 * 3：变更申请
	 * 4：开通网银
	 * 5：注销网银
	 * 6：开户开通网银
	 */
	public String getDefKeyByType(String type){
//		return "AccountBank_"+type;
		return "AccountBank";
	}
	
}