package com.pointlion.mvc.admin.oa.apply.finance;

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
import com.pointlion.mvc.common.model.OaApplyFinance;
import com.pointlion.mvc.common.model.OaContract;
import com.pointlion.mvc.common.model.OaFlowCarbonC;
import com.pointlion.mvc.common.utils.office.word.WordExportUtil;
import com.pointlion.mvc.common.utils.ModelToMapUtil;
import com.pointlion.mvc.common.utils.office.word.POITemplateUtil;

public class OaApplyFinanceService{
	public static final OaApplyFinanceService me = new OaApplyFinanceService();
	public static final String TABLE_NAME = OaApplyFinance.tableName;
	public static final WorkFlowService workFlowService = new WorkFlowService();
	
	/***
	 * 根据主键查询
	 */
	public OaApplyFinance getById(String id){
		return OaApplyFinance.dao.findById(id);
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
    		OaApplyFinance o = me.getById(id);
    		o.delete();
    	}
	}
	
	/***
	 * 导出
	 * @throws Exception 
	 */
	public File export(String id,HttpServletRequest request) throws Exception{
		OaApplyFinance finance = OaApplyFinance.dao.findById(id);
		if("3".equals(finance.getType())){//如果是税费申请
			finance.setCommonPrice(finance.getTaxPrice());
			finance.setCommonPriceName(finance.getTaxPriceName());
		}
		String path = request.getSession().getServletContext().getRealPath("")+"/WEB-INF/admin/oa/apply/finance/template/";
		String templateUrl = path + "finance_"+finance.getType()+".docx";
		OaContract contract = OaContract.dao.getById(finance.getContractId());
//		OaProject project = OaProject.dao.getById(finance.getProjectId());
		List<Record> list = workFlowService.getHisTaskList(finance.getProcInsId());
		List<String> receivelist = new ArrayList<String>();
		for(Record r : list){
			receivelist.add(r.getStr("taskName")+":"+r.getStr("assignee")+":"+r.getStr("message"));
		}
		finance.put("receiver", StringUtils.join(receivelist,POITemplateUtil.NewLine+"———————————————————————————"+POITemplateUtil.NewLine));
		String exportURL = path+finance.getTitle()+"_"+finance.getId()+".docx";
		Map<String , Object > data = ModelToMapUtil.ModelToMap2(finance);
		data.put("contractName", contract!=null?contract.getName():"");
		data.put("projectName", finance.getFormProjectName());
		//抄送
				List<OaFlowCarbonC> cclist = OaFlowCarbonC.dao.find("select * from oa_flow_carbon_c c ,sys_user u  where u.id=c.user_id and business_id = '"+id+"' ");
				List<String> nameList = new ArrayList<String>();
				 for(OaFlowCarbonC cc : cclist){
					 nameList.add(cc.getStr("name"));
				 }
				data.put("cc",StringUtils.join(nameList,","));
		WordExportUtil.export(data, templateUrl, exportURL);
		File file = new File(exportURL);
		if(file.exists()){
			return file;
		}else{
			return null;
		}
	}
	
	/***
	 * 获取申请类型对应的名称
	 * @param type
	 * @return
	 * 1：建材及设备采购款支付申请
	 * 2：工程款支付申请
	 * 3：税费申请
	 * 4：归还贷款申请
	 * 5：财务相关费用支付申请
	 * 6：管理费用支付申请（差旅，应酬）
	 * 7：管理费用支付申请（其他）
	 * 8：无票报销
	 * 9：业务暂借款申请
	 * 10：资金调拨申请
	 */
	public String getTypeName(String type){
		String name = "";
		if("1".equals(type)){
			name = "建材及设备采购款支付申请";
		}else if("2".equals(type)){
			name = "工程款支付申请";
		}else if("3".equals(type)){
			name = "税费申请";
		}else if("4".equals(type)){
			name = "归还贷款申请";
		}else if("5".equals(type)){
			name = "财务相关费用支付申请";
		}else if("6".equals(type)){
			name = "管理费用支付申请（差旅，应酬）";
		}else if("7".equals(type)){
			name = "管理费用支付申请（其他）";
		}else if("8".equals(type)){
			name = "无票报销";
		}else if("9".equals(type)){
			name = "业务暂借款申请";
		}else if("10".equals(type)){
			name = "资金调拨申请";
		}else if("11".equals(type)){
			name = "审批内卡(融资用)";
		}else if("12".equals(type)){
			name = "审批内卡(财务筹划)";
		}else if("13".equals(type)){
			name = "审批内卡(工商登记变更等)";
		}else if("14".equals(type)){
			name = "审批内卡(其他)";
		}else if("15".equals(type)){
			name = "固定资产申请";
		}
		return name;
	}
	
	/***
	 * 获取申请类型对应的名称
	 * @param type
	 * @return
	 * 1：建材及设备采购款支付申请
	 * 2：工程款支付申请
	 * 3：税费申请
	 * 4：归还贷款申请
	 * 5：财务相关费用支付申请
	 * 6：管理费用支付申请（差旅，应酬）
	 * 7：管理费用支付申请（其他）
	 * 8：无票报销
	 * 9：业务暂借款申请
	 * 10：资金调拨申请
	 */
	public String getDefKeyByType(String type){
		return "Finance_"+type;
	}
}