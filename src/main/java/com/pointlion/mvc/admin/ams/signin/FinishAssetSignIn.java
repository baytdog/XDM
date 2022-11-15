package com.pointlion.mvc.admin.ams.signin;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.flowable.engine.runtime.ProcessInstance;

import com.pointlion.mvc.admin.oa.common.OAConstants;
import com.pointlion.mvc.admin.oa.workflow.WorkFlowService;
import com.pointlion.mvc.admin.oa.workflow.WorkFlowUtil;
import com.pointlion.mvc.common.model.AmsAsset;
import com.pointlion.mvc.common.model.AmsAssetSignIn;
import com.pointlion.mvc.common.utils.Constants;
import com.pointlion.mvc.common.utils.DateUtil;
import com.pointlion.plugin.flowable.FlowablePlugin;

public class FinishAssetSignIn implements JavaDelegate{
	@Override
	public void execute(DelegateExecution execution) {
		ProcessInstance instance = FlowablePlugin.buildProcessEngine().getRuntimeService().createProcessInstanceQuery().processInstanceId(execution.getProcessInstanceId()).singleResult();
		String id = instance.getBusinessKey();//业务主键
		String defKey = instance.getProcessDefinitionKey();//流程key
		String pass = execution.getVariable("pass").toString();//是否同意
		if(Constants.SUBMIT_PASS_YES.equals(pass)){//同意
			AmsAssetSignIn s = AmsAssetSignIn.dao.getById(id);
			AmsAsset a = new AmsAsset();
			a.setId(s.getId());
			a.setAssetName(s.getAssetName());
			a.setBelongOrgId(s.getOrgId());
			a.setBelongOrgName(s.getOrgName());
			a.setCount(s.getCount());
			a.setCreateUserId(s.getUserid());
			a.setCreateUserName(s.getApplyerName());
			a.setModelNum(s.getModelNum());
			a.setState(s.getState());
			a.setSumPrice(s.getSumPrice());
			a.setUnitPrice(s.getUnitPrice());
			a.setCreateTime(DateUtil.getCurrentTime());
			a.setState(OAConstants.AMS_ASSET_STATE_0);
			a.save();
		}
		String tableName = WorkFlowUtil.getTablenameByDefkey(defKey);
		WorkFlowService.me.updateIfCompleteAndIfAgree(tableName, pass, id);//更新是否同意，是否完成
		
	}
}
