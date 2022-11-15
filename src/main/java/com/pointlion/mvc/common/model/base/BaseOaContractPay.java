package com.pointlion.mvc.common.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseOaContractPay<M extends BaseOaContractPay<M>> extends Model<M> implements IBean {

	public void setId(java.lang.String id) {
		set("id", id);
	}
	
	public java.lang.String getId() {
		return getStr("id");
	}

	public void setUserid(java.lang.String userid) {
		set("userid", userid);
	}
	
	public java.lang.String getUserid() {
		return getStr("userid");
	}

	public void setApplyerName(java.lang.String applyerName) {
		set("applyer_name", applyerName);
	}
	
	public java.lang.String getApplyerName() {
		return getStr("applyer_name");
	}

	public void setOrgId(java.lang.String orgId) {
		set("org_id", orgId);
	}
	
	public java.lang.String getOrgId() {
		return getStr("org_id");
	}

	public void setOrgName(java.lang.String orgName) {
		set("org_name", orgName);
	}
	
	public java.lang.String getOrgName() {
		return getStr("org_name");
	}

	public void setLeaderMessage(java.lang.String leaderMessage) {
		set("leader_message", leaderMessage);
	}
	
	public java.lang.String getLeaderMessage() {
		return getStr("leader_message");
	}

	public void setLeader2Message(java.lang.String leader2Message) {
		set("leader2_message", leader2Message);
	}
	
	public java.lang.String getLeader2Message() {
		return getStr("leader2_message");
	}

	public void setIfSubmit(java.lang.String ifSubmit) {
		set("if_submit", ifSubmit);
	}
	
	public java.lang.String getIfSubmit() {
		return getStr("if_submit");
	}

	public void setIfComplete(java.lang.String ifComplete) {
		set("if_complete", ifComplete);
	}
	
	public java.lang.String getIfComplete() {
		return getStr("if_complete");
	}

	public void setIfAgree(java.lang.String ifAgree) {
		set("if_agree", ifAgree);
	}
	
	public java.lang.String getIfAgree() {
		return getStr("if_agree");
	}

	public void setProcInsId(java.lang.String procInsId) {
		set("proc_ins_id", procInsId);
	}
	
	public java.lang.String getProcInsId() {
		return getStr("proc_ins_id");
	}

	public void setCreateTime(java.lang.String createTime) {
		set("create_time", createTime);
	}
	
	public java.lang.String getCreateTime() {
		return getStr("create_time");
	}

	public void setDes(java.lang.String des) {
		set("des", des);
	}
	
	public java.lang.String getDes() {
		return getStr("des");
	}

	public void setContractId(java.lang.String contractId) {
		set("contract_id", contractId);
	}
	
	public java.lang.String getContractId() {
		return getStr("contract_id");
	}

	public void setBankNum(java.lang.String bankNum) {
		set("bank_num", bankNum);
	}
	
	public java.lang.String getBankNum() {
		return getStr("bank_num");
	}

	public void setBankAddress(java.lang.String bankAddress) {
		set("bank_address", bankAddress);
	}
	
	public java.lang.String getBankAddress() {
		return getStr("bank_address");
	}

	public void setBankcardPerson(java.lang.String bankcardPerson) {
		set("bankcard_person", bankcardPerson);
	}
	
	public java.lang.String getBankcardPerson() {
		return getStr("bankcard_person");
	}

	public void setAmountOfMoney(java.lang.String amountOfMoney) {
		set("amount_of_money", amountOfMoney);
	}
	
	public java.lang.String getAmountOfMoney() {
		return getStr("amount_of_money");
	}

	public void setReason(java.lang.String reason) {
		set("reason", reason);
	}
	
	public java.lang.String getReason() {
		return getStr("reason");
	}

	public void setTitle(java.lang.String title) {
		set("title", title);
	}
	
	public java.lang.String getTitle() {
		return getStr("title");
	}

	public void setCreateUserId(String create_user_id) {
		set("create_user_id", create_user_id);
	}

	public String getCreateUserId() {
		return get("create_user_id");
	}

	public void setCreateUserName(String create_user_name) {
		set("create_user_name", create_user_name);
	}

	public String getCreateUserName() {
		return get("create_user_name");
	}

	public void setCreateOrgId(String create_org_id) {
		set("create_org_id", create_org_id);
	}

	public String getCreateOrgId() {
		return get("create_org_id");
	}

	public void setCreateOrgName(String create_org_name) {
		set("create_org_name", create_org_name);
	}

	public String getCreateOrgName() {
		return get("create_org_name");
	}
}
