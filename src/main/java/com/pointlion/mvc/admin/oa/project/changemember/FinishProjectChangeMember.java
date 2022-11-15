package com.pointlion.mvc.admin.oa.project.changemember;

import com.jfinal.plugin.activerecord.Model;
import com.pointlion.mvc.admin.oa.common.OAConstants;
import com.pointlion.mvc.common.model.OaProject;
import com.pointlion.mvc.common.model.OaProjectChangeMember;
import com.pointlion.mvc.common.utils.Constants;
import com.pointlion.plugin.flowable.FlowablePlugin;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.flowable.engine.runtime.ProcessInstance;

public class FinishProjectChangeMember implements JavaDelegate{
	@Override
	public void execute(DelegateExecution execution) {
		ProcessInstance instance = FlowablePlugin.buildProcessEngine().getRuntimeService().createProcessInstanceQuery().processInstanceId(execution.getProcessInstanceId()).singleResult();
		String id = instance.getBusinessKey();//业务主键
		String pass = execution.getVariable("pass").toString();//是否同意
		String className = execution.getVariable(OAConstants.WORKFLOW_VAR_APPLY_BUSINESS_CLASSNAME).toString();//对象类名
		try {
			Class busClass = Class.forName(className);
			Model model = (Model)busClass.newInstance();
			model.set("id", id);
			model.set("if_complete",Constants.IF_COMPLETE_YES);
			if(Constants.SUBMIT_PASS_YES.equals(pass)){//如果进来的参数是pass=1
				model.set("if_agree",Constants.IF_AGREE_YES);
				OaProjectChangeMember b = OaProjectChangeMember.dao.getById(id);
				OaProject p = OaProject.dao.getById(b.getProjectId());
				if(p!=null){
					p.setLeader(b.getLeader());
					p.setMember(b.getMember());
					p.update();
				}
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
