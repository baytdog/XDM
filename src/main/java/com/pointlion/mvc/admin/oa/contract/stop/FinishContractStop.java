package com.pointlion.mvc.admin.oa.contract.stop;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.flowable.engine.runtime.ProcessInstance;

import com.pointlion.mvc.admin.oa.common.OAConstants;
import com.pointlion.mvc.admin.oa.workflow.WorkFlowService;
import com.pointlion.mvc.admin.oa.workflow.WorkFlowUtil;
import com.pointlion.mvc.common.model.OaContract;
import com.pointlion.mvc.common.model.OaContractStop;
import com.pointlion.mvc.common.utils.Constants;
import com.pointlion.plugin.flowable.FlowablePlugin;

public class FinishContractStop implements JavaDelegate{
	@Override
	public void execute(DelegateExecution execution) {
		ProcessInstance instance = FlowablePlugin.buildProcessEngine().getRuntimeService().createProcessInstanceQuery().processInstanceId(execution.getProcessInstanceId()).singleResult();
		String id = instance.getBusinessKey();//业务主键
		OaContractStop a = OaContractStop.dao.getById(id);
		String defKey = instance.getProcessDefinitionKey();//流程key
		String pass = execution.getVariable("pass").toString();//是否同意
		if(Constants.SUBMIT_PASS_YES.equals(pass)){//同意
			OaContract c = OaContract.dao.getById(a.getContractId());
			if(c!=null){
				c.setState(OAConstants.OA_CONTRACT_STATE_STOP);
				c.update();
			}
		}
		String tableName = WorkFlowUtil.getTablenameByDefkey(defKey);
		WorkFlowService.me.updateIfCompleteAndIfAgree(tableName, pass, id);//更新是否同意，是否完成
	
	}
}
