package com.pointlion.mvc.common.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseOaLeave<M extends BaseOaLeave<M>> extends Model<M> implements IBean {

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

	public void setApplyerPosition(java.lang.String position) {
		set("applyer_position", position);
	}

	public java.lang.String getApplyerPosition() {
		return get("applyer_position");
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

	public void setNote(java.lang.String note) {
		set("note", note);
	}

	public java.lang.String getNote() {
		return getStr("note");
	}

	public void setTitle(java.lang.String title) {
		set("title", title);
	}

	public java.lang.String getTitle() {
		return get("title");
	}

	public void setStartTime(java.lang.String startTime) {
		set("start_time", startTime);
	}

	public java.lang.String getStartTime() {
		return get("start_time");
	}

	public void setEndTime(java.lang.String endTime) {
		set("end_time", endTime);
	}

	public java.lang.String getEndTime() {
		return get("end_time");
	}

	public void setDays(java.lang.String days) {
		set("days", days);
	}

	public java.lang.String getDays() {
		return get("days");
	}

	public void setMessage(java.lang.String message) {
		set("message", message);
	}

	public java.lang.String getMessage() {
		return get("message");
	}

	
	public void setType(java.lang.String type) {
		set("type", type);
	}

	public java.lang.String getType() {
		return get("type");
	}

	public void setProjectId(java.lang.String projectId) {
		set("project_id", projectId);
	}

	public java.lang.String getProjectId() {
		return getStr("project_id");
	}

	public void setProjectName(java.lang.String projectName) {
		set("project_name", projectName);
	}

	public java.lang.String getProjectName() {
		return getStr("project_name");
	}

	public void setLeaveType(java.lang.String leave_type) {
		set("leave_type", leave_type);
	}

	public java.lang.String getLeaveType() {
		return getStr("leave_type");
	}

	public void setOtherUserId(java.lang.String other_user_id) {
		set("other_user_id", other_user_id);
	}

	public java.lang.String getOtherUserId() {
		return getStr("other_user_id");
	}

	public void setOtherUserName(java.lang.String other_user_name) {
		set("other_user_name", other_user_name);
	}

	public java.lang.String getOtherUserName() {
		return getStr("other_user_name");
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
