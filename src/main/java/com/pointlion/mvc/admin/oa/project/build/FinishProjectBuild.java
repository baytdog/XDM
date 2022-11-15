package com.pointlion.mvc.admin.oa.project.build;

import com.jfinal.plugin.activerecord.Model;
import com.pointlion.mvc.admin.oa.common.OAConstants;
import com.pointlion.mvc.common.model.OaProject;
import com.pointlion.mvc.common.model.OaProjectBuild;
import com.pointlion.mvc.common.utils.Constants;
import com.pointlion.mvc.common.utils.DateUtil;
import com.pointlion.plugin.flowable.FlowablePlugin;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.flowable.engine.runtime.ProcessInstance;

public class FinishProjectBuild implements JavaDelegate{
	@Override
	public void execute(DelegateExecution execution){
		ProcessInstance instance = FlowablePlugin.buildProcessEngine().getRuntimeService().createProcessInstanceQuery().processInstanceId(execution.getProcessInstanceId()).singleResult();
		String id = instance.getBusinessKey();//业务主键
		String pass = execution.getVariable("pass").toString();//是否同意
		String className = execution.getVariable(OAConstants.WORKFLOW_VAR_APPLY_BUSINESS_CLASSNAME).toString();//对象类名
		try {
			Class busClass = Class.forName(className);
			Model model = (Model)busClass.newInstance();
			model.set("id", id);
			model.set("if_complete",Constants.IF_COMPLETE_YES);
			//是否同意
			if(Constants.SUBMIT_PASS_YES.equals(pass)){//如果进来的参数是pass=1
				OaProjectBuild b = OaProjectBuild.dao.getById(id);
				OaProject p = new OaProject();
				p.setId(b.getId());
				p.setActAllStrategySuggest(b.getActAllStrategySuggest());
				p.setActSuggest(b.getActSuggest());
				p.setBuildId(b.getId());
				p.setContactMail(b.getContactMail());
				p.setContactMobile(b.getContactMobile());
				p.setContactName(b.getContactName());
				p.setCreateTime(DateUtil.getCurrentTime());
				p.setCreateUserId(b.getUserid());
				p.setCustomerContactMobile(b.getCustomerContactMobile());
				p.setCustomerContactName(b.getCustomerContactName());
				p.setCustomerName(b.getCustomerName());
				p.setCustomHope(b.getCustomHope());
				p.setLeader(b.getLeader());
				p.setMember(b.getMember());
				p.setProjectStartTime(b.getProjectStartTime());
				p.setProjectEndTime(b.getProjectEndTime());
				p.setProjectMoney(b.getProjectMoney());
				p.setProjectName(b.getProjectName());
				p.setRiskAndMeasure(b.getRiskAndMeasure());
				p.setProjectCode(b.getProjectCode());
				p.setProjectType(b.getProjectType());
				p.setProjectName(b.getProjectTypeName());
				p.save();
				model.set("if_agree",Constants.IF_AGREE_YES);
			}else{
				model.set("if_agree",Constants.IF_AGREE_NO);
			}
			model.update();
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		} catch (InstantiationException ex) {
			ex.printStackTrace();
		} catch (IllegalAccessException ex) {
			ex.printStackTrace();
		}
	}
}
