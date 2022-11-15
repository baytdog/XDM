package com.pointlion.mvc.common.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseOaApplyBuy<M extends BaseOaApplyBuy<M>> extends Model<M> implements IBean {

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

	public void setProjectName(java.lang.String projectName) {
		set("project_name", projectName);
	}
	
	public java.lang.String getProjectName() {
		return getStr("project_name");
	}

	public void setProjectArea(java.lang.String projectArea) {
		set("project_area", projectArea);
	}
	
	public java.lang.String getProjectArea() {
		return getStr("project_area");
	}

	public void setPrice(java.lang.String price) {
		set("price", price);
	}
	
	public java.lang.String getPrice() {
		return getStr("price");
	}

	public void setBankNum(java.lang.String bankNum) {
		set("bank_num", bankNum);
	}
	
	public java.lang.String getBankNum() {
		return getStr("bank_num");
	}

	public void setMobile(java.lang.String mobile) {
		set("mobile", mobile);
	}
	
	public java.lang.String getMobile() {
		return getStr("mobile");
	}

	public void setApplyKeaderSign(java.lang.String applyKeaderSign) {
		set("apply_keader_sign", applyKeaderSign);
	}
	
	public java.lang.String getApplyKeaderSign() {
		return getStr("apply_keader_sign");
	}

	public void setLeader1Sign(java.lang.String leader1Sign) {
		set("leader1_sign", leader1Sign);
	}
	
	public java.lang.String getLeader1Sign() {
		return getStr("leader1_sign");
	}

	public void setLeader2Sign(java.lang.String leader2Sign) {
		set("leader2_sign", leader2Sign);
	}
	
	public java.lang.String getLeader2Sign() {
		return getStr("leader2_sign");
	}

	public void setMainLeaderSign(java.lang.String mainLeaderSign) {
		set("main_leader_sign", mainLeaderSign);
	}
	
	public java.lang.String getMainLeaderSign() {
		return getStr("main_leader_sign");
	}

	public void setBossSign(java.lang.String bossSign) {
		set("boss_sign", bossSign);
	}
	
	public java.lang.String getBossSign() {
		return getStr("boss_sign");
	}

	public void setFinanceLeaderSign(java.lang.String financeLeaderSign) {
		set("finance_leader_sign", financeLeaderSign);
	}
	
	public java.lang.String getFinanceLeaderSign() {
		return getStr("finance_leader_sign");
	}

	public void setBuyLeaderSign(java.lang.String buy_leader_sign) {
		set("buy_leader_sign", buy_leader_sign);
	}
	
	public java.lang.String getBuyLeaderSign() {
		return getStr("buy_leader_sign");
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
