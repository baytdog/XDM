package com.pointlion.mvc.admin.oa.contract;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.pointlion.mvc.admin.oa.common.OAConstants;
import com.pointlion.mvc.admin.oa.workflow.WorkFlowService;
import com.pointlion.mvc.common.model.OaContract;
import com.pointlion.mvc.common.model.OaContractApply;
import com.pointlion.mvc.common.model.OaFlowCarbonC;
import com.pointlion.mvc.common.utils.DateUtil;
import com.pointlion.mvc.common.utils.office.word.WordExportUtil;
import com.pointlion.mvc.common.utils.ModelToMapUtil;
import com.pointlion.mvc.common.utils.office.word.POITemplateUtil;

public class OaContractService{
	public static final OaContractService me = new OaContractService();
	public static final WorkFlowService workFlowService = new WorkFlowService();
	public static final String TABLE_NAME = OaContract.tableName;
	
	/***
	 * 根据主键查询
	 */
	public OaContract getById(String id){
		return OaContract.dao.findById(id);
	}
	
	/***
	 * 获取分页
	 */
	public Page<Record> getPage(String userid ,String state,int pnum,int psize){
		String sql  = " from "+TABLE_NAME+" o where 1=1 ";
		if(StrKit.notBlank(userid)){
			sql = sql + " and o.create_userid='"+userid+"' ";
		}
		if(StrKit.notBlank(state)){
			sql = sql + " and o.state = '"+state+"'";
		}
		sql = sql + " order by o.create_time desc";
		return Db.paginate(pnum, psize, " select o.* ", sql);
	}
	/***
	 * 获取分页
	 */
	public Page<Record> getCanUsePage(String userid,int pnum,int psize){
		String sql  = " from "+TABLE_NAME+" o where 1=1 ";
		if(StrKit.notBlank(userid)){
			sql = sql + " and o.create_userid='"+userid+"' ";
		}
		sql = sql + " and o.state !='"+OAConstants.OA_CONTRACT_STATE_STOP+"'";
		sql = sql + " order by o.create_time desc";
		return Db.paginate(pnum, psize, " select o.* ", sql);
	}
	
	/***
	 * 删除
	 * @param ids
	 */
	@Before(Tx.class)
	public void deleteByIds(String ids){
    	String idarr[] = ids.split(",");
    	for(String id : idarr){
    		OaContract o = me.getById(id);
    		o.delete();
    	}
	}
	/***
	 * 获取合同类型
	 * @return
	 */
	public String getTypeName(String type){
		String name = "";
		if("1".equals(type)){
			name = "买卖";
		}else if("2".equals(type)){
			name = "租赁";
		}else if("3".equals(type)){
			name = "供用电/水/气";
		}else if("4".equals(type)){
			name = "融资租赁合同";
		}else if("5".equals(type)){
			name = "技术合同";
		}else if("6".equals(type)){
			name = "运输合同及建设工程合同";
		}
		return name;
	}
	/***
	 * 导出
	 * @throws Exception 
	 */
	public File export(String id,HttpServletRequest request) throws Exception{
		//		Contract_Apply.docx
		String path = request.getSession().getServletContext().getRealPath("")+"/WEB-INF/admin/oa/contract/apply/template/";
		OaContractApply contract = OaContractApply.dao.findById(id);
		Map<String , Object > data = ModelToMapUtil.ModelToMap2(contract);
		String start_time = contract.getStartTime();
		if(StrKit.notBlank(start_time)){
			data.put("start_time", DateUtil.dateToString(DateUtil.StringToDate(start_time),7));
		}
		String end_time = contract.getEndTime();
		if(StrKit.notBlank(end_time)){
			data.put("end_time",DateUtil.dateToString(DateUtil.StringToDate(end_time),7));
		}
		String can_able_date = contract.getCanAbleDate();
		if(StrKit.notBlank(can_able_date)){
			data.put("can_able_date",DateUtil.dateToString(DateUtil.StringToDate(can_able_date),7));
		}
		
		List<Record> list = workFlowService.getHisTaskList(contract.getProcInsId());
		List<String> receivelist = new ArrayList<String>();
		for(Record r : list){
			receivelist.add(r.getStr("taskName")+":"+r.getStr("assignee")+":"+r.getStr("message"));
		}
		data.put("receiver", StringUtils.join(receivelist,POITemplateUtil.NewLine+"——————————————————————————————————————"+POITemplateUtil.NewLine));
		data.put("htbh", contract.getContractNum());
		data.put("type", getTypeName(contract.getType2()));
		data.put("biaoti2", contract.getTitle());
		//抄送
				List<OaFlowCarbonC> cclist = OaFlowCarbonC.dao.find("select * from oa_flow_carbon_c c ,sys_user u  where u.id=c.user_id and business_id = '"+id+"' ");
				List<String> nameList = new ArrayList<String>();
				 for(OaFlowCarbonC cc : cclist){
					 nameList.add(cc.getStr("name"));
				 }
				data.put("cc",StringUtils.join(nameList,","));
		String exportURL = path+contract.getName()+"_"+contract.getTitle()+"_"+contract.getId()+".docx";
		WordExportUtil.export(data, path+"Contract.docx", exportURL);
		File file = new File(exportURL);
		if(file.exists()){
			return file;
		}else{
			return null;
		}
	}
	
}