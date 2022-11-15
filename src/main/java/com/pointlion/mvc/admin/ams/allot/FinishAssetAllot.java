package com.pointlion.mvc.admin.ams.allot;

import com.pointlion.plugin.flowable.FlowablePlugin;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.flowable.engine.runtime.ProcessInstance;

import com.pointlion.mvc.admin.oa.workflow.WorkFlowService;
import com.pointlion.mvc.admin.oa.workflow.WorkFlowUtil;
import com.pointlion.mvc.common.model.AmsAsset;
import com.pointlion.mvc.common.model.AmsAssetAllot;
import com.pointlion.mvc.common.utils.Constants;

public class FinishAssetAllot implements JavaDelegate{
	@Override
	public void execute(DelegateExecution execution) {
		ProcessInstance instance = FlowablePlugin.buildProcessEngine().getRuntimeService().createProcessInstanceQuery().processInstanceId(execution.getProcessInstanceId()).singleResult();
		String id = instance.getBusinessKey();//业务主键
		
		String defKey = instance.getProcessDefinitionKey();//流程key
		String pass = execution.getVariable("pass").toString();//是否同意
		if(Constants.SUBMIT_PASS_YES.equals(pass)){//同意
			AmsAssetAllot s = AmsAssetAllot.dao.getById(id);
			AmsAsset a = AmsAsset.dao.getById(s.getAssetId());
			if(a!=null){
				a.setBelongOrgId(s.getAllotToOrgId());
				a.setBelongOrgName(s.getAllotToOrgName());
				a.update();
			}
		}
		String tableName = WorkFlowUtil.getTablenameByDefkey(defKey);
		WorkFlowService.me.updateIfCompleteAndIfAgree(tableName, pass, id);//更新是否同意，是否完成
	}
}
