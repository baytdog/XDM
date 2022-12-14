package com.pointlion.mvc.common.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseOaProject<M extends BaseOaProject<M>> extends Model<M> implements IBean {

	public void setId(java.lang.String id) {
		set("id", id);
	}
	
	public java.lang.String getId() {
		return getStr("id");
	}

	public void setLeader(java.lang.String leader) {
		set("leader", leader);
	}
	
	public java.lang.String getLeader() {
		return getStr("leader");
	}

	public void setMember(java.lang.String member) {
		set("member", member);
	}
	
	public java.lang.String getMember() {
		return getStr("member");
	}

	public void setProjectName(java.lang.String projectName) {
		set("project_name", projectName);
	}
	
	public java.lang.String getProjectName() {
		return getStr("project_name");
	}

	public void setProjectCode(java.lang.String projectCode) {
		set("project_code", projectCode);
	}

	public java.lang.String getProjectCode() {
		return getStr("project_code");
	}

	public void setCustomerName(java.lang.String customerName) {
		set("customer_name", customerName);
	}
	
	public java.lang.String getCustomerName() {
		return getStr("customer_name");
	}

	public void setProjectStartTime(java.lang.String projectStartTime) {
		set("project_start_time", projectStartTime);
	}
	
	public java.lang.String getProjectStartTime() {
		return getStr("project_start_time");
	}

	public void setProjectEndTime(java.lang.String project_end_time) {
		set("project_start_time", project_end_time);
	}

	public java.lang.String getProjectEndTime() {
		return getStr("project_end_time");
	}

	public void setProjectMoney(java.lang.String projectMoney) {
		set("project_money", projectMoney);
	}
	
	public java.lang.String getProjectMoney() {
		return getStr("project_money");
	}

	public void setCustomerContactName(java.lang.String customerContactName) {
		set("customer_contact_name", customerContactName);
	}
	
	public java.lang.String getCustomerContactName() {
		return getStr("customer_contact_name");
	}

	public void setCustomerContactMobile(java.lang.String customerContactMobile) {
		set("customer_contact_mobile", customerContactMobile);
	}
	
	public java.lang.String getCustomerContactMobile() {
		return getStr("customer_contact_mobile");
	}

	public void setActSuggest(java.lang.String actSuggest) {
		set("act_suggest", actSuggest);
	}
	
	public java.lang.String getActSuggest() {
		return getStr("act_suggest");
	}

	public void setCustomHope(java.lang.String customHope) {
		set("custom_hope", customHope);
	}
	
	public java.lang.String getCustomHope() {
		return getStr("custom_hope");
	}

	public void setRiskAndMeasure(java.lang.String riskAndMeasure) {
		set("risk_and_measure", riskAndMeasure);
	}
	
	public java.lang.String getRiskAndMeasure() {
		return getStr("risk_and_measure");
	}

	public void setActAllStrategySuggest(java.lang.String actAllStrategySuggest) {
		set("act_all_strategy_suggest", actAllStrategySuggest);
	}
	
	public java.lang.String getActAllStrategySuggest() {
		return getStr("act_all_strategy_suggest");
	}

	public void setContactName(java.lang.String contactName) {
		set("contact_name", contactName);
	}
	
	public java.lang.String getContactName() {
		return getStr("contact_name");
	}

	public void setContactMobile(java.lang.String contactMobile) {
		set("contact_mobile", contactMobile);
	}
	
	public java.lang.String getContactMobile() {
		return getStr("contact_mobile");
	}

	public void setContactMail(java.lang.String contactMail) {
		set("contact_mail", contactMail);
	}
	
	public java.lang.String getContactMail() {
		return getStr("contact_mail");
	}

	public void setBuildId(java.lang.String buildId) {
		set("build_id", buildId);
	}
	
	public java.lang.String getBuildId() {
		return getStr("build_id");
	}

	public void setCreateUserId(java.lang.String createUserId) {
		set("create_user_id", createUserId);
	}
	
	public java.lang.String getCreateUserId() {
		return getStr("create_user_id");
	}

	public void setCreateTime(java.lang.String createTime) {
		set("create_time", createTime);
	}
	
	public java.lang.String getCreateTime() {
		return getStr("create_time");
	}

	public void setProjectType(java.lang.String project_type) {
		set("project_type", project_type);
	}

	public java.lang.String getProjectType() {
		return getStr("project_type");
	}


	public void setProjectTypeName(java.lang.String project_type_name) {
		set("project_type_name", project_type_name);
	}

	public java.lang.String getProjectTypeName() {
		return getStr("project_type_name");
	}
}
