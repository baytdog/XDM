package com.pointlion.mvc.common.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseOaBumphRz<M extends BaseOaBumphRz<M>> extends Model<M> implements IBean {

	public void setId(java.lang.String id) {
		set("id", id);
	}
	
	public java.lang.String getId() {
		return getStr("id");
	}

	public void setTitle(java.lang.String title) {
		set("title", title);
	}
	
	public java.lang.String getTitle() {
		return getStr("title");
	}

	public void setSenderId(java.lang.String senderId) {
		set("sender_id", senderId);
	}
	
	public java.lang.String getSenderId() {
		return getStr("sender_id");
	}

	public void setSenderName(java.lang.String senderName) {
		set("sender_name", senderName);
	}
	
	public java.lang.String getSenderName() {
		return getStr("sender_name");
	}

	public void setSenderOrgid(java.lang.String senderOrgid) {
		set("sender_orgid", senderOrgid);
	}
	
	public java.lang.String getSenderOrgid() {
		return getStr("sender_orgid");
	}

	public void setSenderOrgname(java.lang.String senderOrgname) {
		set("sender_orgname", senderOrgname);
	}
	
	public java.lang.String getSenderOrgname() {
		return getStr("sender_orgname");
	}

	public void setDocNum(java.lang.String docNum) {
		set("doc_num", docNum);
	}
	
	public java.lang.String getDocNum() {
		return getStr("doc_num");
	}

	public void setDocNumSource(java.lang.String docNumSource) {
		set("doc_num_source", docNumSource);
	}
	
	public java.lang.String getDocNumSource() {
		return getStr("doc_num_source");
	}

	public void setDocNumYear(java.lang.String docNumYear) {
		set("doc_num_year", docNumYear);
	}
	
	public java.lang.String getDocNumYear() {
		return getStr("doc_num_year");
	}

	public void setDocNumN(java.lang.Integer docNumN) {
		set("doc_num_n", docNumN);
	}
	
	public java.lang.Integer getDocNumN() {
		return getInt("doc_num_n");
	}

	public void setContent(java.lang.String content) {
		set("content", content);
	}
	
	public java.lang.String getContent() {
		return getStr("content");
	}

	public void setIfSubmit(java.lang.String ifSubmit) {
		set("if_submit", ifSubmit);
	}
	
	public java.lang.String getIfSubmit() {
		return getStr("if_submit");
	}

	public void setIfSend(java.lang.String ifSend) {
		set("if_send", ifSend);
	}
	
	public java.lang.String getIfSend() {
		return getStr("if_send");
	}

	public void setIfAgree(java.lang.String ifAgree) {
		set("if_agree", ifAgree);
	}
	
	public java.lang.String getIfAgree() {
		return getStr("if_agree");
	}

	public void setIfComplete(java.lang.String ifComplete) {
		set("if_complete", ifComplete);
	}
	
	public java.lang.String getIfComplete() {
		return getStr("if_complete");
	}

	public void setProcInsId(java.lang.String procInsId) {
		set("proc_ins_id", procInsId);
	}
	
	public java.lang.String getProcInsId() {
		return getStr("proc_ins_id");
	}

	public void setFirstLeaderAudit(java.lang.String firstLeaderAudit) {
		set("first_leader_audit", firstLeaderAudit);
	}
	
	public java.lang.String getFirstLeaderAudit() {
		return getStr("first_leader_audit");
	}

	public void setSecondLeaderAudit(java.lang.String secondLeaderAudit) {
		set("second_leader_audit", secondLeaderAudit);
	}
	
	public java.lang.String getSecondLeaderAudit() {
		return getStr("second_leader_audit");
	}

	public void setSendTime(java.lang.String sendTime) {
		set("send_time", sendTime);
	}
	
	public java.lang.String getSendTime() {
		return getStr("send_time");
	}

	public void setDocType(java.lang.String docType) {
		set("doc_type", docType);
	}
	
	public java.lang.String getDocType() {
		return getStr("doc_type");
	}

	public void setType(java.lang.String type) {
		set("type", type);
	}
	
	public java.lang.String getType() {
		return getStr("type");
	}

	public void setCreateTime(java.lang.String createTime) {
		set("create_time", createTime);
	}
	
	public java.lang.String getCreateTime() {
		return getStr("create_time");
	}

	public void setCreateUserId(java.lang.String createUserId) {
		set("create_user_id", createUserId);
	}
	
	public java.lang.String getCreateUserId() {
		return getStr("create_user_id");
	}

	public void setCreateUserName(java.lang.String createUserName) {
		set("create_user_name", createUserName);
	}
	
	public java.lang.String getCreateUserName() {
		return getStr("create_user_name");
	}

	public void setCreateOrgId(java.lang.String createOrgId) {
		set("create_org_id", createOrgId);
	}
	
	public java.lang.String getCreateOrgId() {
		return getStr("create_org_id");
	}

	public void setCreateOrgName(java.lang.String createOrgName) {
		set("create_org_name", createOrgName);
	}
	
	public java.lang.String getCreateOrgName() {
		return getStr("create_org_name");
	}

	public void setReceiveTime(java.lang.String receiveTime) {
		set("receive_time", receiveTime);
	}
	
	public java.lang.String getReceiveTime() {
		return getStr("receive_time");
	}

	public void setZc(java.lang.String zc) {
		set("zc", zc);
	}
	
	public java.lang.String getZc() {
		return getStr("zc");
	}

	public void setDocFrom(java.lang.String docFrom) {
		set("doc_from", docFrom);
	}
	
	public java.lang.String getDocFrom() {
		return getStr("doc_from");
	}

	public void setRegeitTime(java.lang.String regeitTime) {
		set("regeit_time", regeitTime);
	}
	
	public java.lang.String getRegeitTime() {
		return getStr("regeit_time");
	}

	public void setFileOrnot(java.lang.String fileOrnot) {
		set("file_ornot", fileOrnot);
	}
	
	public java.lang.String getFileOrnot() {
		return getStr("file_ornot");
	}

	public void setFileName(java.lang.String fileName) {
		set("file_name", fileName);
	}
	
	public java.lang.String getFileName() {
		return getStr("file_name");
	}

	public void setFileNo(java.lang.String fileNo) {
		set("file_no", fileNo);
	}
	
	public java.lang.String getFileNo() {
		return getStr("file_no");
	}

	public void setNbrids(java.lang.String nbrids) {
		set("nbrids", nbrids);
	}
	
	public java.lang.String getNbrids() {
		return getStr("nbrids");
	}

	public void setNbrnames(java.lang.String nbrnames) {
		set("nbrnames", nbrnames);
	}
	
	public java.lang.String getNbrnames() {
		return getStr("nbrnames");
	}

	public void setNbyj(java.lang.String nbyj) {
		set("nbyj", nbyj);
	}
	
	public java.lang.String getNbyj() {
		return getStr("nbyj");
	}

	public void setClqk(java.lang.String clqk) {
		set("clqk", clqk);
	}
	
	public java.lang.String getClqk() {
		return getStr("clqk");
	}

	public void setLeadersid(java.lang.String leadersid) {
		set("leadersid", leadersid);
	}
	
	public java.lang.String getLeadersid() {
		return getStr("leadersid");
	}

	public void setLeadersname(java.lang.String leadersname) {
		set("leadersname", leadersname);
	}
	
	public java.lang.String getLeadersname() {
		return getStr("leadersname");
	}

	public void setHostsid(java.lang.String hostsid) {
		set("hostsid", hostsid);
	}
	
	public java.lang.String getHostsid() {
		return getStr("hostsid");
	}

	public void setHostsname(java.lang.String hostsname) {
		set("hostsname", hostsname);
	}
	
	public java.lang.String getHostsname() {
		return getStr("hostsname");
	}

	public void setCustomersid(java.lang.String customersid) {
		set("customersid", customersid);
	}
	
	public java.lang.String getCustomersid() {
		return getStr("customersid");
	}

	public void setCustomersname(java.lang.String customersname) {
		set("customersname", customersname);
	}
	
	public java.lang.String getCustomersname() {
		return getStr("customersname");
	}

	public void setStatus(java.lang.String status) {
		set("status", status);
	}
	
	public java.lang.String getStatus() {
		return getStr("status");
	}

	public void setBackup1(java.lang.String backup1) {
		set("backup1", backup1);
	}
	
	public java.lang.String getBackup1() {
		return getStr("backup1");
	}

	public void setBackup2(java.lang.String backup2) {
		set("backup2", backup2);
	}
	
	public java.lang.String getBackup2() {
		return getStr("backup2");
	}

	public void setBackup3(java.lang.String backup3) {
		set("backup3", backup3);
	}
	
	public java.lang.String getBackup3() {
		return getStr("backup3");
	}

}
